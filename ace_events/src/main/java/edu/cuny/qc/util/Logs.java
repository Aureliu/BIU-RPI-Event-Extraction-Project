package edu.cuny.qc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.ace.acetypes.Scorer.Stats;
import edu.cuny.qc.perceptron.core.AllTrainingScores;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Evaluator.Score;
import edu.cuny.qc.perceptron.folds.Run;
import edu.cuny.qc.perceptron.types.FeatureVector;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;

public class Logs {
	public static final DecimalFormat FMT = new DecimalFormat("#.###"); //("#.####") //("#.#####")
	public static final String POST_ITERATION_MARK = "PostItr";
	public static final String LOG_NAME_ID = "ODIE";
	public static final BigDecimal MINUS_ONE = new BigDecimal("-1"); 
	
	public static final int LEVEL_U = 1;
	public static final int LEVEL_F_1 = 2;
	public static final int LEVEL_F_2 = 4;
	public static final int LEVEL_W_1 = 3;
	public static final int LEVEL_W_2 = 7;
	public static final int LEVEL_W_3 = 8;
	public static final int LEVEL_P = 1;
	public static final int LEVEL_B_1 = 5;
	public static final int LEVEL_B_2 = 6;
	public static final int LEVEL_R = 1;
	public static final int LEVEL_WEIGHTS = 1;
	public static final int LEVEL_MIN_F_U_W = Math.min(Math.min(LEVEL_U, LEVEL_F_1), LEVEL_W_1);
	
	public Controller controller;
	public String logSuffix; 
	public File outFolder;
	
	static {
		System.err.println("??? Logs: Now that I am returning args, I'm still not doing that for logging. I'll only do it on a need-to basis.");
		System.err.println("??? Logs: In Runs.log, the number of gold event triggers coming from Scores is slightly smaller than the one we count in the SentenceInstances. Kinda weird.");
	}
	
