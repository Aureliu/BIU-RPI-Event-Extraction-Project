package edu.cuny.qc.perceptron.folds;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections15.ListUtils;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import ac.biu.nlp.nlp.ie.onthefly.input.AeException;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecHandler;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import edu.cuny.qc.ace.acetypes.AceDocument;
import edu.cuny.qc.ace.acetypes.AceEventMention;
import edu.cuny.qc.ace.acetypes.Scorer;
import edu.cuny.qc.ace.acetypes.Scorer.Stats;
import edu.cuny.qc.perceptron.core.AllTrainingScores;
import edu.cuny.qc.perceptron.core.ArgOMethod;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Decoder;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.core.Pipeline;
import edu.cuny.qc.perceptron.core.SentenceSortingMethod;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.scorer.SignalMechanismsContainer;
import edu.cuny.qc.util.Logs;
import edu.cuny.qc.util.Utils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;

public class Folds {
	protected static Logger logger;
	public static final int BUILD_RUN_FAIL_RATIO = 10000;
	private static final String CORPUS_DIR = "../ace_events_large_resources/src/main/resources/corpus/qi";
//	private static final File TRAIN_DOCS = new File("src/main/resources/doclists/new_filelist_ACE_training.txt");
//	private static final File DEV_DOCS = new File("src/main/resources/doclists/new_filelist_ACE_dev.txt");
//	private static final File TEST_DOCS = new File("src/main/resources/doclists/new_filelist_ACE_test.txt");
//	private static final String CONTROLLER_PARAMS =
//			"beamSize=4 maxIterNum=5 skipNonEventSent=true avgArguments=true skipNonArgument=true useGlobalFeature=false " +
//			"addNeverSeenFeatures=true crossSent=false crossSentReranking=false order=0 evaluatorType=1 learnBigrams=true logLevel=7 " +
//			"oMethod=G0P- serialization=BZ2 featureProfile=NORMAL usePreprocessedFiles=true useSignalFiles=true useArguments=false " +
//			"logOnlyTheseSentences=5b";
	
