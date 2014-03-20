package edu.cuny.qc.perceptron.similarity_scorer;

import java.util.LinkedHashMap;

import org.apache.uima.jcas.JCas;

import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.types.SentenceInstance;

public abstract class FeatureMechanism {
	
	public FeatureMechanism() { }

//	public abstract void preprocessSpec(JCas spec) throws FeatureMechanismException;
//	public abstract void preprocessTextSentence(SentenceInstance textSentence) throws FeatureMechanismException;
	public abstract LinkedHashMap<String, Double> scoreTrigger(JCas spec, SentenceInstance textSentence, int i) throws FeatureMechanismException;
	public abstract LinkedHashMap<String, Double> scoreArgument(JCas spec, SentenceInstance textSentence, int i, AceMention mention) throws FeatureMechanismException;
	
	public void close() { }
}
