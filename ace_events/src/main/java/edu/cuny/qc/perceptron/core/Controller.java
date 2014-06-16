package edu.cuny.qc.perceptron.core;

import java.util.Date;

// This is a controller of the settings in percetpron
public class Controller implements java.io.Serializable
{
	private static final long serialVersionUID = -2434003761785667377L;
	
	public int beamSize = 5;
	public int maxIterNum = 30;
	public boolean skipNonEventSent = true;
	public boolean avgArguments = true;
	// skip the features of NON argument link assignment
	public boolean skipNonArgument = false;
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
	 * 1 - model, weights, performance
	 * 2 - model, weights, performance, features (only label per token)
	 * 3 - model, weights, performance, features (only label per token), weights (only vector summary per sentence)
	 * 4 - model, weights, performance, features, weights (only vector summary per sentence)
	 * 5 - model, weights, performance, features, weights (only vector summary per sentence), beam (only full assignments)
	 * 6 - model, weights, performance, features, weights (only vector summary per sentence), beam
	 * 7 - model, weights, performance, features, weights (vector summary per sentence + all weights only for PostItr), beam
	 * 8 - model, weights, performance, features, weights, beam
	 */
	public int logLevel = 1;

	// Which of the four (A,B,C,D) methods should be used for the O features. No valid default value, must be explicitly supplied.
	public String oMethod = null;
	
	public boolean usePreprocessFiles = true;
	
	public boolean useSignalFiles = true;
	
	public Controller()
	{
		System.out.printf("\n[%s] ******** Controller() **********\n", new Date());
		System.out.printf("******** %s **********\n", this);
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
				System.out.println("Malform argument: " + arg);
				System.exit(-1);
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
				skipNonArgument = Boolean.parseBoolean(fields[1]);
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
		}
	}
	
	public String toString()
	{
		String ret = "beam size: " + beamSize + " max iter: " + maxIterNum + " skipNonEventSent: " + skipNonEventSent
		+ " averaged weights: " + avgArguments + " skipNonArgument: " + skipNonArgument
		+ " useGlobalFeature:" + useGlobalFeature + " addNeverSeenFeatures: " + addNeverSeenFeatures
		+ " crossSent:" + crossSent + " crossSentReranking:" + crossSentReranking + " order:" + order +
		" evaluatorType:" + evaluatorType + " learnBigrams: " + learnBigrams;
		return ret;
	}
	
	public static void main(String[] args)
	{
		Controller controller = new Controller();
		String comm = "maxIterNum=50 useGlobalFeature=false";
		controller.setValueFromArguments(comm.split("\\s+"));
		System.out.println(controller);
	}
}
