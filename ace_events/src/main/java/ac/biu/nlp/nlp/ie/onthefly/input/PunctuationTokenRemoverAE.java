package ac.biu.nlp.nlp.ie.onthefly.input;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.Lists;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.util.Utils;

public class PunctuationTokenRemoverAE extends JCasAnnotator_ImplBase {
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException
	{
		List<Token> copyOfTokens = Lists.newArrayList(JCasUtil.select(jcas, Token.class));
		for (Token token : copyOfTokens) {
			if (Utils.PUNCTUATION.contains(token.getCoveredText())) {
				token.removeFromIndexes();
			}
		}
	}
}
