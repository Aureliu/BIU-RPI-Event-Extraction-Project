/* This is a sequence of feature vectors (adapted from Mallet)*/

package edu.cuny.qc.perceptron.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeatureVectorSequence implements Serializable
{
	private static final long serialVersionUID = 1061851977260208018L;

	List<FeatureVector> sequence = new ArrayList<FeatureVector>();

	public FeatureVectorSequence()
	{
		;
	}
	
	public List<FeatureVector> getSequence()
	{
		return sequence;
	}
	
	/**
	 * only deep copy the last featureVector
	 * @return
	 */
	public FeatureVectorSequence clone2()
	{
		FeatureVectorSequence fvs = new FeatureVectorSequence();
		for(int i=0; i<sequence.size(); i++)
		{
			FeatureVector fv = sequence.get(i);
			if(i < sequence.size() - 1)
			{
				fvs.add(fv);
			}
			else
			{
				FeatureVector clone_fv = fv.clone();
				fvs.add(clone_fv);
			}
		}
		return fvs;
	}
	
	// deep copy FeatureVectorSequence
	public FeatureVectorSequence clone()
	{
		FeatureVectorSequence fvs = new FeatureVectorSequence();
		for(FeatureVector fv : sequence)
		{
			FeatureVector clone_fv = fv.clone();
			fvs.add(clone_fv);
		}
		return fvs;
	}
	
	public FeatureVectorSequence (Collection<FeatureVector> featureVectors)
	{
		this.sequence.addAll(featureVectors);
	}

	public int size()
	{
		return sequence.size();
	}
	
	public void add(FeatureVector fv)
	{
		sequence.add(fv);
	}
	
	public void set(int i, FeatureVector fv)
	{
		sequence.set(i, fv);
	}
	
	public FeatureVector get(int i)
	{
		return sequence.get(i);
	}

	public String toString ()
	{
		StringBuffer sb = new StringBuffer ();
		sb.append (super.toString());
		sb.append ('\n');
		for (int i = 0; i < sequence.size(); i++) {
			sb.append (Integer.toString(i)+": ");
			sb.append (sequence.get(i).toStringFull(true));
			sb.append ('\n');
		}
		return sb.toString();
	}

}
