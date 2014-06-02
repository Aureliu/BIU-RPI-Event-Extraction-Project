package ac.biu.nlp.nlp.ace_uima.analyze;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ac.biu.nlp.nlp.ie.onthefly.input.SpecHandler;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.core.Pipeline;
import edu.cuny.qc.perceptron.types.SentenceInstance;

public class SignalAnalyzer {
	private static final String CORPUS_DIR = "src/main/resources/corpus/qi";
	private static final String CONTROLLER_PARAMS =
					"beamSize=4 maxIterNum=20 skipNonEventSent=true avgArguments=true skipNonArgument=true useGlobalFeature=false " +
					"addNeverSeenFeatures=true crossSent=false crossSentReranking=false order=0 evaluatorType=1 learnBigrams=true logLevel=3 oMethod=F";

	public static void analyze(File inputFileList, File specList, File outputFolder, String triggerDocName, String argDocName, String globalDocName) throws Exception {
		List<String> specXmlPaths = SpecHandler.readSpecListFile(specList);
		TypesContainer types = new TypesContainer(specXmlPaths, false);
		Perceptron perceptron = new Perceptron(null);
		perceptron.controller = new Controller();
		perceptron.controller.setValueFromArguments(StringUtils.split(CONTROLLER_PARAMS));

		List<SentenceInstance> instances = Pipeline.readInstanceList(perceptron, types, new File(CORPUS_DIR), inputFileList, null, false);
		
//		TODO:
//		- Build some global.. thing... that has the gold annotation
//		- go through sentences, and update DocCollection according to signals, from: instance.get(InstanceAnnotations.NodeTextSignalsBySpec) 
	}
	
	public static void main(String args[]) throws Exception {
		if (args.length != 6) {
			System.err.println("USAGE: SignalAnalyzer <input file list> <spec list> <output folder> <trigger doc> <arg doc> <global doc>");
			return;
		}
		SignalAnalyzer.analyze(new File(args[0]), new File(args[1]), new File(args[2]), args[3], args[4], args[5]);
	}

}
