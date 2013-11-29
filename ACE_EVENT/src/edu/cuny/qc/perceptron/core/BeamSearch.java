package edu.cuny.qc.perceptron.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.types.FeatureVector;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.util.TypeConstraints;

/**
 * In this beam search, we use strict beam to search trigger and arguments
 * in each state:
 * 		(1) determine trigger labeling in beam
 * 		(2) determine argument labeling in beam for each entity candidate
 * Hopefully, this beam search can be more time efficient than BeamSearch class
 * @author che
 *
 */
public class BeamSearch
{
	Perceptron model;
	boolean isTraining = true;
	
	private static final boolean PRINT_BEAM = false;
	
	protected FeatureVector getWeights()
	{
		if(!isTraining && model.controller.avgArguments)
		{
			return model.getAvg_weights();
		}
		else
		{
			return model.getWeights();
		}
	}
	
	public BeamSearch(Perceptron model, boolean isTraining)
	{
		this.model = model;
		this.isTraining = isTraining;
	}
	
	public SentenceAssignment beamSearch(SentenceInstance problem, int beamSize, boolean isLearning)
	{
		List<SentenceAssignment> beam = new ArrayList<SentenceAssignment>();
		SentenceAssignment initial = new SentenceAssignment(problem.nodeTargetAlphabet, problem.edgeTargetAlphabet, problem.featureAlphabet, problem.controller);
		beam.add(initial);
		
		// clear the feature vector of ground-truth assignment
		problem.target.clearFeatureVectors();
		
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
			// evaluate all successors only consider trigger labeling, and then select beam
			problem.target.makeNodeFeatures(problem, i, false, model.controller.addNeverSeenFeatures);
			if(problem.controller.useGlobalFeature)
			{
				problem.target.makeGlobalFeaturesTrigger(problem, i, false, model.controller.addNeverSeenFeatures);
			}
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
			
			// check early violation
			if(isLearning)
			{	
				boolean violation = problem.violateGoldStandard(beam, -1); // only consider the trigger labeling
				if(violation)
				{
					beam.get(0).setViolate(true);
					return beam.get(0);
				}
			}
			
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
							// if there is no sucessor on this entity, then add the assn to the successor
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
				// System.out.println("sucessor size 2: " + successor.size());
				if(isLearning)
				{	
					boolean violation = problem.violateGoldStandard(beam, k); // only consider the trigger labeling and k-th argument labeling
					if(violation)
					{
						beam.get(0).setViolate(true);
						return beam.get(0);
					}
				}
			} // end of expanding args
		}
		
		// check if final result is correct
		if(isLearning && problem.violateGoldStandard(beam.get(0)))
		{
			beam.get(0).setViolate(true);
		}
		
		if (PRINT_BEAM) {
			System.out.printf("Beam at the end:\n");
			for (int i=0; i<beam.size(); i++) {
				System.out.printf("%d. [%f] %s\n", i, beam.get(i).getScore(), beam.get(i));
			}
		}
		return beam.get(0);
	}
	
	/**
	 * given a assignment in the beam, expand the 1-best argument labeling for i-th token and k-th entity in the sentence
	 * @param problem
	 * @param assn
	 * @param i
	 * @param k
	 * @param isLearning
	 * @return
	 */
	protected List<SentenceAssignment> expandArg(SentenceInstance problem, SentenceAssignment assn, int i, int k, boolean isLearning)
	{
		String currentNodeLabel = assn.getCurrentNodeLabel();
		// make sucessors of assn by expanding the possible argument links
		List<SentenceAssignment> sucessor = new ArrayList<SentenceAssignment>();
		
		AceMention mention = problem.eventArgCandidates.get(k);
		if(!TypeConstraints.isEntityTypeEventCompatible(currentNodeLabel, mention.getType()))
		{
			return null;
		}
		// for a compatible mention, create an individual labeling
		for(int l=0; l<problem.edgeTargetAlphabet.size(); l++)
		{	
			String edgeLabel = (String) problem.edgeTargetAlphabet.lookupObject(l);
			// skip the mentions that is not compatible with the current node label (trigger type)
			if(!isRoleCompatible(mention.getType(), currentNodeLabel, edgeLabel))
			{
				continue;
			}
			// make local score for (k,l) link
			SentenceAssignment child = assn.clone();
			child.setCurrentEdgeLabel(k, l);
			sucessor.add(child);
		}
		return sucessor;
	}

	/**
	 * given the current state, expand its sucessors for trigger labeling
	 * @param problem 
	 * @param root_assn
	 * @param successor
	 * @param i the index of the current token to be processed
	 */
	protected List<SentenceAssignment> expandTrigger(SentenceInstance problem, SentenceAssignment root_assn, int i, boolean addIfNotPresent)
	{	
		List<SentenceAssignment> successor = new ArrayList<SentenceAssignment>();
		
		// in this function, rules out words that are not one of (Verb, Noun, Adj)
		if(!TypeConstraints.isPossibleTriggerByPOS(problem, i) || !TypeConstraints.isPossibleTriggerByEntityType(problem, i))
		{
			SentenceAssignment assn = root_assn.clone();
			assn.incrementState();
			assn.setCurrentNodeLabel(SentenceAssignment.Default_Trigger_Label);
			successor.add(assn);
			return successor;
		}
		
		String previousLabel = root_assn.getCurrentNodeLabel();
		List<String> nextLabels = nextLabels(previousLabel);
		
		// traverse all possible target alphabet (trigger labels) for the current token
		for(int j=0; nextLabels != null && j<nextLabels.size(); j++)
		{
			String outcome = nextLabels.get(j);
			// create a new assn as a successor 
			SentenceAssignment assn = root_assn.clone();
			assn.incrementState();
			assn.setCurrentNodeLabel(outcome);
			successor.add(assn);
		}
		// traverse all possible target alphabet (trigger labels) for the current token
		/*
		for(int j=0; j<model.nodeTargetAlphabet.size(); j++)
		{
			// create a new assn as a successor 
			SentenceAssignment assn = root_assn.clone();
			assn.incrementState();
			assn.setCurrentNodeLabel(j);
			successor.add(assn);
		}
		*/	
		return successor;
	}
	
	/**
	 * this is to evaluate the cost (credits/score/probability) of a partial results in the beam search
	 * @param partial
	 * @param problem
	 * @return
	 */
	static protected double evaluate(SentenceAssignment partial, FeatureVector weights)
	{
		partial.updateScoreForNewState(weights);
		return partial.getScore();
	}
	
	/**
	 * get a list of possible next trigger label, this is learnt from all training data
	 * @param previousLabel
	 * @return
	 */
	protected List<String> nextLabels(String previousLabel)
	{
		return model.getLabelBigram().get(previousLabel);
	}
	
	/**
	 * check if ace mention is compatible with the event type and argument role in the
	 * current hypothesis
	 * @param edgeLabel 
	 * @param type
	 * @param currentNodeLabel
	 * @return
	 */
	protected static boolean isRoleCompatible(String mention_type, String triggerType, String edgeLabel)
	{
		if(edgeLabel.equals(SentenceAssignment.Default_Argument_Label) || 
				(TypeConstraints.isRoleCompatible(triggerType, edgeLabel) && TypeConstraints.isEntityTypeCompatible(edgeLabel, mention_type)))
		{
			return true;
		}
		return false;
	}
	
	public static class ScoreComparator implements Comparator<SentenceAssignment>
	{
		@Override
		public int compare(SentenceAssignment assn1, SentenceAssignment assn2)
		{
			if(Math.abs(assn1.getScore() - assn2.getScore()) < 0.00001)
			{
				return 0;
			}
			if(assn1.getScore() > assn2.getScore())
			{
				return -1;
			}
			else 
			{
				return 1;
			}
		}	
	}
}
