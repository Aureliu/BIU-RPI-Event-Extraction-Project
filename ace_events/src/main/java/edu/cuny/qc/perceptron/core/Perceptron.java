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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

import edu.cuny.qc.perceptron.core.Evaluator.Score;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.FeatureVector;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.util.TokenAnnotations;
import edu.cuny.qc.util.TypeConstraints;
import edu.cuny.qc.util.UnsupportedParameterException;
import edu.cuny.qc.util.Utils;


/**
 * This class implements the learning/decoding part of perceptron, 
 * as well as serialization/deserialization
 * @author che
 *
 */
public class Perceptron implements java.io.Serializable
{
	private static final long serialVersionUID = -8870655270637917361L;
	
	public static final DecimalFormat FMT = new DecimalFormat("#.###"); //("#.####") //("#.#####")
	public static final String POST_ITERATION_MARK = "PostItr";
	public static final String LOG_NAME_ID = "master";

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
	
	public static int iter; // num of current iteration - public and static for logging
	public static int i; // num of current sentence - public and static for logging
	
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
	
	
	public static String size(FeatureVector fv) {
		if (fv == null) {
			return "X";
		}
		else {
			return ((Integer) fv.size()).toString();
			
		}
	}
	
	public static String size(Map<?, ?> map) {
		if (map == null) {
			return "X";
		}
		else {
			return ((Integer) map.size()).toString();
			
		}
	}
	
	public static String str(FeatureVector fv, String key) {
		if (fv == null) {
			return "X";
		}
		Object val = fv.get(key);
		if (val != null) {
			return FMT.format(val);
		}
		else {
			return "X";
		}
	}
	
	public static String str(Map<?, ?> map, String key) {
		if (map == null) {
			return "X";
		}
		Object val = map.get(key);
		if (val != null) {
			return FMT.format(val);
		}
		else {
			return "X";
		}
	}
	
	public static String twoLabels(SentenceAssignment assn1, SentenceAssignment assn2, int j) {
		return twoLabels(assn1, assn2.getLabelAtToken(j), j);
	}
	
	public static String twoLabels(SentenceAssignment assn1, String assn2Label, int j) {
		return String.format("%s,%s", assn1.getLabelAtToken(j), assn2Label);
	}
	
	public static String twoLabelsAndScore(SentenceAssignment assn1, SentenceAssignment assn2, int j, Map<?, ?> map, String key) {
		return twoLabelsAndScore(assn1, assn2.getLabelAtToken(j), j, str(map, key));
	}
	
	public static String twoLabelsAndScore(SentenceAssignment assn1, String assn2Label, int j, String assnSignal) {
		return String.format("%s,%s", twoLabels(assn1, assn2Label, j), assnSignal);
	}
	
	public static String values(FeatureVector fv) {
		if (fv == null) {
			return "X";
		}
		else {
			return fv.toStringOnlyValues();
		}
	}
	
	public static String feature(String featureName) {
		return featureName.replace('|', '*').replaceAll("\\s+", " ").replace("BigramFeature: ", "B:").replace("EdgeLocalFeature: ", "E:")
				.replace("NodeLevelGlobalFeature: ","GN:").replace("SentLevelGlobalFeature: ","GS:").replace("TriggerLevelGlobalFeature: ","GT:");
	}
	
	public static String lemma(String lemma) {
		final int MAX_CHARS = 10;
		if (lemma.length() > MAX_CHARS) {
			return lemma.substring(0, MAX_CHARS-1) + "+";
		}
		else {
			return lemma;
		}
	}
	
	public static String assn(SentenceAssignment assn) {
		// the minimum output out of these two methods is eventually taken
		final int MAX_CHARS = 120;
		final int MAX_NODES = 20;
		String ret = assn.toString(MAX_NODES);
		if (ret.length() > MAX_CHARS) {
			ret = ret.substring(0, MAX_CHARS-1) + "+";
		}
		return ret;
	}
	
	public static String sentence(String sentence) {
		final int MAX_CHARS = 100;
		String ret = sentence.replace('\n', ' ');
		if (ret.length() > MAX_CHARS) {
			return ret.substring(0, MAX_CHARS-1) + "+";
		}
		else {
			return ret;
		}
	}
	
//	public static String docid(String id) {
//		File f = new File(id);
//		return f.getName();
//	}
	
