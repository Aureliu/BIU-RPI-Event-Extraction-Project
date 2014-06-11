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
	
	public ScorerData(String basicName, SignalMechanismSpecIterator scorer,	Aggregator aggregator) {
		this.basicName = basicName;
		this.scorer = scorer;
		this.aggregator = aggregator;
		
		this.fullName = getFullName();
		this.scorerTypeName = scorer.getTypeName();
		this.aggregatorTypeName = aggregator.getTypeName();
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
