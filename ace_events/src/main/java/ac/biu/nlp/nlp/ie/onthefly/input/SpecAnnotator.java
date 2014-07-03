package ac.biu.nlp.nlp.ie.onthefly.input;

import java.util.Collection;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentType;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.LemmaByPos;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.NounLemma;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateInUsageSample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateName;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.VerbLemma;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceArgumentTypes;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

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
				if (result.containsKey(lemmaStr)) {
					String coveredText = element.getCoveredText();
					throw new SpecXmlException(String.format("element '%s' has lemma '%s' that appears more than once in %s", coveredText, lemmaStr, title));
				}
				result.put(lemmaStr, element);
			}
		}
		return result;
	}

	@SuppressWarnings("unused")
	public void validateArgumentTypes(JCas view) throws SpecXmlException {
		String argTypeStr = null;
		AceArgumentTypes enumvalue;
		try {
			for (ArgumentType argType : JCasUtil.select(view, ArgumentType.class)) {
				argTypeStr = argType.getCoveredText();
				enumvalue = AceArgumentTypes.valueOf(argTypeStr);
			}
		} catch (IllegalArgumentException e) {
			throw new SpecXmlException("Bad value for argument type: " + argTypeStr, e);
		}
	}
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			jcas.setDocumentLanguage("EN");
			JCas tokenView = jcas.createView(TOKEN_VIEW);
			JCas sentenceView = jcas.createView(SENTENCE_VIEW);

			tokenView.setDocumentLanguage("EN");
			tokenView.setDocumentText(jcas.getDocumentText());
			sentenceView.setDocumentLanguage("EN");
			sentenceView.setDocumentText(jcas.getDocumentText());
			
			// Load basic spec annotation types
			SpecXmlCasLoader loader = new SpecXmlCasLoader();
			loader.load(jcas, tokenView, sentenceView);
			
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
			Iterator<UsageSample> iterSentence = JCasUtil.iterator(sentenceView, UsageSample.class);
			while (iterSentence.hasNext()) {
				anno = iterSentence.next();
				Sentence sentence = new Sentence(sentenceView, anno.getBegin(), anno.getEnd());
				sentence.addToIndexes();
			}
			
			tokenAE.process(tokenView);
			sentenceAE.process(sentenceView);
			
			// For each lemma value, remember all of its PredicateSeeds/ArgumentExamples
			// this way we can verify if any of them appeared more than once - which is legit, but not for UsageSamples
			Multimap<String, Annotation> lemmasToAnnotations = HashMultimap.create();
			
			Predicate predicate = JCasUtil.selectSingle(tokenView, Predicate.class);
			lemmasToAnnotations.putAll(getLemmaToAnnotation(tokenView, predicate, PredicateSeed.class, "predicate"));
			
			for (Argument arg : JCasUtil.select(tokenView, Argument.class)) {
				lemmasToAnnotations.putAll(getLemmaToAnnotation(tokenView, arg, ArgumentExample.class, "argument"));
			}
			
			for (UsageSample sample : JCasUtil.select(sentenceView, UsageSample.class)) {
				for (Lemma lemma : JCasUtil.selectCovered(Lemma.class, sample)) {
					String text = lemma.getValue();
					Collection<Annotation> elements = lemmasToAnnotations.get(text);
					if (elements.size() > 1) {
						// We don't allow in the usage samples lemmas that appear more than once in spec elements
						throw new SpecXmlException(String.format("Usage example contains a token ('%s') with a lemma ('%s') that appears more than once in the spec - This is prohibited.",
								lemma.getCoveredText(), text));
					}
					else if (elements.size() == 1) {
						Annotation element = elements.iterator().next();
						if (element instanceof PredicateSeed) {
							PredicateInUsageSample pius = new PredicateInUsageSample(sentenceView, lemma.getBegin(), lemma.getEnd());
							pius.setPredicateSeed((PredicateSeed) element);
							pius.addToIndexes();
						}
						else { //element instanceof ArgumentExample
							ArgumentInUsageSample aius = new ArgumentInUsageSample(sentenceView, lemma.getBegin(), lemma.getEnd());
							aius.setArgumentExample((ArgumentExample) element);
							aius.addToIndexes();
						}
					}
					
					// if elements.size() == 0, this lemma doesn't appear in the spec elements, so we ignore it
				}
			}
			
			Collection<Token> allTokens = JCasUtil.select(jcas, Token.class);
			System.err.printf("%d tokens total in spec\n", allTokens.size());
		}
		catch (Exception e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e); 
		}

	}

	public static final String ANNOTATOR_FILE_PATH = "/desc/SpecAnnotator.xml";
	public static final String TOKEN_VIEW = "TokenBasedView";
	public static final String SENTENCE_VIEW = "SentenceBasedView";
}