	public static List<Run> buildRuns(Controller controller, TypesContainer types, Map<String, Integer> trainMentionsByType, Map<String, Integer> devMentionsByType, int numRuns, int minTrainEvents, int maxTrainEvents, int minDevEvents, int maxDevEvents, int minTrainMentions, int minDevMentions) throws CASException {
		int totalTries = BUILD_RUN_FAIL_RATIO * numRuns;
		List<Run> result = Lists.newArrayListWithCapacity(numRuns*types.specs.size());
		//int prevAmountResult = 0;
		int totalCounter = 0;
		List<JCas> allTestSpecs;
		if (controller.testType != null) {
			allTestSpecs = types.getPartialSpecList(ImmutableList.of(controller.testType));
		}
		else if (controller.testOnlyTypes != null) {
			allTestSpecs = types.getPartialSpecList(controller.testOnlyTypes);			
		}
		else {
			allTestSpecs = types.specs;
		}
		
		for (JCas testSpec : allTestSpecs) {
			int perTestCounter = 0;
			System.out.printf("Starting %s tries for spec...\n", totalTries);
			List<Run> runsForType = Lists.newArrayListWithCapacity(numRuns*allTestSpecs.size());
			for (int n=0; n<totalTries; n++) {
				Run run = new Run();
				run.sentenceSortingMethod = controller.sentenceSortingMethod;
				run.argOMethod = controller.argOMethod;
				
				List<JCas> specsCopy = Lists.newArrayList(types.specs);
				
				run.testEvent = testSpec;
				specsCopy.remove(run.testEvent);
				
				
				if (controller.trainList != null) {
					run.trainEvents = types.getPartialSpecList(controller.trainList);
					if (run.trainEvents.contains(run.testEvent)) { //a little hacky - we have to check for this explicitly, as here we don't the train events from specsCopy
						continue;
					}
				}
				else {
					List<JCas> specsToChooseFrom = specsCopy;
					if (controller.trainOnlyTypes != null) {
						specsToChooseFrom = types.getPartialSpecList(controller.trainOnlyTypes);
						specsToChooseFrom.remove(run.testEvent); // just in case testEvent is part of the list (we don't check if it was actually there or not)
					}
					int numTrainEvents = Utils.randInt(minTrainEvents, maxTrainEvents);
					// not enough event types left - ignore current run
					if (numTrainEvents > specsToChooseFrom.size()) {
						//System.out.printf("%s. numTrainEvents=%s > specsCopy.size()=%s\n", n, numTrainEvents, specsCopy.size());
						continue;
					}
					run.trainEvents = Utils.sample(specsToChooseFrom, numTrainEvents);
				}
				specsCopy.removeAll(run.trainEvents);

				
				List<JCas> devEventsList = null;
				if (controller.devList != null) {
					devEventsList = types.getPartialSpecList(controller.devList);
					if (devEventsList.contains(run.testEvent) || !ListUtils.intersection(devEventsList, run.trainEvents).isEmpty()) { //also hacky, see above
						continue;
					}
				}
				else {
					List<JCas> specsToChooseFrom = specsCopy;
					if (controller.devOnlyTypes != null) {
						specsToChooseFrom = types.getPartialSpecList(controller.devOnlyTypes);
						specsToChooseFrom.remove(run.testEvent); // just in case any of these is part of the list (we don't check if it was actually there or not)
						specsToChooseFrom.removeAll(run.trainEvents);
					}
					int numDevEvents = Utils.randInt(minDevEvents, maxDevEvents);
					// not enough event types left - ignore current run
					if (numDevEvents > specsToChooseFrom.size()) {
						//System.out.printf("%s. numDevEvents=%s > specsCopy.size()=%s\n", n, numDevEvents, specsCopy.size());
						continue;
					}
					devEventsList = Utils.sample(specsToChooseFrom, numDevEvents);
				}
				run.devEvents = Sets.newLinkedHashSet(devEventsList);
				specsCopy.removeAll(run.devEvents);

				run.trainMentions = 0;
				for (JCas trainSpec : run.trainEvents) {
					String label = SpecAnnotator.getSpecLabel(trainSpec);
//					System.out.printf("trainMentionsByType=%s, label=%s, numTrainMentions=%s\n", trainMentionsByType, label, numTrainMentions);
//					System.out.printf("trainMentionsByType.get(label)=%s\n", trainMentionsByType.get(label));
					Integer trainMentionsInType = trainMentionsByType.get(label);
					if (trainMentionsInType == null) {
						trainMentionsInType = 0;
					}
					run.trainMentions += trainMentionsInType;
				}
				if (run.trainMentions < minTrainMentions) {
					//System.out.printf("%s. numTrainMentions=%s < minTrainMentions=%s\n", n, numTrainMentions, minTrainMentions);
					continue;
				}
				
				run.devMentions = 0;
				for (JCas devSpec : run.devEvents) {
					String label = SpecAnnotator.getSpecLabel(devSpec);
					Integer devMentionsInType = devMentionsByType.get(label);
					if (devMentionsInType == null) {
						devMentionsInType = 0;
					}
					run.devMentions += devMentionsInType;
				}
				if (run.devMentions < minDevMentions) {
					//System.out.printf("%s. numDevMentions=%s < minDevMentions=%s\n", n, numDevMentions, minDevMentions);
					continue;
				}
				
				// If we already have an equivalent run - ignore the current one
				if (result.contains(run)) {
					//System.out.printf("%s. run already contained: %s\n", n, run);
					continue;
				}
				
				totalCounter++;
				perTestCounter++;
				run.id = totalCounter;
				run.idPerTest = perTestCounter;
				run.calcSuffix();

				result.add(run);
				runsForType.add(run);
				
				if (runsForType.size() >= numRuns) {
					//System.out.printf("%s. Reached the limit %s! Breaking!\n\n", n, numRuns);
					break;
				}
			}
			
			System.out.printf("Added %d runs for test type %s:\n", runsForType.size(), SpecAnnotator.getSpecLabel(testSpec));
			for (Run run : runsForType) {
				System.out.printf("- %s\n", run.toStringFull());
			}
			//prevAmountResult = result.size();
		}
		System.out.printf("\nFinished creating a total of %s runs.\n\n", result.size());
		
		if (controller.sentenceSortingMethod==SentenceSortingMethod.ITERATE) {
			int expectedTotalRuns = result.size() * (SentenceSortingMethod.values().length-1);
			List<Run> newResult = Lists.newArrayListWithCapacity(expectedTotalRuns);
			for (SentenceSortingMethod method : SentenceSortingMethod.values()) {
				if (method != SentenceSortingMethod.ITERATE) {
					for (Run run : result) {
						Run newRun = Run.shallowCopy(run);
						newRun.sentenceSortingMethod = method;
						newResult.add(newRun);
					}
				}
			}
			System.out.printf("... and now due to sentenceSortingMethod=%s, we changed it from %s to %s runs (should be %s runs, I hope it is...)\n\n",
					SentenceSortingMethod.ITERATE, result.size(), newResult.size(), expectedTotalRuns);
			result = newResult;
		}
		
//		if (controller.argOMethod==ArgOMethod.ITERATE) {
//			int expectedTotalRuns = result.size() * (ArgOMethod.values().length-1);
//			List<Run> newResult = Lists.newArrayListWithCapacity(expectedTotalRuns);
//			for (ArgOMethod method : ArgOMethod.values()) {
//				if (method != ArgOMethod.ITERATE) {
//					for (Run run : result) {
//						Run newRun = Run.shallowCopy(run);
//						newRun.argOMethod = method;
//						newResult.add(newRun);
//					}
//				}
//			}
//			System.out.printf("... and now due to argOMethod=%s, we changed it from %s to %s runs (should be %s runs, I hope it is...)\n\n",
//					ArgOMethod.ITERATE, result.size(), newResult.size(), expectedTotalRuns);
//			result = newResult;
//		}
		
		return result;
	}
	