	public Logs(File outFolder, Controller controller, String logSuffix) {
		this.controller = controller;
		this.logSuffix = logSuffix;
		this.outFolder = outFolder;
	}
	
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
		return twoLabels(assn1.getLabelAtToken(j), assn2Label);
	}
	
	public static String twoLabels(String assn1Label, String assn2Label) {
		return String.format("%s,%s", assn1Label, assn2Label);
	}
	
	public static String twoLabelsAndScore(SentenceAssignment assn1, SentenceAssignment assn2, int j, Map<?, ?> map, String key) {
		return twoLabelsAndScore(assn1, assn2.getLabelAtToken(j), j, str(map, key));
	}
	
	public static String twoLabelsAndScore(SentenceAssignment assn1, String assn2Label, int j, String assnSignal) {
		return String.format("%s,%s", twoLabels(assn1, assn2Label, j), assnSignal);
	}
	
	public static String twoLabelsAndScore(String assn1Label, String assn2Label, String assnSignal) {
		return String.format("%s,%s", twoLabels(assn1Label, assn2Label), assnSignal);
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
	
	public static String triggerLabel(String triggerLabel) {
		final int MAX_CHARS_ONE_WORD = 5;
		final int MAX_CHARS_MULTI_WORDS = 3;
		final String SEPARATOR = "-";
		if (!triggerLabel.contains(SEPARATOR)) {
			return triggerLabel.substring(0, Math.min(triggerLabel.length(), MAX_CHARS_ONE_WORD));
		}
		else {
			String[] split = triggerLabel.split(SEPARATOR);
			List<String> parts = Lists.newArrayListWithCapacity(split.length);
			for (String s : split) {
				parts.add(s.substring(0, Math.min(s.length(), MAX_CHARS_MULTI_WORDS)));
			}
			return StringUtils.join(parts);
		}
	}
	
	public static String argLabel(String triggerLabel, String argLabel) {
		return String.format("%s.%s", triggerLabel, argLabel);
	}
	
	public static String lemma(String lemma) {
		final int MAX_CHARS = 10;
		return lemma(lemma, MAX_CHARS);
	}
	
	/// DEBUG
	public static String lemma(String lemma, String head, boolean moo) {
		if (!head.isEmpty()) {
			int c=8;
			int g=c+3;
		}
		return lemma(lemma);
	}
	///
	public static String lemma(String lemma, int maxChars) {
		if (lemma == null) {
			return "(null)";
		}
		String s = lemma.replaceAll("\\s+", " ");
		if (s.length() > maxChars) {
			return s.substring(0, maxChars-1) + "+";
		}
		else {
			return s;
		}
	}
	
	public static String assn(SentenceAssignment assn) {
		// the minimum output out of these two methods is eventually taken
		final int MAX_CHARS = 200;
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
	

	public static String labelList(Collection<JCas> types) throws CASException {
		List<String> labels = Lists.newArrayListWithCapacity(types.size());
		for (JCas spec : types) {
			labels.add(SpecAnnotator.getSpecLabel(spec));
		}
		//Collections.sort(labels);
		return StringUtils.join(labels, ",");
	}
	
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
	
	public void printWeights(PrintStream out, Object iter, Object docId, Object sentenceNo, Object c, Object tokens, Object sentenceText,
			FeatureVector weights, FeatureVector avg_weights, FeatureVector avg_weights_base, boolean doLogging) {
		if (  doLogging && out!=null &&
			  (  (controller.logLevel >= Logs.LEVEL_W_2 && sentenceNo.equals(POST_ITERATION_MARK))   ||
			     (controller.logLevel >= Logs.LEVEL_W_3)  )  ) {
			List<String> featureNames = new ArrayList<String>();
			for (Object feat : weights.getMap().keySet()) {
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
		if (controller.logLevel >= Logs.LEVEL_W_1) {
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
	
	public void printScore(PrintStream out, String iter, int numSentences, Score score, boolean doLogging) {
		if (doLogging && out != null) {
			Utils.print(out, "", "\n", "|", null, 
					iter, numSentences,
					score.count_trigger_gold, score.count_trigger_ans, score.count_trigger_correct,
					score.trigger_precision, score.trigger_recall, score.trigger_F1, 
					score.count_arg_gold, score.count_arg_ans, score.count_arg_correct,
					score.arg_precision, score.arg_recall, score.arg_F1, 
					score.harmonic_mean);
		}
	}
	
	public PrintStream getW(String mode) throws FileNotFoundException {
		if (controller.logLevel >= LEVEL_W_1) {
			String weightsOutputFilePath = outFolder.getAbsolutePath() + "/" + mode + "-AllWeights-" + LOG_NAME_ID + "." + controller.logLevel + logSuffix + ".tsv";
			PrintStream w = new PrintStream(weightsOutputFilePath);
			
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
			
			return w;
		}
		return null;
	}
	
	public PrintStream getF(String mode) throws FileNotFoundException {
		if (controller.logLevel >= LEVEL_F_1) {
			String featuresOutputFilePath = outFolder.getAbsolutePath() + "/" + mode + "-AllFeatures-" + LOG_NAME_ID + "." + controller.logLevel + logSuffix + ".tsv";
			PrintStream f = new PrintStream(featuresOutputFilePath);
			
			Utils.print(f, "", "\n", "|", null,			
					"Iter",
					"DocID",
					"SentenceNo",
					"AssocSpec",
					"c",
					"Tokens",
					"ArgCands",
					"AssnScore",
					"Sentence",
					"i",
					"Token",
					"Lemma",
					"k",
					"Head",
					"Type",
					//"HeToLem",
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
					"partial-score",
					"weights-size",
					"weights",
					"avg_weights"
			);
			
			return f;
		}
		return null;
	}
	
	public PrintStream getU(String mode) throws FileNotFoundException {
		if (controller.logLevel >= LEVEL_U) {
			String updatesOutputFilePath = outFolder.getAbsolutePath() + "/" + mode + "-AllUpdates-" + LOG_NAME_ID + "." + controller.logLevel + logSuffix + ".tsv";
			PrintStream u = new PrintStream(updatesOutputFilePath);
			
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
			
			return u;
		}
		return null;
	}
	
	public PrintStream getP(String mode) throws FileNotFoundException {
		if (controller.logLevel >= LEVEL_P) {
			String performanceOutputFilePath = outFolder.getAbsolutePath() + "/" + mode + "-Performance-" + LOG_NAME_ID + "." + controller.logLevel + logSuffix + ".tsv";
			PrintStream p = new PrintStream(performanceOutputFilePath);
			
			Utils.print(p, "", "\n", "|", null,				
					"Iter",
					"Sentences",
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

			return p;
		}
		return null;
	}
	
	public PrintStream getB(String mode) throws FileNotFoundException {
		if (controller.logLevel >= LEVEL_B_1) {
			String beamsOutputFilePath = outFolder.getAbsolutePath() + "/" + mode + "-AllBeams-" + LOG_NAME_ID + "." + controller.logLevel + logSuffix + ".tsv";
			PrintStream b = new PrintStream(beamsOutputFilePath);
			
			Utils.print(b, "", "\n", "|", null,	
					//general
					"Iter",
					"DocID",
					"SentenceNo",
					"violation",
					"beam-size",
					
					//assignment
					"pos",
					"assignment", //toString()
					"target",
					"compatible",
					"score",
					"state",
					
					//token
					"i",
					"Lemma",
					"target-label",
					"assn-label",
					"both-labels",
					"partial-score",
					
					//feature
					"Feature",
					"target",
					"assn",
					"labels+assn",
					"Weight",
					"AvgWeight"
			);
			
			return b;
		}
		return null;
	}
	
	public PrintStream getR(String mode) throws FileNotFoundException {
		if (controller.logLevel >= LEVEL_R) {
			String runsOutputFilePath = outFolder.getAbsolutePath() + "/" + mode + "AllRuns-" + LOG_NAME_ID + "." + controller.logLevel + logSuffix + ".tsv";
			PrintStream r = new PrintStream(runsOutputFilePath);
			
			Utils.print(r, "", "\n", "|", null,
					"", //"Id",
					"", //"IdPerTest",
					//"", //"SortMethod",
					"Train-Triggers", //"Iteration",
					"", //"Gold",
					"", //"System",
					"", //"Correct",
					"", //"Precision",
					"", //"Recall",
					"", //"F1",
					"Train-Args", //"Gold",
					"", //"System",
					"", //"Correct",
					"", //"Precision",
					"", //"Recall",
					"", //"F1",
					"", //"Harmonic",
					"Dev-Triggers", //"Iteration",
					"", //"Gold",
					"", //"System",
					"", //"Correct",
					"", //"Precision",
					"", //"Recall",
					"", //"F1",
					"Dev-Args", //"Gold",
					"", //"System",
					"", //"Correct",
					"", //"Precision",
					"", //"Recall",
					"", //"F1",
					"", //"Harmonic",
					"Test-Triggers", //"Gold",
					"", //"System",
					"", //"Correct",
					"", //"Precision",
					"", //"Recall",
					"", //"F1",
					"Test-Args", //"Gold",
					"", //"System",
					"", //"Correct",
					"", //"Precision",
					"", //"Recall",
					"", //"F1",
					"TrainEvents", //"List",
					"", //"Types",
					"", //"Sentences",
					"", //"Mentions",
					"", //"ArgCands"
					"", //"Args"
					"DevEvents", //"List",
					"", //"Types",
					"", //"Sentences",
					"", //"Mentions",
					"", //"ArgCands"
					"", //"Args"
					"TestEvents", //"List",
					//"", //"Types",
					"", //"Sentences",
					"", //"Mentions"
					"", //"ArgCands"
					"" //"Args"
			);
			Utils.print(r, "", "\n", "|", null, "");
			Utils.print(r, "", "\n", "|", null,
					"Id",
					"IdPerTest",
					//"SortMethod",
					"Iteration",
					"Gold",
					"System",
					"Correct",
					"Precision",
					"Recall",
					"F1",
					"Gold",
					"System",
					"Correct",
					"Precision",
					"Recall",
					"F1",
					"Harmonic",
					"Iteration",
					"Gold",
					"System",
					"Correct",
					"Precision",
					"Recall",
					"F1",
					"Gold",
					"System",
					"Correct",
					"Precision",
					"Recall",
					"F1",
					"Harmonic",
					"Gold",
					"System",
					"Correct",
					"Precision",
					"Recall",
					"F1",
					"Gold",
					"System",
					"Correct",
					"Precision",
					"Recall",
					"F1",
					"List",
					"Types",
					"Sentences",
					"Mentions",
					"ArgCands",
					"Args",
					"List",
					"Types",
					"Sentences",
					"Mentions",
					"ArgCands",
					"Args",
					"List",
					//"Types",
					"Sentences",
					"Mentions",
					"ArgCands",
					"Args"
			);

			return r;
		}
		return null;
	}
	
	public void logPostBeamSearch(SentenceInstance instance, SentenceAssignment assn, BigDecimal c, Integer iter, int i,
			FeatureVector weights, FeatureVector avg_weights, FeatureVector avg_weights_base, PrintStream w, PrintStream f, PrintStream u, boolean doLogging) {
		if (controller.logLevel >= LEVEL_MIN_F_U_W && doLogging) {
			//DEBUG
			String sentText = sentence(instance.textStart);
			String assnScore = assn.getScore().toString();
			int argCands = instance.eventArgCandidates.size();
			
			String assocLabel;
			try {
				assocLabel = SpecAnnotator.getSpecLabel(instance.associatedSpec);
			} catch (CASException e) {
				throw new RuntimeException(e);
			}
			
			//printf(w, "|%s%d|%s\n", wt.getFeaturesStringSkip(), i, wt.getFeaturesString());
			printWeights(w, iter, instance.docID, instance.sentInstID, c, instance.size(), sentText, weights, avg_weights, avg_weights_base, doLogging);
	
	
			// {SignalName : {Label : {Category : AmountInSentence}}}
			Map<String, Map<String, Map<String, BigDecimal>>> amounts = Maps.newTreeMap();
	
			// {SignalName: {Category : AmountInSentence}} - oof and oot ignore labels
			Map<String, Map<String, BigDecimal>> amountsOO = Maps.newTreeMap();
	
			List<Map<Class<?>, Object>> tokens = (List<Map<Class<?>, Object>>) instance.get(InstanceAnnotations.Token_FEATURE_MAPs);
			for (int j=0; j<instance.size(); j++) {
				Token tokenAnno = instance.sent.getTokenAnnotation(j);
				String surface = tokenAnno.getCoveredText();
				String lemma = (String) tokens.get(j).get(TokenAnnotations.LemmaAnnotation.class);
				Set<Object> allFeaturesSet = new HashSet<Object>();
				FeatureVector fvTarget = instance.target.getFeatureVectorSequence().get(j);
				Map<Object, BigDecimal> mapTarget = fvTarget.getMap();
				String assnPartialScore = "";

				String targetTriggerLabel = triggerLabel(instance.target.getLabelAtToken(j));
				
				String assnTriggerLabel = "X";
				FeatureVector fvAssn = null;
				Map<Object, BigDecimal> mapAssn = new HashMap<Object, BigDecimal>();
				if (j<assn.getFeatureVectorSequence().size()) {
					fvAssn = assn.getFeatureVectorSequence().get(j);
					mapAssn = fvAssn.getMap();
					assnTriggerLabel = triggerLabel(assn.getLabelAtToken(j));
					assnPartialScore = assn.getPartialScores().get(j).toString();
				}
				
				if (controller.logLevel >= LEVEL_F_1) {
				
					allFeaturesSet.addAll(mapTarget.keySet());
					allFeaturesSet.addAll(mapAssn.keySet());
					List<String> allFeaturesList = new ArrayList<String>(allFeaturesSet.size());
					for (Object o : allFeaturesSet) {
						allFeaturesList.add((String) o);
					}
					Collections.sort(allFeaturesList);
					
					int topK = -1;
					if (controller.useArguments) {
						topK = instance.eventArgCandidates.size()-1;
					}
					
					Map<Object, BigDecimal> currMapTarget;
					Map<Object, BigDecimal> currMapAssn;
					
					String targetLabel;
					String assnLabelG;
					List<String> allTargetArgLabels = Lists.newArrayList();
					List<String> allAssnArgLabels = Lists.newArrayList();
					
					ImmutableSortedSet<Integer> kValsImm = ContiguousSet.create(Range.closed(-1, topK), DiscreteDomain.integers());
					List<Integer> kVals = Lists.newArrayList(kValsImm);
					
					for (int k : kVals) {
						String kStr = "";

						String headText = "";
						String mentionType = "";
						//String headTokenLemma = "";
						
						if (k == -1) { //Trigger!
							currMapTarget = mapTarget;
							currMapAssn = mapAssn;
							targetLabel = targetTriggerLabel;
							assnLabelG = assnTriggerLabel;
						}
						else { //Argument!
							targetLabel = argLabel(targetTriggerLabel, "-");
							assnLabelG = argLabel(assnTriggerLabel, "-");
							Integer targetRoleNum = null;
							Integer assnRoleNum = null;
							
							if(instance.target.getEdgeAssignment().get(j) != null) {
								targetRoleNum = instance.target.getEdgeAssignment().get(j).get(k);
							}
							if(assn.getEdgeAssignment().get(j) != null) {
								assnRoleNum = assn.getEdgeAssignment().get(j).get(k);
							}
							
							if (targetRoleNum==null && assnRoleNum==null) {
								continue;
							}
							if (targetRoleNum!=null) {
								String targetArgLabel = (String) instance.target.edgeTargetAlphabet.lookupObject(targetRoleNum);
								targetLabel = argLabel(targetTriggerLabel, targetArgLabel);
								allTargetArgLabels.add(targetArgLabel);
							}
							if (assnRoleNum!=null) {
								String assnArgLabel = (String) instance.target.edgeTargetAlphabet.lookupObject(assnRoleNum);
								assnLabelG = argLabel(assnTriggerLabel, assnArgLabel);
								allAssnArgLabels.add(assnArgLabel);
							}

							currMapTarget = null;
							if (fvTarget != null && fvTarget.argsFV != null && fvTarget.argsFV.containsKey(k)) {
								currMapTarget = fvTarget.argsFV.get(k).getMap();
								
								Entry<Object, BigDecimal> e = currMapTarget.entrySet().iterator().next();
								System.out.printf("    j=%s k=%s currMapTarget.size()=%s (e.g. %s: %s)\n", j, k, currMapTarget.size(), e.getKey(), e.getValue());
							}
							currMapAssn = null;
							if (fvAssn != null && fvAssn.argsFV != null && fvAssn.argsFV.containsKey(k)) {
								currMapAssn = fvAssn.argsFV.get(k).getMap();
								
								Entry<Object, BigDecimal> e = currMapAssn.entrySet().iterator().next();
								System.out.printf("    j=%s k=%s currMapAssn.size()=%s (e.g. %s: %s)\n", j, k, currMapAssn.size(), e.getKey(), e.getValue());
							}

							
							kStr = Integer.toString(k);
							AceMention mention = instance.eventArgCandidates.get(k);
							headText = mention.getHeadText();
							mentionType = mention.getType();
							//headTokenLemma = mention.headTokenLemma;
						}
						
						for (String s : allFeaturesList) {
							
							// Filter feature by trigger\arg
							if ((k == -1 && !s.startsWith("B")) ||
								(k != -1 && !s.startsWith("E"))) {
								
								continue;
							}
							
							String inTarget = str(currMapTarget, s);
							String inAssn = str(currMapAssn, s);
							
							if (inTarget.equals("X") && inAssn.equals("X")) {
								continue;
							}
							
							String inWeights = str(weights, s);
							String inAvg = str(avg_weights, s);
							
							String targetSignal = "-"; //getSignalScore(instance.target, j, s);
							String assnSignal = "-"; //getSignalScore(assn, j, s);
							
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
							
							/// DEBUG
//							if (k!=-1 && !assnLabelG.contains("X") && !assnLabelG.contains("-") && inAssn.equals("X")) {
//								int d = 89 + 5;
//								int a = d + 6;
//							}
							/////
							
							if (controller.logLevel >= LEVEL_F_2) {
								Utils.print(f, "", "\n", "|", instance.sentInstID,
										iter,
										instance.docID,
										instance.sentInstID,
										assocLabel,
										c,
										instance.size(),
										
										argCands,										
										assnScore,
										
										
										"",//sentText,
										j,
										lemma(surface),
										lemma(lemma),
										kStr,
										lemma(headText, 25),
										mentionType,
										//lemma(headTokenLemma, headText, false),
										targetLabel,
										assnLabelG,
										twoLabels(targetLabel, assnLabelG),//twoLabels(instance.target, assnLabel, j),
										feature(s),
										"",//mapTarget.size(),
										targetSignal,
										inTarget,
										"",//mapAssn.size(),
										assnSignal,
										inAssn,
										twoLabelsAndScore(targetLabel, assnLabelG, inAssn),//twoLabelsAndScore(instance.target, assnLabel, j, inAssn),
										bothTargetAndAssn,
										sameTargetAndAssn,
										
										assnPartialScore,
										
										"",//weights.size(),
										inWeights,
										inAvg
								);
							}
						}
					}
					
					if (controller.logLevel >= LEVEL_F_1) {
		
						String targetArgsStr = StringUtils.join(allTargetArgLabels, "_");
						targetLabel = argLabel(targetTriggerLabel, targetArgsStr);
						String assnArgsStr = StringUtils.join(allAssnArgLabels, "_");
						assnLabelG = argLabel(assnTriggerLabel, assnArgsStr);
								
						Utils.print(f, "", "\n", "|", instance.sentInstID,
								iter,
								instance.docID,
								instance.sentInstID,
								assocLabel,
								c,
								instance.size(),
								argCands,
								assnScore,
								sentText,
								j,
								lemma(surface),
								lemma(lemma),
								"", //k
								"", //Head
								"", //mentionType
								//"", //HeadTokenLemma
								targetLabel,
								assnLabelG,
								twoLabels(targetLabel, assnLabelG),
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
								assnPartialScore,
								size(weights),
								"",
								""
						);
					}
				}
				
				if (controller.logLevel >= LEVEL_U && j<assn.getFeatureVectorSequence().size() && false) {
//					String targetLabel = instance.target.getLabelAtToken(j);
//					
//					for (String signalName : SentenceAssignment.signalToFeature.keySet()) {
//						try {
//							String featureName = null;
//							if (SentenceAssignment.signalToFeature.get(signalName).containsKey(assnLabel)) {
//								// Due to deisgn limitations of the table, all data under categories (eg LBL,O,T)
//								// would refer only to P+. P- won't be mentioned there (but is basically the opposite of the third portion).
//								featureName = SentenceAssignment.signalToFeature.get(signalName).get(assnLabel).get("P+");
//							}
//							// {label : {category : amount}}
//							Map<String, Map<String, BigDecimal>> forSignalName = amounts.get(signalName);
//							if (forSignalName == null) {
//								forSignalName = Maps.newLinkedHashMap();
//								amounts.put(signalName, forSignalName);
//							}
//							Map<String, BigDecimal> forSignalNameOO = amountsOO.get(signalName);
//							if (forSignalNameOO == null) {
//								forSignalNameOO = Maps.newLinkedHashMap();
//								amountsOO.put(signalName, forSignalNameOO);
//							}
//							
//							boolean targetIsO = targetLabel.equals(SentenceAssignment.PAD_Trigger_Label);
//							boolean assnIsO = assnLabel.equals(SentenceAssignment.PAD_Trigger_Label);
//							
//							if (targetIsO && assnIsO) {
//								addAccordingly(mapAssn, forSignalNameOO, "O,O,T", "O,O,F", featureName);
//							}
//							else if (targetIsO && !assnIsO) {
//								addAccordingly(mapAssn, forSignalName, assnLabel, "O,LBL,T", "O,LBL,F", featureName);
//							}
//							else if (!targetIsO && assnIsO) {
//								addAccordingly(mapAssn, forSignalName, targetLabel, "LBL,O,T", "LBL,O,F", featureName);
//							}
//							else if (!targetIsO && !assnIsO) {
//								if (targetLabel.equals(assnLabel)) {
//									addAccordingly(mapAssn, forSignalName, targetLabel, "LBL,LBL,T", "LBL,LBL,F", featureName);
//								}
//								else {
//									addAccordingly(mapAssn, forSignalName, assnLabel, "weird,weird,T", "weird,weird,F", featureName);
//									addAccordingly(mapAssn, forSignalName, targetLabel, "weird,weird,T", "weird,weird,F", featureName);
//								}
//							}
//						}
//						catch (RuntimeException e) {
//							throw new RuntimeException(
//									String.format("Exception in inst=%s, j=%s, targetLabel=%s, assnLabel=%s sig=%s.", instance, j, targetLabel, assnLabel, signalName), e);
//						}
//					}
				}
			}
			
			if (controller.logLevel >= LEVEL_U) {
//				for (String signalName : SentenceAssignment.signalToFeature.keySet()) {
//					Map<String, Map<String, BigDecimal>> forSignalName = amounts.get(signalName);
//					Map<String, BigDecimal> forSignalNameOO = amountsOO.get(signalName);
//					for (String label : forSignalName.keySet()) {
//						try {
//							Map<String, BigDecimal> forLabel = forSignalName.get(label);
//							
//							// Explaining this horrible convention: pp=P+ (p plus), pm=P- (p minus)
//							// and if you get a similar weird null regarding O (and not some LBL), then maybe do an if for it as well :)
//							String featureNameLBLpp="";
//							String featureNameLBLpm="";
//							String featureNameOpp="";
//							String featureNameOpm="";
//							BigDecimal weightLBLpp=BigDecimal.ZERO;
//							BigDecimal weightLBLpm=BigDecimal.ZERO;
//							BigDecimal weightOpp=BigDecimal.ZERO;
//							BigDecimal weightOpm=BigDecimal.ZERO;
//							String weightLBLStrpp="X";
//							String weightLBLStrpm="X";
//							String weightOStrpp="X";
//							String weightOStrpm="X";
//							BigDecimal changeLBLpp=BigDecimal.ZERO;
//							BigDecimal changeLBLpm=BigDecimal.ZERO;
//							BigDecimal changeOpp=BigDecimal.ZERO;
//							BigDecimal changeOpm=BigDecimal.ZERO;
//							String changeLBLStrpp="X";
//							String changeLBLStrpm="X";
//							String changeOStrpp="X";
//							String changeOStrpm="X";
//							
//							// this is a rather subtle point - we have this feature only if makeFeature() was called on it,
//							// but it must also be in weight (makeFeature() could have been called on an assn that
//							// was eventually not the one chosen in the beam)
//							boolean featureLBLppExists = false;
//							if (SentenceAssignment.signalToFeature.get(signalName).get(label) != null &&
//								SentenceAssignment.signalToFeature.get(signalName).get(label).get("P+") != null) {
//								featureNameLBLpp = SentenceAssignment.signalToFeature.get(signalName).get(label).get("P+");
//								weightLBLpp = weights.get(featureNameLBLpp);
//								if (weightLBLpp!=null) {
//									featureLBLppExists = true;
//									weightLBLStrpp = FMT.format(weightLBLpp);
//								}
//							}
//							boolean featureLBLpmExists = false;
//							if (SentenceAssignment.signalToFeature.get(signalName).get(label) != null &&
//								SentenceAssignment.signalToFeature.get(signalName).get(label).get("P-") != null) {
//								featureNameLBLpm = SentenceAssignment.signalToFeature.get(signalName).get(label).get("P-");
//								weightLBLpm = weights.get(featureNameLBLpm);
//								if (weightLBLpm!=null) {
//									featureLBLpmExists = true;
//									weightLBLStrpm = FMT.format(weightLBLpm);
//								}
//							}
//							boolean featureOppExists = false;
//							if (SentenceAssignment.signalToFeature.get(signalName).get(SentenceAssignment.PAD_Trigger_Label) != null &&
//								SentenceAssignment.signalToFeature.get(signalName).get(SentenceAssignment.PAD_Trigger_Label).get("P+") != null) {
//								featureNameOpp = SentenceAssignment.signalToFeature.get(signalName).get(SentenceAssignment.PAD_Trigger_Label).get("P+");
//								weightOpp = weights.get(featureNameOpp);
//								if (weightOpp!=null) {
//									featureOppExists = true;
//									weightOStrpp = FMT.format(weightOpp);
//								}
//							}
//							boolean featureOpmExists = false;
//							if (SentenceAssignment.signalToFeature.get(signalName).get(SentenceAssignment.PAD_Trigger_Label) != null &&
//								SentenceAssignment.signalToFeature.get(signalName).get(SentenceAssignment.PAD_Trigger_Label).get("P-") != null) {
//								featureNameOpm = SentenceAssignment.signalToFeature.get(signalName).get(SentenceAssignment.PAD_Trigger_Label).get("P-");
//								weightOpm = weights.get(featureNameOpm);
//								if (weightOpm!=null) {
//									featureOpmExists = true;
//									weightOStrpm = FMT.format(weightOpm);
//								}
//							}
//	
//							// we are only marking "change", if it happened to both features in the same token
//							// otherwise, most-likely O was changed with a different LBL, so we don't want to show it in this row
//							// (if we would, it would make weird distortions, like showing there was change even though
//							// only none-change categories are on)
//							if ((featureLBLppExists && featureOppExists) || (featureLBLpmExists && featureOpmExists)) {
//								for (Map<Object, BigDecimal> featureChangesOnSameToken : weights.updates.values()) {
//									if (featureChangesOnSameToken.containsKey(featureNameLBLpp) && featureChangesOnSameToken.containsKey(featureNameOpp)) {
//										changeLBLpp = featureChangesOnSameToken.get(featureNameLBLpp);
//										changeOpp = featureChangesOnSameToken.get(featureNameOpp);
//										changeLBLStrpp = FMT.format(changeLBLpp);
//										changeOStrpp = FMT.format(changeOpp);
//									}
//									if (featureChangesOnSameToken.containsKey(featureNameLBLpm) && featureChangesOnSameToken.containsKey(featureNameOpm)) {
//										changeLBLpm = featureChangesOnSameToken.get(featureNameLBLpm);
//										changeOpm = featureChangesOnSameToken.get(featureNameOpm);
//										changeLBLStrpm = FMT.format(changeLBLpm);
//										changeOStrpm = FMT.format(changeOpm);
//									}
//								}
//							}
//							
//							boolean changePp = (changeLBLpp!=BigDecimal.ZERO || changeOpp!=BigDecimal.ZERO);
//							boolean changePm = (changeLBLpm!=BigDecimal.ZERO || changeOpm!=BigDecimal.ZERO);
//							boolean anyChange = (changePp || changePm);
//							String changePpStr = changePp?"T":"F";
//							String changePmStr = changePm?"T":"F";
//							String anyChangeStr = anyChange?"T":"F";
//							
//							List<String> summary = Lists.newArrayList();
//							addSummary(summary, forSignalNameOO, "O,O,F");
//							addSummary(summary, forSignalNameOO, "O,O,T");
//							addSummary(summary, forLabel, "LBL,O,T");
//							addSummary(summary, forLabel, "O,LBL,F");
//							addSummary(summary, forLabel, "LBL,LBL,T");
//							addSummary(summary, forLabel, "O,LBL,T");
//							addSummary(summary, forLabel, "LBL,O,F");
//							addSummary(summary, forLabel, "LBL,LBL,F");
//							addSummary(summary, forLabel, "weird,weird,F");
//							addSummary(summary, forLabel, "weird,weird,T");
//						
//							Utils.print(u, "", "\n", "|", null,
//									iter, //Iter
//									String.format("%s:%s", instance.docID, instance.sentInstID), //"DocID:SentenceNo"
//									instance.target.getState(),//"Tokens"
//									assn.getState(),//"TokensProcessed"
//									signalName, //"SignalName"
//									label, //"Label"
//									
//									weightLBLStrpp, //"Weight:LBL:P+"
//									weightLBLStrpm, //"Weight:LBL:P-"
//									weightOStrpp, //"Weight:O:P+"
//									weightOStrpm, //"Weight:O:P-"
//									anyChangeStr, //"AnyChange"
//									changePpStr, //
//									changePmStr,
//									changeLBLStrpp, // "Change:LBL:P+"
//									changeLBLStrpm, // "Change:LBL:P-"
//									changeOStrpp, // "Change:O:P+"
//									changeOStrpm, // "Change:O:P-"
//									String.format("Change=%s:%s*%s", changePpStr, changePmStr, summary), //"Summary"
//	
//									forSignalNameOO.get("O,O,F"),
//									forLabel.get("LBL,O,T"),
//									forLabel.get("O,LBL,F"),
//									forLabel.get("LBL,LBL,T"),
//									forLabel.get("O,LBL,T"),
//									forLabel.get("LBL,O,F"),
//									forSignalNameOO.get("O,O,T"),
//									forLabel.get("LBL,LBL,F"),
//									forLabel.get("weird,weird,F"),
//									forLabel.get("weird,weird,T")
//									
//							);
//						}
//						catch (RuntimeException e) {
//							throw new RuntimeException(String.format("Exception for instance=%s, sig=%s, label=%s", instance, signalName, label), e);
//						}
//					}
//				}
			}
		}
	}
	
	public void logRun(PrintStream r, Run run, AllTrainingScores scores, Stats testStats, Collection<JCas> trainTypes, Collection<JCas> devTypes, JCas testType, Collection<SentenceInstance> runTrain, Collection<SentenceInstance> runDev, Collection<SentenceInstance> runTest) throws CASException {
		if (controller.logLevel >= LEVEL_R) {
			int trainEventMentions = SentenceInstance.getNumEventMentions(runTrain);
			int devEventMentions = SentenceInstance.getNumEventMentions(runDev);
			int testEventMentions = SentenceInstance.getNumEventMentions(runTest);

			int trainArgCandMentions = SentenceInstance.getNumArgCandsForTriggers(runTrain);
			int devArgCandMentions = SentenceInstance.getNumArgCandsForTriggers(runDev);
			int testArgCandMentions = SentenceInstance.getNumArgCandsForTriggers(runTest);

			int trainArgsMentions = SentenceInstance.getNumArgsForTriggers(runTrain);
			int devArgsMentions = SentenceInstance.getNumArgsForTriggers(runDev);
			int testArgsMentions = SentenceInstance.getNumArgsForTriggers(runTest);

			Utils.print(r, "", "\n", "|", null,
					run.id, //"Id",
					run.idPerTest,//"IdPerTest",
					//run.sentenceSortingMethod,//"SortMethod",
					
					// Train-Triggers
					scores.train.bestScore.iteration,//"Iteration",
					scores.train.bestScore.count_trigger_gold,//"Gold",
					scores.train.bestScore.count_trigger_ans,//"System",
					scores.train.bestScore.count_trigger_correct,//"Correct",
					scores.train.bestScore.trigger_precision,//"Precision",
					scores.train.bestScore.trigger_recall,//"Recall",
					scores.train.bestScore.trigger_F1,//"F1",
					
					// Train-Args
					scores.train.bestScore.count_arg_gold,//"Gold",
					scores.train.bestScore.count_arg_ans,//"System",
					scores.train.bestScore.count_arg_correct,//"Correct",
					scores.train.bestScore.arg_precision,//"Precision",
					scores.train.bestScore.arg_recall,//"Recall",
					scores.train.bestScore.arg_F1,//"F1",
					scores.train.bestScore.harmonic_mean,//"Harmonic",
					
					// Dev-Triggers
					scores.dev.bestScore.iteration,//"Iteration",
					scores.dev.bestScore.count_trigger_gold,//"Gold",
					scores.dev.bestScore.count_trigger_ans,//"System",
					scores.dev.bestScore.count_trigger_correct,//"Correct",
					scores.dev.bestScore.trigger_precision,//"Precision",
					scores.dev.bestScore.trigger_recall,//"Recall",
					scores.dev.bestScore.trigger_F1,//"F1",
					
					// Dev-Args
					scores.dev.bestScore.count_arg_gold,//"Gold",
					scores.dev.bestScore.count_arg_ans,//"System",
					scores.dev.bestScore.count_arg_correct,//"Correct",
					scores.dev.bestScore.arg_precision,//"Precision",
					scores.dev.bestScore.arg_recall,//"Recall",
					scores.dev.bestScore.arg_F1,//"F1",
					scores.dev.bestScore.harmonic_mean,//"Harmonic",
					
					// Test-Triggers
					testStats.num_trigger_gold,//"Gold",
					testStats.num_trigger_ans,//"System",
					testStats.num_trigger_correct,//"Correct",
					testStats.prec_trigger,//"Precision",
					testStats.recall_trigger,//"Recall",
					testStats.f1_trigger,//"F1",

					// Test-Args
					testStats.num_arg_gold,//"Gold",
					testStats.num_arg_ans,//"System",
					testStats.num_arg_correct,//"Correct",
					testStats.prec_arg,//"Precision",
					testStats.recall_arg,//"Recall",
					testStats.f1_arg,//"F1",

					labelList(trainTypes),//"List",
					trainTypes.size(),//"Types",
					runTrain.size(),//"Sentences",
					trainEventMentions,//"Mentions",
					trainArgCandMentions,//"ArgCands"
					trainArgsMentions,//"Args"
					labelList(devTypes),//"List",
					devTypes.size(),//"Types",
					runDev.size(),//"Sentences",
					devEventMentions,//"Mentions",
					devArgCandMentions,//"ArgCands"
					devArgsMentions,//"Args"
					SpecAnnotator.getSpecLabel(testType),//"List",
					//testTypes.size(),//"Types",
					runTest.size(),//"Sentences",
					testEventMentions,//"Mentions",
					testArgCandMentions,//"ArgCands"
					testArgsMentions//"Args"
			);					
		}
	}

}
