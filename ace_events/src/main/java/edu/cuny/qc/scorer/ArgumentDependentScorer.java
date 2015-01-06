package edu.cuny.qc.scorer;

import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceMention;

public abstract class ArgumentDependentScorer<T extends Annotation> extends SignalMechanismSpecIterator<T> {
	private static final long serialVersionUID = 4906146197584372502L;
	
	public void prepareCalc(JCas spec, Token textTriggerToken, Map<Class<?>, Object> textTriggerTokenMap, Argument argument, AceMention aceMention, JCas docJCas, String docAllText, ScorerData scorerData) throws SignalMechanismException {
		this.scorerData = scorerData;
		this.docAllText = docAllText;
		this.textTriggerToken = textTriggerToken;
		this.textTriggerTokenMap = textTriggerTokenMap;
		this.argument = argument;
		this.aceMention = aceMention;
		prepareSpecIteration(spec);
	}
	
	@Override
	public Boolean calcScore(T spec) throws SignalMechanismException {
		return calcBooleanArgumentDependentScore(spec);
	}

	public abstract Boolean calcBooleanArgumentDependentScore(T spec) throws SignalMechanismException;
	protected abstract void prepareSpecIteration(JCas spec) throws SignalMechanismException;
	
	//protected transient ScorerData scorerData;
	protected transient Token textTriggerToken;
	protected transient Map<Class<?>, Object> textTriggerTokenMap;
	protected transient Argument argument;
	protected transient AceMention aceMention;
}