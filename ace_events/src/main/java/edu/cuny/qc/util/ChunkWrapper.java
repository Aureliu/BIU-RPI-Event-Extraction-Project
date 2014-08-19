package edu.cuny.qc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.InvalidFormatException;

public class ChunkWrapper {
public static ChunkWrapper chunkwrapper;
	
	public static ChunkWrapper getChunker() throws InvalidFormatException, IOException
	{
		if(chunkwrapper == null)
		{
			chunkwrapper = new ChunkWrapper(new File("../ace_events_large_resources/src/main/resources/data/en-chunker.bin"));
		}
		return chunkwrapper;
	}
	
	ChunkerME chunker;
	
	ChunkWrapper(File model_File) throws InvalidFormatException, IOException
	{
		InputStream modelIn = new FileInputStream(model_File);
		ChunkerModel model = new ChunkerModel(modelIn);
		chunker = new ChunkerME(model);
	}
	
	/**
	 * the tokens
	 * @param sent the tokens
	 * @return the tags for each token
	 */
	public String[] chunk(String[] sent, String[] pos)
	{
		return chunker.chunk(sent, pos);
	}
	
	public static void main(String[] args) throws InvalidFormatException, IOException
	{
		String sent = "Vitamin B12 of 855 , folic acid of 15.4 .";
		String[] tokens = sent.split("\\s");
		
		String[] pos = POSTaggerWrapper.getTagger().poSTag(tokens);
		String[] chunks = getChunker().chunk(sent.split("\\s"), pos);
		
		for(int i=0; i<tokens.length; i++)
		{
			System.out.println(tokens[i] + "\t" + pos[i] + "\t" + chunks[i]);
		}
	}
}
