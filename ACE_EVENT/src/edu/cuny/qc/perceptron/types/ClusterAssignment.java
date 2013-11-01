package edu.cuny.qc.perceptron.types;

import java.util.ArrayList;
import java.util.List;

import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.featureGenerator.CrossSentFeatureGenerator;

public class ClusterAssignment extends SentenceAssignment
{
	/**
	 * The cluster (sequential) of assignments
	 * this corresponds to cluster instance
	 */
	public List<SentenceAssignment> clusterAssn = new ArrayList<SentenceAssignment>();
	
	/**
	 * this feature vector is the fv that shared by all instances in the cluster
	 */
	FeatureVector sharedFv = new FeatureVector();

	boolean individualViolate = false;
	
	/**
	 * this is a signal showing that individual sentence assignment violates
	 * therefore the weights should be early updated
	 * @param violate
	 */
	public void setIndividualViolate(boolean violate)
	{
		this.individualViolate = violate;
	}
	
	public boolean getIndividualViolate()
	{
		return this.individualViolate;
	}
	
	public ClusterAssignment(Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, Controller controller)
	{
		super(nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller);
	}
	
	public ClusterAssignment(ClusterInstance clusterInstance)
	{
		super(clusterInstance.nodeTargetAlphabet, clusterInstance.edgeTargetAlphabet, 
				clusterInstance.featureAlphabet, clusterInstance.controller);
		
		for(SentenceInstance inst : clusterInstance.getInstances())
		{
			this.add(inst.target);
			this.state++;
		}
	}
	
	public void add(SentenceAssignment assn)
	{
		clusterAssn.add(assn);
	}
	
	public SentenceAssignment get(int sent_id)
	{
		return clusterAssn.get(sent_id);
	}
	
	public List<SentenceAssignment> getAssns()
	{
		return this.clusterAssn;
	}

	public FeatureVector getFv()
	{
		return sharedFv;
	}
	
	public ClusterAssignment clone()
	{
		ClusterAssignment ret = new ClusterAssignment(nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller);
		// shallow copy of individual sent assignments
		ret.clusterAssn.addAll(this.clusterAssn);
		// deep copy fv
		ret.sharedFv = this.sharedFv.clone();
		ret.score = this.score;
		ret.state = this.state;
		return ret;
	}
	
	public double getScore()
	{
		double ret = 0.0;
		for(SentenceAssignment assn : clusterAssn)
		{
			ret += assn.getScore();
		}
		ret += this.score;
		return ret;
	}
	
	public void clearFeatureVectors()
	{
		this.sharedFv = new FeatureVector();
	}
	
	/**
	 * make cross-sent features on sent_level
	 * @param clusterInstance
	 * @param state the last sentence in the current configuration
	 */
	public void makeCrossSentFeatures(ClusterInstance clusterInstance, int sent_id)
	{
		List<String> features = CrossSentFeatureGenerator.makeCrossSentFeature(clusterInstance, this, sent_id);  
		for(String feature : features)
		{
			String featureStr = "CrossSentFeature:\t" + feature;
			this.getFv().add(featureStr, 1.0);
		}
	}

	/**
	 * make cross-sent features on a newly added node (token) 
	 * @param clusterInstance
	 * @param sent_id the id of the last sent in the configuration
	 * @param token_id token id of the last sent
	 */
	public void makeCrossSentFeaturesTrigger(ClusterInstance clusterInstance,
			int sent_id, int token_id)
	{
		List<String> features = CrossSentFeatureGenerator.makeCrossSentFeatureTrigger(clusterInstance, this, sent_id, token_id);  
		for(String feature : features)
		{
			String featureStr = "CrossSentFeatureTrigger:\t" + feature;
			this.getFv().add(featureStr, 1.0);
		}
	}
	
	/**
	 * make cross-sent features on a newly added node (token)
	 * @param clusterInstance
	 * @param sent_id the id of the last sent in the configuration
	 * @param token_id token id of the last sent
	 * @param argNum the index of the argument mention
	 */
	public void makeCrossSentFeaturesArg(ClusterInstance clusterInstance,
			int sent_id, int token_id, int argNum)
	{
		List<String> features = CrossSentFeatureGenerator.makeCrossSentFeatureArg(clusterInstance, this, sent_id, token_id, argNum);  
		for(String feature : features)
		{
			String featureStr = "CrossSentFeatureArg:\t" + feature;
			this.getFv().add(featureStr, 1.0);
		}
	}
	
	public int size()
	{
		return this.clusterAssn.size();
	}
	
	/**
	 * check if two assignments equals to each other before (inclusive) state step
	 * @param target
	 * @param state2
	 */
	public boolean equals(SentenceAssignment assn, int step)
	{
		if(!(assn instanceof ClusterAssignment))
		{
			return false;
		}
		ClusterAssignment clusterAssn = (ClusterAssignment) assn;
		for(int i=0; i<=step && i<=clusterAssn.state && i<=this.state; i++)
		{
			if(!clusterAssn.get(i).equals(this.get(i)))
			{
				return false;
			}
		}
		return true;
	}
	
	public void incrementState()
	{
		this.state++;
	}
	
	public void updateScoreForNewState(FeatureVector weights)
	{
		this.score = 0;
		for(SentenceAssignment sentAssn : clusterAssn)
		{
			this.score += sentAssn.getScore();
		}
		this.score += weights.dotProduct(sharedFv);
	}
	
	public String toString()
	{
		return this.clusterAssn.toString();
	}
}
