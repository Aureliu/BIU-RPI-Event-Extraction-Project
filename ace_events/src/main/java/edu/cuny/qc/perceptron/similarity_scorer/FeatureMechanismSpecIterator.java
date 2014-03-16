package edu.cuny.qc.perceptron.similarity_scorer;

import java.util.Iterator;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

public abstract class FeatureMechanismSpecIterator implements Iterator<Double> {

	public FeatureMechanismSpecIterator init(JCas spec, String viewName, Class<? extends Annotation> type, Annotation textAnno) throws FeatureMechanismException {
		try {
			JCas view = spec.getView(viewName);
			specIterator = JCasUtil.iterator(view, type);
			this.textAnno = textAnno;
	
			// A little Java trick, to allow doing several things in a one-liner:
			// instantiate this object, call init(), and use it inside an Aggregator method
			return this;
		} catch (CASException e) {
			throw new FeatureMechanismException(e);
		}
	}
	
	@Override
	public Double next() {
		try {
			Annotation specElement = specIterator.next();
			return calcScore(textAnno, specElement);
		} catch (FeatureMechanismException e) {
			throw new FeatureMechanismRuntimeException(e);
		}
	}
	
	@Override
	public boolean hasNext() {
		return specIterator.hasNext();
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException(String.format("%s does not support removing spec items", this.getClass().getSimpleName()));
	}
	
	public abstract Double calcScore(Annotation text, Annotation spec) throws FeatureMechanismException;
	
	protected Iterator<? extends Annotation> specIterator;
	protected Annotation textAnno;
}
