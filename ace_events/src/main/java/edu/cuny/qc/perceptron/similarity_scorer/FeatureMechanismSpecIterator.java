package edu.cuny.qc.perceptron.similarity_scorer;

import java.util.Iterator;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

public abstract class FeatureMechanismSpecIterator implements Iterator<Double> {

	public FeatureMechanismSpecIterator(JCas spec, String viewName, Class<? extends Annotation> type, Annotation textAnno) throws CASException {
		JCas view = spec.getView(viewName);
		specIterator = JCasUtil.iterator(view, type);
		this.textAnno = textAnno;
	}
	
	@Override
	public Double next() {
		Annotation specElement = specIterator.next();
		return calcScore(textAnno, specElement);
	}
	
	@Override
	public boolean hasNext() {
		return specIterator.hasNext();
	}
	
	public abstract Double calcScore(Annotation text, Annotation spec);
	
	private Iterator<? extends Annotation> specIterator;
	private Annotation textAnno;
}
