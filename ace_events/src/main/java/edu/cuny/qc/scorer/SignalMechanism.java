package edu.cuny.qc.scorer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.perceptron.types.SignalInstance;
import edu.cuny.qc.perceptron.types.SignalType;
import edu.cuny.qc.util.Utils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public abstract class SignalMechanism {
	
	static {
		System.err.println("??? SignalMechanism: for argument, now considering only HEAD (not extent), and only FIRST WORD of head (could be more than one word). Need to think of handling MWEs.");
		System.err.println("??? SignalMechanism: HACK: just put zero in the signal if we get an exception. What SHOULD happen is probably something like not adding the signal (but then I should make sure that down the pipeline they ignore this as well).");
	}
	
	public SignalMechanism(Controller controller) throws SignalMechanismException {
		this.controller = controller;
		scorers = LinkedHashMultimap.create(2, 5);
//		scorers.put(SignalType.TRIGGER,   new ArrayList<ScorerData>());
//		scorers.put(SignalType.ARGUMENT,  new ArrayList<ScorerData>());
		
		try {
			init();
			addScorers();
		}
		catch (Exception e) {
			throw new SignalMechanismException(e);
		}
	}
	
	public void addTrigger(ScorerData data) {
		scorers.put(SignalType.TRIGGER, data);
	}

	public void addTriggers(Collection<ScorerData> datas) {
		for (ScorerData data : datas) {
			addTrigger(data);
		}
	}

	public void addArgumentDependent(ScorerData data) {
		scorers.put(SignalType.ARGUMENT_DEPENDENT, data);
	}
	
	public void addArgumentFree(ScorerData data) {
		scorers.put(SignalType.ARGUMENT_FREE, data);
	}
	
	// These are only entry points, any SignalMechanism can choose to implement any of them
	public void init() throws Exception {}
	public void entrypointPreSpec(JCas spec) throws SignalMechanismException {}
	public void entrypointPreSentence(SentenceInstance inst) throws SignalMechanismException {
		/// DEBUG
		//System.out.printf("%s %s: PreSentence\n", Utils.detailedLog(), this.getClass().getSimpleName());
		////
	}
	public void entrypointPreDocument(Document doc) throws SignalMechanismException {}
	//public void entrypointPreDocumentBunch() throws SignalMechanismException {}

	public void scoreTrigger(Map<ScorerData, SignalInstance> existingSignals, /*Set<ScorerData> allTriggerScorers,*/ JCas spec, SentenceInstance textSentence, int i, boolean debug) throws SignalMechanismException {
		Token textTriggerToken = textSentence.sent.getTokenAnnotation(i);
		Map<Class<?>, Object> textTriggerTokenMap = ((List<Map<Class<?>, Object>>) textSentence.get(InstanceAnnotations.Token_FEATURE_MAPs)).get(i);
		String docAllText = textSentence.doc.allText;

		//XX somehow here do the same for PIUS scorers
		for (ScorerData data : scorers.get(SignalType.TRIGGER)) {
			SignalInstance signal = null;
//			if (!existingSignals.containsKey(data) || (debug && existingSignals.get(data).history == null)) {
//				data.scorer.init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken, textTriggerTokenMap);
//			}
			if (!existingSignals.containsKey(data)) {
				///DEBUG
//				int dataHash = data.hashCode();
//				int existingHash;
//				for (ScorerData existing : existingSignals.keySet()) {
//					existingHash = existing.hashCode();
//				}
				////
				//System.out.printf("%s calcing trigger signal, data=%s, doc=%s, inst=%s, i=%s\n", Utils.detailedLog(), data, textSentence.doc.docLine, textSentence, i);
				//// DEBUG
//				if (	/*(textSentence.sentInstID.equals("1b") && i==0 && textSentence.docID.equals("APW_ENG_20030424.0532")) ||*/
//						(textSentence.sentInstID.equals("1a") && i==17)) {
//					System.out.printf("\n\n\n\n\nvoo\n\n\n\n\n\n");
//				}
				////
//				PredicateScorer<?> scorer = (PredicateScorer<?>) data.scorer;
				
				BigDecimal score = calcTriggerScore(data.elementAggregator, spec, textTriggerToken, textTriggerTokenMap, docAllText, data, textSentence);
//				try {
//					scorer.prepareCalc(spec, textTriggerToken, textTriggerTokenMap, docAllText, data);
//					score = data.elementAggregator.aggregate(scorer);
//				} catch (Throwable e) {
//					// HACK: just put zero in the signal if we get an exception. What SHOULD happen is probably something like not adding the signal (but then I should make sure that down the pipeline they ignore this as well).
//					score = BigDecimal.ZERO;
//					System.err.printf("SignalMechanism: Got some error while calcing score for trigger token=%s, doc=%s, scorer=%s: %s\n", UimaUtils.annotationToString(textTriggerToken), textSentence.doc.docLine, data, e);
//					e.printStackTrace(System.err);
//					System.err.printf("#############################################\n");
//				}
				
				///// DEBUG
//				if (textTriggerToken.getCoveredText().equals("attack") && !SignalInstance.isPositive.apply(score)) {
//					System.out.printf("\n\n\n\n\nGot a bad one!!!!!!! '%s'", textTriggerToken.getCoveredText());
//				}
				/////
				
				signal = new SignalInstance(data, SignalType.TRIGGER, score);
				existingSignals.put(data, signal);
				//allTriggerScorers.add(data);
				textSentence.markSignalUpdate();
			}
			if (debug) {
				if (signal == null) {
					signal = existingSignals.get(data);
				}
				if (signal.history == null) {
//					signal.initHistory();
					data.scorer.debug = true;
					data.scorer.history = ArrayListMultimap.create();
					
					////
					//System.out.printf("%s calcing trigger history signal, data=%s, doc=%s, inst=%s, i=%s\n", Utils.detailedLog(), data, textSentence.doc.docLine, textSentence, i);
					//// DEBUG
					// need to init again because the inner iterator is already exhausted, need to get a new one
//					PredicateScorer<?> scorer = (PredicateScorer<?>) data.scorer;
//					scorer.prepareCalc(spec, textTriggerToken, textTriggerTokenMap, docAllText, data);
//					debugAggregator.aggregate(scorer);
					calcTriggerScore(debugAggregator, spec, textTriggerToken, textTriggerTokenMap, docAllText, data, textSentence);
					
					data.scorer.debug = false;
					signal.history = data.scorer.history;
					textSentence.markSignalUpdate();
				}
			}
		}
	}

	private BigDecimal calcTriggerScore(Aggregator aggregator, JCas spec, Token textTriggerToken, Map<Class<?>, Object> textTriggerTokenMap, String docAllText, ScorerData data, SentenceInstance textSentence) {
		BigDecimal score;
		try {
			PredicateScorer<?> scorer = (PredicateScorer<?>) data.scorer;
			scorer.prepareCalc(spec, textTriggerToken, textTriggerTokenMap, docAllText, data);
			score = aggregator.aggregate(scorer);
		} catch (OutOfMemoryError e) {
			throw e;
		} catch (Throwable e) {
			// HACK: just put zero in the signal if we get an exception. What SHOULD happen is probably something like not adding the signal (but then I should make sure that down the pipeline they ignore this as well).
			score = BigDecimal.ZERO;
			System.err.printf("SignalMechanism: Got some error while calcing score for trigger token=%s, doc=%s, scorer=%s: %s\n", UimaUtils.annotationToString(textTriggerToken), textSentence.doc.docLine, data, e);
			e.printStackTrace(System.err);
			System.err.printf("#############################################\n");
		}
		return score;
	}
//	public LinkedHashMap<ScorerData, Multimap<String, String>> getTriggerDetails(JCas spec, SentenceInstance textSentence, int i) throws SignalMechanismException {
//		Token textTriggerToken = textSentence.getTokenAnnotation(i);
//		Map<Class<?>, Object> textTriggerTokenMap = ((List<Map<Class<?>, Object>>) textSentence.get(InstanceAnnotations.Token_FEATURE_MAPs)).get(i);
//		
//		return getTriggerTokenDetails(spec, textSentence, textTriggerToken, textTriggerTokenMap);
//	}

	public void scoreDependentArgument(Map<ScorerData, SignalInstance> existingSignals, JCas spec, SentenceInstance textSentence, int i, Argument argument, AceMention mention, boolean debug) throws SignalMechanismException {
		Token textTriggerToken = textSentence.sent.getTokenAnnotation(i);
		Map<Class<?>, Object> textTriggerTokenMap = ((List<Map<Class<?>, Object>>) textSentence.get(InstanceAnnotations.Token_FEATURE_MAPs)).get(i);
		String docAllText = textSentence.doc.allText;

		for (ScorerData data : scorers.get(SignalType.ARGUMENT_DEPENDENT)) {
			SignalInstance signal = null;
			if (!existingSignals.containsKey(data)) {
//				ArgumentDependentScorer<?> scorer = (ArgumentDependentScorer<?>) data.scorer;
				
				////
				//System.out.printf("%s calcing dep arg signal, data=%s, doc=%s, inst=%s, i=%s, mention=%s\n", Utils.detailedLog(), data, textSentence.doc.docLine, textSentence, i, mention);
				//// DEBUG
				BigDecimal score = calcArgDependentScore(data.elementAggregator, spec, textTriggerToken, textTriggerTokenMap, argument, mention, docAllText, data, textSentence);
//				try {
//					scorer.prepareCalc(spec, textTriggerToken, textTriggerTokenMap, argument, mention, docAllText, data);
//					score = data.elementAggregator.aggregate(scorer);
//				} catch (Throwable e) {
//					// HACK: just put zero in the signal if we get an exception. What SHOULD happen is probably something like not adding the signal (but then I should make sure that down the pipeline they ignore this as well).
//					score = BigDecimal.ZERO;
//					System.err.printf("SignalMechanism: Got some error while calcing score for arg (dependent), trigger-token=%s, role=%s, mention=%s, doc=%s, scorer=%s: %s\n",
//							UimaUtils.annotationToString(textTriggerToken), argument.getRole().getCoveredText(), mention, textSentence.doc.docLine, data, e);
//					e.printStackTrace(System.err);
//					System.err.printf("#############################################\n");
//				}
				
				signal = new SignalInstance(data, SignalType.ARGUMENT_DEPENDENT, score);
				existingSignals.put(data, signal);
				//allTriggerScorers.add(data);
				textSentence.markSignalUpdate();
			}
			if (debug) {
				if (signal == null) {
					signal = existingSignals.get(data);
				}
				if (signal.history == null) {
					data.scorer.debug = true;
					data.scorer.history = ArrayListMultimap.create();
					////
					//System.out.printf("%s calcing dep arg history signal, data=%s, doc=%s, inst=%s, i=%s, mention=%s\n", Utils.detailedLog(), data, textSentence.doc.docLine, textSentence, i, mention);
					//// DEBUG
					// need to init again because the inner iterator is already exhausted, need to get a new one
					calcArgDependentScore(debugAggregator, spec, textTriggerToken, textTriggerTokenMap, argument, mention, docAllText, data, textSentence);

//					ArgumentDependentScorer<?> scorer = (ArgumentDependentScorer<?>) data.scorer;
//					scorer.prepareCalc(spec, textTriggerToken, textTriggerTokenMap, argument, mention, docAllText, data);
//					debugAggregator.aggregate(scorer);
					data.scorer.debug = false;
					signal.history = data.scorer.history;
					textSentence.markSignalUpdate();
				}
			}
		}
	}

	private BigDecimal calcArgDependentScore(Aggregator aggregator, JCas spec, Token textTriggerToken, Map<Class<?>, Object> textTriggerTokenMap, Argument argument, AceMention mention, String docAllText, ScorerData data, SentenceInstance textSentence) {
		BigDecimal score;
		try {
			ArgumentDependentScorer<?> scorer = (ArgumentDependentScorer<?>) data.scorer;
			scorer.prepareCalc(spec, textTriggerToken, textTriggerTokenMap, argument, mention, docAllText, data);
			score = aggregator.aggregate(scorer);
		} catch (OutOfMemoryError e) {
			throw e;
		} catch (Throwable e) {
			// HACK: just put zero in the signal if we get an exception. What SHOULD happen is probably something like not adding the signal (but then I should make sure that down the pipeline they ignore this as well).
			score = BigDecimal.ZERO;
			System.err.printf("SignalMechanism: Got some error while calcing score for arg (dependent), trigger-token=%s, role=%s, mention=%s, doc=%s, scorer=%s: %s\n",
					UimaUtils.annotationToString(textTriggerToken), argument.getRole().getCoveredText(), mention, textSentence.doc.docLine, data, e);
			e.printStackTrace(System.err);
			System.err.printf("#############################################\n");
		}
		return score;
	}

	public void scoreFreeArgument(Map<ScorerData, SignalInstance> existingSignals, JCas spec, SentenceInstance textSentence, Argument argument, AceMention mention, boolean debug) throws SignalMechanismException {
		String docAllText = textSentence.doc.allText;
		JCas docJCas = textSentence.doc.jcas;

		for (ScorerData data : scorers.get(SignalType.ARGUMENT_FREE)) {
			SignalInstance signal = null;
			if (!existingSignals.containsKey(data)) {
//				ArgumentFreeScorer<?> scorer = (ArgumentFreeScorer<?>) data.scorer;
				
				////
				//System.out.printf("%s calcing free arg signal, data=%s, doc=%s, inst=%s, mention=%s\n", Utils.detailedLog(), data, textSentence.doc.docLine, textSentence, mention);
				//// DEBUG
				BigDecimal score = calcArgFreeScore(data.elementAggregator, spec, argument, mention, docJCas, docAllText, data, textSentence);
//				try {
//					scorer.prepareCalc(spec, argument, mention, docAllText, docJCas, data);
//					score = data.elementAggregator.aggregate(scorer);
//				} catch (Throwable e) {
//					// HACK: just put zero in the signal if we get an exception. What SHOULD happen is probably something like not adding the signal (but then I should make sure that down the pipeline they ignore this as well).
//					score = BigDecimal.ZERO;
//					System.err.printf("SignalMechanism: Got some error while calcing score for arg (free), role=%s, mention=%s, doc=%s, scorer=%s: %s\n",
//							argument.getRole().getCoveredText(), mention, textSentence.doc.docLine, data, e);
//					e.printStackTrace(System.err);
//					System.err.printf("#############################################\n");
//				}
				
				signal = new SignalInstance(data, SignalType.ARGUMENT_FREE, score);
				existingSignals.put(data, signal);
				//allTriggerScorers.add(data);
				textSentence.markSignalUpdate();
			}
			if (debug) {
				if (signal == null) {
					signal = existingSignals.get(data);
				}
				if (signal.history == null) {
					data.scorer.debug = true;
					data.scorer.history = ArrayListMultimap.create();
					// need to init again because the inner iterator is already exhausted, need to get a new one
					////
					//System.out.printf("%s calcing free arg history signal, data=%s, doc=%s, inst=%s, mention=%s\n", Utils.detailedLog(), data, textSentence.doc.docLine, textSentence, mention);
					//// DEBUG
					calcArgFreeScore(debugAggregator, spec, argument, mention, docJCas, docAllText, data, textSentence);
//					ArgumentFreeScorer<?> scorer = (ArgumentFreeScorer<?>) data.scorer;
//					scorer.prepareCalc(spec, argument, mention, docAllText, docJCas, data);
//					debugAggregator.aggregate(scorer);
					data.scorer.debug = false;
					signal.history = data.scorer.history;
					textSentence.markSignalUpdate();
				}
			}
		}
	}

	private BigDecimal calcArgFreeScore(Aggregator aggregator, JCas spec, Argument argument, AceMention mention, JCas docJCas, String docAllText, ScorerData data, SentenceInstance textSentence) {
		BigDecimal score;
		try {
			ArgumentFreeScorer<?> scorer = (ArgumentFreeScorer<?>) data.scorer;
			scorer.prepareCalc(spec, argument, mention, docAllText, docJCas, data);
			score = aggregator.aggregate(scorer);
		} catch (OutOfMemoryError e) {
			throw e;
		} catch (Throwable e) {
			// HACK: just put zero in the signal if we get an exception. What SHOULD happen is probably something like not adding the signal (but then I should make sure that down the pipeline they ignore this as well).
			score = BigDecimal.ZERO;
			System.err.printf("SignalMechanism: Got some error while calcing score for arg (free), role=%s, mention=%s, doc=%s, scorer=%s: %s\n",
					argument.getRole().getCoveredText(), mention, textSentence.doc.docLine, data, e);
			e.printStackTrace(System.err);
			System.err.printf("#############################################\n");
		}
		return score;
	}


	public String toString() {
		return String.format("%s(%s scorers)", getClass().getSimpleName(), scorers.size());
	}

	public abstract void addScorers() throws Exception;

	/**
	 * Optional operation
	 */
	public void close() { }
	
	public Controller controller;
	public Multimap<SignalType, ScorerData> scorers;
	private Aggregator debugAggregator = Aggregator.ScanAll.inst;
}
