package edu.cuny.qc.perceptron.similarity_scorer;

import java.util.Map;

import org.apache.uima.jcas.tcas.Annotation;

import edu.cuny.qc.perceptron.similarity_scorer.TargetMarker.Spec;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;

public class PastaScorer extends SimilarityScorer {

	public static PastaScorer instance = new PastaScorer();

	public void init(String someCoolPastaPath1, String andAnother, int numbersAreAlsoPrettyAwesome) {
		
	}
	
	@Override
	public void preprocessSentence(Sentence sentence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double scoreTriggerOneItem(SentenceInstance sent,
			SentenceAssignment assn, int i, Annotation specItem,
			ScopeMarker textScopeMarker, ScopeMarker specScopeMarker,
			AttributeMarker textAttrMarker, AttributeMarker specAttrMarker,
			FeatureMarker scorerMarker) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double scoreTriggerMultiItems(SentenceInstance sent,
			SentenceAssignment assn, int i, Spec specTargetMarker,
			ScopeMarker textScopeMarker, ScopeMarker specScopeMarker,
			AttributeMarker textAttrMarker, AttributeMarker specAttrMarker,
			FeatureMarker scorerMarker) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double scoreArgOneItem(SentenceInstance sent,
			SentenceAssignment assn, int i, int k, Annotation specItem,
			ScopeMarker textScopeMarker, ScopeMarker specScopeMarker,
			AttributeMarker textAttrMarker, AttributeMarker specAttrMarker,
			FeatureMarker scorerMarker) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double scoreArgMultiItems(SentenceInstance sent,
			SentenceAssignment assn, int i, int k, Spec specTargetMarker,
			ScopeMarker textScopeMarker, ScopeMarker specScopeMarker,
			AttributeMarker textAttrMarker, AttributeMarker specAttrMarker,
			FeatureMarker scorerMarker) {
		// TODO Auto-generated method stub
		return 0;
	}


}
