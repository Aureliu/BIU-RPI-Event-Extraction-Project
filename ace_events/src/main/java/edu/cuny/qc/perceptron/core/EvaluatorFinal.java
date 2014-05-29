package edu.cuny.qc.perceptron.core;

import java.util.List;

import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.util.UnsupportedParameterException;

/**
 * implement the evaluator as in the final scorer
 * i.e. check the co-reference chain
 * @author XX
 *
 */
public class EvaluatorFinal extends Evaluator
{
	@Override
	public Score evaluate(List<SentenceAssignment> results, List<SentenceInstance> instances)
	{	
		throw new UnsupportedParameterException("EvaluatorFinal");
		
//		Score score = new Score();
//		Stats stats = new Stats();
//	
//		// group them by document id. map from docid -- instance
//		Map<String, List<SentenceInstance>> map_instances = new HashMap<String, List<SentenceInstance>>();
//		Map<String, List<SentenceAssignment>> map_results = new HashMap<String, List<SentenceAssignment>>();
//		for(int i=0; i<instances.size(); i++)
//		{
//			SentenceInstance inst = instances.get(i);
//			SentenceAssignment ans = results.get(i);
//			
//			String docid = inst.docID;
//			List<SentenceInstance> list_instances = map_instances.get(docid);
//			if(list_instances == null)
//			{
//				list_instances = new ArrayList<SentenceInstance>();
//				map_instances.put(docid, list_instances);
//			}
//			list_instances.add(inst);
//			
//			List<SentenceAssignment> list_results = map_results.get(docid);
//			if(list_results == null)
//			{
//				list_results = new ArrayList<SentenceAssignment>();
//				map_results.put(docid, list_results);
//			}
//			list_results.add(ans);
//		}
//		
//		// read event mentions
//		for(String docid : map_instances.keySet())
//		{
//			List<SentenceInstance> list_instances = map_instances.get(docid);
//			List<SentenceAssignment> list_results = map_results.get(docid);
//			
//			List<AceEventMention> eventMentions_ans = new ArrayList<AceEventMention>();
//			List<AceEventMention> eventMentions_gold = new ArrayList<AceEventMention>();
//			for(int i=0; i<list_results.size(); i++)
//			{
//				SentenceInstance inst = list_instances.get(i);
//				SentenceAssignment ans = list_results.get(i);
//				SentenceAssignment perfect = inst.target;
//				List<AceEvent> events_ans = inst.getEvents(ans, "id", inst.allText);
//				List<AceEvent> events_gold = inst.getEvents(perfect, "id", inst.allText);
//				
//				for(AceEvent event : events_ans)
//				{
//					eventMentions_ans.addAll(event.mentions);
//				}
//				
//				for(AceEvent event : events_gold)
//				{
//					eventMentions_gold.addAll(event.mentions);
//				}
//			}
//			Scorer.evaluate(stats, eventMentions_ans, eventMentions_gold, System.out);
//		}
//		
//		stats.calc();
//		score.arg_precision = stats.prec_arg;
//		score.arg_recall = stats.recall_arg;
//		score.arg_F1 = stats.f1_arg;
//		score.trigger_precision = stats.prec_trigger;
//		score.trigger_recall = stats.recall_trigger;
//		score.trigger_F1 = stats.f1_trigger;
//		
//		// calculate harmonic mean
//		score.calculateHarmonic_mean();
//		return score;
	}
}
