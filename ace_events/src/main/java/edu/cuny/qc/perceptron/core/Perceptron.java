package edu.cuny.qc.perceptron.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;

import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.FeatureVector;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.util.TypeConstraints;


/**
 * This class implements the learning/decoding part of perceptron, 
 * as well as serialization/deserialization
 * @author che
 *
 */
public class Perceptron implements java.io.Serializable
{
	private static final long serialVersionUID = -8870655270637917361L;

	// the alphabet of node labels (trigger labels)
	public Alphabet nodeTargetAlphabet;	
	// the alphabet of the label for each edge (trigger-->argument link), shared by the whole application
	// they should be consistent with SentenceInstance object
	public Alphabet edgeTargetAlphabet;
	// the alphabet of features, shared by the whole application
	public Alphabet featureAlphabet;
	// the settings of the perceptron
	public Controller controller = new Controller();
	
	// label bigram
	private Map<String, List<String>> labelBigram;
	
	// the weights of features, however, 
	protected FeatureVector weights;
	protected FeatureVector avg_weights;
	protected FeatureVector avg_weights_base; // for average weights update
	
	// default constructor 
	public Perceptron(Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet)
	{
		this.nodeTargetAlphabet = nodeTargetAlphabet;
		this.edgeTargetAlphabet = edgeTargetAlphabet;
		this.featureAlphabet = featureAlphabet;
		
		// create weights vector
		this.setWeights(new FeatureVector());
		this.avg_weights_base = new FeatureVector();
		
		labelBigram = new HashMap<String, List<String>>();
	}
	
	// default constructor 
	public Perceptron(Controller controller)
	{
		this.nodeTargetAlphabet = new Alphabet();
		this.edgeTargetAlphabet = new Alphabet();
		this.featureAlphabet = new Alphabet();
		this.controller = controller;
		
		// create weights vector
		this.setWeights(new FeatureVector());
		this.avg_weights_base = new FeatureVector();
		labelBigram = new HashMap<String, List<String>>();
	}
	
	/**
	 *  given an instanceList, decode, and give the best assignmentList
	 * @param instance
	 * @return
	 */
	public List<SentenceAssignment> decoding(List<? extends SentenceInstance> instanceList)
	{
		List<SentenceAssignment> ret = new ArrayList<SentenceAssignment>();
		BeamSearch beamSearcher = new BeamSearch(this, false);
		if(this.controller.updateType == 1)
		{
			beamSearcher = new BeamSearchStandard(this, false);
		}
		for(SentenceInstance inst : instanceList)
		{
			SentenceAssignment assn = beamSearcher.beamSearch(inst, controller.beamSize, false);
			ret.add(assn);
			
			// DEBUG
			// SentenceAssignment assn1 = beamSearcher.beamSearch(inst, 1, false);
			// SentenceAssignment assn4 = beamSearcher.beamSearch(inst, 2, false);
			// if(assn1.getScore() > assn4.getScore())
			// {
			//	System.out.println("ERROR: " + " large beam size has smaller model score " + instanceList.indexOf(inst));
			// }
			// END DEBUG
		}
		return ret;
	}
	
//	public void learning(List<SentenceInstance> trainingList, int maxIter)
//	{
//		learning(trainingList, null, 0);
//	}
	
	public void learning(List<SentenceInstance> trainingList, List<SentenceInstance> devList, int cutoff) {
		learning(trainingList, devList, cutoff, null);
	}
	
