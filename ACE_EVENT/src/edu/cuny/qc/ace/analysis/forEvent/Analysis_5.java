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

import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.util.TokenAnnotations;

public class Analysis_5
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
		int sent_num = 0;
		
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		int totalNumEventMentions = 0;
		String line = "";
		List<SentenceInstance> instancelist = new ArrayList<SentenceInstance>();
		
		// number of events
		Map<String, Double> freq = new HashMap<String, Double>();
		// ratio of events co-occur in the same sent
		Map<String, Map<String, Double>> map_co_occur = new HashMap<String, Map<String,Double>>();
		Map<String, Map<String, Double>> map_freq = new HashMap<String, Map<String,Double>>();
		
		while((line = reader.readLine()) != null)
		{
			System.out.println(line);
			boolean monoCase = line.contains("bn/") ? true : false;
			
			Document doc = new Document(srcDir + File.separator + line, true, monoCase);

			TextFeatureGenerator.doPreprocessCheap(doc);
			Alphabet nodeTargetAlphabet = new Alphabet();
			Alphabet edgeTargetAlphabet = new Alphabet();
			Alphabet featureAlphabet = new Alphabet();
			Controller controller = new Controller();
			
			sent_num += doc.getSentences().size();
			
			for(int sent_id=0 ; sent_id<doc.getSentences().size(); sent_id++)
			{
				Sentence sent = doc.getSentences().get(sent_id);
				SentenceInstance inst = new SentenceInstance(sent, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, 
						controller, true);
				instancelist.add(inst);
				
				// count the pairs of event happends in the same sentence
				List<String> eventMentions = new ArrayList<String>();
				for(int i=0; i<inst.size(); i++)
				{
					String trigger_label = inst.target.getLabelAtToken(i);
					if(!trigger_label.equals(SentenceAssignment.Default_Trigger_Label))
					{
						eventMentions.add(trigger_label);
					}
				}
				
				for(int i=0; i<eventMentions.size(); i++)
				{
					String label = eventMentions.get(i);
					Double count = freq.get(label);
					if(count == null)
					{
						count = 0.0;
					}
					count++;
					freq.put(label, count);
					
					for(int j=0; j<eventMentions.size(); j++)
					{
						if(j != i)
						{
							String labelj = eventMentions.get(j);
							Map<String, Double> percentage = map_co_occur.get(label);
							if(percentage == null)
							{
								percentage = new HashMap<String, Double>();
								map_co_occur.put(label, percentage);
							}
							Double num = percentage.get(labelj);
							if(num == null)
							{
								num = 0.0;
							}
							num++;
							percentage.put(labelj, num);
						}
					}
				}
				
				
//				for(int i=0; i<inst.size(); i++)
//				{
//					String labeli = inst.target.getLabelAtToken(i);
//					if(!labeli.equals(SentenceAssignment.Default_Trigger_Label))
//					{
//						for(int j=i+1; j<inst.size(); j++)
//						{
//							String labelj = inst.target.getLabelAtToken(i);
//							if(!labelj.equals(SentenceAssignment.Default_Trigger_Label))
//							{
//								List<String> synsi = (List<String>) inst.getTokenFeatureMaps().get(i).get(TokenAnnotations.SynonymsAnnotation.class);
//								List<String> synsj = (List<String>) inst.getTokenFeatureMaps().get(j).get(TokenAnnotations.SynonymsAnnotation.class);
//								if(synsi != null && synsj != null && !Collections.disjoint(synsi, synsj))
//								{
//									System.out.println(doc.docID);
//									
//									String texti = (String) inst.getTokenFeatureMaps().get(i).get(TokenAnnotations.TextAnnotation.class);
//									String textj = (String) inst.getTokenFeatureMaps().get(j).get(TokenAnnotations.TextAnnotation.class);
//									System.out.println(labeli + "#" + labelj + "#" + texti + "#" + textj);
//								}
//							}
//						}
//					}
//				}
			}
		}
		
		extractTriggerLabelBigrams(instancelist);
		
		// print pos tag and freq
		for(String pre : labelBigram.keySet())
		{
			List<String> next = labelBigram.get(pre);
			System.out.print(pre + " --> ");
			for(String nex : next)
			{
				System.out.print(nex + " ");
			}
			System.out.println();
		}
		
		System.out.println("Total num of event mentions :" + totalNumEventMentions);
		
		System.out.println("Total num of sents :" + sent_num);
		
		for(String label : map_co_occur.keySet())
		{
			Double label_count = freq.get(label);
			Map<String, Double> co_occur = map_co_occur.get(label);
			for(String labelj : co_occur.keySet())
			{
				Double num = co_occur.get(labelj);
				num = num / label_count;
				co_occur.put(labelj, num);
			}
		}
		
		// sort and print 
		for(String eventType : map_co_occur.keySet())
		{
			// sort
			List<Entry<String, Double>> entries = new ArrayList<Entry<String, Double>>();
			entries.addAll(map_co_occur.get(eventType).entrySet());
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
				out.println(eventType + "\t" + entry.getKey() + "\t" + entry.getValue());
			}
		}
	}
	// label bigram
	private Map<String, List<String>> labelBigram = new HashMap<String, List<String>>();
	
	protected void extractTriggerLabelBigrams(List<SentenceInstance> traininglist)
	{
		for(SentenceInstance instance : traininglist)
		{
			SentenceAssignment target = instance.target;
			String prev = SentenceAssignment.PAD_Trigger_Label;
			for(int i=0; i<target.getNodeAssignment().size(); i++)
			{
				Integer index = target.getNodeAssignment().get(i);
				String label = (String) target.nodeTargetAlphabet.lookupObject(index);
				
				List<String> list = labelBigram.get(prev);
				if(list == null)
				{
					list = new ArrayList<String>();
				}
				if(!list.contains(label))
				{
					list.add(label);
				}
				labelBigram.put(prev, list);
				prev = label;
			}
		}
	}

	static public void main(String[] args) throws DocumentException, IOException
	{
		Analysis_5 ana = new Analysis_5();
		
		File srcDir = new File("/Users/XX/Data/ACE/ACE2005-TrainingData-V6.0/English/");
		File file_list = new File("/Users/XX/Data/ACE/filelist_ACE_all");
		
		ana.doAnalysis(srcDir, file_list, System.out);
	}
}
