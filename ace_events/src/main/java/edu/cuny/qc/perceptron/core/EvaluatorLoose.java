package edu.cuny.qc.perceptron.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.util.InfoGainAndEntropy;
import edu.cuny.qc.util.Span;

/**
 * This is a special evaluator, which use loose metric to evaluate argument
 * an argument is correct iff 
 * 		its event type (ranther than Trigger) is correct
 *     	and role and head is correct 
 * @author che
 *
 */
public class EvaluatorLoose extends Evaluator
{
	
	public static class Argument
	{
		public Span headSpan;		
		public String role;
		public String eventType;
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj == null || !(obj instanceof Argument))
			{
				return false;
			}
			Argument arg = (Argument) obj;
			return new EqualsBuilder().append(headSpan, arg.headSpan).append(role, arg.role).append(eventType, arg.eventType).isEquals();
		}
		
		public Argument()
		{
			;
		}
		
		public Argument(Span headSpan, String role, String eventType)
		{
			this.headSpan = headSpan;
			this.role = role;
			this.eventType = eventType;
		}
	}
	
	/**
	 * Redundant!!!
	 */
	public static class GetArgumentsResult {
		public List<Argument> arguments = null;
		public double totalArgCandidates = 0;
	}
	
	/**
	 * evaluate the performance of argument
	 * @param results
	 * @param instances
	 * @return
	 */
	@Override
	public void evaluteArgument(List<SentenceAssignment> results, List<SentenceAssignment> goldTargets, Score score)
	{
		double count_arg_total = 0;
		double count_arg_ans = 0;
		double count_arg_gold = 0;
		double count_arg_correct = 0;
		
		for(int i=0; i<results.size(); i++)
		{
			SentenceAssignment ans = results.get(i);
			//SentenceInstance goldInstance = instancesGold.get(i);
			SentenceAssignment gold = goldTargets.get(i);//goldInstance.target;
			
			// count num of args
			GetArgumentsResult args_ans = getArguments(gold, ans);
			GetArgumentsResult args_gold = getArguments(gold, gold);
			count_arg_ans += args_ans.arguments.size();
			count_arg_gold += args_gold.arguments.size();
			count_arg_total += gold.eventArgCandidates.size();
			
			// count num of correct args
			for(Argument arg_ans : args_ans.arguments)
			{
				for(Argument arg_gold : args_gold.arguments)
				{
					if(arg_ans.equals(arg_gold))
					{
						count_arg_correct++;
						break;
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
		
		score.count_arg_total = count_arg_total;
		score.count_arg_ans = count_arg_ans;
		score.count_arg_gold = count_arg_gold;
		score.count_arg_correct = count_arg_correct;

		score.arg_precision = prec;
		score.arg_recall = recall;
		score.arg_F1 = f_measure;
		
		double count_arg_ans_not_correct = count_arg_ans - count_arg_correct;
		score.arg_info_gain = InfoGainAndEntropy.infoGain(
				count_arg_correct,
				count_arg_ans_not_correct,
				count_arg_gold - count_arg_correct,
				count_arg_total - count_arg_gold - count_arg_ans_not_correct);

	}

	protected static GetArgumentsResult getArguments(SentenceAssignment gold, SentenceAssignment ans)
	{
		GetArgumentsResult result = new GetArgumentsResult();
		result.arguments = new ArrayList<Argument>();
		
		/// DEBUG
//		if (ans!=gold) {
//			System.out.printf(" | ");
//		}
		///
		Map<Integer, Map<Integer, Integer>> edgeAssns = ans.getEdgeAssignment();
		if(edgeAssns != null)
		{
			for(Integer nodeIndex : edgeAssns.keySet())
			{
				String nodeLabel = ans.getLabelAtToken(nodeIndex);
				Map<Integer, Integer> edgeAssn = edgeAssns.get(nodeIndex);
				for(Integer mentionIndex : edgeAssn.keySet())
				{
					//result.totalArgCandidates++;
					String role = (String) gold.edgeTargetAlphabet.lookupObject(edgeAssn.get(mentionIndex));
					if(!role.equals(SentenceAssignment.Default_Argument_Label))
					{
						AceMention mention = gold.eventArgCandidates.get(mentionIndex);
						Argument argument = new Argument();
						argument.eventType = nodeLabel;
						argument.role = role;
						if(mention instanceof AceEntityMention)
						{
							AceEntityMention entityMention = (AceEntityMention) mention;
							argument.headSpan = entityMention.head;
						}
						else
						{
							argument.headSpan = mention.extent;
						}
						
						if(!result.arguments.contains(argument))
						{
							result.arguments.add(argument);
						}
						
						/// DEBUG
						if (ans!=gold && role.equals("Attacker")) {
							System.out.printf("(%s,%s,%s) ", ans.ord, nodeIndex, mentionIndex);
						}
						///
					}
				}
			}
		}
		
		return result;
	}

}
