package ac.biu.nlp.nlp.ie.onthefly.input;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.lap.biu.en.lemmatizer.gate.GateLemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;

public class GateLemmatizerAE extends JCasAnnotator_ImplBase {
	private Lemmatizer tool;
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		try {
			tool = new GateLemmatizer(new File(GATE_LEMMATIZER_RULE_FILE).toURI().toURL());
			tool.init();
			System.err.println("We only take one lemma for each tokn, with JOKER pos. Instead, we should for each word run it several times with noun,verb,adj,adv (because for some reason JOKEr returns only one even if could be several, check \"surroundings\" which is 'surround' as verb and 'surrounding' as noun). And, then, take the multiple lemmas and insert them to our great future solution of extended tokens (like the one for NOMLEX, CATVAR).");
		} catch (MalformedURLException e) {
			throw new ResourceInitializationException(e);
		} catch (LemmatizerException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			for (Token token : JCasUtil.select(jcas, Token.class)) {
				tool.set(token.getCoveredText());
				tool.process();
				String lemma = tool.getLemma();
				
				Lemma lemmaAnno = new Lemma(jcas, token.getBegin(), token.getEnd());
				lemmaAnno.setValue(lemma);
				lemmaAnno.addToIndexes();
				token.setLemma(lemmaAnno);
			}
		}
		catch (LemmatizerException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}
	}
	
	private static final String GATE_LEMMATIZER_RULE_FILE = "src/main/resources/data/GATE-3.1/plugins/Tools/resources/morph/default.rul";
}
