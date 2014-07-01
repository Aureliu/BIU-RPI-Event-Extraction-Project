package edu.cuny.qc.scorer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.jcas.tcas.Annotation;
import org.hibernate.property.Getter;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import edu.cuny.qc.perceptron.types.SignalInstance;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

public abstract class Compose extends SignalMechanismSpecTokenIterator {
	
	public static class Or extends Compose {
		SignalMechanismSpecTokenIterator[] scorers;
		public Or(SignalMechanismSpecTokenIterator... scorers) {
			this.scorers = scorers;
		}
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException {
			for (SignalMechanismSpecTokenIterator scorer : scorers) {
				boolean positive = scorer.calcTokenBooleanScore(textToken, textTriggerTokenMap, textStr, textPos, specStr, specPos, scorerData);
				if (positive) {
					return true;
				}
			}
			return false;
		}
		@Override
		public String getTypeName() {
			List<String> names = new ArrayList<String>(scorers.length);
			for (SignalMechanismSpecIterator scorer : scorers) {
				names.add(scorer.getTypeName());
			}
			return String.format("%s(%s)", getClass().getSimpleName(), StringUtils.join(names, ','));
		}
	}
	public static class And extends Compose {
		SignalMechanismSpecTokenIterator[] scorers;
		public And(SignalMechanismSpecTokenIterator... scorers) {
			this.scorers = scorers;
		}
		@Override
		public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException {
			for (SignalMechanismSpecTokenIterator scorer : scorers) {
				boolean positive = scorer.calcTokenBooleanScore(textToken, textTriggerTokenMap, textStr, textPos, specStr, specPos, scorerData);
				if (!positive) {
					return false;
				}
			}
			return true;
		}
		@Override
		public String getTypeName() {
			List<String> names = new ArrayList<String>(scorers.length);
			for (SignalMechanismSpecIterator scorer : scorers) {
				names.add(scorer.getTypeName());
			}
			return String.format("%s(%s)", getClass().getSimpleName(), StringUtils.join(names, ','));
		}
	}
}
