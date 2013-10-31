package edu.cuny.qc.ace.acetypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;
import edu.cuny.qc.util.Span;

/**
 * check the Identification scores
 * @author XX
 *
 */
public class Scorer
{
	public static class Stats
	{
		public double num_trigger_ans = 0.0;
		public double num_trigger_gold = 0.0;
		public double num_trigger_correct = 0.0;
		
		public double num_arg_ans = 0.0;
		public double num_arg_gold = 0.0;
		public double num_arg_correct = 0.0;
		
		public double prec_trigger = 0.0;
		public double recall_trigger = 0.0;
		public double f1_trigger = 0.0;
		
		public double prec_arg = 0.0;
		public double recall_arg = 0.0;
		public double f1_arg = 0.0;
		
		// for identification scoring
		public double num_trigger_idt_correct = 0.0;
		public double num_arg_idt_correct = 0.0;
		
		public double prec_trigger_idt = 0.0;
		public double recall_trigger_idt = 0.0;
		public double f1_trigger_idt = 0.0;
	
		public double prec_arg_idt = 0.0;
		public double recall_arg_idt = 0.0;
		public double f1_arg_idt = 0.0;
		
		/**
		 * calculate the performance
		 */
		public void calc()
		{
			prec_trigger = num_trigger_correct / num_trigger_ans;
			recall_trigger = num_trigger_correct / num_trigger_gold;
			f1_trigger = 2 * (prec_trigger * recall_trigger) / (prec_trigger + recall_trigger);
			
			prec_arg = num_arg_correct / num_arg_ans;
			recall_arg = num_arg_correct / num_arg_gold;
			f1_arg = 2 * (prec_arg * recall_arg) / (prec_arg + recall_arg);
			
			// for identification scoring
			prec_trigger_idt = num_trigger_idt_correct / num_trigger_ans;
			recall_trigger_idt = num_trigger_idt_correct / num_trigger_gold;
			f1_trigger_idt = 2 * (prec_trigger_idt * recall_trigger_idt) / (prec_trigger_idt + recall_trigger_idt);
			
			prec_arg_idt = num_arg_idt_correct / num_arg_ans;
			recall_arg_idt = num_arg_idt_correct / num_arg_gold;
			f1_arg_idt = 2 * (prec_arg_idt * recall_arg_idt) / (prec_arg_idt + recall_arg_idt);
		}
		
		@Override
		public String toString()
		{
			String ret = String.format("Trigger:\tF1\t%.3f\tPrec\t%.3f\tRecall\t%.3f\tArg:\tF1\t%.3f\tPrec\t%.3f\tRecall\t%.3f\t",
					f1_trigger, prec_trigger, recall_trigger, f1_arg, prec_arg, recall_arg);
			ret += "\n Identification:\n";
			ret += String.format("Trigger:\tF1\t%.3f\tPrec\t%.3f\tRecall\t%.3f\tArg:\tF1\t%.3f\tPrec\t%.3f\tRecall\t%.3f\t",
					f1_trigger_idt, prec_trigger_idt, recall_trigger_idt, f1_arg_idt, prec_arg_idt, recall_arg_idt);
			return ret;
		}
	}
	
	public static void doAnalysis(File goldDir, File ansDir, File file_list, PrintStream out) throws IOException, DocumentException
	{
		Stats stats = new Stats();
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		while((line = reader.readLine()) != null)
		{
			File apf_ans = new File(ansDir + File.separator + line + ".sgm.apf");
			if(!apf_ans.exists())
			{
				apf_ans = new File(ansDir + File.separator + line);
			}
			if(!apf_ans.exists())
			{
				apf_ans = new File(ansDir + File.separator + line + ".apf.xml");
			}
			int idx = line.indexOf("/");
			String new_line = line.substring(0, idx+1) + "timex2norm" + File.separator + line.substring(idx+1);
			File apf_gold = new File(goldDir + File.separator + new_line + ".apf.xml");
			File textFile = new File(goldDir + File.separator + new_line + ".sgm");
			if(!apf_gold.exists())
			{
				apf_gold = new File(goldDir + File.separator + line + ".apf.xml");
				textFile = new File(goldDir + File.separator + line + ".sgm");
			}
			AceDocument doc_ans = new AceDocument(textFile.getAbsolutePath(), apf_ans.getAbsolutePath());
			AceDocument doc_gold = new AceDocument(textFile.getAbsolutePath(), apf_gold.getAbsolutePath());
			doAnalysisForFile(doc_ans, doc_gold, stats);
		}
		
		stats.calc();
		
		out.println("\n\n---------------------------");
		out.println("Summary:\n");
		out.println(stats);
	}
	
