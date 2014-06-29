package edu.cuny.qc.scorer.mechanism;

import java.util.Map;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.scorer.Aggregator;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.SignalMechanismSpecTokenIterator;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

public class PlainSignalMechanism extends SignalMechanism {

	@Override
	public void addScorers() {
//		addTrigger(new ScorerData("FAKE_LETTER_E",		TextHasLetterE.inst,		Aggregator.Any.inst		));
//		addTrigger(new ScorerData("FAKE_LETTER_X",		TextHasLetterX.inst,		Aggregator.Any.inst		));
		addTrigger(new ScorerData("PL_SAME_TOKEN",		SameToken.inst,				Aggregator.Any.inst		));
		addTrigger(new ScorerData("PL_SAME_LEMMA",		SameLemma.inst,				Aggregator.Any.inst		));
	}

	public PlainSignalMechanism() throws SignalMechanismException {
		super();
	}

	private static class SameToken extends SignalMechanismSpecTokenIterator {
		public static final SameToken inst = new SameToken();
		@Override public String getForm(Token token) { return token.getCoveredText();}
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos) throws SignalMechanismException
		{
			return textStr.equals(specStr);
		}
	}
	
	private static class SameLemma extends SignalMechanismSpecTokenIterator {
		public static final SameLemma inst = new SameLemma();
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos) throws SignalMechanismException
		{
			return textStr.equals(specStr);
		}
	}
	
	private static class TextHasLetterE extends SignalMechanismSpecTokenIterator {
		public static final TextHasLetterE inst = new TextHasLetterE();
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos) throws SignalMechanismException
		{
			return textStr.contains("e");
		}
	}
	
	private static class TextHasLetterX extends SignalMechanismSpecTokenIterator {
		public static final TextHasLetterX inst = new TextHasLetterX();
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos) throws SignalMechanismException
		{
			return textStr.contains("x");
		}
	}
}
