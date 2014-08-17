package ac.biu.nlp.nlp.ie.onthefly.input;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.Lists;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.util.Utils;

public class TokenFixerAE extends JCasAnnotator_ImplBase {
	
	static {
		System.err.println("??? TokenFixerAE: We may want to do this after POS tagger and parser. See comments in code.");
		System.err.println("??? TokenFixerAE: We may want to deal with other chars (like /), or multiple occurrences of them in the same token (currently only dealing with 1).");
	}
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException
	{
		throw new RuntimeException("Probably shouldn't use this AE for now, as it screws up the parse for quite a few sentences... " +
									"(eg. in Be-Born, 4 sentences were left with several tokens out of the tree and I got that conversion printed error)");
//		List<Token> copyOfTokens = Lists.newArrayList(JCasUtil.select(jcas, Token.class));
//		for (Token token : copyOfTokens) {
//			
//			// That's the only fix we have so far - separate hyphenated words to distinct tokens.
//			// NOTE we might want to perform this change not now, but after POS tagger and parser -
//			// since changing it now may dramatically hurt the interpretation of the sentence by
//			// the other tools, yet doing the change after them should (?) be OK, as we just want to
//			// conform later to aius and pius, which naybe be partial on stuff like "Egyptian-born"
//			// or "post-war"
//			String text = token.getCoveredText();
//			int idx = text.indexOf("-");
//			if (idx!=-1 && text.length()>idx+1) {
//				token.removeFromIndexes();
//				Token t1 = new Token(jcas, token.getBegin(), token.getBegin()+idx); t1.addToIndexes();
//				Token t2 = new Token(jcas, token.getBegin()+idx, token.getBegin()+idx+"-".length()); t2.addToIndexes();
//				Token t3 = new Token(jcas, token.getBegin()+idx+"-".length(), token.getEnd()); t3.addToIndexes();
//				
//				if (t1.getCoveredText().contains("-") || t3.getCoveredText().contains("-")) {
//					throw new IllegalStateException("Got token with more than one hyphen, currently not handling this: " + text);
//				}
//			}
//		}
	}
}
