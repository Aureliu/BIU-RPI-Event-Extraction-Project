package edu.cuny.qc.scorer;

import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public abstract class PredicateScorer<T extends Annotation> extends SignalMechanismSpecIterator<T> {
	private static final long serialVersionUID = 5020368504922213594L;
	
	static {
		System.err.println("??? PredicateScorer: should add here hard-codedly all the tests of text tokens that exist in BeamSearch.expandTriggers (like if the POS is right), and just return false.");
	}

	public void prepareCalc(JCas spec, Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String docAllText, ScorerData scorerData) throws SignalMechanismException {
		this.scorerData = scorerData;
		this.docAllText = docAllText;
		this.textToken = textToken;
		this.textTriggerTokenMap = textTriggerTokenMap;
		prepareSpecIteration(spec);
	}
	
	@Override
	public Boolean calcScore(T spec) throws SignalMechanismException {
		return calcBooleanPredicateScore(spec);
	}

	public abstract Boolean calcBooleanPredicateScore(T spec) throws SignalMechanismException;
	protected abstract void prepareSpecIteration(JCas spec) throws SignalMechanismException;
	
	//protected transient ScorerData scorerData;
	protected transient Token textToken;
	protected transient Map<Class<?>, Object> textTriggerTokenMap;

}
