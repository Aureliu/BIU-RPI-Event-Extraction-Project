package edu.cuny.qc.scorer.mechanism;

import java.util.Map;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.scorer.Aggregator;
import edu.cuny.qc.scorer.Compose.Or;
import edu.cuny.qc.scorer.Derivation;
import edu.cuny.qc.scorer.Deriver.NoDerv;
import edu.cuny.qc.scorer.PredicateSeedScorer;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

public class POSSignalMechanism extends SignalMechanism {

	@Override
	public void addScorers() throws UnsupportedPosTagStringException {
		
		// Ignore this entirely for now
		/*
		switch(controller.featureProfile) {
		case TOKEN_BASELINE: break;
		case ANALYSIS: //fall-through, analyze exactly all normal scorers 
		case NORMAL:
//			addTrigger(new ScorerData(null, new SpecificPOS("NN"), true));
//			addTrigger(new ScorerData(null, new SpecificPOS("VBN"), true));
//			addTrigger(new ScorerData(null, new Or(new SpecificPOS("NN"), new SpecificPOS("VBN"), new SpecificPOS("NNS"), new SpecificPOS("VBD")), true));
			
			break;
		default:
			throw new IllegalStateException("Bad FeatureProfile enum value: " + controller.featureProfile);
		}
		*/
	}

	public POSSignalMechanism(Controller controller) throws SignalMechanismException {
		super(controller);
	}

	public static class SpecificPOS extends PredicateSeedScorer {
		private static final long serialVersionUID = 1722107959748327810L;
		public PartOfSpeech pos;
		public SpecificPOS(String specificPosTagStr) throws UnsupportedPosTagStringException {
			pos = new PennPartOfSpeech(specificPosTagStr);
		}
		@Override public String getTypeName() {
			return "POS_" + pos;
		}
		@Override
		public Boolean calcBoolPredicateSeedScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			return textPos.equals(pos);
		}
	}

}
