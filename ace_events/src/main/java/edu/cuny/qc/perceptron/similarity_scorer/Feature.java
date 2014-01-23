package edu.cuny.qc.perceptron.similarity_scorer;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;

import edu.cuny.qc.perceptron.similarity_scorer.TargetMarker.Spec;
import edu.cuny.qc.perceptron.similarity_scorer.TargetMarker.Text;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;

public class Feature {

	private SimilarityScorer scorer;
	private FeatureMarker featureMarker;
	private AggragateMarker aggragateMarker;
	private ScopeMarker textScopeMarker;
	private ScopeMarker specScopeMarker;
	private AttributeMarker textAttrMarker;
	private AttributeMarker specAttrMarker;
	private TargetMarker.Text textTargetMarker;
	private TargetMarker.Spec specTargetMarker;

	
	public Feature(SimilarityScorer scorer,
			FeatureMarker scorerMarker, AggragateMarker aggragateMarker,
			ScopeMarker textScopeMarker, ScopeMarker specScopeMarker,
			AttributeMarker textAttrMarker, AttributeMarker specAttrMarker,
			Text textTargetMarker, Spec specTargetMarker) {

		this.scorer = scorer;
		this.featureMarker = featureMarker;
		this.aggragateMarker = aggragateMarker;
		this.textScopeMarker = textScopeMarker;
		this.specScopeMarker = specScopeMarker;
		this.textAttrMarker = textAttrMarker;
		this.specAttrMarker = specAttrMarker;
		this.textTargetMarker = textTargetMarker;
		this.specTargetMarker = specTargetMarker;
	}



	public double scoreTrigger(SentenceInstance sent, SentenceAssignment assn, int i, JCas spec) {
		double finalScore;
		if (this.aggragateMarker == AggragateMarker.USE_ALL_ITEMS) {
			finalScore = scorer.scoreTriggerMultiItems(sent, assn, i, specTargetMarker, textScopeMarker, specScopeMarker, textAttrMarker, specAttrMarker, featureMarker);
		}
		else {
			List<Double> scores = new ArrayList<Double>();
			for specItem in specSet:
				double score = scorer.scoreTriggerOneItem(… specItem ...)
				scores.add(score)
			switch aggrMarker:
				case CHOOSE_MAX: finalScore = max(scores)
				case CHOOSE_AVG: finalScore = avg(scores)
		}
		return finalScore

	}
}
