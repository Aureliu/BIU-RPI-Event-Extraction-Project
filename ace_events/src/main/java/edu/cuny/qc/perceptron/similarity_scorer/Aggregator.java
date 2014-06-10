package edu.cuny.qc.perceptron.similarity_scorer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;

import com.google.common.collect.Iterators;

import edu.cuny.qc.perceptron.types.SignalInstance;

public abstract class Aggregator /*implements Serializable*/ {
	private static final long serialVersionUID = -2650084338758293108L;

	public String getTypeName() {return getClass().getSimpleName(); }
	public abstract String getSuffix();
	public abstract BigDecimal aggregate(Iterator<BigDecimal> scoreIterator);
	
	public static class Any extends Aggregator {
		private static final long serialVersionUID = -5751052602451116048L;
		public static final Aggregator inst = new Any();
		@Override public String getSuffix() { return ""; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			return SignalInstance.toDouble(Iterators.any(scoreIterator, SignalInstance.isPositive));
		}
	}
	
	public static class Min2 extends Aggregator {
		private static final long serialVersionUID = 2977476394909382268L;
		public static final Aggregator inst = new Min2();
		@Override public String getSuffix() { return "-Min2"; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			Iterator<BigDecimal> filtered = Iterators.filter(scoreIterator, SignalInstance.isPositive);
			return SignalInstance.toDouble(Iterators.size(filtered) >= 2);
		}
	}
}