	public static <T> void add(Map<T, Double> map, T key, Double toAdd) {
		Double currVal = map.get(key);
		if (currVal == null) {
			currVal = 0.0;
		}
		Double newVal = currVal + toAdd;
		map.put(key, newVal);
	}
	
	public static <S,T> void add(Map<S, Map<T, Double>> map, S key1, T key2, Double toAdd) {
		Map<T, Double> mapInner = map.get(key1);
		if (mapInner == null) {
			mapInner = Maps.newLinkedHashMap();
			map.put(key1, mapInner);
		}
		add(mapInner, key2, toAdd);
	}
	
	public static void addAccordingly(Map<Object, Double> mapAssn, Map<String, Map<String, Double>> forSignalName,
			String label, String tCategory, String fCatefory, String featureName) {
		if (mapAssn.containsKey(featureName)) {
			add(forSignalName, label, tCategory, mapAssn.get(featureName));
		}
		else {
			add(forSignalName, label, fCatefory, 1.0);
		}

	}
	private void printWeights(PrintStream out, Object iter, Object docId, Object sentenceNo, Object c, Object tokens, Object sentenceText) {
		if (  (controller.logLevel >= 7 && sentenceNo.equals(POST_ITERATION_MARK))   ||
			  (controller.logLevel >= 8)  ) {
			List<String> featureNames = new ArrayList<String>();
			for (Object feat : this.weights.getMap().keySet()) {
				featureNames.add((String) feat);
			}
			Collections.sort(featureNames);
			for (String name : featureNames) {
				Utils.print(out, "", "\n", "|", sentenceNo.toString(),			
						iter,
						docId,
						sentenceNo,
						c,
						//tokens,
						//sentenceText,
						"",
						"",
						feature(name),
						str(weights, name),
						str(avg_weights_base, name),
						str(avg_weights, name),
						"",//size(weights),
						"",//size(avg_weights_base),
						"" //size(avg_weights)
				);
			}
		}
		if (controller.logLevel >= 3) {
			Utils.print(out, "", "\n", "|", sentenceNo.toString(),
					iter,
					docId,
					sentenceNo,
					c,
					//tokens,
					//sentenceText,
					values(weights),
					values(avg_weights),
					"",
					"",
					"",
					"",
					size(weights),
					size(avg_weights_base),
					size(avg_weights)
			);
		}
	}
	
