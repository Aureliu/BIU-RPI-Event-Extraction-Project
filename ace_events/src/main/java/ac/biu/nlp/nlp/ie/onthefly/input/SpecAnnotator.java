package ac.biu.nlp.nlp.ie.onthefly.input;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCopier;
import org.hibernate.property.Getter;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.FSCollectionFactory;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.ace_uima.AceAbnormalMessage;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample_Type;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentType;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.LemmaByPos;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.NounLemma;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateInUsageSample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateName;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Treeout;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepGenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepGenPosWithContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepGenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepGenPosWithContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepSpecPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepSpecPosWithContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepWithContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepSpecPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepSpecPosWithContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepWithContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.VerbLemma;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceArgumentType;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.util.fragment.FragmentAndReference;
import edu.cuny.qc.util.fragment.FragmentLayer;
import edu.ucla.sspace.util.HashMultiMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeToLineString;
import eu.excitementproject.eop.common.utilities.DockedToken;
import eu.excitementproject.eop.common.utilities.DockedTokenFinder;
import eu.excitementproject.eop.common.utilities.DockedTokenFinderException;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;

public class SpecAnnotator extends JCasAnnotator_ImplBase {
	//private Perceptron perceptron = null;
	private AnalysisEngine tokenAE;
	private AnalysisEngine sentenceAE;
		
//	public static final String PARAM_PERCEPTRON = "perceptron_object";
//	@ConfigurationParameter(name = PARAM_PERCEPTRON, mandatory = true)
//	private Perceptron perceptron;

//	public void init(Perceptron perceptron) throws AeException {
//		this.perceptron = perceptron;
//		tokenAE = AnalysisEngines.forSpecTokenView();
//		sentenceAE = AnalysisEngines.forSpecSentenceView();
//	}
	
	@Override
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		super.initialize(aContext);

		try {
			tokenAE = AnalysisEngines.forSpecTokenView(TOKEN_VIEW);
			sentenceAE = AnalysisEngines.forSpecSentenceView(SENTENCE_VIEW);
		} catch (AeException e) {
			throw new ResourceInitializationException(e);
		}
	}

	private static <T extends Annotation> String getValue(JCas spec, String viewName, Class<T> type) throws CASException {
		JCas view = spec.getView(viewName);
		T anno = JCasUtil.selectSingle(view, type);
		return anno.getCoveredText();
	}
	
//	private static <T extends Annotation> List<String> getStringList(JCas spec, String viewName, Class<T> type) throws CASException {
//		JCas view = spec.getView(viewName);
//		Collection<T> annotations = JCasUtil.select(view, type);
//		return JCasUtil.toText(annotations);
//	}
//	
	private static <T extends Annotation> Collection<T> getAnnotationCollection(JCas spec, String viewName, Class<T> type) throws CASException {
		JCas view = spec.getView(viewName);
		return JCasUtil.select(view, type);
	}
	
	public static String getSpecLabel(JCas spec) throws CASException {
		return getValue(spec, TOKEN_VIEW, PredicateName.class);
	}

	/**
	 * It's enough to just remove Argument annotations (no need to remove all other covered annotations like ArgRole etc),
	 * since for iterating a spec's argument list, always the list of Argument annotations is iterated.
	 * This happens in TypesContainer() and in SentenceInstance.getPersistentSignals().
	 */
	public static Map<String, JCas> getSingleRoleSpecs(JCas spec) throws CASException, UimaUtilsException, ResourceInitializationException {
		Map<String, JCas> result = Maps.newLinkedHashMap();
		int numArgs = getSpecArguments(spec).size();
		for (int num=0; num<numArgs; num++) {
			JCas singleRoleSpec = SpecHandler.ae.newJCas();
			CasCopier.copyCas(spec.getCas(), singleRoleSpec.getCas(), true);
			List<Argument> copyOfArguments = Lists.newArrayList(getSpecArguments(singleRoleSpec));
			Argument remainingArg = copyOfArguments.remove(num); //in this iteration, only the num'th argument will remain
			String role = remainingArg.getRole().getCoveredText();
			for (Argument argToRemove : copyOfArguments) {
				argToRemove.removeFromIndexes();
			}
			result.put(role, singleRoleSpec);
		}
		return result;
	}

