package edu.cuny.qc.perceptron.similarity_scorer;

import java.util.Map;

import org.apache.uima.jcas.JCas;

import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;

public abstract class FeatureMechanism {

	public abstract void preprocessSpec(JCas spec);
	public abstract void preprocessTextSentence(SentenceInstance textSentence);
	public abstract Map<String, Double> scoreTrigger(JCas spec, SentenceInstance textSentence, SentenceAssignment assn, int i);
	public abstract Map<String, Double> scoreArgument(JCas spec, SentenceInstance textSentence, SentenceAssignment assn, int i, int k);
}
