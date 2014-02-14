package edu.cuny.qc.perceptron.types;

public class FeatureInstance {
	
	public String name;
	public FeatureType type;
	public float score;
	public boolean isPositive;

	public FeatureInstance(String name, FeatureType type, float score, boolean isPositive) {
		this.name = name;
		this.type = type;
		this.score = score;
		this.isPositive = isPositive;
	}

}
