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
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.Sentence.Sent_Attribute;
import edu.cuny.qc.util.TokenAnnotations;

/**
 * This class is to analysize the training 
 * @author XX
 *
 */
public class Analysis_Triggers
{
	public static Map<String, Double> num_token = new HashMap<String, Double>();
	public static Map<String, Map<String, Double>> num_token_event = new HashMap<String, Map<String, Double>>();
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
				List<Map<Class<?>, Object>> tokens =  (List<Map<Class<?>, Object>>) sent.get(Sent_Attribute.Token_FEATURE_MAPs);
				
				// count num of event mentions
				for(AceEventMention event : sent.eventMentions)
				{
					for(int anchorIndex : event.getHeadIndices())
					{
						String lemma = (String) tokens.get(anchorIndex).get(TokenAnnotations.LemmaAnnotation.class);
						Map<String, Double> temp = num_token_event.get(event.getSubType());
						if(temp == null)
						{
							temp = new HashMap<String, Double>();
							num_token_event.put(event.getSubType(), temp);
						}
						Double freq = temp.get(lemma);
						if(freq == null)
						{
							freq = 0.0;
						}
						freq++;
						temp.put(lemma, freq);
					}
				}
				
				// count token num
				for(int token_id=0; token_id<sent.size(); token_id++)
				{
					String lemma = (String) tokens.get(token_id).get(TokenAnnotations.LemmaAnnotation.class);
					Double count = num_token.get(lemma);
					if(count == null)
					{
						count = 0.0;
					}
					count++;
					num_token.put(lemma, count);
				}
			}
		}
		
		// sort by frequency of num_event/num_token
		for(String eventType : num_token_event.keySet())
		{
			Map<String, Double> event_map = num_token_event.get(eventType);
			Map<String, Double> event_map_freq = map_freq.get(eventType);
			if(event_map_freq == null)
			{
				event_map_freq = new HashMap<String, Double>();
				map_freq.put(eventType, event_map_freq);
			}
			for(String name : event_map.keySet())
			{
				Double count_entity = num_token.get(name);
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
				Double freq = num_token.get(name);
				if(freq < 2.0 || entry.getValue() < 0.1)
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
		File filelist = new File("/Users/XX/Data/ACE/filelist_ACE_training");
		
		Alphabet nodeTargetAlphabet = new Alphabet();
		Alphabet edgeTargetAlphabet = new Alphabet();
		Alphabet featureAlphabet = new Alphabet();
		Controller controller = new Controller();
		
		PrintStream out = new PrintStream(new File("/Users/XX/Analysis_Triggers"));
		readInstanceList(src, filelist, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, true, out);
		out.close();
	}
}
