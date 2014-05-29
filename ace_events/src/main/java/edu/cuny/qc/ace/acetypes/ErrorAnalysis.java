package edu.cuny.qc.ace.acetypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Node;

import edu.cuny.qc.ace.analysis.forEvent.Analysis.Arg;
import edu.cuny.qc.ace.analysis.forEvent.Analysis.EventMention;
import edu.cuny.qc.ace.analysis.forEvent.ApfReader;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.util.Span;

/**
 * analysis the errors in ACE cross-validation
 * print to html
 * @author che
 *
 */
public class ErrorAnalysis
{
	static String htmlHead = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body><div>";
	static String htmlTail = "</div></body></html>"; 
	static String htmlBar = "<br><hr><br>";
	
	static public List<EventMention> getEventMentions(File apf_file, List<Span> sentences, String doc_text) throws DocumentException
	{
		ApfReader apfReader_apf = new ApfReader(apf_file.getAbsolutePath());
		List<Node> nodes_event_mentions = apfReader_apf.getEventMentions();
		
		List<EventMention> event_mentions = new ArrayList<EventMention>();
		for(Node node_event_mention : nodes_event_mentions)
		{
			EventMention event_mention = new EventMention(node_event_mention, sentences, doc_text);
			event_mentions.add(event_mention);
		}
		
		return event_mentions;
	}
	
	static void doAnalysisForFile(File textFile, File apf_ans, File apf_gold, Stats stats, PrintStream out,
			PrintStream out2, PrintStream out3, PrintStream out4, PrintStream out5) throws DocumentException, IOException
	{
		Document doc = new Document(textFile.getAbsolutePath().replace(".sgm", ""));
		List<Span> sentences = new ArrayList<Span>();
		for(Sentence sent : doc.getSentences())
		{
			sentences.add(sent.getExtent());
		}
		String doc_text = doc.allText;
		
		List<EventMention> mentions_gold = getEventMentions(apf_gold, sentences, doc_text);
		List<EventMention> mentions_ans = getEventMentions(apf_ans, sentences, doc_text);
		
		stats.num_empty_event_ans += getNumberOfEmptyEvents(mentions_ans);
		stats.num_empty_event_gold += getNumberOfEmptyEvents(mentions_gold);
		
		stats.num_missing_place_events_ans += getNumberOfMissingPlace(mentions_ans);
		stats.num_missing_place_events_gold += getNumberOfMissingPlace(mentions_gold);
		
		stats.num_event_gold += mentions_gold.size();
		stats.num_event_ans += mentions_ans.size();
		
		for(EventMention mention_gold : mentions_gold)
		{
			boolean event_mention_missing = true;
			stats.num_arg_gold += mention_gold.arguments.size();
			
			for(EventMention mention_ans : mentions_ans)
			{
				if(mention_ans.span.overlap(mention_gold.span) && mention_ans.trigger.equals(mention_gold.trigger))
				{
					event_mention_missing = false;
					// the scope of trigger matches
					if(mention_gold.type.equals(mention_ans.type))
					{
						// the type of two mentions match
						for(Arg arg_gold : mention_gold.arguments)
						{
							boolean arg_missing = true;
							for(Arg arg_ans : mention_ans.arguments)
							{
								if(arg_ans.arg_head.equals(arg_gold.arg_head))
								{
									arg_missing = false;
									// the head of argument matches
									if(arg_ans.role.equals(arg_gold.role))
									{
										// the role of argument matches
									}
									else
									{
										out.print("<br><br>");
										out.println("<b>argument role error</b>" + "\t" + mention_gold + "\t" + "<font color=\"red\">" + arg_ans.toString() + "</font>" 
												+ "\t" + "<font color=\"blue\">" + mention_gold.text.replace('\n',' ') + "</font>");
										stats.num_arg_role_error++;
									}
									break;
								}
							}
							if(arg_missing)
							{
								out2.print("<br><br>");
								out2.println("<b>argument missing</b>" + "\t" + mention_gold + "\t" + "<font color=\"red\">" + 
										arg_gold.toString() + "</font>" + "\t" + "<font color=\"blue\">" + mention_gold.text.replace('\n',' ') + "</font>");
								stats.num_arg_missing++;
							}
						}
						
						// check if there is false positive argument
						for(Arg arg_ans : mention_ans.arguments)
						{
							boolean arg_false_positive = true;
							for(Arg arg_gold : mention_gold.arguments)
							{
								if(arg_ans.arg_head.equals(arg_gold.arg_head))
								{
									arg_false_positive = false;
									break;
								}
							}
							if(arg_false_positive)
							{
								out3.print("<br><br>");
								out3.println("<b>argument false positive</b>" + "\t" + mention_gold + "\t" + 
										"<font color=\"red\">" + arg_ans.toString() + "</font>" + "\t" + "<font color=\"blue\">" + mention_gold.text.replace('\n',' ') + "</font>");
								stats.num_arg_false_positive++;
							}
						}
					}
					else
					{
						out4.print("<br><br>");
						out4.println("<b>event type error</b>" + "\t" + mention_gold.toString() + "\t" + "<font color=\"red\">" + mention_ans.toString() + "</font>"+ 
								"\t" + "\t" + "<font color=\"blue\">" + mention_gold.text.replace('\n',' ') + "</font>");
						stats.num_event_type_error++;
					}
					break;
				}
			}
			
			if(event_mention_missing)
			{
				out5.print("<br><br>");
				out5.println("<b>event mention missing</b>" + "\t" + mention_gold.toString() + "\t" + "<font color=\"blue\">" + mention_gold.text + "</font>");
				stats.num_event_missing++;
			}
		}
		
		for(EventMention mention_ans : mentions_ans)
		{
			stats.num_arg_ans += mention_ans.arguments.size();
			
			boolean event_mention_false_positive = true;
			for(EventMention mention_gold : mentions_gold)
			{
				if(mention_ans.span.overlap(mention_gold.span) && mention_ans.trigger.equals(mention_gold.trigger))
				{
					event_mention_false_positive = false;
					break;
				}
			}
			if(event_mention_false_positive)
			{
				out.print("<br><br>");
				out.println("<b>event mention_false positive</b>" + "\t" + "<font color=\"red\">" + mention_ans + "</font>" + "\t" + "<font color=\"blue\">" + mention_ans.text + "</font>");
				stats.num_event_false_positive++;
			}
		}
	}
	
