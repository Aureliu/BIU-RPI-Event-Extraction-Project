package edu.cuny.qc.perceptron.similarity_scorer;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import edu.cuny.qc.perceptron.types.FeatureInstance;

public class Aggregator {

	public static Double any(Iterator<Double> scoreIterator) {
		return FeatureInstance.toDouble(Iterators.any(scoreIterator, FeatureInstance.isPositive));
	}
}
