package edu.cuny.qc.scorer;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.Multimap;

import edu.cuny.qc.perceptron.types.SignalInstance;

public abstract class SignalMechanismSpecIterator implements Iterator<BigDecimal> {
	
	public SignalMechanismSpecIterator init(JCas spec, String viewName, AnnotationFS covering, Class<? extends Annotation> type, Annotation textAnno, Map<Class<?>, Object> textTriggerTokenMap, ScorerData scorerData) throws SignalMechanismException {
		try {
			if (covering != null) {
				specIterator = JCasUtil.iterator(covering, type, true, true);
			}
			else {
				JCas view = spec.getView(viewName);
				specIterator = JCasUtil.iterator(view, type);
			}
			this.textAnno = textAnno;
			this.textTriggerTokenMap = textTriggerTokenMap;
			this.scorerData = scorerData;
	
//			if (debug) {
//				history = ArrayListMultimap.create();
//			}
			
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
			BigDecimal result = calcScore(textAnno, textTriggerTokenMap, specElement, scorerData);
			return result;
		} catch (SignalMechanismException e) {
			throw new SignalMechanismRuntimeException(e);
		}
	}
		
//	public void addToHistory(BigDecimal result) throws SignalMechanismException {
//		history.put(specElement.getCoveredText().intern(), textAnno.getCoveredText().intern());
//	}
	
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
	
	public abstract BigDecimal calcScore(Annotation text, Map<Class<?>, Object> textTriggerTokenMap, Annotation spec, ScorerData scorerData) throws SignalMechanismException;
	
	protected Iterator<? extends Annotation> specIterator;
	protected Annotation textAnno;
	protected Map<Class<?>, Object> textTriggerTokenMap;
	protected Annotation specElement;
	protected ScorerData scorerData;
	public Multimap<String, String> history;  // {specTok1 = [textTok1, textTok2], specTok2=[textTok3, textTok1, textTok3]}
	public boolean debug = false;
}
