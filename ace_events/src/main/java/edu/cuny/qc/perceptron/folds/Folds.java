package edu.cuny.qc.perceptron.folds;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

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
import edu.cuny.qc.ace.acetypes.ErrorAnalysis;
import edu.cuny.qc.ace.acetypes.Scorer;
import edu.cuny.qc.ace.acetypes.Scorer.Stats;
import edu.cuny.qc.perceptron.core.AllTrainingScores;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Decoder;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.core.Pipeline;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.scorer.SignalMechanismsContainer;
import edu.cuny.qc.util.BackupSource;
import edu.cuny.qc.util.Logs;
import edu.cuny.qc.util.Utils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;

public class Folds {
	protected static Logger logger;
	public static final int BUILD_RUN_FAIL_RATIO = 1000;
	private static final String CORPUS_DIR = "../ace_events_large_resources/src/main/resources/corpus/qi";
	// private static final File TRAIN_DOCS = new
	// File("src/main/resources/doclists/new_filelist_ACE_training.txt");
	// private static final File DEV_DOCS = new
	// File("src/main/resources/doclists/new_filelist_ACE_dev.txt");
	// private static final File TEST_DOCS = new
	// File("src/main/resources/doclists/new_filelist_ACE_test.txt");
	// private static final String CONTROLLER_PARAMS =
	// "beamSize=4 maxIterNum=5 skipNonEventSent=true avgArguments=true skipNonArgument=true useGlobalFeature=false "
	// +
	// "addNeverSeenFeatures=true crossSent=false crossSentReranking=false order=0 evaluatorType=1 learnBigrams=true logLevel=7 "
	// +
	// "oMethod=G0P- serialization=BZ2 featureProfile=NORMAL usePreprocessedFiles=true useSignalFiles=true useArguments=false "
	// +
	// "logOnlyTheseSentences=5b";

	public static BigDecimal MAGIC_NO_PROPORTION_RESTRICTION = BigDecimal.ZERO;
	public static BigDecimal MAGIC_NO_AMOUNT_RESTRICTION = BigDecimal.ZERO;
	
	static {
		//System.out.printf("Folds: Not supporting minTrainMentions and minDevMentions anymore!\n"); //If I wanna add support, I need to think where I need to add check, in this new scheme
	}

