package edu.cuny.qc.perceptron.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.dom4j.DocumentException;

import edu.cuny.qc.ace.acetypes.AceDocument;
import edu.cuny.qc.ace.acetypes.AceEntity;
import edu.cuny.qc.ace.acetypes.AceEvent;
import edu.cuny.qc.ace.acetypes.AceRelation;
import edu.cuny.qc.ace.acetypes.AceTimex;
import edu.cuny.qc.ace.acetypes.AceValue;
import edu.cuny.qc.ace.acetypes.Scorer;
import edu.cuny.qc.ace.acetypes.Scorer.Stats;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.DocumentCrossSent;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;

public class Decoder
{
	public static String OPTION_NO_SCORING = "-n"; 
	public static File outDir = null; //TODO DEBUG
	
	public static void writeEntities (PrintWriter w, AceDocument aceDoc, List<AceEvent> events) {
		w.println ("<?xml version=\"1.0\"?>");
		w.println ("<!DOCTYPE source_file SYSTEM \"apf.v5.1.1.dtd\">");
		w.print   ("<source_file URI=\"" + aceDoc.sourceFile + "\"");
		w.println (" SOURCE=\"" + aceDoc.sourceType + "\" TYPE=\"text\">");
		w.println ("<document DOCID=\"" + aceDoc.docID + "\">");
		for (int i=0; i<aceDoc.entities.size(); i++) {
			AceEntity entity = (AceEntity) aceDoc.entities.get(i);
			entity.write(w);
		}
		for (int i=0; i<aceDoc.values.size(); i++) {
			AceValue value = (AceValue) aceDoc.values.get(i);
			value.write(w);
		}
		for (int i=0; i<aceDoc.timeExpressions.size(); i++) {
			AceTimex timex = (AceTimex) aceDoc.timeExpressions.get(i);
			timex.write(w);
		}
		for (int i=0; i<aceDoc.relations.size(); i++) {
			AceRelation relation = (AceRelation) aceDoc.relations.get(i);
			relation.write(w);
		}
		for (int i=0; i<events.size(); i++) {
			AceEvent event = (AceEvent) events.get(i);
			event.write(w);
		}
		w.println ("</document>");
		w.println ("</source_file>");
		w.close();
	}
	
