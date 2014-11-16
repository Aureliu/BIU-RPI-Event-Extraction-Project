package ac.biu.nlp.nlp.ie.onthefly.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentType;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;

public class TypesContainer {

	public List<JCas> specs;
	public Alphabet nodeTargetAlphabet;
	public Alphabet edgeTargetAlphabet;
	//public Set<String> triggerTypes;
	public LinkedHashMap<String, Integer> triggerTypes; //The type here is a horrible, horrible hack
	public List<String> possibleTriggerLabels;
	
	public LinkedHashMap<String, JCas> namedSpecs; // This is because I don't have the energy to change all references to this.specs, so it's basically duplicated...
	
	// map event subtype --> entity types
	public Map<String, Set<String>> eventEntityTypes = new HashMap<String, Set<String>>();
	// map event subtype --> argument role
	public Map<String, LinkedHashMap<String, Integer>> argumentRoles = new HashMap<String, LinkedHashMap<String, Integer>>();
	// map argument_role --> entity types 
	public Map<String, Set<String>> roleEntityTypes = new HashMap<String, Set<String>>();


	// second param - hack
	public TypesContainer(List<String> specXmlPaths, boolean ignoreThis) throws CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, UimaUtilsException, IOException, AeException, CASException, SpecException { 
		this(SpecHandler.getSpecs(specXmlPaths));
	}
	public TypesContainer(List<JCas> specs) throws CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, UimaUtilsException, IOException, AeException, CASException {
		this.specs = specs;
		triggerTypes = Maps.newLinkedHashMap();
		namedSpecs = Maps.newLinkedHashMap();

		nodeTargetAlphabet = new Alphabet();
		edgeTargetAlphabet = new Alphabet();
		
		nodeTargetAlphabet.lookupIndex(SentenceAssignment.Default_Trigger_Label);
		edgeTargetAlphabet.lookupIndex(SentenceAssignment.Default_Argument_Label);
		
		int specNum = 0;
		for (JCas spec : specs) {

			String triggerName = SpecAnnotator.getSpecLabel(spec);
			triggerTypes.put(triggerName, specNum);
			nodeTargetAlphabet.lookupIndex(triggerName);
			specNum++;
			namedSpecs.put(triggerName, spec);
			
			JCas tokenView = spec.getView(SpecAnnotator.TOKEN_VIEW);
			for (Argument arg : JCasUtil.select(tokenView, Argument.class)) {
				String role = arg.getRole().getCoveredText();
				List<ArgumentType> types = JCasUtil.selectCovered(tokenView, ArgumentType.class, arg);
				List<String> typeStrs = JCasUtil.toText(types);
				edgeTargetAlphabet.lookupIndex(role);
				updateMaps(triggerName, role, typeStrs);
			}
		}
		
		finalizeMaps();
		possibleTriggerLabels = new ArrayList<String>(triggerTypes.size() + 1);
		possibleTriggerLabels.addAll(triggerTypes.keySet());
		Collections.sort(possibleTriggerLabels);
		possibleTriggerLabels.add(0, SentenceAssignment.Default_Trigger_Label); // First element!
	}
	
	public List<JCas> getPartialSpecList(List<String> names) {
		List<JCas> result = Lists.newArrayListWithCapacity(names.size());
		for (String name : names) {
			JCas spec = namedSpecs.get(name);
			if (spec == null) {
				throw new IllegalArgumentException("Spec for name '" + name + "' doesn't exist");
			}
			result.add(spec);
		}
		return result;
	}
		
	public boolean isEntityTypeCompatible(String role, String type)
	{
		role = getCanonicalRoleName(role);
		Set<String> entityTypes = roleEntityTypes.get(role);
		if(entityTypes != null && entityTypes.contains(type))
		{
			return true;
		}
		return false;
	}

	public static String getCanonicalRoleName(String role)
	{
		if(role.startsWith("Time"))
		{
			return "Time";
		}
		else
		{
			return role;
		}
	}

	/**
	 * check if ace mention is compatible with the event type and argument role in the
	 * current hypothesis
	 * @param edgeLabel 
	 * @param type
	 * @param currentNodeLabel
	 * @return
	 */
	public boolean isRoleCompatible(String mention_type, String triggerType, String edgeLabel)
	{
		if(edgeLabel.equals(SentenceAssignment.Default_Argument_Label) || 
				(isRoleCompatible(triggerType, edgeLabel) && isEntityTypeCompatible(edgeLabel, mention_type)))
		{
			return true;
		}
		return false;
	}

	public boolean isRoleCompatible(String subtype, String role)
	{
		role = getCanonicalRoleName(role);
		Set<String> roles = argumentRoles.get(subtype).keySet();
		if(roles != null && roles.contains(role))
		{
			return true;
		}
		return false;
	}

	public boolean isEntityTypeEventCompatible(String eventType, String entityType)
	{
		Set<String> types = eventEntityTypes.get(eventType);
		return types.contains(entityType);
	}

	public boolean isPossibleTriggerByPOS(SentenceInstance problem, int i)
	{
		final String allowedPOS = "IN|JJ|RB|DT|VBG|VBD|NN|NNPS|VB|VBN|NNS|VBP|NNP|PRP|VBZ";
		String[] posTags = problem.getPosTags();
		if(posTags[i].matches(allowedPOS))
		{
			return true;
		}
		return false;
	}

	public boolean isPossibleTriggerByEntityType(SentenceInstance problem, int i)
	{
		for(AceMention mention : problem.eventArgCandidates)
		{
			if(mention instanceof AceEntityMention && mention.getHeadIndices().contains(i))
			{
				return false;
			}
		}
		return true;
	}

	public Set<String> getCompatibleEntityTypes(String role)
	{
		return roleEntityTypes.get(role);
	}
	
	public String toString() {
		//return argumentRoles.toString();
		return String.format("%s(%s)", getClass().getSimpleName(), namedSpecs.keySet());
	}

	
	private void updateMaps(String triggerType, String role, List<String> types) {
		LinkedHashMap<String, Integer> map = argumentRoles.get(triggerType);
		if(map == null)
		{
			map = Maps.newLinkedHashMap();
		}
		map.put(role, map.size());
		argumentRoles.put(triggerType, map);
		
		for(String type : types)
		{
			Set<String> entityTypes = roleEntityTypes.get(role);
			if(entityTypes == null)
			{
				entityTypes = new HashSet<String>();
			}
			entityTypes.add(type);
			roleEntityTypes.put(role, entityTypes);
		}
	}
	
	private void finalizeMaps()
	{
		for(String eventType : argumentRoles.keySet())
		{
			Set<String> roles = argumentRoles.get(eventType).keySet();
			for(String role : roles)
			{
				Set<String> entityTypes = roleEntityTypes.get(role);
				if(entityTypes != null)
				{
					Set<String> possibleEntityTypes = eventEntityTypes.get(eventType);
					if(possibleEntityTypes == null)
					{
						possibleEntityTypes = new HashSet<String>();
						eventEntityTypes.put(eventType, possibleEntityTypes);
					}
					possibleEntityTypes.addAll(entityTypes);
				}
			}
		}
	}
}
