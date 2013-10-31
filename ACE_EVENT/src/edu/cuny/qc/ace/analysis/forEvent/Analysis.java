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
public class Analysis
{
	public Analysis()
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
		
		public EventMention(Node node, List<Span> sentences, String doc_text)
		{
			arguments = new ArrayList<Arg>();
			
			Node charseq = node.selectSingleNode("./extent/charseq");
			
			Node anchor = node.selectSingleNode("./anchor/charseq");
			trigger = anchor.getText();
			
			Node parent = node.getParent();
			type = parent.valueOf("@SUBTYPE");
			
			int start = Integer.parseInt(charseq.valueOf("@START"));
			int end = Integer.parseInt(charseq.valueOf("@END"));
			span = new Span(start, end);
			if(sentences != null)
			{
				// fix this by having the whole sentence
				for(Span sentSpan : sentences)
				{
					if(sentSpan.overlap(span))
					{
						this.text = sentSpan.getCoveredText(doc_text);
						break;
					}
				}
			}
			else
			{
				this.text = charseq.getText();
			}
			
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
	
	private static class RolePair
	{
		String role_1;
		String role_2;
		
		public RolePair(String role1, String role2)
		{
			this.role_1 = role1;
			this.role_2 = role2;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof RolePair)
			{
				RolePair pair = (RolePair) obj;
				if(pair.role_1.equals(this.role_1) && pair.role_2.equals(this.role_2) || pair.role_1.equals(this.role_2) && pair.role_2.equals(this.role_1))
				{
					return true;
				}
			}
			return false;
		}
		
		@Override
		public int hashCode()
		{
			return role_1.hashCode() + role_2.hashCode();
		}
	}
	
	private static class Stats
	{
		public int num_event_mention;
		public int num_type_1;
		public int num_type_2;
		public int num_type_3;
		
		// the map of type 1 arguments combination freq
		Map<RolePair, Integer> map_type_2_freq;
		Map<RolePair, Integer> map_type_3_freq;
		// the map of event pairs freq
		Map<RolePair, Integer> map_type_1_freq;
		// the map of event_type_role_type to freq
		Map<RolePair, Integer> map_event_role_type_2_freq;
		Map<RolePair, Integer> map_event_role_type_3_freq;
		
		public void addEventRolePairType3(String event_role_1, String event_role_2)
		{
			RolePair pair = new RolePair(event_role_1, event_role_2);
			addRolePairType(pair, map_event_role_type_3_freq);
		}
		
		public void addEventRolePairType2(String event_role_1, String event_role_2)
		{
			RolePair pair = new RolePair(event_role_1, event_role_2);
			addRolePairType(pair, map_event_role_type_2_freq);
		}
		
		public void addRolePairType3(String role1, String role2)
		{
			RolePair pair = new RolePair(role1, role2);
			addRolePairType(pair, map_type_3_freq);
		}
		
		public void addRolePairType2(String role1, String role2)
		{
			RolePair pair = new RolePair(role1, role2);
			addRolePairType(pair, map_type_2_freq);
		}
		
		public void addRolePairType1(String role1, String role2)
		{
			RolePair pair = new RolePair(role1, role2);
			addRolePairType(pair, map_type_1_freq);
		}
		
		protected static void addRolePairType(RolePair pair, Map<RolePair, Integer> map)
		{
			Integer freq = map.get(pair);
			if(freq == null)
			{
				freq = 0;
			}
			freq++;
			map.put(pair, freq);
		}
		
		public Stats()
		{
			this.num_event_mention = 0;
			this.num_type_1 = 0;
			this.num_type_2 = 0;
			this.num_type_3 = 0;
			
			map_type_1_freq = new HashMap<RolePair, Integer>();
			map_type_2_freq = new HashMap<RolePair, Integer>();
			map_type_3_freq = new HashMap<RolePair, Integer>();
			map_event_role_type_2_freq = new HashMap<RolePair, Integer>();
			map_event_role_type_3_freq = new HashMap<RolePair, Integer>();
		}
	}
	
