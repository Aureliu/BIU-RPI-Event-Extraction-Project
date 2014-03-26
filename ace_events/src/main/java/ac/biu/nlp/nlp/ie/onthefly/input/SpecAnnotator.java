package ac.biu.nlp.nlp.ie.onthefly.input;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentRole;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateName;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample;

import com.google.common.collect.Iterators;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

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
			
			// Add linguistic segmentation annotations - Token and Sentence
			Annotation anno;
			Iterator<Annotation> iterToken = Iterators.concat(JCasUtil.iterator(tokenView, PredicateSeed.class), JCasUtil.iterator(tokenView, ArgumentExample.class));
			while (iterToken.hasNext()) {
				anno = iterToken.next();
				Token token = new Token(tokenView, anno.getBegin(), anno.getEnd());
				token.addToIndexes();
				token.setPos(null); // This is technically redundant, but is here to emphasize that these tokens have a "joker POS", meaning that any POS could be considered for them
			}
			Iterator<UsageSample> iterSentence = JCasUtil.iterator(sentenceView, UsageSample.class);
			while (iterSentence.hasNext()) {
				anno = iterSentence.next();
				Sentence sentence = new Sentence(sentenceView, anno.getBegin(), anno.getEnd());
				sentence.addToIndexes();
			}
			
			tokenAE.process(tokenView);
			sentenceAE.process(sentenceView);
			
//			for (FeatureMechanism featureMechanism : perceptron.featureMechanisms) {
//				featureMechanism.preprocessSpec(jcas);
//			}
			
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