	public static void mainNoScoring(String[] args, String filenameSuffix, String folderNamePrefix) throws IOException, DocumentException
	{
		System.out.printf("Args:\n%s\n\n", new ArrayList<String>(Arrays.asList(args)));
		//if((args.length < 4) || (args.length>=5 && !args[4].equals(OPTION_NO_SCORING)))
		if((args.length < 4) || (args.length>5))
		{
			System.out.println("Usage:");
			System.out.println("args[0]: model");
			System.out.println("args[1]: src dir");
			System.out.println("args[2]: file list");
			System.out.println("args[3]: output dir");
			System.out.printf("optional args[4]: '%s' to not perform scoring, or anything else as a suffix for file names of intermediate output files\n", OPTION_NO_SCORING);
			System.exit(-1);
		}
		
		System.err.println("(Decoding err stream)");
		
		File srcDir = new File(args[1]);
		File fileList = new File(args[2]);
		outDir = new File(args[3]);   //TODO DEBUG
		//File outDir = new File(args[3]);
		if(!outDir.exists())
		{
			outDir.mkdirs();
		}
		
		// Perceptron read model from the serialized file
		Perceptron perceptron = Perceptron.deserializeObject(new File(args[0]));
		Alphabet nodeTargetAlphabet = perceptron.nodeTargetAlphabet;
		Alphabet edgeTargetAlphabet = perceptron.edgeTargetAlphabet;
		Alphabet featureAlphabet = perceptron.featureAlphabet;
		
		//Intermediate output - all features+weights to text files
		String s;
		PrintStream featuresOut = new PrintStream(new File(outDir + File.separator + "FeatureAlphabet" + filenameSuffix));
		for (Object o : featureAlphabet.toArray()) {
			s = (String) o;
			featuresOut.printf("%s\n", s);
		}
		featuresOut.close();
		
		PrintStream weightsOut = new PrintStream(new File(outDir + File.separator + "Weights" + filenameSuffix));
		weightsOut.printf("%s", perceptron.getWeights().toString());
		weightsOut.close();
		
		PrintStream avgWeightsOut = new PrintStream(new File(outDir + File.separator + "AvgWeights" + filenameSuffix));
		avgWeightsOut.printf("%s", perceptron.getAvg_weights().toString());
		avgWeightsOut.close();
				
		System.out.printf("--------------\nPerceptron.controller =\n%s\r\n\r\n--------------------------\r\n\r\n", perceptron.controller);
		
		BufferedReader reader = new BufferedReader(new FileReader(fileList));
		String line = "";
		TextFeatureGenerator featGen = new TextFeatureGenerator();
		while((line = reader.readLine()) != null)
		{
			List<SentenceInstance> localInstanceList = null;
			boolean monoCase = line.contains("bn/") ? true : false;
			String fileName = srcDir + File.separator + line;
			System.out.println(fileName);
			Document doc = null;
			if(perceptron.controller.crossSent)
			{
				doc = new DocumentCrossSent(fileName, true, monoCase);
				// fill in text feature vector for each token
				featGen.fillTextFeatures(doc);
				((DocumentCrossSent) doc).setSentenceClustersByTokens();	
			}
			else
			{
				doc = new Document(fileName, true, monoCase);
				// fill in text feature vector for each token
				featGen.fillTextFeatures(doc);
			}
			localInstanceList = doc.getInstanceList(nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, 
					perceptron.controller, true);
			
			// decoding
			List<SentenceAssignment> localResults = perceptron.decoding(localInstanceList);
			
			// print to docs
			File outputFile = new File(outDir + File.separator + folderNamePrefix + line);
			if(!outputFile.getParentFile().exists())
			{
				outputFile.getParentFile().mkdirs();
			}
			String docID = doc.docID.substring(doc.docID.lastIndexOf(File.separator) + 1);
			String id_prefix = docID + "-" + "EV";
			PrintWriter out = new PrintWriter(outputFile);
			
			// output entities and predicted events from doc
			List<AceEvent> eventsInDoc = new ArrayList<AceEvent>();
			
			List<SentenceInstance> canonicalList = perceptron.getCanonicalInstanceList(localInstanceList);
			for(int inst_id=0; inst_id < canonicalList.size(); inst_id++)
			{
				SentenceAssignment assn = localResults.get(inst_id);
				SentenceInstance inst = canonicalList.get(inst_id);
				String id = id_prefix + inst_id;
				// each event only contains one single event mention
				List<AceEvent> events = inst.getEvents(assn, id, doc.allText);
				eventsInDoc.addAll(events);
			}
			writeEntities(out, doc.getAceAnnotations(), eventsInDoc);
			out.close();
		}
		
		System.out.printf("[%s] --------------\r\nPerceptron.controller =\r\n%s\r\n\r\n--------------------------\r\n\r\n", new Date(), perceptron.controller);
	}
	
	public static Stats mainWithScoring(String[] args, String filenameSuffix, String folderNamePrefix) throws IOException, DocumentException {
		mainNoScoring(args, filenameSuffix, folderNamePrefix);
		
		File outputFile = new File(outDir + File.separator + "Score" + filenameSuffix);
		Stats stats = Scorer.mainMultiRunReturningStats(folderNamePrefix, new String[]{args[1], args[3], args[2], outputFile.getAbsolutePath()});
		return stats;
	}

	public static void main(String[] args) throws IOException, DocumentException {
		//TODO the organization here is bad, should be improved:
		// 1. there's no way to specify both a filenameSufix and NO_SCORING
		// 2. The check for enough args is only done later, so we'll get an exception
		String filenameSuffix = ".txt";
		String folderNamePrefix = "";
		if (args.length>=5) {
			if (args[4].equals(OPTION_NO_SCORING)) {
				mainNoScoring(args, filenameSuffix, folderNamePrefix);
			}
			else {
				filenameSuffix = args[4];
				folderNamePrefix = "DIR" + args[4] + "."; //yes, I know it's silly it has ".txt" in it, maybe should fix later
				mainWithScoring(args, filenameSuffix, folderNamePrefix);
			}
		}
	}
}
