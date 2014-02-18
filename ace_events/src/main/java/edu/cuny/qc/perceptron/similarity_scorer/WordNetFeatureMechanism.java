package edu.cuny.qc.perceptron.similarity_scorer;

import java.util.LinkedHashMap;

import org.apache.commons.lang.NotImplementedException;
import org.apache.uima.jcas.JCas;

import edu.cuny.qc.ace.acetypes.AceMention;
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
	public LinkedHashMap<String, Double> scoreTrigger(JCas spec, SentenceInstance textSentence, int i) {
		throw new NotImplementedException();
	}

	@Override
	public LinkedHashMap<String, Double> scoreArgument(JCas spec,	SentenceInstance textSentence, int i, AceMention mention) {
		throw new NotImplementedException();
	}

}
