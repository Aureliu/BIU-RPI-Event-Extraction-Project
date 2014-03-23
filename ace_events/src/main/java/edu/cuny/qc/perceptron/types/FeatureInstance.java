package edu.cuny.qc.perceptron.types;

import com.google.common.base.Predicate;

public class FeatureInstance {
	
	public String name;
	public FeatureType type;
	public double score;
	public boolean positive;
	
	public static Predicate<Double> isPositive = new Predicate<Double>() {
		@Override
		public boolean apply(Double score) {
			return score >= SCORE_THRESHOLD;
		}
	};
	
	public static Double toDouble(boolean positive) {
		if (positive) {
			return POSITIVE_SCORE;
		}
		else {
			return NEGATIVE_SCORE;
		}
	}
	
	public static final double SCORE_THRESHOLD = 0.5;
	public static final double POSITIVE_SCORE = 1.0;
	public static final double NEGATIVE_SCORE = 0.0;

	public FeatureInstance(String name, FeatureType type, double score) {
		this.name = name;
		this.type = type;
		this.score = score;
		this.positive = isPositive.apply(score);
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s=%s[%s])", type, name, positive, score);
	}
}
