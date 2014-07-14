package edu.cuny.qc.perceptron.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;

import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SignalInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.util.InfoGainAndEntropy;
import edu.cuny.qc.util.InfoGainAndEntropy.InfoGainResult;
/**
 * this is a scorer for ACE event assignment
 * @author che
 *
 */
public class Evaluator
{	
	public static class Score
	{
		static {
			System.err.println("??? Evaluator.Score: args are disables. Harmonic mean actually just takes triggers. Undo these when args are back in fashion.");
		}
		
		public InfoGainResult trigger_info_gain = null;
		public double trigger_F1 = 0.0;
		public double trigger_precision = 0.0;
		public double trigger_recall = 0.0;
		
		public InfoGainResult arg_info_gain = null;
		public double arg_F1 = 0.0;
		public double arg_precision = 0.0;
		public double arg_recall = 0.0;
	
		public double count_trigger_total = 0;
		public double count_trigger_ans = 0;
		public double count_trigger_gold = 0;
		public double count_trigger_correct = 0;

		public double count_arg_total = 0;
		public double count_arg_ans = 0;
		public double count_arg_gold = 0;
		public double count_arg_correct = 0;
		
		
		public double trigger_F1_idt = 0.0;
		public double trigger_precision_idt = 0.0;
		public double trigger_recall_idt = 0.0;
		
		public double arg_F1_idt = 0.0;
		public double arg_precision_idt = 0.0;
		public double arg_recall_idt = 0.0;
	
		//public double count_trigger_ans_idt = 0;
		//public double count_trigger_gold_idt = 0;
		public double count_trigger_correct_idt = 0;

		public double count_arg_ans_idt = 0;
		public double count_arg_gold_idt = 0;
		public double count_arg_correct_idt = 0;
		
		
		// harmonic_mean of trigger F1 and argument F1
		public double harmonic_mean = 0.0;
		
		// calculate harmonic mean of trigger F1 and argument F1
		public void calculateHarmonic_mean()
		{
			harmonic_mean = 2 * trigger_F1 * arg_F1 / (trigger_F1 + arg_F1);
		}
		
		public String toString()
		{
			return String.format("Trigger: F1 %.3f Prec %.3f Recall %.3f  Arg: F1 %.3f Prec %.3f Recall %.3f  mean %.3f", 
					this.trigger_F1, this.trigger_precision, this.trigger_recall, 
					this.arg_F1, this.arg_precision, this.arg_recall, this.harmonic_mean);
		}
	}
	
	public Score evaluate(List<SentenceAssignment> results, List<SentenceInstance> instancesGold) {
		List<SentenceAssignment> goldTargets = new ArrayList<SentenceAssignment>(instancesGold.size());
		for (SentenceInstance inst : instancesGold) {
			goldTargets.add(inst.target);
		}
		
		return evaluate(results, goldTargets, false);
	}
	
	public Score evaluate(List<SentenceAssignment> results, List<SentenceAssignment> goldTargets, boolean fake)
	{
		Score score = new Score();
		evaluteTrigger(results, goldTargets, score);
		evaluteArgument(results, goldTargets, score);
		
		score.calculateHarmonic_mean();
		
		return score;
	}
	
