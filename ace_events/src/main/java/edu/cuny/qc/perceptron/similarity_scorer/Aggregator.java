package edu.cuny.qc.perceptron.similarity_scorer;

import java.math.BigDecimal;
import java.util.Iterator;

import com.google.common.collect.Iterators;

import edu.cuny.qc.perceptron.types.SignalInstance;

public abstract class Aggregator {
	public abstract String getSuffix();
	public abstract BigDecimal aggregate(Iterator<BigDecimal> scoreIterator);
	
	public static class Any extends Aggregator {
		public static final Aggregator inst = new Any();
		@Override public String getSuffix() { return ""; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			return SignalInstance.toDouble(Iterators.any(scoreIterator, SignalInstance.isPositive));
		}
	}
	
	public static class Min2 extends Aggregator {
		public static final Aggregator inst = new Min2();
		@Override public String getSuffix() { return "-Min2"; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			Iterator<BigDecimal> filtered = Iterators.filter(scoreIterator, SignalInstance.isPositive);
			return SignalInstance.toDouble(Iterators.size(filtered) >= 2);
		}
	}
}
