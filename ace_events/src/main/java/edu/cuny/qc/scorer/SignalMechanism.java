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

public abstract class SignalMechanism {
	
	static {
		System.err.println("??? SignalMechanism: for argument, now considering only HEAD (not extent), and only FIRST WORD of head (could be more than one word). Need to think of handling MWEs.");
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
				//System.out.printf("%s NEW\n", Pipeline.detailedLog());
				
				//// DEBUG
//				if (	/*(textSentence.sentInstID.equals("1b") && i==0 && textSentence.docID.equals("APW_ENG_20030424.0532")) ||*/
//						(textSentence.sentInstID.equals("1a") && i==17)) {
//					System.out.printf("\n\n\n\n\nvoo\n\n\n\n\n\n");
//				}
				////
				PredicateScorer<?> scorer = (PredicateScorer<?>) data.scorer;
				scorer.prepareCalc(spec, textTriggerToken, textTriggerTokenMap, docAllText, data);
				BigDecimal score = data.elementAggregator.aggregate(scorer);
				
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
					// need to init again because the inner iterator is already exhausted, need to get a new one
					PredicateScorer<?> scorer = (PredicateScorer<?>) data.scorer;
					scorer.prepareCalc(spec, textTriggerToken, textTriggerTokenMap, docAllText, data);
					debugAggregator.aggregate(scorer);
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

	public void scoreDependentArgument(Map<ScorerData, SignalInstance> existingSignals, JCas spec, SentenceInstance textSentence, int i,	Argument argument, AceMention mention, boolean debug) throws SignalMechanismException {
		Token textTriggerToken = textSentence.sent.getTokenAnnotation(i);
		Map<Class<?>, Object> textTriggerTokenMap = ((List<Map<Class<?>, Object>>) textSentence.get(InstanceAnnotations.Token_FEATURE_MAPs)).get(i);
		String docAllText = textSentence.doc.allText;

		for (ScorerData data : scorers.get(SignalType.ARGUMENT_DEPENDENT)) {
			SignalInstance signal = null;
			if (!existingSignals.containsKey(data)) {
				///DEBUG
				ArgumentDependentScorer<?> scorer = (ArgumentDependentScorer<?>) data.scorer;
				scorer.prepareCalc(spec, textTriggerToken, textTriggerTokenMap, argument, mention, docAllText, data);
				BigDecimal score = data.elementAggregator.aggregate(scorer);
				
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
					data.scorer.debug = true;
					data.scorer.history = ArrayListMultimap.create();
					// need to init again because the inner iterator is already exhausted, need to get a new one
					ArgumentDependentScorer<?> scorer = (ArgumentDependentScorer<?>) data.scorer;
					scorer.prepareCalc(spec, textTriggerToken, textTriggerTokenMap, argument, mention, docAllText, data);
					debugAggregator.aggregate(scorer);
					data.scorer.debug = false;
					signal.history = data.scorer.history;
					textSentence.markSignalUpdate();
				}
			}
		}
	}

	public void scoreFreeArgument(Map<ScorerData, SignalInstance> existingSignals, JCas spec, SentenceInstance textSentence, Argument argument, AceMention mention, boolean debug) throws SignalMechanismException {
		String docAllText = textSentence.doc.allText;
		JCas docJCas = textSentence.doc.jcas;

		for (ScorerData data : scorers.get(SignalType.ARGUMENT_FREE)) {
			SignalInstance signal = null;
			if (!existingSignals.containsKey(data)) {
				///DEBUG
				ArgumentFreeScorer<?> scorer = (ArgumentFreeScorer<?>) data.scorer;
				scorer.prepareCalc(spec, argument, mention, docAllText, docJCas, data);
				BigDecimal score = data.elementAggregator.aggregate(scorer);
				
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
					data.scorer.debug = true;
					data.scorer.history = ArrayListMultimap.create();
					// need to init again because the inner iterator is already exhausted, need to get a new one
					ArgumentFreeScorer<?> scorer = (ArgumentFreeScorer<?>) data.scorer;
					scorer.prepareCalc(spec, argument, mention, docAllText, docJCas, data);
					debugAggregator.aggregate(scorer);
					data.scorer.debug = false;
					signal.history = data.scorer.history;
					textSentence.markSignalUpdate();
				}
			}
		}
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