	public static List<Run> buildRuns(Controller controller, TypesContainer types, Map<String, Integer> trainMentionsByType, Map<String, Integer> devMentionsByType,
			List<Entry<Integer,Integer>> mandatoryTrainDevEventNums, int numRuns,
			List<Integer> trainEventNums, List<Integer> devEventNums, int minTrainMentions, int minDevMentions,
			Collection<SentenceInstance> trainInstances, Collection<SentenceInstance> devInstances, List<BigDecimal> proportionsRestrictions, List<BigDecimal> amountRestrictions) throws CASException, CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, UimaUtilsException, IOException, AeException {
		int totalTries = BUILD_RUN_FAIL_RATIO * numRuns;
		List<Run> result = Lists.newArrayListWithCapacity(numRuns*types.specs.size());
		
		//int prevAmountResult = 0;
		int totalCounter = 0;
		List<JCas> allTestSpecs;
		if (controller.testType != null) {
			allTestSpecs = types.getPartialSpecList(ImmutableList.of(controller.testType));
		}
		/**
		 * if we have testOnlyTypes - then we choose the test events only from that list, and not from the entire spec list
		 */
		else if (controller.testOnlyTypes != null) {
			allTestSpecs = types.getPartialSpecList(controller.testOnlyTypes);			
		}
		else {
			allTestSpecs = types.specs;
		}
		
		List<Entry<Integer,Integer>> trainAndDevNumsToChooseFrom = mandatoryTrainDevEventNums;
		if (trainAndDevNumsToChooseFrom == null) {
			Entry<Integer,Integer> nullPair = new AbstractMap.SimpleEntry<Integer,Integer>(null, null);
			trainAndDevNumsToChooseFrom = ImmutableList.of(nullPair);
		}
		
		// Configurations!
		for (JCas testSpec : allTestSpecs) {
			for (Entry<Integer,Integer> pair : trainAndDevNumsToChooseFrom) {
				Integer numTrainEventsRequired = pair.getKey();
				Integer numDevEventsRequired = pair.getValue();
				
				for (BigDecimal restrictProportionInput : proportionsRestrictions) {
					
					
					for (BigDecimal restrictAmountInput : amountRestrictions) {
						System.out.printf("%s ** Starting %s tries for configuration: test=%s(/%s), prop=%s(/%s), amount=%s(/%s) |train|=%s |dev|=%s\n",
								Utils.detailedLog(), totalTries, SpecAnnotator.getSpecLabel(testSpec), allTestSpecs.size(), restrictProportionInput,
								proportionsRestrictions, restrictAmountInput, amountRestrictions, numTrainEventsRequired, numDevEventsRequired);
												
						int perConfCounter = 0;
						List<Run> runsForConfiguration = Lists.newArrayListWithCapacity(numRuns*allTestSpecs.size());

						for (int n=0; n<totalTries; n++) {
							Run currRun = new Run();
							currRun.sentenceSortingMethod = controller.sentenceSortingMethod;
							currRun.argOMethod = controller.argOMethod;
							currRun.featureProfile = controller.featureProfile;
							
							List<JCas> specsCopy = Lists.newArrayList(types.specs);
							
							currRun.testEvent = testSpec;
							specsCopy.remove(currRun.testEvent);
				
							/**
							 * if we have trainList - then the train events will be EXACTLY THE SAME for ALL RUNS
							 */
							if (controller.trainList != null) {
								currRun.trainEvents = types.getPartialSpecList(controller.trainList);
								if (currRun.trainEvents.contains(currRun.testEvent)) { //a little hacky - we have to check for this explicitly, as here we don't the train events from specsCopy
									continue;
								}
							}
							else {
								List<JCas> specsToChooseFrom = specsCopy;

								/**
								 * if we have trainOnlyTypes - then we choose the train events only from that list, and not from the entire spec list
								 */
								if (controller.trainOnlyTypes != null) {
									specsToChooseFrom = types.getPartialSpecList(controller.trainOnlyTypes);
									specsToChooseFrom.remove(currRun.testEvent); // just in case testEvent is part of the list (we don't check if it was actually there or not)
								}
								Integer numTrainEvents = numTrainEventsRequired;
								if (numTrainEvents == null) {
									numTrainEvents = Utils.sample(trainEventNums, 1).iterator().next();
								}
								// not enough event types left - ignore current run
								if (numTrainEvents > specsToChooseFrom.size()) {
									//System.out.printf("%s. numTrainEvents=%s > specsCopy.size()=%s\n", n, numTrainEvents, specsCopy.size());
									continue;
								}
								currRun.trainEvents = Utils.sample(specsToChooseFrom, numTrainEvents);
							}
							specsCopy.removeAll(currRun.trainEvents);

							List<JCas> devEventsList = null;
							/**
							 * if we have devList - then the dev events will be EXACTLY THE SAME for ALL RUNS
							 */
							if (controller.devList != null) {
								devEventsList = types.getPartialSpecList(controller.devList);
								if (devEventsList.contains(currRun.testEvent) || !ListUtils.intersection(devEventsList, currRun.trainEvents).isEmpty()) { //also hacky, see above
									continue;
								}
							}
							else {
								List<JCas> specsToChooseFrom = specsCopy;
								/**
								 * if we have devOnlyTypes - then we choose the dev events only from that list, and not from the entire spec list
								 */
								if (controller.devOnlyTypes != null) {
									specsToChooseFrom = types.getPartialSpecList(controller.devOnlyTypes);
									specsToChooseFrom.remove(currRun.testEvent); // just in case any of these is part of the list (we don't check if it was actually there or not)
									specsToChooseFrom.removeAll(currRun.trainEvents);
								}
								Integer numDevEvents = numDevEventsRequired;
								if (numDevEvents == null) {
									numDevEvents = Utils.sample(devEventNums, 1).iterator().next();
								}
								// not enough event types left - ignore current run
								if (numDevEvents > specsToChooseFrom.size()) {
									//System.out.printf("%s. numDevEvents=%s > specsCopy.size()=%s\n", n, numDevEvents, specsCopy.size());
									continue;
								}
								devEventsList = Utils.sample(specsToChooseFrom, numDevEvents);
							}
							currRun.devEvents = Sets.newLinkedHashSet(devEventsList);
							specsCopy.removeAll(currRun.devEvents);
				
				
							/**
							 * Check that this run doesn't violate the number-of-mentions restrictions
							 * I now CHOOSE to apply these restrictions only to the FULL set of sentences, and not to the filtered out
							 * sets of sentences that are created later according to the amount&proportion restriction mechanisms.
							 * This is just a choice for now, and could be changed later if I want.
							 * 
							 * Haha, a night later, and I want to change it. Viva la evolution.
							 * 
							 * One week later - oh wait, but this could be a cool preliminary test, and kill a lot of run time!
							 * 
							 * 8.3.15: And now with changing the method of building the runs (the "early loops") - eh, I think we'll leave it in.
							 * emmm.... OR NOT?????
							 */
//							currRun.trainMentions = 0;
//							for (JCas trainSpec : currRun.trainEvents) {
//								String label = SpecAnnotator.getSpecLabel(trainSpec);
////								System.out.printf("trainMentionsByType=%s, label=%s, numTrainMentions=%s\n", trainMentionsByType, label, numTrainMentions);
////								System.out.printf("trainMentionsByType.get(label)=%s\n", trainMentionsByType.get(label));
//								Integer trainMentionsInType = trainMentionsByType.get(label);
//								if (trainMentionsInType == null) {
//									trainMentionsInType = 0;
//								}
//								currRun.trainMentions += trainMentionsInType;
//							}
//							if (currRun.trainMentions < minTrainMentions) {
//								//System.out.printf("%s. numTrainMentions=%s < minTrainMentions=%s\n", n, numTrainMentions, minTrainMentions);
//								System.out.printf("%s Preliminary check fail: %s. run.trainMentions=%s < minTrainMentions=%s\n", Utils.detailedLog(), n, currRun.trainMentions, minTrainMentions);
//								continue;
//							}
//							currRun.trainMentions = 0;
//							
//							currRun.devMentions = 0;
//							for (JCas devSpec : currRun.devEvents) {
//								String label = SpecAnnotator.getSpecLabel(devSpec);
//								Integer devMentionsInType = devMentionsByType.get(label);
//								if (devMentionsInType == null) {
//									devMentionsInType = 0;
//								}
//								currRun.devMentions += devMentionsInType;
//							}
//							if (currRun.devMentions < minDevMentions) {
//								//System.out.printf("%s. numDevMentions=%s < minDevMentions=%s\n", n, numDevMentions, minDevMentions);
//								System.out.printf("%s Preliminary check fail: %s. run.devMentions=%s < minDevMentions=%s\n", Utils.detailedLog(), n, currRun.devMentions, minDevMentions);
//								continue;
//							}
//							currRun.devMentions = 0;
//							
							System.out.printf("%s A   |trainInstances|=%s |devInstances|=%s\n", Utils.detailedLog(), trainInstances.size(), devInstances.size());
							Multimap<Document, SentenceInstance> runTrain = getInstancesForTypes(controller, trainInstances, currRun.trainEvents, true);
							Multimap<Document, SentenceInstance> runDev = getInstancesForTypes(controller, devInstances, currRun.devEvents, false);

							List<SentenceInstance> trainInstanceList = Lists.newArrayList(runTrain.values());
							List<SentenceInstance> devInstanceList = Lists.newArrayList(runDev.values());
							int trainMentions = SentenceInstance.getNumEventMentions(trainInstanceList, null);
							int devMentions = SentenceInstance.getNumEventMentions(devInstanceList, null);
							int allMentions = trainMentions + devMentions;
							BigDecimal trainMentionsBD = new BigDecimal(trainMentions);
							BigDecimal devMentionsBD = new BigDecimal(devMentions);
							BigDecimal allMentionsBD = new BigDecimal(allMentions);
							

							System.out.printf("%s B   Train=[%s insts, %s mentions] Dev=[%s insts, %s mentions] Train+Dev mentions=%s\n",
									Utils.detailedLog(), trainInstanceList.size(), trainMentions, devInstanceList.size(), devMentions, allMentions);

							BigDecimal restrictProportion = (restrictProportionInput.equals(MAGIC_NO_PROPORTION_RESTRICTION)) ? devMentionsBD.divide(allMentionsBD, MathContext.DECIMAL128) : restrictProportionInput;
							BigDecimal restrictAmount = (restrictAmountInput.equals(MAGIC_NO_AMOUNT_RESTRICTION)) ? allMentionsBD : restrictAmountInput;
							
							//chooseFromDev = restrictProportion * restrictAmount
							int chooseFromDev = Utils.roundUp(restrictProportion.multiply(restrictAmount));
							
							//chooseFromTrain = (1 - restrictProportion) * restrictAmount
							int chooseFromTrain = Utils.roundDown(BigDecimal.ONE.subtract(restrictProportion).multiply(restrictAmount));
							
							System.out.printf("%s chooseFromDev=prop(%s)*amount(%s)=%s chooseFromTrain=(1-prop)*amount=%s chooseFromDev+chooseFromTrain=%s\n",
									Utils.detailedLog(), restrictProportion, restrictAmount, chooseFromDev, chooseFromTrain, chooseFromDev+chooseFromTrain);
							
							// Do some verifications and adjustments on "choose" vals
							if (!restrictAmountInput.equals(MAGIC_NO_AMOUNT_RESTRICTION)) {
								if (chooseFromTrain>trainMentions) {
									System.out.printf("%s restrictAmount=%s, restrictProportion=%s, trainMentions=%s, chooseFromTrain=%s: Cannot fulfill all restrictions, since chooseFromTrain>trainMentions. Skipping run.\n",
											Utils.detailedLog(), restrictAmountInput, restrictProportion, trainMentions, chooseFromTrain);
									continue;
								}
								if (chooseFromDev>devMentions) {
									System.out.printf("%s restrictAmount=%s, restrictProportion=%s, devMentions=%s, chooseFromDev=%s: Cannot fulfill all restrictions, since chooseFromDev>devMentions. Skipping run.\n",
											Utils.detailedLog(), restrictAmountInput, restrictProportion, devMentions, chooseFromDev);
									continue;
								}
							}
							else {
								if (chooseFromTrain>trainMentions) {
									int newChooseFromDev = Utils.roundUp(trainMentionsBD.multiply(restrictProportion).divide(BigDecimal.ONE.subtract(restrictProportion), MathContext.DECIMAL128));
									System.out.printf("%s Shrinking chooseFromTrain from %s to %s (==trainMentions) and chooseFromDev from %s to %s, since restrictProportion=%s\n",
											Utils.detailedLog(), chooseFromTrain, trainMentions, chooseFromDev, newChooseFromDev, restrictProportion);
									chooseFromTrain = trainMentions;
									chooseFromDev = newChooseFromDev;
								}
								else if (chooseFromDev>devMentions) {
									int newChooseFromTrain = Utils.roundDown(devMentionsBD.multiply(BigDecimal.ONE.subtract(restrictProportion)).divide(restrictProportion, MathContext.DECIMAL128));
									System.out.printf("%s Shrinking chooseFromDev from %s to %s (==devMentions) and chooseFromTrain from %s to %s, since restrictProportion=%s\n",
											Utils.detailedLog(), chooseFromDev, devMentions, chooseFromTrain, newChooseFromTrain, restrictProportion);
									chooseFromTrain = newChooseFromTrain;
									chooseFromDev = devMentions;
								}
							}
							
							System.out.printf("%s 1   chooseFromTrain=%s (trainMentions=%s) chooseFromDev=%s (devMentions=%s)\n", Utils.detailedLog(), chooseFromTrain, trainMentions, chooseFromDev, devMentions);
							Collection<SentenceInstance> sampledTrainInsts, sampledDevInsts;
							if (chooseFromTrain == trainMentions) {
								sampledTrainInsts = Lists.newArrayList(trainInstanceList);
							}
							else {
								sampledTrainInsts = getSentenceInstancesByNumOfMentions("trainInsts", trainInstanceList, chooseFromTrain);
							}
							if (chooseFromDev == devMentions) {
								sampledDevInsts = Lists.newArrayList(devInstanceList);
							}
							else {
								sampledDevInsts = getSentenceInstancesByNumOfMentions("devInsts", devInstanceList, chooseFromDev);
							}

							System.out.printf("%s 2   |sampledTrainInsts|=%s |sampledDevInsts|=%s\n", Utils.detailedLog(), sampledTrainInsts.size(), sampledDevInsts.size());
							currRun.devInsts = Sets.newHashSet(sampledDevInsts);
							currRun.trainInsts = Sets.newHashSet(sampledTrainInsts);
									
							/**
							 * Check the minimum mentions requirements
							 * Notice that this happens on all runs, also restricted ones
							 */
							Multimap<String, AceEventMention> trainMentionByType = HashMultimap.create();
							currRun.trainMentions = SentenceInstance.getNumEventMentions(currRun.trainInsts, trainMentionByType);
							/////
							System.out.printf("%s trainInsts(%s,mentions=%s): ", Utils.detailedLog(), currRun.trainInsts.size(), currRun.trainMentions);
							int i=0;
							Iterator<SentenceInstance> iter = currRun.trainInsts.iterator();
							while (i<6 && iter.hasNext()) {
								i++;
								SentenceInstance inst = iter.next();
								System.out.printf("Inst(%s spec=%s, mentions=%s)[total mentions for spec are %s], ", inst.sentInstID, SpecAnnotator.getSpecLabel(inst.associatedSpec), inst.eventMentions.size(), trainMentionByType.get(SpecAnnotator.getSpecLabel(inst.associatedSpec)).size());
							}
							System.out.printf("...\n");
							/////
							if (currRun.trainMentions < minTrainMentions) {
								System.out.printf("%s Final check fail: %s. currRun.trainMentions=%s < minTrainMentions=%s\n", Utils.detailedLog(), n, currRun.trainMentions, minTrainMentions);
								continue;
							}
							Multimap<String, AceEventMention> devMentionByType = HashMultimap.create();
							currRun.devMentions = SentenceInstance.getNumEventMentions(currRun.devInsts, devMentionByType);
							/////
							System.out.printf("%s devInsts(%s,mentions=%s): ", Utils.detailedLog(), currRun.devInsts.size(), currRun.devMentions);
							i=0;
							iter = currRun.devInsts.iterator();
							while (i<6 && iter.hasNext()) {
								i++;
								SentenceInstance inst = iter.next();
								System.out.printf("Inst(%s spec=%s, mentions=%s)[total mentions for spec are %s], ", inst.sentInstID, SpecAnnotator.getSpecLabel(inst.associatedSpec), inst.eventMentions.size(), devMentionByType.get(SpecAnnotator.getSpecLabel(inst.associatedSpec)).size());
							}
							System.out.printf("...\n");
							/////
							if (currRun.devMentions < minDevMentions) {
								System.out.printf("%s Final check fail: %s. currRun.devMentions=%s < minDevMentions=%s\n", Utils.detailedLog(), n, currRun.devMentions, minDevMentions);
								continue;
							}
							
							// If we already have an equivalent run - ignore the current one
							if (result.contains(currRun)) {
								System.out.printf("%s %s. Out of current %s results, run already contained: %s\n", Utils.detailedLog(), n, result.size(), currRun);
								continue;
							}
							
							totalCounter++;
							perConfCounter++;
							currRun.id = totalCounter;
							currRun.idPerTest = perConfCounter;
							currRun.calcSuffix();
							currRun.restrictAmount = restrictAmountInput.intValueExact();
							currRun.restrictProportion = restrictProportionInput;

							result.add(currRun);
							runsForConfiguration.add(currRun);
							
							Utils.outputSentenceInstanceList("Train(id=" + currRun.id + ")", runTrain.keySet(), currRun.trainInsts, currRun.trainMentions, trainMentionByType);
							Utils.outputSentenceInstanceList("Dev(id=" + currRun.id + ")", runDev.keySet(), currRun.devInsts, currRun.devMentions, devMentionByType);

							if (runsForConfiguration.size() >= numRuns) {
								//System.out.printf("%s. Reached the limit %s! Breaking!\n\n", n, numRuns);
								break;
							}
						}
						
						System.out.printf("Added %d runs for configuration: test=%s(/%s), prop=%s(/%s), amount=%s(/%s) |train|=%s |dev|=%s\n",
								runsForConfiguration.size(), SpecAnnotator.getSpecLabel(testSpec), allTestSpecs.size(), restrictProportionInput,
								proportionsRestrictions, restrictAmountInput, amountRestrictions, numTrainEventsRequired, numDevEventsRequired);
						for (Run run : runsForConfiguration) {
							System.out.printf("- %s\n", run.toStringFull());
						}
					}
				}
			}
			
			//prevAmountResult = result.size();
		}
		System.out.printf("\nFinished creating a total of %s runs.\n\n", result.size());
		
		// Oops, can't support this... :)
//		if (controller.featureProfile==FeatureProfile.FINAL_F1__ITERATE) {
//			List<FeatureProfile> vals = ImmutableList.of(FeatureProfile.FINAL1_F1, FeatureProfile.FINAL1_F1_REC_PREC, FeatureProfile.FINAL1_F1_REC, FeatureProfile.FINAL1_F1_PREC);
//			int expectedTotalRuns = result.size() * vals.size();
//			List<Run> newResult = Lists.newArrayListWithCapacity(expectedTotalRuns);
//			for (FeatureProfile prof : vals) {
//				for (Run run : result) {
//					Run newRun = Run.shallowCopy(run);
//					newRun.featureProfile = prof;
//					newResult.add(newRun);
//				}
//			}
//			System.out.printf("... and now due to featureProfile=%s, we changed it from %s to %s runs (should be %s runs, I hope it is...)\n\n",
//					FeatureProfile.FINAL_F1__ITERATE, result.size(), newResult.size(), expectedTotalRuns);
//			result = newResult;
//		}
		
//		if (controller.sentenceSortingMethod==SentenceSortingMethod.ITERATE) {
//			int expectedTotalRuns = result.size() * (SentenceSortingMethod.values().length-1);
//			List<Run> newResult = Lists.newArrayListWithCapacity(expectedTotalRuns);
//			for (SentenceSortingMethod method : SentenceSortingMethod.values()) {
//				if (method != SentenceSortingMethod.ITERATE) {
//					for (Run run : result) {
//						Run newRun = Run.shallowCopy(run);
//						newRun.sentenceSortingMethod = method;
//						newResult.add(newRun);
//					}
//				}
//			}
//			System.out.printf("... and now due to sentenceSortingMethod=%s, we changed it from %s to %s runs (should be %s runs, I hope it is...)\n\n",
//					SentenceSortingMethod.ITERATE, result.size(), newResult.size(), expectedTotalRuns);
//			result = newResult;
//		}
		
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
	 * How many *SentenceInstances* do we get in each step, with regards to
	 * *numMentions*? - 100% - if mentions<target: (meaning I am in
	 * non-filtered, ==dev) while mentions<target: add target*10 between prev
	 * and curr amount: add 50 (fixed) between prev an curr amount add 1 fixed.
	 * - else: (meaning I am in filtered, ==train) while mentions>target: remove
	 * 5 between prev and curr amount remove 1
	 * 
	 * @param insts
	 * @param targetMentions
	 * @return
	 */
	public static Collection<SentenceInstance> getSentenceInstancesByNumOfMentions(
			String instsTitle, List<SentenceInstance> insts, int targetMentions) {
		List<SentenceInstance> pool = Lists.newArrayList(insts);
		List<SentenceInstance> result = Utils.sample2AndRemoveSafe(pool,
				targetMentions);
		List<SentenceInstance> toAdd, toRemove, toRemoveCopy = null;
		int mentions = SentenceInstance.getNumEventMentions(result, null);
		int tempMentions;
		int iters = 1;
		final int MAX_ITERS = 2000;

		System.out.printf("%s Folds.SIBNO(|%s|=%s,target=%s): [%s,%s]",
				Utils.detailedLog(), instsTitle, insts.size(), targetMentions,
				result.size(), mentions);
		// Zig-zagging!!!
		if (mentions < targetMentions) {
			while (mentions < targetMentions) {
				toAdd = Utils.sample2AndRemoveSafe(pool, targetMentions * 10);
				tempMentions = SentenceInstance
						.getNumEventMentions(toAdd, null);
				result.addAll(toAdd);
				mentions += tempMentions;
				iters++;
				System.out.printf("/+%s,%s+/", result.size(), mentions);
				if (iters >= MAX_ITERS) {
					System.out.printf("\n\n");
					throw new RuntimeException("Got " + iters
							+ " iters! That's waaaay too much!");
				}
			}
			while (mentions > targetMentions && result.size() >= 50) {
				toRemove = result.subList(result.size() - 50, result.size());
				toRemoveCopy = Lists.newArrayList(toRemove);
				tempMentions = SentenceInstance.getNumEventMentions(toRemove,
						null);
				toRemove.clear(); // Yes, apparently this deletes them from the
									// underlying list!
				mentions -= tempMentions;
				iters++;
				System.out.printf("/-%s,%s-/", result.size(), mentions);
				if (iters >= MAX_ITERS) {
					System.out.printf("\n\n");
					throw new RuntimeException("Got " + iters
							+ " iters! That's waaaay too much!");
				}
			}
			while (mentions < targetMentions && !toRemoveCopy.isEmpty()) {
				toAdd = ImmutableList.of(toRemoveCopy.remove(0));
				tempMentions = SentenceInstance
						.getNumEventMentions(toAdd, null);
				result.addAll(toAdd);
				mentions += tempMentions;
				iters++;
				System.out.printf("/+%s,%s+/", result.size(), mentions);
				if (iters >= MAX_ITERS) {
					System.out.printf("\n\n");
					throw new RuntimeException("Got " + iters
							+ " iters! That's waaaay too much!");
				}
			}
		} else if (mentions > targetMentions) {
			while (mentions > targetMentions && result.size() >= 5) {
				toRemove = result.subList(result.size() - 5, result.size());
				toRemoveCopy = Lists.newArrayList(toRemove);
				tempMentions = SentenceInstance.getNumEventMentions(toRemove,
						null);
				toRemove.clear(); // Yes, apparently this deletes them from the
									// underlying list!
				mentions -= tempMentions;
				iters++;
				System.out.printf("\\-%s,%s-\\", result.size(), mentions);
				if (iters >= MAX_ITERS) {
					System.out.printf("\n\n");
					throw new RuntimeException("Got " + iters
							+ " iters! That's waaaay too much!");
				}
			}
			while (mentions < targetMentions && !toRemoveCopy.isEmpty()) {
				toAdd = ImmutableList.of(toRemoveCopy.remove(0));
				tempMentions = SentenceInstance
						.getNumEventMentions(toAdd, null);
				result.addAll(toAdd);
				mentions += tempMentions;
				iters++;
				System.out.printf("\\+%s,%s+\\", result.size(), mentions);
				if (iters >= MAX_ITERS) {
					System.out.printf("\n\n");
					throw new RuntimeException("Got " + iters
							+ " iters! That's waaaay too much!");
				}
			}
		}

		System.out
				.printf("\n%s         From |%s|=%s, target=%s, in %s iterations we got to |resultInsts|=%s, mentions=%s\n",
						Utils.detailedLog(), instsTitle, insts.size(),
						targetMentions, iters, result.size(), mentions);
		return result;
	}

	/**
	 * For each new Document we encounter - we do a shallow copy of it, replace
	 * its AceDocument with a deep copy of the original one, and then filter it
	 * by the type.<BR>
	 * This way, the returned document are filtered by the given type, without
	 * modifying the original document.<BR>
	 * 
	 * NOTE that this filters only the AceDocument, and not the Sentence
	 * objects!!! So this is not a general and full copy-and-filter, it's
	 * specifically good for our scenario.
	 */
	public static Multimap<Document, SentenceInstance> getInstancesWithFilteredDocsForType(
			Multimap<JCas, SentenceInstance> insts, JCas event)
			throws CASRuntimeException, AnalysisEngineProcessException,
			ResourceInitializationException, CASException, UimaUtilsException,
			IOException, AeException {
		Multimap<Document, SentenceInstance> result = HashMultimap.create();
		Map<Document, Document> clones = Maps.newHashMap();
		TypesContainer oneType = new TypesContainer(ImmutableList.of(event));

		Collection<SentenceInstance> instsOfSpec = insts.get(event);
		if (instsOfSpec != null) {
			for (SentenceInstance inst : instsOfSpec) {
				Document filteredClone = clones.get(inst.doc);
				if (filteredClone == null) {
					filteredClone = Document.shallowCopy(inst.doc);
					AceDocument aceDocCloned = AceDocument
							.deepCopy(filteredClone.aceAnnotations);
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
	 * this doesn't filter or clone anything, as this is not required for train
	 * and dev (they only use the SentenceInstances for decoding and evaluating,
	 * not the Document).
	 * 
	 * NOTE: We don't perform any inner-filtering in Documents, but we do filter
	 * out some of the SentenceInstances (under some super-complicated
	 * conditions).
	 */
	public static Multimap<Document, SentenceInstance> getInstancesForTypes(
			Controller controller, Collection<SentenceInstance> insts,
			Collection<JCas> events, boolean learnable)
			throws CASRuntimeException, AnalysisEngineProcessException,
			ResourceInitializationException, CASException, UimaUtilsException,
			IOException, AeException {
		Multimap<Document, SentenceInstance> result = HashMultimap.create();
		Map<Sentence, Sentence> clones = Maps.newHashMap();
		TypesContainer types = new TypesContainer(Lists.newArrayList(events));

		for (SentenceInstance inst : insts) {
			if (events.contains(inst.associatedSpec)) {
				if (learnable && controller.skipNonEventSent) {
					if (controller.filterSentenceInstance) {
						if (inst.eventMentions != null
								&& inst.eventMentions.size() > 0) {
							result.put(inst.doc, inst);
						}
					} else {
						Sentence filteredClone = clones.get(inst.sent);
						if (filteredClone == null) {
							filteredClone = Sentence
									.partiallyDeepCopy(inst.sent);
							filteredClone.filterBySpecs(types);
							clones.put(inst.sent, filteredClone);
						}
						if (filteredClone.eventMentions != null
								&& filteredClone.eventMentions.size() > 0) {
							result.put(inst.doc, inst);
						}
					}
				} else {
					result.put(inst.doc, inst);
				}
			}
		}

		return result;
	}

	private static void validateAndConvertRestrictions(
			List<String> proportionsRestrictionsStrs,
			List<String> amountRestrictionsStrs,
			List<BigDecimal> proportionsRestrictions,
			List<BigDecimal> amountRestrictions) {

		for (String propStr : proportionsRestrictionsStrs) {
			BigDecimal prop = new BigDecimal(propStr);

			// prop can't be smaller than 0, or equal-or-greater than 1
			if (prop.compareTo(BigDecimal.ZERO) < 0
					|| prop.compareTo(BigDecimal.ONE) >= 0) {
				throw new IllegalArgumentException(String.format(
						"Proportion restrictions must be >=0 and <1, got %s",
						prop));
			}

			proportionsRestrictions.add(prop);
		}

		for (String amountStr : amountRestrictionsStrs) {
			BigDecimal amount = new BigDecimal(amountStr);

			// amount can't be smaller than 0
			if (amount.compareTo(BigDecimal.ZERO) < 0) {
				throw new IllegalArgumentException(String.format(
						"Amount restrictions must be >=0, got %s", amount));
			}

			amountRestrictions.add(amount);
		}

	}

	public static void calcTestScore(Run run, File outputFolder,
			File ansApfFile, Document doc, Stats stats, PrintStream scoreFile)
			throws IOException {
		String sgmPath = CORPUS_DIR + "/" + doc.docLine + ".sgm";
		AceDocument ansDoc = new AceDocument(sgmPath,
				ansApfFile.getAbsolutePath());
		Scorer.doAnalysisForFile(ansDoc, doc.getAceAnnotations(), stats,
				doc.docLine, scoreFile);
	}

	public static void main(String args[]) throws Exception {

		logger = Utils.handleLog();
		File outputFolder = new File(args[0]);
		List<String> allSpecs = SpecHandler.readSpecListFile(new File(args[1]));
		Integer numRuns = Integer.parseInt(args[2]);
		// Integer minTrainEvents = Integer.parseInt(args[3]);
		// Integer maxTrainEvents = Integer.parseInt(args[4]);
		List<Integer> trainEventNums = Utils.stringToIntList(args[3], ",");
		// Integer minDevEvents = Integer.parseInt(args[5]);
		// Integer maxDevEvents = Integer.parseInt(args[6]);
		List<Integer> devEventNums = Utils.stringToIntList(args[4], ",");
		List<Entry<Integer, Integer>> mandatoryTrainDevEventNums = Utils
				.stringToIntPairList(args[5], ",", "-");
		Integer minTrainMentions = Integer.parseInt(args[6]);
		Integer minDevMentions = Integer.parseInt(args[7]);
		File trainDocs = new File(args[8]);
		File devDocs = new File(args[9]);
		File testDocs = new File(args[10]);
		List<String> proportionsRestrictionsStrs = Arrays.asList(args[11]
				.split(","));
		List<String> amountRestrictionsStrs = Arrays
				.asList(args[12].split(","));
		System.out
				.printf("Args:\n\toutputFolder=%s\n\tspecsFile=%s (with %s specs)\n\tnumRuns=%s\n\ttrainEventNums=%s\n\tdevEventNums=%s\n\tminTrainMentions=%s\n\tminDevMentions=%s\n\ttrainDocs=%s\n\tdevDocs=%s\n\ttestDocs=%s\n\tproportionsRestrictions=%s\n\tamountRestrictions=%s\n\n",
						outputFolder, args[1], allSpecs.size(), numRuns,
						trainEventNums, devEventNums, minTrainMentions,
						minDevMentions, trainDocs, devDocs, testDocs,
						proportionsRestrictionsStrs, amountRestrictionsStrs);
		Utils.OUTPUT_FOLDER = outputFolder;
		if ((trainEventNums != null || devEventNums != null)
				&& mandatoryTrainDevEventNums != null) {
			throw new IllegalArgumentException(
					"Can have either only regular event nums (train and dev), or a mandatory pair-list. But not both!");
		}
		BackupSource.backup(outputFolder);

		List<BigDecimal> proportionsRestrictions = Lists
				.newArrayListWithCapacity(proportionsRestrictionsStrs.size());
		List<BigDecimal> amountRestrictions = Lists
				.newArrayListWithCapacity(amountRestrictionsStrs.size());
		validateAndConvertRestrictions(proportionsRestrictionsStrs,
				amountRestrictionsStrs, proportionsRestrictions,
				amountRestrictions);

		File corpusDir = new File(CORPUS_DIR);
		Controller controller = new Controller();
		controller.setValueFromArguments(Arrays.copyOfRange(args, 13,
				args.length));// (StringUtils.split(CONTROLLER_PARAMS));
		Perceptron.controllerStatic = controller;
		TypesContainer types = new TypesContainer(allSpecs, false);
		SignalMechanismsContainer signalMechanismsContainer = new SignalMechanismsContainer(
				controller);
		Perceptron perceptron = null;

		Logs logs = new Logs(outputFolder, controller, "");
		PrintStream r = logs.getR("");
		// logs.logTitles(null, null, null, null, null, r);
		System.out.printf("%s Starting to read docs...\n", Utils.detailedLog());

		Map<String, Integer> trainMentions = Maps
				.newHashMapWithExpectedSize(types.specs.size());
		Multimap<JCas, SentenceInstance> trainInstances = Pipeline
				.readInstanceList(controller, signalMechanismsContainer, types,
						corpusDir, trainDocs, new Alphabet(), trainMentions,
						true, false, null, "Train");
		System.out
				.printf("%s Finished reading training documents: %s sentence instances (total for all %s types)\n",
						Utils.detailedLog(), trainInstances.size(),
						trainInstances.keySet().size());
		Map<String, Integer> devMentions = Maps
				.newHashMapWithExpectedSize(types.specs.size());
		Multimap<JCas, SentenceInstance> devInstances = Pipeline
				.readInstanceList(controller, signalMechanismsContainer, types,
						corpusDir, devDocs, new Alphabet(), devMentions, false,
						false, null, "Dev");
		System.out
				.printf("%s Finished reading dev documents: %s sentence instances (total for all %s types)\n",
						Utils.detailedLog(), devInstances.size(), devInstances
								.keySet().size());
		Multimap<JCas, SentenceInstance> testInstances = Pipeline
				.readInstanceList(controller, signalMechanismsContainer, types,
						corpusDir, testDocs, new Alphabet(), null, true, false,
						null, "Test");
		System.out
				.printf("%s Finished reading test documents: %s sentence instances (total for all %s types)\n",
						Utils.detailedLog(), testInstances.size(),
						testInstances.keySet().size());

		System.out
				.printf("\n%s Finished reading ALL documents:  Train: %s instances, %s types; Dev: %s instances, %s types; Test: %s instances, %s types\n\n",
						Utils.detailedLog(), trainInstances.size(),
						trainInstances.keySet().size(), devInstances.size(),
						devInstances.keySet().size(), testInstances.size(),
						testInstances.keySet().size());

		List<Run> runs = buildRuns(controller, types, trainMentions,
				devMentions, mandatoryTrainDevEventNums, numRuns,
				trainEventNums, devEventNums, minTrainMentions, minDevMentions,
				trainInstances.values(), devInstances.values(),
				proportionsRestrictions, amountRestrictions);

		System.out.printf(
				"%s ############################ Starting %s runs: %s\n",
				Utils.detailedLog(), runs.size(), runs);
		for (Run run : runs) {
			controller.sentenceSortingMethod = run.sentenceSortingMethod;
			controller.argOMethod = run.argOMethod;
			controller.featureProfile = run.featureProfile;

			Alphabet featureAlphabet = new Alphabet();
			perceptron = new Perceptron(featureAlphabet, controller,
					outputFolder, signalMechanismsContainer);
			// /xxx these things - maybe should be done on the sentences saved
			// in each run? I think so!
			// Multimap<Document, SentenceInstance> runTrain =
			// getInstancesForTypes(controller, trainInstances, run.trainEvents,
			// true);
			// Multimap<Document, SentenceInstance> runDev =
			// getInstancesForTypes(controller, devInstances, run.devEvents,
			// false);

			String dirPrefix = "DIR_" + run.suffix;// + "__";
			String runDir = outputFolder + "/" + dirPrefix;
			String modelFileName = outputFolder.getAbsolutePath() + "/Model_"
					+ run.suffix;
			logs.logSuffix = "." + run.suffix;
			Perceptron.uTrain = logs.getU("Train");
			Perceptron.wTrain = logs.getW("Train");
			Perceptron.fTrain = logs.getF("Train");
			Perceptron.pTrain = logs.getP("Train");
			Perceptron.bTrain = logs.getB("Train");
			Perceptron.uDev = logs.getU("Dev");
			Perceptron.fDev = logs.getF("Dev");
			Perceptron.pDev = logs.getP("Dev");
			Perceptron.bDev = logs.getB("Dev");
			// logs.logTitles(w, null, null, u, null, null);
			String scoreFileName = outputFolder.getAbsolutePath()
					+ "/TestScore." + run.suffix + ".txt";
			PrintStream scoreFile = new PrintStream(scoreFileName);

			AllTrainingScores scores = perceptron.learning(run.trainInsts,
					run.devInsts, 0, logs.logSuffix, true, false);
			Perceptron.serializeObject(perceptron, new File(modelFileName));

			Multimap<Document, SentenceInstance> runTest = getInstancesWithFilteredDocsForType(
					testInstances, run.testEvent);
			Stats testStats = new Stats();
			for (Document doc : runTest.keySet()) {
				Collection<SentenceInstance> docInsts = runTest.get(doc);
				File ansApfFile = Decoder.decodeAndOutputFile(logs, perceptron,
						dirPrefix, outputFolder, docInsts, doc);
				calcTestScore(run, outputFolder, ansApfFile, doc, testStats,
						scoreFile);
			}
			testStats.calc();
			testStats.printFullOutput(scoreFile);

			logs.logRun(r, run, scores, testStats, runTest.values());

			if (!runTest.isEmpty() && controller.doErrorAnalysis) {
				String[] errorAnalysisArgs = new String[] { CORPUS_DIR, runDir,
						testDocs.getAbsolutePath(), runDir + "/NtpOut",
						SpecAnnotator.getSpecLabel(run.testEvent) };
				System.out.printf("%s Starting ErrorAnalysis\n",
						Utils.detailedLog());
				ErrorAnalysis.main(errorAnalysisArgs);
				System.out.printf("%s Finished ErrorAnalysis\n",
						Utils.detailedLog());
			}

			System.out
					.printf("%s ############################################# Finished run %s (%s in test spec)\n",
							Utils.detailedLog(), run.id, run.idPerTest);
		}
		System.out.printf("%s Finished all folds!\n", Utils.detailedLog());
	}
}
