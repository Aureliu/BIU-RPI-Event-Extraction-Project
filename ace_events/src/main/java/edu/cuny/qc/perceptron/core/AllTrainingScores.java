package edu.cuny.qc.perceptron.core;

import java.util.Map;

import com.google.common.collect.Maps;

import edu.cuny.qc.perceptron.core.Evaluator.Score;
import edu.cuny.qc.perceptron.types.FeatureVector;

public class AllTrainingScores {
	public ScoresList train = new ScoresList();
	public ScoresList dev = new ScoresList();
	
	public static class ScoresList {
		public static final int FAKE_ITER = -1;
		public Map<Integer, Score> scores = Maps.newLinkedHashMap();
		public Score bestScore = new Score(FAKE_ITER);
		public FeatureVector bestWeights = null;
		public FeatureVector bestAvgWeights = null;
	}
}
