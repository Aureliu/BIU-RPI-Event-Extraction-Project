package edu.cuny.qc.perceptron.types;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class SignalInstance implements Serializable {
	private static final long serialVersionUID = 6105654845782902773L;
	
	public String name;
	public SignalType type;
	private /*public*/ BigDecimal score;
	public boolean positive;
	public Multimap<String, String> history; //optional, and is not part of the object's identity
	
	static {
		System.err.println("??? SignalInstance: score is currently made private, so that conceptually there is no link between which numbers represent a *signal*'s positiveness, and which numbers represent a *feature*'s positiveness. But in the future, maybe a signal's score will be meaningful, and we would want to somehow use it for the feature's score.");
	}
	
	public static Predicate<BigDecimal> isPositive = new Predicate<BigDecimal>() {
		@Override
		public boolean apply(BigDecimal score) {
			return score.compareTo(SCORE_THRESHOLD) >= 0; // score >= SCORE_THRESHOLD
		}
	};
	
	public static BigDecimal toDouble(boolean positive) {
		if (positive) {
			return POSITIVE_SCORE;
		}
		else {
			return NEGATIVE_SCORE;
		}
	}
	
	public static final BigDecimal SCORE_THRESHOLD = new BigDecimal("0.5");
	public static final BigDecimal POSITIVE_SCORE = new BigDecimal("1.0");
	public static final BigDecimal NEGATIVE_SCORE = new BigDecimal("0.0");//new BigDecimal("-1.0");

	public SignalInstance(String name, SignalType type, BigDecimal score) {
		this.name = name;
		this.type = type;
		this.score = score;
		this.positive = isPositive.apply(score);
	}
	
	public String getPositiveString() {
		return positive ? "T" : "F";
	}
	
	@Override
	public String toString() {
		return String.format("_%s_(%s=%s[%s])", type.toString().toLowerCase(), name, positive, score);
	}

//	public void initHistory() {
//		history = ArrayListMultimap.create();
//	}
//	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (positive ? 1231 : 1237);
		result = prime * result + ((score == null) ? 0 : score.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SignalInstance other = (SignalInstance) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (positive != other.positive)
			return false;
		if (score == null) {
			if (other.score != null)
				return false;
		} else if (!score.equals(other.score))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
