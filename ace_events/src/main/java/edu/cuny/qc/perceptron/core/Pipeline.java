package edu.cuny.qc.perceptron.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dom4j.DocumentException;
import org.springframework.util.LinkedMultiValueMap;

import ac.biu.nlp.nlp.ie.onthefly.input.AeException;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecHandler;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import edu.cuny.qc.ace.acetypes.AceEventMention;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.scorer.SignalMechanismsContainer;
import edu.cuny.qc.util.Logs;
import edu.cuny.qc.util.PosMap;
import edu.cuny.qc.util.UnsupportedParameterException;
import edu.cuny.qc.util.Utils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;

public class Pipeline
{
	//DEBUG
	public static File modelFile = null;
	public static final int DOCUMENT_GC_FREQ = 100;
	
	/**
	 * Given the document list, train a perceptron model, and write to modelFile
	 * @param srcDir
	 * @param trainingFileList
	 * @param modelFile
	 */
	public static Perceptron trainPerceptron(File srcDir, File trainingFileList, File modelFile, File devFileList, Controller controller, List<String> trainSpecXmlPaths, List<String> devSpecXmlPaths, String logSuffix) throws Exception
	{
		Alphabet featureAlphabet = new Alphabet();
		File outFolder = new File(modelFile.getParent());
		
		File prevModelFile = new File(modelFile.getAbsolutePath() + ".previous");
		if (modelFile.isFile()) {
			prevModelFile.delete();
			modelFile.renameTo(prevModelFile);
		}
			
		// Make sure model file is writable
		PrintStream stream = new PrintStream(modelFile);
		stream.printf("(file is writable - verified)");
		stream.close();

		// start marker
		PrintStream m = new PrintStream(new File(modelFile.getAbsolutePath() + logSuffix + ".start"));
		m.close();

		// read instance list from training data (and dev data)
		Collection<SentenceInstance> trainInstanceList = null;
		Collection<SentenceInstance> devInstanceList = null;
		Perceptron model = null;
		SignalMechanismsContainer signalMechanismsContainer = new SignalMechanismsContainer(controller);
		
		if(!controller.crossSent)
		{
			model = new Perceptron(featureAlphabet, controller, outFolder, signalMechanismsContainer);
			TypesContainer trainTypes = new TypesContainer(trainSpecXmlPaths, false);
			TypesContainer devTypes = new TypesContainer(devSpecXmlPaths, false);
			trainInstanceList = readInstanceList(controller, signalMechanismsContainer, trainTypes, srcDir, trainingFileList, featureAlphabet, null, true, false).values();
			System.out.printf("=== Finished Training Documents (%s Sentence Instances) =====================================\n", trainInstanceList.size());
			devInstanceList = readInstanceList(controller, signalMechanismsContainer, devTypes, srcDir, devFileList, featureAlphabet, null, false, false).values();
			System.out.printf("=== Finished Dev Documents (%s Sentence Instances) =====================================\n", devInstanceList.size());
		}
		else
		{
			throw new UnsupportedParameterException("crossSent = true");
		}
		
		System.out.printf("%s Finished reading %s train sentence instances and %s dev sentence instances.\n=================================\n",
				Utils.detailedLog(), trainInstanceList.size(), devInstanceList.size());
		//DEBUG
		Pipeline.modelFile = modelFile;
		//////////////////

		

		// learning
		model.learning(trainInstanceList, devInstanceList, 0, logSuffix, true, true);
		// save learned perceptron to file
		Perceptron.serializeObject(model, modelFile);
		
		return model;
	}
	
