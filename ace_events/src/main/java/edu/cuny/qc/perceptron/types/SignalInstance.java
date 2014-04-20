package edu.cuny.qc.perceptron.types;

import java.math.BigDecimal;

import com.google.common.base.Predicate;

public class SignalInstance {
	
	public String name;
	public SignalType type;
	public BigDecimal score;
	public boolean positive;
	
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
	
	@Override
	public String toString() {
		return String.format("_%s_(%s=%s[%s])", type.toString().toLowerCase(), name, positive, score);
	}
}