	protected static void doAnalysisForFile(File apf_file, File inline_file, Stats ret, PrintStream out) throws DocumentException
	{	
		// read a list of sentence offsets
		ApfReader apfReader_inline = new ApfReader(inline_file.getAbsolutePath());
		List<Span> sents = apfReader_inline.getSentSpans();
		
		ApfReader apfReader_apf = new ApfReader(apf_file.getAbsolutePath());
		List<Node> nodes_event_mentions = apfReader_apf.getEventMentions();
		
		List<EventMention> event_mentions = new ArrayList<EventMention>();
		for(Node node_event_mention : nodes_event_mentions)
		{
			EventMention event_mention = new EventMention(node_event_mention, null, null);
			event_mentions.add(event_mention);
		}
		
		ret.num_event_mention += event_mentions.size();
		
		// check if two events appear in the same sentence
		for(int i=0; i<event_mentions.size(); i++)
		{
			EventMention event_mention = event_mentions.get(i);
			int num_Giver = 0;
			for(int j=0; j<event_mention.arguments.size(); j++)
			{
				Arg arg = event_mention.arguments.get(j);
				if(arg.role.equals("Person"))
				{
					num_Giver++;
				}
			}
			
			if(num_Giver > 3)
			{
				System.err.println("NumGiver==9");
			}
			
			
			for(int j=i+1; j<event_mentions.size(); j++)
			{
				EventMention event_mention_2 = event_mentions.get(j);
				
				// compare the extent of two event mentions
				for(Span sent : sents)
				{
					if(sent.overlap(event_mention.span))
					{
						if(sent.overlap(event_mention_2.span))
						{
							// two events appear in the same sentence
							out.print("1:");
							out.print("\t");
							out.print(apf_file.getName());
							out.print("\t");
							out.print(event_mention.text.replace("\n", " "));
							out.print("\t");
							out.print(event_mention);
							out.print("\t");
							out.print(event_mention_2);
							out.println();
							
							ret.num_type_1++;
							ret.addRolePairType1(event_mention.type, event_mention_2.type);
							if(event_mention.trigger.equalsIgnoreCase(event_mention_2.trigger))
							{
								System.err.println(apf_file.getName());
								System.err.println("ALTERT: Same tokens " + event_mention.trigger);
							}
						}
						else
						{
							break;
						}
					}
				}
			}
		}
		
		// check if one entity mention is arguments for two events
		for(int i=0; i<event_mentions.size(); i++)
		{
			EventMention event_mention = event_mentions.get(i);
			for(Arg arg : event_mention.arguments)
			{
				for(int j=i+1; j<event_mentions.size(); j++)
				{
					EventMention event_mention_2 = event_mentions.get(j);
					
					// compare two arguments 
					for(Arg arg_2 : event_mention_2.arguments)
					{
						if(arg.mention_id.equals(arg_2.mention_id))
						{
							// two events appear in the same sentence
							out.print("2:");
							out.print("\t");
							out.print(apf_file.getName());
							out.print("\t");
							out.println(event_mention.type + "_" + arg.role + ", " + event_mention_2.type + "_" + arg_2.role);
							out.print("\t");
							out.print(event_mention.text.replace("\n", " "));
							out.print("\t");
							out.print(event_mention);
							out.print("\t");
							out.print(event_mention_2);
							out.println();
							
							ret.num_type_2++;
							ret.addRolePairType2(arg.role, arg_2.role);
							ret.addEventRolePairType2(event_mention.type + "_" + arg.role, event_mention_2.type + "_" + arg_2.role);
						}
					}
				}
			}
		}
		
		// check if one entity is arguments for two events
		for(int i=0; i<event_mentions.size(); i++)
		{
			EventMention event_mention = event_mentions.get(i);
			for(Arg arg : event_mention.arguments)
			{
				for(int j=i+1; j<event_mentions.size(); j++)
				{
					EventMention event_mention_2 = event_mentions.get(j);
					// compare two arguments 
					for(Arg arg_2 : event_mention_2.arguments)
					{
						if(arg.entity_id.equals(arg_2.entity_id))
						{
							// two events appear in the same sentence
							out.print("3:");
							out.print("\t");
							out.print(apf_file.getName());
							out.println("\t");
							out.println(event_mention.type + "_" + arg.role + ", " + event_mention_2.type + "_" + arg_2.role);
							out.print("\t");
							out.print(event_mention.text.replace("\n", " "));
							out.print("\t");
							out.print(event_mention);
							out.print("\t");
							out.print(event_mention_2.text.replace("\n", " "));
							out.print("\t");
							out.print(event_mention_2);
							out.println();
							
							ret.num_type_3++;
							ret.addRolePairType3(arg.role, arg_2.role);
							ret.addEventRolePairType3(event_mention.type + "_" + arg.role, event_mention_2.type + "_" + arg_2.role);
						}
					}
				}
			}
		}
	}
	
