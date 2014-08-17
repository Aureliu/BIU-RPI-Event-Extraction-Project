package edu.cuny.qc.perceptron.core;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import edu.cuny.qc.scorer.FeatureProfile;
import edu.cuny.qc.util.Utils;

// This is a controller of the settings in percetpron
public class Controller implements java.io.Serializable
{
	private static final long serialVersionUID = -2434003761785667377L;
	
	public int beamSize = 5;
	public int maxIterNum = 30;
	public boolean skipNonEventSent = true;
	public boolean avgArguments = true;
	// skip the features of NON argument link assignment
	//public boolean skipNonArgument = false;
	// use global feature
	public boolean useGlobalFeature = true;
	// true if during training, skip arguments expansion for triggers 
	// that are not correct
	public boolean addNeverSeenFeatures = true;
	// whether use crossSent inference 
	public boolean crossSent = false;
	// whether use BeamSearchCluster or BeamSearchClusterSeq
	public boolean crossSentReranking = false;
	
	// the type of evaluator during training 1 is EvaluatorLoose, 0 is evaluatorFinal
	public int evaluatorType = 0;
	
	// order of trigger labeling 0/1 0 stands for unigram, 1 stands for bigram
	public int order = 1;
	
	// standard (1) or early-update (0). Default is early-update
	public int updateType = 0;
	
	// Should labelBigrams be learned from training data. Otherwise, all possible bigrams are considered by default.
	public boolean learnBigrams = true;
	
	public SerializationMethod serialization = SerializationMethod.BZ2; 
	
	/**
	 * 0 - only model file
	 * 1 - model, final weights, performance, updates  - [also "runs" when applicable]
	 * 2 - model, final weights, performance, updates, features (only label per token)
	 * 3 - model, final weights, performance, updates, features (only label per token), weights (only vector summary per sentence)
	 * 4 - model, final weights, performance, updates, features, weights (only vector summary per sentence)
	 * 5 - model, final weights, performance, updates, features, weights (only vector summary per sentence), beam (only full assignments)
	 * 6 - model, final weights, performance, updates, features, weights (only vector summary per sentence), beam
	 * 7 - model, final weights, performance, updates, features, weights (vector summary per sentence + all weights only for PostItr), beam
	 * 8 - model, final weights, performance, updates, features, weights, beam
	 */
	public int logLevel = 1;

	// Which of the four (A,B,C,D) methods should be used for the O features. No valid default value, must be explicitly supplied.
	public String oMethod = null;
	
	public boolean usePreprocessFiles = true;
	
	public boolean useSignalFiles = true;
	
	public boolean saveSignalsToValues = false;
	
	public FeatureProfile featureProfile = FeatureProfile.NORMAL;
	
	public boolean singleTokenSentences = false;
	
	public String[] logOnlyTheseSentences = {};
	
	public boolean useArguments = true;
	
	public boolean updateOnlyOnViolation = true;
	
	public List<String> trainList = null;
	public List<String> devList = null;
	public String testType = null;
	
	public List<String> trainOnlyTypes = null;
	public List<String> devOnlyTypes = null;
	public List<String> testOnlyTypes = null;
	
	// The previous default was DOC_SENT_SPEC(_ROLE), but SPEC(_ROLE)_DOC_SENT proved to be almost always better
	public SentenceSortingMethod sentenceSortingMethod = SentenceSortingMethod.SPEC_ROLE_DOC_SENT;// SentenceSortingMethod.DOC_SENT_SPEC; // this was the implied default long before I came up with this enum
	
	public boolean filterSentenceInstance = true;
	
	public boolean enhanceSpecs = false;
	
	public boolean takeExtendedTags = false;
	
	public ArgOMethod argOMethod = ArgOMethod.OR_ALL;//ArgOMethod.SKIP_O;
	
	// This param is special since we don't get it from command line - it's a calculated param!
	public boolean lazyTargetFeatures = false;
	
	public Controller()
	{
//		System.out.printf("\n[%s] ******** Controller() **********\n", new Date());
//		System.out.printf("******** %s **********\n", this);
	}
	
