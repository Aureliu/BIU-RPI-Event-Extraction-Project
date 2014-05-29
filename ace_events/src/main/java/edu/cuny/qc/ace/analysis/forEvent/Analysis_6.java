package edu.cuny.qc.ace.analysis.forEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.DocumentException;

import edu.cuny.qc.ace.acetypes.AceTimexMention;
import edu.cuny.qc.ace.acetypes.AceValueMention;
import edu.cuny.qc.perceptron.types.Document;

public class Analysis_6
{
	/**
	 * give a file list, make analysis on all files
	 * @param srcDir
	 * @param file_list
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void doAnalysis(File srcDir, File file_list, PrintStream out) throws IOException, DocumentException
	{
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		while((line = reader.readLine()) != null)
		{
			System.out.println(line);
			boolean monoCase = line.contains("bn/") ? true : false;
			
			Document doc = new Document(srcDir + File.separator + line, true, monoCase);
			for(AceTimexMention mention : doc.getAceAnnotations().timexMentions)
			{
				String[] tokens = mention.text.split("\\s");
				for(String token : tokens)
				{
					fillFreq(token, freqTime);
				}
			}
			for(AceValueMention mention : doc.getAceAnnotations().valueMentions)
			{
				if(mention.getType().startsWith("Job"))
				{
					String[] tokens = mention.text.split("\\s");
					System.out.println(mention.text);
					for(String token : tokens)
					{
						fillFreq(token, freqTitle);
					}
				}
			}
		}
		
		// sort and print
		List<Entry<String, Integer>> titleList = sortCountMap(freqTitle);
		List<Entry<String, Integer>> timeList = sortCountMap(freqTime);
		
		// print list
		printList(titleList, "/Users/che/title");
		printList(timeList, "/Users/che/time");
	}
	
	public static void printList(List<Entry<String, Integer>> entryList, String filename) throws FileNotFoundException
	{
		PrintStream out = new PrintStream(new File(filename));
		for(Entry<String, Integer> entry : entryList)
		{
			out.print(entry.getKey() + " " + entry.getValue());
			out.println(); 
		}
		out.close();
	}
	
	public static List<Entry<String, Integer>> sortCountMap(Map<String, Integer> map)
	{
		Set<Entry<String, Integer>> entrySet = map.entrySet();
		ArrayList<Entry<String, Integer>> entryList = new ArrayList<Entry<String, Integer>>();
		entryList.addAll(entrySet);
		Collections.sort(entryList, new Comparator<Entry<String, Integer>>()
			{
				// order asending 
				@Override
				public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1) {
					if(arg0.getValue() == arg1.getValue())
					{
						return 0;
					}
					else if(arg0.getValue() > arg1.getValue())
					{
						return -1;
					}
					else
					{
						return 1;
					}
				}
				
			}
		);
		return entryList;
	}
	
	public static void fillFreq(String key, Map<String, Integer> map)
	{
		Integer value = map.get(key);
		if(value == null)
		{
			value = 0;
		}
		value = value + 1;
		map.put(key, value);
	}
	
	// label bigram
	private Map<String, Integer> freqTime = new HashMap<String, Integer>();
	private Map<String, Integer> freqTitle = new HashMap<String, Integer>();

	static public void main(String[] args) throws DocumentException, IOException
	{
		Analysis_6 ana = new Analysis_6();
		
		File srcDir = new File("/Users/che/Data/ACE/ACE2005-TrainingData-V6.0/English/");
		File file_list = new File("/Users/che/Data/ACE/filelist_ACE_training");
		
		ana.doAnalysis(srcDir, file_list, System.out);
	}
}
