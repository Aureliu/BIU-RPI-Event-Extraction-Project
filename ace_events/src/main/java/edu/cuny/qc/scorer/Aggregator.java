package edu.cuny.qc.scorer;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import edu.cuny.qc.perceptron.types.SignalInstance;

public abstract class Aggregator /*implements Serializable*/ {
	//private static final long serialVersionUID = -2650084338758293108L;

	public String getTypeName() {return getClass().getSimpleName(); }
	public abstract String getSuffix();
	public abstract BigDecimal aggregate(Iterator<BigDecimal> scoreIterator);
	
	public static class Any extends Aggregator {
		//private static final long serialVersionUID = -5751052602451116048L;
		public static final Aggregator inst = new Any();
		@Override public String getSuffix() { return ""; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			return SignalInstance.toDouble(Iterators.any(scoreIterator, SignalInstance.isPositive));
		}
	}
	
	public static class ScanAll extends Aggregator {
		public static final Aggregator inst = new ScanAll();
		@Override public String getSuffix() { return "-ScanAll"; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			Iterators.frequency(scoreIterator, null); //just consume all, result is irrelevant
			return BigDecimal.ZERO;
		}
	}
	
	public static class Min2 extends Aggregator {
		//private static final long serialVersionUID = 2977476394909382268L;
		public static final Aggregator inst = new Min2();
		@Override public String getSuffix() { return "-Min2"; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			Iterator<BigDecimal> filtered = Iterators.filter(scoreIterator, SignalInstance.isPositive);
			return SignalInstance.toDouble(Iterators.size(filtered) >= 2);
		}
	}
	
	public static class Min3 extends Aggregator {
		public static final Aggregator inst = new Min3();
		@Override public String getSuffix() { return "-Min3"; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			Iterator<BigDecimal> filtered = Iterators.filter(scoreIterator, SignalInstance.isPositive);
			return SignalInstance.toDouble(Iterators.size(filtered) >= 3);
		}
	}
	
	public static class Min4 extends Aggregator {
		public static final Aggregator inst = new Min4();
		@Override public String getSuffix() { return "-Min4"; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			Iterator<BigDecimal> filtered = Iterators.filter(scoreIterator, SignalInstance.isPositive);
			return SignalInstance.toDouble(Iterators.size(filtered) >= 4);
		}
	}
	
	public static class MinHalf extends Aggregator {
		public static final Aggregator inst = new MinHalf();
		@Override public String getSuffix() { return "-MinHalf"; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			List<BigDecimal> allElements = Lists.newArrayList(scoreIterator);
			Iterator<BigDecimal> filtered = Iterators.filter(allElements.iterator(), SignalInstance.isPositive);
			return SignalInstance.toDouble(Iterators.size(filtered) >= allElements.size()/2);
		}
	}
}
