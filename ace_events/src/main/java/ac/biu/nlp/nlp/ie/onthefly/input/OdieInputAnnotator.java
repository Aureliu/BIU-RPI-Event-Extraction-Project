package ac.biu.nlp.nlp.ie.onthefly.input;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.FSCollectionFactory;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.ie.onthefly.input.jaxb.OdieInput;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentSeed;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.InputMetadata;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.similarity_scorer.FeatureMechanism;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public class OdieInputAnnotator extends JCasAnnotator_ImplBase {
	public static final String LIST_SEPARATOR = ",";
	private Perceptron perceptron = null;
	
	public void setPerceptorn(Perceptron perceptron) {
		this.perceptron = perceptron;
	}

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
			
			//TODO here add all generic preprocessing (like POS tagging, parsing, etc.)
			xxx;
			
			for (FeatureMechanism featureMechanism : perceptron.getFeatureMechanisms()) {
				featureMechanism.preprocessSpec(jCas);
			}
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
