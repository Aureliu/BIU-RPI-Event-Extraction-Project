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
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.cuny.qc.perceptron.core.Evaluator.Score;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.FeatureVector;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.perceptron.types.SignalInstance;
import edu.cuny.qc.perceptron.types.SignalType;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.mechanism.BrownClustersSignalMechanism;
import edu.cuny.qc.scorer.mechanism.DependencySignalMechanism;
import edu.cuny.qc.scorer.mechanism.POSSignalMechanism;
import edu.cuny.qc.scorer.mechanism.PlainSignalMechanism;
import edu.cuny.qc.scorer.mechanism.WordNetSignalMechanism;
import edu.cuny.qc.util.TokenAnnotations;
import edu.cuny.qc.util.UnsupportedParameterException;
import edu.cuny.qc.util.Utils;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;


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
	public static final String LOG_NAME_ID = "ODIE";

	// the alphabet of node labels (trigger labels)
	//public Alphabet nodeTargetAlphabet;	
	// the alphabet of the label for each edge (trigger-->argument link), shared by the whole application
	// they should be consistent with SentenceInstance object
	//public Alphabet edgeTargetAlphabet;
	// the alphabet of features, shared by the whole application
	public Alphabet featureAlphabet;
	// the settings of the perceptron
	public static Controller controller = new Controller();
	
	// label bigram
	//private Map<String, List<String>> labelBigram;
	
	// the weights of features, however, 
	protected FeatureVector weights;
	protected FeatureVector avg_weights;
	protected FeatureVector avg_weights_base; // for average weights update
	
	public transient List<SignalMechanism> signalMechanisms = new ArrayList<SignalMechanism>();
	
	
	public Set<ScorerData> triggerScorers = new LinkedHashSet<ScorerData>();
	public Set<ScorerData> argumentScorers = new LinkedHashSet<ScorerData>();
	
	public static int iter=-1; // num of current iteration - public and static for logging
	public static int i; // num of current sentence - public and static for logging
	public static boolean inEarlyUpdate=false; //DEBUG
	
	// default constructor 
	public Perceptron(Alphabet featureAlphabet) throws SignalMechanismException
	{
//		this.nodeTargetAlphabet = nodeTargetAlphabet;
//		this.edgeTargetAlphabet = edgeTargetAlphabet;
		this.featureAlphabet = featureAlphabet;
		
		// create weights vector
		this.setWeights(new FeatureVector());
		this.avg_weights_base = new FeatureVector();
		
		//labelBigram = new HashMap<String, List<String>>();
		
		buildSignalMechanisms();
	}
	
	public void close() {
		for (SignalMechanism signalMechanism : signalMechanisms) {
			signalMechanism.close();
		}
	}
	
	public void buildSignalMechanisms() throws SignalMechanismException {
			signalMechanisms = new ArrayList<SignalMechanism>();
		
//		try {
			
			signalMechanisms.add(new PlainSignalMechanism(this));
			signalMechanisms.add(new WordNetSignalMechanism(this));
			signalMechanisms.add(new BrownClustersSignalMechanism(this));
			signalMechanisms.add(new POSSignalMechanism(this));
			signalMechanisms.add(new DependencySignalMechanism(this));
			
//		} catch (UnsupportedPosTagStringException e) {
//			throw new SignalMechanismException(e);
//		} catch (WordNetInitializationException e) {
//			throw new SignalMechanismException(e);
//		}
			for (SignalMechanism mechanism : signalMechanisms) {
				triggerScorers.addAll(mechanism.scorers.get(SignalType.TRIGGER));
			}
	}
		
	public void logSignalMechanismsPreSentence() {
		for (SignalMechanism signalMechanism : signalMechanisms) {
			signalMechanism.logPreSentence();
		}
	}
	public void logSignalMechanismsPreDocument() {
		for (SignalMechanism signalMechanism : signalMechanisms) {
			signalMechanism.logPreDocument();
		}
	}
	public void logSignalMechanismsPreDocumentBunch() {
		for (SignalMechanism signalMechanism : signalMechanisms) {
			signalMechanism.logPreDocumentBunch();
		}
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
	
	private String getSignalScore(SentenceAssignment assn, Integer i, String featureName) {
		String result;
		String strippedFeatureName = SentenceAssignment.stripLabel(featureName);
		if (strippedFeatureName.contains(SentenceAssignment.BIAS_FEATURE)) { //TODO: horrible, horrible hack. Shold have a more generic treatment for features that are known not to exist for non-O situations (or just "not exist sometimes").
			return "BIAS";
		}
		if (!assn.signalsToValues.containsKey(i)) {
			throw new IllegalArgumentException("Cannot find featureToSignal map for index: "+i+" in assignment: " + assn.toString());
		}
		Map<String, String> map = assn.signalsToValues.get(i);
		if (!map.containsKey(strippedFeatureName)) {
			String msg = "Cannot find feature (stripped) '"+strippedFeatureName+"' for i="+i+" in assignment: " + assn.toString();
			//throw new IllegalArgumentException(msg);
			//System.err.println(msg); //an even worse hack! cause I have no idea why we get this exception.
			return "-";
		}
		String signals = map.get(strippedFeatureName);
		if (signals == null) {
			result = "N/A";
		}
		else {
//			List<String> strs = new ArrayList<String>(signals.size());
//			for (SignalInstance signal : signals) {
//				strs.add(signal.getPositiveString());
//			}
//			if (signals.size() == 1) {
//				result = strs.get(0);
//			}
//			else {
//				result = strs.toString();
//			}
			result = signals;
		}
		return result;
	}

	
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
	
	public static <T> void add(Map<T, BigDecimal> map, T key, BigDecimal toAdd) {
		BigDecimal currVal = map.get(key);
		if (currVal == null) {
			currVal = BigDecimal.ZERO;
		}
		BigDecimal newVal = currVal.add(toAdd);
		map.put(key, newVal);
	}
	
	public static <S,T> void add(Map<S, Map<T, BigDecimal>> map, S key1, T key2, BigDecimal toAdd) {
		Map<T, BigDecimal> mapInner = map.get(key1);
		if (mapInner == null) {
			mapInner = Maps.newLinkedHashMap();
			map.put(key1, mapInner);
		}
		add(mapInner, key2, toAdd);
	}
	
	public static void addAccordingly(Map<Object, BigDecimal> mapAssn, Map<String, Map<String, BigDecimal>> forSignalName,
			String label, String tCategory, String fCatefory, String featureName) {
		BigDecimal value = mapAssn.get(featureName);
		if (value!=null && value.compareTo(BigDecimal.ZERO)!=0) {
			add(forSignalName, label, tCategory, mapAssn.get(featureName));
		}
		else {
			add(forSignalName, label, fCatefory, BigDecimal.ONE);
		}
	}
	
	public static void addAccordingly(Map<Object, BigDecimal> mapAssn, Map<String, BigDecimal> forSignalName,
			String tCategory, String fCatefory, String featureName) {
		BigDecimal value = mapAssn.get(featureName);
		if (value!=null && value.compareTo(BigDecimal.ZERO)!=0) {
			add(forSignalName, tCategory, value);
		}
		else {
			add(forSignalName, fCatefory, BigDecimal.ONE);
		}
	}
	
	public static <T> void addSummary(List<String> summary, Map<T, BigDecimal> map, String category) {
		BigDecimal result = map.get(category);
		if (result != null && result.compareTo(BigDecimal.ZERO) > 0) {
			summary.add(category);
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
	
//	public void learning(List<SentenceInstance> trainingList, List<SentenceInstance> devList, int cutoff) {
//		learning(trainingList, devList, cutoff, null);
//	}
	
	
	/**
	 * given an training instance list, and max number of iterations, learn weights by perceptron
	 * in each iteration, use current weights to test the dev instance list, and in each peak, save the model to file
	 * @param trainingList
	 * @param maxIter
	 */
	public void learning(List<SentenceInstance> trainingList, List<SentenceInstance> devList, int cutoff)
	{	
		// the evaluator for dev set
		Evaluator evaluator = null;
		if(controller.evaluatorType == 0)
		{
			throw new UnsupportedParameterException("evaluatorType == 0");
		}
		else
		{
			evaluator = new EvaluatorLoose();
		}
		
//		fillLabelBigrams();
		
		BeamSearch beamSearcher = createBeamSearcher(this, true);
		
		System.out.print("Alphabet size: " + this.featureAlphabet.size() + "\t");
//		System.out.println("Node target alphabet:" + this.nodeTargetAlphabet);
//		System.out.println("edge target alphabet:" + this.edgeTargetAlphabet);
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
				"target-signal",
				"target",
				"assn-size",
				"assn-signal",
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
			if (controller.logLevel >= 3) {
				u = new PrintStream(updatesOutputFilePath);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Utils.print(u, "", "\n", "|", null,			
				"Iter",
				"DocID:SentenceNo",
				"Tokens",
				"TokensProcessed",
				"Signal",
				"Label",
				"W:LBL:P+",
				"W:LBL:P-",
				"W:O:P+",
				"W:O:P-",
				"AnyChange",
				"C:P+",
				"C:P-",
				"C:LBL:P+",
				"C:LBL:P-",
				"C:O:P+",
				"C:O:P-",
				"Summary",

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
		BigDecimal c = BigDecimal.ZERO; // for averaged parameter
		for(iter=0; iter<this.controller.maxIterNum; iter++)
		{
			long startTime = System.currentTimeMillis();	
			int error_num = 0;	
			/*int*/ i=0;
			int countNoViolation = 0;
			for(SentenceInstance instance : trainingList)
			{
				SentenceAssignment assn = beamSearcher.beamSearch(instance, controller.beamSize, true);
				// for averaged parameter
				if(this.controller.avgArguments)
				{
					c = c.add(BigDecimal.ONE);
				}
				weights.updates = Maps.newHashMap();
				if(assn.getViolate())
				{
					inEarlyUpdate = true;
					earlyUpdate(assn, instance.target, c);
					error_num ++;
					inEarlyUpdate = false;
				}
				else {
					//System.out.printf("  %d. No violation! (iter=%d) assn: %s\n", countNoViolation+1, iter, assn.toString());
					countNoViolation += 1;
				}
				
				//DEBUG
				String sentText = sentence(instance.textStart);
				//printf(w, "|%s%d|%s\n", wt.getFeaturesStringSkip(), i, wt.getFeaturesString());
				printWeights(w, iter, instance.docID, instance.sentInstID, c, instance.size(), sentText);


				// {SignalName : {Label : {Category : AmountInSentence}}}
				Map<String, Map<String, Map<String, BigDecimal>>> amounts = Maps.newTreeMap();

				// {SignalName: {Category : AmountInSentence}} - oof and oot ignore labels
				Map<String, Map<String, BigDecimal>> amountsOO = Maps.newTreeMap();

				List<Map<Class<?>, Object>> tokens = (List<Map<Class<?>, Object>>) instance.get(InstanceAnnotations.Token_FEATURE_MAPs);
				for (int j=0; j<instance.size(); j++) {
					String lemma = (String) tokens.get(j).get(TokenAnnotations.LemmaAnnotation.class);
					Set<Object> allFeaturesSet = new HashSet<Object>();
					Map<Object, BigDecimal> mapTarget = instance.target.getFeatureVectorSequence().get(j).getMap();
					
					String assnLabel = "X";
					Map<Object, BigDecimal> mapAssn = new HashMap<Object, BigDecimal>();
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
						
						String targetSignal = getSignalScore(instance.target, j, s);
						String assnSignal = getSignalScore(assn, j, s);
						
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
							Utils.print(f, "", "\n", "|", instance.sentInstID,
									iter,
									"",//instance.docID,
									instance.sentInstID,
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
									targetSignal,
									inTarget,
									"",//mapAssn.size(),
									assnSignal,
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

						Utils.print(f, "", "\n", "|", instance.sentInstID,
								iter,
								instance.docID,
								instance.sentInstID,
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
								"",
								size(mapAssn),
								"",
								"",
								"",
								"",
								"",
								size(weights),
								"",
								""
						);
					}
					
					if (controller.logLevel >= 3 && j<assn.getFeatureVectorSequence().size()) {
						String targetLabel = instance.target.getLabelAtToken(j);
						
						for (String signalName : SentenceAssignment.signalToFeature.keySet()) {
							try {
								String featureName = null;
								if (SentenceAssignment.signalToFeature.get(signalName).containsKey(assnLabel)) {
									// Due to deisgn limitations of the table, all data under categories (eg LBL,O,T)
									// would refer only to P+. P- won't be mentioned there (but is basically the opposite of the third portion).
									featureName = SentenceAssignment.signalToFeature.get(signalName).get(assnLabel).get("P+");
								}
								// {label : {category : amount}}
								Map<String, Map<String, BigDecimal>> forSignalName = amounts.get(signalName);
								if (forSignalName == null) {
									forSignalName = Maps.newLinkedHashMap();
									amounts.put(signalName, forSignalName);
								}
								Map<String, BigDecimal> forSignalNameOO = amountsOO.get(signalName);
								if (forSignalNameOO == null) {
									forSignalNameOO = Maps.newLinkedHashMap();
									amountsOO.put(signalName, forSignalNameOO);
								}
								
								boolean targetIsO = targetLabel.equals(SentenceAssignment.PAD_Trigger_Label);
								boolean assnIsO = assnLabel.equals(SentenceAssignment.PAD_Trigger_Label);
								
								/// DEBUG
//								if (instance.sentInstID.equals("5a")) {
//									System.err.printf("\n\n\n5a\n\n\n");
//								}
								///
								
								if (targetIsO && assnIsO) {
									addAccordingly(mapAssn, forSignalNameOO, "O,O,T", "O,O,F", featureName);
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
							catch (RuntimeException e) {
								throw new RuntimeException(
										String.format("Exception in inst=%s, j=%s, targetLabel=%s, assnLabel=%s sig=%s.", instance, j, targetLabel, assnLabel, signalName), e);
							}
						}
					}
				}
				
				if (controller.logLevel >= 3) {
					for (String signalName : SentenceAssignment.signalToFeature.keySet()) {
						Map<String, Map<String, BigDecimal>> forSignalName = amounts.get(signalName);
						Map<String, BigDecimal> forSignalNameOO = amountsOO.get(signalName);
						for (String label : forSignalName.keySet()) {
							try {
								Map<String, BigDecimal> forLabel = forSignalName.get(label);
								
								// Explaining this horrible convention: pp=P+ (p plus), pm=P- (p minus)
								// and if you get a similar weird null regarding O (and not some LBL), then maybe do an if for it as well :)
								String featureNameLBLpp="";
								String featureNameLBLpm="";
								String featureNameOpp="";
								String featureNameOpm="";
								BigDecimal weightLBLpp=BigDecimal.ZERO;
								BigDecimal weightLBLpm=BigDecimal.ZERO;
								BigDecimal weightOpp=BigDecimal.ZERO;
								BigDecimal weightOpm=BigDecimal.ZERO;
								String weightLBLStrpp="X";
								String weightLBLStrpm="X";
								String weightOStrpp="X";
								String weightOStrpm="X";
								BigDecimal changeLBLpp=BigDecimal.ZERO;
								BigDecimal changeLBLpm=BigDecimal.ZERO;
								BigDecimal changeOpp=BigDecimal.ZERO;
								BigDecimal changeOpm=BigDecimal.ZERO;
								String changeLBLStrpp="X";
								String changeLBLStrpm="X";
								String changeOStrpp="X";
								String changeOStrpm="X";
								
								// this is a rather subtle point - we have this feature only if makeFeature() was called on it,
								// but it must also be in weight (makeFeature() could have been called on an assn that
								// was eventually not the one chosen in the beam)
								boolean featureLBLppExists = false;
								if (SentenceAssignment.signalToFeature.get(signalName).get(label) != null &&
									SentenceAssignment.signalToFeature.get(signalName).get(label).get("P+") != null) {
									featureNameLBLpp = SentenceAssignment.signalToFeature.get(signalName).get(label).get("P+");
									weightLBLpp = weights.get(featureNameLBLpp);
									if (weightLBLpp!=null) {
										featureLBLppExists = true;
										weightLBLStrpp = FMT.format(weightLBLpp);
									}
								}
								boolean featureLBLpmExists = false;
								if (SentenceAssignment.signalToFeature.get(signalName).get(label) != null &&
									SentenceAssignment.signalToFeature.get(signalName).get(label).get("P-") != null) {
									featureNameLBLpm = SentenceAssignment.signalToFeature.get(signalName).get(label).get("P-");
									weightLBLpm = weights.get(featureNameLBLpm);
									if (weightLBLpm!=null) {
										featureLBLpmExists = true;
										weightLBLStrpm = FMT.format(weightLBLpm);
									}
								}
								boolean featureOppExists = false;
								if (SentenceAssignment.signalToFeature.get(signalName).get(SentenceAssignment.PAD_Trigger_Label) != null &&
									SentenceAssignment.signalToFeature.get(signalName).get(SentenceAssignment.PAD_Trigger_Label).get("P+") != null) {
									featureNameOpp = SentenceAssignment.signalToFeature.get(signalName).get(SentenceAssignment.PAD_Trigger_Label).get("P+");
									weightOpp = weights.get(featureNameOpp);
									if (weightOpp!=null) {
										featureOppExists = true;
										weightOStrpp = FMT.format(weightOpp);
									}
								}
								boolean featureOpmExists = false;
								if (SentenceAssignment.signalToFeature.get(signalName).get(SentenceAssignment.PAD_Trigger_Label) != null &&
									SentenceAssignment.signalToFeature.get(signalName).get(SentenceAssignment.PAD_Trigger_Label).get("P-") != null) {
									featureNameOpm = SentenceAssignment.signalToFeature.get(signalName).get(SentenceAssignment.PAD_Trigger_Label).get("P-");
									weightOpm = weights.get(featureNameOpm);
									if (weightOpm!=null) {
										featureOpmExists = true;
										weightOStrpm = FMT.format(weightOpm);
									}
								}

								// we are only marking "change", if it happened to both features in the same token
								// otherwise, most-likely O was changed with a different LBL, so we don't want to show it in this row
								// (if we would, it would make weird distortions, like showing there was change even though
								// only none-change categories are on)
								if ((featureLBLppExists && featureOppExists) || (featureLBLpmExists && featureOpmExists)) {
									for (Map<Object, BigDecimal> featureChangesOnSameToken : weights.updates.values()) {
										if (featureChangesOnSameToken.containsKey(featureNameLBLpp) && featureChangesOnSameToken.containsKey(featureNameOpp)) {
											changeLBLpp = featureChangesOnSameToken.get(featureNameLBLpp);
											changeOpp = featureChangesOnSameToken.get(featureNameOpp);
											changeLBLStrpp = FMT.format(changeLBLpp);
											changeOStrpp = FMT.format(changeOpp);
										}
										if (featureChangesOnSameToken.containsKey(featureNameLBLpm) && featureChangesOnSameToken.containsKey(featureNameOpm)) {
											changeLBLpm = featureChangesOnSameToken.get(featureNameLBLpm);
											changeOpm = featureChangesOnSameToken.get(featureNameOpm);
											changeLBLStrpm = FMT.format(changeLBLpm);
											changeOStrpm = FMT.format(changeOpm);
										}
									}
								}
								
								boolean changePp = (changeLBLpp!=BigDecimal.ZERO || changeOpp!=BigDecimal.ZERO);
								boolean changePm = (changeLBLpm!=BigDecimal.ZERO || changeOpm!=BigDecimal.ZERO);
								boolean anyChange = (changePp || changePm);
								String changePpStr = changePp?"T":"F";
								String changePmStr = changePm?"T":"F";
								String anyChangeStr = anyChange?"T":"F";
								
								List<String> summary = Lists.newArrayList();
								addSummary(summary, forSignalNameOO, "O,O,F");
								addSummary(summary, forSignalNameOO, "O,O,T");
								addSummary(summary, forLabel, "LBL,O,T");
								addSummary(summary, forLabel, "O,LBL,F");
								addSummary(summary, forLabel, "LBL,LBL,T");
								addSummary(summary, forLabel, "O,LBL,T");
								addSummary(summary, forLabel, "LBL,O,F");
								addSummary(summary, forLabel, "LBL,LBL,F");
								addSummary(summary, forLabel, "weird,weird,F");
								addSummary(summary, forLabel, "weird,weird,T");
							
								Utils.print(u, "", "\n", "|", null,
										iter, //Iter
										String.format("%s:%s", instance.docID, instance.sentInstID), //"DocID:SentenceNo"
										instance.target.getState(),//"Tokens"
										assn.getState(),//"TokensProcessed"
										signalName, //"SignalName"
										label, //"Label"
										
										weightLBLStrpp, //"Weight:LBL:P+"
										weightLBLStrpm, //"Weight:LBL:P-"
										weightOStrpp, //"Weight:O:P+"
										weightOStrpm, //"Weight:O:P-"
										anyChangeStr, //"AnyChange"
										changePpStr, //
										changePmStr,
										changeLBLStrpp, // "Change:LBL:P+"
										changeLBLStrpm, // "Change:LBL:P-"
										changeOStrpp, // "Change:O:P+"
										changeOStrpm, // "Change:O:P-"
										String.format("Change=%s:%s*%s", changePpStr, changePmStr, summary), //"Summary"

										forSignalNameOO.get("O,O,F"),
										forLabel.get("LBL,O,T"),
										forLabel.get("O,LBL,F"),
										forLabel.get("LBL,LBL,T"),
										forLabel.get("O,LBL,T"),
										forLabel.get("LBL,O,F"),
										forSignalNameOO.get("O,O,T"),
										forLabel.get("LBL,LBL,F"),
										forLabel.get("weird,weird,F"),
										forLabel.get("weird,weird,T")
										
								);
							}
							catch (RuntimeException e) {
								throw new RuntimeException(String.format("Exception for instance=%s, sig=%s, label=%s", instance, signalName, label), e);
							}
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
				
				if (!controller.useArguments) {
					//System.out.printf("Since useArguments, switching harmonic_mean=%s with trigger_F1=%s\n", dev_score.harmonic_mean, dev_score.trigger_F1);
					dev_score.harmonic_mean = dev_score.trigger_F1;
				}


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
	private void makeAveragedWeights(BigDecimal c)
	{
		this.avg_weights = new FeatureVector();
		for(Object feat : this.weights.getMap().keySet())
		{
			BigDecimal value = this.weights.get(feat); // w_0
			BigDecimal value_a = this.avg_weights_base.get(feat); // w_a
			BigDecimal quotient = value_a.divide(c, MathContext.DECIMAL128);
			value = value.subtract(quotient); //value - value_a / c; // w_0 - w_a/c
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

//	private void fillDefaultLabelBigrams(String singleEventType) {
//		if (singleEventType != null) {
//			getLabelBigram().put(singleEventType,                      Arrays.asList(new String[] {SentenceAssignment.PAD_Trigger_Label, singleEventType}));
//			getLabelBigram().put(SentenceAssignment.PAD_Trigger_Label, Arrays.asList(new String[] {SentenceAssignment.PAD_Trigger_Label, singleEventType}));
//		}
//		else {
//			List<String> allTypes = new ArrayList<String>(TypeConstraints.eventTypeMap.keySet());
//			allTypes.add(0, SentenceAssignment.PAD_Trigger_Label);
//			String currType = null;
//			for (int i=0; i<allTypes.size(); i++) {
//				currType = allTypes.get(i);
//				List<String> list = new ArrayList<String>(allTypes);
//				list.remove(i);
//				getLabelBigram().put(currType, list);
//			}
//		}
//	}
//
//	/**
//	 * After each type of trigger, can appear any other type of trigger. Default label ("O") included.
//	 */
//	public void fillLabelBigrams() {
//		List<String> allTypes = new ArrayList<String>(TypeConstraints.specTypes);
//		allTypes.add(0, SentenceAssignment.PAD_Trigger_Label);
//		String currType = null;
//		for (int i=0; i<allTypes.size(); i++) {
//			currType = allTypes.get(i);
//			List<String> list = new ArrayList<String>(allTypes);
//			//list.remove(i);
//			getLabelBigram().put(currType, list);
//		}
//	}
	
//	protected void extractTriggerLabelBigrams(List<SentenceInstance> traininglist)
//	{
//		for(SentenceInstance instance : traininglist)
//		{
//			SentenceAssignment target = instance.target;
//			String prev = SentenceAssignment.PAD_Trigger_Label;
//			for(int i=0; i<target.getNodeAssignment().size(); i++)
//			{
//				Integer index = target.getNodeAssignment().get(i);
//				String label = (String) this.nodeTargetAlphabet.lookupObject(index);
//				
//				List<String> list = getLabelBigram().get(prev);
//				if(list == null)
//				{
//					list = new ArrayList<String>();
//				}
//				if(!list.contains(label))
//				{
//					list.add(label);
//				}
//				getLabelBigram().put(prev, list);
//				prev = label;
//			}
//		}
//	}

	/**
	 * given an assignment, and the gold-standard, update the weights
	 * @param assn
	 * @param target
	 * @param c 
	 * @return return true if it's updated, i.e. the assn is not correct
	 */
	protected void earlyUpdate(SentenceAssignment assn, SentenceAssignment target, BigDecimal c)
	{
		// the beam search may return a early assignment, and we only update the prefix
		for(int i=0; i <= assn.getState(); i++)
		{
			// weights = \phi(y*) - \phi(y)
			this.getWeights().addDelta(target.featVecSequence.get(i), assn.featVecSequence.get(i), BigDecimal.ONE, i);
			//this.getWeights().addDelta(target.featVecSequence.get(i), assn.featVecSequence.get(i), 1.0);
			
			if(this.controller.avgArguments)
			{
				this.avg_weights_base.addDelta(target.featVecSequence.get(i), assn.featVecSequence.get(i), c, i);
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

//	protected Map<String, List<String>> getLabelBigram()
//	{
//		return labelBigram;
//	}
	
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
