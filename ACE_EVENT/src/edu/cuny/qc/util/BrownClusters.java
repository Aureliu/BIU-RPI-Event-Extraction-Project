package edu.cuny.qc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This class is to implements the basic BrownCluster dictionary introduced in Lev Conll 2009
public class BrownClusters 
{

	static private BrownClusters dict = null;
	
	static public BrownClusters getSingleton()
	{
		if(dict == null)
		{
			try
			{
				File dict_path = new File("data/aceAllAndKDD.brownCluster"); 			
				dict = new BrownClusters(dict_path);
			} 
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			} 
		}
		return dict;
	}
	
	
	Map<String, List<String>> map = new HashMap<String, List<String>>();
	
	public BrownClusters(File dict_path) throws FileNotFoundException,IOException 
	{
		initializeDict(dict_path);
	}

	static final Integer[] Prefix_Length = new Integer[]{13, 16, 20};
	
	/**
	 * read the dictionary to memory data structure
	 * @param dictFile
	 */
	protected void initializeDict(File dictFile) 
	{
		System.out.print("loading brown cluster...");
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(dictFile));
		
			String line = "";
			while((line = reader.readLine()) != null)
			{
				// convert tokens into lowercase
				line = line.trim();
				String[] fields = line.split("\\s");
				if(fields.length < 2)
				{
					continue;
				}
				String prefix = fields[0];
				String token = fields[1];
				
				for(int i=0; i<Prefix_Length.length; i++)
				{
					int len = Prefix_Length[i];
					if(prefix.length() >= len)
					{
						String catorgory = prefix.substring(0, len);
						List<String> cats = map.get(token);
						if(cats == null)
						{
							cats = new ArrayList<String>();
							map.put(token, cats);
						}
						cats.add(catorgory);
					}
					
				}
			}
			reader.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("done");
	}
	
	public List<String> getBrownCluster(String token)
	{
		return map.get(token);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		File dict_path = new File("data/aceAllAndKDD.brownCluster"); 
		
		BrownClusters dicts = new BrownClusters(dict_path);
		System.out.println(dicts.getBrownCluster("launched"));
		System.out.println(dicts.getBrownCluster("formed"));
		System.out.println(dicts.getBrownCluster("begins"));
		System.out.println(dicts.getBrownCluster("starts"));
	}
}
