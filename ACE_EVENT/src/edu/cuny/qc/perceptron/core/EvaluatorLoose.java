package edu.cuny.qc.perceptron.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
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
	 * evaluate the performance of argument
	 * @param results
	 * @param instances
	 * @return
	 */
	@Override
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
			
			// count num of args
			List<Argument> args_ans = getArguments(goldInstance, ans);
			List<Argument> args_gold = getArguments(goldInstance, gold); 
			count_arg_ans += args_ans.size();
			count_arg_gold += args_gold.size();
			
			// count num of correct args
			for(Argument arg_ans : args_ans)
			{
				for(Argument arg_gold : args_gold)
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
		
		score.arg_precision = prec;
		score.arg_recall = recall;
		score.arg_F1 = f_measure;
	}

	protected static List<Argument> getArguments(SentenceInstance inst, SentenceAssignment ans)
	{
		List<Argument> ret = new ArrayList<Argument>();
		
		Map<Integer, Map<Integer, Integer>> edgeAssns = ans.getEdgeAssignment();
		if(edgeAssns != null)
		{
			for(Integer nodeIndex : edgeAssns.keySet())
			{
				String nodeLabel = ans.getLabelAtToken(nodeIndex);
				Map<Integer, Integer> edgeAssn = edgeAssns.get(nodeIndex);
				for(Integer mentionIndex : edgeAssn.keySet())
				{
					String role = (String) inst.edgeTargetAlphabet.lookupObject(edgeAssn.get(mentionIndex));
					if(!role.equals(SentenceAssignment.Default_Argument_Label))
					{
						AceMention mention = inst.eventArgCandidates.get(mentionIndex);
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
						
						if(!ret.contains(argument))
						{
							ret.add(argument);
						}
					}
				}
			}
		}
		
		return ret;
	}

}
