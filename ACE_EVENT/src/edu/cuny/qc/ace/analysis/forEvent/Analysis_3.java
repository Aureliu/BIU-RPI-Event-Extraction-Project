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
import java.util.Set;
import java.util.Map.Entry;

import org.dom4j.DocumentException;
import org.dom4j.Node;

import edu.cuny.qc.util.Span;

/**
 * Do analysis on ACE 2005 data to see the motivation about joint Event tagging
 * @author che
 *
 */
public class Analysis_3
{
	public Analysis_3()
	{
		// TODO Auto-generated constructor stub
	}
	
	public static class Arg
	{
		public String role;
		public String arg_head;
		public String mention_id;
		public String entity_id;
		
		public String toString()
		{
			return arg_head + "_" + role;
		}
		
		public Arg(String role, String arg, String id)
		{
			this.role = role;
			this.arg_head = arg;
			this.mention_id = id;
			this.entity_id = id.replaceFirst("-\\d+$", "");
		}
	}
	
	/**
	 * object of the event mention
	 * @author che
	 *
	 */
	public static class EventMention
	{
		public Span span;
		public String trigger;
		public String type;
		public List<Arg> arguments;
		public String text;
		
		public EventMention(Node node)
		{
			arguments = new ArrayList<Arg>();
			
			Node charseq = node.selectSingleNode("./extent/charseq");
			text = charseq.getText();
			
			Node anchor = node.selectSingleNode("./anchor/charseq");
			trigger = anchor.getText();
			
			Node parent = node.getParent();
			type = parent.valueOf("@TYPE");
			type += "_" + parent.valueOf("@SUBTYPE");
			
			int start = Integer.parseInt(charseq.valueOf("@START"));
			int end = Integer.parseInt(charseq.valueOf("@END"));
			span = new Span(start, end);
			
			List<Node> args = node.selectNodes("./event_mention_argument");
			String head_string = "";
			for(Node arg : args)
			{
				String role = arg.valueOf("@ROLE");
				String REFID = arg.valueOf("@REFID");
				// timex
				if(REFID.matches(".*-T\\d+-\\d+"))
				{
					Node entity_mention = arg.selectSingleNode("//source_file/document/timex2/timex2_mention[@ID='" + REFID +"']");
					Node entityHead = entity_mention.selectSingleNode("./extent/charseq");
					head_string = entityHead.getText();
				}
				// entity
				else if(REFID.matches(".*-E\\d+-\\d+") || REFID.matches("[^-]+-\\d+-\\d+"))
				{
					Node entity_mention = arg.selectSingleNode("//source_file/document/entity/entity_mention[@ID='" + REFID +"']");
					Node entityHead = entity_mention.selectSingleNode("./head/charseq");
					head_string = entityHead.getText();
				}
				// value
				else if(REFID.matches(".*-V\\d+-\\d+"))
				{
					Node entity_mention = arg.selectSingleNode("//source_file/document/value/value_mention[@ID='" + REFID +"']");
					Node entityHead = entity_mention.selectSingleNode("./extent/charseq");
					head_string = entityHead.getText();
				}
				else
				{
					System.out.print("invalid REFID");
					continue;
				}
				arguments.add(new Arg(role, head_string, REFID));
			}
		}
		
		public String toString()
		{
			String ret = "";
			ret += "trigger:" + this.trigger + "\t" + "type:"+this.type;
			for(Arg pair : arguments)
			{
				ret += "\t" + pair.role + ":" + pair.arg_head;
			}
			return ret;
		}
	}
	
	private static class Stats
	{
		public int num_event_mention;
		public int num_trigger_single_word;
		public Map<String, Integer> map_trigger_freq;
		
		public Stats()
		{
			this.num_event_mention = 0;
			num_trigger_single_word = 0;
			
			map_trigger_freq = new HashMap<String, Integer>();
		}
	}
	
	protected static void doAnalysisForFile(File apf_file, Stats ret, PrintStream out) throws DocumentException, IOException
	{	
		ApfReader apfReader_apf = new ApfReader(apf_file.getAbsolutePath());
		List<Node> nodes_event_mentions = apfReader_apf.getEventMentions();
		
		List<EventMention> event_mentions = new ArrayList<EventMention>();
		for(Node node_event_mention : nodes_event_mentions)
		{
			EventMention event_mention = new EventMention(node_event_mention);
			event_mentions.add(event_mention);
		}
		
		ret.num_event_mention += event_mentions.size();
		
		// check if the event mention anchor is a single word
		for(int i=0; i<event_mentions.size(); i++)
		{
			EventMention event_mention = event_mentions.get(i);
			if(event_mention.trigger.split("\\s").length == 1)
			{
				ret.num_trigger_single_word++;
			}
			else
			{
				out.println(apf_file.getCanonicalPath());
				out.println(event_mention.trigger);
			}
			
			Integer freq = ret.map_trigger_freq.get(event_mention.trigger);
			if(freq == null)
			{
				freq = 0;
			}
			freq++;
			ret.map_trigger_freq.put(event_mention.trigger, freq);
		}
	}
	
	/**
	 * print a ranked list of the frequency map
	 * @param mapType_1Freq
	 * @param out
	 */
	private static void printRankedList(Map<String, Integer> map, PrintStream out)
	{
		Set<Entry<String, Integer>> set = map.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
		Collections.sort(list, new Comparator<Entry<String, Integer>>()
				{
					@Override
					public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1)
					{
						if(arg0.getValue() > arg1.getValue())
						{
							return -1;
						}
						else if(arg0.getValue() < arg1.getValue())
						{
							return 1;
						}
						return 0;
					}
			
				}
		);
		
		// print the top 50
		int count = 50;
		for(Entry<String, Integer> entry : list)
		{
			if(count-- < 0)
			{
				break;
			}
			out.println("" + entry.getKey() + "\t" + entry.getValue());
		}
	}
	
	/**
	 * give a file list, make analysis on all files
	 * @param srcDir
	 * @param file_list
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void doAnalysis(File srcDir, File file_list, PrintStream out) throws IOException, DocumentException
	{
		Stats stats = new Stats();
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		while((line = reader.readLine()) != null)
		{
			File apf_file = new File(srcDir + File.separator + line + ".apf.xml");
			doAnalysisForFile(apf_file, stats, out);
		}
		
		out.println("\n\n");
		out.println("----------------------------------------------");
		out.println("Total num of event mentions: " + stats.num_event_mention);
		out.println("Total num of single word trigger: " + stats.num_trigger_single_word);
		
		// output the freq of trigger
		out.println("\n\n");
		out.println("-----------------------------------------------");
		out.println("frequent triggers");
		printRankedList(stats.map_trigger_freq, out);
		reader.close();
	}

	static public void main(String[] args) throws DocumentException, IOException
	{
		File srcDir = new File("/Users/che/Data/ACE/ACE2005-TrainingData-V6.0/English/");
		File file_list = new File("/Users/che/Data/ACE/ACE2005-TrainingData-V6.0/English/file_list");
		
		PrintStream out = new PrintStream(new File("/Users/che/event_triggers"));
		doAnalysis(srcDir, file_list, out);
	
		out.close();
	}
}

