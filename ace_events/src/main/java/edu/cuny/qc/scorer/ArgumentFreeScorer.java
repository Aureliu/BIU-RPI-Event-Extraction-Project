package edu.cuny.qc.scorer;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import edu.cuny.qc.ace.acetypes.AceMention;

public abstract class ArgumentFreeScorer<T extends Annotation> extends SignalMechanismSpecIterator<T> {
	private static final long serialVersionUID = 4906146197584372502L;
	
	static {
		System.err.println("??? ArgumentFreeScorer: should add here hard-codedly all the tests of text tokens that exist in BeamSearch.expandArgs (like if the type is right for the role), and just return false.");
	}
	
	public void prepareCalc(JCas spec, Argument argument, AceMention aceMention, String docAllText, JCas docJCas, ScorerData scorerData) throws SignalMechanismException {
		this.scorerData = scorerData;
		this.docAllText = docAllText;
		this.docJCas = docJCas;
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
	protected transient JCas docJCas;
	protected transient Argument argument;
	protected transient AceMention aceMention;
}