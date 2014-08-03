package edu.cuny.qc.scorer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.Multimap;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import edu.cuny.qc.perceptron.types.SignalInstance;

public abstract class SignalMechanismSpecIterator<T extends Annotation> implements Iterator<BigDecimal>, Serializable {
	
	private static final long serialVersionUID = -7666054959411686538L;
//	public void prepareCalc(JCas spec, String viewName, Token textAnno, Map<Class<?>, Object> textTriggerTokenMap, ScorerData scorerData) throws SignalMechanismException {
//		prepareSpecIteration(spec);
////		this.textAnno = textAnno;
////		this.textTriggerTokenMap = textTriggerTokenMap;
//		this.scorerData = scorerData;
//	}
	
	@Override
	public BigDecimal next() {
		try {
			T specElement = specIterator.next();
			Boolean result = calcScore(specElement, scorerData);
			return SignalInstance.toDouble(result);
		} catch (SignalMechanismException e) {
			throw new SignalMechanismRuntimeException(e);
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
	
	public abstract Boolean calcScore(T spec, ScorerData scorerData) throws SignalMechanismException;
//	public abstract void prepareSpecIteration(JCas spec) throws SignalMechanismException;
	
	protected transient Iterator<T> specIterator;
//	protected transient Annotation textAnno;
//	protected transient Map<Class<?>, Object> textTriggerTokenMap;
	protected ScorerData scorerData;
	public Multimap<String, String> history;  // {specTok1 = [textTok1, textTok2], specTok2=[textTok3, textTok1, textTok3]}
	public boolean debug = false;
}
