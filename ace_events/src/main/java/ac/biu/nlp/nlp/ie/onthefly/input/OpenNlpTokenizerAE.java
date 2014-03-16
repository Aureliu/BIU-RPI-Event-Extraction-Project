package ac.biu.nlp.nlp.ie.onthefly.input;

import java.io.File;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.util.Span;
import edu.cuny.qc.util.TokenizerWrapper;

public class OpenNlpTokenizerAE extends JCasAnnotator_ImplBase {
	
	/**
	 * Inspired by part of {@link edu.cuny.qc.perceptron.types.Document#readDoc(File, boolean)}
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException
	{
		try {
			for(Sentence sentAnno : JCasUtil.select(jcas, Sentence.class)) {
				String sentenceText = sentAnno.getCoveredText();
				int sentenceBegin = sentAnno.getBegin();
				Span[] tokenSpans = TokenizerWrapper.getTokenizer().tokenizeSpan(sentenceText);
				for (Span tokenSpan : tokenSpans) {
					Token tokenAnno = new Token(jcas, sentenceBegin+tokenSpan.start(), sentenceBegin+tokenSpan.end()+1);
					tokenAnno.addToIndexes();
				}
			}
		}
		catch (IOException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}

	}
}
