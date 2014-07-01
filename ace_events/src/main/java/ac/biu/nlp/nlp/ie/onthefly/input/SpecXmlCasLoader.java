package ac.biu.nlp.nlp.ie.onthefly.input;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.FSCollectionFactory;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentRole;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentType;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateName;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample;

public class SpecXmlCasLoader {

	public SpecXmlCasLoader() {	}
	
	private List<AnnotationFS> getElementList(JCas jcas, AnnotationFS container, String elementName, Class<?> annotationType) throws SpecXmlException {
		Type type = null;
		List<AnnotationFS> results = new ArrayList<AnnotationFS>();
		if (annotationType != null) {
			type = JCasUtil.getAnnotationType(jcas, annotationType);
		}
		
		Set<String> seenElements = new HashSet<String>();
		String pattern = String.format(XML_ELEMENT, elementName, elementName);
		Matcher matcher = Pattern.compile(pattern).matcher(container.getCoveredText());
		while (matcher.find()) {
			MatchResult result = matcher.toMatchResult();
			String text = result.group(1);
			
			// Allow only one instance of the same string in the same section
			if (seenElements.contains(text)) {
				throw new SpecXmlException(String.format("Found duplicate element '%s' under '%s'", text, elementName));
			}
			seenElements.add(text);
			
			int begin = container.getBegin() + result.start(1);
			int end   = container.getBegin() + result.end(1);
			
			AnnotationFS anno = null;
			if (annotationType == null) {
				anno = new Annotation(jcas, begin, end);
			}
			else {
				anno = jcas.getCas().createAnnotation(type, begin, end);
				jcas.addFsToIndexes(anno);
			}
			
			// Debug
//			String c = container.getCoveredText();
//			System.err.printf("\n*Container(%s)[%d:%d], len=%d: '%s' {}\n\n", container.getType().getShortName(), container.getBegin(), container.getEnd(), c.length(), c);
//			
//			System.err.printf("\nAnno(%s)[%d:%d]", anno.getType().getShortName(), anno.getBegin(), anno.getEnd());
//			JCas main;
//			try {
//				main = jcas.getView("_InitialView");
//			} catch (CASException e) {
//				throw new SpecXmlException(e);
//			}
//			String doct = main.getDocumentText();
//			System.err.printf(" {%s}", doct.substring(begin, end));
//			String t = anno.getCoveredText();
//			System.err.printf(", t=%s: ", t);
//			System.err.printf(", len=%d \n\n", t.length());
			
			results.add(anno);
		}
		return results;
	}
	
	private List<AnnotationFS> getSectionList(JCas jcas, AnnotationFS container, String elementName) throws SpecXmlException {
		return getElementList(jcas, container, elementName, null);
	}

	private List<AnnotationFS> addItems(JCas view, AnnotationFS container, String itemName, Class<?> annotationType) throws SpecXmlException {
		if (annotationType == null) {
			throw new SpecXmlException("When adding items, a specific annotation type must be given");
		}
		return getElementList(view, container, itemName, annotationType);
	}
	
	private List<AnnotationFS> addItemsInSingleSection(JCas view, AnnotationFS container, String sectionName, String itemName, Class<?> annotationType) throws SpecXmlException, CASException {
		AnnotationFS section = getSingleSection(container, sectionName);
		return addItems(view, section, itemName, annotationType);
	}
	
	private AnnotationFS addSingleItem(JCas view, AnnotationFS container, String itemName, Class<?> annotationType) throws SpecXmlException {
		List<AnnotationFS> results = addItems(view, container, itemName, annotationType);
		if (results.size() != 1) {
			throw new SpecXmlException(String.format("Expected a single element named \"%s\", got %d", itemName, results.size()));
		}
		return results.get(0);
	}

	private AnnotationFS getSingleSection(AnnotationFS container, String sectionName) throws SpecXmlException, CASException {
		List<AnnotationFS> results = getElementList(container.getCAS().getJCas(), container, sectionName, null);
		if (results.size() != 1) {
			throw new SpecXmlException(String.format("Expected a single element named \"%s\", got %d", sectionName, results.size()));
		}
		return results.get(0);
	}
	
	public void load(JCas jcas, JCas tokenView, JCas sentenceView) throws CASException, SpecXmlException {
		Annotation annoFullDocument = new Annotation(jcas, 0, jcas.getDocumentText().length());

		// Predicate
		AnnotationFS predicateSection = getSingleSection(annoFullDocument, "predicate");
		Predicate predicate = new Predicate(tokenView, predicateSection.getBegin(), predicateSection.getEnd());
		predicate.addToIndexes();
		
		PredicateName name = (PredicateName) addSingleItem(tokenView, predicateSection, "name", PredicateName.class);
		predicate.setName(name);

		List<AnnotationFS> seeds = addItemsInSingleSection(tokenView, predicateSection, "seeds", "seed", PredicateSeed.class);
		predicate.setSeeds((FSArray) FSCollectionFactory.createFSArray(jcas, seeds));
		
		
		// Arguments
		AnnotationFS argumentsSection = getSingleSection(annoFullDocument, "arguments");
		List<AnnotationFS> arguments = getSectionList(jcas, argumentsSection, "argument");
		for (AnnotationFS argumentAnno : arguments) {
			Argument argument = new Argument(tokenView, argumentAnno.getBegin(), argumentAnno.getEnd());
			argument.addToIndexes();
			
			ArgumentRole role = (ArgumentRole) addSingleItem(tokenView, argumentAnno, "role", ArgumentRole.class);
			argument.setRole(role);
			
			List<AnnotationFS> types = addItemsInSingleSection(tokenView, argumentAnno, "types", "type", ArgumentType.class);
			argument.setTypes((FSArray) FSCollectionFactory.createFSArray(jcas, types));
			
			List<AnnotationFS> examples = addItemsInSingleSection(tokenView, argumentAnno, "examples", "example", ArgumentExample.class);
			argument.setExamples((FSArray) FSCollectionFactory.createFSArray(jcas, examples));
			for (AnnotationFS exampleAnno : examples) {
				ArgumentExample example = (ArgumentExample) exampleAnno;
				example.setArgument(argument);
			}
		}
		
		// Usage Samples
		addItemsInSingleSection(sentenceView, annoFullDocument, "usage_samples", "sample", UsageSample.class);

	}

	public static final String XML_ELEMENT ="(?s)<%s[^>]*>(.*?)</%s>";

}
