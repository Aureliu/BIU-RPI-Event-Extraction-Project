package edu.cuny.qc.scorer.mechanism;

import static edu.cuny.qc.scorer.Aggregator.*;
import static edu.cuny.qc.scorer.Derivation.*;
import static edu.cuny.qc.scorer.Deriver.*;

import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.scorer.Aggregator;
import edu.cuny.qc.scorer.Derivation;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.PredicateSeedScorer;
import edu.cuny.qc.scorer.mechanism.WordNetSignalMechanism.WordnetDervRltdDeriver;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

public class POSSignalMechanism extends SignalMechanism {

	@Override
	public void addScorers() {
//		addTrigger(new ScorerData("PL_SAME_TOKEN",		SameToken.inst,				Aggregator.Any.inst		));
//		addTrigger(new ScorerData("PL_SAME_LEMMA",		SameLemma.inst,				Aggregator.Any.inst		));
//		
//		addTrigger(new ScorerData("PL_DERIVATION_OD", TokenDerivation.inst, WordnetDervRltdDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, Aggregator.Any.inst));
	}

	public POSSignalMechanism() throws SignalMechanismException {
		super();
	}

	private static class SpecificPOS extends PredicateSeedScorer {
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			throw new NotImplementedException("SpecificPOS");
		}
	}

}
