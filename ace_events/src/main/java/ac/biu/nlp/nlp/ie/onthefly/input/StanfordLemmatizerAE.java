package ac.biu.nlp.nlp.ie.onthefly.input;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.util.ParserWrapper;

public class StanfordLemmatizerAE extends JCasAnnotator_ImplBase {
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Token token : JCasUtil.select(jcas, Token.class)) {
			String lemma = ParserWrapper.lemmanize(token.getCoveredText(), "V");
			
			Lemma lemmaAnno = new Lemma(jcas, token.getBegin(), token.getEnd());
			lemmaAnno.setValue(lemma);
			lemmaAnno.addToIndexes();
			token.setLemma(lemmaAnno);
		}
	}
	
	static {
		System.err.println("StanfordLemmatizerAE: Always lemmatizing with 'V' POS. Should try to lemmatize with all possible poses, and add lemmas if necessary (with some token-extensions mechanism).");
	}
}
