package edu.cuny.qc.perceptron.similarity_scorer;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import edu.cuny.qc.perceptron.types.SignalInstance;

public abstract class SignalMechanismSpecIterator implements Iterator<BigDecimal> {
	
	public SignalMechanismSpecIterator init(JCas spec, String viewName, AnnotationFS covering, Class<? extends Annotation> type, Annotation textAnno) throws SignalMechanismException {
		try {
			if (covering != null) {
				specIterator = JCasUtil.iterator(covering, type, true, true);
			}
			else {
				JCas view = spec.getView(viewName);
				specIterator = JCasUtil.iterator(view, type);
			}
			this.textAnno = textAnno;
	
			if (debug) {
				history = ArrayListMultimap.create();
			}
			
			// A little Java trick, to allow doing several things in a one-liner:
			// instantiate this object, call init(), and use it inside an Aggregator method
			return this;
		} catch (CASException e) {
			throw new SignalMechanismException(e);
		}
	}
	
	@Override
	public BigDecimal next() {
		try {
			specElement = specIterator.next();
			BigDecimal result = calcScore(textAnno, specElement);
			addToHistory(result);
			return result;
		} catch (SignalMechanismException e) {
			throw new SignalMechanismRuntimeException(e);
		}
	}
	
	public void addToHistory(BigDecimal result) {
		if (debug && SignalInstance.isPositive.apply(result)) {
			history.put(specElement.getCoveredText(), textAnno.getCoveredText());
		}
	}
	
	public String getTypeName() {
		return getClass().getSimpleName();
	}

	@Override
	public boolean hasNext() {
		return specIterator.hasNext();
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException(String.format("%s does not support removing spec items", this.getClass().getSimpleName()));
	}
	
	public abstract BigDecimal calcScore(Annotation text, Annotation spec) throws SignalMechanismException;
	
	protected Iterator<? extends Annotation> specIterator;
	protected Annotation textAnno;
	protected Annotation specElement;
	public Multimap<String, String> history;  // {specTok1 = [textTok1, textTok2], specTok2=[textTok3, textTok1, textTok3]}
	public static boolean debug = false;
}
