package ac.biu.nlp.nlp.ace_uima.utils;

import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

/**
 * Generic convenience methods when using UIMA.
 * 
 * @author Ofer Bronstein
 * @since August 2013
 */
public class UimaUtils {
	
	// This class should not be instantiated
	private UimaUtils() {}

	/**
	 * Loads an AE from its descriptor.
	 * 
	 * @param aeDescriptorPath path to an xml desciptor of the AE
	 * @return
	 * @throws InvalidXMLException
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngine loadAE(String aeDescriptorPath) throws InvalidXMLException, ResourceInitializationException {
		InputStream s = UimaUtils.class.getResourceAsStream(aeDescriptorPath);
		XMLInputSource in = new XMLInputSource(s, null); 
		ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);		
		return UIMAFramework.produceAnalysisEngine(specifier); 
	}
	
	/**
	 * Loads a CAS from its XMI file.
	 * 
	 * @param xmiFile file to load
	 * @param aeDescriptorPath path to an XML descriptor of SOME analysis engine that connects
	 * to the type system used in the XMI. You can create some Dummy AE for that
	 * (see the one in lap: <tt>src/main/resources/desc/DummyAE.xml</tt>)
	 * @return
	 * @throws InvalidXMLException
	 * @throws ResourceInitializationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static JCas loadXmi(File xmiFile, String aeDescriptorPath) throws InvalidXMLException, ResourceInitializationException, SAXException, IOException {
		AnalysisEngine ae = UimaUtils.loadAE(aeDescriptorPath);
		JCas jcas = ae.newJCas(); 
		FileInputStream inputStream = new FileInputStream(xmiFile);
		XmiCasDeserializer.deserialize(inputStream, jcas.getCas()); 
		inputStream.close();
		return jcas; 
	}
	
	public static void dumpXmi(File xmiFile, JCas jcas) throws SAXException, IOException {
		FileOutputStream out = new FileOutputStream(xmiFile);
		XmiCasSerializer ser = new XmiCasSerializer(jcas.getTypeSystem());
		XMLSerializer xmlSer = new XMLSerializer(out, false);
		ser.serialize(jcas.getCas(), xmlSer.getContentHandler());
		out.close();
	}
	
	public static TypeSystemDescription loadTypeSystem(String relativeTsDescriptionPath) throws InvalidXMLException {
		URL tsUrl = UimaUtils.class.getResource(relativeTsDescriptionPath);
		TypeSystemDescription typeSystem = createTypeSystemDescriptionFromPath(tsUrl.toString());
		typeSystem.resolveImports();
		return typeSystem;
	}
	
	public static TypeSystemDescription loadTypeSystem(String relativeTsDescriptionPath, String existingTypeName) throws InvalidXMLException, UimaUtilsException {
		TypeSystemDescription typeSystem = loadTypeSystem(relativeTsDescriptionPath);
		TypeDescription type = typeSystem.getType(existingTypeName);
		if (type == null) {
			throw new UimaUtilsException("Could not find type " + existingTypeName + " in type system loaded from " + relativeTsDescriptionPath);
		}
		return typeSystem;
	}
}
