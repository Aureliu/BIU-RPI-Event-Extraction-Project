package edu.cuny.qc.perceptron.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ac.biu.nlp.nlp.ie.onthefly.input.AeException;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecException;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecHandler;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;
import edu.cuny.qc.ace.acetypes.AceDocument;
import edu.cuny.qc.ace.acetypes.AceEntity;
import edu.cuny.qc.ace.acetypes.AceEvent;
import edu.cuny.qc.ace.acetypes.AceEventMention;
import edu.cuny.qc.ace.acetypes.AceRelation;
import edu.cuny.qc.ace.acetypes.AceTimex;
import edu.cuny.qc.ace.acetypes.AceValue;
import edu.cuny.qc.ace.acetypes.Scorer;
import edu.cuny.qc.ace.acetypes.Scorer.Stats;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.SignalMechanismsContainer;
import edu.cuny.qc.util.Logs;
import edu.cuny.qc.util.UnsupportedParameterException;
import edu.cuny.qc.util.Utils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;

public class Decoder
{
	public static final String OPTION_NO_SCORING = "-n";
	public static File outDir = null; //TODO DEBUG
	
	// logs
	public static PrintStream wTrain = null, wDev = null;
	public static PrintStream fTrain = null, fDev = null;
	public static PrintStream pTrain = null, pDev = null;
	public static PrintStream uTrain = null, uDev = null;
	public static PrintStream bTrain = null, bDev = null;

