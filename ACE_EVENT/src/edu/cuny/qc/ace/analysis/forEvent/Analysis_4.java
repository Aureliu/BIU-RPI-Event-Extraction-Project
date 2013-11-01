package edu.cuny.qc.ace.analysis.forEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dom4j.DocumentException;

import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceEventMention;
import edu.cuny.qc.ace.acetypes.AceTimexMention;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.Sentence.Sent_Attribute;

public class Analysis_4
{
	/**
	 * give a file list, make analysis on all files
	 * @param srcDir
	 * @param file_list
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void doAnalysis(File srcDir, File file_list, PrintStream out) throws IOException, DocumentException
	{
		Map<String, Integer> posMap = new HashMap<String, Integer>();
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		int totalNumEventMentions = 0;
		String line = "";
		while((line = reader.readLine()) != null)
		{
			boolean monoCase = line.contains("bn/") ? true : false;
			
			Document doc = new Document(srcDir + File.separator + line, true, monoCase);
			int numEventMentions = doAnalysisForFile(doc, posMap);
			totalNumEventMentions += numEventMentions;
		}
		
		// print pos tag and freq
		for(String pos : posMap.keySet())
		{
			Integer freq = posMap.get(pos);
			System.out.println(pos + "\t" + freq);
		}
		
		System.out.println("Total num of event mentions :" + totalNumEventMentions);
	}

	private static int doAnalysisForFile(Document doc, Map<String, Integer> posMap) throws IOException
	{
		int eventMentionNum = doc.getAceAnnotations().eventMentions.size();
		int eventMentionNum2 = 0;
		List<AceEventMention> list = new ArrayList<AceEventMention>();
		
		TextFeatureGenerator.doPreprocessCheap(doc);
		for(Sentence sent : doc.getSentences())
		{
			eventMentionNum2 += sent.eventMentions.size();
			list.addAll(sent.eventMentions);
			
			for(AceEventMention mention : sent.eventMentions)
			{
				if(mention.anchorText.equalsIgnoreCase("it"))
				{
//					System.err.println("XXXXXXXXX---" + mention);
					int index = mention.getHeadIndices().get(0);
					index = index - 1;
					String[] tokens = (String[]) sent.get(Sent_Attribute.TOKENS);
					if(index < 0 || tokens[index].matches("\\.|,"))
					{
//						System.err.println("XXXXXXXXX---" + "head " + mention);
					}
				}
				
				// check if the scope of event trigger overlaps with other entities like time
				for(AceEntityMention entity : sent.entityMentions)
				{
					List<Integer> list1 = entity.getHeadIndices();
					List<Integer> list2 = mention.getHeadIndices();
					if(overlap(list1, list2))
					{
//						System.err.println("overlap " + entity.getType() + mention);
					}
				}
				for(AceTimexMention entity : sent.timexMentions)
				{
					List<Integer> list1 = entity.getHeadIndices();
					List<Integer> list2 = mention.getHeadIndices();
					if(overlap(list1, list2))
					{
//						System.err.println("overlap " + entity.getType() + "\t" + entity + "\t" + mention);
					}
				}
				
				Vector<Integer> vec = mention.getHeadIndices();
				if(vec.size() == 0)
				{
					continue;
				}
				Integer index = vec.get(0);
				String[] posTags = (String[]) sent.get(Sent_Attribute.POSTAGS);
				String pos = posTags[index];
				
				Integer freq = posMap.get(pos);
				if(freq == null)
				{
					freq = 0;
				}
				freq++;
				posMap.put(pos, freq);
				
				if(pos.matches("WP|RP|FW|PRP$|CD|MD|WDT|WRB"))
				{
//					System.out.println(pos + "\t" + mention.anchorText + "\t" + mention.text);
				}
				
			}
		}
		
		if(eventMentionNum2 != eventMentionNum)
		{
			System.out.println(list);
			System.out.println(doc.docID);
		}
		
		return eventMentionNum;
	}

	private static boolean overlap(List<Integer> list1, List<Integer> list2)
	{
		for(int i : list1)
		{
			for(int j : list2)
			{
				if(i == j)
				{
					return true;
				}
			}
		}
		return false;
	}

	static public void main(String[] args) throws DocumentException, IOException
	{
		File srcDir = new File("/Users/che/Data/ACE/ACE2005-TrainingData-V6.0/English/");
		File file_list = new File("/Users/che/Data/ACE/filelist_ACE_all");
		
		PrintStream out = new PrintStream(new File("/Users/che/event_triggers"));
		doAnalysis(srcDir, file_list, out);
	
		out.close();
	}
}
