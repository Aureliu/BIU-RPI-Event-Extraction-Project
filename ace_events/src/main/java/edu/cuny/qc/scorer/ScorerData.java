package edu.cuny.qc.scorer;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

public class ScorerData implements Serializable {
	private static final long serialVersionUID = 1696282253769512311L;
	
	//this is commented out in an attemt to save memory (very long unique string in many-many instances of this classes), although
	//I'm not sure it would help, as the calculate fullName enters as SignalInstance's name, so it will still be around...
	//public String fullName; 
	
	public String basicName;
	public SignalMechanismSpecIterator scorer;
	//public String scorerTypeName;
	public Aggregator aggregator;
	//public String aggregatorTypeName;
	public Deriver deriver;
	//public String deriverTypeName;
	public Derivation derivation;
	public int leftSenseNum;
	public int rightSenseNum;
	public PartOfSpeech specificPos;
	public boolean isSpecIndependent;
	
	public ScorerData(String basicName, SignalMechanismSpecIterator scorer,	Deriver deriver, Derivation derivation, int leftSenseNum, int rightSenseNum, PartOfSpeech specificPos, Aggregator aggregator, boolean isSpecIndependent) {
		//this.scorerTypeName = scorer.getTypeName().intern();
		//this.aggregatorTypeName = aggregator.getTypeName().intern();
		//this.deriverTypeName = deriver.getTypeName().intern();

		if (basicName != null) {
			this.basicName = basicName.intern();
		}
		else {
			this.basicName = scorer.getTypeName();;
		}
		this.scorer = scorer;
		this.aggregator = aggregator;
		this.deriver = deriver;
		this.derivation = derivation;
		this.leftSenseNum = leftSenseNum;
		this.rightSenseNum = rightSenseNum;
		this.specificPos = specificPos;
		
		//this.fullName = getFullName();
		
		this.isSpecIndependent = isSpecIndependent;
	}
	
	//X
	public ScorerData(String basicName, SignalMechanismSpecIterator scorer, Deriver deriver, Derivation derivation, int leftSenseNum, int rightSenseNum, PartOfSpeech specificPos, Aggregator aggregator) {
		this(basicName, scorer, deriver, derivation, leftSenseNum, rightSenseNum, specificPos, aggregator, false);
	}
	
	//X
	public ScorerData(String basicName, SignalMechanismSpecIterator scorer, Deriver deriver, Derivation derivation, Aggregator aggregator) {
		this(basicName, scorer, deriver, derivation, 1, 1, null, aggregator);
	}
	
	//X
	public ScorerData(String basicName, SignalMechanismSpecIterator scorer, Aggregator aggregator) {
		this(basicName, scorer, Deriver.NoDerv.inst, Derivation.NONE, aggregator);
	}
	
	public ScorerData(String basicName, SignalMechanismSpecIterator scorer) {
		this(basicName, scorer, Aggregator.Any.inst);
	}
	
	public String getAggregatorTypeName() {
		return aggregator.getTypeName().intern();
	}
	
	public String getDeriverTypeName() {
		return deriver.getTypeName().intern();
	}
	
	private String numSenseString(int senseNum, String title) {
		switch(senseNum) {
			case -1: return String.format("-%sAllSense", title);
			case 0: return "";
			default: return String.format("-%s%dSense", title, senseNum);
		}
	}
	
	public String getFullName() {
		String posStr="";
		if (specificPos != null) {
			posStr = String.format("-just%s", specificPos);
		}

		// DEBUG
		String checkNull = deriver.getSuffix();
		checkNull = aggregator.getSuffix();
				
		return String.format("%s%s%s%s%s%s%s",
				basicName,
				numSenseString(leftSenseNum, "Text"), numSenseString(rightSenseNum, "Spec"),
				deriver.getSuffix(), derivation, posStr, aggregator.getSuffix()).intern();
	}
	
	public String toString() {
		return String.format("%s(%s)", this.getClass().getSimpleName(), getFullName());
	}
	
	@Override
	public int hashCode() {
	     int hash = new HashCodeBuilder(17, 37).append(basicName).append(deriver).append(aggregator)
	    		 .append(derivation).append(leftSenseNum).append(rightSenseNum).append(specificPos).toHashCode();
	     return hash;
	}
	@Override
	public boolean equals(Object obj) {
	   if (obj == null) { return false; }
	   if (obj == this) { return true; }
	   if (obj.getClass() != getClass()) {
	     return false;
	   }
	   ScorerData rhs = (ScorerData) obj;
	   boolean result = new EqualsBuilder().append(basicName, rhs.basicName).append(deriver, rhs.deriver).append(aggregator, rhs.aggregator)
	    	   .append(derivation, rhs.derivation).append(leftSenseNum, rhs.leftSenseNum).append(rightSenseNum, rhs.rightSenseNum)
	    	   .append(specificPos, rhs.specificPos).isEquals();
	   return result;
	}

}
