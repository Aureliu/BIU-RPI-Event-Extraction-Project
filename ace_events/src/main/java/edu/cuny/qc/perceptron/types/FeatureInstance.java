package edu.cuny.qc.perceptron.types;

public class FeatureInstance {
	
	public String name;
	public FeatureType type;
	public double score;
	public boolean isPositive;
	
	public static final double POSITIVE_THRESHOLD = 0.5;

	public FeatureInstance(String name, FeatureType type, double score) {
		this.name = name;
		this.type = type;
		this.score = score;
		this.isPositive = calcPositive(score);
	}
	
	private boolean calcPositive(double score) {
		return score >= POSITIVE_THRESHOLD;
	}

}
