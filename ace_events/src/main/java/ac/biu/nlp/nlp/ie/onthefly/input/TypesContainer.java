package ac.biu.nlp.nlp.ie.onthefly.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.util.JCasUtil;

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
	public List<String> possibleTriggerLabels;
	
	// map event subtype --> entity types
	public Map<String, Set<String>> eventEntityTypes = new HashMap<String, Set<String>>();
	// map event subtype --> argument role
	public Map<String, Set<String>> argumentRoles = new HashMap<String, Set<String>>();
	// map argument_role --> entity types 
	public Map<String, Set<String>> roleEntityTypes = new HashMap<String, Set<String>>();


	public TypesContainer(List<String> specXmlPaths) throws CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, UimaUtilsException, IOException, AeException, CASException {
		specs = SpecHandler.getSpecs(specXmlPaths);
		possibleTriggerLabels = new ArrayList<String>();

		nodeTargetAlphabet = new Alphabet();
		edgeTargetAlphabet = new Alphabet();
		
		nodeTargetAlphabet.lookupIndex(SentenceAssignment.Default_Trigger_Label);
		edgeTargetAlphabet.lookupIndex(SentenceAssignment.Default_Argument_Label);
		possibleTriggerLabels.add(SentenceAssignment.Default_Trigger_Label);
		
		for (JCas spec : specs) {

			String triggerName = SpecAnnotator.getSpecLabel(spec);
			possibleTriggerLabels.add(triggerName);
			nodeTargetAlphabet.lookupIndex(triggerName);
			
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
	}
	
	public List<String> getPossibleTriggerLabels() {
		return possibleTriggerLabels;
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

	public String getCanonicalRoleName(String role)
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

	public boolean isRoleCompatible(String subtype, String role)
	{
		role = getCanonicalRoleName(role);
		Set<String> roles = argumentRoles.get(subtype);
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

	
	private void updateMaps(String triggerType, String role, List<String> types) {
		Set<String> set = argumentRoles.get(triggerType);
		if(set == null)
		{
			set = new HashSet<String>();
		}
		set.add(role);
		argumentRoles.put(triggerType, set);
		
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
			Set<String> roles = argumentRoles.get(eventType);
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
