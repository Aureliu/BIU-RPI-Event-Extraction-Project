package edu.cuny.qc.perceptron.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.dom4j.DocumentException;
import org.uimafit.util.JCasUtil;
import org.xml.sax.SAXException;

import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.InputMetadata;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate;
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
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.util.TypeConstraints;
import edu.cuny.qc.util.UnsupportedParameterException;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public class Decoder
{
	public static final String OPTION_NO_SCORING = "-n";
	public static final String PREPROCESSED_SPEC_FILE_EXT = ".preprocessed";
	public static File outDir = null; //TODO DEBUG
	
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
	
	private static JCas getPreprocessedSpec(String specXmlPath, Perceptron perceptron) throws InvalidXMLException, ResourceInitializationException, SAXException, IOException, AnalysisEngineProcessException {
		JCas spec = null;
		File preprocessed = new File(specXmlPath + PREPROCESSED_SPEC_FILE_EXT);
		if (preprocessed.isFile()) {
			spec = UimaUtils.loadXmi(preprocessed);
		}
		else {
			AnalysisEngine ae = UimaUtils.loadAE(SpecAnnotator.ANNOTATOR_FILE_PATH);
			JCas jcas = ae.newJCas();
			jcas.setDocumentLanguage("EN");
			
			SpecAnnotator myAe = (SpecAnnotator) ae;
			myAe.setPerceptorn(perceptron);

			InputMetadata meta = new InputMetadata(jcas);
			meta.setInputFilePath(specXmlPath);
			meta.addToIndexes();
			
			ae.process(jcas);

			try {
				UimaUtils.dumpXmi(preprocessed, spec);
			}
			catch (SAXException e) {
				Files.deleteIfExists(preprocessed.toPath());
				throw e;
			}
			catch (IOException e) {
				Files.deleteIfExists(preprocessed.toPath());
				throw e;
			}
		}
		
		return spec;
	}


	public static void decode(String[] args, String filenameSuffix, String folderNamePrefix, String singleEventType, File specListFile) throws IOException, DocumentException, AnalysisEngineProcessException, InvalidXMLException, ResourceInitializationException, SAXException
	{
		List<String> specPaths = FileUtils.loadFileToList(specListFile);
		decode(args, filenameSuffix, folderNamePrefix, singleEventType, specPaths);
	}
	
	public static void decode(String[] args, String filenameSuffix, String folderNamePrefix, String singleEventType, List<String> specXmlPaths) throws IOException, DocumentException, AnalysisEngineProcessException, InvalidXMLException, ResourceInitializationException, SAXException
	{		
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
		weightsOut.printf("%s", perceptron.getWeights().toStringFull());
		weightsOut.close();
		
		// no need in printing that - we have the model.weights file! With exact same data!!! :)
		//PrintStream avgWeightsOut = new PrintStream(new File(outDir + File.separator + "AvgWeights" + filenameSuffix));
		//avgWeightsOut.printf("%s", perceptron.getAvg_weights().toString());
		//avgWeightsOut.close();
				
		System.out.printf("--------------\nPerceptron.controller =\n%s\r\n\r\n--------------------------\r\n\r\n", perceptron.controller);
		
		// handle specs
		List<JCas> specs = new ArrayList<JCas>(specXmlPaths.size());
		List<String[]> linesForArgs = new ArrayList<String[]>();
		for (String specXmlPath : specXmlPaths) {
			JCas spec = getPreprocessedSpec(specXmlPath, perceptron);
			specs.add(spec);

			Predicate predicate = JCasUtil.selectSingle(spec, Predicate.class);
			String predicateName = predicate.getName();
			TypeConstraints.addSpecType(predicateName);
			perceptron.nodeTargetAlphabet.lookupIndex(predicateName);
			
			for (Argument arg : JCasUtil.select(spec, Argument.class)) {
				String role = arg.getRole();
				List<String> types = Arrays.asList(arg.getTypes().toStringArray());
				perceptron.edgeTargetAlphabet.lookupIndex(predicateName);
				
				List<String> lineList = new ArrayList<String>();
				lineList.add(predicateName);
				lineList.add(role);
				lineList.addAll(types);
				linesForArgs.add((String[]) lineList.toArray());
			}
		}
		perceptron.fillLabelBigrams();
		TypeConstraints.fillArgRolesAndTypesLists(linesForArgs);
		
		
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
				throw new UnsupportedParameterException("crossSent = true");
			}
			else
			{
				doc = Document.createAndPreprocess(fileName, true, monoCase, true, true, singleEventType);
				// fill in text feature vector for each token
				featGen.fillTextFeatures_NoPreprocessing(doc);
			}
			localInstanceList = doc.getInstanceList(perceptron, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, 
					perceptron.controller, true);
			
			// decoding
			List<SentenceAssignment> localResults = perceptron.decoding(localInstanceList);
			
			// print to docs
			File outputFile = new File(outDir + File.separator + folderNamePrefix + line + ".apf.xml");
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
	
	public static Stats decodeAndScore(String[] args, String filenameSuffix, String folderNamePrefix, String singleEventType, File specListFile) throws IOException, DocumentException, AnalysisEngineProcessException, InvalidXMLException, ResourceInitializationException, SAXException {
		decode(args, filenameSuffix, folderNamePrefix, singleEventType, specListFile);
		
		File outputFile = new File(outDir + File.separator + "Score" + filenameSuffix);
		Stats stats = Scorer.mainMultiRunReturningStats(folderNamePrefix, singleEventType, new String[]{args[1], args[3], args[2], outputFile.getAbsolutePath()});
		return stats;
	}

	public static void main(String[] args) throws IOException, DocumentException, AnalysisEngineProcessException, InvalidXMLException, ResourceInitializationException, SAXException {
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
				decode(args, filenameSuffix, folderNamePrefix, null, new File(args[4])); //no singleEventType
			}
			else {
				filenameSuffix = args[5];
				folderNamePrefix = "DIR" + args[5] + "."; //yes, I know it's silly it has ".txt" in it, maybe should fix later
				decodeAndScore(args, filenameSuffix, folderNamePrefix, null, new File(args[4])); //no singleEventType
			}
		}
		else {
			decodeAndScore(args, filenameSuffix, folderNamePrefix, null, new File(args[4]));
		}
	}
}