	private void printScore(PrintStream out, String iter, int devSize, Score score) {
		Utils.print(out, "", "\n", "|", null, 
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
			throw new UnsupportedParameterException("cutoff > 0");
		}
		
		//DEBUG
		//WeightTracer wt = new WeightTracer(this);
		String weightsOutputFilePath = Pipeline.modelFile.getParent() + "/AllWeights-" + LOG_NAME_ID + "." + controller.logLevel + ".tsv";
		PrintStream w = null;
		try {
			if (controller.logLevel >= 3) {
				w = new PrintStream(weightsOutputFilePath);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		//printf(w, "Iter|%sSentenceNo|%s\n", wt.getFeaturesStringTitle(), wt.getFeaturesStringTitle());
		Utils.print(w, "", "\n", "|", null,			
				"Iter",
				"DocID",
				"SentenceNo",
				"c",
				//"Tokens",
				//"Sentence"
				"Weights",
				"AvgWeights",
				"Feature",
				"Weight",
				"BaseWeight",
				"AvgWeight",
				"len-Weights",
				"len-BaseWeights",
				"len-AvgWeights"
		);

		String featuresOutputFilePath = Pipeline.modelFile.getParent() + "/AllFeatures-" + LOG_NAME_ID + "." + controller.logLevel + ".tsv";
		PrintStream f = null;
		try {
			if (controller.logLevel >= 2) {
				f = new PrintStream(featuresOutputFilePath);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Utils.print(f, "", "\n", "|", null,			
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
				"both-labels",
				"Feature",
				"target-size",
				"target",
				"assn-size",
				"assn",
				"labels+assn",
				"in-both",
				"same-score",
				"weights-size",
				"weights",
				"avg_weights"
		);
		
		String updatesOutputFilePath = Pipeline.modelFile.getParent() + "/AllUpdates-" + LOG_NAME_ID + "." + controller.logLevel + ".tsv";
		PrintStream u = null;
		try {
			if (controller.logLevel >= 4) {
				u = new PrintStream(updatesOutputFilePath);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
//		String noTrigger = SentenceAssignment.PAD_Trigger_Label;
//		String updatesLogTriggerLabel = "TRG";
//		if (singleEventType != null) {
//			updatesLogTriggerLabel = (String) this.nodeTargetAlphabet.lookupObject(1);
//			if (updatesLogTriggerLabel.equals(noTrigger)) {
//				throw new IllegalStateException("Somehow got " + noTrigger + " as the *second* trigger label...");
//			}
//		}
		Utils.print(u, "", "\n", "|", null,			
				"Iter",
				"DocID:SentenceNo",
				"Signal",
				"Label",
				"Weight:LBL",
				"Weight:O",
				"AnyChange",
				"Change:LBL",
				"Change:O",

				"O,O,F",
				"LBL,O,T",
				"O,LBL,F",
				"LBL,LBL,T",
				"O,LBL,T",
				"LBL,O,F",
				"O,O,T",
				"LBL,LBL,F",
				"weird,weird,T",
				"weird,weird,F"
		);
		
		String devOutputFilePath = Pipeline.modelFile.getParent() + "/DevPerformance-" + LOG_NAME_ID + "." + controller.logLevel + ".tsv";
		PrintStream d = null;
		try {
			if (controller.logLevel >= 1) {
				d = new PrintStream(devOutputFilePath);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Utils.print(d, "", "\n", "|", null,				
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
		/*int*/ iter = 0;
		double c = 0; // for averaged parameter
		for(iter=0; iter<this.controller.maxIterNum; iter++)
		{
			long startTime = System.currentTimeMillis();	
			int error_num = 0;	
			/*int*/ i=0;
			int countNoViolation = 0;
			FeatureVector.updates = Maps.newHashMap();
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
				String sentText = sentence(instance.text);
				//printf(w, "|%s%d|%s\n", wt.getFeaturesStringSkip(), i, wt.getFeaturesString());
				printWeights(w, iter, instance.docID, i, c, instance.size(), sentText);


				// {SignalName : {Label : {SignalValue : AmountInSentence}}}
				Map<String, Map<String, Map<String, Double>>> amounts = Maps.newTreeMap();
//				Map<String, Map<String, Map<String, Double>>> amountsTarget = Maps.newTreeMap();
//				Map<String, Map<String, Map<String, Double>>> amountsAssn = Maps.newTreeMap();
				Double oof = 0.0;
				Double oot = 0.0;

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
						String inTarget = str(mapTarget, s);
						String inAssn = str(mapAssn, s);
						String inWeights = str(weights, s);
						String inAvg = str(avg_weights, s);
						
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
						
						if (controller.logLevel >= 4) {
							Utils.print(f, "", "\n", "|", i,
									iter,
									"",//instance.docID,
									i,
									c,
									instance.size(),
									"",//sentText,
									j,
									lemma(lemma),
									instance.target.getLabelAtToken(j),
									assnLabel,
									twoLabels(instance.target, assnLabel, j),
									feature(s),
									"",//mapTarget.size(),
									inTarget,
									"",//mapAssn.size(),
									inAssn,
									twoLabelsAndScore(instance.target, assnLabel, j, inAssn),
									bothTargetAndAssn,
									sameTargetAndAssn,
									"",//weights.size(),
									inWeights,
									inAvg
							);
						}
					}
					if (controller.logLevel >= 2) {

						Utils.print(f, "", "\n", "|", i,
								iter,
								instance.docID,
								i,
								c,
								instance.size(),
								sentText,
								j,
								lemma(lemma),
								instance.target.getLabelAtToken(j),
								assnLabel,
								twoLabels(instance.target, assnLabel, j),
								"",
								size(mapTarget),
								"",
								size(mapAssn),
								"",
								"",
								"",
								"",
								size(weights),
								"",
								""
						);
					}
					
					if (controller.logLevel >= 4 && j<assn.getFeatureVectorSequence().size()) {
						String targetLabel = instance.target.getLabelAtToken(j);
						for (String signalName : SentenceAssignment.signalCategoryFeature.keySet()) {
							String featureName = SentenceAssignment.signalCategoryFeature.get(signalName).get(assnLabel).get(null);
							// {label : {category : amount}}
							Map<String, Map<String, Double>> forSignalName = amounts.get(signalName);
							if (forSignalName == null) {
								forSignalName = Maps.newLinkedHashMap();
								amounts.put(signalName, forSignalName);
							}
							
							boolean targetIsO = targetLabel.equals(SentenceAssignment.PAD_Trigger_Label);
							boolean assnIsO = assnLabel.equals(SentenceAssignment.PAD_Trigger_Label);
							
							if (targetIsO && assnIsO) {
								if (mapAssn.containsKey(featureName)) {
									oot += mapAssn.get(featureName);
								}
								else {
									oof += 1.0;
								}
							}
							else if (targetIsO && !assnIsO) {
								addAccordingly(mapAssn, forSignalName, assnLabel, "O,LBL,T", "O,LBL,F", featureName);
							}
							else if (!targetIsO && assnIsO) {
								addAccordingly(mapAssn, forSignalName, targetLabel, "LBL,O,T", "LBL,O,F", featureName);
							}
							else if (!targetIsO && !assnIsO) {
								if (targetLabel.equals(assnLabel)) {
									addAccordingly(mapAssn, forSignalName, targetLabel, "LBL,LBL,T", "LBL,LBL,F", featureName);
								}
								else {
									addAccordingly(mapAssn, forSignalName, assnLabel, "weird,weird,T", "weird,weird,F", featureName);
									addAccordingly(mapAssn, forSignalName, targetLabel, "weird,weird,T", "weird,weird,F", featureName);
								}
							}
						}
					}
				}
				
				if (controller.logLevel >= 4) {
					for (String signalName : SentenceAssignment.signalCategoryFeature.keySet()) {
						Map<String, Map<String, Double>> forSignalName = amounts.get(signalName);
						for (String label : forSignalName.keySet()) {
							Map<String, Double> forLabel = forSignalName.get(label);
							String featureNameLBL = SentenceAssignment.signalCategoryFeature.get(signalName).get(label).get(null);
							String featureNameO = SentenceAssignment.signalCategoryFeature.get(signalName).get(SentenceAssignment.PAD_Trigger_Label).get(null);
							Double changeLBL = FeatureVector.updates.get(featureNameLBL); 
							Double changeO = FeatureVector.updates.get(featureNameO); 
							boolean anyChange = (changeLBL+changeO!=0);
						
							Utils.print(u, "", "\n", "|", null,
									iter, //Iter
									String.format("%s:%s", instance.docID, i), //"DocID:SentenceNo"
									signalName, //"SignalName"
									label, //"Label"
									
									weights.get(featureNameLBL), //"Weight:LBL"
									weights.get(featureNameO), //"Weight:O"
									anyChange, //"AnyChange"
									changeLBL, // "Change:LBL"
									changeO, // "Change:O"
									
									oof,
									forLabel.get("LBL,O,T"),
									forLabel.get("O,LBL,F"),
									forLabel.get("LBL,LBL,T"),
									forLabel.get("O,LBL,T"),
									forLabel.get("LBL,O,F"),
									oot,
									forLabel.get("LBL,LBL,F"),
									forLabel.get("weird,weird,F"),
									forLabel.get("weird,weird,T")								
							);
						}
					}
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
				printWeights(w, iter, "", POST_ITERATION_MARK, c, "", "");
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
			System.out.printf("%s Finished iteration %s\n", Pipeline.detailedLog(), iter);
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
			System.out.println(Pipeline.detailedLog() + " converge in iter " + iter + "\t time:" + totalTime);
			lastIter = String.format("Best(iter=%s, converged)", best_iter);
			iter++;
		}
		else
		{
			// stop without convergency
			System.out.println(Pipeline.detailedLog() + " Stop without convergency" + "\t time:" + totalTime);
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
		printWeights(w, lastIter, "", POST_ITERATION_MARK, c, "", "");

		
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
