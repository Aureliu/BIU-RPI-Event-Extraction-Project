package edu.cuny.qc.scorer.mechanism;

import static edu.cuny.qc.scorer.Aggregator.*;
import static edu.cuny.qc.scorer.Derivation.*;
import static edu.cuny.qc.scorer.Deriver.*;

import java.util.Map;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.scorer.Aggregator;
import edu.cuny.qc.scorer.Derivation;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.PredicateSeedScorer;
import edu.cuny.qc.scorer.mechanism.WordNetSignalMechanism.WordnetDervRltdDeriver;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

public class PlainSignalMechanism extends SignalMechanism {

	@Override
	public void addScorers() {
//		addTrigger(new ScorerData("FAKE_LETTER_E",		TextHasLetterE.inst,		Aggregator.Any.inst		));
//		addTrigger(new ScorerData("FAKE_LETTER_X",		TextHasLetterX.inst,		Aggregator.Any.inst		));
		addTrigger(new ScorerData("PL_SAME_TOKEN",		SameToken.inst,				Aggregator.Any.inst		));
		addTrigger(new ScorerData("PL_SAME_LEMMA",		SameLemma.inst,				Aggregator.Any.inst		));
		
		addTrigger(new ScorerData("PL_DERIVATION_OD", TokenDerivation.inst, WordnetDervRltdDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, Aggregator.Any.inst));
	}

	public PlainSignalMechanism() throws SignalMechanismException {
		super();
	}

	private static class SameToken extends PredicateSeedScorer {
		private static final long serialVersionUID = -2874181064215529174L;
		public static final SameToken inst = new SameToken();
		@Override public String getForm(Token token) { return token.getCoveredText();}
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.equals(specStr);
		}
	}
	
	private static class SameLemma extends PredicateSeedScorer {
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
	private static class TokenDerivation extends PredicateSeedScorer {
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
	private static class LemmaDerivation extends PredicateSeedScorer {
		private static final long serialVersionUID = 3247642100800512316L;
		public static final LemmaDerivation inst = new LemmaDerivation();
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.equals(specStr);
		}
	}
	
	private static class TextHasLetterE extends PredicateSeedScorer {
		private static final long serialVersionUID = -7752844963383651977L;
		public static final TextHasLetterE inst = new TextHasLetterE();
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.contains("e");
		}
	}
	
	private static class TextHasLetterX extends PredicateSeedScorer {
		private static final long serialVersionUID = 4984463409316221623L;
		public static final TextHasLetterX inst = new TextHasLetterX();
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.contains("x");
		}
	}
}
