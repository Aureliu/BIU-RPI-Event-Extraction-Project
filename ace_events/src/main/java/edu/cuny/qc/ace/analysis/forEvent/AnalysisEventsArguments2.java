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

public class AnalysisEventsArguments2
{
	
	public static Map<String, Double> num_entity = new HashMap<String, Double>();
	public static Map<String, Map<String, Double>> num_entity_event = new HashMap<String, Map<String, Double>>();
	public static Map<String, Map<String, Double>> map_freq = new HashMap<String, Map<String, Double>>();
	
	public static void readInstanceList(File srcDir, File file_list, 
			Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, 
			Controller controller, boolean learnable, PrintStream out) throws IOException, DocumentException
	{
		System.out.println("Reading training instance ...");
		
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
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
				
				// increment entity num
				List<String> entities = new ArrayList<String>();
				for(AceEntityMention mention : sent.entityMentions)
				{
					String name = getEntityNAM(mention);
					name = name.toLowerCase();
					name = name.replace("\n", "");
					if(!entities.contains(name))
					{
						entities.add(name);
					}
				}
				for(String name : entities)
				{
					Double freq = num_entity.get(name);
					if(freq == null)
					{
						freq = 0.0;
					}
					freq++;
					num_entity.put(name, freq);
				}
				
				// add events
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
							head = head.toLowerCase();
							head = head.replace("\n", "");
							args.add(head);
						}
					}
					for(String arg : args)
					{
						Map<String, Double> temp = num_entity_event.get(event.getSubType());
						if(temp == null)
						{
							temp = new HashMap<String, Double>();
							num_entity_event.put(event.getSubType(), temp);
						}
						Double freq = temp.get(arg);
						if(freq == null)
						{
							freq = 0.0;
						}
						freq++;
						temp.put(arg, freq);
					}
				}
			}
		}
		
		// sort by frequency of num_arg/num_entity
		
		for(String eventType : num_entity_event.keySet())
		{
			Map<String, Double> event_map = num_entity_event.get(eventType);
			Map<String, Double> event_map_freq = map_freq.get(eventType);
			if(event_map_freq == null)
			{
				event_map_freq = new HashMap<String, Double>();
				map_freq.put(eventType, event_map_freq);
			}
			for(String name : event_map.keySet())
			{
				Double count_entity = num_entity.get(name);
				Double count_event = event_map.get(name);
				Double freq = count_event / count_entity;
				event_map_freq.put(name, freq);
			}
		}
		for(String eventType : map_freq.keySet())
		{
			// sort
			List<Entry<String, Double>> entries = new ArrayList<Entry<String, Double>>();
			entries.addAll(map_freq.get(eventType).entrySet());
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
			// print
			for(Entry<String, Double> entry : entries)
			{
				// filter out entities that with frequency < 10
				String name = entry.getKey();
				Double freq = num_entity.get(name);
				if(freq < 10.0)
				{
					continue;
				}
				out.println(eventType + "\t" + entry.getKey() + "\t" + entry.getValue());
			}
		}
		System.out.println("done");
		return;
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
		File filelist = new File("/Users/XX/Data/ACE/filelist_ACE_all");
		
		Alphabet nodeTargetAlphabet = new Alphabet();
		Alphabet edgeTargetAlphabet = new Alphabet();
		Alphabet featureAlphabet = new Alphabet();
		Controller controller = new Controller();
		
		PrintStream out = new PrintStream(new File("/Users/XX/Analysis_Arguments2"));
		readInstanceList(src, filelist, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, true, out);
		out.close();
	}
}