	private static int getNumberOfMissingPlace(List<EventMention> mentions)
	{
		int ret = 0;
		for(EventMention mention : mentions)
		{
			if(mention.arguments.size() == 0)
			{
				ret++;
			}
			else
			{
				boolean flag = false;
				for(Arg arg : mention.arguments)
				{
					if(arg.role.equalsIgnoreCase("Place"))
					{
						flag = true;
						break;
					}
				}
				if(flag == false)
				{
					ret++;
				}
			}
		}
		return ret;
	}

	/**
	 * given a list of mentions, find number of event mentions that don't have any arguments
	 * @param mentions_ans
	 * @return
	 */
	private static int getNumberOfEmptyEvents(List<EventMention> mentions)
	{
		int ret = 0;
		for(EventMention mention : mentions)
		{
			if(mention.arguments.size() == 0)
			{
				ret++;
			}
		}
		return ret;
	}

	public static void doAnalysis(File goldDir, File ansDir, File file_list, PrintStream out,
			PrintStream out2, PrintStream out3, PrintStream out4, PrintStream out5) throws IOException, DocumentException
	{
		Stats stats = new Stats();
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		
		out.println(htmlHead);
		out2.println(htmlHead);
		out3.println(htmlHead);
		out4.println(htmlHead);
		out5.println(htmlHead);
		while((line = reader.readLine()) != null)
		{
			out.println("<br><br>");
			out2.println("<br><br>");
			out3.println("<br><br>");
			out4.println("<br><br>");
			out5.println("<br><br>");
			out.println("\nDocument: + " + line + "\n");
			out2.println("\nDocument: + " + line + "\n");
			out3.println("\nDocument: + " + line + "\n");
			out4.println("\nDocument: + " + line + "\n");
			out5.println("\nDocument: + " + line + "\n");
			out.println(htmlBar);
			out2.println(htmlBar);
			out3.println(htmlBar);
			out4.println(htmlBar);
			out5.println(htmlBar);
			
			File apf_ans = new File(ansDir + File.separator + line + ".sgm.apf");
			if(!apf_ans.exists())
			{
				apf_ans = new File(ansDir + File.separator + line);
			}
			int idx = line.indexOf("/");
			String new_line = line.substring(0, idx+1) + "timex2norm" + File.separator + line.substring(idx+1);
			File apf_gold = new File(goldDir + File.separator + new_line + ".apf.xml");
			File text_file = new File(goldDir + File.separator + new_line + ".sgm");
			if(!apf_gold.exists())
			{
				apf_gold = new File(goldDir + File.separator + line + ".apf.xml");
				text_file = new File(goldDir + File.separator + line + ".sgm");
			}
			doAnalysisForFile(text_file, apf_ans, apf_gold, stats, 
					out, out2, out3, out4, out5);
		}
		
		out.println("\n\n---------------------------");
		out2.println("\n\n---------------------------");
		out3.println("\n\n---------------------------");
		out4.println("\n\n---------------------------");
		out5.println("\n\n---------------------------");
		
		out.println("Summary:\n");
		out2.println("Summary:\n");
		out3.println("Summary:\n");
		out4.println("Summary:\n");
		out5.println("Summary:\n");
		
		out.println(stats);
		out2.println(stats);
		out3.println(stats);
		out4.println(stats);
		out5.println(stats);
		
		out.println(htmlTail);
		out2.println(htmlTail);
		out3.println(htmlTail);
		out4.println(htmlTail);
		out5.println(htmlTail);	
	}
	
