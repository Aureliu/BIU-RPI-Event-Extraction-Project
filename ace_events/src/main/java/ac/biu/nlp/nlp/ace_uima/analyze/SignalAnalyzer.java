package ac.biu.nlp.nlp.ace_uima.analyze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.io.Files;

import ac.biu.nlp.nlp.ace_uima.stats.SignalPerformanceField;
import ac.biu.nlp.nlp.ace_uima.stats.StatsDocument;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecHandler;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.core.Pipeline;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SignalInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismsContainer;
import edu.cuny.qc.util.Utils;

public class SignalAnalyzer {
	private static final String CORPUS_DIR = "src/main/resources/corpus/qi";
	private static final String CONTROLLER_PARAMS =
					"beamSize=4 maxIterNum=20 skipNonEventSent=true avgArguments=true skipNonArgument=true useGlobalFeature=false " +
					"addNeverSeenFeatures=true crossSent=false crossSentReranking=false order=0 evaluatorType=1 learnBigrams=true logLevel=3 " +
					"oMethod=F serialization=BZ2 featureProfile=ANALYSIS";
	private static SignalAnalyzerDocumentCollection docs = new SignalAnalyzerDocumentCollection();
	private static final int SENTENCE_PRINT_FREQ = 200;
	private static final int SENTENCE_GC_FREQ = 20000;
	//private static final int debugMinSentence = 1310;
	private static final boolean DO_VERBOSE_PRINTS = true; //warning! only use this for really fine-grained debugging - this prints a lot!!!

