package ac.biu.nlp.nlp.ie.onthefly.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;

public class SpecHandler {

	public static List<String> readSpecListFile(File specListFile) throws IOException {
		List<String> outList = new LinkedList<String>();
		String line;
		BufferedReader bufferedReader = new BufferedReader(new FileReader(specListFile));
		while ((line = bufferedReader.readLine()) != null) {
			String trimmed = line.trim();
			if (!trimmed.isEmpty()) {
				outList.add(line);
			}
		}
		bufferedReader.close();
		return outList;
	}

	public static List<JCas> getSpecs(List<String> specXmlPaths) throws SpecException, IOException {
		List<JCas> specs = new ArrayList<JCas>(specXmlPaths.size());
		for (String specXmlPath : specXmlPaths) {
			JCas spec = getPreprocessedSpec(specXmlPath);
			specs.add(spec);
		}
		return specs;
	}
	
//	public static void loadSpecs(List<String> specXmlPaths, Perceptron perceptron) throws CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, UimaUtilsException, IOException, AeException, CASException {
//		perceptron.specs = getSpecs(specXmlPaths);
//		
//		perceptron.nodeTargetAlphabet.lookupIndex(SentenceAssignment.Default_Trigger_Label);
//		perceptron.edgeTargetAlphabet.lookupIndex(SentenceAssignment.Default_Argument_Label);
//		
//		List<String[]> linesForArgs = new ArrayList<String[]>();
//		for (JCas spec : perceptron.specs) {
//
//			String predicateName = SpecAnnotator.getSpecLabel(spec);
//			TypeConstraints.addSpecType(predicateName);
//			perceptron.nodeTargetAlphabet.lookupIndex(predicateName);
//			
//			JCas tokenView = spec.getView(SpecAnnotator.TOKEN_VIEW);
//			for (Argument arg : JCasUtil.select(tokenView, Argument.class)) {
//				String role = arg.getRole().getCoveredText();
//				List<ArgumentType> types = JCasUtil.selectCovered(tokenView, ArgumentType.class, arg);
//				List<String> typeStrs = JCasUtil.toText(types);
//				perceptron.edgeTargetAlphabet.lookupIndex(role);
//				
//				List<String> lineList = new ArrayList<String>();
//				lineList.add(predicateName);
//				lineList.add(role);
//				lineList.addAll(typeStrs);
//				String[] lineArray = new String[lineList.size()];
//				lineArray = lineList.toArray(lineArray);
//				linesForArgs.add(lineArray);
//			}
//		}
//		perceptron.fillLabelBigrams();
//		TypeConstraints.fillArgRolesAndTypesLists(linesForArgs);
//	}
	
	private static JCas getPreprocessedSpec(String specXmlPath/*, Perceptron perceptron*/) throws SpecException, IOException {
		JCas spec = null;
		boolean shouldDeletePreprocessed = false;
		File preprocessed = new File(specXmlPath + PREPROCESSED_SPEC_FILE_EXT);
		try {
			if (preprocessed.isFile()) {
				spec = UimaUtils.loadXmi(preprocessed, SpecAnnotator.ANNOTATOR_FILE_PATH);
			}
			else {
				AnalysisEngine ae = UimaUtils.loadAE(SpecAnnotator.ANNOTATOR_FILE_PATH);
				
				//AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(SpecAnnotator.class);
	//			AnalysisEngine ae;
	//			try {
	//				ae = AnalysisEngineFactory.createAnalysisEngine("desc.SpecAnnotator");
	//			} catch (UIMAException e) {
	//				throw new AnalysisEngineProcessException(e);
	//			}
	
				
				
				
				spec = ae.newJCas();
				spec.setDocumentText(FileUtils.loadFileToString(specXmlPath));
				
				//SpecAnnotator myAe = (SpecAnnotator) ae;
				//myAe.init(perceptron);
				
				ae.process(spec);
				
	//			try {
	//				JCas tokenView = spec.getView(SpecAnnotator.TOKEN_VIEW);
	//				AnalysisEngine tokenAE = AnalysisEngines.forSpecTokenView(SpecAnnotator.TOKEN_VIEW);
	//				tokenAE.process(tokenView);
	//				
	//				JCas sentenceView = spec.getView(SpecAnnotator.SENTENCE_VIEW);
	//				AnalysisEngine sentenceAE = AnalysisEngines.forSpecSentenceView(SpecAnnotator.SENTENCE_VIEW);
	//				sentenceAE.process(sentenceView);
	//			} catch (CASException e) {
	//				throw new AeException(e);
	//			}
				
				
	//			spec = ae3.newJCas();
	//			spec.setDocumentText(FileUtils.loadFileToString(specXmlPath));
	//			
	//			//SpecAnnotator myAe = (SpecAnnotator) ae;
	//			//myAe.init(perceptron);
	//			
	//			ae3.process(spec);
	
				
				
				shouldDeletePreprocessed = true;
				UimaUtils.dumpXmi(preprocessed, spec);
				shouldDeletePreprocessed = false;
			}
		}
		catch (Exception e) {
			throw new SpecException(String.format("Exception in processing spec\"%s\" - %s", specXmlPath, e.toString()));
		}
		finally {
			if (shouldDeletePreprocessed) {
				Files.deleteIfExists(preprocessed.toPath());
			}
		}
		
		return spec;
	}
	
	public static final String PREPROCESSED_SPEC_FILE_EXT = ".preprocessed.xmi";
}
