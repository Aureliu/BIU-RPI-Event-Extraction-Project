package ac.biu.nlp.nlp.ie.onthefly.input;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.FSCollectionFactory;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentRole;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentType;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.similarity_scorer.FeatureMechanism;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public class SpecAnnotator extends JCasAnnotator_ImplBase {
	public static final String LIST_SEPARATOR = ",";
	private Perceptron perceptron = null;
	
	public void setPerceptorn(Perceptron perceptron) {
		this.perceptron = perceptron;
	}
	
	public static String getSpecLabel(JCas spec) {
		Predicate pred = JCasUtil.selectSingle(spec, Predicate.class);
		return pred.getName();
	}

	public static List<String> getSpecRoles(JCas spec) {
		Collection<Argument> args = JCasUtil.select(spec, Argument.class);
		List<String> ret = new ArrayList<String>(args.size());
		for (Argument arg : args) {
			ret.add(arg.getRole());
		}
		return ret;
	}
	
	private List<AnnotationFS> getElementList(JCas jcas, AnnotationFS container, String elementName, Class<?> annotationType) throws SpecXmlException {
		Type type = null;
		List<AnnotationFS> results = new ArrayList<AnnotationFS>();
		if (annotationType != null) {
			type = JCasUtil.getAnnotationType(jcas, annotationType);
		}
		
		String pattern = String.format(XML_ELEMENT, elementName, elementName);
		Matcher matcher = Pattern.compile(pattern).matcher(container.getCoveredText());
		while (matcher.find()) {
			MatchResult result = matcher.toMatchResult();
			int begin = container.getBegin() + result.start(1);
			int end   = container.getEnd()   + result.end(1);
			
			AnnotationFS anno = null;
			if (annotationType == null) {
				anno = new Annotation(jcas, begin, end);
			}
			else {
				anno = jcas.getCas().createAnnotation(type, begin, end);
				jcas.addFsToIndexes(anno);
			}
			results.add(anno);
		}
		return results;
	}
	
	public List<AnnotationFS> getSectionList(JCas jcas, AnnotationFS container, String elementName) throws SpecXmlException {
		return getElementList(jcas, container, elementName, null);
	}

	public List<AnnotationFS> addItems(JCas view, AnnotationFS container, String itemName, Class<?> annotationType) throws SpecXmlException {
		if (annotationType == null) {
			throw new SpecXmlException("When adding items, a specific annotation type must be given");
		}
		return getElementList(view, container, itemName, annotationType);
	}
	
	public List<AnnotationFS> addItemsInSingleSection(JCas view, AnnotationFS container, String sectionName, String itemName, Class<?> annotationType) throws SpecXmlException, CASException {
		AnnotationFS section = getSingleSection(container, sectionName);
		return addItems(view, section, itemName, annotationType);
	}
	
	public AnnotationFS addSingleItem(JCas view, AnnotationFS container, String itemName, Class<?> annotationType) throws SpecXmlException {
		List<AnnotationFS> results = addItems(view, container, itemName, annotationType);
		if (results.size() != 1) {
			throw new SpecXmlException(String.format("Expected a single element named \"%s\", got %d", itemName, results.size()));
		}
		return results.get(0);
	}

	public AnnotationFS getSingleSection(AnnotationFS container, String sectionName) throws SpecXmlException, CASException {
		List<AnnotationFS> results = getElementList(container.getCAS().getJCas(), container, sectionName, null);
		if (results.size() != 1) {
			throw new SpecXmlException(String.format("Expected a single element named \"%s\", got %d", sectionName, results.size()));
		}
		return results.get(0);
	}
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			jcas.setDocumentLanguage("EN");
			Annotation annoFullDocument = new Annotation(jcas, 0, jcas.getDocumentText().length());
			JCas tokenView = jcas.createView(TOKEN_VIEW);
			JCas sentenceView = jcas.createView(SENTENCE_VIEW);
			
			////////
			// 5.3.2014 : New approach! proprietary parsing for spec xml!
			AnnotationFS argumentsSection = getSingleSection(annoFullDocument, "arguments");
			List<AnnotationFS> arguments = getSectionList(jcas, argumentsSection, "argument");
			for (AnnotationFS argumentAnno : arguments) {
				Argument argument = new Argument(tokenView, argumentAnno.getBegin(), argumentAnno.getEnd());
				
				ArgumentRole role = (ArgumentRole) addSingleItem(tokenView, argumentAnno, "role", ArgumentRole.class);
				argument.setRole(role);
				
				List<AnnotationFS> types = addItemsInSingleSection(tokenView, argumentAnno, "types", "type", ArgumentType.class);
				argument.setTypes((FSArray) FSCollectionFactory.createFSArray(jcas, types));
				
				List<AnnotationFS> examples = addItemsInSingleSection(tokenView, argumentAnno, "examples", "example", ArgumentExample.class);
				argument.setExamples((FSArray) FSCollectionFactory.createFSArray(jcas, examples));
			}
			
			// TODO - now do the same for predicate and samples!!!
			
			///////

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
				arg.
				Argument argument = new Argument(jCas);
				argument.setRole(arg.getRole());
				argument.setTypes(listToStringArray(jCas, arg.getTypes()));
				argument.addToIndexes();
				
				// Add a specific UIMA type for each role
				//typeSystemDescription.addType(ArgumentSeed.class.getPackage().getName() + ".roles." + arg.getRole(), "", ArgumentSeed.class.getName());
			}

			// Dump update type system to XML
			URL url = UimaUtils.class.getResource(FULL_TYPE_SYSTEM_FILE_PATH);
			FileOutputStream out = new FileOutputStream(new File(url.toURI()));
			typeSystemDescription.toXML(out);
			
			//TODO here add all generic preprocessing (like POS tagging, parsing, etc.)
			///xxx;
			
			for (FeatureMechanism featureMechanism : perceptron.featureMechanisms) {
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
	
	public class SpecXmlException extends Exception {
		private static final long serialVersionUID = 4101507828603147863L;
		public SpecXmlException(String message) {super(message);}
	}
	
	public static final String ANNOTATOR_FILE_PATH = "/desc/SpecAnnotator.xml";
	public static final String BASIC_TYPE_SYSTEM_FILE_PATH = "/desc/OdieInputTypes.xml";
	public static final String FULL_TYPE_SYSTEM_FILE_PATH = "/desc/OdieInputTypesFull.xml";
	public static final String TOKEN_VIEW = "TokenBasedView";
	public static final String SENTENCE_VIEW = "SentenceBasedView";
	public static final String XML_ELEMENT ="<%s[^>]*>(.*?)</%s>";
}
