package ac.biu.nlp.nlp.ie.onthefly.input;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.xml.sax.SAXException;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.InputMetadata;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public class OdieInputXmiBuilder {

	private static JCas build(String inputFilePath) throws InvalidXMLException, ResourceInitializationException, IOException, SAXException, AnalysisEngineProcessException {
		AnalysisEngine ae = UimaUtils.loadAE(OdieInputAnnotator.ANNOTATOR_FILE_PATH);
		JCas jcas = ae.newJCas();
		jcas.setDocumentLanguage("EN");

		InputMetadata meta = new InputMetadata(jcas);
		meta.setInputFilePath(inputFilePath);
		meta.addToIndexes();
		
		ae.process(jcas);

		return jcas;
	}
	
	private static void buildAndDump(String inputFilePath, String xmiFilePath) throws InvalidXMLException, ResourceInitializationException, IOException, SAXException, AnalysisEngineProcessException {
		File xmi = new File(xmiFilePath);
		xmi.getParentFile().mkdirs();
		
		JCas jcas = build(inputFilePath);
		
		UimaUtils.dumpXmi(xmi, jcas);
	}


	public static void main(String[] args) throws InvalidXMLException, ResourceInitializationException, IOException, SAXException, AnalysisEngineProcessException {
		//// TODO //////////
//		String st1 = "desc/OdieInputAnnotator.xml";
//		String st2 = "/" + st1;
//		boolean exists11 = new File(st1).isFile();
//		boolean exists12 = new File(st2).isFile();
//		String koo = "C:/Java/Git/breep/ace_events" + st2;
//		boolean exists2 = new File(koo).isFile();
//		String moo = "C:/Java/Git/breep/ace_events/src/main/resources/desc/OdieInputAnnotator.xml";
//		boolean exists3 = new File(moo).isFile();
//		InputStream s1 = OdieInputAnnotator.class.getResourceAsStream(st1);
//		InputStream s2 = OdieInputAnnotator.class.getResourceAsStream(st2);
//		System.out.printf("%s\n%s\n%s\n", koo, moo, koo.equals(moo));
//		System.out.printf("Got: %s\n", s2);
//		System.err.println("Remove this crappy debug section at once! Jetzt!!!");

		//// TODO /////////
		
		if (args.length != 2) {
			System.err.println(USAGE);
			return;
		}

		OdieInputXmiBuilder.build(args[0], args[1]);
	}
	private static final String USAGE = "USAGE: OdieInputXmiBuilder <spec file path> <xmi file path>";
}
