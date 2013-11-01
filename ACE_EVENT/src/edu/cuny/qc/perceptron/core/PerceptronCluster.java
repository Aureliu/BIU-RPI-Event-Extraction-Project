package edu.cuny.qc.perceptron.core;

import java.util.ArrayList;
import java.util.List;

import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.ClusterAssignment;
import edu.cuny.qc.perceptron.types.ClusterInstance;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;

/**
 * This class implements the cross-sent perceptron
 * @author che
 *
 */
public class PerceptronCluster extends Perceptron
{
	private static final long serialVersionUID = 3357898831861628387L;
	
	/**
	 * specify beam search type, BeamSearchCluster or BeamSearchClusterSeq
	 */
	Class<? extends BeamSearch> beamSearchType = BeamSearchCluster.class;
	
	/**
	 * by default, use BeamSearchCluster
	 * @param nodeTargetAlphabet
	 * @param edgeTargetAlphabet
	 * @param featureAlphabet
	 */
	public PerceptronCluster(Alphabet nodeTargetAlphabet,
			Alphabet edgeTargetAlphabet, Alphabet featureAlphabet)
	{
		this(nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, BeamSearchCluster.class);
	}
	
	public PerceptronCluster(Alphabet nodeTargetAlphabet,
			Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, Class<? extends BeamSearch> beamSearchType)
	{
		super(nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet);
		this.beamSearchType = beamSearchType;
	}
	
	/**
	 *  given an instanceList, decode, and give the best assignmentList
	 * @param instance
	 * @return
	 */
	public List<SentenceAssignment> decoding(List<? extends SentenceInstance> instanceList)
	{
		List<SentenceAssignment> ret = new ArrayList<SentenceAssignment>();
		BeamSearch beamSearcher = createBeamSearcher(this, false);
	
		for(SentenceInstance inst : instanceList)
		{
			ClusterInstance clusterInst = (ClusterInstance) inst;
			// make search on the cluster basis, convert the result to individual sents
			ClusterAssignment assn = (ClusterAssignment) beamSearcher.beamSearch(clusterInst, controller.beamSize, false);
			for(SentenceAssignment sentAssn : assn.getAssns())
			{
				ret.add(sentAssn);
			}
		}
		return ret;
	}
	
	protected void earlyUpdate(SentenceAssignment assn, SentenceAssignment target, double c)
	{
		ClusterAssignment clusterAssn = (ClusterAssignment) assn;
		ClusterAssignment clusterTarget = (ClusterAssignment) target;
		
		if(clusterAssn.getIndividualViolate())
		{
			// only early update individual assignments which violate
			for(int sent_id=0; sent_id<clusterAssn.size(); sent_id++)
			{
				SentenceAssignment sentAssn = clusterAssn.get(sent_id);
				if(sentAssn == null)
				{
					continue;
				}
				SentenceAssignment sentTarget = clusterTarget.get(sent_id);
				if(sentAssn.getViolate())
				{
					// the beam search may return a early assignment, and we only update the prefix
					for(int i=0; i <= sentAssn.getState(); i++)
					{
						// weights = \phi(y*) - \phi(y)
						this.getWeights().addDelta(sentTarget.featVecSequence.get(i), sentAssn.featVecSequence.get(i), 1.0);
						if(this.controller.avgArguments)
						{
							this.avg_weights_base.addDelta(sentTarget.featVecSequence.get(i), sentAssn.featVecSequence.get(i), c);
						}
					}
				}
			}
			return;
		}
		
		// update weights for individual sent
		for(int sent_id=0; sent_id<clusterAssn.size(); sent_id++)
		{
			SentenceAssignment sentAssn = clusterAssn.get(sent_id);
			SentenceAssignment sentTarget = clusterTarget.get(sent_id);
			// the beam search may return a early assignment, and we only update the prefix
			if(!sentAssn.equals(sentTarget))
			{
				for(int i=0; i <= sentAssn.getState(); i++)
				{
					// weights = \phi(y*) - \phi(y)
					this.getWeights().addDelta(sentTarget.featVecSequence.get(i), sentAssn.featVecSequence.get(i), 1.0);
					if(this.controller.avgArguments)
					{
						this.avg_weights_base.addDelta(sentTarget.featVecSequence.get(i), sentAssn.featVecSequence.get(i), c);
					}
				}
			}
		}
		
		// update weights for cluster
		// weights = \phi(y*) - \phi(y)
		this.getWeights().addDelta(clusterTarget.getFv(), clusterAssn.getFv(), 1.0);
		if(this.controller.avgArguments)
		{
			this.avg_weights_base.addDelta(clusterTarget.getFv(), clusterAssn.getFv(), c);
		}
	}
	
	protected void extractTriggerLabelBigrams(List<SentenceInstance> traininglist)
	{
		List<SentenceInstance> list = new ArrayList<SentenceInstance>();
		for(SentenceInstance inst: traininglist)
		{
			ClusterInstance clusterInst = (ClusterInstance) inst;
			list.addAll(clusterInst.getInstances());
		}
		super.extractTriggerLabelBigrams(list);
	}
	
	protected BeamSearch createBeamSearcher(Perceptron perceptron, boolean b)
	{
		BeamSearch beamSearcher = null;
		if(this.beamSearchType.equals(BeamSearchCluster.class))
		{
			beamSearcher = new BeamSearchCluster(this, b);
		}
		else
		{
			beamSearcher = new BeamSearchClusterSeq(this, b);
		}
		return beamSearcher;
	}
	
	protected List<SentenceInstance> getCanonicalInstanceList(
			List<? extends SentenceInstance> devList)
	{
		List<SentenceInstance> list = new ArrayList<SentenceInstance>();
		for(SentenceInstance inst : devList)
		{
			ClusterInstance clusterInst = (ClusterInstance) inst;
			list.addAll(clusterInst.getInstances());
		}
		return list;
	}
}
