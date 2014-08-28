package ac.biu.nlp.nlp.ace_uima.analyze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import net.sf.jlinkgrammar.Sentence;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.io.Files;

import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.util.gst.Edge;

import ac.biu.nlp.nlp.ace_uima.stats.SignalPerformanceField;
import ac.biu.nlp.nlp.ace_uima.stats.StatsDocument;
import ac.biu.nlp.nlp.ace_uima.stats.StatsException;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecHandler;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Treeout;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.core.Pipeline;
import edu.cuny.qc.perceptron.core.Pipeline.ChunkRecord;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SignalInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismsContainer;
import edu.cuny.qc.scorer.mechanism.DependencySignalMechanism.SpecTreeoutQuery;
import edu.cuny.qc.util.Utils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public class SignalAnalyzer {
	private static final String CORPUS_DIR = "../ace_events_large_resources/src/main/resources/corpus/qi";
	private static final String CONTROLLER_PARAMS =
					"beamSize=4 maxIterNum=20 skipNonEventSent=true avgArguments=true useGlobalFeature=false " +
					"addNeverSeenFeatures=true crossSent=false crossSentReranking=false order=0 evaluatorType=1 learnBigrams=true logLevel=3 " +
					"oMethod=G0P- serialization=BZ2 featureProfile=ANALYSIS calcDebugSignalsAnyway=true docsChunk=20";
	private static SignalAnalyzerDocumentCollection docs = new SignalAnalyzerDocumentCollection();
	private static final int SENTENCE_PRINT_FREQ = 200;
	private static final int SENTENCE_GC_FREQ = 20000;
	//private static final int debugMinSentence = 1310;
	private static final boolean DO_VERBOSE_PRINTS = true; //warning! only use this for really fine-grained debugging - this prints a lot!!!

	public static void analyze(File inputFileList, File specList, File outputFolder, boolean useDumps, String triggerDocName/*, String argDocName, String globalDocName*/, Integer debugMinSentence, Integer docsChunk) throws Exception {
		(new PrintStream(new File(outputFolder, "start"))).close();
		if (debugMinSentence < 0) {
			debugMinSentence = Integer.MAX_VALUE;
		}
		
		AceAnalyzer.fillCategories();

		File triggerFile = new File(outputFolder, triggerDocName);
//		File argFile = new File(outputFolder, argDocName);
//		File globalFile = new File(outputFolder, globalDocName);
		
		Utils.fileInit(triggerFile);
//		Utils.fileInit(argFile);
//		Utils.fileInit(globalFile);

		Controller controller = new Controller();
		controller.setValueFromArguments(StringUtils.split(CONTROLLER_PARAMS));
		controller.usePreprocessFiles = useDumps;
		controller.useSignalFiles = useDumps;
		controller.docsChunk = docsChunk;
		Perceptron.controllerStatic = controller;
		SignalMechanismsContainer signalMechanismsContainer = new SignalMechanismsContainer(controller);
		List<String> specXmlPaths = SpecHandler.readSpecListFile(specList);
		TypesContainer types = new TypesContainer(specXmlPaths, false);
		//Perceptron perceptron = new Perceptron(null, controller, outputFolder, signalMechanismsContainer);
		
		boolean debug = controller.calcDebugSignalsAnyway;
		ChunkRecord chunkRecord = new ChunkRecord();
		
		while (!chunkRecord.isFinished) {
			
			Collection<SentenceInstance> goldInstances = Pipeline.readInstanceList(controller, signalMechanismsContainer, types, new File(CORPUS_DIR), inputFileList, new Alphabet(), null, false, debug, chunkRecord).values();
			//SignalPerformanceField.goldInstances = goldInstances;
			System.out.printf("[%s] Finished reading documents, starting to process %s sentences\n", new Date(), goldInstances.size());
			
			// Iterating instances, and for each creating assignment - ONE FOR EACH SIGNAL
			// inspired by BeamSearch.beamSearch
			int sentNum = 0;
			//for (SentenceInstance problem : goldInstances) {
			for (Iterator<SentenceInstance> sentIter = goldInstances.iterator(); sentIter.hasNext();) {
				final SentenceInstance problem = sentIter.next();
				
				Map<ScorerData, SentenceAssignment> assignments = new HashMap<ScorerData, SentenceAssignment>();
				final String triggerLabel = SpecAnnotator.getSpecLabel(problem.associatedSpec);
				
				Map<String, Map<ScorerData, SentenceAssignment>> assignmentsArgsDep = new HashMap<String, Map<ScorerData, SentenceAssignment>>();
				Map<String, Map<ScorerData, SentenceAssignment>> assignmentsArgsFree = new HashMap<String, Map<ScorerData, SentenceAssignment>>();
	
				List<Integer> goldTriggerPositions = new ArrayList<Integer>();
				
	
				LoadingCache<Integer, SentenceAssignment> cacheTargetForRole = CacheBuilder.newBuilder()
					.maximumSize(20)
					.build(new CacheLoader<Integer, SentenceAssignment>() {
						public SentenceAssignment load(Integer l) throws CASException {
							SentenceAssignment targetOfRole = problem.target.clone();
							// Filter it by current role!
							for (int i=0; i<problem.size(); i++) {
								Map<Integer, Integer> forI = targetOfRole.getEdgeAssignment().get(i);
								if (forI == null) {
									continue;
								}
								Map<Integer, Integer> forICopy = new HashMap<Integer, Integer>(forI);
								targetOfRole.getEdgeAssignment().put(i, forICopy);
								for (int k=0; k<problem.eventArgCandidates.size(); k++) {
									Integer forK = forICopy.get(k);
									if (forK == null) {
										continue;
									}
									if (!forK.equals(l)) {
										forICopy.remove(k);
									}
								}
	//							if (forICopy.isEmpty()) {
	//								targetOfRole.getEdgeAssignment().put(i, null);
	//							}
								//// DEBUG
								if (l==1 && triggerLabel.equals("Attack") && !forICopy.isEmpty()) {
									System.out.printf("SignalAnalyzer: got an Attacker: %s\n\t\t\tNew: %s\n", problem.target, targetOfRole);
								}
								////
							}
							return targetOfRole;
						}
					});
	
				//// DEBUG
				if (problem.sentInstID.equals("47d") || problem.sentInstID.equals("61d")) {
					System.out.printf("SignalAnalyzer: Hard-coded print of %s: %s\n", problem.sentInstID, problem.target);
				}
				////
	
				
				if (sentNum >= debugMinSentence) {
					System.err.printf("\n%s start %s (%s tokens), ", Utils.detailedLog(), sentNum, problem.size());
				}
					
				sentNum++;
				if (sentNum % SENTENCE_PRINT_FREQ == 1) {
					System.out.printf("%s Processing sentence %s\n", Utils.detailedLog(), sentNum);
				}
				//boolean shouldIncreaseState=false;
				
				 // I removed the full path from docId, and anyway I don't want to use it here anymore. If I ever do, just have SentenceInstance also save docPath separately.
	//			String[] split = problem.docID.split("/");
	//			String docId = split[split.length-1];
	//			String folder = split[0];
	//			String category = AceAnalyzer.getCategory(docId);
				
				Map<String,String> key = new HashMap<String,String>();
				key.put("folder", StatsDocument.ANY /*folder*/);
				key.put("category", StatsDocument.ANY /*category*/);
				key.put("docId", StatsDocument.ANY /*docId*/);
				key.put("spec", triggerLabel);
				key.put("role", "-");
	
	//			if (sentNum % SENTENCE_GC_FREQ == 1) {
	//				System.out.printf("%s *** running gc... ", Pipeline.detailedLog());
	//				System.gc();
	//				System.out.printf("%s done.\n", Pipeline.detailedLog());
	//			}
				
				if (sentNum >= debugMinSentence) {
					System.err.printf("1 ");
				}
	
			
				for(int i=0; i<problem.size(); i++) {
	
					if (sentNum >= debugMinSentence) {
						System.err.printf("\n\t2/%s ", i);
					}
	
					// Only increase state once - whenever moving to next i (next token)
					//shouldIncreaseState = true;
					
					String goldLabel = problem.target.getLabelAtToken(i);
					//Map<String, SignalInstance> specSignals = new HashMap<String, SignalInstance>();
					
					///////////////// TRIGGERS //////////////////
					
					List<Map<String, Map<ScorerData, SignalInstance>>> tokens = (List<Map<String, Map<ScorerData, SignalInstance>>>) problem.get(InstanceAnnotations.NodeTextSignalsBySpec);
					Map<String, Map<ScorerData, SignalInstance>> token = tokens.get(i);
					//Integer triggerNum = problem.types.triggerTypes.get(triggerLabel);
					Map<ScorerData, SignalInstance> scoredSignals = token.get(triggerLabel);
	
	//				System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] addTriggerSignals...\n", new Date());
	//				Map<ScorerData, SignalInstance> scoredSignals = problem.addTriggerSignals(problem.associatedSpec, i, perceptron, specSignals);
	//				System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] done.\n", new Date());
					
					//DEBUG
	//				Collection<SignalInstance> c1 = signalsOfLabel.values();
	//				Collection<SignalInstance> c2 = scoredSignals.values();
	//				Multiset<SignalInstance> m1 = LinkedHashMultiset.create(c1);
	//				Multiset<SignalInstance> m2 = LinkedHashMultiset.create(c2);
	//				boolean b = m1.equals(m2);
					///
	
					if (sentNum >= debugMinSentence) {
						System.err.printf("3/%s ", i);
					}
					
					int m=-1;
					for (ScorerData data : scoredSignals.keySet()) {
						m++;
						
						if (sentNum >= debugMinSentence && DO_VERBOSE_PRINTS) {
							//System.err.printf("4(%s, scorerData=%s) ", i, data);
							System.err.printf("\n\t\t4/%s/%05d ", i, m);
						}
						
						SignalInstance signal = scoredSignals.get(data);
						SentenceAssignment assn = null;
						if (assignments.containsKey(data)) {
							
							if (sentNum >= debugMinSentence && DO_VERBOSE_PRINTS) {
								System.err.printf("5/%s ", i);
							}
							
							assn = assignments.get(data);
						}
						else {
							
							if (sentNum >= debugMinSentence && DO_VERBOSE_PRINTS) {
								System.err.printf("6/%s ", i);
							}
							
							assn = new SentenceAssignment(/*problem.types,*/signalMechanismsContainer, problem.eventArgCandidates, problem.target, problem.nodeTargetAlphabet, problem.edgeTargetAlphabet, problem.featureAlphabet, problem.controller);
							assignments.put(data, assn);
							
							if (sentNum >= debugMinSentence && DO_VERBOSE_PRINTS) {
								System.err.printf("7/%s ", i);
							}
							
						}
						assn.incrementState();
						if (signal.positive) {
							assn.setCurrentNodeLabel(triggerLabel);
						}
						else {
							assn.setCurrentNodeLabel(SentenceAssignment.Default_Trigger_Label);
						}
						
	//					if (sentNum % SENTENCE_PRINT_FREQ == 1 && i%10==0) {
	//						System.out.printf("%s   Adding label to assn (i=%s, scorer=%s)\n", Pipeline.detailedLog(), i, data);
	//					}
						/// DEBUG
	//					if (data.fullName.contains("HYPERNYM")) {
	//						System.err.printf("sent=%s, i=%s, label=%s, scorer=%s, signal=%s: %s\n", sentNum, i, triggerLabel, data, signal, signal.history);
	//					}
						///
	
						
						
						if (sentNum >= debugMinSentence && DO_VERBOSE_PRINTS) {
							System.err.printf("8/%s/%s ", i, debug?signal.history.size():-1);
						}
						
						key.put("signal", data.basicName);
						key.put("el-agg", data.getElementAggregatorTypeName());
						key.put("us-agg", data.getUsageSampleAggregatorTypeName());
						key.put("deriver", data.getDeriverTypeName());
						key.put("derivation", ""+data.derivation);
						//key.put("spec-ind", ""+data.isSpecIndependent);
						key.put("left-sense", ""+data.leftSenseNum);
						key.put("right-sense", ""+data.rightSenseNum);
						
						if (debug) {
							for (Entry<String, String> entry : signal.history.entries()) {
								
								// No need to check if signal is positive - this is already done in SignalMechanismSpecIterator.addToHistory()
								if (goldLabel.equals(triggerLabel)) { 
									docs.updateDocs(key, "SpecItems", "TruePositive", entry.getKey());
									docs.updateDocs(key, "SpecTextItems", "TruePositive", entry);
								}
								else {
									docs.updateDocs(key, "SpecItems", "FalsePositive", entry.getKey());
									docs.updateDocs(key, "SpecTextItems", "FalsePositive", entry);
								}
							}
						}
						
						if (sentNum >= debugMinSentence && DO_VERBOSE_PRINTS) {
							System.err.printf("9/%s ", i);
						}
						
						
	//					if (sentNum % SENTENCE_PRINT_FREQ == 1 && i%10==0) {
	//						System.out.printf("%s   Updated docs (i=%s, scorer=%s, len(history)=%s)\n", Pipeline.detailedLog(), i, data, signal.history.size());
	//					}
	
					}
					
					if (sentNum >= debugMinSentence) {
						System.err.printf("10/%s ", i);
					}
					
	
					/////////// DEPENDENT ARGS ////////////
					
					if (!goldLabel.equals(SentenceAssignment.Default_Trigger_Label)) {
						goldTriggerPositions.add(i);
						
						List<Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>> tokensArgs = (List<Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>>) problem.get(InstanceAnnotations.EdgeDependentTextSignals);
						Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>> tokenArg = tokensArgs.get(i);
						List<Map<String, Map<ScorerData, SignalInstance>>> tokenArgSpec = tokenArg.get(triggerLabel);
						
						for (int k=0; k<problem.eventArgCandidates.size(); k++) {
							Integer targetRoleNum = problem.target.getEdgeAssignment().get(i).get(k);
							
							// I'll allow that.
							if (targetRoleNum == null) {
								continue;
							}
							
							// DEBUG
	//						if (problem.target.edgeTargetAlphabet==null) {
	//							System.err.printf("problem.target.edgeTargetAlphabet==null!!!! problem=%s, problem.target=%s\n", problem, problem.target);
	//						}
	//						if (targetRoleNum==null) {
	//							System.err.printf("targetRoleNum==null!!!! problem=%s, problem.target=%s, i=%s, problem.size()=%s, k=%s, problem.eventArgCandidates.size()=%s\n",
	//									problem, problem.target, i, problem.size(), k, problem.eventArgCandidates.size());
	//						}
							///
							String goldArgLabel = (String) problem.target.edgeTargetAlphabet.lookupObject(targetRoleNum);
	
							for(int l=0; l<problem.edgeTargetAlphabet.size(); l++)
							{	
								String role = (String) problem.edgeTargetAlphabet.lookupObject(l);
								
								if (!SentenceAssignment.Default_Argument_Label.equals(role)) {
									boolean goldIsPositive = goldArgLabel.equals(role); 
									
									// do for dependent args
									doArgs(goldIsPositive, problem, i, k, l, role, key, tokenArgSpec, assignmentsArgsDep, signalMechanismsContainer, cacheTargetForRole, debug);
									//shouldIncreaseState = false;
								}
							}
						}
						
	//					for (int k=0; k<problem.eventArgCandidates.size(); k++) {
	//						Integer targetRoleNum = problem.target.getEdgeAssignment().get(i).get(k);
	//						String goldArgLabel = (String) problem.target.edgeTargetAlphabet.lookupObject(targetRoleNum);
	//	
	//						Map<String, Map<ScorerData, SignalInstance>> tokenArgSpecK = tokenArgSpec.get(k);
	//						
	//						for(int l=0; l<problem.edgeTargetAlphabet.size(); l++)
	//						{	
	//							String role = (String) problem.edgeTargetAlphabet.lookupObject(l);
	//							
	//							key.put("role", role);
	//
	//							Map<ScorerData, SentenceAssignment> assignmentsRole = assignmentsArgs.get(role);
	//							if (assignmentsRole == null) {
	//								assignmentsRole = new HashMap<ScorerData, SentenceAssignment>();
	//								assignmentsArgs.put(role, assignmentsRole);
	//							}
	//							
	//							Map<ScorerData, SignalInstance> scoredArgDepSignals = tokenArgSpecK.get(role);
	//							
	//							for (ScorerData data : scoredArgDepSignals.keySet()) {
	//								
	//								SignalInstance signal = scoredArgDepSignals.get(data);
	//								SentenceAssignment assn = null;
	//								if (assignmentsRole.containsKey(data)) {
	//									assn = assignments.get(data);
	//								}
	//								else {
	//									assn = new SentenceAssignment(signalMechanismsContainer, problem.eventArgCandidates, problem.target, problem.nodeTargetAlphabet, problem.edgeTargetAlphabet, problem.featureAlphabet, problem.controller);
	//									// copy some trigger stuff from the gold - we are putting arg signals on gold triggers!
	//									assn.nodeAssignment = (Vector<Integer>) problem.target.nodeAssignment.clone();
	//									//assn.state = problem.target.state; //wrong, state should mark the beginning, not the end
	//									assignmentsRole.put(data, assn);
	//								}
	//								
	//								assn.incrementState();
	//								if (signal.positive) {
	//									assn.setCurrentEdgeLabel(k, l);
	//								}
	//								else {
	//									assn.setCurrentEdgeLabel(k, SentenceAssignment.Default_Argument_Label);
	//								}
	//	
	//								key.put("signal", data.basicName);
	//								key.put("el-agg", data.getElementAggregatorTypeName());
	//								key.put("us-agg", data.getUsageSampleAggregatorTypeName());
	//								key.put("deriver", data.getDeriverTypeName());
	//								key.put("derivation", ""+data.derivation);
	//								key.put("spec-ind", ""+data.isSpecIndependent);
	//								key.put("left-sense", ""+data.leftSenseNum);
	//								key.put("right-sense", ""+data.rightSenseNum);
	//
	//								if (debug) {
	//									for (Entry<String, String> entry : signal.history.entries()) {
	//										
	//										// No need to check if signal is positive - this is already done in SignalMechanismSpecIterator.addToHistory()
	//										if (goldArgLabel.equals(role)) { 
	//											docs.updateDocs(key, "SpecItems", "TruePositive", entry.getKey());
	//											docs.updateDocs(key, "SpecTextItems", "TruePositive", entry);
	//										}
	//										else {
	//											docs.updateDocs(key, "SpecItems", "FalsePositive", entry.getKey());
	//											docs.updateDocs(key, "SpecTextItems", "FalsePositive", entry);
	//										}
	//									}
	//								}
	//							}
	//						}
	//					}
					}
					
	
					///////////////////////////////
					
					
					//				LinkedHashMap<ScorerData, Multimap<String, String>> allDetailedSignals = new LinkedHashMap<ScorerData, Multimap<String, String>>();
	//				//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] getTriggerDetails...", new Date());
	//				for (SignalMechanism mechanism : perceptron.signalMechanisms) {
	//					allDetailedSignals.putAll(mechanism.getTriggerDetails(problem.associatedSpec, problem, i));
	//				}
					//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] done.\n", new Date());
	//				for (ScorerData data : allDetailedSignals.keySet()) {
	//					Multimap<String, String> details = allDetailedSignals.get(data);
	//					key.put("signal", data.basicName);
	//					key.put("agg", data.aggregator.getClass().getSimpleName());
	//					for (Entry<String, String> entry : details.entries()) {
	//						
	//						// No need to check if signal is positive - this is already done in SignalMechanismSpecIterator.addToHistory()
	//						if (goldLabel.equals(triggerLabel)) { 
	//							docs.updateDocs(key, "SpecTokens", "TruePositive", entry.getKey());
	//							docs.updateDocs(key, "SpecTextTokens", "TruePositive", entry);
	//						}
	//						else {
	//							docs.updateDocs(key, "SpecTokens", "FalsePositive", entry.getKey());
	//							docs.updateDocs(key, "SpecTextTokens", "FalsePositive", entry);
	//						}
	//					}
	//				}
				}
	
				if (sentNum % SENTENCE_PRINT_FREQ == 1) {
					System.out.printf("%s Sentence %s: finished token fields\n", Utils.detailedLog(), sentNum);
				}
				//System.gc();
	
				
				if (sentNum >= debugMinSentence) {
					System.err.printf("\n\t\t11 ");
				}
				
				
				/////////// FREE ARGS ////////////
				// NOTE: this shares the same StatsField, assignmentsRole list, and other stuff, with dependent args. no problem with that.
				
				Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>> tokensArgs = (Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>) problem.get(InstanceAnnotations.EdgeFreeTextSignals);
				//Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>> tokenArg = tokensArgs.get(i);
				List<Map<String, Map<ScorerData, SignalInstance>>> tokenArgSpec = tokensArgs.get(triggerLabel);
				
				/// DEBUG
				if (problem.sentInstID.equals("7d")) {
					System.out.printf("");
				}
				if (problem.sentInstID.equals("8d")) {
					System.out.printf("");
				}
				if (problem.sentInstID.equals("9d")) {
					System.out.printf("");
				}
				///
				for (int k=0; k<problem.eventArgCandidates.size(); k++) {
					for(int l=0; l<problem.edgeTargetAlphabet.size(); l++)
					{	
						String role = (String) problem.edgeTargetAlphabet.lookupObject(l);
						
						if (!SentenceAssignment.Default_Argument_Label.equals(role)) {
							///check if in gold, the current cand is in "role" for any trigger in the sentence
							// hard-coded "or" with short-circuit
							boolean goldIsPositive = false;
							for (int i : goldTriggerPositions) {
								Integer targetRoleNum = problem.target.getEdgeAssignment().get(i).get(k);
								// I'll allow that.
								if (targetRoleNum == null) {
									continue;
								}
								String goldArgLabel = (String) problem.target.edgeTargetAlphabet.lookupObject(targetRoleNum);
								if (goldArgLabel.equals(role)) {
									goldIsPositive = true;
									break;
								}
							}
							
							// do for free args
							// free args are always on state==0, by definition
							doArgs(goldIsPositive, problem, 0, k, l, role, key, tokenArgSpec, assignmentsArgsFree, signalMechanismsContainer, cacheTargetForRole, debug);
						}
					}
				}
				//////////
				
				for (ScorerData data : assignments.keySet()) {
					SentenceAssignment assn = assignments.get(data);
					/// DEBUG
	//				if (/*assn.getNodeAssignment().size() != 13 && */problem.sentInstID.equals("0a")) {
	//					System.out.println("Got it!");
	//				}
					///
					key.put("role", "-");
					key.put("signal", data.basicName);
					key.put("el-agg", data.getElementAggregatorTypeName());
					key.put("us-agg", data.getUsageSampleAggregatorTypeName());
					key.put("deriver", data.getDeriverTypeName());
					key.put("derivation", ""+data.derivation);
					//key.put("spec-ind", ""+data.isSpecIndependent);
					key.put("left-sense", ""+data.leftSenseNum);
					key.put("right-sense", ""+data.rightSenseNum);
					
	//				if (sentNum >= DEBUG_MIN_SENTENCE) {
	//					System.err.printf("12 ");
	//				}
					
					docs.updateDocs(key, "TriggerPerformance", "", assn);
				}
				
				for (String role : assignmentsArgsDep.keySet()) {
					Map<ScorerData, SentenceAssignment> assignmentsOfRole = assignmentsArgsDep.get(role);
					for (ScorerData data : assignmentsOfRole.keySet()) {
						SentenceAssignment assn = assignmentsOfRole.get(data);
						key.put("role", role);
						key.put("signal", data.basicName);
						key.put("el-agg", data.getElementAggregatorTypeName());
						key.put("us-agg", data.getUsageSampleAggregatorTypeName());
						key.put("deriver", data.getDeriverTypeName());
						key.put("derivation", ""+data.derivation);
						//key.put("spec-ind", ""+data.isSpecIndependent);
						key.put("left-sense", ""+data.leftSenseNum);
						key.put("right-sense", ""+data.rightSenseNum);
						docs.updateDocs(key, "ArgDepPerformance", "", assn);
					}
				}
				
				for (String role : assignmentsArgsFree.keySet()) {
					Map<ScorerData, SentenceAssignment> assignmentsOfRole = assignmentsArgsFree.get(role);
					for (ScorerData data : assignmentsOfRole.keySet()) {
						SentenceAssignment assn = assignmentsOfRole.get(data);
						key.put("role", role);
						key.put("signal", data.basicName);
						key.put("el-agg", data.getElementAggregatorTypeName());
						key.put("us-agg", data.getUsageSampleAggregatorTypeName());
						key.put("deriver", data.getDeriverTypeName());
						key.put("derivation", ""+data.derivation);
						//key.put("spec-ind", ""+data.isSpecIndependent);
						key.put("left-sense", ""+data.leftSenseNum);
						key.put("right-sense", ""+data.rightSenseNum);
						docs.updateDocs(key, "ArgFreePerformance", "", assn);
					}
				}
				
				sentIter.remove();
				
				if (sentNum % SENTENCE_PRINT_FREQ == 1) {
					System.out.printf("%s Sentence %s: finished perofrmance (and the sentence itself)\n", Utils.detailedLog(), sentNum);
				}
				
				if (sentNum >= debugMinSentence) {
					System.err.printf("13 ");
				}
				
	
			}
			System.out.printf("[%s] Finished processing %s sentences\n", new Date(), goldInstances.size());

		}
		
		System.out.printf("%s Finished all chunks!\n", Utils.detailedLog());


		//System.gc();

		docs.dumpAsCsvFiles(triggerFile/*, argFile, globalFile*/);
		System.out.printf("\n\n[%s] Fully done!\n", new Date());
	}
	
	public static void doArgs(boolean goldIsPositive, SentenceInstance problem, int i, int k, int l, String role, Map<String,String> key, List<Map<String, Map<ScorerData, SignalInstance>>> tokenArgSpec, Map<String, Map<ScorerData, SentenceAssignment>> assignmentsArgs, SignalMechanismsContainer signalMechanismsContainer, LoadingCache<Integer, SentenceAssignment> cacheTargetForRole, boolean debug) throws StatsException, ExecutionException
	{
//		for (int k=0; k<problem.eventArgCandidates.size(); k++) {
//			Integer targetRoleNum = problem.target.getEdgeAssignment().get(i).get(k);
//			String goldArgLabel = (String) problem.target.edgeTargetAlphabet.lookupObject(targetRoleNum);

			Map<String, Map<ScorerData, SignalInstance>> tokenArgSpecK = tokenArgSpec.get(k);
			
//			for(int l=0; l<problem.edgeTargetAlphabet.size(); l++)
//			{	
//				String role = (String) problem.edgeTargetAlphabet.lookupObject(l);
				
				key.put("role", role);

				/// DEBUG
				//System.out.printf("inst=%s, assignmentsArgs=%s\n\n", problem.sentInstID, assignmentsArgs);
				///
				
				Map<ScorerData, SentenceAssignment> assignmentsRole = assignmentsArgs.get(role);
				if (assignmentsRole == null) {
					assignmentsRole = new HashMap<ScorerData, SentenceAssignment>();
					assignmentsArgs.put(role, assignmentsRole);
				}
				
				//System.out.printf("k=%s, role=%s, tokenArgSpecK=%s\n", k, role, tokenArgSpecK);
				
				Map<ScorerData, SignalInstance> scoredArgDepSignals = tokenArgSpecK.get(role);
				
				for (ScorerData data : scoredArgDepSignals.keySet()) {
					
					SignalInstance signal = scoredArgDepSignals.get(data);
					SentenceAssignment assn = null;
					if (assignmentsRole.containsKey(data)) {
						assn = assignmentsRole.get(data);
					}
					else {
						SentenceAssignment targetOfRole = cacheTargetForRole.get(l);
						
						assn = new SentenceAssignment(signalMechanismsContainer, problem.eventArgCandidates, targetOfRole, problem.nodeTargetAlphabet, problem.edgeTargetAlphabet, problem.featureAlphabet, problem.controller);
						// copy some trigger stuff from the gold - we are putting arg signals on gold triggers!
						assn.nodeAssignment = (Vector<Integer>) problem.target.nodeAssignment.clone();
						//assn.state = problem.target.state; //wrong, state should mark the beginning, not the end
						assignmentsRole.put(data, assn);
						
						// already put in state==0. ArgFree will stay here forever, ArgDep won't.
						//assn.incrementState();
					}
					
					/// DEBUG
//					if (assn.ord == 1002) {
//						System.out.printf("\n\n\n\n\n\n\n1002\n\n\n\n\n\n\n");
//					}
					///
					
					/// DEBUG
					if (role.equals("Attacker")) {
						int countAttacker = 0;
						for (int x=0; x<assn.target.getEdgeAssignment().size(); x++) {
							Map<Integer, Integer> forX = assn.target.getEdgeAssignment().get(x);
							if (forX==null) {
								continue;
							}
							for (int y=0; y<forX.size(); y++) {
								Integer z = forX.get(y);
								if (z==null) {
									continue;
								}
								if (z!=0) {
									countAttacker++;
								}
							}
						}
						//System.out.printf("\n\n\n\n\n\n\nAttacker==%s\n\n\n\n\n\n\n", countAttacker);
						if (countAttacker>0) {
							System.out.printf("\n\n\n\n\n\n\nAttacker is more than 0!!!\n\n\n\n\n\n\n", countAttacker);
						}
					}
					///
					
					/// DEBUG
//					if (assn.ord == 20115) {
//						System.out.printf("\n\n\n\n\n\n\n20115\n\n\n\n\n\n\n");
//					}
					///
					
					//make sure assn.state>0, otherwise this assn was just created nd already incremented
//					if (shouldIncreaseState && assn.state>0) {
//						assn.incrementState();
//					}
					assn.increaseStateTo(i);
					
					if (signal.positive) {
						assn.setCurrentEdgeLabel(k, l);
						
						/// DEBUG
//						if (problem.sentInstID.equals("1f")) {
//							System.out.printf("\n\n\n\n\n1f\n\n\n");
//						}
						////
					}
					else {
						// No need to actually sey anything when it's not an arg
						//assn.setCurrentEdgeLabel(k, SentenceAssignment.Default_Argument_Label);
					}

					key.put("signal", data.basicName);
					key.put("el-agg", data.getElementAggregatorTypeName());
					key.put("us-agg", data.getUsageSampleAggregatorTypeName());
					key.put("deriver", data.getDeriverTypeName());
					key.put("derivation", ""+data.derivation);
					//key.put("spec-ind", ""+data.isSpecIndependent);
					key.put("left-sense", ""+data.leftSenseNum);
					key.put("right-sense", ""+data.rightSenseNum);

					if (debug) {
						for (Entry<String, String> entry : signal.history.entries()) {
							
							// No need to check if signal is positive - this is already done in SignalMechanismSpecIterator.addToHistory()
//							if (goldArgLabel.equals(role)) { 
							if (goldIsPositive) {
								docs.updateDocs(key, "SpecItems", "TruePositive", entry.getKey());
								docs.updateDocs(key, "SpecTextItems", "TruePositive", entry);
							}
							else {
								docs.updateDocs(key, "SpecItems", "FalsePositive", entry.getKey());
								docs.updateDocs(key, "SpecTextItems", "FalsePositive", entry);
							}
						}
					}
				}
//			}
//		}
	}
	
	/**
	 * Horrible horrible log stuff I must do, to keep jwnl from yelling "WARN data.PointerUtils: DICTIONARY_WARN_001" all the time
	 * @throws IOException 
	 */
	public static void main(String args[]) throws Exception {
		if (args.length != 7/*8*/) {
			//System.err.println("USAGE: SignalAnalyzer <input file list> <spec list> <output folder> <use dump files> <trigger doc> <arg doc> <global doc> <debug min sentence>");
			System.err.println("USAGE: SignalAnalyzer <input file list> <spec list> <output folder> <use dump files> <trigger doc> <debug min sentence> <docs chunk>");
			return;
		}
		logger = Utils.handleLog();
		SignalAnalyzer.analyze(new File(args[0]), new File(args[1]), new File(args[2]), Boolean.parseBoolean(args[3]), args[4], /*args[5], args[6], */Integer.parseInt(args[5/*7*/]), Integer.parseInt(args[6]));
	}

	protected static Logger logger;

}