	public void evaluteTrigger(List<SentenceAssignment> results, List<SentenceAssignment> goldTargets, Score score)
	{
		double count_trigger_total = 0;
		double count_trigger_ans = 0;
		double count_trigger_gold = 0;
		double count_trigger_correct = 0;
		double count_trigger_correct_idt = 0;
		
		/// DEBUG
//		Multimap<String, SignalInstance> positiveSignalsWithHistory = ArrayListMultimap.create();
//		Multimap<String, SignalInstance> positiveSignalsEmptyHistory = ArrayListMultimap.create();
//		int with=0, withHyp=0, empty=0, emptyHyp=0;
		////
		
		for(int i=0; i<results.size(); i++)
		{
			SentenceAssignment ans = results.get(i);
			//SentenceInstance goldInstance = instancesGold.get(i);
			SentenceAssignment gold = goldTargets.get(i);//goldInstance.target;
			// count num of gold args
			for(int j=0; j<gold.getNodeAssignment().size(); j++)
			{
				count_trigger_total++;
				String gold_trigger = gold.getLabelAtToken(j);
				if(!gold_trigger.equals(SentenceAssignment.Default_Trigger_Label))
				{
					count_trigger_gold++;
				}
			}
			// count num of ans args
			for(int j=0; j<ans.getNodeAssignment().size(); j++)
			{
				String ans_trigger = ans.getLabelAtToken(j);
				if(!ans_trigger.equals(SentenceAssignment.Default_Trigger_Label))
				{
					count_trigger_ans++;
				}
			}
			// count correct args
			for(int j=0; j<gold.getNodeAssignment().size(); j++)
			{
				/// DEBUG
//				String triggerLabel;
//				try {
//					triggerLabel = SpecAnnotator.getSpecLabel(goldInstance.associatedSpec);
//				} catch (Exception e) {
//					throw new RuntimeException(e);
//				}
//				List<Map<String, Map<ScorerData, SignalInstance>>> tokens = (List<Map<String, Map<ScorerData, SignalInstance>>>) goldInstance.get(InstanceAnnotations.NodeTextSignalsBySpec);
//				Map<String, Map<ScorerData, SignalInstance>> token = tokens.get(j);
//				Map<ScorerData, SignalInstance> scoredSignals = token.get(triggerLabel);
				///
				
				String gold_trigger = gold.getLabelAtToken(j);
				String ans_trigger;
				try {
					ans_trigger = ans.getLabelAtToken(j);
				} catch (Exception e) {
					System.err.printf("\n\ni=%s, j=%s, ans=%s, gold=%s, ans.size=%s, gold.size=%s\n\n",
							i, j, ans, gold, ans.getNodeAssignment().size(), gold.getNodeAssignment().size());
					throw new IllegalStateException(e);
				}
				//if(gold_trigger.equals(ans_trigger))
				if(!gold_trigger.equals(SentenceAssignment.Default_Trigger_Label) && !ans_trigger.equals(SentenceAssignment.Default_Trigger_Label))
				{
					/// DEBUG
//					for (SignalInstance signal : scoredSignals.values()) {
////						if (signal.name.startsWith("WN_HYPERNYM") && signal.positive) {
////							System.out.printf(" --- Correct match between text and spec on %s\n", signal.name);
////						}
//						if (signal.positive) {
//							if (signal.history.isEmpty()) {
//								positiveSignalsEmptyHistory.put(signal.name, signal);
//								empty++;
//								if (signal.name.startsWith("WN_HYPERNYM")) {
//									emptyHyp++;
//								}
//							}
//							else {
//								positiveSignalsWithHistory.put(signal.name, signal);
//								with++;
//								if (signal.name.startsWith("WN_HYPERNYM")) {
//									withHyp++;
//								}
//							}
//						}
//					}
					////
					count_trigger_correct_idt++;
					// trigger correct
					if(gold_trigger.equals(ans_trigger))
					{
						count_trigger_correct++;
					}
				}
			}
		}
		
		/// DEBUG
//		System.out.printf("empty=%d, emptyHyp=%d, with=%d, withHyp=%d\n", empty, emptyHyp, with, withHyp);
		///
		double prec;
		double prec_idt;
		if(count_trigger_ans == 0.0)
		{
			prec = 0.00f;
			prec_idt = 0.00f;
		}
		else
		{
			prec = count_trigger_correct / count_trigger_ans;
			prec_idt = count_trigger_correct_idt / count_trigger_ans;
		}
		double recall = count_trigger_correct / count_trigger_gold;
		double recall_idt = count_trigger_correct_idt / count_trigger_gold;
		double f_measure;
		double f_measure_idt;
		if(prec == 0.00f || recall == 0.00f)
		{
			f_measure = 0;
			f_measure_idt = 0;
		}
		else
		{
			f_measure = 2 * (prec * recall) / (prec + recall);
			f_measure_idt = 2 * (prec_idt * recall_idt) / (prec_idt + recall_idt);
		}
		
		score.count_trigger_total = count_trigger_total;
		score.count_trigger_ans = count_trigger_ans;
		score.count_trigger_gold = count_trigger_gold;
		score.count_trigger_correct = count_trigger_correct;
		score.count_trigger_correct_idt = count_trigger_correct_idt;

		score.trigger_precision = prec;
		score.trigger_precision_idt = prec_idt;
		score.trigger_recall = recall;
		score.trigger_recall_idt = recall_idt;
		score.trigger_F1 = f_measure;
		score.trigger_F1_idt = f_measure_idt;
		
		double count_trigger_ans_not_correct = count_trigger_ans - count_trigger_correct;
		score.trigger_info_gain = InfoGainAndEntropy.infoGain(
				count_trigger_correct,
				count_trigger_ans_not_correct,
				count_trigger_gold - count_trigger_correct,
				count_trigger_total - count_trigger_gold - count_trigger_ans_not_correct);
	}
	