	public static void analyze(File inputFileList, File specList, File outputFolder, boolean useDumps, String triggerDocName, String argDocName, String globalDocName, Integer debugMinSentence) throws Exception {
		(new PrintStream(new File(outputFolder, "start"))).close();
		if (debugMinSentence < 0) {
			debugMinSentence = Integer.MAX_VALUE;
		}
		
		AceAnalyzer.fillCategories();

		File triggerFile = new File(outputFolder, triggerDocName);
		File argFile = new File(outputFolder, argDocName);
		File globalFile = new File(outputFolder, globalDocName);
		
		Utils.fileInit(triggerFile);
		Utils.fileInit(argFile);
		Utils.fileInit(globalFile);

		List<String> specXmlPaths = SpecHandler.readSpecListFile(specList);
		TypesContainer types = new TypesContainer(specXmlPaths, false);
		Controller controller = new Controller();
		controller.setValueFromArguments(StringUtils.split(CONTROLLER_PARAMS));
		controller.usePreprocessFiles = useDumps;
		controller.useSignalFiles = useDumps;
		SignalMechanismsContainer signalMechanismsContainer = new SignalMechanismsContainer(controller);
		//Perceptron perceptron = new Perceptron(null, controller, outputFolder, signalMechanismsContainer);
		
		Collection<SentenceInstance> goldInstances = Pipeline.readInstanceList(controller, signalMechanismsContainer, types, new File(CORPUS_DIR), inputFileList, new Alphabet(), false, true).values();
		//SignalPerformanceField.goldInstances = goldInstances;
		System.out.printf("[%s] Finished reading documents, starting to process %s sentences\n", new Date(), goldInstances.size());
		
		// Iterating instances, and for each creating assignment - ONE FOR EACH SIGNAL
		// inspired by BeamSearch.beamSearch
		int sentNum = 0;
		//for (SentenceInstance problem : goldInstances) {
		for (Iterator<SentenceInstance> sentIter = goldInstances.iterator(); sentIter.hasNext();) {
			SentenceInstance problem = sentIter.next();
			
			if (sentNum >= debugMinSentence) {
				System.err.printf("\n%s start %s (%s tokens), ", Utils.detailedLog(), sentNum, problem.size());
			}
				
			sentNum++;
			if (sentNum % SENTENCE_PRINT_FREQ == 1) {
				System.out.printf("%s Processing sentence %s\n", Utils.detailedLog(), sentNum);
			}
			Map<ScorerData, SentenceAssignment> assignments = new HashMap<ScorerData, SentenceAssignment>();
			String triggerLabel = SpecAnnotator.getSpecLabel(problem.associatedSpec);
			
			 // I removed the full path from docId, and anyway I don't want to use it here anymore. If I ever do, just have SentenceInstance also save docPath separately.
//			String[] split = problem.docID.split("/");
//			String docId = split[split.length-1];
//			String folder = split[0];
//			String category = AceAnalyzer.getCategory(docId);
			
			Map<String,String> key = new HashMap<String,String>();
			key.put("folder", StatsDocument.ANY /*folder*/);
			key.put("category", StatsDocument.ANY /*category*/);
			key.put("docId", StatsDocument.ANY /*docId*/);
			key.put("label", triggerLabel);
			
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

				String goldLabel = problem.target.getLabelAtToken(i);
				//Map<String, SignalInstance> specSignals = new HashMap<String, SignalInstance>();
				
				List<Map<Integer, Map<ScorerData, SignalInstance>>> tokens = (List<Map<Integer, Map<ScorerData, SignalInstance>>>) problem.get(InstanceAnnotations.NodeTextSignalsBySpec);
				Map<Integer, Map<ScorerData, SignalInstance>> token = tokens.get(i);
				Integer triggerNum = problem.types.triggerTypes.get(triggerLabel);
				Map<ScorerData, SignalInstance> scoredSignals = token.get(triggerNum);

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
				
				int k=-1;
				for (ScorerData data : scoredSignals.keySet()) {
					k++;
					
					if (sentNum >= debugMinSentence && DO_VERBOSE_PRINTS) {
						//System.err.printf("4(%s, scorerData=%s) ", i, data);
						System.err.printf("\n\t\t4/%s/%05d ", i, k);
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
						
						assn = new SentenceAssignment(/*problem.types,*/problem.eventArgCandidates, problem.target, problem.nodeTargetAlphabet, problem.edgeTargetAlphabet, problem.featureAlphabet, problem.controller);
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
						System.err.printf("8/%s/%s ", i, signal.history.size());
					}
					
					key.put("signal", data.basicName);
					key.put("agg", data.getAggregatorTypeName());
					key.put("deriver", data.getDeriverTypeName());
					key.put("derivation", ""+data.derivation);
					key.put("spec-ind", ""+data.isSpecIndependent);
					key.put("left-sense", ""+data.leftSenseNum);
					key.put("right-sense", ""+data.rightSenseNum);
					for (Entry<String, String> entry : signal.history.entries()) {
						
						// No need to check if signal is positive - this is already done in SignalMechanismSpecIterator.addToHistory()
						if (goldLabel.equals(triggerLabel)) { 
							docs.updateDocs(key, "SpecTokens", "TruePositive", entry.getKey());
							docs.updateDocs(key, "SpecTextTokens", "TruePositive", entry);
						}
						else {
							docs.updateDocs(key, "SpecTokens", "FalsePositive", entry.getKey());
							docs.updateDocs(key, "SpecTextTokens", "FalsePositive", entry);
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
			
			for (ScorerData data : assignments.keySet()) {
				SentenceAssignment assn = assignments.get(data);
				/// DEBUG
//				if (/*assn.getNodeAssignment().size() != 13 && */problem.sentInstID.equals("0a")) {
//					System.out.println("Got it!");
//				}
				///
				key.put("signal", data.basicName);
				key.put("agg", data.getAggregatorTypeName());
				key.put("deriver", data.getDeriverTypeName());
				key.put("derivation", ""+data.derivation);
				key.put("spec-ind", ""+data.isSpecIndependent);
				key.put("left-sense", ""+data.leftSenseNum);
				key.put("right-sense", ""+data.rightSenseNum);
				
//				if (sentNum >= DEBUG_MIN_SENTENCE) {
//					System.err.printf("12 ");
//				}
				
				docs.updateDocs(key, "Performance", "", assn);
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

		//System.gc();

		docs.dumpAsCsvFiles(triggerFile, argFile, globalFile);
		System.out.printf("[%s] Fully done!\n", new Date());
	}
	
	/**
	 * Horrible horrible log stuff I must do, to keep jwnl from yelling "WARN data.PointerUtils: DICTIONARY_WARN_001" all the time
	 * @throws IOException 
	 */
	public static void handleLog() throws IOException {
		File target = new File("./target/classes/log4j.properties");
		Files.createParentDirs(target);
		Files.copy(new File("./log4j.properties"), target);
		logger = Logger.getLogger(SignalAnalyzer.class);
	}
	public static void main(String args[]) throws Exception {
		if (args.length != 8) {
			System.err.println("USAGE: SignalAnalyzer <input file list> <spec list> <output folder> <use dump files> <trigger doc> <arg doc> <global doc> <debug min sentence>");
			return;
		}
		handleLog();
		SignalAnalyzer.analyze(new File(args[0]), new File(args[1]), new File(args[2]), Boolean.parseBoolean(args[3]), args[4], args[5], args[6], Integer.parseInt(args[7]));
	}

	protected static Logger logger;

}
