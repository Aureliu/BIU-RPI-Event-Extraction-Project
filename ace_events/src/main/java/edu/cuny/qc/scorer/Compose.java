package edu.cuny.qc.scorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

public abstract class Compose extends PredicateSeedScorer {
	private static final long serialVersionUID = -6327210445951007729L;
	protected PredicateSeedScorer[] scorers;
	private int hash;
	private boolean hasHash = false;
	@Override public int hashCode() {
		if (!hasHash) {
			hash = new HashCodeBuilder(1231, 1237).append(scorers).toHashCode();
			hasHash = true;
		}
		return hash;
	}
	@Override public boolean equals(Object obj) {
		   if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) { return false; }
		   Compose rhs = (Compose) obj;
		   return new EqualsBuilder().appendSuper(super.equals(obj)).append(scorers, rhs.scorers).isEquals();
	}
	@Override
	public String getTypeName() {
		List<String> names = new ArrayList<String>(scorers.length);
		for (SignalMechanismSpecIterator<?> scorer : scorers) {
			names.add(scorer.getTypeName());
		}
		return String.format("%s(%s)", getClass().getSimpleName(), StringUtils.join(names, ','));
	}

	public static class Or extends Compose {
		private static final long serialVersionUID = -9172233066400279525L;
		public Or(PredicateSeedScorer... scorers) {
			this.scorers = scorers;
		}
		@Override
		public Boolean calcBoolPredicateSeedScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException {
			for (PredicateSeedScorer scorer : scorers) {
				boolean positive = scorer.calcBoolPredicateSeedScore(textToken, textTriggerTokenMap, textStr, textPos, specStr, specPos, scorerData);
				if (positive) {
					return true;
				}
			}
			return false;
		}
	}
	public static class And extends Compose {
		/**
		 * 
		 */
		private static final long serialVersionUID = 201053223418406937L;
		PredicateSeedScorer[] scorers;
		public And(PredicateSeedScorer... scorers) {
			this.scorers = scorers;
		}
		@Override
		public Boolean calcBoolPredicateSeedScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException {
			for (PredicateSeedScorer scorer : scorers) {
				boolean positive = scorer.calcBoolPredicateSeedScore(textToken, textTriggerTokenMap, textStr, textPos, specStr, specPos, scorerData);
				if (!positive) {
					return false;
				}
			}
			return true;
		}
	}
}
