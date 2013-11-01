package edu.cuny.qc.ace.analysis.forEvent;

import java.io.BufferedReader;
import java.io.File;
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

import org.dom4j.DocumentException;

import edu.cuny.qc.ace.acetypes.AceEntity;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceEventMention;
import edu.cuny.qc.ace.acetypes.AceEventMentionArgument;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;

public class AnalysisEventsArguments
{
	public static void readInstanceList(File srcDir, File file_list, 
			Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, 
			Controller controller, boolean learnable, PrintStream out) throws IOException, DocumentException
	{
		System.out.println("Reading training instance ...");
		
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		Map<String, Double> freq = new HashMap<String, Double>();
		while((line = reader.readLine()) != null)
		{
			boolean monoCase = line.contains("bn/") ? true : false;
			String fileName = srcDir + File.separator + line;
			
			System.out.println(fileName);
			
			Document doc = new Document(fileName, true, monoCase);
			
			// fill in text feature vector for each token
			TextFeatureGenerator.doPreprocessCheap(doc);
			for(int sent_id=0 ; sent_id<doc.getSentences().size(); sent_id++)
			{
				Sentence sent = doc.getSentences().get(sent_id);
				// print event types
				for(AceEventMention event : sent.eventMentions)
				{
					List<String> args = new ArrayList<String>();
					for(AceEventMentionArgument arg : event.arguments)
					{
						AceMention mention = arg.value;
						if(mention instanceof AceEntityMention)
						{
							AceEntityMention entityMention = (AceEntityMention) mention;
							String head = getEntityNAM(entityMention);
							args.add(head);
						}
					}
					for(int i=0; i<args.size(); i++)
					{
						String head1 = args.get(i);
						for(int j=i+1; j<args.size(); j++)
						{
							String head2 = args.get(j);
							String first = head1.replace("\n", " ");
							String second = head2.replace("\n", " ");
							
							if(first.compareTo(second) < 0)
							{
								first = head2.replace("\n", " ");
								second = head1.replace("\n", " ");
							}
							
							String key = event.getSubType() + "\t" + head1 + "\t" + head2;
							key = key.toLowerCase();
							Double count = freq.get(key);
							if(count == null)
							{
								count = 0.0;
							}
							count++;
							freq.put(key, count);
						}
					}
				}
			}
		}
		
		// sort and print
		List<Entry<String, Double>> sortEntries = sortEntries(freq);
		for(Entry<String, Double> entry : sortEntries)
		{
			out.println(entry.getKey() + "\t" + entry.getValue());
		}
		
		System.out.println("done");
		return;
	}
	
	static public List<Entry<String, Double>> sortEntries(Map<String, Double> map_freq)
	{
		// sort
		List<Entry<String, Double>> entries = new ArrayList<Entry<String, Double>>();
		entries.addAll(map_freq.entrySet());
		Collections.sort(entries, new Comparator<Entry<String, Double>>()
				{
					@Override
					public int compare(Entry<String, Double> o1,
							Entry<String, Double> o2)
					{
						if(o1.getValue() < o2.getValue())
						{
							return 1;
						}
						else if(o1.getValue() == o2.getValue())
						{
							return 0;
						}
						else
						{
							return -1;
						}
					}}
		);
		return entries;
	}
	
	private static String getEntityNAM(AceEntityMention entityMention)
	{
		String name = entityMention.getHeadText();;
		if(entityMention.type.startsWith("NAM") || entityMention.type.startsWith("NOM"))
		{
			name = entityMention.getHeadText();
			return name;
		}
		else
		{
			AceEntity entity = (AceEntity) entityMention.getParent();
			for(AceEntityMention core : entity.mentions)
			{
				if(core.type.startsWith("NAM") || core.type.startsWith("NOM"))
				{
					name = core.getHeadText();
					return name;
				}
			}
		}
		return name;
	}

	public static void main(String[] args) throws IOException, DocumentException
	{
		File src = new File("/Users/XX/Data/ACE/ACE2005-TrainingData-V6.0/English");
		File filelist = new File("/Users/XX/Data/ACE/filelist_ACE_training");
		
		Alphabet nodeTargetAlphabet = new Alphabet();
		Alphabet edgeTargetAlphabet = new Alphabet();
		Alphabet featureAlphabet = new Alphabet();
		Controller controller = new Controller();
		
		PrintStream out = new PrintStream(new File("/Users/XX/Analysis_Arguments"));
		readInstanceList(src, filelist, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, true, out);
		out.close();
	}
}
