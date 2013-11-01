package edu.cuny.qc.ace.acetypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			out.printf("----------------\n%s\n", line);
			doAnalysisForFile(doc_ans, doc_gold, stats, out);
			out.printf("----------------\n\n");
		}
		
		stats.calc();
		
		out.println("\n\n---------------------------");
		out.println("Summary:\n");
		out.println(stats);
	}
	
	private static void doAnalysisForFile(AceDocument doc_ans, AceDocument doc_gold, Stats stats, PrintStream out)
	{
		List<AceEventMention> mentions_ans = doc_ans.eventMentions;
		List<AceEventMention> mentions_gold = doc_gold.eventMentions;
		
		evaluate(stats, mentions_ans, mentions_gold, out);
	}

	public static void evaluate(Stats stats,
			List<AceEventMention> mentions_ans,
			List<AceEventMention> mentions_gold,
			PrintStream out)
	{
		// evalute triggers
		stats.num_trigger_ans += mentions_ans.size();
		stats.num_trigger_gold += mentions_gold.size();
		out.printf("   Num Trigger Gold: %d, Num Trigger Ans: %d\n", mentions_gold.size(), mentions_ans.size());
		int i=0;
		for(AceEventMention mention : mentions_ans)
		{
			for(AceEventMention mention_gold : mentions_gold)
			{
				if(mention.anchorExtent.overlap(mention_gold.anchorExtent))
				{

					// for identification scoring
					stats.num_trigger_idt_correct++;
					i++;
					
					String equals = "!~!";
					if (mention.anchorExtent.equals(mention_gold.anchorExtent)) {
						equals = " = ";
					}
					
					out.printf("   %02d. Trigger Id: GOLD %-10s(%-18s):'%-14s'[%04d:%04d] %s ANS %-10s(%-18s):'%-14s'[%04d:%04d]", i, 
						minimizeMentionAndArgumentMentionID(mention_gold.id), mention_gold.getSubType(), mention_gold.anchorText, mention_gold.anchorExtent.start(), mention_gold.anchorExtent.end(),
						equals,
						minimizeMentionAndArgumentMentionID(mention.id), mention.getSubType(), mention.anchorText, mention.anchorExtent.start(), mention.anchorExtent.end());

					if(mention.getSubType().equals(mention_gold.getSubType()))
					{
						stats.num_trigger_correct++;
						out.printf("   + REGULAR");
					}
					else {
						out.printf("      (only id)");
					}
					
					out.printf("\n");
					
					break;
				}
			}
		}
		
		// evalute arguments
		List<AceEventMentionArgument> args_ans = getArguments(mentions_ans);
		List<AceEventMentionArgument> args_gold = getArguments(mentions_gold);
		stats.num_arg_ans += args_ans.size();
		stats.num_arg_gold += args_gold.size();
		out.printf("\n       Num Arg Gold: %d, Num Arg Ans: %d\n", args_gold.size(), args_ans.size());
		i=0;
		
		// check number of correct
		for(AceEventMentionArgument arg : args_ans)
		{
			for(AceEventMentionArgument temp : args_gold)
			{
				if(arg.role.equals(temp.role) && arg.mention.getSubType().equals(temp.mention.getSubType()))
				{
					if(headCorrrect(arg.value, temp.value))
					{
						i++;
						String equals = "!~!";
						if (realHead(arg.value).equals(realHead(temp.value))) {
							equals = " = ";
						}
						
						out.printf("       %02d. Arg Reg: GOLD %-10s(%-18s)\\%-12s:'%-22s'[%04d:%04d] %s ANS %-10s(%-18s)\\%-12s:'%-22s'[%04d:%04d]\n", i, 
								minimizeMentionAndArgumentMentionID(temp.value.id), temp.mention.getSubType(), temp.role, realHeadText(temp.value), realHead(temp.value).start(), realHead(temp.value).end(),
								equals,
								minimizeMentionAndArgumentMentionID(arg.value.id), arg.mention.getSubType(), arg.role, realHeadText(arg.value), realHead(arg.value).start(), realHead(arg.value).end());
						
						stats.num_arg_correct++;
						break;
					}
				}
				
			}
		}
		out.printf("\n");
		
		// check number of correct in terms of identification
		i=0;
		for(AceEventMentionArgument arg : args_ans)
		{
			for(AceEventMentionArgument temp : args_gold)
			{
				if(arg.mention.getSubType().equals(temp.mention.getSubType()))
				{
					if(headCorrrect(arg.value, temp.value))
					{
						i++;
						String equals = "!~!";
						if (realHead(arg.value).equals(realHead(temp.value))) {
							equals = " = ";
						}
						
						out.printf("           %02d. Arg Id: GOLD %-10s(%-18s)\\%-12s:'%-22s'[%04d:%04d] %s ANS %-10s(%-18s)\\%-12s:'%-22s'[%04d:%04d]\n", i, 
								minimizeMentionAndArgumentMentionID(temp.value.id), temp.mention.getSubType(), temp.role, realHeadText(temp.value), realHead(temp.value).start(), realHead(temp.value).end(),
								equals,
								minimizeMentionAndArgumentMentionID(arg.value.id), arg.mention.getSubType(), arg.role, realHeadText(arg.value), realHead(arg.value).start(), realHead(arg.value).end());

						stats.num_arg_idt_correct++;
						break;
					}
				}
				
			}
		}
	}
	
	private static String minimizeMentionAndArgumentMentionID(String longID) {
		Matcher m = Pattern.compile("-(\\w*\\d+\\-\\d+)$").matcher(longID);
		m.find();
		return m.group(1);
	}
	
	private static Span realHead(AceMention value) {
		if (value instanceof AceEntityMention)
		{
			return ((AceEntityMention) value).head;
		}
		else {
			return value.extent;
		}
	}
	
	private static String realHeadText(AceMention value) {
		if (value instanceof AceEntityMention)
		{
			return ((AceEntityMention) value).headText;
		}
		else {
			return value.text;
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
		Span head = realHead(value);
		Span head2 = realHead(value2);
		
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
