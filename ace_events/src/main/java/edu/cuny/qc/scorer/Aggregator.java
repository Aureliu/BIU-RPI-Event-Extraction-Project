package edu.cuny.qc.scorer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import edu.cuny.qc.perceptron.types.SignalInstance;
import edu.cuny.qc.scorer.Deriver.Join;

public abstract class Aggregator implements Serializable {

	private static final long serialVersionUID = -8253852128019445603L;
	public String getTypeName() {return getClass().getSimpleName(); }
	public abstract String getSuffix();
	public abstract BigDecimal aggregate(Iterator<BigDecimal> scoreIterator);
	
	@Override public int hashCode() {
	     return getClass().getSimpleName().hashCode();
	}
	@Override public boolean equals(Object obj) {
		   if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   return obj.getClass() == getClass();
	}

	public static class Any extends Aggregator {
		private static final long serialVersionUID = 3748054145390188760L;
		//private static final long serialVersionUID = -5751052602451116048L;
		public static final Aggregator inst = new Any();
		@Override public String getSuffix() { return ""; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			return SignalInstance.toDouble(Iterators.any(scoreIterator, SignalInstance.isPositive));
		}
	}
	
	public static class ScanAll extends Aggregator {
		private static final long serialVersionUID = -2009240757598259610L;
		public static final Aggregator inst = new ScanAll();
		private ScanAll() {} //private c-tor
		@Override public String getSuffix() { return "-ScanAll"; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			Iterators.frequency(scoreIterator, null); //just consume all, result is irrelevant
			return BigDecimal.ZERO;
		}
	}
	
	public static class Min2 extends Aggregator {
		private static final long serialVersionUID = -1016053422276007898L;
		//private static final long serialVersionUID = 2977476394909382268L;
		public static final Aggregator inst = new Min2();
		private Min2() {} //private c-tor
		@Override public String getSuffix() { return "-Min2"; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			Iterator<BigDecimal> filtered = Iterators.filter(scoreIterator, SignalInstance.isPositive);
			return SignalInstance.toDouble(Iterators.size(filtered) >= 2);
		}
	}
	
	public static class Min3 extends Aggregator {
		private static final long serialVersionUID = -1704682798441723374L;
		public static final Aggregator inst = new Min3();
		private Min3() {} //private c-tor
		@Override public String getSuffix() { return "-Min3"; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			Iterator<BigDecimal> filtered = Iterators.filter(scoreIterator, SignalInstance.isPositive);
			return SignalInstance.toDouble(Iterators.size(filtered) >= 3);
		}
	}
	
	public static class Min4 extends Aggregator {
		private static final long serialVersionUID = 1305411399791664326L;
		public static final Aggregator inst = new Min4();
		private Min4() {} //private c-tor
		@Override public String getSuffix() { return "-Min4"; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			Iterator<BigDecimal> filtered = Iterators.filter(scoreIterator, SignalInstance.isPositive);
			return SignalInstance.toDouble(Iterators.size(filtered) >= 4);
		}
	}
	
	public static class MinHalf extends Aggregator {
		private static final long serialVersionUID = -4824578162689196373L;
		public static final Aggregator inst = new MinHalf();
		private MinHalf() {} //private c-tor
		@Override public String getSuffix() { return "-MinHalf"; }

		@Override
		public BigDecimal aggregate(Iterator<BigDecimal> scoreIterator) {
			List<BigDecimal> allElements = Lists.newArrayList(scoreIterator);
			Iterator<BigDecimal> filtered = Iterators.filter(allElements.iterator(), SignalInstance.isPositive);
			return SignalInstance.toDouble(Iterators.size(filtered) >= allElements.size()/2);
		}
	}
	
	public static final Aggregator[] ALL_AGGS = {Aggregator.Any.inst, Aggregator.Min2.inst, Aggregator.Min3.inst, /*Aggregator.Min4.inst, Aggregator.MinHalf.inst*/};
	public static final Aggregator[] AGG_ANY = {Aggregator.Any.inst};
	public static final Aggregator[] AGG_MIN2 = {Aggregator.Min2.inst};
	public static final Aggregator[] AGG_MIN3 = {Aggregator.Min3.inst};
	public static final Aggregator[] AGG_ANY_MIN2 = {Aggregator.Any.inst, Aggregator.Min2.inst};
	public static final Aggregator[] AGG_MIN2_MIN3 = {Aggregator.Min2.inst, Aggregator.Min3.inst};

}
