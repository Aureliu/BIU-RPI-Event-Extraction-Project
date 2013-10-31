package edu.cuny.qc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InvalidFormatException;

/**
 * this is a wrapper for OpenNLP PoSTagger
 * @author z0034d5z
 *
 */
public class POSTaggerWrapper 
{
	public static POSTaggerWrapper poswrapper;
	
	public static POSTaggerWrapper getTagger() throws InvalidFormatException, IOException
	{
		if(poswrapper == null)
		{
			poswrapper = new POSTaggerWrapper(new File("data/en-pos-maxent.bin"));
		}
		return poswrapper;
	}
	
	POSTaggerME tagger;
	
	POSTaggerWrapper(File model_File) throws InvalidFormatException, IOException
	{
		InputStream modelIn = new FileInputStream(model_File);
		POSModel model = new POSModel(modelIn);
		tagger = new POSTaggerME(model);
		
	}
	
	/**
	 * the tokens
	 * @param sent the tokens
	 * @return the tags for each token
	 */
	public String[] poSTag(String[] sent)
	{
		return tagger.tag(sent);
	}
	
	public static void main(String[] args) throws InvalidFormatException, IOException
	{
		String sent = "Yasser ( Arafat ) Murdered ?";
		String[] pos = getTagger().poSTag(sent.split("\\s"));
		
		String[] tokens = sent.split("\\s");
		for(int i=0; i<tokens.length; i++)
		{
			System.out.println(tokens[i] + " " + pos[i]);
		}
		for(int i=0; i<tokens.length; i++)
		{
			System.out.print(tokens[i] + " ");
		}
		System.out.println();
		for(int i=0; i<tokens.length; i++)
		{
			System.out.print(pos[i] + " ");
		}
	}
}