	/**
	 * evaluate the performance of argument
	 * @param results
	 * @param instances
	 * @return
	 */
	public void evaluteArgument(List<SentenceAssignment> results, List<SentenceAssignment> goldTargets, Score score)
	{
		double count_arg_total = 0;
		double count_arg_ans = 0;
		double count_arg_gold = 0;
		double count_arg_correct = 0;
		double count_arg_correct_idt = 0;
		
		for(int i=0; i<results.size(); i++)
		{
			SentenceAssignment ans = results.get(i);
			//SentenceInstance goldInstance = instancesGold.get(i);
			SentenceAssignment gold = goldTargets.get(i);//goldInstance.target;
			// count num of gold args
			for(int j=0; j<gold.getNodeAssignment().size(); j++)
			{
				String gold_trigger = gold.getLabelAtToken(j);
				if(!gold_trigger.equals(SentenceAssignment.Default_Trigger_Label))
				{
					Map<Integer, Integer> gold_edges = gold.getEdgeAssignment().get(j);
					for(int key : gold_edges.keySet())
					{
						count_arg_total++;
						int value_gold = gold_edges.get(key);
						if(!gold.edgeTargetAlphabet.lookupObject(value_gold).equals(SentenceAssignment.Default_Argument_Label))
						{
							count_arg_gold++;
						}
					}
				}
			}
			// count num of ans args
			for(int j=0; j<ans.getNodeAssignment().size(); j++)
			{
				String ans_trigger = ans.getLabelAtToken(j);
				if(!ans_trigger.equals(SentenceAssignment.Default_Trigger_Label))
				{
					Map<Integer, Integer> ans_edges = ans.getEdgeAssignment().get(j);
					if(ans_edges != null)
					{
						for(int key : ans_edges.keySet())
						{
							int value_ans = ans_edges.get(key);
							if(!ans.edgeTargetAlphabet.lookupObject(value_ans).equals(SentenceAssignment.Default_Argument_Label))
							{
								count_arg_ans++;
							}
						}
					}
				}
			}
			// count correct args
			for(int j=0; j<gold.getNodeAssignment().size(); j++)
			{
				String gold_trigger = gold.getLabelAtToken(j);
				String ans_trigger = ans.getLabelAtToken(j);
				if(gold_trigger.equals(ans_trigger))
				{
					// trigger correct
					if(!gold_trigger.equals(SentenceAssignment.Default_Trigger_Label))
					{
						// traverse argument
						Map<Integer, Integer> ans_edges = ans.getEdgeAssignment().get(j);
						Map<Integer, Integer> gold_edges = gold.getEdgeAssignment().get(j);
						if(ans_edges != null && gold_edges != null)
						{
							for(int key : ans_edges.keySet())
							{
								int value_ans = ans_edges.get(key);
								int value_gold = gold_edges.get(key);
								
								if( !gold.edgeTargetAlphabet.lookupObject(value_ans).equals(SentenceAssignment.Default_Argument_Label) &&
									!gold.edgeTargetAlphabet.lookupObject(value_gold).equals(SentenceAssignment.Default_Argument_Label)) {
									
									count_arg_correct_idt++;
									
									if(value_ans == value_gold) {
										count_arg_correct++;
									}
								}
							}
						}
					}
				}
			}
		}
		
		double prec;
		double prec_idt;
		if(count_arg_ans == 0.0)
		{
			prec = 0.00f;
			prec_idt = 0.00f;
		}
		else
		{
			prec = count_arg_correct / count_arg_ans;
			prec_idt = count_arg_correct_idt / count_arg_ans;
		}
		double recall = count_arg_correct / count_arg_gold;
		double recall_idt = count_arg_correct_idt / count_arg_gold;
		double f_measure;
		double f_measure_idt;
		if(prec == 0.00f || recall == 0.00f)
		{
			f_measure = 0;
			f_measure_idt = 0;
		}
		else
		{
			f_measure = 2 * (prec * recall) / (prec + recall);
			f_measure_idt = 2 * (prec_idt * recall_idt) / (prec_idt + recall_idt);
		}
		
		score.count_arg_total = count_arg_total;
		score.count_arg_ans = count_arg_ans;
		score.count_arg_gold = count_arg_gold;
		score.count_arg_correct = count_arg_correct;
		score.count_arg_correct_idt = count_arg_correct_idt;

		score.arg_precision = prec;
		score.arg_precision_idt = prec_idt;
		score.arg_recall = recall;
		score.arg_recall_idt = recall_idt;
		score.arg_F1 = f_measure;
		score.arg_F1_idt = f_measure_idt;
		
		double count_arg_ans_not_correct = count_arg_ans - count_arg_correct;
		score.arg_info_gain = InfoGainAndEntropy.infoGain(
				count_arg_correct,
				count_arg_ans_not_correct,
				count_arg_gold - count_arg_correct,
				count_arg_total - count_arg_gold - count_arg_ans_not_correct);

	}
}
