package edu.cuny.qc.perceptron.featureGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.cuny.qc.ace.acetypes.AceEntity;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.types.ClusterAssignment;
import edu.cuny.qc.perceptron.types.ClusterInstance;
import edu.cuny.qc.perceptron.types.DocumentCrossSent;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.util.TokenAnnotations;
import edu.cuny.qc.util.TypeConstraints;

/**
 * This class implements cross-sent features for BeamSearchClusterSeq
 * and BeamSearchCluster
 * Basic cross-sent features:
 * 1. the events that are linked to the same entity
 * 2. the bag-of-event-types 
 * @author che
 *
 */
public class CrossSentFeatureGenerator
{
	static List<String> getPotentialEventTypes(String lemma)
	{
		List<String> ret = new ArrayList<String>();
		for(String eventType : DocumentCrossSent.triggerTokensFineGrained.keySet())
		{
			if(DocumentCrossSent.triggerTokensFineGrained.get(eventType).contains(lemma))
			{
				ret.add(eventType);
			}
		}
		
		return ret;
	}
	
	/**
	 * TODO: make node features that are based on crossSent information
	 * e.g. what kind of trigger tokens are in the cluster by dict matching
	 * This type of feature is for Trigger 
	 * @param clusterInstance
	 * @param clusterAssn
	 * @param sent_id
	 * @return
	 */
	public static List<String> makeCrossSentNodeFeature(ClusterInstance clusterInstance,
			ClusterAssignment clusterAssn, int sent_id, int token_id)
	{
		List<String> features = new ArrayList<String>();
		SentenceAssignment assn = clusterAssn.get(sent_id);
		String nodeLabel = assn.getLabelAtToken(token_id);
		SentenceInstance inst = clusterInstance.getInst(sent_id);
		List<Map<Class<?>, Object>> tokens = inst.getTokenFeatureMaps();
		String lemma = (String) tokens.get(token_id).get(TokenAnnotations.LemmaAnnotation.class);
		
		List<String> eventTypes = getPotentialEventTypes(lemma);
		// skip if doesn't match event types
		if(eventTypes.size() == 0)
		{
			return features;
		}
		
		for(int i=0; i<clusterInstance.size(); i++)
		{
			if(i == sent_id)
			{
				continue;
			}
			SentenceInstance pre_inst = clusterInstance.getInst(i);
			List<Map<Class<?>, Object>> pre_tokens = pre_inst.getTokenFeatureMaps();
			for(int j=0; j<pre_tokens.size(); j++)
			{
				String pre_lemma = (String) pre_tokens.get(j).get(TokenAnnotations.LemmaAnnotation.class);
				List<String> pre_eventTypes = getPotentialEventTypes(pre_lemma);
				
//				for(String eventType : eventTypes)
//				{
//					for(String pre_eventType : pre_eventTypes)
//					{
//						if(inTheSameCategory(pre_eventType, eventType))
//						{
//							// 1: add feature that saying there is another potential trigger in the same category
//							String feature = "happenInOtherType = " + pre_eventType + "#NodeLabel:" + nodeLabel;
//							if(!features.contains(feature))
//							{
//								features.add(feature);
//							}
//						}
//					}
//				}
				
				for(String pre_eventType : pre_eventTypes)
				{
					List<String> temp = DocumentCrossSent.triggerTokensHighQuality.get(pre_eventType);
					if(temp != null && temp.contains(pre_lemma))
					{
						// 2: add feature that saying there is another high-confidence potential trigger in the cluster
						String feature = "happenInOtherType = " + pre_eventType + "#NodeLabel:" + nodeLabel;
						if(!features.contains(feature))
						{
							features.add(feature);
						}
					}
				}
			}
		}
		
		return features;
	}

	static boolean inTheSameCategory(String subevent1, String subevent2)
	{
		String event1 = TypeConstraints.eventTypeMap.get(subevent1);
		String event2 = TypeConstraints.eventTypeMap.get(subevent2);
		return event1.equals(event2);
	}
	
	/**
	 * given a cluster of sentences
	 * make global features across different sents
	 * Note: always assume the state-th individual assignment is newly added
	 * @param clusterInstance
	 * @param clusterAssn
	 * @param sent_id
	 * @return 
	 */
	public static List<String> makeCrossSentFeature(ClusterInstance clusterInstance,
			ClusterAssignment clusterAssn, int sent_id)
	{
		List<String> features = new ArrayList<String>();
		
		SentenceInstance inst = clusterInstance.getInst(sent_id);
		for(int token_id=0; token_id<inst.size(); token_id++)
		{
			List<String> featuresTrigger = makeCrossSentFeatureTrigger(clusterInstance, clusterAssn, 
					sent_id, token_id);
			features.addAll(featuresTrigger);
			for(int arg_id=0; arg_id < inst.eventArgCandidates.size(); arg_id++)
			{
				List<String> featuresArg = makeCrossSentFeatureArg(clusterInstance, clusterAssn, sent_id, 
					token_id, arg_id);
				features.addAll(featuresArg);
			}
		}
		
		return features;
	}

