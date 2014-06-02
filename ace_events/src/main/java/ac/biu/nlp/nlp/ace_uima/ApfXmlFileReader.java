package ac.biu.nlp.nlp.ace_uima;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import ac.biu.nlp.nlp.ace_uima.jaxb.ApfSourceFileType;

/**
 * A class for reading an ACE corpus file (of type *.apf.xml) into an internal structure.
 * @see http://en.wikipedia.org/wiki/Automatic_Content_Extraction 
 * @author Erel Segal
 * @since 29/11/2011
 */
public class ApfXmlFileReader {
	public ApfXmlFileReader() throws SAXException {
        xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
		/* ignore DTD files (which do not exist) - replace with an empty input source: */
        xmlReader.setEntityResolver(new EntityResolver() {
		    @Override public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		    	//System.out.println("resolveEntity:" + publicId + "|" + systemId);
		    	if (systemId.contains(".dtd")) {  
		    		return new InputSource(new ByteArrayInputStream("".getBytes()));
		    	} else {
		            return null;
		        }
		    }
        });		
	}
	
	/**
	 * @param fullPathToFile e.g. "/home/.../Ace/Files/corpus/bc/timex2norm/CNN_IP_20030407.1600.05.apf.xml".
	 */
	@SuppressWarnings("unchecked")
	public ApfSourceFileType fromApfXmlFile(String fullPathToApfXmlFile) throws IOException, JAXBException  {
		JAXBContext jc = JAXBContext.newInstance("ac.biu.nlp.nlp.ace_uima.jaxb");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
        Source source = new SAXSource(xmlReader, new InputSource(new FileReader(fullPathToApfXmlFile)));
		return ((JAXBElement<ApfSourceFileType>)unmarshaller.unmarshal(source)).getValue();
	}
	
	protected XMLReader xmlReader;
}
