package edu.cuny.qc.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

/**
 * This is a thin wrapper for JWI api, some codes are copied from JWI manual
 * http://projects.csail.mit.edu/jwi/
 *
 */
public class WordNetWrapper 
{
	protected IDictionary dict;
	
	protected static WordNetWrapper singleton;
	
	static public WordNetWrapper getSingleTon() throws IOException
	{
		if(singleton == null)
		{
			singleton = new WordNetWrapper();
		}
		
		return singleton;
	}
	
	protected WordNetWrapper() throws IOException
	{
		// construct the URL to the Wordnet dictionary directory
		URL url = new URL("file", null, "src/main/resources/data/WordnetDb");
		
		// construct the dictionary object and open it
		dict = new Dictionary(url);
		dict.open();
	}
	
	/**
	 * convert Treebank pos tag to WordNet compatible
	 * @param pos
	 * @return
	 */
	static public POS convertTreebankPOS(String pos)
	{
		if(pos.length()<2)
		{
			return null;
		}
		
		pos = pos.substring(0,2);
		POS ret = null;
		if(pos.equals("NN"))
		{
			ret = POS.NOUN;
		}
		else if(pos.equals("JJ"))
		{
			ret = POS.ADJECTIVE;
		}
		else if(pos.equals("VB"))
		{
			ret = POS.VERB;
		}
		else if(pos.equals("RB"))
		{
			ret = POS.ADVERB;
		}
		else
		{
			ret = null;
		}	
		return ret;
	}
	
	public ISynset getHypernym(String lemma, String pos)
	{
		List<ISynset> hyms = getHypernyms(lemma, pos);
		if(hyms == null || hyms.size() == 0)
		{
			return null;
		}
		else
		{
			return hyms.get(0);
		}
	}
	
	public List<String> getSynonyms(String lemma, String pos)
	{
		POS pos_wn = convertTreebankPOS(pos);
		if(pos_wn == null)
		{
			return null;
		}
		
		// look up first sense of the word 
		IIndexWord idxWord = null;
		try
		{
			idxWord = dict.getIndexWord(lemma, pos_wn);
		}
		catch(java.lang.IllegalArgumentException e)
		{
			System.err.println("IllegalArgumentException: lemma: " + lemma + "\t" + "POS: " + pos_wn);
			return null;
		}
		if(idxWord == null)
		{
			return null;
		}
		IWordID wordID = idxWord.getWordIDs().get(0); // 1st meaning
		IWord word = dict.getWord(wordID);
		ISynset synset = word.getSynset();
		List<IWord> words = synset.getWords();
		
		List<String> ret = new ArrayList<String>();
		for(IWord temp : words)
		{
			ret.add(temp.getLemma());
		}
		
		return ret;
	}
	
	public List<ISynset> getHypernyms(String lemma, String pos)
	{	
		List<ISynset> ret = new ArrayList<ISynset>();
		
		POS pos_wn = convertTreebankPOS(pos);
		if(pos_wn == null)
		{
			return null;
		}
		
		// look up first sense of the word 
		IIndexWord idxWord = null;
		try
		{
			idxWord = dict.getIndexWord(lemma, pos_wn);
		}
		catch(java.lang.IllegalArgumentException e)
		{
			System.err.println("IllegalArgumentException: lemma: " + lemma + "\t" + "POS: " + pos_wn);
			return null;
		}
		if(idxWord == null)
		{
			return null;
		}
		IWordID wordID = idxWord.getWordIDs().get(0); // 1st meaning
		IWord word = dict.getWord( wordID );
		ISynset synset = word.getSynset();
	
		// get the hypernyms
		List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);
	
		// get each hypernyms id
		for(ISynsetID sid : hypernyms )
		{
			ret.add(dict.getSynset(sid));
		}
		
		// if there isn't hypernym of current word
		// just fill in the synset of itself
		if(ret.size() == 0)
		{
			ret.add(synset);
		}
		return ret;
	}
	
	public List<String> getHypernymWords(String lemma, String pos)
	{	
		List<String> ret = new ArrayList<String>();
		POS pos_wn = convertTreebankPOS(pos);
		if(pos_wn == null)
		{
			return ret;
		}
		
		// look up first sense of the word 
		IIndexWord idxWord = null;
		try
		{
			idxWord = dict.getIndexWord(lemma, pos_wn);
		}
		catch(java.lang.IllegalArgumentException e)
		{
			System.err.println("IllegalArgumentException: lemma: " + lemma + "\t" + "POS: " + pos_wn);
			return null;
		}
		if(idxWord == null)
		{
			return ret;
		}
		IWordID wordID = idxWord.getWordIDs().get(0); // 1st meaning
		IWord word = dict.getWord( wordID );
		ISynset synset = word.getSynset();
	
		// get the hypernyms
		List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);
		for(ISynsetID sid : hypernyms)
		{
			List<IWord> words = dict.getSynset(sid).getWords();
			for(IWord temp : words)
			{
				ret.add(temp.getLemma());
			}
		}
		return ret;
	}
	
	static public void main(String[] args) throws IOException
	{
		WordNetWrapper wrapper = new WordNetWrapper();
		List<ISynset> synsets = wrapper.getHypernyms("found", "VB");
		
		System.out.println("Hypernyms");
		for(ISynset synset : synsets)
		{
			System.out.println(synset.toString());
		}
		
		List<String> words = wrapper.getSynonyms("found", "VB");
		System.out.println("Synonyms");
		for(String word : words)
		{
			System.out.println(word);
		}
	}
}
