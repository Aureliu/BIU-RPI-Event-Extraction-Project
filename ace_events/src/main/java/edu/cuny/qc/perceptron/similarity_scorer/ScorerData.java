package edu.cuny.qc.perceptron.similarity_scorer;

public class ScorerData {
	public String fullName;
	public String basicName;
	public SignalMechanismSpecIterator scorer;
	public Aggregator aggregator;
	
	public ScorerData(String basicName, SignalMechanismSpecIterator scorer,	Aggregator aggregator) {
		this.basicName = basicName;
		this.scorer = scorer;
		this.aggregator = aggregator;
		
		this.fullName = getFullName();
	}
	
	public String getFullName() {
		return String.format("%s%s", basicName, aggregator.getSuffix());
	}
	
	public String toString() {
		return String.format("%s(%s, %s, %s)", this.getClass().getSimpleName(), fullName, scorer.getTypeName(), aggregator.getClass().getSimpleName());
	}
}
