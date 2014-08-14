package edu.cuny.qc.scorer;

import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceMention;

public abstract class ArgumentScorer<T extends Annotation> extends SignalMechanismSpecIterator<T> {
	private static final long serialVersionUID = 4906146197584372502L;
	
	static {
		System.err.println("??? ArgumentScorer: should add here hard-codedly all the tests of text tokens that exist in BeamSearch.expandArgs (like if the type is right for the role), and just return false.");
	}
	
	public void prepareCalc(JCas spec, Token textTriggerToken, Map<Class<?>, Object> textTriggerTokenMap, Argument argument, AceMention aceMention, String docAllText, ScorerData scorerData) throws SignalMechanismException {
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
		return calcBooleanArgumentScore(spec);
	}

	public abstract Boolean calcBooleanArgumentScore(T spec) throws SignalMechanismException;
	protected abstract void prepareSpecIteration(JCas spec) throws SignalMechanismException;
	
	//protected transient ScorerData scorerData;
	protected transient Token textTriggerToken;
	protected transient Map<Class<?>, Object> textTriggerTokenMap;
	protected transient Argument argument;
	protected transient AceMention aceMention;
}