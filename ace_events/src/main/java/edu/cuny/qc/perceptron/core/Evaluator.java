package edu.cuny.qc.perceptron.core;

import java.util.List;
import java.util.Map;

import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;

/**
 * this is a scorer for ACE event assignment
 * @author che
 *
 */
public class Evaluator
{	
	public static class Score
	{
		public double trigger_F1 = 0.0;
		public double trigger_precision = 0.0;
		public double trigger_recall = 0.0;
		
		public double arg_F1 = 0.0;
		public double arg_precision = 0.0;
		public double arg_recall = 0.0;
	
		public double count_trigger_ans = 0;
		public double count_trigger_gold = 0;
		public double count_trigger_correct = 0;

		public double count_arg_ans = 0;
		public double count_arg_gold = 0;
		public double count_arg_correct = 0;
		
		
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
	
	public Score evaluate(List<SentenceAssignment> results, List<SentenceInstance> instancesGold)
	{
		Score score = new Score();
		evaluteTrigger(results, instancesGold, score);
		evaluteArgument(results, instancesGold, score);
		
		score.calculateHarmonic_mean();
		
		return score;
	}
	
	public void evaluteTrigger(List<SentenceAssignment> results, List<SentenceInstance> instancesGold, Score score)
	{
		double count_trigger_ans = 0;
		double count_trigger_gold = 0;
		double count_trigger_correct = 0;
		
		for(int i=0; i<results.size(); i++)
		{
			SentenceAssignment ans = results.get(i);
			SentenceInstance goldInstance = instancesGold.get(i);
			SentenceAssignment gold = goldInstance.target;
			// count num of gold args
			for(int j=0; j<gold.getNodeAssignment().size(); j++)
			{
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
				String gold_trigger = gold.getLabelAtToken(j);
				String ans_trigger = ans.getLabelAtToken(j);
				if(gold_trigger.equals(ans_trigger))
				{
					// trigger correct
					if(!gold_trigger.equals(SentenceAssignment.Default_Trigger_Label))
					{
						count_trigger_correct++;
					}
				}
			}
		}
		
		double prec;
		if(count_trigger_ans == 0.0)
		{
			prec = 0.00f;
		}
		else
		{
			prec = count_trigger_correct / count_trigger_ans;
		}
		double recall = count_trigger_correct / count_trigger_gold;
		double f_measure;
		if(prec == 0.00f || recall == 0.00f)
		{
			f_measure = 0;
		}
		else
		{
			f_measure = 2 * (prec * recall) / (prec + recall);
		}
		
		score.count_trigger_ans = count_trigger_ans;
		score.count_trigger_gold = count_trigger_gold;
		score.count_trigger_correct = count_trigger_correct;

		score.trigger_precision = prec;
		score.trigger_recall = recall;
		score.trigger_F1 = f_measure;
	}
	
	/**
	 * evaluate the performance of argument
	 * @param results
	 * @param instances
	 * @return
	 */
	public void evaluteArgument(List<SentenceAssignment> results, List<SentenceInstance> instancesGold, Score score)
	{
		double count_arg_ans = 0;
		double count_arg_gold = 0;
		double count_arg_correct = 0;
		
		for(int i=0; i<results.size(); i++)
		{
			SentenceAssignment ans = results.get(i);
			SentenceInstance goldInstance = instancesGold.get(i);
			SentenceAssignment gold = goldInstance.target;
			// count num of gold args
			for(int j=0; j<gold.getNodeAssignment().size(); j++)
			{
				String gold_trigger = gold.getLabelAtToken(j);
				if(!gold_trigger.equals(SentenceAssignment.Default_Trigger_Label))
				{
					Map<Integer, Integer> gold_edges = gold.getEdgeAssignment().get(j);
					for(int key : gold_edges.keySet())
					{
						int value_gold = gold_edges.get(key);
						if(!goldInstance.edgeTargetAlphabet.lookupObject(value_gold).equals(SentenceAssignment.Default_Argument_Label))
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
								if(value_ans == value_gold)
								{
									// arg correct
									if(!goldInstance.edgeTargetAlphabet.lookupObject(value_ans).equals(SentenceAssignment.Default_Argument_Label))
									{
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
		if(count_arg_ans == 0.0)
		{
			prec = 0.00f;
		}
		else
		{
			prec = count_arg_correct / count_arg_ans;
		}
		double recall = count_arg_correct / count_arg_gold;
		double f_measure;
		if(prec == 0.00f || recall == 0.00f)
		{
			f_measure = 0;
		}
		else
		{
			f_measure = 2 * (prec * recall) / (prec + recall);
		}
		
		score.count_arg_ans = count_arg_ans;
		score.count_arg_gold = count_arg_gold;
		score.count_arg_correct = count_arg_correct;

		score.arg_precision = prec;
		score.arg_recall = recall;
		score.arg_F1 = f_measure;
	}
}