	private static void doAnalysisForFile(AceDocument doc_ans, AceDocument doc_gold, Stats stats)
	{
		List<AceEventMention> mentions_ans = doc_ans.eventMentions;
		List<AceEventMention> mentions_gold = doc_gold.eventMentions;
		
		evaluate(stats, mentions_ans, mentions_gold);
	}

	public static void evaluate(Stats stats,
			List<AceEventMention> mentions_ans,
			List<AceEventMention> mentions_gold)
	{
		// evalute triggers
		stats.num_trigger_ans += mentions_ans.size();
		stats.num_trigger_gold += mentions_gold.size();
		for(AceEventMention mention : mentions_ans)
		{
			for(AceEventMention mention_gold : mentions_gold)
			{
				if(mention.anchorExtent.overlap(mention_gold.anchorExtent))
				{
					if(mention.getSubType().equals(mention_gold.getSubType()))
					{
						stats.num_trigger_correct++;
					}
					
					// for identification scoring
					stats.num_trigger_idt_correct++;
					
					break;
				}
			}
		}
		
		// evalute arguments
		List<AceEventMentionArgument> args_ans = getArguments(mentions_ans);
		List<AceEventMentionArgument> args_gold = getArguments(mentions_gold);
		stats.num_arg_ans += args_ans.size();
		stats.num_arg_gold += args_gold.size();
		
		// check number of correct
		for(AceEventMentionArgument arg : args_ans)
		{
			for(AceEventMentionArgument temp : args_gold)
			{
				if(arg.role.equals(temp.role) && arg.mention.getSubType().equals(temp.mention.getSubType()))
				{
					if(headCorrrect(arg.value, temp.value))
					{
						stats.num_arg_correct++;
						break;
					}
				}
				
			}
		}
		
		// check number of correct in terms of identification
		for(AceEventMentionArgument arg : args_ans)
		{
			for(AceEventMentionArgument temp : args_gold)
			{
				if(arg.mention.getSubType().equals(temp.mention.getSubType()))
				{
					if(headCorrrect(arg.value, temp.value))
					{
						stats.num_arg_idt_correct++;
						break;
					}
				}
				
			}
		}
	}
	
	/**
	 * check if two entity are same coreference
	 * @param value
	 * @param value2
	 * @return
	 */
	private static boolean headCorrrect(AceMention value, AceMention value2)
	{
		if(value == null || value2 == null)
		{
			return false;
		}
		Span head = value.extent;
		Span head2 = value2.extent;
		if(value instanceof AceEntityMention)
		{
			head = ((AceEntityMention) value).head;
		}
		if(value2 instanceof AceEntityMention)
		{
			head2 = ((AceEntityMention) value2).head;
		}
		
		if(head.equals(head2))
		{
			return true;
		}
		
		if(value2.getParent() instanceof AceEntity)
		{
			AceEntity parent2 = (AceEntity) value2.getParent();
			for(AceMention coref : parent2.mentions)
			{
				if(coref instanceof AceEntityMention)
				{
					head2 = ((AceEntityMention) coref).head;
				}
				else
				{
					head2 = coref.extent;
				}
				if(head.equals(head2))
				{
					return true;
				}
			}
		}
		return false;
	}

	private static List<AceEventMentionArgument> getArguments(List<AceEventMention> mentions)
	{
		List<AceEventMentionArgument> ret = new ArrayList<AceEventMentionArgument>();
		for(AceEventMention mention : mentions)
		{		
			ret.addAll(mention.arguments);
		}
		return ret;
	}

	static public void main(String[] args) throws DocumentException, IOException
	{	
		if(args.length < 3)
		{
			System.out.println("Automatic error analysis Usage:");
			System.out.println("args[0]: gold Dir");
			System.out.println("args[1]: ans Dir");
			System.out.println("args[2]: file list");
			System.out.println("args[3]: output file");
			System.exit(-1);
		}
		
		File goldDir = new File(args[0]);
		File ansDir = new File(args[1]);
		File filelist = new File(args[2]);
		
		PrintStream out = System.out;
		if(args.length >= 4)
		{
			File output = new File(args[3]);
			out = new PrintStream(output);
		}
		doAnalysis(goldDir, ansDir, filelist, out);
		if(out != System.out)
		{
			out.close();
		}
	}
}
