package ac.biu.nlp.nlp.ie.onthefly.input;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.LemmaByPos;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.NounLemma;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.VerbLemma;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.util.ParserWrapper;

public class TypedStanfordLemmatizerAE extends JCasAnnotator_ImplBase {
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Token token : JCasUtil.select(jcas, Token.class)) {
			String lemmaStr;
			String tokenStr = token.getCoveredText();
			LemmaByPos lemmaAnno;
			
			lemmaStr = ParserWrapper.lemmanize(tokenStr, "V");
			lemmaAnno = new VerbLemma(jcas, token.getBegin(), token.getEnd());
			lemmaAnno.setValue(lemmaStr);
			lemmaAnno.setPosStr("V");
			lemmaAnno.addToIndexes();
			
			lemmaStr = ParserWrapper.lemmanize(tokenStr, "NN");
			lemmaAnno = new NounLemma(jcas, token.getBegin(), token.getEnd());
			lemmaAnno.setValue(lemmaStr);
			lemmaAnno.setPosStr("NN");
			lemmaAnno.addToIndexes();
		}
	}
}
