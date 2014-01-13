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
 * @author che
 *
 */
public class BeamSearchCluster extends BeamSearch
{
	
	public BeamSearchCluster(Perceptron model, boolean isTraining)
	{
		super(model, isTraining);
	}
	
	public List<SentenceAssignment> individualBeamSearch(SentenceInstance inst, int beamSize, boolean isLearning)
	{
		List<SentenceAssignment> beam = new ArrayList<SentenceAssignment>();
		SentenceAssignment initial = new SentenceAssignment(inst.nodeTargetAlphabet, inst.edgeTargetAlphabet, inst.featureAlphabet, inst.controller);
		beam.add(initial);
		
		// clear the feature vector of ground-truth assignment
		inst.target.clearFeatureVectors();
		
		for(int i=0; i<inst.size(); i++)
		{
			// create a container for sucessor 
			List<SentenceAssignment> successor = new ArrayList<SentenceAssignment>();
			// go through each partial assignment in previous beam to get successors for each one
			for(SentenceAssignment assn : beam)
			{
				List<SentenceAssignment> partial_successor = expandTrigger(inst, assn, i, isLearning);
				if(partial_successor != null)
				{
					successor.addAll(partial_successor);
				}
			}		
			// evaluate all successors only consider trigger labeling, and then select beam
			inst.target.makeNodeFeatures(inst, i, false, model.controller.addNeverSeenFeatures);
			if(inst.controller.useGlobalFeature)
			{
				inst.target.makeGlobalFeaturesTrigger(inst, i, false, model.controller.addNeverSeenFeatures);
			}
			for(SentenceAssignment assn : successor)
			{
				// make basic bigram features for event trigger
				assn.makeNodeFeatures(inst, i, false, model.controller.addNeverSeenFeatures);
				// evaluate the score of the assignment
				if(inst.controller.useGlobalFeature)
				{
					assn.makeGlobalFeaturesTrigger(inst, i, false, model.controller.addNeverSeenFeatures);
				}
				
				evaluate(assn, getWeights());
			}
			
			// rank according to score
			Collections.sort(successor, new ScoreComparator());
			beam = successor.subList(0, Math.min(successor.size(), beamSize));
			
			// check early violation
			if(isLearning)
			{	
				boolean violation = inst.violateGoldStandard(beam, -1); // only consider the trigger labeling
				if(violation)
				{
					beam.get(0).setViolate(true);
					return beam;
				}
			}
			
			// expand the arguments for assignments in beam
			for(int k=0; k<inst.eventArgCandidates.size(); k++)
			{
				successor = new ArrayList<SentenceAssignment>();
				for(SentenceAssignment assn : beam)
				{
					if(SentenceAssignment.isArgumentable(assn.getCurrentNodeLabel()))
					{
						List<SentenceAssignment> partial_successor = expandArg(inst, assn, i, k, isLearning);
						if(partial_successor != null)
						{
							successor.addAll(partial_successor);
						}
						else
						{
							// if there is no sucessor on this entity, then add the assn to the successor
							successor.add(assn);
						}
					}
					else
					{
						successor.add(assn);
					}
				}
				inst.target.makeEdgeLocalFeature(inst, i, false, k, model.controller.addNeverSeenFeatures);
				if(inst.controller.useGlobalFeature)
				{
					// in each step of argument expansion, feed global feature if exists
					inst.target.makeGlobalFeaturesProgress(inst, i, k, false, model.controller.addNeverSeenFeatures);
					if(k == inst.eventArgCandidates.size() - 1)
					{
						inst.target.makeGlobalFeaturesComplete(inst, i, false, model.controller.addNeverSeenFeatures);
					}
				}
				for(SentenceAssignment assn : successor)
				{
					// fill in local edge feature for the new argument
					assn.makeEdgeLocalFeature(inst, i, false, k, model.controller.addNeverSeenFeatures);
					if(inst.controller.useGlobalFeature)
					{
						assn.makeGlobalFeaturesProgress(inst, i, k, false, model.controller.addNeverSeenFeatures);
						if(k == inst.eventArgCandidates.size() - 1)
						{
							assn.makeGlobalFeaturesComplete(inst, i, false, model.controller.addNeverSeenFeatures);
						}
					}
					// evaluate the score of the assignment
					evaluate(assn, getWeights());
				}
				// rank according to score
				Collections.sort(successor, new ScoreComparator());
				beam = successor.subList(0, Math.min(successor.size(), beamSize));
				
				if(isLearning)
				{	
					boolean violation = inst.violateGoldStandard(beam, k); // only consider the trigger labeling and k-th argument labeling
					if(violation)
					{
						beam.get(0).setViolate(true);
						return beam;
					}
				}
			} // end of expanding args
		}
		// return the final beam to collaberative re-ranking
		return beam;
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
		
		List<List<SentenceAssignment>> beams = new ArrayList<List<SentenceAssignment>>();
		for(SentenceInstance inst : clusterInstance.getInstances())
		{
			List<SentenceAssignment> beam = individualBeamSearch(inst, beamSize, isLearning);
			beams.add(beam);
		}
		
		ClusterAssignment ret = new ClusterAssignment(clusterInstance.nodeTargetAlphabet, clusterInstance.edgeTargetAlphabet, 
				clusterInstance.featureAlphabet, clusterInstance.controller);
		
		// check if individual beam violates, 
		// if yes update, if not go to collaborative re-ranking
		if(isLearning)
		{
			boolean violate = false;
			for(List<SentenceAssignment> beam : beams)
			{
				if(beam.get(0).getViolate())
				{
					violate = true;
					break;
				}
			}
			if(violate)
			{
				for(List<SentenceAssignment> beam : beams)
				{
					ret.add(beam.get(0));
				}
				ret.setViolate(true);
				// signal showing that we should early update only on the individual
				// assignment which violates
				ret.setIndividualViolate(true);
				return ret;
			}
		}
		
		// do collaborative re-ranking on beams
		// either do exhausitive enumeration on all beams, or do greedy search
		clusterInstance.target.clearFeatureVectors();
		List<ClusterAssignment> clusterBeam = new ArrayList<ClusterAssignment>();
		
		ClusterAssignment initialAssn = new ClusterAssignment(clusterInstance.nodeTargetAlphabet, clusterInstance.edgeTargetAlphabet, 
					clusterInstance.featureAlphabet, clusterInstance.controller);
		clusterBeam.add(initialAssn);
		
		// start from the second sentence, do re-ranking 
		for(int i=0; i<beams.size(); i++)
		{
			List<SentenceAssignment> beam = beams.get(i);
			List<ClusterAssignment> clusterSucessor = new ArrayList<ClusterAssignment>();
			for(ClusterAssignment clusterAssn : clusterBeam)
			{
				List<ClusterAssignment> partial_sucessor = expandNextSent(clusterAssn, beam);
				clusterSucessor.addAll(partial_sucessor);
			}
			
			// make cross-sent features and evalute
			ClusterAssignment target = (ClusterAssignment) clusterInstance.target;
			if(i > 0)
			{
				target.makeCrossSentFeatures(clusterInstance, i);
			}
			for(ClusterAssignment clusterAssn : clusterSucessor)
			{
				if(i > 0)
				{
					clusterAssn.makeCrossSentFeatures(clusterInstance, i);
				}
				evaluate(clusterAssn, model.getWeights());
			}
			
			// sort
			Collections.sort(clusterSucessor, new Comparator<ClusterAssignment>()
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
				);
			
			clusterBeam = clusterSucessor.subList(0, Math.min(clusterSucessor.size(), beamSize));
			
			// still use early update, check if the current cluster assignments violate
			if(isLearning)
			{
				if(clusterInstance.violateClusterGoldStandard(clusterBeam))
				{
					clusterBeam.get(0).setViolate(true);
					ret = clusterBeam.get(0);
					return ret;
				}
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
	 * given current assignment of previous sents, expand to the next sent
	 * @param clusterAssn
	 * @param beam
	 * @return
	 */
	private List<ClusterAssignment> expandNextSent(ClusterAssignment clusterAssn, List<SentenceAssignment> beam)
	{
		List<ClusterAssignment> ret = new ArrayList<ClusterAssignment>();
		for(SentenceAssignment assn : beam)
		{
			ClusterAssignment child = clusterAssn.clone();
			child.incrementState();
			child.add(assn.clone());
			ret.add(child);
		}
		return ret;
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
		clusterAssn.updateScoreForNewState(weights);
		return clusterAssn.getScore();
	}
}