	/**
	 * given a cluster of sentences
	 * make global features across different sents
	 * Note: always assume the state-th individual assignment is newly added
	 * @param clusterInstance
	 * @param clusterAssignment
	 * @param sent_id
	 * @param token_id
	 * @return
	 */
	public static List<String> makeCrossSentFeatureTrigger(
			ClusterInstance clusterInstance,
			ClusterAssignment clusterAssignment, int sent_id, int token_id)
	{
		List<String> features = new ArrayList<String>();
		SentenceAssignment assn = clusterAssignment.get(sent_id);
		String nodeLabel = assn.getLabelAtToken(token_id);
		
		// add cross sent node feature first
		List<String> nodeFeatures = makeCrossSentNodeFeature(clusterInstance,
				clusterAssignment, sent_id, token_id);
		features.addAll(nodeFeatures);
		
		if(sent_id < 1)
		{
			return features;
		}
		
		// get history stats
		List<String> previousEventTypes = new ArrayList<String>();
		for(int i=0; i<sent_id; i++)
		{
			SentenceInstance pre_inst = clusterInstance.getInst(i);
			SentenceAssignment pre_assn = clusterAssignment.get(i);
			for(int j=0; j<pre_inst.size(); j++)
			{
				String pre_nodeLabel = pre_assn.getLabelAtToken(j);
				if(!pre_nodeLabel.equals(SentenceAssignment.Default_Trigger_Label))
				{
					// 2. if two tokens have the same text or Hypernym
					// check if they have same label
					List<String> preWords = (List<String>) pre_inst.getTokenFeatureMaps().get(j).
					get(TokenAnnotations.SynonymsAnnotation.class);
					List<String> words = (List<String>) clusterInstance.getInst(sent_id).
					getTokenFeatureMaps().get(token_id).get(TokenAnnotations.SynonymsAnnotation.class);
					if(preWords != null && words != null && !Collections.disjoint(preWords, words))
					{
						if(pre_nodeLabel.equals(nodeLabel))
						{
							features.add("SameWordSameLabel=" + true);
						}
						else
						{
							features.add("SameWordSameLabel=" + false);
						}
					}
					
					// save the label in history
					if(!previousEventTypes.contains(pre_nodeLabel))
					{
						previousEventTypes.add(pre_nodeLabel);
					}
				}
			}
		}
		
		if(!nodeLabel.equals(SentenceAssignment.Default_Trigger_Label))
		{
			// 1. get bigram cross-sent event types 
			for(String history : previousEventTypes)
			{
				String triggerBigram = getSortedCombination(history, nodeLabel);
				features.add("TriggerBigam=" + triggerBigram);
			}
		}
		
		// 3. check if neighbors of this token are triggers for some event in previous sents
		List<Map<Class<?>, Object>> tokens = (List<Map<Class<?>, Object>>) clusterInstance.getInst(sent_id).get(InstanceAnnotations.Token_FEATURE_MAPs);
		Map<Class<?>, Object> token = tokens.get(token_id);
		AceMention syn_near_mention = (AceMention) token.get(TokenAnnotations.SyntacticallyNearestEntity.class);
		AceMention phy_near_mention = (AceMention) token.get(TokenAnnotations.PhysicallyNearestEntity.class);
		List<String> roles_history = new ArrayList<String>();
		if(syn_near_mention instanceof AceEntityMention)
		{
			List<String> roles_syn_near_mention = getRolesInHistory((AceEntityMention) syn_near_mention, 
					clusterInstance, clusterAssignment, sent_id);
			roles_history.addAll(roles_syn_near_mention);
		}
		if(phy_near_mention instanceof AceEntityMention)
		{
			List<String> roles_phy_near_mention = getRolesInHistory((AceEntityMention) phy_near_mention, 
					clusterInstance, clusterAssignment, sent_id);
			roles_history.addAll(roles_phy_near_mention);
		}
		for(String role : roles_history)
		{
			features.add("neighborEntityRoles=" + role + "#" + nodeLabel);
		}
		
		return features;
	}

