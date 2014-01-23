package edu.cuny.qc.perceptron.similarity_scorer;

public enum TargetMarker {
	;
	
	public enum Spec {
		PRED_NAME,
		PRED_SEEDS,
		ARG_TYPES,
		ARG_EXAMPLES,
		ARG_ROLE,
		USAGE_SAMPLES,
	}
	
	public enum Text {
		PRED,
		ARG,
		PRED_OR_ARG,
	}
}
