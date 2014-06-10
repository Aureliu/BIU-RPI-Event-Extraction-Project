package ac.biu.nlp.nlp.ace_uima.analyze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import ac.biu.nlp.nlp.ace_uima.stats.SignalPerformanceField;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecHandler;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.core.Pipeline;
import edu.cuny.qc.perceptron.similarity_scorer.ScorerData;
import edu.cuny.qc.perceptron.similarity_scorer.SignalMechanism;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SignalInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;

public class SignalAnalyzer {
	private static final String CORPUS_DIR = "src/main/resources/corpus/qi";
	private static final String CONTROLLER_PARAMS =
					"beamSize=4 maxIterNum=20 skipNonEventSent=true avgArguments=true skipNonArgument=true useGlobalFeature=false " +
					"addNeverSeenFeatures=true crossSent=false crossSentReranking=false order=0 evaluatorType=1 learnBigrams=true logLevel=3 " +
					"oMethod=F serialization=BZ2";
	private static SignalAnalyzerDocumentCollection docs = new SignalAnalyzerDocumentCollection();
	private static final int SENTENCE_PRINT_FREQ = 100;

	public static void analyze(File inputFileList, File specList, File outputFolder, String triggerDocName, String argDocName, String globalDocName) throws Exception {
		(new PrintStream(new File(outputFolder, "start"))).close();
		AceAnalyzer.fillCategories();

		File triggerFile = new File(outputFolder, triggerDocName);
		File argFile = new File(outputFolder, argDocName);
		File globalFile = new File(outputFolder, globalDocName);
		
		fileInit(triggerFile);
		fileInit(argFile);
		fileInit(globalFile);

		List<String> specXmlPaths = SpecHandler.readSpecListFile(specList);
		TypesContainer types = new TypesContainer(specXmlPaths, false);
		Perceptron perceptron = new Perceptron(null);
		perceptron.controller = new Controller();
		perceptron.controller.setValueFromArguments(StringUtils.split(CONTROLLER_PARAMS));
		
		List<SentenceInstance> goldInstances = Pipeline.readInstanceList(perceptron, types, new File(CORPUS_DIR), inputFileList, new Alphabet(), false);
		//SignalPerformanceField.goldInstances = goldInstances;
		System.out.printf("[%s] Finished reading documents, starting to process %s sentences\n", new Date(), goldInstances.size());
		
		// Iterating instances, and for each creating assignment - ONE FOR EACH SIGNAL
		// inspired by BeamSearch.beamSearch
		int sentNum = 0;
		for (SentenceInstance problem : goldInstances) {
			sentNum++;
			if (sentNum % SENTENCE_PRINT_FREQ == 1) {
				System.out.printf("[%s] Processing sentence %s\n", new Date(), sentNum);
			}
			Map<ScorerData, SentenceAssignment> assignments = new LinkedHashMap<ScorerData, SentenceAssignment>();
			String triggerLabel = SpecAnnotator.getSpecLabel(problem.associatedSpec);
			
			String[] split = problem.docID.split("/");
			String docId = split[split.length-1];
			String folder = split[0];
			String category = AceAnalyzer.getCategory(docId);
			
			Map<String,String> key = new HashMap<String,String>();
			key.put("folder", folder);
			key.put("category", category);
			key.put("docId", docId);
			key.put("label", triggerLabel);
			
			System.gc();
			
			for(int i=0; i<problem.size(); i++) {
				String goldLabel = problem.target.getLabelAtToken(i);
				//Map<String, SignalInstance> specSignals = new HashMap<String, SignalInstance>();
				
				List<Map<String, Map<ScorerData, SignalInstance>>> tokens = (List<Map<String, Map<ScorerData, SignalInstance>>>) problem.get(InstanceAnnotations.NodeTextSignalsBySpec);
				Map<String, Map<ScorerData, SignalInstance>> token = tokens.get(i);
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
				for (ScorerData data : scoredSignals.keySet()) {
					SignalInstance signal = scoredSignals.get(data);
					SentenceAssignment assn = null;
					if (assignments.containsKey(data)) {
						assn = assignments.get(data);
					}
					else {
						assn = new SentenceAssignment(problem.types, problem, problem.nodeTargetAlphabet, problem.edgeTargetAlphabet, problem.featureAlphabet, problem.controller);
						assignments.put(data, assn);
					}
					assn.incrementState();
					if (signal.positive) {
						assn.setCurrentNodeLabel(triggerLabel);
					}
					else {
						assn.setCurrentNodeLabel(SentenceAssignment.Default_Trigger_Label);
					}
				}
				
				LinkedHashMap<ScorerData, Multimap<String, String>> allDetailedSignals = new LinkedHashMap<ScorerData, Multimap<String, String>>();
				//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] getTriggerDetails...", new Date());
				for (SignalMechanism mechanism : perceptron.signalMechanisms) {
					allDetailedSignals.putAll(mechanism.getTriggerDetails(problem.associatedSpec, problem, i));
				}
				//System.out.printf("[%1$tH:%1$tM:%1$tS.%1$tL] done.\n", new Date());
				for (ScorerData data : allDetailedSignals.keySet()) {
					Multimap<String, String> details = allDetailedSignals.get(data);
					key.put("signal", data.basicName);
					key.put("aggregator", data.aggregator.getClass().getSimpleName());
					for (Entry<String, String> entry : details.entries()) {
						
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
				}
			}

			System.gc();

			for (ScorerData data : assignments.keySet()) {
				SentenceAssignment assn = assignments.get(data);
				/// DEBUG
//				if (/*assn.getNodeAssignment().size() != 13 && */problem.sentInstID.equals("0a")) {
//					System.out.println("Got it!");
//				}
				///
				key.put("signal", data.basicName);
				key.put("aggregator", data.aggregatorTypeName);
				docs.updateDocs(key, "Performance", "", assn);
			}
		}

		System.out.printf("[%s] Finished processing %s sentences\n", new Date(), goldInstances.size());

		System.gc();

		docs.dumpAsCsvFiles(triggerFile, argFile, globalFile);
		System.out.printf("[%s] Done", new Date());
	}
	
	public static void fileInit(File f) throws FileNotFoundException {
		File prev = new File(f.getAbsolutePath() + ".previous");
		if (prev.isFile()) {
			prev.delete();
		}
		if (f.isFile()) {
			f.renameTo(prev);
		}
		PrintStream p = new PrintStream(f);
		p.printf("(file is writable - verified)");
		p.close();
	}
	public static void main(String args[]) throws Exception {
		if (args.length != 6) {
			System.err.println("USAGE: SignalAnalyzer <input file list> <spec list> <output folder> <trigger doc> <arg doc> <global doc>");
			return;
		}
		SignalAnalyzer.analyze(new File(args[0]), new File(args[1]), new File(args[2]), args[3], args[4], args[5]);
	}

}
