package edu.cuny.qc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

/**
 * this is a thin wrapper of sentence detector and tokenizer of OpenNLP
 * @author z0034d5z
 *
 */
public class TokenizerWrapper 
{
	public static TokenizerWrapper tokenizerWrapper;
	
	public static TokenizerWrapper getTokenizer() throws InvalidFormatException, IOException
	{
		if(tokenizerWrapper == null)
		{
			tokenizerWrapper = new TokenizerWrapper(new File("src/main/resources/data/en-token.bin"));
		}
		return tokenizerWrapper;
	}
	
	TokenizerME tokenizer;
	
	TokenizerWrapper(File model_File) throws InvalidFormatException, IOException
	{
		InputStream modelIn = new FileInputStream(model_File);
		TokenizerModel model = new TokenizerModel(modelIn);
		modelIn.close();	
		tokenizer = new TokenizerME(model);
	}
	
	/**
	 * the tokens
	 * @param sent the tokens
	 * @return the tags for each token
	 */
	public String[] tokenize(String text)
	{
		return tokenizer.tokenize(text);
	}
	
	/**
	 * the span, in which we can know the charactor offset of each token
	 * @param text
	 * @return
	 */
	public edu.cuny.qc.util.Span[] tokenizeSpan(String text)
	{
		Span[] spans = tokenizer.tokenizePos(text);
		edu.cuny.qc.util.Span[] ret = new edu.cuny.qc.util.Span[spans.length];
		int i=0;
		for(Span span : spans)
		{
			edu.cuny.qc.util.Span cunySpan = new edu.cuny.qc.util.Span(span.getStart(), span.getEnd() - 1);
			ret[i++] = cunySpan;
		}
		return ret;
	}

	
	public static void main(String[] args) throws InvalidFormatException, IOException
	{
		String text = "a reuters correspondent said dozens of iraqi civilians and soldiers were killed in what witnesses called a barrage of U.S. artillery.";
		
		String[] sents = TokenizerWrapper.getTokenizer().tokenize(text);
		for(String sent : sents)
		{
			System.out.println(sent);
		}
		
	}
}
