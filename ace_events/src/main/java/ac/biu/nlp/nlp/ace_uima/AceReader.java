package ac.biu.nlp.nlp.ace_uima;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

import ac.biu.nlp.nlp.ace_uima.utils.UimaUtils;
import ac.biu.nlp.nlp.ace_uima.utils.UimaUtilsException;

import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.common.utilities.file.RecursiveFileListIterator;
import eu.excitementproject.eop.lap.LAPException;

public class AceReader {

	private static File getXmiFile(String jcasId, String xmiFolder) {
		return new File(xmiFolder, jcasId + ".xmi");
	}
	
	private static void dumpCas(String jcasId, JCas jcas, String xmiFolder) throws SAXException, IOException {
		File xmiFile = new File(xmiFolder, jcasId + ".xmi");
		FileOutputStream out = new FileOutputStream(xmiFile);
		XmiCasSerializer ser = new XmiCasSerializer(jcas.getTypeSystem());
		XMLSerializer xmlSer = new XMLSerializer(out, false);
		ser.serialize(jcas.getCas(), xmlSer.getContentHandler());
		out.close();
	}
	
	private static void validateFolder(File path) throws IOException {
		if (path.isDirectory() && !FileUtils.deleteDirectory(path)) {
			throw new IOException("Failed to delete path: " + path.getAbsolutePath());
		}
		path.mkdirs();	
	}
	
//	private static void dumpSingleFileTypeSystem(AnalysisEngine ae) throws ResourceInitializationException, FileNotFoundException, SAXException, IOException {
//		TypeSystemDescription typeSystemDescription = ae.getAnalysisEngineMetaData().getTypeSystem();
//		typeSystemDescription.toXML(new FileOutputStream(new File("./TypeSystem.xml"))); //TODO remove this to a different entry point
//	}
	
	public static void read(boolean override, boolean full, String corpusPath, String xmiFolder) throws InvalidXMLException, ResourceInitializationException, LAPException, InstantiationException, IOException, AceException, AnalysisEngineProcessException, SAXException, UimaUtilsException {
		AnalysisEngine ae;
		if (full) {
			ae = AceFullAEFactory.create();
		}
		else {
			ae = UimaUtils.loadAE("/desc/AceAnnotator.xml");
		}
		
		if (override) {
			validateFolder(new File(xmiFolder));
		}
		else {
			
		}
		//dumpSingleFileTypeSystem(ae);
		
//		Map<String, JCas> jCases = SgmCasLoader.loadFromFolderTree(corpusPath, ae);
		RecursiveFileListIterator iter = SgmCasLoader.getSgmFiles(corpusPath);
		while (iter.hasNext()) {
			//iter.remove();
			//Entry<String, JCas> entry = iter.next();
			File sgmFile = iter.next();
			JCas jcas = SgmCasLoader.loadCas(sgmFile, ae);
			String docId = SgmCasLoader.getDocId(sgmFile);
			//System.gc();
			System.out.printf(docId);
			File xmiFile = getXmiFile(docId, xmiFolder);
			if (!override && xmiFile.isFile()) {
				System.out.printf(" - already exists, skipping\n");
				continue;
			}
			//try {
			//TODO !!!!
			int x = 6; // here just to create a warning, to remember that this is still an issue. 
			if (!docId.startsWith("alt.vacation.las-vegas_20050109.0133")) {
				ae.process(jcas);
			}
			//}
			//catch (AnalysisEngineProcessException e) {}
			dumpCas(docId, jcas, xmiFolder);
			System.out.printf(" - processed and dumped\n");

		}
		
	}
	
	public static void main(String args[]) throws AnalysisEngineProcessException, ResourceInitializationException, LAPException, InstantiationException, IOException, AceException, InvalidXMLException, SAXException, UimaUtilsException {
		if (args.length != 4) {
			System.err.println(USAGE);
			return;
		}
		
		boolean full;
		if (args[0].equals("ace_only")) {
			full=false;
		}
		else if (args[0].equals("full")) {
			full=true;
		}
		else {
			System.err.println(USAGE);
			return;
		}
		
		boolean override;
		if (args[1].equals("add_missing")) {
			override=false;
		}
		else if (args[1].equals("override_all")) {
			override=true;
		}
		else {
			System.err.println(USAGE);
			return;
		}
		
		AceReader.read(override, full, args[2], args[3]);
	}
	
	private static final String USAGE = "USAGE: AceReader <ace_only|full> <add_missing|override_all> <ace corpus path> <xmi output folder>";
}
