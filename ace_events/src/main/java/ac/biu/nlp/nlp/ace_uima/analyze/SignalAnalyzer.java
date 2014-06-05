package ac.biu.nlp.nlp.ace_uima.analyze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Multimap;

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

public class SignalAnalyzer {
	private static final String CORPUS_DIR = "src/main/resources/corpus/qi";
	private static final String CONTROLLER_PARAMS =
					"beamSize=4 maxIterNum=20 skipNonEventSent=true avgArguments=true skipNonArgument=true useGlobalFeature=false " +
					"addNeverSeenFeatures=true crossSent=false crossSentReranking=false order=0 evaluatorType=1 learnBigrams=true logLevel=3 oMethod=F";
	private static SignalAnalyzerDocumentCollection docs = new SignalAnalyzerDocumentCollection();

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
		SignalPerformanceField.goldInstances = goldInstances;
		
		// Iterating instances, and for each creating assignment - ONE FOR EACH SIGNAL
		// inspired by BeamSearch.beamSearch
		for (SentenceInstance problem : goldInstances) {
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
			
			for(int i=0; i<problem.size(); i++) {
				String goldLabel = problem.target.getLabelAtToken(i);
				Map<String, SignalInstance> specSignals = new HashMap<String, SignalInstance>();
				Map<ScorerData, SignalInstance> scoredSignals = problem.addTriggerSignals(problem.associatedSpec, i, perceptron, specSignals);
				for (ScorerData data : scoredSignals.keySet()) {
					SignalInstance signal = scoredSignals.get(data);
					SentenceAssignment assn = null;
					if (assignments.containsKey(data)) {
						assn = assignments.get(data);
					}
					else {
						assn = new SentenceAssignment(problem.types, problem.nodeTargetAlphabet, problem.edgeTargetAlphabet, problem.featureAlphabet, problem.controller);
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
				for (SignalMechanism mechanism : perceptron.signalMechanisms) {
					allDetailedSignals.putAll(mechanism.getTriggerDetails(problem.associatedSpec, problem, i));
				}
				for (ScorerData data : allDetailedSignals.keySet()) {
					Multimap<String, String> details = allDetailedSignals.get(data);
					key.put("signal", data.basicName);
					key.put("aggregator", data.aggregator.getClass().getSimpleName());
					for (Entry<String, String> entry : details.entries()) {
						
						// No need to check if signal is positive - this is already done in SignalMechanismSpecIterator.addToHistory()
						if (goldLabel.equals(triggerLabel)) { 
							docs.updateDocs(key, "SpecTokens", "TruePositive", entry.getKey());
						}
						else {
							docs.updateDocs(key, "SpecTokens", "FalsePositive", entry.getKey());
						}
					}
				}
			}
			
			for (ScorerData data : assignments.keySet()) {
				SentenceAssignment assn = assignments.get(data);
				key.put("signal", data.basicName);
				key.put("aggregator", data.aggregator.getClass().getSimpleName());
				docs.updateDocs(key, "Performance", "", assn);
			}
		}
		
		docs.dumpAsCsvFiles(triggerFile, argFile, globalFile);
	}
	
	public static void fileInit(File f) throws FileNotFoundException {
		File prev = new File(f.getAbsolutePath() + ".previous");
		if (prev.isFile()) {
			prev.delete();
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
