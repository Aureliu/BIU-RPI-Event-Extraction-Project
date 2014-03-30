package edu.cuny.qc.perceptron.similarity_scorer;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import edu.cuny.qc.perceptron.types.SignalInstance;

public class Aggregator {

	public static Double any(Iterator<Double> scoreIterator) {
		return SignalInstance.toDouble(Iterators.any(scoreIterator, SignalInstance.isPositive));
	}
}
