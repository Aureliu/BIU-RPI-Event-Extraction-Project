package edu.cuny.qc.perceptron.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.cuny.qc.perceptron.types.ClusterAssignment;
import edu.cuny.qc.perceptron.types.ClusterInstance;
import edu.cuny.qc.perceptron.types.FeatureVector;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;

/**
 * This class implements the beamSearch for cross-sentence searching
 * the basis of the search is a cluster of sentence ClusterInstance
 * 
 * Different from BeamSearchCluster, 
 * it consider sents in a cluster as a big sequence 
 * @author che
 *
 */
public class BeamSearchClusterSeq extends BeamSearch
{
	
	public BeamSearchClusterSeq(Perceptron model, boolean isTraining)
	{
		super(model, isTraining);
	}
	
	
	/**
	 * search through each sentence in the cluster as standard beamSearch
	 * at last state, apply cross-sentence features
	 * @param clusterInstance
	 * @param beamSize
	 * @param isLearning
	 * @return
	 */
	public SentenceAssignment beamSearch(SentenceInstance instance, int beamSize, boolean isLearning)
	{
		ClusterInstance clusterInstance = (ClusterInstance) instance;
		ClusterAssignment ret = new ClusterAssignment(clusterInstance.nodeTargetAlphabet, clusterInstance.edgeTargetAlphabet, 
				clusterInstance.featureAlphabet, clusterInstance.controller);
		
		// do sequential beamSearch on all sents
		clusterInstance.target.clearFeatureVectors();
		List<ClusterAssignment> clusterBeam = new ArrayList<ClusterAssignment>();
		
		ClusterAssignment initialAssn = new ClusterAssignment(clusterInstance.nodeTargetAlphabet, clusterInstance.edgeTargetAlphabet, 
					clusterInstance.featureAlphabet, clusterInstance.controller);
		clusterBeam.add(initialAssn);
		
		// start from the second sentence, do re-ranking 
		for(int i=0; i<clusterInstance.size(); i++)
		{			
			clusterBeam = expandNextSent(clusterInstance, clusterBeam, i, isLearning, beamSize);
			// check early update
			if(isLearning && clusterBeam.get(0).getViolate())
			{
				// DEBUG
				/*
				int sent_id = clusterBeam.get(0).getState();
				int node_id = clusterBeam.get(0).get(sent_id).getState();
				if(sent_id != i)
				{
					System.err.println("ERROR!!!");
				}
				System.out.println("sent_id: " + sent_id + "\t node_id:" + node_id);
				*/
				// END DEBUG
				return clusterBeam.get(0);
			}
		}
		
		// check if final result is correct
		if(isLearning)
		{
			if(clusterInstance.violateClusterGoldStandard(clusterBeam.get(0)))
			{
				clusterBeam.get(0).setViolate(true);
			}
		}
		// return the best result
		ret = clusterBeam.get(0);
		return ret;
	}
	
