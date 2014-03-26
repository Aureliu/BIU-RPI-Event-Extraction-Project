package edu.cuny.qc.perceptron.similarity_scorer;

import java.util.LinkedHashMap;
import java.util.List;

import javax.security.auth.callback.TextInputCallback;

import org.apache.uima.jcas.JCas;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;

public abstract class MeasureMechanism {
	
	static {
		System.err.println("??? MeasureMechanism: for argument, now considering only HEAD (not extent), and only FIRST WORD of head (could be more than one word). Need to think of handling MWEs.");
	}
	
	public MeasureMechanism() { }

//	public abstract void preprocessSpec(JCas spec) throws MeasureMechanismException;
//	public abstract void preprocessTextSentence(SentenceInstance textSentence) throws MeasureMechanismException;
	public LinkedHashMap<String, Double> scoreTrigger(JCas spec, SentenceInstance textSentence, int i) throws MeasureMechanismException {
		List<Token> textAnnos = (List<Token>) textSentence.get(InstanceAnnotations.TokenAnnotations);
		Token textTriggerToken = textAnnos.get(i);

		return scoreTriggerToken(spec, textSentence, textTriggerToken);
	}

	public LinkedHashMap<String, Double> scoreArgument(JCas spec, Argument argument, SentenceInstance textSentence, int i, AceMention mention) throws MeasureMechanismException {
		int argHeadFirstTokenIndex = mention.getHeadIndices().get(0);
		
		List<Token> textAnnos = (List<Token>) textSentence.get(InstanceAnnotations.TokenAnnotations);
		Token textTriggerToken = textAnnos.get(i);
		Token textArgToken = textAnnos.get(argHeadFirstTokenIndex);
		
		return scoreArgumentFirstHeadToken(spec, argument, textSentence, textTriggerToken, textArgToken);
	}

	public abstract LinkedHashMap<String, Double> scoreTriggerToken(JCas spec, SentenceInstance textSentence, Token textTriggerToken) throws MeasureMechanismException;
	public abstract LinkedHashMap<String, Double> scoreArgumentFirstHeadToken(JCas spec, Argument argument, SentenceInstance textSentence, Token textTriggerToken, Token textArgToken) throws MeasureMechanismException;
	
	/**
	 * Optional operation
	 */
	public void close() { }
}
