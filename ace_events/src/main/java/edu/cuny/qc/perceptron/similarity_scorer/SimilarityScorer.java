package edu.cuny.qc.perceptron.similarity_scorer;

import java.util.Map;

import org.apache.uima.jcas.tcas.Annotation;

import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;

public abstract class SimilarityScorer {
	public abstract void/*??*/ preprocessSentence(Sentence sentence);
	
	public abstract double scoreTriggerOneItem(SentenceInstance sent, SentenceAssignment assn, int i, Annotation specItem, ScopeMarker textScopeMarker, ScopeMarker specScopeMarker, AttributeMarker textAttrMarker, AttributeMarker specAttrMarker, FeatureMarker scorerMarker);
	public abstract double scoreTriggerMultiItems(SentenceInstance sent, SentenceAssignment assn, int i, TargetMarker.Spec specTargetMarker, ScopeMarker textScopeMarker, ScopeMarker specScopeMarker, AttributeMarker textAttrMarker, AttributeMarker specAttrMarker, FeatureMarker scorerMarker);
	public abstract double scoreArgOneItem(SentenceInstance sent, SentenceAssignment assn, int i, int k, Annotation specItem, ScopeMarker textScopeMarker, ScopeMarker specScopeMarker, AttributeMarker textAttrMarker, AttributeMarker specAttrMarker, FeatureMarker scorerMarker);
	public abstract double scoreArgMultiItems(SentenceInstance sent, SentenceAssignment assn, int i, int k, TargetMarker.Spec specTargetMarker, ScopeMarker textScopeMarker, ScopeMarker specScopeMarker, AttributeMarker textAttrMarker, AttributeMarker specAttrMarker, FeatureMarker scorerMarker);

	/** Cannot call c-tor directly **/
	protected SimilarityScorer() { }
	
}
