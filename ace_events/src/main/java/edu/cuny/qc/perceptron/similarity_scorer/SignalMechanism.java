package edu.cuny.qc.perceptron.similarity_scorer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;

import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.perceptron.types.SignalInstance;
import edu.cuny.qc.perceptron.types.SignalType;

public abstract class SignalMechanism {
	
	static {
		System.err.println("??? SignalMechanism: for argument, now considering only HEAD (not extent), and only FIRST WORD of head (could be more than one word). Need to think of handling MWEs.");
	}
	
	public SignalMechanism() {
		scorers = new LinkedHashMap<SignalType, List<ScorerData>>(2);
		scorers.put(SignalType.TRIGGER,   new ArrayList<ScorerData>());
		scorers.put(SignalType.ARGUMENT,  new ArrayList<ScorerData>());
		
		addScorers();
	}
	
	public void addTrigger(ScorerData data) {
		scorers.get(SignalType.TRIGGER).add(data);
	}

	public void addArgument(ScorerData data) {
		scorers.get(SignalType.ARGUMENT).add(data);
	}

	public LinkedHashMap<ScorerData, BigDecimal> scoreTrigger(Map<String, SignalInstance> existingSignals, JCas spec, SentenceInstance textSentence, int i) throws SignalMechanismException {
		Token textTriggerToken = textSentence.getTokenAnnotation(i);
		Map<Class<?>, Object> textTriggerTokenMap = ((List<Map<Class<?>, Object>>) textSentence.get(InstanceAnnotations.Token_FEATURE_MAPs)).get(i);
		
		return scoreTriggerToken(existingSignals, spec, textSentence, textTriggerToken, textTriggerTokenMap);
	}

	public LinkedHashMap<ScorerData, Multimap<String, String>> getTriggerDetails(JCas spec, SentenceInstance textSentence, int i) throws SignalMechanismException {
		Token textTriggerToken = textSentence.getTokenAnnotation(i);
		Map<Class<?>, Object> textTriggerTokenMap = ((List<Map<Class<?>, Object>>) textSentence.get(InstanceAnnotations.Token_FEATURE_MAPs)).get(i);
		
		return getTriggerTokenDetails(spec, textSentence, textTriggerToken, textTriggerTokenMap);
	}

	public LinkedHashMap<ScorerData, BigDecimal> scoreArgument(Map<String, SignalInstance> existingSignals, JCas spec, Argument argument, SentenceInstance textSentence, int i, AceMention mention) throws SignalMechanismException {
		int argHeadFirstTokenIndex = mention.getHeadIndices().get(0);
		
		Token textTriggerToken = textSentence.getTokenAnnotation(i);
		Token textArgToken = textSentence.getTokenAnnotation(argHeadFirstTokenIndex);
		
		List<Map<Class<?>, Object>> textSentenceMaps = (List<Map<Class<?>, Object>>) textSentence.get(InstanceAnnotations.Token_FEATURE_MAPs);
		Map<Class<?>, Object> textTriggerTokenMap = textSentenceMaps.get(i);
		Map<Class<?>, Object> textArgTokenMap = textSentenceMaps.get(argHeadFirstTokenIndex);
		
		return scoreArgumentFirstHeadToken(existingSignals, spec, argument, textSentence, textTriggerToken, textTriggerTokenMap, textArgToken, textArgTokenMap);
	}
	
	// not doing right now the arg equivalent of getTriggerDetails
	
	
	public LinkedHashMap<ScorerData, BigDecimal> scoreTriggerToken(Map<String, SignalInstance> existingSignalNames, JCas spec, SentenceInstance textSentence, Token textTriggerToken, Map<Class<?>, Object> textTriggerTokenMap) throws SignalMechanismException {
		LinkedHashMap<ScorerData, BigDecimal> ret = new LinkedHashMap<ScorerData, BigDecimal>();

		for (ScorerData data : scorers.get(SignalType.TRIGGER)) {
			if (existingSignalNames == null || !existingSignalNames.containsKey(data.fullName)) {
				data.scorer.init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken);
				ret.put(data, data.aggregator.aggregate(data.scorer));
			}
		}
		return ret;
	}
	
	public LinkedHashMap<ScorerData, Multimap<String, String>> getTriggerTokenDetails(JCas spec, SentenceInstance textSentence, Token textTriggerToken, Map<Class<?>, Object> textTriggerTokenMap) throws SignalMechanismException {
		LinkedHashMap<ScorerData, Multimap<String, String>> ret = new LinkedHashMap<ScorerData, Multimap<String, String>>();

		for (ScorerData data : scorers.get(SignalType.TRIGGER)) {
			data.scorer.debug = true;
			data.scorer.init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken);
			Iterators.frequency(data.scorer, null); // consume all of it - it will build the history. THIS SHOULD ACTUALLY BE AN AGGREGATOR.
			ret.put(data, ArrayListMultimap.create(data.scorer.history)); //store a copy, we are re-using history
			data.scorer.debug = false;
		}
		return ret;
	}
	
	public LinkedHashMap<ScorerData, BigDecimal> scoreArgumentFirstHeadToken(Map<String, SignalInstance> existingSignalNames, JCas spec, Argument argument, SentenceInstance textSentence, Token textTriggerToken, Map<Class<?>, Object> textTriggerTokenMap, Token textArgToken, Map<Class<?>, Object> textArgTokenMap) throws SignalMechanismException {
		LinkedHashMap<ScorerData, BigDecimal> ret = new LinkedHashMap<ScorerData, BigDecimal>();

		for (ScorerData data : scorers.get(SignalType.ARGUMENT)) {
			if (existingSignalNames == null || !existingSignalNames.containsKey(data.fullName)) {
				data.scorer.init(spec, null, argument, ArgumentExample.class, textArgToken);
				ret.put(data, data.aggregator.aggregate(data.scorer));
			}
		}
		return ret;
		
	}

	public abstract void addScorers();

	/**
	 * Optional operation
	 */
	public void close() { }
	
	private Map<SignalType, List<ScorerData>> scorers;
}
