package edu.cuny.qc.perceptron.types;

import java.util.ArrayList;
import java.util.List;

import edu.cuny.qc.perceptron.core.Controller;

/**
 * This is an instance of a cluster of sentences
 * one cluster at least should have one sent
 * @author che
 *
 */
public class ClusterInstance extends SentenceInstance
{
	/**
	 * The cluster of instance, corresponds to cluster of sents
	 */
	List<SentenceInstance> instanceCluster = new ArrayList<SentenceInstance>();
	
	public ClusterInstance(List<Sentence> sentCluster, Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet,
			Controller controller, boolean learnable)
	{
		super(nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, learnable);
		
		// initiates sentence instance in cluster
		for(Sentence sent : sentCluster)
		{
			SentenceInstance instance = new SentenceInstance(sent, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, learnable);
			instanceCluster.add(instance);
		}
		
		this.target = new ClusterAssignment(this);
	}
	
	public List<SentenceInstance> getInstances()
	{
		return instanceCluster;
	}
	
	public SentenceInstance getInst(int i)
	{
		return this.instanceCluster.get(i);
	}
	
	public int size()
	{
		return instanceCluster.size();
	}
	
	/**
	 * check if the assignment is correct up to current assn.getState()
	 * @param assn
	 * @return
	 */
	public boolean violateClusterGoldStandard(ClusterAssignment assn)
	{
		// if there isn't "target" in this, that means this is not for learning
		if(target == null)
		{
			return false;
		}
		return !assn.equals(this.target, this.target.state);
	}
	
	/**
	 * compare a set of assignments with gold standard 
	 * @param beam
	 * @return true if violation false if not violation 
	 */
	public boolean violateClusterGoldStandard(List<ClusterAssignment> beam)
	{
		for(ClusterAssignment assn : beam)
		{
			if(assn.equals(this.target, assn.state))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * check if the correct solution appears in the beam, up to the state in the beam, 
	 * and the entityIndex in the beam
	 * If entityIndex == -1, then only consider the trigger labeling
	 * @param beam
	 * @param entityIndex
	 * @return
	 */
	public boolean violateClusterGoldStandard(List<ClusterAssignment> beam, int entityIndex)
	{
		for(ClusterAssignment assn : beam)
		{
			boolean correct = true;
			ClusterAssignment target = (ClusterAssignment) this.target;
			for(int i=0; i<=assn.state; i++)
			{
				SentenceAssignment sentAssn = assn.get(i);
				SentenceAssignment sentTarget = target.get(i);
				if(i < assn.state)
				{
					if(!sentAssn.equals(sentTarget, sentAssn.getState()))
					{
						correct = false;
						break;
					}
				}
				else if(!sentAssn.equals(sentTarget, sentAssn.getState(), entityIndex)) 
				{
					correct = false;
					break;
				}
			}
			if(correct)
			{
				return false; // not violate
			}
		}
		return true; // violate
	}
}