	/**
	 * given an training instance list, and max number of iterations, learn weights by perceptron
	 * in each iteration, use current weights to test the dev instance list, and in each peak, save the model to file
	 * @param trainingList
	 * @param maxIter
	 */
	public void learning(List<SentenceInstance> trainingList, List<SentenceInstance> devList, int cutoff, String singleEventType)
	{	
		// the evaluator for dev set
		Evaluator evaluator = null;
		if(controller.evaluatorType == 0)
		{
			evaluator = new EvaluatorFinal();
		}
		else
		{
			evaluator = new EvaluatorLoose();
		}
		
		if (controller.learnBigrams) {
			// traverse the training instance to get the trigger label bigram
			extractTriggerLabelBigrams(trainingList);
		}
		else {
			fillDefaultLabelBigrams(singleEventType);
		}
		
		BeamSearch beamSearcher = createBeamSearcher(this, true);
		
		System.out.print("Alphabet size: " + this.featureAlphabet.size() + "\t");
		System.out.println("Node target alphabet:" + this.nodeTargetAlphabet);
		System.out.println("edge target alphabet:" + this.edgeTargetAlphabet);
		System.out.println("instance num: " + trainingList.size());
		
		// feature cutoff
		if(cutoff > 0)
		{
			featureCutOff(trainingList, cutoff);
		}
		
		// online learning with beam search and early update
		long totalTime = 0;
		Evaluator.Score max_score = new Evaluator.Score();
		int best_iter = 0;
		FeatureVector best_weights = null;
		FeatureVector best_avg_weights = null;
		int iter = 0;
		double c = 0; // for averaged parameter
		for(iter=0; iter<this.controller.maxIterNum; iter++)
		{
			long startTime = System.currentTimeMillis();	
			int error_num = 0;	
			int i=0;
			for(SentenceInstance instance : trainingList)
			{
				SentenceAssignment assn = beamSearcher.beamSearch(instance, controller.beamSize, true);
				// for averaged parameter
				if(this.controller.avgArguments)
				{
					c++;
				}
				if(assn.getViolate())
				{
					earlyUpdate(assn, instance.target, c);
					error_num ++;
				}
				i++;
			}
			
			long endTime = System.currentTimeMillis();
			long iterTime = endTime - startTime;
			totalTime += iterTime;
			System.out.println("\nIter " + iter + "\t error num: " + error_num + "\t time:" + iterTime + "\t feature size:" + this.weights.size());
			
			// use current weight to decode and evaluate developement instances
			if(devList != null)
			{
				makeAveragedWeights(c);
				
				//TODO DEBUG
				List<SentenceAssignment> goldAssignments = new ArrayList<SentenceAssignment>(devList.size());
				for (SentenceInstance instance : devList) {
					goldAssignments.add(instance.target);
				}
				/// TODO END DEBUG
				
				List<SentenceAssignment> devResult = decoding(devList);
				Evaluator.Score dev_score = evaluator.evaluate(devResult, getCanonicalInstanceList(devList));
				
				System.out.println("Dev " + dev_score);

				if((dev_score.harmonic_mean - max_score.harmonic_mean) >= 0.001)
				{
					// dump the model as best one
					best_weights = this.weights.clone();
					if(this.controller.avgArguments)
					{
						best_avg_weights = this.avg_weights.clone();
					}
					best_iter = iter;
					max_score = dev_score;
				}
			}
			
			if(error_num == 0)
			{
				// converge
				break;
			}
			
			// print out num of invalid update
			if(beamSearcher instanceof BeamSearchStandard)
			{
				((BeamSearchStandard) beamSearcher).print_num_update(System.out);
			}
		}
		
		if(iter < this.controller.maxIterNum)
		{
			// converge
			System.out.println("converge in iter " + iter + "\t time:" + totalTime);
			iter++;
		}
		else
		{
			// stop without convergency
			System.out.println("Stop without convergency" + "\t time:" + totalTime);
		}
		
		if(devList != null && best_weights != null)
		{
			this.weights = best_weights;
			if(this.controller.avgArguments)
			{
				this.avg_weights = best_avg_weights;
			}
			System.out.println("best performance on dev set: iter " + best_iter + " :" + max_score);
		}
		else if(this.controller.avgArguments)
		{
			makeAveragedWeights(c);
		}
		
		// print out num of invalid update
		if(beamSearcher instanceof BeamSearchStandard)
		{
			((BeamSearchStandard) beamSearcher).print_num_update(System.out);
		}
		
		return;
	}
	
	protected List<SentenceInstance> getCanonicalInstanceList(
			List<? extends SentenceInstance> devList)
	{
		return (List<SentenceInstance>) devList;
	}

	protected BeamSearch createBeamSearcher(Perceptron perceptron, boolean b)
	{
		if(this.controller.updateType == 1)
		{
			return new BeamSearchStandard(this, true);
		}
		else
		{
			return new BeamSearch(this, true);
		}
	}

	/**
	 *  w_0 - w_a/c (w_0 is standard weights, w_a is the base of averaged weights) 	
	 * @param c
	 */
	private void makeAveragedWeights(double c)
	{
		this.avg_weights = new FeatureVector();
		for(Object feat : this.weights.getMap().keySet())
		{
			double value = this.weights.get(feat); // w_0
			double value_a = this.avg_weights_base.get(feat); // w_a
			value = value - value_a / c; // w_0 - w_a/c
			this.avg_weights.add(feat, value);
		}
	}

