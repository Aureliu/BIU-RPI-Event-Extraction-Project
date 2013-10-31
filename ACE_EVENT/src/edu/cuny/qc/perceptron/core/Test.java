package edu.cuny.qc.perceptron.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import edu.cuny.qc.ace.acetypes.AceEvent;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;

public class Test
{
	/**
	 * This is for test purpose of the training/decoding procedures
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
		Document doc = new Document(txtFile.getAbsolutePath(), true, false);
		TextFeatureGenerator featGen = new TextFeatureGenerator();
		featGen.fillTextFeatures(doc);
		doc.printDocBasic(System.out);
		
		List<SentenceInstance> instancelist = new ArrayList<SentenceInstance>();
		Alphabet nodeTargetAlphabet = new Alphabet();
		Alphabet edgeTargetAlphabet = new Alphabet();
		Alphabet featureAlphabet = new Alphabet();
		
		Perceptron perceptron = new Perceptron(nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet);
		perceptron.controller.beamSize = 2;
		perceptron.controller.avgArguments = true;
		perceptron.controller.addNeverSeenFeatures = true;
		perceptron.controller.useGlobalFeature = true;
		perceptron.controller.maxIterNum = 12;
		perceptron.controller.evaluatorType = 1;
		
		for(int sent_id=0 ; sent_id<doc.getSentences().size(); sent_id++)
		{
			Sentence sent = doc.getSentences().get(sent_id);
			SentenceInstance inst = new SentenceInstance(sent, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, perceptron.controller, true);
			instancelist.add(inst);
		}
		
		// test the whole learning procedure
		perceptron.learning(instancelist, instancelist, 0);
		
		// save learned perceptron to file
		Perceptron.serializeObject(perceptron, new File("model"));
		
		// read model from the serialized file
		perceptron = Perceptron.deserializeObject(new File("model"));
		perceptron.controller.avgArguments = false;
		List<SentenceAssignment> results = perceptron.decoding(instancelist);
		perceptron.controller.avgArguments = true;
		List<SentenceAssignment> resultsAvg = perceptron.decoding(instancelist);
		
		// convert and output results
		String id_prefix = doc.docID + "-" + "EV";
		for(int inst_id=0; inst_id < instancelist.size(); inst_id++)
		{
			SentenceAssignment assn = results.get(inst_id);
			SentenceInstance inst = instancelist.get(inst_id);
			String id = id_prefix + "-" + inst_id;
			List<AceEvent> events = inst.getEvents(assn, id, doc.allText);
			for(AceEvent event : events)
			{
				System.out.println(event);
			}
		}
		System.out.println("\n----------------------\n");
		for(int inst_id=0; inst_id < instancelist.size(); inst_id++)
		{
			SentenceAssignment assn = resultsAvg.get(inst_id);
			SentenceInstance inst = instancelist.get(inst_id);
			String id = id_prefix + "-" + inst_id;
			List<AceEvent> events = inst.getEvents(assn, id, doc.allText);
			for(AceEvent event : events)
			{
				System.out.println(event);
			}
		}
		
		PrintStream out = new PrintStream(new File("weights"));
		out.println(perceptron.getWeights().toString());
		out.close();
	}
}
