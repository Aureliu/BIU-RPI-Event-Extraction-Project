package edu.cuny.qc.perceptron.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.dom4j.DocumentException;

import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.ClusterInstance;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.DocumentCrossSent;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.SentenceInstance;

public class Pipeline
{
	//DEBUG
	public static File modelFile = null;
	///////////
	
	/**
	 * Given the document list, train a perceptron model, and write to modelFile
	 * @param srcDir
	 * @param trainingFileList
	 * @param modelFile
	 */
	public static Perceptron trainPerceptron(File srcDir, File trainingFileList, File modelFile, File devFileList, Controller controller, String singleEventType)
	{
		
		Alphabet nodeTargetAlphabet = new Alphabet();
		Alphabet edgeTargetAlphabet = new Alphabet();
		Alphabet featureAlphabet = new Alphabet();
		try
		{
			// Make sure model file is writable
			PrintStream stream = new PrintStream(modelFile);
			stream.printf("(file is writable - verified)");
			stream.close();

			// read instance list from training data (and dev data)
			List<SentenceInstance> trainInstanceList = null;
			List<SentenceInstance> devInstanceList = null;
			Perceptron model = null;
			
			if(!controller.crossSent)
			{
				System.out.printf("[%s] Reading instance list of train...\n", new Date());
				trainInstanceList = readInstanceList(srcDir, trainingFileList, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, true, singleEventType);
				System.out.printf("[%s] Finished reading instance list of train, got %d instances\n", new Date(), trainInstanceList.size());
				System.out.printf("[%s] Reading instance list of dev\n", new Date());
				devInstanceList = readInstanceList(srcDir, devFileList, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, false, singleEventType);
				System.out.printf("[%s] Finished reading instance list of dev, got %d instances\n", new Date(), devInstanceList.size());
				// perceptron training
				System.out.printf("[%s] Building perceptron...\n", new Date());
				model = new Perceptron(nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet);
				System.out.printf("[%s] Finished building perceptron. It's LabelBigramis: %s\n", new Date(), model.getLabelBigram());
			}
			else
			{
				trainInstanceList = readInstanceClusters(srcDir, trainingFileList, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, true);
				devInstanceList = readInstanceClusters(srcDir, devFileList, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, false);
				if(controller.crossSentReranking)
				{
					// use BeamSearchCluster to do inference
					model = new PerceptronCluster(nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet);
				}
				else
				{
					// use BeamSearchSeq to do inference
					model = new PerceptronCluster(nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet,
							BeamSearchClusterSeq.class);
				}
			}
			
			//DEBUG
			Pipeline.modelFile = modelFile;
			//////////////////
			
			model.controller = controller;
			// learning
			model.learning(trainInstanceList, devInstanceList, 0, singleEventType);
			// save learned perceptron to file
			Perceptron.serializeObject(model, modelFile);
			
			return model;
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		catch (DocumentException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * give a file list and home dir, get an instance list
	 * @param srcDir
	 * @param file_list
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static List<SentenceInstance> readInstanceList(File srcDir, File file_list, 
			Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, 
			Controller controller, boolean learnable, String singleEventType) throws IOException, DocumentException
	{
		System.out.println("Reading training instance ...");
		
		List<SentenceInstance> instancelist = new ArrayList<SentenceInstance>();
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		TextFeatureGenerator featGen = new TextFeatureGenerator();
		while((line = reader.readLine()) != null)
		{
			boolean monoCase = line.contains("bn/") ? true : false;
			String fileName = srcDir + File.separator + line;
			
			System.out.println(fileName);
			
			Document doc = Document.createAndPreprocess(fileName, true, monoCase, true, true, singleEventType);
			// fill in text feature vector for each token
			featGen.fillTextFeatures_NoPreprocessing(doc);
			for(int sent_id=0 ; sent_id<doc.getSentences().size(); sent_id++)
			{
				Sentence sent = doc.getSentences().get(sent_id);
				// during learning, skip instances that do not have event mentions 
				if(learnable && controller.skipNonEventSent)
				{
					if(sent.eventMentions != null && sent.eventMentions.size() > 0)
					{
						SentenceInstance inst = new SentenceInstance(sent, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet,
								controller, learnable);
						instancelist.add(inst);
					}
				}
				else // add all instances
				{
					SentenceInstance inst = new SentenceInstance(sent, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, 
							controller, learnable);
					instancelist.add(inst);
				}
			}
		}
		
		System.out.println("done");
		return instancelist;
	}
	
	/**
	 * This function is to get list of ClusterInstance
	 * it's used for corss-sentence decoding
	 */
	public static List<SentenceInstance> readInstanceClusters(File srcDir, File file_list, 
			Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, 
			Controller controller, boolean learnable) throws IOException, DocumentException
	{
		System.out.println("Reading training instance ...");
		
		List<SentenceInstance> instancelist = new ArrayList<SentenceInstance>();
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		TextFeatureGenerator featGen = new TextFeatureGenerator();
		while((line = reader.readLine()) != null)
		{
			boolean monoCase = line.contains("bn/") ? true : false;
			String fileName = srcDir + File.separator + line;
			
			System.out.println(fileName);
			
			DocumentCrossSent doc = new DocumentCrossSent(fileName, true, monoCase);
			// fill in text feature vector for each token
			featGen.fillTextFeatures(doc);
			doc.setSentenceClustersByTokens();
			for(int cluster_id=0 ; cluster_id<doc.getSentenceClusters().size(); cluster_id++)
			{
				List<Sentence> cluster = doc.getSentenceClusters().get(cluster_id);
				
				// during learning, skip instances that do not have event mentions 
				if(learnable && controller.skipNonEventSent)
				{
					if(hasEventMention(cluster))
					{
						SentenceInstance inst = new ClusterInstance(cluster, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet,
								controller, learnable);
						instancelist.add(inst);
					}
				}
				else // add all instances
				{
					SentenceInstance inst = new ClusterInstance(cluster, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, 
							controller, learnable);
					instancelist.add(inst);
				}
			}
		}
		
		System.out.println("done");
		return instancelist;
	}

	/**
	 * check if a cluster of sentences contain event mentions
	 * @param cluster
	 * @return
	 */
	private static boolean hasEventMention(List<Sentence> cluster)
	{
		for(Sentence sent : cluster)
		{
			if(sent.eventMentions != null && sent.eventMentions.size() > 0)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * This is a very simple pipeline
	 * @param args
	 * @throws IOException
	 */
	static public void main(String[] args) throws IOException
	{
		//mainWithSingleEventType(args, null);
		mainWithSingleEventType(args);
	}
	
	public static void mainWithSingleEventType(String[] args) throws IOException {
		System.out.printf("Args:\n%s\n\n", new ArrayList<String>(Arrays.asList(args)));
		if(args.length < 4)
		{
			System.out.println("Training perceptron Usage:");
			System.out.println("args[0]: source dir of training data");
			System.out.println("args[1]: file list of training data");
			System.out.println("args[2]: model file to be saved");
			System.out.println("args[3]: file list of dev data");
			System.out.println("args[4]: single event type ('null' for all event types)");
			System.out.println("args[5+]: controller arguments");
			System.exit(-1);
		}
		
		System.err.println("(Training err stream)");

		File srcDir = new File(args[0]);
		File trainingFileList = new File(args[1]);
		File modelFile = new File(args[2]);
		File devFileList = new File(args[3]);
		String singleEventType = args[4];
		if (singleEventType.equals("null")) {
			singleEventType = null;
		}
		
		PrintStream out = new PrintStream(modelFile.getAbsoluteFile() + ".weights");

		// set settings
		Controller controller = new Controller();
		String[] settings = Arrays.copyOfRange(args, 5, args.length);
		controller.setValueFromArguments(settings);
		System.out.println("\n" + controller.toString() + "\n");
		
		// train model
		Perceptron model = trainPerceptron(srcDir, trainingFileList, modelFile, devFileList, controller, singleEventType);
		
		// print out weights
		if(model.controller.avgArguments)
		{
			out.print(model.getAvg_weights().toStringFull());
		}
		else
		{
			out.print(model.getWeights().toStringFull());
		}
		out.close();
	}
}