	/**
	 * the statistics about different errors
	 * @author che
	 *
	 */
	public static class Stats
	{
		// count number of empty event
		int num_missing_place_events_ans = 0;
		int num_missing_place_events_gold = 0;
		int num_empty_event_ans = 0;
		int num_empty_event_gold = 0;
		int num_event_gold = 0;
		int num_arg_gold = 0;
		int num_event_ans = 0;
		int num_arg_ans = 0;
		int num_event_type_error = 0;
		int num_event_false_positive = 0;
		int num_event_missing = 0;
		int num_arg_missing = 0;
		int num_arg_false_positive = 0;
		int num_arg_role_error = 0;
		
		public String toString()
		{
			String ret = "";
			ret = String.format("num_event: %d \t num_arg: %d \t num_event_ans: %d \t num_arg_ans %d \n num_event_type_error: %d \t num_event_false_positive: %d \t num_event_missing: %d" +
					"\n num_arg_missing: %d \t num_arg_false_positive: %d \t num_arg_role_error: %d  \n num_empty_events_ans: %d \t num_empty_events_gold: %d", num_event_gold, num_arg_gold, num_event_ans, num_arg_ans, num_event_type_error, num_event_false_positive, 
					num_event_missing, num_arg_missing, num_arg_false_positive, num_arg_role_error, num_empty_event_ans, num_empty_event_gold);
			ret += String.format("\n missing_place_ans: %d \t missing_place_gold %d", num_missing_place_events_ans, num_missing_place_events_gold);
			return ret;
		}
	}
	
	static public void main(String[] args) throws DocumentException, IOException
	{	
		if(args.length < 4)
		{
			System.out.println("Automatic error analysis Usage:");
			System.out.println("args[0]: gold Dir");
			System.out.println("args[1]: ans Dir");
			System.out.println("args[2]: file list");
			System.out.println("args[3]: output filename");
			System.exit(-1);
		}
		
		File goldDir = new File(args[0]);
		File ansDir = new File(args[1]);
		File filelist = new File(args[2]);		
		PrintStream out = new PrintStream(new File(args[3]) + "1.html");
		PrintStream out2 = new PrintStream(new File(args[3]) + "2.html");
		PrintStream out3 = new PrintStream(new File(args[3]) + "3.html");
		PrintStream out4 = new PrintStream(new File(args[3]) + "4.html");
		PrintStream out5 = new PrintStream(new File(args[3]) + "5.html");
		
		doAnalysis(goldDir, ansDir, filelist, out, out2, out3, out4, out5);
		
		out.close();
		out2.close();
		out3.close();
		out3.close();
		out4.close();
		out5.close();
	}
}
