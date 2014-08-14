package edu.cuny.qc.scorer.mechanism;

import static edu.cuny.qc.scorer.Aggregator.*;
import static edu.cuny.qc.scorer.Derivation.*;
import static edu.cuny.qc.scorer.Deriver.*;

import java.util.Map;

import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.scorer.Aggregator;
import edu.cuny.qc.scorer.ArgumentExampleScorer;
import edu.cuny.qc.scorer.Derivation;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.PredicateSeedScorer;
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
			addTrigger(new ScorerData("PL_SAME_TOKEN",				SameTriggerToken.inst,				Aggregator.Any.inst		));
			addArgument(new ScorerData("PL_SAME_FULLHEAD",			SameArgumentText.inst,				Aggregator.Any.inst		));
			break;
		case ANALYSIS: //fall-through, analyze exactly all normal scorers 
		case NORMAL:
			addTrigger(new ScorerData("PL_SAME_LEMMA",				SameTriggerLemma.inst,				Aggregator.Any.inst		));
			
			addArgument(new ScorerData("PL_SAME_FULLHEAD",			SameArgumentText.inst,				Aggregator.Any.inst		));
			addArgument(new ScorerData("PL_SAME_LEMMA_HEADTOKEN",	SameArgumentHeadTokenLemma.inst,	Aggregator.Any.inst		));
			
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

	public PlainSignalMechanism(Controller controller) throws SignalMechanismException {
		super(controller);
	}

	private static class SameTriggerToken extends PredicateSeedScorer {
		private static final long serialVersionUID = -2874181064215529174L;
		public static final SameTriggerToken inst = new SameTriggerToken();
		@Override public String getForm(Token token) { return token.getCoveredText();}
		@Override
		public Boolean calcBoolPredicateSeedScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.equals(specStr);
		}
	}
	
	private static class SameTriggerLemma extends PredicateSeedScorer {
		private static final long serialVersionUID = 3117453748881596932L;
		public static final SameTriggerLemma inst = new SameTriggerLemma();
		@Override
		public Boolean calcBoolPredicateSeedScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.equals(specStr);
		}
	}
	
	private static class SameArgumentText extends ArgumentExampleScorer {
		private static final long serialVersionUID = 756493837201510988L;
		public static final SameArgumentText inst = new SameArgumentText();
		@Override
		public Boolean calcBoolArgumentExampleScore(AceEntityMention corefMention, Annotation headAnno, String textHeadTokenStr, PartOfSpeech textHeadTokenPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return headAnno.getCoveredText().equalsIgnoreCase(specStr);
		}
	}
	
	private static class SameArgumentHeadTokenLemma extends ArgumentExampleScorer {
		private static final long serialVersionUID = 5682579893362232185L;
		public static final SameArgumentHeadTokenLemma inst = new SameArgumentHeadTokenLemma();
		@Override
		public Boolean calcBoolArgumentExampleScore(AceEntityMention corefMention, Annotation headAnno, String textHeadTokenStr, PartOfSpeech textHeadTokenPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textHeadTokenStr.equals(specStr);
		}
	}
	
	/**
	 * This class is actually identical to SameToken, but it's meant to be used with derivers.
	 */
	public static class TokenDerivation extends PredicateSeedScorer {
		private static final long serialVersionUID = -7787465525225717077L;
		public static final TokenDerivation inst = new TokenDerivation();
		@Override public String getForm(Token token) { return token.getCoveredText();}
		@Override
		public Boolean calcBoolPredicateSeedScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
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
		public Boolean calcBoolPredicateSeedScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.equals(specStr);
		}
	}
	
	private static class TextHasLetterE extends PredicateSeedScorer {
		private static final long serialVersionUID = -7752844963383651977L;
		public static final TextHasLetterE inst = new TextHasLetterE();
		@Override
		public Boolean calcBoolPredicateSeedScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.contains("e");
		}
	}
	
	private static class TextHasLetterX extends PredicateSeedScorer {
		private static final long serialVersionUID = 4984463409316221623L;
		public static final TextHasLetterX inst = new TextHasLetterX();
		@Override
		public Boolean calcBoolPredicateSeedScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textStr.contains("x");
		}
	}
}
