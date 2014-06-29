package edu.cuny.qc.scorer;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ScorerData implements Serializable {
	private static final long serialVersionUID = 1696282253769512311L;
	
	public String fullName;
	public String basicName;
	public transient SignalMechanismSpecIterator scorer;
	public String scorerTypeName;
	public transient Aggregator aggregator;
	public String aggregatorTypeName;
	public transient Deriver deriver;
	public String deriverTypeName;
	public Derivation derivation;
	public int leftSenseNum;
	public int rightSenseNum;
	public transient boolean isSpecIndependent;
	
	public ScorerData(String basicName, SignalMechanismSpecIterator scorer,	Aggregator aggregator, Deriver deriver, Derivation derivation, int leftSenseNum, int rightSenseNum, boolean isSpecIndependent) {
		this.basicName = basicName.intern();
		this.scorer = scorer;
		this.aggregator = aggregator;
		this.deriver = deriver;
		this.derivation = derivation;
		this.leftSenseNum = leftSenseNum;
		this.rightSenseNum = rightSenseNum;
		
		this.fullName = getFullName().intern();
		this.scorerTypeName = scorer.getTypeName().intern();
		this.aggregatorTypeName = aggregator.getTypeName().intern();
		this.deriverTypeName = deriver.getTypeName().intern();
		
		this.isSpecIndependent = isSpecIndependent;
	}
	
	public ScorerData(SignalMechanismSpecIterator scorer,	Aggregator aggregator) {
		this(scorer.getTypeName(), scorer, aggregator);
	}
	
	public String getFullName() {
		return String.format("%s%s", basicName, aggregator.getSuffix());
	}
	
	public String toString() {
		return String.format("%s(%s, %s, %s)", this.getClass().getSimpleName(), fullName, scorerTypeName, aggregatorTypeName);
	}
	
	@Override
	public int hashCode() {
	     return new HashCodeBuilder(17, 37).append(fullName).append(basicName)
	    		 .append(scorerTypeName).append(aggregatorTypeName).toHashCode();  // for these guys only take their type names, seems to be enough
	}
	@Override
	public boolean equals(Object obj) {
	   if (obj == null) { return false; }
	   if (obj == this) { return true; }
	   if (obj.getClass() != getClass()) {
	     return false;
	   }
	   ScorerData rhs = (ScorerData) obj;
	   return new EqualsBuilder().append(fullName, rhs.fullName).append(basicName, rhs.basicName)
			   .append(scorerTypeName, rhs.scorerTypeName).append(aggregatorTypeName, rhs.aggregatorTypeName).isEquals(); // for these guys only take their type names, seems to be enough
	}

}
