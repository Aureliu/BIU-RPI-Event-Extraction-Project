package edu.cuny.qc.perceptron.core;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.Multimap;

import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.scorer.SignalMechanismsContainer;
import edu.cuny.qc.util.Utils;

import ac.biu.nlp.nlp.ie.onthefly.input.SpecHandler;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;

public class Folds {
	public static final int BUILD_RUN_FAIL_RATIO = 100;
	private static final String CORPUS_DIR = "src/main/resources/corpus/qi";
	private static final File TRAIN_DOCS = new File("src/main/resources/doclists/new_filelist_ACE_training.txt");
	private static final File DEV_DOCS = new File("src/main/resources/doclists/new_filelist_ACE_dev.txt");
	private static final File TEST_DOCS = new File("src/main/resources/doclists/new_filelist_ACE_test.txt");
	private static final String CONTROLLER_PARAMS =
			"beamSize=4 maxIterNum=20 skipNonEventSent=true avgArguments=true skipNonArgument=true useGlobalFeature=false " +
			"addNeverSeenFeatures=true crossSent=false crossSentReranking=false order=0 evaluatorType=1 learnBigrams=true logLevel=8 " +
			"oMethod=G0P- serialization=BZ2 featureProfile=ANALYSIS usePreprocessFiles=true useSignalFiles=true";
	
	public static class Run {
		public Map<String, JCas> trainEvents, devEvents, testEvents;
	}
	public static void main(String args[]) throws Exception {
		
		File outputFolder = new File(args[0]);
		List<String> allSpecs = SpecHandler.readSpecListFile(new File(args[0]));
		Integer numRuns = Integer.parseInt(args[0]);
		Integer minTrainEvents = Integer.parseInt(args[0]);
		Integer maxTrainEvents = Integer.parseInt(args[0]);
		Integer minDevEvents = Integer.parseInt(args[0]);
		Integer maxDevEvents = Integer.parseInt(args[0]);
		Integer minTrainMentions = Integer.parseInt(args[0]);
		Integer minDevMentions = Integer.parseInt(args[0]);
				
		File corpusDir = new File(CORPUS_DIR);
		TypesContainer types = new TypesContainer(allSpecs, false);
		Controller controller = new Controller();
		controller.setValueFromArguments(StringUtils.split(CONTROLLER_PARAMS));
		SignalMechanismsContainer signalMechanismsContainer = new SignalMechanismsContainer(controller);
		Perceptron perceptron = new Perceptron(null, controller, outputFolder, signalMechanismsContainer);

		List<Run> runs = buildRuns();
		
		Multimap<JCas, SentenceInstance> trainInstances = Pipeline.readInstanceList(perceptron, types, corpusDir, TRAIN_DOCS, new Alphabet(), true, false);
		System.out.printf("%s Finished reading training documents: %s sentence instances (total for all %s types)\n", Utils.detailedLog(), trainInstances.size(), trainInstances.keySet().size());
		Multimap<JCas, SentenceInstance> devInstances = Pipeline.readInstanceList(perceptron, types, corpusDir, DEV_DOCS, new Alphabet(), false, false);
		System.out.printf("%s Finished reading dev documents: %s sentence instances (total for all %s types)\n", Utils.detailedLog(), devInstances.size());
		Multimap<JCas, SentenceInstance> testInstances = Pipeline.readInstanceList(perceptron, types, corpusDir, TEST_DOCS, new Alphabet(), true, false);
		System.out.printf("%s Finished reading test documents: %s sentence instances (total for all %s types)\n", Utils.detailedLog(), testInstances.size());

		for (Run run : runs) {
			model.learning(trainInstanceList, devInstanceList, 0, logSuffix);
			Perceptron.serializeObject(model, modelFile);
			
		}
	}
}