	/**
	 * For each new Document we encounter - we do a shallow copy of it, replace its AceDocument
	 * with a deep copy of the original one, and then filter it by the type.<BR>
	 * This way, the returned document are filtered by the given type, without modifying the original document.<BR>
	 * 
	 * NOTE that this filters only the AceDocument, and not the Sentence objects!!!
	 * So this is not a general and full copy-and-filter, it's specifically good for our scenario. 
	 */
	public static Multimap<Document, SentenceInstance> getInstancesWithFilteredDocsForType(Multimap<JCas, SentenceInstance> insts, JCas event) throws CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, CASException, UimaUtilsException, IOException, AeException {
		Multimap<Document, SentenceInstance> result = HashMultimap.create();
		Map<Document, Document> clones = Maps.newHashMap();
		TypesContainer oneType = new TypesContainer(ImmutableList.of(event));
		
		Collection<SentenceInstance> instsOfSpec = insts.get(event);
		if (instsOfSpec != null) {
			for (SentenceInstance inst : instsOfSpec) {
				Document filteredClone = clones.get(inst.doc);
				if (filteredClone == null) {
					filteredClone = Document.shallowCopy(inst.doc);
					AceDocument aceDocCloned = AceDocument.deepCopy(filteredClone.aceAnnotations);
					aceDocCloned.filterBySpecs(oneType);
					filteredClone.aceAnnotations = aceDocCloned;
					clones.put(inst.doc, filteredClone);
				}
				result.put(filteredClone, inst);
			}
		}
		return result;
	}

