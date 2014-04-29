package edu.cuny.qc.perceptron.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import edu.cuny.qc.perceptron.core.Evaluator.Score;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.FeatureVector;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.Sentence.Sent_Attribute;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.util.TokenAnnotations;
import edu.cuny.qc.util.TypeConstraints;
import edu.cuny.qc.util.UnsupportedParameterException;
import edu.cuny.qc.util.Utils;
import edu.cuny.qc.util.WeightTracer;


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
//	public Perceptron(Controller controller)
//	{
//		this.nodeTargetAlphabet = new Alphabet();
//		this.edgeTargetAlphabet = new Alphabet();
//		this.featureAlphabet = new Alphabet();
//		this.controller = controller;
//		
//		// create weights vector
//		this.setWeights(new FeatureVector());
//		this.avg_weights_base = new FeatureVector();
//		labelBigram = new HashMap<String, List<String>>();
//	}
//	
	
	
	private String size(FeatureVector fv) {
		if (fv == null) {
			return "null vector";
		}
		else {
			return ((Integer) fv.size()).toString();
			
		}
	}
	
	private String str(FeatureVector fv, String key) {
		if (fv == null) {
			return "null vector";
		}
		Object val = fv.get(key);
		if (val != null) {
			return val.toString();
		}
		else {
			return "X";
		}
	}
	
	private String feature(String featureName) {
		return featureName.replace('|', '*').replace("\t", "  ");
	}
	
	private void printWeights(PrintStream out, Object iter, Object docId, Object sentenceNo, Object c, Object tokens, Object sentenceText) {
		List<String> featureNames = new ArrayList<String>();
		for (Object feat : this.weights.getMap().keySet()) {
			featureNames.add((String) feat);
		}
		Collections.sort(featureNames);
		for (String name : featureNames) {
			Utils.print(out, "", "\n", "|",					
					iter,
					//docId,
					sentenceNo,
					c,
					//tokens,
					//sentenceText,
					feature(name),
					str(weights, name),
					str(avg_weights_base, name),
					str(avg_weights, name),
					size(weights),
					size(avg_weights_base),
					size(avg_weights)
			);
		}
	}
	
	private void printScore(PrintStream out, String iter, int devSize, Score score) {
		Utils.print(out, "", "\n", "|",
				iter, devSize,
				score.count_trigger_gold, score.count_trigger_ans, score.count_trigger_correct,
				score.trigger_precision, score.trigger_recall, score.trigger_F1, 
				score.count_arg_gold, score.count_arg_ans, score.count_arg_correct,
				score.arg_precision, score.arg_recall, score.arg_F1, 
				score.harmonic_mean);
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
			throw new UnsupportedParameterException("updateType == 1");
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
		
		//DEBUG
		WeightTracer wt = new WeightTracer(this);
		String weightsOutputFilePath = Pipeline.modelFile.getParent() + "/AllWeights-matser.tsv";
		PrintStream w = null;
		try {
			w = new PrintStream(weightsOutputFilePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		//printf(w, "Iter|%sSentenceNo|%s\n", wt.getFeaturesStringTitle(), wt.getFeaturesStringTitle());
		Utils.print(w, "", "\n", "|",					
				"Iter",
				//"DocID",
				"SentenceNo",
				"c",
				//"Tokens",
				//"Sentence"
				"Feature",
				"Weight",
				"BaseWeight",
				"AvgWeight",
				"len-Weights",
				"len-BaseWeights",
				"len-AvgWeights"
		);

		String featuresOutputFilePath = Pipeline.modelFile.getParent() + "/AllFeatures-master.tsv";
		PrintStream f = null;
		try {
			f = new PrintStream(featuresOutputFilePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Utils.print(f, "", "\n", "|",					
				"Iter",
				"DocID",
				"SentenceNo",
				"c",
				"Tokens",
				"Sentence",
				"i",
				"Lemma",
				"target-label",
				"assn-label",
				"Feature",
				"target-size",
				"target",
				"assn-size",
				"assn",
				"in-both",
				"same-score",
				"weights-size",
				"weights",
				"avg_weights"
		);
		
		String devOutputFilePath = Pipeline.modelFile.getParent() + "/DevPerformance-master.tsv";
		PrintStream d = null;
		try {
			d = new PrintStream(devOutputFilePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Utils.print(d, "", "\n", "|",					
				"Iter",
				"DevSentences",
				"Trigger-Gold",
				"Trigger-System",
				"Trigger-Correct",
				"Trigger-Precision",
				"Trigger-Recall",
				"Trigger-F1",
				"Arg-Gold",
				"Arg-System",
				"Arg-Correct",
				"Arg-Precision",
				"Arg-Recall",
				"Arg-F1",
				"HarmonicMean-F1"
		);		
		//////////
		
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
			int countNoViolation = 0;
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
				else {
					//System.out.printf("  %d. No violation! (iter=%d) assn: %s\n", countNoViolation+1, iter, assn.toString());
					countNoViolation += 1;
				}
				
				//DEBUG
				String sentText = instance.text.replace('\n', ' ');
				//printf(w, "|%s%d|%s\n", wt.getFeaturesStringSkip(), i, wt.getFeaturesString());
				printWeights(w, iter, instance.docID, i, c, instance.size(), sentText);

				List<Map<Class<?>, Object>> tokens = (List<Map<Class<?>, Object>>) instance.get(InstanceAnnotations.Token_FEATURE_MAPs);
				for (int j=0; j<instance.size(); j++) {
					String lemma = (String) tokens.get(j).get(TokenAnnotations.LemmaAnnotation.class);
					Set<Object> allFeaturesSet = new HashSet<Object>();
					Map<Object, Double> mapTarget = instance.target.getFeatureVectorSequence().get(j).getMap();
					
					String assnLabel = "X";
					Map<Object, Double> mapAssn = new HashMap<Object, Double>();
					if (j<assn.getFeatureVectorSequence().size()) {
						mapAssn = assn.getFeatureVectorSequence().get(j).getMap();
						assnLabel = assn.getLabelAtToken(j);
					}
					
					allFeaturesSet.addAll(mapTarget.keySet());
					allFeaturesSet.addAll(mapAssn.keySet());
					List<String> allFeaturesList = new ArrayList<String>(allFeaturesSet.size());
					for (Object o : allFeaturesSet) {
						allFeaturesList.add((String) o);
					}
					Collections.sort(allFeaturesList);
					for (String s : allFeaturesList) {						
						String inTarget = "X";
						Double numTarget = mapTarget.get(s);
						if (numTarget != null) {
							inTarget = numTarget.toString();
						}
						String inAssn = "X";
						Double numAssn = mapAssn.get(s);
						if (numAssn != null) {
							inAssn = numAssn.toString();
						}
						String inWeights = "X";
						Double numWeights = weights.get(s);
						if (numWeights != null) {
							inWeights = numWeights.toString();
						}
						String inAvg = "X";
						if (avg_weights == null) {
							inAvg = "null vector";
						}
						else {
							Double numAvg = avg_weights.get(s);
							if (numAvg != null) {
								inAvg = numAvg.toString();
							}
						}
						
						String bothTargetAndAssn = null;
						String sameTargetAndAssn = "F";
						if (!inTarget.equals("X") && !inAssn.equals("X")) {
							bothTargetAndAssn = "T";
							if (inTarget.equals(inAssn)) {
								sameTargetAndAssn = "T";
							}
						}
						else {
							bothTargetAndAssn ="F";
						}
						
						Utils.print(f, "", "\n", "|",					
								iter,
								instance.docID,
								i,
								c,
								instance.size(),
								sentText,
								j,
								lemma,
								instance.target.getLabelAtToken(j),
								assnLabel,
								feature(s),
								mapTarget.size(),
								inTarget,
								mapAssn.size(),
								inAssn,
								bothTargetAndAssn,
								sameTargetAndAssn,
								weights.size(),
								inWeights,
								inAvg
						);
					}
					Utils.print(f, "", "\n", "|",					
							iter,
							instance.docID,
							i,
							c,
							instance.size(),
							sentText,
							j,
							lemma,
							instance.target.getLabelAtToken(j),
							assnLabel,
							"",
							mapTarget.size(),
							"",
							mapAssn.size(),
							"",
							"",
							"",
							weights.size(),
							"",
							""
					);
				}
				////////////
				
				i++;
			}
			
			long endTime = System.currentTimeMillis();
			long iterTime = endTime - startTime;
			totalTime += iterTime;
			//System.out.println("\nIter " + iter + "\t error num: " + error_num + "\t time:" + iterTime + "\t feature size:" + this.weights.size());
			
			// use current weight to decode and evaluate developement instances
			if(devList != null)
			{
				makeAveragedWeights(c);
				
				//TODO DEBUG
				printWeights(w, iter, "", "After-Iter", c, "", "");
				/// TODO END DEBUG
				
				List<SentenceAssignment> devResult = decoding(devList);
				Evaluator.Score dev_score = evaluator.evaluate(devResult, getCanonicalInstanceList(devList));
				printScore(d, new Integer(iter).toString(), devList.size(), dev_score);


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
			
			//DEBUG
			//printf(w, "%d|%s\n", iter, wt.getFeaturesString());		
			////////////

			if(error_num == 0)
			{
				// converge
				break;
			}
			
			// print out num of invalid update
//			if(beamSearcher instanceof BeamSearchStandard)
//			{
//				((BeamSearchStandard) beamSearcher).print_num_update(System.out);
//			}
		}
		
		String lastIter = null;
		if(iter < this.controller.maxIterNum)
		{
			// converge
			System.out.println("converge in iter " + iter + "\t time:" + totalTime);
			lastIter = String.format("Best(iter=%s, converged)", best_iter);
			iter++;
		}
		else
		{
			// stop without convergency
			System.out.println("Stop without convergency" + "\t time:" + totalTime);
			lastIter = String.format("Best(iter=%s, NO converge)", best_iter);
		}
		printScore(d, lastIter, devList.size(), max_score);

		
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
		printWeights(w, lastIter, "", "After-Iter", c, "", "");

		
		// print out num of invalid update
//		if(beamSearcher instanceof BeamSearchStandard)
//		{
//			((BeamSearchStandard) beamSearcher).print_num_update(System.out);
//		}
		
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
			throw new UnsupportedParameterException("updateType == 1");
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

//	protected void setLabelBigram(Map<String, List<String>> labelBigram)
//	{
//		this.labelBigram = labelBigram;
//	}

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