	public static void writeEntities (PrintWriter w, AceDocument aceDoc, List<AceEvent> events) {
		w.println ("<?xml version=\"1.0\"?>");
		w.println ("<!DOCTYPE source_file SYSTEM \"apf.v5.1.1.dtd\">");
		w.print   ("<source_file URI=\"" + aceDoc.sourceFile + "\"");
		w.println (" SOURCE=\"" + aceDoc.sourceType + "\" TYPE=\"text\" AUTHOR=\"LDC\" ENCODING=\"UTF-8\">");
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


	public static TypesContainer decode(String[] args, String filenameSuffix, String folderNamePrefix, File specListFile) throws IOException, DocumentException, AnalysisEngineProcessException, InvalidXMLException, ResourceInitializationException, SAXException, CASRuntimeException, CASException, UimaUtilsException, AeException, SignalMechanismException, SpecException
	{
		List<String> specXmlPaths = SpecHandler.readSpecListFile(specListFile);
		return decode(args, filenameSuffix, folderNamePrefix, specXmlPaths);
	}
	
	public static TypesContainer decode(String[] args, String filenameSuffix, String folderNamePrefix, List<String> specXmlPaths) throws IOException, DocumentException, AnalysisEngineProcessException, InvalidXMLException, ResourceInitializationException, SAXException, CASRuntimeException, CASException, UimaUtilsException, AeException, SignalMechanismException, SpecException
	{		
		//System.err.println("(Decoding err stream)");
		
		File srcDir = new File(args[1]);
		File fileList = new File(args[2]);
		outDir = new File(args[3]);   //TODO DEBUG
		//File outDir = new File(args[3]);
		if(!outDir.exists())
		{
			outDir.mkdirs();
		}
		
		TypesContainer types = new TypesContainer(specXmlPaths, false);
		
		// Perceptron read model from the serialized file
		Perceptron perceptron = Perceptron.deserializeObject(new File(args[0]));
		//Alphabet nodeTargetAlphabet = types.nodeTargetAlphabet;
		//Alphabet edgeTargetAlphabet = types.edgeTargetAlphabet;
		Alphabet featureAlphabet = perceptron.featureAlphabet;
		perceptron.setController(perceptron.controller); //Yes, I know this looks weird - it's because of the static controller hack
		SignalMechanismsContainer signalMechanismsContainer = new SignalMechanismsContainer(perceptron.controller);
		perceptron.setSignalMechanismsContainer(signalMechanismsContainer);

		Logs logs = new Logs(outDir, perceptron.controller, "");
		try {
			wTrain = logs.getW("Train");
			fTrain = logs.getF("Train");
			fDev =   logs.getF("Dev");
			pTrain = logs.getP("Train");
			pDev =   logs.getP("Dev");
			uTrain = logs.getU("Train");
			uDev =   logs.getU("Dev");
			bTrain = logs.getB("Train");
			bDev =   logs.getB("Dev");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		logs.logTitles(wTrain, fTrain, pTrain, uTrain, bTrain);
		logs.logTitles(null, fDev, pDev, uDev, bDev);
		
		//Intermediate output - all features+weights to text files
//		String s;
//		PrintStream featuresOut = new PrintStream(new File(outDir + "/" + "FeatureAlphabet" + filenameSuffix));
//		for (Object o : featureAlphabet.toArray()) {
//			s = (String) o;
//			featuresOut.printf("%s\n", s);
//		}
//		featuresOut.close();
		
		PrintStream weightsOut = new PrintStream(new File(outDir + "/" + "Weights" + filenameSuffix));
		weightsOut.printf("%s", perceptron.getWeights().toStringFull());
		weightsOut.close();
		
		// no need in printing that - we have the model.weights file! With exact same data!!! :)
		//PrintStream avgWeightsOut = new PrintStream(new File(outDir + File.separator + "AvgWeights" + filenameSuffix));
		//avgWeightsOut.printf("%s", perceptron.getAvg_weights().toString());
		//avgWeightsOut.close();
				
		System.out.printf("--------------\nPerceptron.controller =\n%s\r\n\r\n--------------------------\r\n\r\n", perceptron.controller);

		
		BufferedReader reader = new BufferedReader(new FileReader(fileList));
		String line = "";
		//TextFeatureGenerator featGen = new TextFeatureGenerator();
		int num = 0;
		while((line = reader.readLine()) != null)
		{
			num++;
			
			Collection<SentenceInstance> localInstanceList = null;
			boolean monoCase = line.contains("bn/") ? true : false;
			String fileName = srcDir + "/" + line;
			System.out.printf("[%s] %s\n", new Date(), fileName);
			Document doc = null;
			if(perceptron.controller.crossSent)
			{
				throw new UnsupportedParameterException("crossSent = true");
			}
			else
			{
				doc = Document.createAndPreprocess(fileName, true, monoCase, perceptron.controller.usePreprocessFiles, perceptron.controller.usePreprocessFiles, types, perceptron.controller, signalMechanismsContainer);
				// fill in text feature vector for each token
				//featGen.fillTextFeatures_NoPreprocessing(doc);
			}
			localInstanceList = doc.getInstances(signalMechanismsContainer, types, featureAlphabet, 
					perceptron.controller, true, false).values();
			
			doc.dumpSignals(/*localInstanceList, types, */perceptron.controller);

			// decoding
			List<SentenceAssignment> localResults = perceptron.decoding(logs, localInstanceList, -1, -1,
					perceptron.weights, perceptron.avg_weights, perceptron.avg_weights_base, null, null, null);
			
			// print to docs
			File outputFile = new File(outDir + "/" + folderNamePrefix + line + ".apf.xml");
			if(!outputFile.getParentFile().exists())
			{
				outputFile.getParentFile().mkdirs();
			}
			String docID = doc.docID;//.substring(doc.docID.lastIndexOf("/") + 1);
			String id_prefix = docID + "-" + "EV";
			PrintWriter out = new PrintWriter(outputFile);
			
			// output entities and predicted events from doc
			List<AceEvent> eventsInDoc = new ArrayList<AceEvent>();
			
			//List<SentenceInstance> canonicalList = perceptron.getCanonicalInstanceList(localInstanceList);
			int inst_id=0;
			for (Iterator<SentenceInstance> iter = localInstanceList.iterator(); iter.hasNext();)
			{
				SentenceAssignment assn = localResults.get(inst_id);
				SentenceInstance inst = iter.next();
				String id = id_prefix + inst_id;
				// each event only contains one single event mention
				List<AceEvent> events = inst.getEvents(assn, id, doc.allText);
				eventsInDoc.addAll(events);
				inst_id++;
			}
			System.err.println("??? Decoder: Calls SentenceInstance.getEvents(), which uses TypeConstraints.eventTypeMap, which is not consistent with specs, so I'm not sure what should happen here...");
			writeEntities(out, doc.getAceAnnotations(), eventsInDoc);
			out.close();
			
			System.out.printf("%s Finished processing document %s\n", Utils.detailedLog(), doc.docID);
		}
		
		System.out.printf("%s Decoder: Finished processing all %s documents.\n", Utils.detailedLog(), num);

		perceptron.close();

		return types;
	}
	
	public static Stats decodeAndScore(String[] args, String filenameSuffix, String folderNamePrefix, File specListFile) throws IOException, DocumentException, AnalysisEngineProcessException, InvalidXMLException, ResourceInitializationException, SAXException, CASRuntimeException, CASException, UimaUtilsException, AeException, SignalMechanismException, SpecException {
		TypesContainer types = decode(args, filenameSuffix, folderNamePrefix, specListFile);
		
		File outputFile = new File(outDir + "/" + "Score" + filenameSuffix);
		PrintStream out = new PrintStream(outputFile);
		Stats stats = Scorer.mainMultiRunReturningStats(args[1], args[3], args[2], types, out, folderNamePrefix);
		return stats;
	}

	public static void main(String[] args) throws IOException, DocumentException, AnalysisEngineProcessException, InvalidXMLException, ResourceInitializationException, SAXException, CASRuntimeException, CASException, UimaUtilsException, AeException, SignalMechanismException, SpecException {
		//TODO the organization here is bad, should be improved:
		// 1. there's no way to specify both a filenameSufix and NO_SCORING

		System.out.printf("Args:\n%s\n\n", new ArrayList<String>(Arrays.asList(args)));
		if((args.length < 5) || (args.length>6))
		{
			System.out.println("Usage:");
			System.out.println("args[0]: model");
			System.out.println("args[1]: src dir");
			System.out.println("args[2]: file list");
			System.out.println("args[3]: output dir");
			System.out.println("args[4]: spec list");
			System.out.printf("optional args[5]: '%s' to not perform scoring, or anything else as a suffix for file names of intermediate output files\n", OPTION_NO_SCORING);
			System.exit(-1);
		}

		String filenameSuffix = ".txt";
		String folderNamePrefix = "";
		if (args.length>=6) {
			if (args[5].equals(OPTION_NO_SCORING)) {
				decode(args, filenameSuffix, folderNamePrefix, new File(args[4]));
			}
			else {
				filenameSuffix = args[5];
				folderNamePrefix = "DIR" + args[5] + "."; //yes, I know it's silly it has ".txt" in it, maybe should fix later
				decodeAndScore(args, filenameSuffix, folderNamePrefix, new File(args[4]));
			}
		}
		else {
			decodeAndScore(args, filenameSuffix, folderNamePrefix, new File(args[4]));
		}
	}
}
