package edu.cuny.qc.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBEscapingProcessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * this is Wrapper of Stanford PosTagger 
 * @author che
 *
 */
public class POSTaggerWrapperStanford 
{
	static protected String taggerModelPath; 
	
	static
	{
		taggerModelPath = "data" + File.separator + "left3words-wsj-0-18.tagger";
	}
		
	private MaxentTagger tagger;
	
	private PTBEscapingProcessor<HasWord, ?, ?> escaper;
	
	// singleton for tagger
	static protected POSTaggerWrapperStanford taggerWrapper = null;

	static public POSTaggerWrapperStanford getPosTagger() throws IOException
	{
		if(taggerWrapper == null)
		{
			taggerWrapper = new POSTaggerWrapperStanford(); 
		}
		return taggerWrapper;
	}
	
	private POSTaggerWrapperStanford()
	{
		;
	}
	
	/**
	 * get tag sent that tokenized
	 * @param sentence
	 * @return
	 */
	public String[] posTag(String[] tokens)
	{	
		List<HasWord> sentence = new ArrayList<HasWord>();
		for(String token : tokens)
		{
			Word word = new Word(token);
			sentence.add(word);
		}
		
		// in case the sentence is tokenized correctly 
		sentence = getEscaper().apply(sentence);
		
		for(int i=0; i<sentence.size(); i++)
		{
			tokens[i] = sentence.get(i).word();
		}
		
		ArrayList<TaggedWord> tSentence = getTagger().tagSentence(sentence);
		String[] postags = new String[tokens.length];
		for(int i=0; i<tSentence.size(); i++)
		{
			postags[i] = tSentence.get(i).tag();
		}
		return postags;
	}
	
	/**
	 * in case the sentence is tokenized correctly, but not correctly escaped,
	 * we need use the escaper
	 * @return
	 */
	protected PTBEscapingProcessor<HasWord, ?, ?> getEscaper()
	{
		if(escaper == null)
		{
			escaper = new PTBEscapingProcessor();
		}
		return escaper;
	}
	
	protected void setTagger(MaxentTagger tagger) 
	{
		this.tagger = tagger;
	}

	protected MaxentTagger getTagger() 
	{
		if(tagger == null)
		{
			try 
			{
				tagger = new MaxentTagger(taggerModelPath);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
		return tagger;
	}
	
	public static void main(String[] args) throws Exception 
	{
		String rawSent = "Re : Yasser ( Arafat ) Murdered ?";
		String tags[] = getPosTagger().posTag(rawSent.split("\\s"));
		for(String tag: tags)
		{
			System.out.println(tag);
		}
	}
}