	/**
	 * give a file list and home dir, get an instance list
	 * @param srcDir
	 * @param file_list
	 * @throws IOException
	 * @throws DocumentException
	 * @throws AeException 
	 * @throws UimaUtilsException 
	 * @throws CASException 
	 * @throws ResourceInitializationException 
	 * @throws AnalysisEngineProcessException 
	 * @throws CASRuntimeException 
	 */
	public static Multimap<JCas, SentenceInstance> readInstanceList(/*Perceptron perceptron,*/
			Controller controller, SignalMechanismsContainer signalMechanismsContainer,
			TypesContainer types, File srcDir, File file_list, Alphabet featureAlphabet, 
			Map<String, Integer> numMentions, boolean learnable, boolean debug) throws IOException, DocumentException, CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, CASException, UimaUtilsException, AeException
	{
		System.out.printf("\n%s Reading instance list. srcDir=%s, file_list=%s, numMentions=%s, learnable=%s, debug=%s, featureAlphabet=%s, types=%s, signals=%s\n",
				Utils.detailedLog(), srcDir, file_list, numMentions, learnable, debug, featureAlphabet, types, signalMechanismsContainer);
		new PosMap();
		
		Multimap<JCas, SentenceInstance> result = LinkedHashMultimap.create(types.specs.size(), 20);
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		Multimap<String, AceEventMention> allEventMentions = HashMultimap.create();
		//TextFeatureGenerator featGen = new TextFeatureGenerator();
		//try {
			int num = 0;
			while((line = reader.readLine()) != null)
			{
//				if (num % DOCUMENT_GC_FREQ == 0) {
//					perceptron.logSignalMechanismsPreDocumentBunch();
//					System.out.printf("***%s running gc...", detailedLog());
//					System.gc();
//					System.out.printf("%s done.\n", detailedLog());					
//				}
				num++;
				
				boolean monoCase = line.contains("bn/") ? true : false;
				String fileName = srcDir + "/" + line;
				
				System.out.printf("[%s] %s\n", new Date(), fileName);
				
				//perceptron.logSignalMechanismsPreDocument();
				Document doc = Document.createAndPreprocess(fileName, line, true, monoCase, controller.usePreprocessFiles, controller.usePreprocessFiles, types, controller, signalMechanismsContainer);
				// fill in text feature vector for each token
				//featGen.fillTextFeatures_NoPreprocessing(doc);
				
				//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] running gc...", new Date());
				//System.gc();
				//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] done.\n", new Date());

				
				//List<SentenceInstance> docInstancelist = new ArrayList<SentenceInstance>();
				for(int sent_id=0 ; sent_id<doc.getSentences().size(); sent_id++)
				{
					Sentence sent = doc.getSentences().get(sent_id);
					// during learning, skip instances that do not have event mentions 
					
					// 1.6.14: Create instances even if we skip them - to fully create their signals  
					//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] sent_id=%2$s, insts..", new Date(), sent_id);
					LinkedHashMap<JCas,SentenceInstance> insts = Document.getInstancesForSentence(controller, signalMechanismsContainer, sent, types, featureAlphabet, learnable, debug);
					//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] done(%2$d).", new Date(), insts.size());
					//docInstancelist.addAll(insts);
					
					// Do the very-very conditional filtering!
					if(learnable && controller.skipNonEventSent)
					{
						if (controller.filterSentenceInstance) {
							for (Entry<JCas,SentenceInstance> entry : insts.entrySet()) {
								SentenceInstance inst = entry.getValue();
								if (inst.eventMentions != null && inst.eventMentions.size() > 0) {
									result.put(entry.getKey(), inst);
								}
							}
						}
						else {
							if(sent.eventMentions != null && sent.eventMentions.size() > 0)
							{
								//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] add\n", new Date());
								Utils.addToMultimap(result, insts);
							}
						}
					}
					else // add all instances
					{
						//List<SentenceInstance> insts = Document.getInstancesForSentence(perceptron, sent, types, featureAlphabet, learnable);
						//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] add\n", new Date());
						Utils.addToMultimap(result, insts);
					}
				}
				System.out.printf("%s Finished reading sentence instances - now we have a total of %d instances from all documents.\n", Utils.detailedLog(), result.size());
				
				doc.dumpSignals(/*docInstancelist, types, */controller);
				for (AceEventMention evMention : doc.getAceAnnotations().eventMentions) {
					allEventMentions.put(evMention.event.subtype, evMention);
				}

			}
//		}
//		finally {
//			reader.close();
//		}
		
//		System.out.printf("%s FINAL GC...", detailedLog());
//		System.gc();
//		System.out.printf("%s done.\n", detailedLog());
//		System.out.println("done");
			
		// DEBUG
		System.out.printf("Finished loading %s documents, in them %s sentence instances (total for all types)\n", num, result.size());
		System.out.printf("Total of %s event mentions, in %s types:\n", allEventMentions.size(), allEventMentions.keySet().size());
		for (String eventType : allEventMentions.keySet()) {
			int mentions = allEventMentions.get(eventType).size();
			System.out.printf(" - %s: %s mentions\n", eventType, mentions);
			if (numMentions != null) {
				numMentions.put(eventType, mentions);
			}
		}
		/////
			
		return result;
	}
	
