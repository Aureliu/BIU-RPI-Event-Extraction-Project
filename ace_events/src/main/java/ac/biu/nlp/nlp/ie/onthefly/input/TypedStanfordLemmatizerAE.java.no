package ac.biu.nlp.nlp.ie.onthefly.input;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.Lists;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.LemmaByPos;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.NounLemma;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.VerbLemma;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.util.ParserWrapper;

public class TypedStanfordLemmatizerAE extends JCasAnnotator_ImplBase {
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		// These are not really sentences, these are almost always single tokens
		// But in some cases it may be a MWE (2-3 words)
		// Anyway, we put a single LemmaByPos on the entire MWE
		for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
			List<String> nounLemmas = Lists.newArrayList();
			List<String> verbLemmas = Lists.newArrayList();
			
			for (Token token : JCasUtil.selectCovered(jcas, Token.class, sentence)) {
				String tokenStr = token.getCoveredText();
				verbLemmas.add(ParserWrapper.lemmanize(tokenStr, "V"));
				nounLemmas.add(ParserWrapper.lemmanize(tokenStr, "N"));
			}
			
			LemmaByPos lemmaAnno;
			String value;

			value = StringUtils.join(verbLemmas, " ");
			lemmaAnno = new VerbLemma(jcas, sentence.getBegin(), sentence.getEnd());
			lemmaAnno.setValue(value);
			lemmaAnno.setPosStr("V");
			lemmaAnno.addToIndexes();
			
			value = StringUtils.join(nounLemmas, " ");
			lemmaAnno = new NounLemma(jcas, sentence.getBegin(), sentence.getEnd());
			lemmaAnno.setValue(value);
			lemmaAnno.setPosStr("NN");
			lemmaAnno.addToIndexes();
		}
	}
}