	/**
	 * given the cluster instance and the current beam, 
	 * expand next sentence (i-th sentence)
	 * if violates at some step, return for early update
	 * @param clusterInstance
	 * @param clusterBeam
	 * @param i  the sent id
	 * @return the new beam
	 */
	private List<ClusterAssignment> expandNextSent(ClusterInstance clusterInstance, List<ClusterAssignment> clusterBeam, 
			int sent_id, boolean isLearning, int beamSize)
	{
		SentenceInstance inst = clusterInstance.getInst(sent_id);
		// do standard beam search on inst
		// clear the feature vector of ground-truth assignment
		inst.target.clearFeatureVectors();
		// create empty assignment for this current sent
		for(ClusterAssignment clusterAssn : clusterBeam)
		{
			SentenceAssignment initial = new SentenceAssignment(inst.nodeTargetAlphabet, inst.edgeTargetAlphabet, 
					inst.featureAlphabet, inst.controller);
			clusterAssn.add(initial);
			clusterAssn.incrementState();
		}
		for(int node_id=0; node_id<inst.size(); node_id++)
		{
			// go through each partial assignment in previous beam to get successors for each one
			List<ClusterAssignment> sucessor = new ArrayList<ClusterAssignment>();
			for(ClusterAssignment clusterAssn : clusterBeam)
			{
				SentenceAssignment assn = clusterAssn.get(sent_id);
				List<SentenceAssignment> partial_successor = expandTrigger(inst, assn, node_id, isLearning);
				if(partial_successor != null)
				{
					for(SentenceAssignment temp : partial_successor)
					{
						ClusterAssignment child = clusterAssn.clone();
						child.clusterAssn.set(sent_id, temp);
						sucessor.add(child);
					}
				}
			}		
			// evaluate all successors only consider trigger labeling, and then select beam
			inst.target.makeNodeFeatures(inst, node_id, false, model.controller.addNeverSeenFeatures);
			if(inst.controller.useGlobalFeature)
			{
				inst.target.makeGlobalFeaturesTrigger(inst, node_id, false, model.controller.addNeverSeenFeatures);
			}
			((ClusterAssignment) clusterInstance.target).makeCrossSentFeaturesTrigger(clusterInstance, sent_id, node_id);
			for(ClusterAssignment clusterAssn : sucessor)
			{
				SentenceAssignment assn = clusterAssn.get(sent_id);
				// make basic bigram features for event trigger
				assn.makeNodeFeatures(inst, node_id, false, model.controller.addNeverSeenFeatures);
				// evaluate the score of the assignment
				if(inst.controller.useGlobalFeature)
				{
					assn.makeGlobalFeaturesTrigger(inst, node_id, false, model.controller.addNeverSeenFeatures);
				}
				// add cross-sent features for new trigger
				clusterAssn.makeCrossSentFeaturesTrigger(clusterInstance, sent_id, node_id);
				evaluate(clusterAssn, getWeights());
			}
			
			// rank according to score
			Collections.sort(sucessor, new ClusterScoreComparator());
			clusterBeam = sucessor.subList(0, Math.min(sucessor.size(), beamSize));
			
			// check early violation
			// only consider the trigger labeling
			if(isLearning)
			{	
				boolean violation = clusterInstance.violateClusterGoldStandard(clusterBeam, -1); 
				if(violation)
				{
					clusterBeam.get(0).setViolate(true);
					return clusterBeam;
				}
			}
			
			// expand the arguments for assignments in beam
			for(int k=0; k<inst.eventArgCandidates.size(); k++)
			{
				sucessor = new ArrayList<ClusterAssignment>();
				for(ClusterAssignment clusterAssn : clusterBeam)
				{
					SentenceAssignment assn = clusterAssn.get(sent_id);
					if(SentenceAssignment.isArgumentable(assn.getCurrentNodeLabel()))
					{
						List<SentenceAssignment> partial_successor = expandArg(inst, assn, node_id, k, isLearning);
						if(partial_successor != null)
						{
							for(SentenceAssignment temp : partial_successor)
							{
								ClusterAssignment child = clusterAssn.clone();
								child.clusterAssn.set(sent_id, temp);
								sucessor.add(child);
							}
						}
						else
						{
							// if there is no sucessor on this entity, then add the assn to the successor
							sucessor.add(clusterAssn);
						}
					}
					else
					{
						sucessor.add(clusterAssn);
					}
				}
				inst.target.makeEdgeLocalFeature(inst, node_id, false, k, model.controller.addNeverSeenFeatures);
				if(inst.controller.useGlobalFeature)
				{
					// in each step of argument expansion, feed global feature if exists
					inst.target.makeGlobalFeaturesProgress(inst, node_id, k, false, model.controller.addNeverSeenFeatures);
					if(k == inst.eventArgCandidates.size() - 1)
					{
						inst.target.makeGlobalFeaturesComplete(inst, node_id, false, model.controller.addNeverSeenFeatures);
					}
				}
				((ClusterAssignment) clusterInstance.target).makeCrossSentFeaturesArg(clusterInstance, sent_id, node_id, k);
				for(ClusterAssignment clusterAssn : sucessor)
				{
					SentenceAssignment assn = clusterAssn.get(sent_id);
					// fill in local edge feature for the new argument
					assn.makeEdgeLocalFeature(inst, node_id, false, k, model.controller.addNeverSeenFeatures);
					if(inst.controller.useGlobalFeature)
					{
						assn.makeGlobalFeaturesProgress(inst, node_id, k, false, model.controller.addNeverSeenFeatures);
						if(k == inst.eventArgCandidates.size() - 1)
						{
							assn.makeGlobalFeaturesComplete(inst, node_id, false, model.controller.addNeverSeenFeatures);
						}
					}
					// add cross-sent features for new trigger
					clusterAssn.makeCrossSentFeaturesArg(clusterInstance, sent_id, node_id, k);
					// evaluate the score of the assignment
					evaluate(clusterAssn, getWeights());
				}
				// rank according to score
				Collections.sort(sucessor, new ClusterScoreComparator());
				clusterBeam = sucessor.subList(0, Math.min(sucessor.size(), beamSize));
				
				if(isLearning)
				{	
					// only consider the trigger labeling and k-th argument labeling
					boolean violation = clusterInstance.violateClusterGoldStandard(clusterBeam, k);  
					if(violation)
					{
						clusterBeam.get(0).setViolate(true);
						return clusterBeam;
					}
				}
			} // end of expanding args
		}
		return clusterBeam;
	}

	public static class ClusterScoreComparator implements Comparator<ClusterAssignment>
	{
		@Override
		public int compare(
				ClusterAssignment arg0, ClusterAssignment arg1)
		{
			double score0 = arg0.getScore();
			double score1 = arg1.getScore();
			if(Math.abs(score0 - score1) < 0.00001)
			{
				return 0;
			}
			if(score0 > score1)
			{
				return -1;
			}
			else 
			{
				return 1;
			}
		}
	}
	
	/**
	 * evaluate the score of the cluster of assignments
	 * this includes score of individual assignment and cross-sent features
	 * @param partial
	 * @param problem
	 * @return
	 */
	static protected double evaluate(ClusterAssignment clusterAssn, FeatureVector weights)
	{
		
		// for the current sentence, update the score
		SentenceAssignment sentAssn = clusterAssn.get(clusterAssn.getState());
		sentAssn.updateScoreForNewState(weights);
		
		// for sentence in the past, just add the score
		clusterAssn.updateScoreForNewState(weights);
		return clusterAssn.getScore();
	}
}
