package edu.cuny.qc.perceptron.core;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;

/**
 * This is for experiments of standard update 
 * @author XX
 *
 */
public class BeamSearchStandard extends BeamSearch
{
	public int num_update = 0;
	public int num_update_invalid = 0;
	
	public void print_num_update(PrintStream out)
	{
		out.println("Num update: " + num_update);
		out.println("Num invalid update: " + num_update_invalid);
	}
	
	public BeamSearchStandard(Perceptron model, boolean isTraining)
	{
		super(model, isTraining);
		num_update = 0;
		num_update_invalid = 0;
	}

	@Override
	public SentenceAssignment beamSearch(SentenceInstance problem, int beamSize, boolean isLearning)
	{
		List<SentenceAssignment> beam = new ArrayList<SentenceAssignment>();
		SentenceAssignment initial = new SentenceAssignment(problem.nodeTargetAlphabet, problem.edgeTargetAlphabet, problem.featureAlphabet, problem.controller);
		beam.add(initial);
		
		// clear the feature vector of ground-truth assignment
		problem.target.clearFeatureVectors();
		problem.target.setScore(0.0);
		problem.target.retSetState();
		
		for(int i=0; i<problem.size(); i++)
		{
			// create a container for sucessor 
			List<SentenceAssignment> successor = new ArrayList<SentenceAssignment>();
			// go through each partial assignment in previous beam to get successors for each one
			for(SentenceAssignment assn : beam)
			{
				List<SentenceAssignment> partial_successor = expandTrigger(problem, assn, i, isLearning);
				if(partial_successor != null)
				{
					successor.addAll(partial_successor);
				}
			}
			
			// increase the state of target
			problem.target.incrementState();
			
			// evaluate all successors only consider trigger labeling, and then select beam
			problem.target.makeNodeFeatures(problem, i, false, model.controller.addNeverSeenFeatures);
			if(problem.controller.useGlobalFeature)
			{
				problem.target.makeGlobalFeaturesTrigger(problem, i, false, model.controller.addNeverSeenFeatures);
			}
			// also evaluate gold standard
			evaluate(problem.target, getWeights());
			for(SentenceAssignment assn : successor)
			{
				// make basic bigram features for event trigger
				assn.makeNodeFeatures(problem, i, false, model.controller.addNeverSeenFeatures);
				// evaluate the score of the assignment
				if(problem.controller.useGlobalFeature)
				{
					assn.makeGlobalFeaturesTrigger(problem, i, false, model.controller.addNeverSeenFeatures);
				}
				evaluate(assn, getWeights());
			}
			
			// rank according to score
			Collections.sort(successor, new ScoreComparator());
			beam = successor.subList(0, Math.min(successor.size(), beamSize));
			
			// expand the arguments for assignments in beam
			for(int k=0; k<problem.eventArgCandidates.size(); k++)
			{
				successor = new ArrayList<SentenceAssignment>();
				for(SentenceAssignment assn : beam)
				{
					if(SentenceAssignment.isArgumentable(assn.getCurrentNodeLabel()))
					{
						List<SentenceAssignment> partial_successor = expandArg(problem, assn, i, k, isLearning);
						if(partial_successor != null)
						{
							successor.addAll(partial_successor);
						}
						else
						{
							// if there is no successor on this entity, then add the assn to the successor
							successor.add(assn);
						}
					}
					else
					{
						successor.add(assn);
					}
				}
				problem.target.makeEdgeLocalFeature(problem, i, false, k, model.controller.addNeverSeenFeatures);
				if(problem.controller.useGlobalFeature)
				{
					// in each step of argument expansion, feed global feature if exists
					problem.target.makeGlobalFeaturesProgress(problem, i, k, false, model.controller.addNeverSeenFeatures);
					if(k == problem.eventArgCandidates.size() - 1)
					{
						problem.target.makeGlobalFeaturesComplete(problem, i, false, model.controller.addNeverSeenFeatures);
					}
				}
				// also evaluate gold-standard
				evaluate(problem.target, getWeights());
				
				for(SentenceAssignment assn : successor)
				{
					// fill in local edge feature for the new argument
					assn.makeEdgeLocalFeature(problem, i, false, k, model.controller.addNeverSeenFeatures);
					if(problem.controller.useGlobalFeature)
					{
						assn.makeGlobalFeaturesProgress(problem, i, k, false, model.controller.addNeverSeenFeatures);
						if(k == problem.eventArgCandidates.size() - 1)
						{
							assn.makeGlobalFeaturesComplete(problem, i, false, model.controller.addNeverSeenFeatures);
						}
					}
					// evaluate the score of the assignment
					evaluate(assn, getWeights());
				}
				
				// rank according to score
				Collections.sort(successor, new ScoreComparator());
				beam = successor.subList(0, Math.min(successor.size(), beamSize));
			} // end of expanding args
		}
		
		// this is just standard update 
		if(isLearning && problem.violateGoldStandard(beam.get(0)))
		{
			// check if it's invalid update
			if((problem.target.getScore() - beam.get(0).getScore()) >= 0.01)
			{
				this.num_update_invalid++;
			}
			this.num_update++;
			beam.get(0).setViolate(true);
		}
		return beam.get(0);
	}
}
