package edu.cuny.qc.perceptron.similarity_scorer;

import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.uima.jcas.JCas;

import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;

public class WordNetFeatureMechanism extends FeatureMechanism {

	@Override
	public void preprocessSpec(JCas spec) {
		throw new NotImplementedException();
	}

	@Override
	public void preprocessTextSentence(SentenceInstance textSentence) {
		throw new NotImplementedException();
	}

	@Override
	public Map<String, Double> scoreTrigger(JCas spec,
			SentenceInstance textSentence, SentenceAssignment assn, int i) {
		throw new NotImplementedException();
	}

	@Override
	public Map<String, Double> scoreArgument(JCas spec,	SentenceInstance textSentence, SentenceAssignment assn, int i, int k) {
		throw new NotImplementedException();
	}

}