	/**
	 * given a cutoff threshold, cut the features off when the number of occurrence 
	 * is < cutoff
	 * @param trainingList
	 */
	private void featureCutOff(List<SentenceInstance> trainingList, int cutoff)
	{
		if(cutoff <=0)
		{
			return;
		}
		Map<Object, Integer> countMap = new HashMap<Object, Integer>();
		for(SentenceInstance inst : trainingList)
		{
			for(FeatureVector fv : inst.target.featVecSequence.getSequence())
			{
				for(Object key : fv.getMap().keySet())
				{
					Integer freq = countMap.get(key);
					if(freq == null)
					{
						freq = 0;
					}
					freq++;
					countMap.put(key, freq);
				}
			}
		}
		Alphabet newFeatAlphabet = new Alphabet(this.featureAlphabet.size());
		for(Object feat : countMap.keySet())
		{
			Integer freq = countMap.get(feat);
			if(freq > cutoff)
			{
				newFeatAlphabet.lookupIndex(feat, true);
			}
		}
		this.featureAlphabet = newFeatAlphabet;
		// update featureAlphabet
		for(SentenceInstance inst : trainingList)
		{
			inst.featureAlphabet = newFeatAlphabet;
			inst.target.featureAlphabet = newFeatAlphabet;
		}
	}

	private void fillDefaultLabelBigrams(String singleEventType) {
		if (singleEventType != null) {
			getLabelBigram().put(singleEventType,                      Arrays.asList(new String[] {SentenceAssignment.PAD_Trigger_Label, singleEventType}));
			getLabelBigram().put(SentenceAssignment.PAD_Trigger_Label, Arrays.asList(new String[] {SentenceAssignment.PAD_Trigger_Label, singleEventType}));
		}
		else {
			List<String> allTypes = new ArrayList<String>(TypeConstraints.eventTypeMap.keySet());
			allTypes.add(0, SentenceAssignment.PAD_Trigger_Label);
			String currType = null;
			for (int i=0; i<allTypes.size(); i++) {
				currType = allTypes.get(i);
				List<String> list = new ArrayList<String>(allTypes);
				list.remove(i);
				getLabelBigram().put(currType, list);
			}
		}
	}


	protected void extractTriggerLabelBigrams(List<SentenceInstance> traininglist)
	{
		for(SentenceInstance instance : traininglist)
		{
			SentenceAssignment target = instance.target;
			String prev = SentenceAssignment.PAD_Trigger_Label;
			for(int i=0; i<target.getNodeAssignment().size(); i++)
			{
				Integer index = target.getNodeAssignment().get(i);
				String label = (String) this.nodeTargetAlphabet.lookupObject(index);
				
				List<String> list = getLabelBigram().get(prev);
				if(list == null)
				{
					list = new ArrayList<String>();
				}
				if(!list.contains(label))
				{
					list.add(label);
				}
				getLabelBigram().put(prev, list);
				prev = label;
			}
		}
	}

	/**
	 * given an assignment, and the gold-standard, update the weights
	 * @param assn
	 * @param target
	 * @param c 
	 * @return return true if it's updated, i.e. the assn is not correct
	 */
	protected void earlyUpdate(SentenceAssignment assn, SentenceAssignment target, double c)
	{
		// the beam search may return a early assignment, and we only update the prefix
		for(int i=0; i <= assn.getState(); i++)
		{
			// weights = \phi(y*) - \phi(y)
			this.getWeights().addDelta(target.featVecSequence.get(i), assn.featVecSequence.get(i), 1.0);
			
			if(this.controller.avgArguments)
			{
				this.avg_weights_base.addDelta(target.featVecSequence.get(i), assn.featVecSequence.get(i), c);
			}
		}
	}
	
	public void setWeights(FeatureVector weights)
	{
		this.weights = weights;
	}

	public FeatureVector getWeights()
	{
		return weights;
	}

	protected void setLabelBigram(Map<String, List<String>> labelBigram)
	{
		this.labelBigram = labelBigram;
	}

	protected Map<String, List<String>> getLabelBigram()
	{
		return labelBigram;
	}
	
	/**
	 * serialize the model (mainly weights/alphabets) to the file
	 * @param modelFile
	 */
	static public void serializeObject(Serializable model, File modelFile)
	{
		try
		{
			OutputStream stream = new FileOutputStream(modelFile);
			SerializationUtils.serialize(model, stream);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * deserialize a saved model from a file
	 * @param modelFile
	 * @return
	 */
	public static Perceptron deserializeObject(File modelFile)
	{
		Perceptron model = null;
		try
		{
			InputStream stream = new FileInputStream(modelFile);
			model = (Perceptron) SerializationUtils.deserialize(stream);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return model;
	}

	public FeatureVector getAvg_weights()
	{
		return avg_weights;
	}
}
