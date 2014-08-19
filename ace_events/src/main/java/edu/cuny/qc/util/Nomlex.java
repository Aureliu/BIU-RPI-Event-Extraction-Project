package edu.cuny.qc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Nomlex
{
	
	static Nomlex singleton = null;
	
	static public Nomlex getSingleTon()
	{
		if(singleton == null)
		{
			singleton = new Nomlex();
		}
		return singleton;
	}
	
	protected Nomlex()
	{
		readDict();
	}
	
	// store the map from a word to its base form
	protected Map<String, String> basemap = new HashMap<String, String>();
	
	/**
	 * check if contains this verb
	 * @param verb
	 * @return
	 */
	public boolean contains(String verb)
	{
		return basemap.values().contains(verb);
	}
	
	// convert noun to its verb base form
	// e.g. retirement --> retire
	public String getBaseForm(String word)
	{
		String base = basemap.get(word);
		return base;
	}
	
	public void readDict()
	{
		File filePath = new File("../ace_events_large_resources/src/main/resources/data/NOMLEX");
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = "";
			String verb = null;
			String noun = null;
			while((line = reader.readLine()) != null)
			{
				if(line.startsWith("(NOM"))
				{
					Pattern p = Pattern.compile("\\(NOM :ORTH \"(\\w+)\"");
					Matcher matcher = p.matcher(line);
					if(matcher.find())
					{
						noun = matcher.group(1);
					}
				}
				else if(line.matches("\\s+:VERB \"(\\w+)\""))
				{
					Pattern p = Pattern.compile("\\s+:VERB \"(\\w+)\"");
					Matcher matcher = p.matcher(line);
					if(matcher.find())
					{
						verb = matcher.group(1);
						if(noun != null)
						{
							this.basemap.put(noun, verb);
							noun = null;
						}
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
	}
	
	public static void main(String args[])
	{
		Nomlex comlex = new Nomlex();
		System.out.println(comlex.getBaseForm("connection"));
		System.out.println(comlex.contains("depart"));
	}
}