	/**
	 * print a ranked list of the frequency map
	 * @param mapType_1Freq
	 * @param out
	 */
	private static void printRankedList(Map<RolePair, Integer> map, PrintStream out)
	{
		Set<Entry<RolePair, Integer>> set = map.entrySet();
		List<Entry<RolePair, Integer>> list = new ArrayList<Entry<RolePair, Integer>>(set);
		Collections.sort(list, new Comparator<Entry<RolePair, Integer>>()
				{
					@Override
					public int compare(Entry<RolePair, Integer> arg0, Entry<RolePair, Integer> arg1)
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
		for(Entry<RolePair, Integer> entry : list)
		{
			if(count-- < 0)
			{
				break;
			}
			out.println("" + entry.getKey().role_1 + "\t" + entry.getKey().role_2 + "\t" + entry.getValue());
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
		out.println("----------------------------------------------");
		out.println("1 : Two event mentions appear in the same sentence");
		out.println("2 : One entity mention is arguments for two events");
		out.println("3 : One entity is arguments for two events");
		out.println("----------------------------------------------");
		
		Stats stats = new Stats();
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		while((line = reader.readLine()) != null)
		{
			File apf_file = new File(srcDir + File.separator + line + ".apf.xml");
			File inline_file = new File(srcDir + File.separator + line + ".sgm.iapf");
			doAnalysisForFile(apf_file, inline_file, stats, out);
		}
		
		out.println("\n\n");
		out.println("----------------------------------------------");
		out.println("Total num of event mentions: " + stats.num_event_mention);
		out.println("Total num of type 1: " + stats.num_type_1);
		out.println("Total num of type 2: " + stats.num_type_2);
		out.println("Total num of type 3: " + stats.num_type_3);
		
		// output the freq of different values in type1, type2, type3
		out.println("\n\n");
		out.println("-----------------------------------------------");
		out.println("pair of event types");
		printRankedList(stats.map_type_1_freq, out);
		out.println("\n\n");
		out.println("-----------------------------------------------");
		out.println("pair of roles of the same mention");
		printRankedList(stats.map_type_2_freq, out);
		out.println("\n\n");
		out.println("-----------------------------------------------");
		out.println("pair of roles of the same entity");
		printRankedList(stats.map_type_3_freq, out);
		out.println("\n\n");
		out.println("-----------------------------------------------");
		out.println("pair of event_role of the same mention");
		printRankedList(stats.map_event_role_type_2_freq, out);
		out.println("\n\n");
		out.println("-----------------------------------------------");
		out.println("pair of event_role of the same entity");
		printRankedList(stats.map_event_role_type_3_freq, out);
		
		reader.close();
	}

	static public void main(String[] args) throws DocumentException, IOException
	{
		File srcDir = new File("/Users/che/Data/ACE/ACE2005-TrainingData-V6.0/English/");
		File file_list = new File("/Users/che/Data/ACE/filelist_ACE_nw_test");
		
		PrintStream out = new PrintStream("/Users/che/event_analysis_2");
		doAnalysis(srcDir, file_list, out);
	
		out.close();
	}
}
