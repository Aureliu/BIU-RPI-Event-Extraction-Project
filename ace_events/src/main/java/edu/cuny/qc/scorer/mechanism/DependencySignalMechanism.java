package edu.cuny.qc.scorer.mechanism;

import java.util.List;
import java.util.Map;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.graph.GraphEdge;
import edu.cuny.qc.perceptron.graph.GraphNode;
import edu.cuny.qc.scorer.Aggregator;
import edu.cuny.qc.scorer.Compose.Or;
import edu.cuny.qc.scorer.Derivation;
import edu.cuny.qc.scorer.Deriver.NoDerv;
import edu.cuny.qc.scorer.PredicateSeedScorerTEMP;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.util.TokenAnnotations;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

public class DependencySignalMechanism extends SignalMechanism {

	@Override
	public void addScorers() throws UnsupportedPosTagStringException {
		//addTrigger(new ScorerData(null, new Or(new OneDepUp("pobj"), new OneDepUp("dobj"), new OneDepUp("nsubj")), true));
	}

	public DependencySignalMechanism(Perceptron perceptron) throws SignalMechanismException {
		super(perceptron);
	}

	public static class OneDepUp extends PredicateSeedScorerTEMP {
		private static final long serialVersionUID = 5805470654188632623L;
		public String relation;
		public OneDepUp(String relation) {
			this.relation = relation;
		}
		@Override public String getTypeName() {
			return "DepUp_" + relation;
		}
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			List<GraphEdge> toParents = (List<GraphEdge>) textTriggerTokenMap.get(TokenAnnotations.EdgesToParents.class);
			for (GraphEdge edge : toParents) {
				if (edge.getRelation().equals(relation)) {
					return true;
				}
			}
			return false;
		}
	}

}
