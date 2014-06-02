package ac.biu.nlp.nlp.ace_uima.odie;

import java.io.File;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.xml.sax.SAXException;

import ac.biu.nlp.nlp.ace_uima.odie.uima.InputMetadata;
import ac.biu.nlp.nlp.ace_uima.utils.UimaUtils;

public class OdieInputXmiBuilder {

	private static void build(String inputFilePath, String xmiFilePath) throws InvalidXMLException, ResourceInitializationException, IOException, SAXException, AnalysisEngineProcessException {
		AnalysisEngine ae = UimaUtils.loadAE(OdieInputAnnotator.ANNOTATOR_FILE_PATH);
		JCas jcas = ae.newJCas();
		jcas.setDocumentLanguage("EN");

		InputMetadata meta = new InputMetadata(jcas);
		meta.setInputFilePath(inputFilePath);
		meta.addToIndexes();
		
		ae.process(jcas);

		UimaUtils.dumpXmi(new File(xmiFilePath), jcas);

	}

	public static void main(String[] args) throws InvalidXMLException, ResourceInitializationException, IOException, SAXException, AnalysisEngineProcessException {
		if (args.length != 2) {
			System.err.println(USAGE);
			return;
		}

		OdieInputXmiBuilder.build(args[0], args[1]);
	}
	private static final String USAGE = "USAGE: OdieInputXmiBuilder <input file path> <xmi file path>";
}
