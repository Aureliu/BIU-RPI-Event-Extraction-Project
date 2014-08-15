package edu.cuny.qc.perceptron.core;

public enum ArgOMethod {
	SKIP_O,
	DUPLICATE_BY_ROLE,
	OR_ALL,
	
	//ITERATE //This didn't work out, because it affects Document.getInstancesForSentence(), which Folds calls while loading the sentences and before training, so accomodating to this would require some REALLY REALLY ugly stuff in code
}