//	public static List<String> getSpecRoles(JCas spec) throws CASException {
//		return getStringList(spec, TOKEN_VIEW, ArgumentRole.class);
//	}
	
	public static Collection<Argument> getSpecArguments(JCas spec) throws CASException {
		return getAnnotationCollection(spec, TOKEN_VIEW, Argument.class);
	}
	

	public Multimap<String, Annotation> getLemmaToAnnotation(JCas view, Annotation covering, Class<? extends Annotation> elementType, String title) throws SpecXmlException {
		Multimap<String, Annotation> result = HashMultimap.create();
		for (Annotation element : JCasUtil.selectCovered(elementType, covering)) {
			NounLemma nounLemma = UimaUtils.selectCoveredSingle(view, NounLemma.class, element);
			VerbLemma verbLemma = UimaUtils.selectCoveredSingle(view, VerbLemma.class, element);
			ImmutableSet<String> lemmas = ImmutableSet.of(nounLemma.getValue(), verbLemma.getValue()); //remove duplicates between two lemmas
			for (String lemmaStr : lemmas) {
				
				// There is quite a complicated treatment here in case two elements has some joint lemmas by some POSes
				// E.g.: 'meet'-->meet/N,meet/V. 'meeting'-->meeting/N,meet/V.
				// So in case the current element, or some other element that has the same lemmas, also have other lemmas -
				// then leave it only with the other lemmas and remove the current one.
				// However, if both elements have exactly one lemma - throw an exception.

				if (result.containsKey(lemmaStr)) {
					
					if (lemmas.size() > 1) {
						System.err.printf("SpecAnnotator: in %s, removing lemma '%s' from element '%s' since another element already has it, and the current element has other lemmas as well\n",
								title, lemmaStr, element.getCoveredText());
						continue;
					}
					else {
						Collection<Annotation> otherElementsWithSameLemma = result.get(lemmaStr);
						if (otherElementsWithSameLemma.size() > 1) {
							throw new SpecXmlException(String.format("Got %s elements with the lemma % in %s, should have up to 1", otherElementsWithSameLemma.size(), lemmaStr, title));
						}
						Annotation otherSingleElementWithSameLemma = otherElementsWithSameLemma.iterator().next();
						// wrap in a new list since the one returned by JCasUtil in unmodifiable,
						// and we may need to remove() from it later
						List<LemmaByPos> allLemmasOfEvilElement = Lists.newArrayList(JCasUtil.selectCovered(LemmaByPos.class, otherSingleElementWithSameLemma));
						if (allLemmasOfEvilElement.size() == 1) {
							
							// This is really the only bad situation - the only other element
							// with the current lemma, also has just a single lemma. this cannot be
							// resolved, so we throw an exception, and the spec must be manually fixed
							throw new SpecXmlException(String.format("In %s, element '%s' has only a single lemma '%s', " +
									"and another elemnt also has the same lemma as a *single* lemma. Please remove one of these elements " +
									"from the spec, or conjucate one of them to have other lemmas (with other parts-of-speech).",
									title, element.getCoveredText(), lemmaStr));

						}
						else {
							// other (evil) element has more lemmas, so we'll remove the current lemma from it.
							boolean removed = false;
							for (Iterator<LemmaByPos> iterator = allLemmasOfEvilElement.iterator(); iterator.hasNext();) {
								LemmaByPos currLemma = iterator.next();
								if (currLemma.getValue().equals(lemmaStr)) {
									iterator.remove();
									removed = true;
									System.err.printf("SpecAnnotator: in %s, removing lemma '%s' from element '%s', but leaving it in element '%s'\n",
											title, lemmaStr, otherSingleElementWithSameLemma.getCoveredText(), element.getCoveredText());
									break;
								}
							}
							if (!removed) {
								throw new SpecXmlException(String.format("Internal error - in %s, element '%s' " +
										"is supposed to contain lemma '%s', but it doesn't!", title,
										otherSingleElementWithSameLemma.getCoveredText(), lemmaStr));
							}
						}
					}
				}
				result.put(lemmaStr, element);
			}
		}
		return result;
	}

	@SuppressWarnings("unused")
	public void validateArgumentTypes(JCas view) throws SpecXmlException {
		String argTypeStr = null;
		AceArgumentType enumvalue;
		try {
			for (ArgumentType argType : JCasUtil.select(view, ArgumentType.class)) {
				argTypeStr = argType.getCoveredText();
				enumvalue = AceArgumentType.valueOf(argTypeStr);
			}
		} catch (IllegalArgumentException e) {
			throw new SpecXmlException("Bad value for argument type: " + argTypeStr, e);
		}
	}

	private Annotation getElement(Map<String, Map<String, Annotation>> markerMap, Multimap<String, String> absents, String marker, String elementText) throws SpecXmlException {
		try {
			///DEBUG
	//		if (elementText.contains("late")) {
	//			System.out.printf("\n\n\n\n\nlate\n\n\n\n\n");
	//		}
			////
			String lowercaseElementText = elementText.toLowerCase(); //We don't care about capitalization in spec element
			if (markerMap.get(marker).keySet().contains(lowercaseElementText)) {
				return markerMap.get(marker).get(lowercaseElementText);
			}
			else {
				if (Perceptron.controllerStatic.enhanceSpecs) {
					absents.put(marker, elementText);
					return null;
				}
				else {
					throw new SpecXmlException(String.format("Cannot find element '%s' for marker '%s'", elementText, marker));
				}
			}
		}
		catch (Exception e) {
			throw new SpecXmlException(String.format("Exception while getting element '%s' for marker '%s' (Maybe this spec doesn't support this marker?)", elementText, marker), e);
		}
	}

	public static Feature getAiusTreeoutFeature(Class<? extends Treeout> cls, ArgumentInUsageSample aius) throws CASException {
		String clsName = cls.getSimpleName();
		String featureName = clsName.substring(0, 1).toLowerCase() + clsName.substring(1, clsName.length());
		JCas jcas = aius.getCAS().getJCas();
		Type type = jcas.getTypeSystem().getType(aius.getClass().getName());
		Feature feature = type.getFeatureByBaseName(featureName);
		return feature;
	}
	
	public static Map<String, Integer> getFrequenciesFromVAll(VAll vAll) {
		Map<String, Integer> result = Maps.newHashMap();
		String[] perRole = vAll.getVal().split(" \\| ");
		for (String roleInfo : perRole) {
			String[] parts = roleInfo.split("\\*");
			if (parts.length != 2) {
				throw new IllegalArgumentException(String.format("Badly formatted vAll: %s", vAll.getVal()));
			}
			Integer count = Integer.parseInt(parts[0]);
			String roleName = parts[1];
			result.put(roleName, count);
		}
		return result;
	}
	
	public Multimap<Annotation, Annotation> organizeUsageSamples(JCas tokenView, JCas sentenceViewTemp, JCas sentenceView, Map<String, Map<String, Annotation>> markerMap) throws SpecXmlException {
		final Pattern USAGE_SAMPLE_ELEMENT = Pattern.compile("([^\\[]*)\\[(\\w{3}) ([^\\]]+)\\]([^\\[]*)");
		String oldDocText = sentenceViewTemp.getDocumentText(); 
		Multimap<Annotation, Annotation> toUsage = HashMultimap.create();
		Multimap<String, String> absents = HashMultimap.create(); //for enhanceSpecs
		//List<UsageSample> oldUsageSamples = Lists.newArrayList();
		
		// We want to have a list and not a direct iterator, so that we could remove and add samples to the CAS without a Concurrency Exception
		List<UsageSample> allUsageSamples = ImmutableList.copyOf(JCasUtil.select(sentenceViewTemp, UsageSample.class));
		Iterator<UsageSample> iterSamples = allUsageSamples.iterator();//JCasUtil.iterator(sentenceView, UsageSample.class);
		if (!iterSamples.hasNext()) {
			throw new SpecXmlException("Spec doesn't have any usage samples. It must have at least one.");
		}
		
		UsageSample nextSample = iterSamples.next();
		String nextSampleText = nextSample.getCoveredText();
		int nextOffsetInOldText = nextSample.getBegin();
		//int nextOffsetInNewText = nextOffsetInOldText;
		
		UsageSample sample;
		String sampleText;
		int offsetInOldText;
		
		int offsetInNewText = nextOffsetInOldText;		
		
		boolean hasMoreSamples = true;		
		StringBuilder newSectionBuilder = new StringBuilder(oldDocText.substring(0, nextOffsetInOldText));
		
		while (hasMoreSamples) {
			sample = nextSample;
			sampleText = nextSampleText;
			offsetInOldText = nextOffsetInOldText;
			//offsetInNewText = nextOffsetInNewText;
			sample.removeFromIndexes(); //No need for the old sample, we are creating a new one to replace it
			//oldUsageSamples.add(sample);
			
			Map<Integer, Annotation> elements = Maps.newLinkedHashMap();
			StringBuilder newSampleBuilder = new StringBuilder();
			List<String> tokens = Lists.newArrayList();
			Matcher matcher = USAGE_SAMPLE_ELEMENT.matcher(sampleText);
			
			for (int i=0; matcher.find(); i++) {
				String preElement = matcher.group(1);
				String marker = matcher.group(2);
				String elementText = matcher.group(3);
				String postElement = matcher.group(4);
				
				/// DEBUG
//				if (sampleText.contains("Reims")) {
//					System.out.printf("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n%s\n\n\n\n\n", sampleText);					
//				}
				////
				
				Annotation element = getElement(markerMap, absents, marker, elementText);
				if (element != null) {
					elements.put(i, element);
					
					tokens.add(preElement);		newSampleBuilder.append(preElement);
					tokens.add(elementText);	newSampleBuilder.append(elementText);
					tokens.add(postElement);	newSampleBuilder.append(postElement);
				}
			}


			String newSample = newSampleBuilder.toString();
					
			hasMoreSamples = iterSamples.hasNext();
			if (hasMoreSamples) {
				nextSample = iterSamples.next();
				nextSampleText = nextSample.getCoveredText();
				nextOffsetInOldText = nextSample.getBegin();
			}
			else {
				nextOffsetInOldText = oldDocText.length();
			}
				
			if (!Perceptron.controllerStatic.enhanceSpecs) {

				SortedMap<Integer, DockedToken> dockedTokens;
				try {
					dockedTokens = DockedTokenFinder.find(newSample, tokens);
				} catch (DockedTokenFinderException e) {
					throw new SpecXmlException(e);
				}
				
				for (Entry<Integer, Annotation> entry : elements.entrySet()) {
					Annotation element = entry.getValue();
					int numInDocked = 1 + 3*entry.getKey(); //This formula comes from the unique structure of the regex pattern - each match has 3 textual parts, the 2nd of which is our element
					DockedToken docked = dockedTokens.get(numInDocked);
					int begin = docked.getCharOffsetStart()+offsetInNewText;
					int end = docked.getCharOffsetEnd()+offsetInNewText;
					
					if (element instanceof PredicateSeed) {
						PredicateInUsageSample pius = new PredicateInUsageSample(sentenceView, begin, end);
						PredicateSeed seed = (PredicateSeed) element;
						toUsage.put(seed, pius);
						//seed.setPius(pius);
						pius.setPredicateSeed(seed);
						pius.addToIndexes();
					}
					else { //element instanceof ArgumentExample
						ArgumentInUsageSample aius = new ArgumentInUsageSample(sentenceView, begin, end);
						ArgumentExample example = (ArgumentExample) element;
						toUsage.put(example, aius);
						toUsage.put(example.getArgument(), aius); //a little abuse of the format - put aius also directly for the Argument (the one that has many examples)
						//example.setAius(aius);
						aius.setArgumentExample(example);
						aius.addToIndexes();
					}
				}
				
				int oldTextSampleEnd = offsetInOldText + sampleText.length();
				String betweenSamples = oldDocText.substring(oldTextSampleEnd, nextOffsetInOldText);
				newSectionBuilder.append(newSample);
				newSectionBuilder.append(betweenSamples);
				
				UsageSample newUsageSample = new UsageSample(sentenceView, offsetInNewText, offsetInNewText+newSample.length());
				newUsageSample.addToIndexes();
				
				offsetInNewText += newSample.length() + betweenSamples.length();
			}
		}
		
		String newSection = newSectionBuilder.toString();
		sentenceView.setDocumentText(newSection);
		
//		// Remove old usage examples, we created new ones to replace them
//		for (UsageSample oldSample : oldUsageSamples) {
//			oldSample.removeFromIndexes();
//		}
		
		if (Perceptron.controllerStatic.enhanceSpecs) {
			for (Entry<String, Collection<String>> absent : absents.asMap().entrySet()) {
				String marker = absent.getKey();

				String xmlElem = "example";
				String title = marker;
				String spaces = "        ";
				if (marker.equals(SpecXmlCasLoader.PREDICATE_MARKER)) {
					xmlElem = "seed";
					title = "***Predicate";
					spaces = "      ";
				}
				
				System.err.printf("\n    %s:\n", title);
				List<String> sortedElements = Lists.newArrayList(absent.getValue());
				Collections.sort(sortedElements, String.CASE_INSENSITIVE_ORDER);
				for (String elem : sortedElements) {
					System.err.printf("%s<%s>%s</%s>\n", spaces, xmlElem, elem, xmlElem);
				}
			}
		}

		return toUsage;
		
//		- Args_by_marker = {marker : Argument}
//		- Args_by_marke["PRD"] = Predicate
//		- PATT = "([^[]*)\[(\w\w\w) ([^]]+)\]([^[]*)"
//		- int offsetInOldText = samples[0].begin;
//		- int offsetInNewText = offsetInOldText;
//		- StringBuilder newSection = oldText[0:offsetInOldText];
//		- allSamples = JCasUtils.select(UsageSample.class)
//		- For sample in allSamples:
//			o elements = LinkedHashMap{elementNum : ArgumentExample\PredicateSeed}
//			o matcher = PATT.matcher(sample.getCoveredText)
//			o StringBuilder sb = "";
//			o List<String> tokens = [];
//			o while (matcher.find):
//				* // some update of "elements"
//				* if matcher.group(1) != "":
//					` sb += matcher.group(1)
//					` tokens.add(matcher.group(1))
//				* sb += matcher.group(3)
//				* tokens.add(matcher.group(3))
//				* if matcher.group(4) != "":
//					` sb += matcher.group(4)
//					` tokens.add(matcher.group(4))
//			o String newText = sb.toString()
//			o int oldTextSampleEnd = offsetInOldText + sample.getCoveredText().size()
//			o offsetInOldText = beginningOfNextSampleOrEndOfDoc(currSampleNum)
//			o newSection += newText + oldText[oldTextSampleEnd : offsetInOldText];
//			o sortedmap = DockedTokenFinder.find(newText, tokens)
//			o for Entry<elementNum, anno> : elements:
//				* DockedToken docked = sortedmap.get(elementNum)
//				* if anno instanceof PredicateSeed:
//					` PIUS pius = new PIUS(docked.begin+offsetInNewText, docked.end+offsetInNewText)
//					` anno.piuses.add(pius)
//				* else: //arg
//					` AIUS ...
//			o offsetInNewText += newText.size() + (offsetInOldText - oldTextSampleEnd)
//		- sentenceView.setDocumentText(newSection.toString())
	}
	
	private static void addTreeout(Map<Class<? extends Treeout>, Map<String, Multiset<String>>> treeoutByRole, Map<Class<? extends Treeout>, Map<String, Multiset<String>>> treeoutByVal,
			Map<Class<? extends Treeout>, Multimap<String, ArgumentInUsageSample>> aiusesByTreeout, Class<? extends Treeout> cls, ArgumentInUsageSample aius, String role, String value) throws CASException {
		JCas jcas = aius.getCAS().getJCas();
		Type type = jcas.getTypeSystem().getType(cls.getName());
		AnnotationFS newAnno = jcas.getCas().createAnnotation(type, aius.getBegin(), aius.getEnd());
		Treeout treeout = (Treeout) newAnno;

		// Pad role with '_', so that all roles are aligned, and are displayed well in Chrome (which omits consecutive spaces!!!)
		final int ROLE_PADDED_LEN=12;
		treeout.setRole(StringUtils.rightPad(role, ROLE_PADDED_LEN, "_")/*String.format("%s%s", role, role.length()<ROLE_PADDED_LEN?StringUtils.repeat('_', ROLE_PADDED_LEN-role.length()):"")*/);

		treeout.setValue(value);
		treeout.addToIndexes();
		
		// Handle treeoutByRole
		Map<String, Multiset<String>> ofCls = treeoutByRole.get(cls);
		if (ofCls == null) {
			ofCls = Maps.newHashMap();
			treeoutByRole.put(cls, ofCls);
		}
		Multiset<String> ofRole = ofCls.get(role);
		if (ofRole == null) {
			ofRole = HashMultiset.create();
			ofCls.put(role, ofRole);
		}
		ofRole.add(value);
		
		// Handle treeoutByVal
		ofCls = treeoutByVal.get(cls);
		if (ofCls == null) {
			ofCls = Maps.newHashMap();
			treeoutByVal.put(cls, ofCls);
		}
		Multiset<String> ofVal = ofCls.get(value);
		if (ofVal == null) {
			ofVal = HashMultiset.create();
			ofCls.put(value, ofVal);
		}
		ofVal.add(role);
		
		Multimap<String, ArgumentInUsageSample> ofCls2 = aiusesByTreeout.get(cls);
		if (ofCls2 == null) {
			ofCls2 = HashMultimap.create();
			aiusesByTreeout.put(cls, ofCls2);
		}
		ofCls2.put(value, aius);
		Feature treeoutFeature = getAiusTreeoutFeature(cls, aius);
		///// DEBUG
		if (treeoutFeature == null) {
			System.out.printf("\n\n\n\n\n\n\n\n\ntreeoutFeature == null\n\n\n\n\n\n\n");
		}
		///
		aius.setFeatureValue(treeoutFeature, treeout);
	}
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			jcas.setDocumentLanguage("EN");
			JCas tokenView = jcas.createView(TOKEN_VIEW);
			JCas sentenceView = jcas.createView(SENTENCE_VIEW);
			JCas sentenceViewTemp = jcas.createView(SENTENCE_VIEW_TEMP);

			tokenView.setDocumentLanguage("EN");
			tokenView.setDocumentText(jcas.getDocumentText());
			sentenceView.setDocumentLanguage("EN");
			// not setting sentenceView's DocumentText - this is done when organizing usage samples
			sentenceViewTemp.setDocumentLanguage("EN");
			sentenceViewTemp.setDocumentText(jcas.getDocumentText()); //NOTE that later we will rebuild the text, by removing all marks - and then set a new Document to the view
			
			// Load basic spec annotation types
			SpecXmlCasLoader loader = new SpecXmlCasLoader();
			Map<String, Map<String, Annotation>> markerMap = loader.load(jcas, tokenView, sentenceViewTemp);
			
			// Remove markers and add pius\aius
			// Also maps each PredicateSeed/ArgumentExample to all of its instances in the usage samples
			// Of course, not every PredicateSeed/ArgumentExample needs an instance in the usage sample, and the ones that do have, can have 1 or many
			Multimap<Annotation, Annotation> toUsage = organizeUsageSamples(tokenView, sentenceViewTemp, sentenceView, markerMap);
			
			// Build once, use many times
			Collection<UsageSample> usageSamples = JCasUtil.select(sentenceView, UsageSample.class);
			
			// For tracing treeouts
			Map<Class<? extends Treeout>, Map<String, Multiset<String>>> treeoutByRole = Maps.newHashMap();
			Map<Class<? extends Treeout>, Map<String, Multiset<String>>> treeoutByVal = Maps.newHashMap();
			Map<Class<? extends Treeout>, Multimap<String, ArgumentInUsageSample>> aiusesByTreeout = Maps.newHashMap();
			
			// Make sure usage samples don't have exact duplicates
			List<String> sampleTextsList = JCasUtil.toText(usageSamples);
			Set<String> sampleTextsSet = Sets.newHashSet(sampleTextsList);
			if (sampleTextsList.size() != sampleTextsSet.size()) {
				throw new SpecXmlException(String.format("Found duplicates in Usage Samples! There are %d samples, but only %s unique ones", sampleTextsList.size(), sampleTextsSet.size()));
			}
			
			// Make sure argument type adhere to ACE types
			validateArgumentTypes(tokenView);
			
			// Add linguistic segmentation annotations - Token and Sentence
			Annotation anno;
			Iterator<Annotation> iterElement = Iterators.concat(JCasUtil.iterator(tokenView, PredicateSeed.class), JCasUtil.iterator(tokenView, ArgumentExample.class));
			while (iterElement.hasNext()) {
				anno = iterElement.next();
				Sentence sentence = new Sentence(tokenView, anno.getBegin(), anno.getEnd());
				sentence.addToIndexes();
			}
			for (UsageSample sample : usageSamples) {
				Sentence sentence = new Sentence(sentenceView, sample.getBegin(), sample.getEnd());
				sentence.addToIndexes();
			}
			
			tokenAE.process(tokenView);
			if (Perceptron.controllerStatic.useArguments) {
//				System.out.printf("\n\n\n**** the class: \n");
//				System.out.println(edu.stanford.nlp.tagger.maxent.ExtractorFrames.class.getProtectionDomain().getCodeSource().getLocation().getPath());
				sentenceAE.process(sentenceView);
			}
			
			// For each lemma value, remember all of its PredicateSeeds/ArgumentExamples
			// this way we can verify if any of them appeared more than once - which is legit, but not for UsageSamples
			// NOTE: now things have changed, and we don't use this lemma mapping for Usage Samples anymore
			// However, we still call, it, as it has the side-effect of making sure there are no illegitimate repetitions
			Multimap<String, Annotation> lemmasToAnnotations = HashMultimap.create();
			
			Predicate predicate = JCasUtil.selectSingle(tokenView, Predicate.class);
			lemmasToAnnotations.putAll(getLemmaToAnnotation(tokenView, predicate, PredicateSeed.class, "predicate"));
			
			for (Argument arg : JCasUtil.select(tokenView, Argument.class)) {
				lemmasToAnnotations.putAll(getLemmaToAnnotation(tokenView, arg, ArgumentExample.class, "argument"));
			}
			
			FragmentLayer fragmentLayer = new FragmentLayer(sentenceView, Document.converter);
			
			for (UsageSample sample : usageSamples) {
				sample.setText(sample.getCoveredText());
				BasicNode sampleRoot = fragmentLayer.getRoot(sample);
				sample.setTreeout(TreeToLineString.getStringWordRelCanonicalPos(sampleRoot));
				
				// Now set a pius for each aius!
				// Assuming exactly one pius per sample
				List<PredicateInUsageSample> piuses = JCasUtil.selectCovered(sentenceView, PredicateInUsageSample.class, sample);
				if (piuses.size() == 0) {
					if (Perceptron.controllerStatic.enhanceSpecs) {
						System.err.printf("Found a sample without predicate, during enhanceSpecs - skipping: %s\n\n", sample.getCoveredText());
						continue;
					}
					else {
						throw new SpecXmlException(String.format("Usage example does not contain any predicate seed! '%s'", sample.getCoveredText()));
					}
				}
				else if (piuses.size() > 1) {
					throw new SpecXmlException(String.format("Usage example has more than one predicate seed (%s), in: '%s'",
							JCasUtil.toText(piuses), sample.getCoveredText()));
				}
				else {
					PredicateInUsageSample pius = piuses.get(0);
					for (ArgumentInUsageSample aius : JCasUtil.selectCovered(ArgumentInUsageSample.class, sample)) {
						aius.setPius(pius);
						aius.setSample(sample);
						
						// Build fragment layer stuff (twice, also without conj)
						FragmentAndReference linkFrag = fragmentLayer.getRootLinkingTreeFragment(pius, aius, false, null);
						List<BasicNode> subroots = ImmutableList.of(linkFrag.getFragmentRoot());
						FragmentAndReference linkFragNoConj = fragmentLayer.getRootLinkingTreeFragment(pius, aius, true, null);
						List<BasicNode> subrootsNoConj = ImmutableList.of(linkFragNoConj.getFragmentRoot());

						String role = aius.getArgumentExample().getArgument().getRole().getCoveredText();
						addTreeout(treeoutByRole, treeoutByVal, aiusesByTreeout, TreeoutDepNoContext.class, aius, role, TreeToLineString.getStringRel(subroots, false, true));
						addTreeout(treeoutByRole, treeoutByVal, aiusesByTreeout, TreeoutDepGenPosNoContext.class, aius, role, TreeToLineString.getStringRelCanonicalPos(subroots, false, true));
						addTreeout(treeoutByRole, treeoutByVal, aiusesByTreeout, TreeoutDepSpecPosNoContext.class, aius, role, TreeToLineString.getStringRelPos(subroots, false, true));
						addTreeout(treeoutByRole, treeoutByVal, aiusesByTreeout, TreeoutDepWithContext.class, aius, role, TreeToLineString.getStringRel(subroots, true, true));
						addTreeout(treeoutByRole, treeoutByVal, aiusesByTreeout, TreeoutDepGenPosWithContext.class, aius, role, TreeToLineString.getStringRelCanonicalPos(subroots, true, true));
						addTreeout(treeoutByRole, treeoutByVal, aiusesByTreeout, TreeoutDepSpecPosWithContext.class, aius, role, TreeToLineString.getStringRelPos(subroots, true, true));
						addTreeout(treeoutByRole, treeoutByVal, aiusesByTreeout, TreeoutDepPrepNoContext.class, aius, role, TreeToLineString.getStringRelPrep(subrootsNoConj, false, true));
						addTreeout(treeoutByRole, treeoutByVal, aiusesByTreeout, TreeoutDepPrepGenPosNoContext.class, aius, role, TreeToLineString.getStringRelPrepCanonicalPos(subrootsNoConj, false, true));
						addTreeout(treeoutByRole, treeoutByVal, aiusesByTreeout, TreeoutDepPrepSpecPosNoContext.class, aius, role, TreeToLineString.getStringRelPrepPos(subrootsNoConj, false, true));
						addTreeout(treeoutByRole, treeoutByVal, aiusesByTreeout, TreeoutDepPrepWithContext.class, aius, role, TreeToLineString.getStringRelPrep(subrootsNoConj, true, true));
						addTreeout(treeoutByRole, treeoutByVal, aiusesByTreeout, TreeoutDepPrepGenPosWithContext.class, aius, role, TreeToLineString.getStringRelPrepCanonicalPos(subrootsNoConj, true, true));
						addTreeout(treeoutByRole, treeoutByVal, aiusesByTreeout, TreeoutDepPrepSpecPosWithContext.class, aius, role, TreeToLineString.getStringRelPrepPos(subrootsNoConj, true, true));
					}
				}
				
				// And now, set sample instances for each PredicateSeed/ArgumentExample (and also for Arguments)
				for (PredicateSeed seed : JCasUtil.select(tokenView, PredicateSeed.class)) {
					Collection<Annotation> piusesOfSeed = toUsage.get(seed);
					seed.setPiuses((FSArray) FSCollectionFactory.createFSArray(tokenView, piusesOfSeed));
				}
				for (ArgumentExample example : JCasUtil.select(tokenView, ArgumentExample.class)) {
					Collection<Annotation> aiusesOfExample = toUsage.get(example);
					example.setAiuses((FSArray) FSCollectionFactory.createFSArray(tokenView, aiusesOfExample));
				}
				for (Argument arg : JCasUtil.select(tokenView, Argument.class)) {
					Collection<Annotation> aiusesOfArg = toUsage.get(arg);
					arg.setAiuses((FSArray) FSCollectionFactory.createFSArray(tokenView, aiusesOfArg));
				}
				
			}
			
			for (Class<?> cls : treeoutByRole.keySet()) {
				Map<String, Multiset<String>> byCls = treeoutByRole.get(cls);
				for (String role : byCls.keySet()) {
					Multiset<String> byRole = byCls.get(role);
					List<String> strs = Lists.newArrayList();
					for (String val : byRole.elementSet()) {
						int count = byRole.count(val);
						String s = String.format("%s*%s", count, val);
						strs.add(s);
					}
					String oneStr = StringUtils.join(strs, " | ");
					
					TAll tAll = new TAll(sentenceView);
					tAll.setCls(cls.getSimpleName());
					tAll.setRole(role);
					tAll.setVal(oneStr);
					tAll.addToIndexes();
				}
			}
			for (Class<? extends Treeout> cls : treeoutByVal.keySet()) {
				Map<String, Multiset<String>> byCls = treeoutByVal.get(cls);
				for (String treeout : byCls.keySet()) {
					Multiset<String> byTreeout = byCls.get(treeout);
					List<String> strs = Lists.newArrayList();
					for (String role : byTreeout.elementSet()) {
						int count = byTreeout.count(role);
						String s = String.format("%s*%s", count, role);
						strs.add(s);
					}
					String oneStr = StringUtils.join(strs, " | ");

					VAll vAll = new VAll(sentenceView);
					vAll.setCls(cls.getSimpleName());
					vAll.setTreeout(treeout);
					vAll.setVal(oneStr);
					vAll.addToIndexes();
					
					Collection<ArgumentInUsageSample> aiuses = aiusesByTreeout.get(cls).get(treeout);
					for (ArgumentInUsageSample aius : aiuses) {
						Feature treeoutFeature = getAiusTreeoutFeature(cls, aius);
						Treeout treeoutAnno = (Treeout) aius.getFeatureValue(treeoutFeature);
						/// DEBUG
//						if (treeoutAnno.getValue().contains("<SUBROOT>[PRD](dobj->NN[ARG])") &&
//								cls == TreeoutDepSpecPosNoContext.class) {
//							System.out.printf("\n\n\n\n\n\n\n\ngot treeout1\n\n\n\n\n\n\n");
//						}
//						if (treeoutAnno.getValue().contains("<SUBROOT>[PRD](dobj->NNS[ARG])") &&
//								cls == TreeoutDepSpecPosNoContext.class) {
//							System.out.printf("\n\n\n\n\n\n\n\ngot treeout2\n\n\n\n\n\n\n");
//						}
						///
						VAll existingVAll = treeoutAnno.getVAll();
						if (existingVAll != null && existingVAll != vAll) {
							throw new IllegalStateException(String.format("Trying to set vAll of <%s> to treeout %s<%s>, but this treeout already has a vAll of <%s>!",
									vAll.getVal(), cls.getSimpleName(), treeoutAnno.getValue(), existingVAll.getVal()));
						}
						treeoutAnno.setVAll(vAll);
					}
				}
			}
						
			System.err.printf("- In spec '%s': %d tokens in token view, %s tokens in sentence view\n", 
					getSpecLabel(jcas), JCasUtil.select(tokenView, Token.class).size(), JCasUtil.select(sentenceView, Token.class).size());
		}
		catch (Exception e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e); 
		}
		catch (AceAbnormalMessage e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e); 
		}

	}

	public static final String ANNOTATOR_FILE_PATH = "/desc/SpecAnnotator.xml";
	public static final String TOKEN_VIEW = "TokenBasedView";
	public static final String SENTENCE_VIEW = "SentenceBasedView";
	public static final String SENTENCE_VIEW_TEMP = "SentenceBasedView_WithMarkers";
}
