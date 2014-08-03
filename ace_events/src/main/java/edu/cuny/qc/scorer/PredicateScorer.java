package edu.cuny.qc.scorer;

import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public abstract class PredicateScorer<T extends Annotation> extends SignalMechanismSpecIterator<T> {
	private static final long serialVersionUID = 5020368504922213594L;

	public void prepareCalc(JCas spec, Token textToken, Map<Class<?>, Object> textTriggerTokenMap, ScorerData scorerData) throws SignalMechanismException {
		prepareSpecIteration(spec);
		this.textToken = textToken;
		this.textTriggerTokenMap = textTriggerTokenMap;
		this.scorerData = scorerData;
	}
	
	@Override
	public Boolean calcScore(T spec, ScorerData scorerData) throws SignalMechanismException {
		return calcBooleanPredicateScore(textToken, textTriggerTokenMap, spec, scorerData);
	}

	public abstract Boolean calcBooleanPredicateScore(Token text, Map<Class<?>, Object> textTriggerTokenMap, T spec, ScorerData scorerData) throws SignalMechanismException;
	protected abstract void prepareSpecIteration(JCas spec) throws SignalMechanismException;
	
	protected transient Token textToken;
	protected transient Map<Class<?>, Object> textTriggerTokenMap;

}
