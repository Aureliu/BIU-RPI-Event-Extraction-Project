package edu.cuny.qc.scorer.mechanism;

import static edu.cuny.qc.scorer.Aggregator.*;
import static edu.cuny.qc.scorer.Derivation.*;
import static edu.cuny.qc.scorer.Deriver.*;

import java.util.Map;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.scorer.Aggregator;
import edu.cuny.qc.scorer.Derivation;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.PredicateSeedScorerTEMP;
import edu.cuny.qc.scorer.Compose.Or;
import edu.cuny.qc.scorer.mechanism.POSSignalMechanism.SpecificPOS;
import edu.cuny.qc.scorer.mechanism.WordNetSignalMechanism.WordnetDervRltdDeriver;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

public class PlainSignalMechanism extends SignalMechanism {

	static {
		System.err.printf("??? PlainSignalMechanism: remove one of PL_DERIVATION scorers - they are bot hthere only as an experiment!!!\n");
	}
	@Override
	public void addScorers() {
		switch(controller.featureProfile) {
		case TOKEN_BASELINE:
			addTrigger(new ScorerData("PL_SAME_TOKEN",		SameToken.inst,				Aggregator.Any.inst		));
			break;
		case ANALYSIS: //fall-through, analyze exactly all normal scorers 
		case NORMAL:
			addTrigger(new ScorerData("PL_SAME_LEMMA",		SameLemma.inst,				Aggregator.Any.inst		));
			
			break;
		default:
			throw new IllegalStateException("Bad FeatureProfile enum value: " + controller.featureProfile);
		}

		// they are both here only as an experiment - they should behave the same due to TEXT_ORIG_AND_DERV!!!!
		// Later, remove one of them
		// Shit, and now the first one gets an exception, since NoDerv doesn't like to be inside of a Join. Maybe solve at some point.
		//addTrigger(new ScorerData("PL_DERIVATION+LEMMA", TokenDerivation.inst, new Join(NoDerv.inst, WordnetDervRltdDeriver.inst), Derivation.TEXT_ORIG_AND_DERV, Aggregator.Any.inst));
		
		// This guy annoys me and probably has a bug - spec words are ignored!!!
		//addTrigger(new ScorerData("PL_DERIVATION", TokenDerivation.inst, WordnetDervRltdDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, Aggregator.Any.inst));
		
	}

	public PlainSignalMechanism(Perceptron perceptron) throws SignalMechanismException {
		super(perceptron);
	}

	private static class SameToken extends PredicateSeedScorerTEMP {
		private static final long serialVersionUID = -2874181064215529174L;
		public static final SameToken inst = new SameToken();
		@Override public String getForm(Token token) { return token.getCoveredText();}
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.equals(specStr);
		}
	}
	
	private static class SameLemma extends PredicateSeedScorerTEMP {
		private static final long serialVersionUID = 3117453748881596932L;
		public static final SameLemma inst = new SameLemma();
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.equals(specStr);
		}
	}
	
	/**
	 * This class is actually identical to SameToken, but it's meant to be used with derivers.
	 */
	public static class TokenDerivation extends PredicateSeedScorerTEMP {
		private static final long serialVersionUID = -7787465525225717077L;
		public static final TokenDerivation inst = new TokenDerivation();
		@Override public String getForm(Token token) { return token.getCoveredText();}
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.equals(specStr);
		}
	}
	/**
	 * This class is actually identical to SameLemma, but it's meant to be used with derivers.
	 */
	private static class LemmaDerivation extends PredicateSeedScorerTEMP {
		private static final long serialVersionUID = 3247642100800512316L;
		public static final LemmaDerivation inst = new LemmaDerivation();
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.equals(specStr);
		}
	}
	
	private static class TextHasLetterE extends PredicateSeedScorerTEMP {
		private static final long serialVersionUID = -7752844963383651977L;
		public static final TextHasLetterE inst = new TextHasLetterE();
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.contains("e");
		}
	}
	
	private static class TextHasLetterX extends PredicateSeedScorerTEMP {
		private static final long serialVersionUID = 4984463409316221623L;
		public static final TextHasLetterX inst = new TextHasLetterX();
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.contains("x");
		}
	}
}