	/**
	 * This function is to get list of ClusterInstance
	 * it's used for corss-sentence decoding
	 */
//	public static List<SentenceInstance> readInstanceClusters(File srcDir, File file_list, 
//			Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, 
//			Controller controller, boolean learnable) throws IOException, DocumentException
//	{
//		System.out.println("Reading training instance ...");
//		
//		List<SentenceInstance> instancelist = new ArrayList<SentenceInstance>();
//		BufferedReader reader = new BufferedReader(new FileReader(file_list));
//		String line = "";
//		TextFeatureGenerator featGen = new TextFeatureGenerator();
//		while((line = reader.readLine()) != null)
//		{
//			boolean monoCase = line.contains("bn/") ? true : false;
//			String fileName = srcDir + File.separator + line;
//			
//			System.out.println(fileName);
//			
//			DocumentCrossSent doc = new DocumentCrossSent(fileName, true, monoCase);
//			// fill in text feature vector for each token
//			featGen.fillTextFeatures(doc);
//			doc.setSentenceClustersByTokens();
//			for(int cluster_id=0 ; cluster_id<doc.getSentenceClusters().size(); cluster_id++)
//			{
//				List<Sentence> cluster = doc.getSentenceClusters().get(cluster_id);
//				
//				// during learning, skip instances that do not have event mentions 
//				if(learnable && controller.skipNonEventSent)
//				{
//					if(hasEventMention(cluster))
//					{
//						SentenceInstance inst = new ClusterInstance(cluster, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet,
//								controller, learnable);
//						instancelist.add(inst);
//					}
//				}
//				else // add all instances
//				{
//					SentenceInstance inst = new ClusterInstance(cluster, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, 
//							controller, learnable);
//					instancelist.add(inst);
//				}
//			}
//		}
//		
//		System.out.println("done");
//		return instancelist;
//	}

	/**
	 * check if a cluster of sentences contain event mentions
	 * @param cluster
	 * @return
	 */
//	private static boolean hasEventMention(List<Sentence> cluster)
//	{
//		for(Sentence sent : cluster)
//		{
//			if(sent.eventMentions != null && sent.eventMentions.size() > 0)
//			{
//				return true;
//			}
//		}
//		return false;
//	}

	/**
	 * This is a very simple pipeline
	 * @param args
	 * @throws IOException
	 */
	static public void main(String[] args) throws Exception
	{
//		mainWithSingleEventType(args, null);
//	}
//	
//	public static void mainWithSingleEventType(String[] args, String singleEventType) throws IOException {
		System.out.printf("Args:\n%s\n\n", new ArrayList<String>(Arrays.asList(args)));
		if(args.length < 6)
		{
			System.out.println("Training perceptron Usage:");
			System.out.println("args[0]: source dir of training data");
			System.out.println("args[1]: file list of training data");
			System.out.println("args[2]: model file to be saved");
			System.out.println("args[3]: file list of dev data");
			System.out.println("args[4]: training spec list");
			System.out.println("args[5]: dev spec list");
			System.out.println("args[6]: log file name suffix");
			System.out.println("args[7+]: controller arguments");
			System.exit(-1);
		}
		
		System.out.printf("\n[%s] Starting Pipeline...\n", new Date());

		File srcDir = new File(args[0]);
		File trainingFileList = new File(args[1]);
		File modelFile = new File(args[2]);
		File devFileList = new File(args[3]);
		File trainSpecListFile = new File(args[4]);
		File devSpecListFile = new File(args[5]);
		String logSuffix = args[6];
		List<String> trainSpecXmlPaths = SpecHandler.readSpecListFile(trainSpecListFile);
		List<String> devSpecXmlPaths = SpecHandler.readSpecListFile(devSpecListFile);
		
		// set settings
		Controller controller = new Controller();
		String[] settings = Arrays.copyOfRange(args, 7, args.length);
		controller.setValueFromArguments(settings);
		System.out.println("\n" + controller.toString() + "\n");
		
		if (logSuffix.equals("null")) {
			logSuffix = "";
		}
		else {
			logSuffix = "." + logSuffix;
		}
		
		PrintStream weightsOut = null;
		PrintStream weightsAvgOut = null;
		if (controller.logLevel >= Logs.LEVEL_WEIGHTS) {
			weightsOut = new PrintStream(modelFile.getAbsoluteFile() + "." + controller.logLevel + logSuffix + ".weights");
			weightsAvgOut = new PrintStream(modelFile.getAbsoluteFile() + "." + controller.logLevel + logSuffix + ".avg_weights");
		}

		// train model
		Perceptron model = trainPerceptron(srcDir, trainingFileList, modelFile, devFileList, controller, trainSpecXmlPaths, devSpecXmlPaths, logSuffix);
		
		// print out weights
		Utils.print(weightsOut, "", "", "", null, model.getWeights().toStringFull());			
		if(model.controller.avgArguments)
		{
			Utils.print(weightsAvgOut, "", "", "", null, model.getAvg_weights().toStringFull());			
		}

		
		if (weightsOut != null	) {
			weightsOut.close();
			weightsAvgOut.close();
		}
		
		model.close();
		
		System.out.printf("\n[%s] Finished Pipeline successfully\n", new Date());
	}
}
