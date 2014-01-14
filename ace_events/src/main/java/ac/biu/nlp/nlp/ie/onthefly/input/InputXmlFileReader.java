package ac.biu.nlp.nlp.ie.onthefly.input;

import java.io.FileReader;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import ac.biu.nlp.nlp.ie.onthefly.input.jaxb.OdieInput;

/**
 * Reads the system's input XML via JAXB.
 * Inspired by {@link ac.biu.nlp.nlp.ace_uima.ApfXmlFileReader}.
 * 
 * @author Ofer Bronstein
 * @since August 2013
 */
public class InputXmlFileReader {
	public InputXmlFileReader() throws SAXException {
        xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
		/* ignore DTD files (which do not exist) - replace with an empty input source: */
//        xmlReader.setEntityResolver(new EntityResolver() {
//		    @Override public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
//		    	//System.out.println("resolveEntity:" + publicId + "|" + systemId);
//		    	if (systemId.contains(".dtd")) {  
//		    		return new InputSource(new ByteArrayInputStream("".getBytes()));
//		    	} else {
//		            return null;
//		        }
//		    }
//        });		
	}
	
	public OdieInput fromPath(String fullPathToInputXmlFile) throws IOException, JAXBException  {
		JAXBContext jc = JAXBContext.newInstance("ac.biu.nlp.nlp.ie.onthefly.input.jaxb");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
        Source source = new SAXSource(xmlReader, new InputSource(new FileReader(fullPathToInputXmlFile)));
		Object unmarshalled = unmarshaller.unmarshal(source);
		return (OdieInput) unmarshalled;
	}
	
	protected XMLReader xmlReader;
}
