package edu.cuny.qc.scorer;

import java.util.Map;

import org.apache.uima.jcas.JCas;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateInUsageSample;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceArgumentType;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

public interface SignalScorer {
	
	public interface PredicateSeedScorer extends SignalScorer {
		public void preparePredicateSeedIteration(JCas spec, Token textAnno, Map<Class<?>, Object> textTriggerTokenMap, ScorerData scorerData) throws SignalMechanismException;
		public Boolean calcPredicateSeedBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException;
	}
	
	public interface PredicateUsageSampleScorer extends SignalScorer {
		public void preparePredicateUsageSampleIteration(JCas spec, Token textAnno, Map<Class<?>, Object> textTriggerTokenMap, ScorerData scorerData) throws SignalMechanismException;
		public Boolean calcPredicateUsageSampleBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, PredicateInUsageSample pius, ScorerData scorerData) throws SignalMechanismException;
	}

	public interface ArgumentExampleScorer extends SignalScorer {
		public void prepareArgumentExampleIteration(JCas spec, Token textAnno, Map<Class<?>, Object> textTriggerTokenMap, String role, ScorerData scorerData) throws SignalMechanismException;
		public Boolean calcArgumentExampleBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException;
	}
	
	public interface ArgumentTypeScorer extends SignalScorer {
		public void prepareArgumentTypeIteration(JCas spec, Token textAnno, Map<Class<?>, Object> textTriggerTokenMap, String role, ScorerData scorerData) throws SignalMechanismException;
		public Boolean calcArgumentTypeBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, AceArgumentType argumentType, ScorerData scorerData) throws SignalMechanismException;
	}
	
	public interface ArgumentUsageSampleScorer extends SignalScorer {
		public void prepareArgumentUsageSampleIteration(JCas spec, Token textAnno, Map<Class<?>, Object> textTriggerTokenMap, String role, ScorerData scorerData) throws SignalMechanismException;
		public Boolean calcArgumentUsageSampleBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, ArgumentInUsageSample aius, ScorerData scorerData) throws SignalMechanismException;
	}
}
