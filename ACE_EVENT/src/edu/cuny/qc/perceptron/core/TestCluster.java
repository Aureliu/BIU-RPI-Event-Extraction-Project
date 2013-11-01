package edu.cuny.qc.perceptron.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import edu.cuny.qc.ace.acetypes.AceEvent;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.ClusterInstance;
import edu.cuny.qc.perceptron.types.DocumentCrossSent;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;

public class TestCluster
{
	/**
	 * This is for test purpose of the training/decoding procedures
	 * about "cluster" perceptron
	 * @param args
	 * @throws IOException
	 */
	static public void main(String[] args) throws IOException
	{
		if(args.length == 0)
		{
			System.out.println("Usage:");
			System.out.println("args[0]: doc id");
			System.exit(-1);
		}
		File txtFile = new File(args[0]);
		DocumentCrossSent doc = new DocumentCrossSent(txtFile.getAbsolutePath(), true, false);
		TextFeatureGenerator featGen = new TextFeatureGenerator();
		featGen.fillTextFeatures(doc);
		doc.setSentenceClustersStrict();
		doc.printDocBasic(System.out);
		
		List<SentenceInstance> instancelist = new ArrayList<SentenceInstance>();
		Alphabet nodeTargetAlphabet = new Alphabet();
		Alphabet edgeTargetAlphabet = new Alphabet();
		Alphabet featureAlphabet = new Alphabet();
		
		PerceptronCluster perceptron = new PerceptronCluster(nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet);
		perceptron.controller.beamSize = 10;
		perceptron.controller.avgArguments = true;
		perceptron.controller.addNeverSeenFeatures = true;
		perceptron.controller.useGlobalFeature = true;
		perceptron.controller.maxIterNum = 50;
		perceptron.controller.crossSent = true;
		perceptron.controller.crossSentReranking = false;
		
		for(int cluster_id=0 ; cluster_id<doc.getSentenceClusters().size(); cluster_id++)
		{
			List<Sentence> cluster = doc.getSentenceClusters().get(cluster_id);
			SentenceInstance inst = new ClusterInstance(cluster, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, perceptron.controller, true);
			instancelist.add(inst);
		}
		
		// test the whole learning procedure
		perceptron.learning(instancelist, instancelist, 0);
		
		// save learned perceptron to file
		Perceptron.serializeObject(perceptron, new File("model_cluster"));
		
		// read model from the serialized file
		perceptron = (PerceptronCluster) PerceptronCluster.deserializeObject(new File("model_cluster"));
		List<SentenceAssignment> resultsAvg = perceptron.decoding(instancelist);
		String id_prefix = doc.docID + "-" + "EV";
		List<SentenceInstance> list = perceptron.getCanonicalInstanceList(instancelist);
		for(int inst_id=0; inst_id < list.size(); inst_id++)
		{
			SentenceAssignment assn = resultsAvg.get(inst_id);
			SentenceInstance inst = list.get(inst_id);
			String id = id_prefix + "-" + inst_id;
			List<AceEvent> events = inst.getEvents(assn, id, doc.allText);
			for(AceEvent event : events)
			{
				System.out.println(event);
			}
		}
		
		PrintStream out = new PrintStream(new File("weights_cluster"));
		out.println(perceptron.getWeights().toString());
		out.close();
	}
}
