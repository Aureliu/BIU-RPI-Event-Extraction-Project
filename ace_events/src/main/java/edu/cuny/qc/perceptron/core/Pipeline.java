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

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.resource.ResourceInitializationException;
import org.dom4j.DocumentException;

import ac.biu.nlp.nlp.ie.onthefly.input.AeException;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecHandler;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.BundledSignals;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.util.UnsupportedParameterException;
import edu.cuny.qc.util.Utils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;

public class Pipeline
{
	//DEBUG
	public static File modelFile = null;
	public static final int DOCUMENT_GC_FREQ = 100;
	private static final Runtime runtime = Runtime.getRuntime();
	public static final double MB = 1024.0*1024;
	///////////

	public static double inMB(long bytes) {
		return bytes / MB;
	}
	public static String detailedLog() {
		//Runtime runtime = Runtime.getRuntime();
		long max = runtime.maxMemory();
		long total = runtime.totalMemory();
		return String.format("[%1$tH:%1$tM:%1$tS.%1$tL max=%2$.2f, total=%3$.2f, used=%4$.2f]",
				new Date(),
				max==Long.MAX_VALUE? "no limit" : inMB(max), 
				inMB(total), 
				inMB(total - runtime.freeMemory()));
	}
	/**
	 * Given the document list, train a perceptron model, and write to modelFile
	 * @param srcDir
	 * @param trainingFileList
	 * @param modelFile
	 */
	public static Perceptron trainPerceptron(File srcDir, File trainingFileList, File modelFile, File devFileList, Controller controller, List<String> trainSpecXmlPaths, List<String> devSpecXmlPaths) throws Exception
	{
		Alphabet featureAlphabet = new Alphabet();
		
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
		PrintStream m = new PrintStream(new File(modelFile.getAbsolutePath() + ".start"));
		m.close();

		// read instance list from training data (and dev data)
		List<SentenceInstance> trainInstanceList = null;
		List<SentenceInstance> devInstanceList = null;
		Perceptron model = null;
		
		if(!controller.crossSent)
		{
			model = new Perceptron(featureAlphabet);
			model.controller = controller;
			TypesContainer trainTypes = new TypesContainer(trainSpecXmlPaths, false);
			TypesContainer devTypes = new TypesContainer(devSpecXmlPaths, false);
			trainInstanceList = readInstanceList(model, trainTypes, srcDir, trainingFileList, featureAlphabet, true, false);
			devInstanceList = readInstanceList(model, devTypes, srcDir, devFileList, featureAlphabet, false, false);
		}
		else
		{
			throw new UnsupportedParameterException("crossSent = true");
		}
		
		//DEBUG
		Pipeline.modelFile = modelFile;
		//////////////////

		

		// learning
		model.learning(trainInstanceList, devInstanceList, 0);
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
	public static List<SentenceInstance> readInstanceList(Perceptron perceptron,
			TypesContainer types, File srcDir, File file_list, Alphabet featureAlphabet, 
			boolean learnable, boolean debug) throws IOException, DocumentException, CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, CASException, UimaUtilsException, AeException
	{
		System.out.printf("%s Reading instance list ...\n", detailedLog());
		
		List<SentenceInstance> instancelist = new ArrayList<SentenceInstance>();
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		//TextFeatureGenerator featGen = new TextFeatureGenerator();
		try {
			int num = 0;
			while((line = reader.readLine()) != null)
			{
				num++;
				if (num % DOCUMENT_GC_FREQ == 0) {
					System.out.printf("***%s running gc...", detailedLog());
					System.gc();
					System.out.printf("%s done.\n", detailedLog());					
				}
				
				boolean monoCase = line.contains("bn/") ? true : false;
				String fileName = srcDir + "/" + line;
				
				System.out.printf("[%s] %s\n", new Date(), fileName);
				
				
				Document doc = Document.createAndPreprocess(fileName, true, monoCase, true, true, types, perceptron);
				// fill in text feature vector for each token
				//featGen.fillTextFeatures_NoPreprocessing(doc);
				
				//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] running gc...", new Date());
				//System.gc();
				//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] done.\n", new Date());

				
				List<SentenceInstance> docInstancelist = new ArrayList<SentenceInstance>();
				for(int sent_id=0 ; sent_id<doc.getSentences().size(); sent_id++)
				{
					Sentence sent = doc.getSentences().get(sent_id);
					// during learning, skip instances that do not have event mentions 
					
					// 1.6.14: Create instances even if we skip them - to fully create their signals  
					//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] sent_id=%2$s, insts..", new Date(), sent_id);
					List<SentenceInstance> insts = Document.getInstancesForSentence(perceptron, sent, types, featureAlphabet, learnable, debug);
					//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] done(%2$d).", new Date(), insts.size());
					docInstancelist.addAll(insts);
					
					if(learnable && perceptron.controller.skipNonEventSent)
					{
						if(sent.eventMentions != null && sent.eventMentions.size() > 0)
						{
							//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] add\n", new Date());
							instancelist.addAll(insts);
						}
					}
					else // add all instances
					{
						//List<SentenceInstance> insts = Document.getInstancesForSentence(perceptron, sent, types, featureAlphabet, learnable);
						//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] add\n", new Date());
						instancelist.addAll(insts);
					}
					
					//System.gc();

				}
				/// DEBUG
//				if (doc.docID.contains("CNN_ENG_20030506_160524.18")) {
//					System.out.println(docInstancelist.size());
//				}
				///
				doc.dumpSignals(docInstancelist, types, perceptron);
				/// DEBUG
//				BundledSignals dumpedSignals = doc.signals;
//				doc.loadSignals(types);
//				boolean b = dumpedSignals.equals(doc.signals);
//				System.out.println(b);
//				////
			}
		}
		finally {
			reader.close();
		}
		
		System.out.printf("%s FINAL GC...", detailedLog());
		System.gc();
		System.out.printf("%s done.\n", detailedLog());
		System.out.println("done");
		return instancelist;
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
		if(args.length < 5)
		{
			System.out.println("Training perceptron Usage:");
			System.out.println("args[0]: source dir of training data");
			System.out.println("args[1]: file list of training data");
			System.out.println("args[2]: model file to be saved");
			System.out.println("args[3]: file list of dev data");
			System.out.println("args[4]: training spec list");
			System.out.println("args[5]: dev spec list");
			System.out.println("args[6+]: controller arguments");
			System.exit(-1);
		}
		
		System.out.printf("\n[%s] Starting Pipeline...\n", new Date());

		File srcDir = new File(args[0]);
		File trainingFileList = new File(args[1]);
		File modelFile = new File(args[2]);
		File devFileList = new File(args[3]);
		File trainSpecListFile = new File(args[4]);
		File devSpecListFile = new File(args[5]);
		List<String> trainSpecXmlPaths = SpecHandler.readSpecListFile(trainSpecListFile);
		List<String> devSpecXmlPaths = SpecHandler.readSpecListFile(devSpecListFile);
		
		// set settings
		Controller controller = new Controller();
		String[] settings = Arrays.copyOfRange(args, 6, args.length);
		controller.setValueFromArguments(settings);
		System.out.println("\n" + controller.toString() + "\n");
		
		PrintStream out = null;
		if (controller.logLevel >= 1) {
			out = new PrintStream(modelFile.getAbsoluteFile() + "." + controller.logLevel + ".weights");
		}

		// train model
		Perceptron model = trainPerceptron(srcDir, trainingFileList, modelFile, devFileList, controller, trainSpecXmlPaths, devSpecXmlPaths);
		
		// print out weights
		if(model.controller.avgArguments)
		{
			Utils.print(out, "", "", "", model.getAvg_weights().toStringFull());			
		}
		else
		{
			Utils.print(out, "", "", "", model.getWeights().toStringFull());			
		}
		
		if (out != null	) {
			out.close();
		
		model.close();
		
		System.out.printf("\n[%s] Finished Pipeline successfully\n", new Date());
		}
	}
}
