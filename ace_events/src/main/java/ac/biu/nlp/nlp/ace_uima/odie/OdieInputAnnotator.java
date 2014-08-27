package ac.biu.nlp.nlp.ace_uima.odie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.JAXBException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.FSCollectionFactory;
import org.uimafit.util.JCasUtil;
import org.xml.sax.SAXException;

import ac.biu.nlp.nlp.ace_uima.odie.jaxb.OdieInput;
import ac.biu.nlp.nlp.ace_uima.odie.uima.Argument;
import ac.biu.nlp.nlp.ace_uima.odie.uima.ArgumentSeed;
import ac.biu.nlp.nlp.ace_uima.odie.uima.InputMetadata;
import ac.biu.nlp.nlp.ace_uima.odie.uima.Predicate;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public class OdieInputAnnotator extends JCasAnnotator_ImplBase {
	public static final String LIST_SEPARATOR = ",";

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {
			InputMetadata meta = JCasUtil.selectSingle(jCas, InputMetadata.class);
			String inputPath = meta.getInputFilePath();
			jCas.setDocumentText(FileUtils.loadFileToString(inputPath));
			OdieInput input = new InputXmlFileReader().fromPath(inputPath);
			TypeSystemDescription typeSystemDescription = UimaUtils.loadTypeSystem(BASIC_TYPE_SYSTEM_FILE_PATH);
			
			meta.setCorpusXmiFolderPath(input.getCorpus().getXmiFolderPath());
			
			Predicate predicate = new Predicate(jCas);
			predicate.setName(input.getPredicate().getName());
			predicate.addToIndexes();
			
			
			for (OdieInput.Arguments.Argument arg : input.getArguments().getArgument()) {
				Argument argument = new Argument(jCas);
				argument.setRole(arg.getRole());
				argument.setTypes(listToStringArray(jCas, arg.getTypes()));
				argument.addToIndexes();
				
				// Add a specific UIMA type for each role
				typeSystemDescription.addType(ArgumentSeed.class.getPackage().getName() + ".roles." + arg.getRole(), "", ArgumentSeed.class.getName());
			}

			// Dump update type system to XML
			URL url = UimaUtils.class.getResource(FULL_TYPE_SYSTEM_FILE_PATH);
			FileOutputStream out = new FileOutputStream(new File(url.toURI()));
			typeSystemDescription.toXML(out);
		}
		catch (Exception e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e); 
		}

	}
	
	private StringArray listToStringArray(JCas jCas, String listStr)  {
		String[] split = listStr.split(LIST_SEPARATOR);
		return (StringArray) FSCollectionFactory.createStringArray(jCas, split);
	}
	
	public static final String ANNOTATOR_FILE_PATH = "/desc/OdieInputAnnotator.xml";
	public static final String BASIC_TYPE_SYSTEM_FILE_PATH = "/desc/OdieInputTypes.xml";
	public static final String FULL_TYPE_SYSTEM_FILE_PATH = "/desc/OdieInputTypesFull.xml";
}