	/**
	 * this doesn't filter or clone anything, as this is not required for train and dev
	 * (they only use the SentenceInstances for decoding and evaluating, not the Document).
	 * 
	 * NOTE: We don't perform any inner-filtering in Documents, but we do filter out some
	 * of the SentenceInstances (under some super-complicated conditions).
	 */
	public static Multimap<Document, SentenceInstance> getInstancesForTypes(Controller controller, Multimap<JCas, SentenceInstance> insts, Collection<JCas> events, boolean learnable) throws CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, CASException, UimaUtilsException, IOException, AeException {
		Multimap<Document, SentenceInstance> result = HashMultimap.create();
		Map<Sentence, Sentence> clones = Maps.newHashMap();
		TypesContainer types = new TypesContainer(Lists.newArrayList(events));

		for (JCas spec : events) {
			Collection<SentenceInstance> instsOfSpec = insts.get(spec);
			if (instsOfSpec != null) {
				for (SentenceInstance inst : instsOfSpec) {
					if (learnable && controller.skipNonEventSent) {
						if (controller.filterSentenceInstance) {
							if (inst.eventMentions!=null && inst.eventMentions.size() > 0) {
								result.put(inst.doc, inst);
							}
						}
						else {
							Sentence filteredClone = clones.get(inst.sent);
							if (filteredClone == null) {
								filteredClone = Sentence.partiallyDeepCopy(inst.sent);
								filteredClone.filterBySpecs(types);
								clones.put(inst.sent, filteredClone);
							}
							if (filteredClone.eventMentions!=null && filteredClone.eventMentions.size() > 0) {
								result.put(inst.doc, inst);
							}
						}
					}
					else {
						result.put(inst.doc, inst);
					}
				}
			}
		}
		
		// Calculate final number of mentions - it should be the same as the original!
		int countEvents = 0;
		int countInsts = 0;
		Multimap<String, AceEventMention> mentionByType = HashMultimap.create();
		for (SentenceInstance inst : result.values()) {
			for (AceEventMention e : inst.eventMentions) {
				mentionByType.put(e.getSubType(), e);
				countEvents++;
			}
			countInsts++;
		}
		System.out.printf("Built final list of SentenceInstances (learnable=%s): %d Documents, %d SentenceInstances, %d event mentions:\n\t\t", learnable, result.keySet().size(), countInsts, countEvents);
		for (Entry<String, Collection<AceEventMention>> entry : mentionByType.asMap().entrySet()) {
			System.out.printf("%s: %d mentions\t", entry.getKey(), entry.getValue().size());
		}
		System.out.printf("\n");

		return result;
	}
	
	public static void calcTestScore(Run run, File outputFolder, File ansApfFile, Document doc, Stats stats, PrintStream scoreFile) throws IOException {
		String sgmPath = CORPUS_DIR + "/" + doc.docLine + ".sgm";
		AceDocument ansDoc = new AceDocument(sgmPath, ansApfFile.getAbsolutePath());
		Scorer.doAnalysisForFile(ansDoc, doc.getAceAnnotations(), stats, doc.docLine, scoreFile);
	}
	