	/**
	 * set the values according to command line arguments
	 * @param line
	 */
	public void setValueFromArguments(String[] arguments)
	{
		for(String arg : arguments)
		{
			String[] fields = arg.split("=");
			if(fields.length != 2)
			{
				throw new IllegalArgumentException(String.format("Malform argument in controller '%s', got %s part instead of 2", arg, fields.length));
			}
			else if(fields[0].equalsIgnoreCase("beamSize"))
			{
				beamSize = Integer.parseInt(fields[1]); 
			}
			else if(fields[0].equalsIgnoreCase("maxIterNum"))
			{
				maxIterNum = Integer.parseInt(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("skipNonEventSent"))
			{
				skipNonEventSent = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("avgArguments"))
			{
				avgArguments = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("skipNonArgument"))
			{
				//skipNonArgument = Boolean.parseBoolean(fields[1]);
				throw new IllegalArgumentException("'skipNonArgument' is not supported anymore, use argOMethod=SKIP_O instead");
			}
			else if(fields[0].equalsIgnoreCase("useGlobalFeature"))
			{
				useGlobalFeature = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("addNeverSeenFeatures"))
			{
				addNeverSeenFeatures = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("crossSent"))
			{
				crossSent = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("crossSentReranking"))
			{
				crossSentReranking = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("order"))
			{
				order = Integer.parseInt(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("evaluatorType"))
			{
				evaluatorType = Integer.parseInt(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("updateType"))
			{
				updateType = Integer.parseInt(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("learnBigrams"))
			{
				learnBigrams = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("logLevel"))
			{
				logLevel = Integer.parseInt(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("oMethod"))
			{
				oMethod = fields[1];
			}
			else if(fields[0].equalsIgnoreCase("serialization"))
			{
				serialization = SerializationMethod.valueOf(fields[1].toUpperCase());
			}
			else if(fields[0].equalsIgnoreCase("usePreprocessFiles"))
			{
				usePreprocessFiles = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("useSignalFiles"))
			{
				useSignalFiles = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("saveSignalsToValues"))
			{
				saveSignalsToValues = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("featureProfile"))
			{
				featureProfile = FeatureProfile.valueOf(fields[1].toUpperCase());
			}
			else if(fields[0].equalsIgnoreCase("singleTokenSentences"))
			{
				singleTokenSentences = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("logOnlyTheseSentences"))
			{
				logOnlyTheseSentences = fields[1].split(",");
				Utils.logOnlyTheseSentences = Arrays.asList(logOnlyTheseSentences);
			}
			else if(fields[0].equalsIgnoreCase("useArguments"))
			{
				useArguments = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("updateOnlyOnViolation"))
			{
				updateOnlyOnViolation = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("trainList"))
			{
				trainList = Arrays.asList(fields[1].split(","));
			}
			else if(fields[0].equalsIgnoreCase("devList"))
			{
				devList = Arrays.asList(fields[1].split(","));
			}
			else if(fields[0].equalsIgnoreCase("testType"))
			{
				testType = fields[1];
			}
			else if(fields[0].equalsIgnoreCase("trainOnlyTypes"))
			{
				trainOnlyTypes = Arrays.asList(fields[1].split(","));
			}
			else if(fields[0].equalsIgnoreCase("devOnlyTypes"))
			{
				devOnlyTypes = Arrays.asList(fields[1].split(","));
			}
			else if(fields[0].equalsIgnoreCase("testOnlyTypes"))
			{
				testOnlyTypes = Arrays.asList(fields[1].split(","));
			}
			else if(fields[0].equalsIgnoreCase("sentenceSortingMethod"))
			{
				sentenceSortingMethod = SentenceSortingMethod.valueOf(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("filterSentenceInstance"))
			{
				filterSentenceInstance = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("enhanceSpecs"))
			{
				enhanceSpecs = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("takeExtendedTags"))
			{
				takeExtendedTags = Boolean.parseBoolean(fields[1]);
			}
			else if(fields[0].equalsIgnoreCase("argOMethod"))
			{
				argOMethod = ArgOMethod.valueOf(fields[1]);
			}
		}
		
		
		lazyTargetFeatures = false;//(argOMethod==ArgOMethod.ITERATE);
		
		System.out.printf("\n[%s] ******** Controller() **********\n", new Date());
		System.out.printf("******** %s **********\n", this);
	}
	
	public String toString()
	{
		String ret = "Controller:\n" +
		"\tbeamSize: " + beamSize + " maxIterNum: " + maxIterNum + " skipNonEventSent: " + skipNonEventSent
		+ " averaged weights: " + avgArguments + //" skipNonArgument: " + skipNonArgument +
		" useGlobalFeature:" + useGlobalFeature + " addNeverSeenFeatures: " + addNeverSeenFeatures
		+ "\n\tcrossSent:" + crossSent + " crossSentReranking:" + crossSentReranking + " order:" + order +
		" evaluatorType:" + evaluatorType + " learnBigrams: " + learnBigrams + " logLevel: " + logLevel +
		" oMethod: " + oMethod + " serialization: " + serialization + " usePreprocessFiles: " + usePreprocessFiles
		+ "\n\tuseSignalFiles: " + useSignalFiles + " saveFeatureSignalNames: " + saveSignalsToValues +
		" featureProfile: " + featureProfile + " singleTokenSentences: " + singleTokenSentences +
		" logOnlyTheseSentences: " + Lists.newArrayList(logOnlyTheseSentences) + " useArguments: " + useArguments +
		"\n\tupdateOnlyOnViolation: " + updateOnlyOnViolation + " trainList: " + trainList + " devList: " + devList +
		" testType: " + testType + "\n\ttrainOnlyTypes: " + trainOnlyTypes + " devOnlyTypes: " + devOnlyTypes + 
		" testOnlyTypes: " + testOnlyTypes + " sentenceSortingMethod: " + sentenceSortingMethod +
		"\n\tfilterSentenceInstance: " + filterSentenceInstance + " enhanceSpecs: " + enhanceSpecs +
		" takeExtendedTags: " + takeExtendedTags + " argOMethod: " + argOMethod + " lazyTargetFeatures: " + lazyTargetFeatures;
		
		return ret + "\n";
	}
	
	public static void main(String[] args)
	{
		Controller controller = new Controller();
		String comm = "maxIterNum=50 useGlobalFeature=false";
		controller.setValueFromArguments(comm.split("\\s+"));
		System.out.println(controller);
	}
}
