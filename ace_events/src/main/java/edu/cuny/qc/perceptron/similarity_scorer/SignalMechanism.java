package edu.cuny.qc.perceptron.similarity_scorer;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.perceptron.types.SignalInstance;

public abstract class SignalMechanism {
	
	static {
		System.err.println("??? SignalMechanism: for argument, now considering only HEAD (not extent), and only FIRST WORD of head (could be more than one word). Need to think of handling MWEs.");
	}
	
	public SignalMechanism() { }

//	public abstract void preprocessSpec(JCas spec) throws SignalMechanismException;
//	public abstract void preprocessTextSentence(SentenceInstance textSentence) throws SignalMechanismException;
	public LinkedHashMap<String, BigDecimal> scoreTrigger(Map<String, SignalInstance> existingSignals, JCas spec, SentenceInstance textSentence, int i) throws SignalMechanismException {
		Token textTriggerToken = textSentence.getTokenAnnotation(i);
		Map<Class<?>, Object> textTriggerTokenMap = ((List<Map<Class<?>, Object>>) textSentence.get(InstanceAnnotations.Token_FEATURE_MAPs)).get(i);
		
		return scoreTriggerToken(existingSignals, spec, textSentence, textTriggerToken, textTriggerTokenMap);
	}

	public LinkedHashMap<String, BigDecimal> scoreArgument(Map<String, SignalInstance> existingSignals, JCas spec, Argument argument, SentenceInstance textSentence, int i, AceMention mention) throws SignalMechanismException {
		int argHeadFirstTokenIndex = mention.getHeadIndices().get(0);
		
		Token textTriggerToken = textSentence.getTokenAnnotation(i);
		Token textArgToken = textSentence.getTokenAnnotation(argHeadFirstTokenIndex);
		
		List<Map<Class<?>, Object>> textSentenceMaps = (List<Map<Class<?>, Object>>) textSentence.get(InstanceAnnotations.Token_FEATURE_MAPs);
		Map<Class<?>, Object> textTriggerTokenMap = textSentenceMaps.get(i);
		Map<Class<?>, Object> textArgTokenMap = textSentenceMaps.get(argHeadFirstTokenIndex);
		
		return scoreArgumentFirstHeadToken(existingSignals, spec, argument, textSentence, textTriggerToken, textTriggerTokenMap, textArgToken, textArgTokenMap);
	}
	
	public abstract LinkedHashMap<String, BigDecimal> scoreTriggerToken(Map<String, SignalInstance> existingSignalNames, JCas spec, SentenceInstance textSentence, Token textTriggerToken, Map<Class<?>, Object> textTriggerTokenMap) throws SignalMechanismException;
	public abstract LinkedHashMap<String, BigDecimal> scoreArgumentFirstHeadToken(Map<String, SignalInstance> existingSignalNames, JCas spec, Argument argument, SentenceInstance textSentence, Token textTriggerToken, Map<Class<?>, Object> textTriggerTokenMap, Token textArgToken, Map<Class<?>, Object> textArgTokenMap) throws SignalMechanismException;
	
	/**
	 * Optional operation
	 */
	public void close() { }
}
