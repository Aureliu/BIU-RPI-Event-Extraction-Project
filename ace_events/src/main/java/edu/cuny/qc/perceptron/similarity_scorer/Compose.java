package edu.cuny.qc.perceptron.similarity_scorer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.jcas.tcas.Annotation;
import org.hibernate.property.Getter;

import edu.cuny.qc.perceptron.types.SignalInstance;

public abstract class Compose extends SignalMechanismSpecIterator {
	
	public static class Or extends Compose {
		SignalMechanismSpecIterator[] scorers;
		public Or(SignalMechanismSpecIterator... scorers) {
			this.scorers = scorers;
		}
		@Override
		public BigDecimal calcScore(Annotation text, Annotation spec) throws SignalMechanismException {
			for (SignalMechanismSpecIterator scorer : scorers) {
				boolean positive = SignalInstance.isPositive.apply(scorer.calcScore(text, spec));
				if (positive) {
					return SignalInstance.toDouble(true);
				}
			}
			return SignalInstance.toDouble(false);
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
		SignalMechanismSpecIterator[] scorers;
		public And(SignalMechanismSpecIterator... scorers) {
			this.scorers = scorers;
		}
		@Override
		public BigDecimal calcScore(Annotation text, Annotation spec) throws SignalMechanismException {
			for (SignalMechanismSpecIterator scorer : scorers) {
				boolean positive = SignalInstance.isPositive.apply(scorer.calcScore(text, spec));
				if (!positive) {
					return SignalInstance.toDouble(false);
				}
			}
			return SignalInstance.toDouble(true);
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