	/**
	 * 
	 * @param syn_near_mention
	 * @param clusterInstance
	 * @param clusterAssignment
	 * @param sent_id
	 * @return
	 */
	private static List<String> getRolesInHistory(AceEntityMention mention,
			ClusterInstance clusterInstance,
			ClusterAssignment clusterAssignment, int sent_id)
	{
		List<String> ret = new ArrayList<String>();
		AceEntity parent = (AceEntity) mention.getParent();
		for(int i=0; i<sent_id; i++)
		{
			SentenceInstance inst = clusterInstance.getInst(i);
			SentenceAssignment assn = clusterAssignment.get(i);
			Map<Integer, Map<Integer, Integer>> edgeAssns = assn.getEdgeAssignment();
			if(edgeAssns == null)
			{
				continue;
			}
			for(int j=0; j<inst.size(); j++)
			{
				String nodeLabel = assn.getLabelAtToken(j);
				Map<Integer, Integer> edgeAssn = edgeAssns.get(j);
				if(edgeAssn == null)
				{
					continue;
				}
				for(int entityIndex : edgeAssn.keySet())
				{
					if(inst.eventArgCandidates.get(entityIndex).getParent() == parent)
					{
						int edgeLabelIndx = edgeAssn.get(entityIndex);
						String edgeLabel = (String) inst.edgeTargetAlphabet.lookupObject(edgeLabelIndx);
						if(!edgeLabel.equals(SentenceAssignment.Default_Argument_Label))
						{
							ret.add(edgeLabel+nodeLabel);
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * cross-sent features, focus on argument labels
	 * @param clusterInstance
	 * @param clusterAssignment
	 * @param sent_id
	 * @param token_id
	 * @param argNum
	 * @return
	 */
	public static List<String> makeCrossSentFeatureArg(ClusterInstance clusterInstance,
			ClusterAssignment clusterAssignment, int sent_id, int token_id,
			int argNum)
	{
		List<String> features = new ArrayList<String>();
		
		// get history stats
		List<AceEntity> pre_entities = new ArrayList<AceEntity>(); 
		List<String> pre_eventTypes = new ArrayList<String>();
		List<String> pre_edgeLabels = new ArrayList<String>();
		for(int i=0; i<sent_id; i++)
		{
			SentenceInstance inst = clusterInstance.getInst(i);
			SentenceAssignment assn = clusterAssignment.get(i);
			Map<Integer, Map<Integer, Integer>> edgeAssn = assn.getEdgeAssignment();
			
			for(Integer node_idx : edgeAssn.keySet())
			{
				if(i == sent_id && node_idx > token_id || i > sent_id)
				{
					break;
				}
				Map<Integer, Integer> args = edgeAssn.get(node_idx);
				for(Integer arg_idx : args.keySet())
				{
					if(i == sent_id && node_idx == token_id && arg_idx > argNum 
							|| i == sent_id && node_idx > token_id || i > sent_id)
					{
						break;
					}
					int edgeLabelIdx = args.get(arg_idx);
					String edgeLabel = (String) inst.edgeTargetAlphabet.lookupObject(edgeLabelIdx);
					if(!edgeLabel.equals(SentenceAssignment.Default_Argument_Label))
					{
						String nodeLabel = assn.getLabelAtToken(node_idx);
						
						AceMention mention = inst.eventArgCandidates.get(arg_idx);
						if(mention instanceof AceEntityMention)
						{
							AceEntity entity = (AceEntity) mention.getParent();
							pre_entities.add(entity);
							pre_eventTypes.add(nodeLabel);
							pre_edgeLabels.add(edgeLabel);
						}
					}
				}
			}
		}
		
		SentenceInstance inst = clusterInstance.getInst(sent_id);
		SentenceAssignment assn = clusterAssignment.get(sent_id);
		String nodeLabel = assn.getLabelAtToken(token_id);
		Map<Integer, Integer> edgeAssn = assn.getEdgeAssignment().get(token_id);
		if(edgeAssn == null)
		{
			return features;
		}
		Integer edgeLabelIndx = edgeAssn.get(argNum);
		if(edgeLabelIndx == null)
		{
			return features;
		}
		String edgeLabel = (String) inst.edgeTargetAlphabet.lookupObject(edgeLabelIndx);
		AceMention mention = inst.eventArgCandidates.get(argNum);
		if(! (mention instanceof AceEntityMention))
		{
			return features;
		}
		AceEntity entity = (AceEntity) mention.getParent();
		// get combination of event types and combination of arg types
		for(int j=0; j<pre_entities.size(); j++)
		{
			AceEntity pre_entity = pre_entities.get(j);
			if(pre_entity.equals(entity))
			{
				String pre_nodeLabel = pre_eventTypes.get(j);
				String pre_edgeLabel = pre_edgeLabels.get(j);
				
				if(!edgeLabel.equals(SentenceAssignment.Default_Argument_Label))
				{
					// 1. get role pair associates with same entity
					// 2. get event type pair associates with same entity
					String rolePair = getSortedCombination(pre_edgeLabel+pre_nodeLabel, edgeLabel+nodeLabel);
					String eventPair = getSortedCombination(pre_nodeLabel, nodeLabel);
					
					features.add("RolePair=" + rolePair);
					features.add("TriggerPair=" + eventPair);
				}
			}
		}
		
		return features;
	}
	
	/**
	 * given a set of labels, get a string of ordered labels
	 * @param label1
	 * @param label2
	 * @return
	 */
	static private String getSortedCombination(String label1, String label2)
	{
		String ret = "";
		List<String> list = new ArrayList<String>();
		list.add(label1);
		list.add(label2);
		// sort
		Collections.sort(list);
		for(String label : list)
		{
			ret += label + "#";
		}
		return ret;
	}
}