	public static void main(String args[]) throws Exception {
		
		logger = Utils.handleLog();
		File outputFolder = new File(args[0]);
		List<String> allSpecs = SpecHandler.readSpecListFile(new File(args[1]));
		Integer numRuns = Integer.parseInt(args[2]);
		Integer minTrainEvents = Integer.parseInt(args[3]);
		Integer maxTrainEvents = Integer.parseInt(args[4]);
		Integer minDevEvents = Integer.parseInt(args[5]);
		Integer maxDevEvents = Integer.parseInt(args[6]);
		Integer minTrainMentions = Integer.parseInt(args[7]);
		Integer minDevMentions = Integer.parseInt(args[8]);
		File trainDocs = new File(args[9]);
		File devDocs = new File(args[10]);
		File testDocs = new File(args[11]);
		System.out.printf("Args:\n\toutputFolder=%s\n\tspecsFile=%s (with %s specs)\n\tnumRuns=%s\n\tminTrainEvents=%s\n\tmaxTrainEvents=%s\n\tminDevEvents=%s\n\tmaxDevEvents=%s\n\tminTrainMentions=%s\n\tminDevMentions=%s\n\ttrainDocs=%s\n\tdevDocs=%s\n\ttestDocs=%s\n\n",
				outputFolder, args[1], allSpecs.size(), numRuns, minTrainEvents, maxTrainEvents, minDevEvents, maxDevEvents, minTrainMentions, minDevMentions, trainDocs, devDocs, testDocs);
		
		File corpusDir = new File(CORPUS_DIR);
		Controller controller = new Controller();
		controller.setValueFromArguments(Arrays.copyOfRange(args, 12, args.length));//(StringUtils.split(CONTROLLER_PARAMS));
		Perceptron.controllerStatic = controller;
		TypesContainer types = new TypesContainer(allSpecs, false);
		SignalMechanismsContainer signalMechanismsContainer = new SignalMechanismsContainer(controller);
		Perceptron perceptron = null;
		
		Logs logs = new Logs(outputFolder, controller, "");
		PrintStream r = logs.getR("");
		//logs.logTitles(null,  null,  null,  null,  null,  r);
		System.out.printf("%s Starting to read docs...\n", Utils.detailedLog());

		Map<String, Integer> trainMentions = Maps.newHashMapWithExpectedSize(types.specs.size());
		Multimap<JCas, SentenceInstance> trainInstances = Pipeline.readInstanceList(controller, signalMechanismsContainer, types, corpusDir, trainDocs, new Alphabet(), trainMentions, true, false, null);
		System.out.printf("%s Finished reading training documents: %s sentence instances (total for all %s types)\n", Utils.detailedLog(), trainInstances.size(), trainInstances.keySet().size());
		Map<String, Integer> devMentions = Maps.newHashMapWithExpectedSize(types.specs.size());
		Multimap<JCas, SentenceInstance> devInstances = Pipeline.readInstanceList(controller, signalMechanismsContainer, types, corpusDir, devDocs, new Alphabet(), devMentions, false, false, null);
		System.out.printf("%s Finished reading dev documents: %s sentence instances (total for all %s types)\n", Utils.detailedLog(), devInstances.size(), devInstances.keySet().size());
		Multimap<JCas, SentenceInstance> testInstances = Pipeline.readInstanceList(controller, signalMechanismsContainer, types, corpusDir, testDocs, new Alphabet(), null, true, false, null);
		System.out.printf("%s Finished reading test documents: %s sentence instances (total for all %s types)\n", Utils.detailedLog(), testInstances.size(), testInstances.keySet().size());

		List<Run> runs = buildRuns(controller, types, trainMentions, devMentions, numRuns, minTrainEvents, maxTrainEvents, minDevEvents, maxDevEvents, minTrainMentions, minDevMentions);

		System.out.printf("%s ############################ Starting %s runs: %s\n", Utils.detailedLog(), runs.size(), runs);
		for (Run run : runs) {
			controller.sentenceSortingMethod = run.sentenceSortingMethod;
			controller.argOMethod = run.argOMethod;
			
			Alphabet featureAlphabet = new Alphabet();
			perceptron = new Perceptron(featureAlphabet, controller, outputFolder, signalMechanismsContainer);
			Multimap<Document, SentenceInstance> runTrain = getInstancesForTypes(controller, trainInstances, run.trainEvents, true);
			Multimap<Document, SentenceInstance> runDev = getInstancesForTypes(controller, devInstances, run.devEvents, false);
			
			String dirPrefix = "DIR_" + run.suffix + "__";
			String modelFileName = outputFolder.getAbsolutePath() + "/Model_" + run.suffix;
			logs.logSuffix = "." + run.suffix;
			Perceptron.uTrain = logs.getU("Train");
			Perceptron.wTrain = logs.getW("Train");
			Perceptron.fTrain = logs.getF("Train");
			Perceptron.pTrain = logs.getP("Train");
			Perceptron.bTrain = logs.getB("Train");
			Perceptron.uDev = logs.getU("Dev");
			Perceptron.fDev = logs.getF("Dev");
			Perceptron.pDev   = logs.getP("Dev");
			Perceptron.bDev   = logs.getB("Dev");
			//logs.logTitles(w,  null,  null,  u,  null,  null);
			String scoreFileName = outputFolder.getAbsolutePath() + "/TestScore." + run.suffix + ".txt";
			PrintStream scoreFile = new PrintStream(scoreFileName);
			
			AllTrainingScores scores = perceptron.learning(runTrain.values(), runDev.values(), 0, logs.logSuffix, true, false);
			Perceptron.serializeObject(perceptron, new File(modelFileName));
			
			Multimap<Document, SentenceInstance> runTest = getInstancesWithFilteredDocsForType(testInstances, run.testEvent);
			Stats testStats = new Stats();
			for (Document doc : runTest.keySet()) {
				Collection<SentenceInstance> docInsts = runTest.get(doc);
				File ansApfFile = Decoder.decodeAndOutputFile(logs, perceptron, dirPrefix, outputFolder, docInsts, doc);
				calcTestScore(run, outputFolder, ansApfFile, doc, testStats, scoreFile);
			}
			testStats.calc();
			testStats.printFullOutput(scoreFile);

			logs.logRun(r, run, scores, testStats, run.trainEvents, run.devEvents, run.testEvent,
					runTrain.values(), runDev.values(), runTest.values());
			System.out.printf("%s ############################################# Finished run %s (%s in test spec)\n", Utils.detailedLog(), run.id, run.idPerTest);
		}
		System.out.printf("%s Finished all folds!\n", Utils.detailedLog());
	}
}
