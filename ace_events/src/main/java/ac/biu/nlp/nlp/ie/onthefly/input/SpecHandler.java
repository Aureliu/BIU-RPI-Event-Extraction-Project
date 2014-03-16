package ac.biu.nlp.nlp.ie.onthefly.input;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.util.TypeConstraints;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;

public class SpecHandler {

	public static void loadSpecs(Perceptron perceptron, List<String> specXmlPaths) throws CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, UimaUtilsException, IOException, AeException, CASException {
		List<JCas> specs = new ArrayList<JCas>(specXmlPaths.size());
		List<String[]> linesForArgs = new ArrayList<String[]>();
		for (String specXmlPath : specXmlPaths) {
			JCas spec = getPreprocessedSpec(specXmlPath, perceptron);
			specs.add(spec);

			String predicateName = SpecAnnotator.getSpecLabel(spec);
			TypeConstraints.addSpecType(predicateName);
			perceptron.nodeTargetAlphabet.lookupIndex(predicateName);
			
			for (Argument arg : JCasUtil.select(spec, Argument.class)) {
				String role = arg.getRole().getCoveredText();
				List<String> types = Arrays.asList(arg.getTypes().toStringArray());
				perceptron.edgeTargetAlphabet.lookupIndex(predicateName);
				
				List<String> lineList = new ArrayList<String>();
				lineList.add(predicateName);
				lineList.add(role);
				lineList.addAll(types);
				linesForArgs.add((String[]) lineList.toArray());
			}
		}
		perceptron.fillLabelBigrams();
		TypeConstraints.fillArgRolesAndTypesLists(linesForArgs);
	}
	
	private static JCas getPreprocessedSpec(String specXmlPath, Perceptron perceptron) throws UimaUtilsException, ResourceInitializationException, CASRuntimeException, IOException, AeException, AnalysisEngineProcessException {
		JCas spec = null;
		File preprocessed = new File(specXmlPath + PREPROCESSED_SPEC_FILE_EXT);
		if (preprocessed.isFile()) {
			spec = UimaUtils.loadXmi(preprocessed);
		}
		else {
			AnalysisEngine ae = UimaUtils.loadAE(SpecAnnotator.ANNOTATOR_FILE_PATH);
			JCas jcas = ae.newJCas();
			jcas.setDocumentText(FileUtils.loadFileToString(specXmlPath));
			
			SpecAnnotator myAe = (SpecAnnotator) ae;
			myAe.init(perceptron);

			ae.process(jcas);

			try {
				UimaUtils.dumpXmi(preprocessed, spec);
			}
			catch (UimaUtilsException e) {
				Files.deleteIfExists(preprocessed.toPath());
				throw e;
			}
		}
		
		return spec;
	}
	
	public static final String PREPROCESSED_SPEC_FILE_EXT = ".preprocessed";
}
