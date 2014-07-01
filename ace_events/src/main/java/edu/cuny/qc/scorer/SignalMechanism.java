package edu.cuny.qc.scorer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.jcas.JCas;

import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed;

import com.google.common.collect.ArrayListMultimap;

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
	
	public SignalMechanism() throws SignalMechanismException {
		scorers = new LinkedHashMap<SignalType, List<ScorerData>>(2);
		scorers.put(SignalType.TRIGGER,   new ArrayList<ScorerData>());
		scorers.put(SignalType.ARGUMENT,  new ArrayList<ScorerData>());
		
		try {
			init();
			addScorers();
		}
		catch (Exception e) {
			throw new SignalMechanismException(e);
		}
	}
	
	public void addTrigger(ScorerData data) {
		scorers.get(SignalType.TRIGGER).add(data);
	}

	public void addTriggers(Collection<ScorerData> datas) {
		for (ScorerData data : datas) {
			addTrigger(data);
		}
	}

	public void addArgument(ScorerData data) {
		scorers.get(SignalType.ARGUMENT).add(data);
	}
	
	// These are only entry points, any SignalMechanism can choose to implement any of them
	public void init() throws Exception {}
	public void logPreSentence() {}
	public void logPreDocument() {}
	public void logPreDocumentBunch() {}

	public void scoreTrigger(Map<ScorerData, SignalInstance> existingSignals, Set<ScorerData> allTriggerScorers, JCas spec, SentenceInstance textSentence, int i, boolean debug) throws SignalMechanismException {
		Token textTriggerToken = textSentence.getTokenAnnotation(i);
		Map<Class<?>, Object> textTriggerTokenMap = ((List<Map<Class<?>, Object>>) textSentence.get(InstanceAnnotations.Token_FEATURE_MAPs)).get(i);

		for (ScorerData data : scorers.get(SignalType.TRIGGER)) {
			SignalInstance signal = null;
//			if (!existingSignals.containsKey(data) || (debug && existingSignals.get(data).history == null)) {
//				data.scorer.init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken, textTriggerTokenMap);
//			}
			if (!existingSignals.containsKey(data)) {
				data.scorer.init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken, textTriggerTokenMap, data);
				BigDecimal score = data.aggregator.aggregate(data.scorer);
				signal = new SignalInstance(data, SignalType.TRIGGER, score);
				existingSignals.put(data, signal);
				allTriggerScorers.add(data);
				textSentence.markSignalUpdate();
			}
			if (debug) {
				if (signal == null) {
					signal = existingSignals.get(data);
				}
				if (signal.history == null) {
//					signal.initHistory();
					data.scorer.debug = true;
					// need to init again because the inner iterator is already exhausted, need to get a new one
					data.scorer.init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken, textTriggerTokenMap, data);
					data.scorer.history = ArrayListMultimap.create();
					debugAggregator.aggregate(data.scorer);
					data.scorer.debug = false;
					signal.history = data.scorer.history;
					textSentence.markSignalUpdate();
				}
			}
		}
	}

//	public LinkedHashMap<ScorerData, Multimap<String, String>> getTriggerDetails(JCas spec, SentenceInstance textSentence, int i) throws SignalMechanismException {
//		Token textTriggerToken = textSentence.getTokenAnnotation(i);
//		Map<Class<?>, Object> textTriggerTokenMap = ((List<Map<Class<?>, Object>>) textSentence.get(InstanceAnnotations.Token_FEATURE_MAPs)).get(i);
//		
//		return getTriggerTokenDetails(spec, textSentence, textTriggerToken, textTriggerTokenMap);
//	}

	public LinkedHashMap<ScorerData, BigDecimal> scoreArgument(Map<ScorerData, SignalInstance> existingSignals, JCas spec, Argument argument, SentenceInstance textSentence, int i, AceMention mention) throws SignalMechanismException {
		int argHeadFirstTokenIndex = mention.getHeadIndices().get(0);
		
		Token textTriggerToken = textSentence.getTokenAnnotation(i);
		Token textArgToken = textSentence.getTokenAnnotation(argHeadFirstTokenIndex);
		
		List<Map<Class<?>, Object>> textSentenceMaps = (List<Map<Class<?>, Object>>) textSentence.get(InstanceAnnotations.Token_FEATURE_MAPs);
		Map<Class<?>, Object> textTriggerTokenMap = textSentenceMaps.get(i);
		Map<Class<?>, Object> textArgTokenMap = textSentenceMaps.get(argHeadFirstTokenIndex);
		
		return scoreArgumentFirstHeadToken(existingSignals, spec, argument, textSentence, textTriggerToken, textTriggerTokenMap, textArgToken, textArgTokenMap);
	}
	
	// not doing right now the arg equivalent of getTriggerDetails
	
	
//	public LinkedHashMap<ScorerData, BigDecimal> scoreTriggerToken(Map<ScorerData, SignalInstance> existingSignalNames, JCas spec, SentenceInstance textSentence, Token textTriggerToken, Map<Class<?>, Object> textTriggerTokenMap, boolean debug) throws SignalMechanismException {
//		LinkedHashMap<ScorerData, BigDecimal> ret = new LinkedHashMap<ScorerData, BigDecimal>();
//
//		for (ScorerData data : scorers.get(SignalType.TRIGGER)) {
//			if (existingSignalNames == null || !existingSignalNames.containsKey(data)) {
//				data.scorer.init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken);
//				ret.put(data, data.aggregator.aggregate(data.scorer));
//			}
//		}
//		return ret;
//	}
	
//	public LinkedHashMap<ScorerData, Multimap<String, String>> getTriggerTokenDetails(JCas spec, SentenceInstance textSentence, Token textTriggerToken, Map<Class<?>, Object> textTriggerTokenMap) throws SignalMechanismException {
//		LinkedHashMap<ScorerData, Multimap<String, String>> ret = new LinkedHashMap<ScorerData, Multimap<String, String>>();
//
//		for (ScorerData data : scorers.get(SignalType.TRIGGER)) {
//			data.scorer.debug = true;
//			data.scorer.init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken);
//			Iterators.frequency(data.scorer, null); // consume all of it - it will build the history. THIS SHOULD ACTUALLY BE AN AGGREGATOR.
//			ret.put(data, ArrayListMultimap.create(data.scorer.history)); //store a copy, we are re-using history
//			data.scorer.debug = false;
//		}
//		return ret;
//	}
	
	public LinkedHashMap<ScorerData, BigDecimal> scoreArgumentFirstHeadToken(Map<ScorerData, SignalInstance> existingSignalNames, JCas spec, Argument argument, SentenceInstance textSentence, Token textTriggerToken, Map<Class<?>, Object> textTriggerTokenMap, Token textArgToken, Map<Class<?>, Object> textArgTokenMap) throws SignalMechanismException {
		LinkedHashMap<ScorerData, BigDecimal> ret = new LinkedHashMap<ScorerData, BigDecimal>();

		for (ScorerData data : scorers.get(SignalType.ARGUMENT)) {
			if (existingSignalNames == null || !existingSignalNames.containsKey(data)) {
				throw new RuntimeException("you should fix this call to init!!! add the maps thingy!");
				//data.scorer.init(spec, null, argument, ArgumentExample.class, textArgToken);
				//ret.put(data, data.aggregator.aggregate(data.scorer));
			}
		}
		return ret;
		
	}

	public abstract void addScorers() throws Exception;

	/**
	 * Optional operation
	 */
	public void close() { }
	
	public Map<SignalType, List<ScorerData>> scorers;
	private Aggregator debugAggregator = Aggregator.ScanAll.inst;
}
