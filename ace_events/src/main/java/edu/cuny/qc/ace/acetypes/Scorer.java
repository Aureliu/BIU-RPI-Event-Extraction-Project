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

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.resource.ResourceInitializationException;
import org.dom4j.DocumentException;

import ac.biu.nlp.nlp.ie.onthefly.input.AeException;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecHandler;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;
import edu.cuny.qc.util.Span;
import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;

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
	
	public static Stats doAnalysis(File goldDir, File ansDir, File file_list, TypesContainer types, PrintStream out, String dirNamePrefix) throws IOException, DocumentException
	{
		Stats stats = new Stats();
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		while((line = reader.readLine()) != null)
		{
			File apf_ans = new File(ansDir + "/" + dirNamePrefix + line + ".sgm.apf");
			if(!apf_ans.exists())
			{
				apf_ans = new File(ansDir + "/" + dirNamePrefix + line);
			}
			if(!apf_ans.exists())
			{
				apf_ans = new File(ansDir + "/" + dirNamePrefix + line + ".apf.xml");
			}
			int idx = line.indexOf("/");
			String new_line = line.substring(0, idx+1) + "timex2norm" + "/" + line.substring(idx+1);
			File apf_gold = new File(goldDir + "/" + new_line + ".apf.xml");
			File textFile = new File(goldDir + "/" + new_line + ".sgm");
			if(!apf_gold.exists())
			{
				apf_gold = new File(goldDir + "/" + line + ".apf.xml");
				textFile = new File(goldDir + "/" + line + ".sgm");
			}
			AceDocument doc_ans = new AceDocument(textFile.getAbsolutePath(), apf_ans.getAbsolutePath());
			AceDocument doc_gold = new AceDocument(textFile.getAbsolutePath(), apf_gold.getAbsolutePath());
			if (types.specs != null) {
				doc_gold.filterBySpecs(types);
			}
			// There no need to call setSingleEventType on doc_gold, as it already has just the single type (if indeed only one is required)

			out.printf("----------------\n%s\n", line);
			doAnalysisForFile(doc_ans, doc_gold, stats, out);
			out.printf("----------------\n\n");
		}
		
		stats.calc();
		
		out.println("\n\n---------------------------");
		out.printf("num_trigger_gold=%f, num_trigger_ans=%f  /  num_trigger_correct=%f, num_trigger_idt_correct=%f\n", stats.num_trigger_gold, stats.num_trigger_ans, stats.num_trigger_correct, stats.num_trigger_idt_correct);
		out.printf("num_arg_gold=%f, num_arg_ans=%f  /  num_arg_correct=%f, num_arg_idt_correct=%f\n", stats.num_arg_gold, stats.num_arg_ans, stats.num_arg_correct, stats.num_arg_idt_correct);
		out.println("\n---------------------------");
		out.println("Summary:\n");
		out.println(stats);
		
		return stats;
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
								minimizeMentionAndArgumentMentionID(temp.value.id), temp.mention.getSubType(), temp.role, normalizedRealHeadText(temp.value), realHead(temp.value).start(), realHead(temp.value).end(),
								equals,
								minimizeMentionAndArgumentMentionID(arg.value.id), arg.mention.getSubType(), arg.role, normalizedRealHeadText(arg.value), realHead(arg.value).start(), realHead(arg.value).end());
						
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
								minimizeMentionAndArgumentMentionID(temp.value.id), temp.mention.getSubType(), temp.role, normalizedRealHeadText(temp.value), realHead(temp.value).start(), realHead(temp.value).end(),
								equals,
								minimizeMentionAndArgumentMentionID(arg.value.id), arg.mention.getSubType(), arg.role, normalizedRealHeadText(arg.value), realHead(arg.value).start(), realHead(arg.value).end());

						stats.num_arg_idt_correct++;
						break;
					}
				}
				
			}
		}
	}
	
	private static String minimizeMentionAndArgumentMentionID(String longID) {
		Matcher m = Pattern.compile("-(\\w*\\d+\\-\\d+)$").matcher(longID);
		if (m.find()) {
			return m.group(1);
		}
		else {
			return longID;
		}
//		try {
//			return m.group(1);
//		}
//		catch (IllegalStateException e) {
//			throw e;
//		}
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
	
	private static String normalizedRealHeadText(AceMention value) {
		return realHeadText(value).replace('\n', ' ');
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

	static public Stats mainReturningStats(String[] args) throws DocumentException, IOException, CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, UimaUtilsException, AeException, CASException
	{
		if(args.length < 4)
		{
			System.out.println("Automatic error analysis Usage:");
			System.out.println("args[0]: gold Dir");
			System.out.println("args[1]: ans Dir");
			System.out.println("args[2]: file list");
			System.out.println("args[3]: spec list");
			System.out.println("optional args[4]: spec list");
			System.exit(-1);
		}

		String goldDir = args[0];
		String ansDir = args[1];
		String filelist = args[2];
		File specListFile = new File(args[3]);
		List<String> specXmlPaths = SpecHandler.readSpecListFile(specListFile);
		TypesContainer types = new TypesContainer(specXmlPaths, false);

		PrintStream out = null;
		if(args.length >= 5)
		{
			File output = new File(args[4]);
			out = new PrintStream(output);
		}
		return mainMultiRunReturningStats(goldDir, ansDir, filelist, types, out, "");
	}
	
	static public Stats mainMultiRunReturningStats(String goldDir, String ansDir, String filelist, TypesContainer types, PrintStream out, String dirNamePrefix) throws DocumentException, IOException
	{	
		System.err.println("??? Scorer: Nore that we are removing all non-spec events from gold, which is wrong - we need those for the Trigger Idt (I think... don't we?). Anyway, this should be well thoght-of.");
		
		if (out == null) {
			out = System.out;
		}
		
		Stats stats = doAnalysis(new File(goldDir), new File(ansDir), new File(filelist), types, out, dirNamePrefix);
		
		if(out != System.out)
		{
			out.close();
		}
		
		return stats;
	}
	
	static public void main(String[] args) throws DocumentException, IOException, CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, UimaUtilsException, AeException, CASException {
		mainReturningStats(args);
	}

}
