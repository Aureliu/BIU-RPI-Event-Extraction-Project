package edu.cuny.qc.scorer;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

public class ScorerData implements Serializable {
	private static final long serialVersionUID = 1696282253769512311L;
	
	//this is commented out in an attempt to save memory (very long unique string in many-many instances of this classes), although
	//I'm not sure it would help, as the calculate fullName enters as SignalInstance's name, so it will still be around...
	//public String fullName; 
	
	private String basicName;
	private SignalMechanismSpecIterator<?> scorer;
	//public String scorerTypeName;
	private Aggregator elementAggregator;
	private Aggregator usageSampleAggregator;
	//public String aggregatorTypeName;
	private Deriver deriver;
	//public String deriverTypeName;
	private Derivation derivation;
	private int leftSenseNum;
	private int rightSenseNum;
	private PartOfSpeech specificPos;
	//private boolean isSpecIndependent;
	
	private int hash;
	private boolean hasHash = false;
	
	public ScorerData(String basicName, SignalMechanismSpecIterator<?> scorer,	Deriver deriver, Derivation derivation, int leftSenseNum, int rightSenseNum, PartOfSpeech specificPos, Aggregator elementAggregator, Aggregator usageSampleAggregator, boolean isSpecIndependent) {
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
		this.elementAggregator = elementAggregator;
		this.usageSampleAggregator = usageSampleAggregator;
		this.deriver = deriver;
		this.derivation = derivation;
		this.leftSenseNum = leftSenseNum;
		this.rightSenseNum = rightSenseNum;
		this.specificPos = specificPos;
		
		//this.fullName = getFullName();
		
		//this.isSpecIndependent = isSpecIndependent;
	}
	
	//X
	public ScorerData(String basicName, SignalMechanismSpecIterator<?> scorer, Deriver deriver, Derivation derivation, int leftSenseNum, int rightSenseNum, PartOfSpeech specificPos, Aggregator elementAggregator, Aggregator usageSampleAggregator) {
		this(basicName, scorer, deriver, derivation, leftSenseNum, rightSenseNum, specificPos, elementAggregator, usageSampleAggregator, false);
	}
	
	//X
	public ScorerData(String basicName, SignalMechanismSpecIterator<?> scorer, Deriver deriver, Derivation derivation, int leftSenseNum, int rightSenseNum, PartOfSpeech specificPos, Aggregator elementAggregator) {
		this(basicName, scorer, deriver, derivation, leftSenseNum, rightSenseNum, specificPos, elementAggregator, Aggregator.Any.inst);
	}
	
	//X
	public ScorerData(String basicName, SignalMechanismSpecIterator<?> scorer, boolean isSpecIndependent) {
		this(basicName, scorer, Deriver.NoDerv.inst, Derivation.NONE, 1, 1, null, Aggregator.Any.inst, Aggregator.Any.inst, isSpecIndependent);
	}
	
	//X
	public ScorerData(String basicName, SignalMechanismSpecIterator<?> scorer, Deriver deriver, Derivation derivation, Aggregator elementAggregator) {
		this(basicName, scorer, deriver, derivation, 1, 1, null, elementAggregator);
	}
	
	//X
	public ScorerData(String basicName, SignalMechanismSpecIterator<?> scorer, Aggregator elementAggregator) {
		this(basicName, scorer, Deriver.NoDerv.inst, Derivation.NONE, elementAggregator);
	}
	
	public ScorerData(String basicName, SignalMechanismSpecIterator<?> scorer) {
		this(basicName, scorer, Aggregator.Any.inst);
	}
	
	public String getElementAggregatorTypeName() {
		return elementAggregator.getTypeName().intern();
	}
	
	public String getUsageSampleAggregatorTypeName() {
		return usageSampleAggregator.getTypeName().intern();
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
//		String checkNull = deriver.getSuffix();
//		checkNull = elementAggregator.getSuffix();
				
		return String.format("%s%s%s%s%s%s%s%s",
				basicName,
				numSenseString(leftSenseNum, "Text"), numSenseString(rightSenseNum, "Spec"),
				deriver.getSuffix(), derivation, posStr, elementAggregator.getSuffix(),
				usageSampleAggregator.getSuffix()).intern();
	}
	
	public String toString() {
		return String.format("%s(%s)", this.getClass().getSimpleName(), getFullName());
	}
	
//	@Override
//	public int hashCode() {
//	     return new HashCodeBuilder(17, 37).append(basicName).append(deriver).append(elementAggregator).append(usageSampleAggregator)
//	    		 .append(derivation).append(leftSenseNum).append(rightSenseNum).append(specificPos).toHashCode();
//	}
//	@Override
//	public boolean equals(Object obj) {
//	   if (obj == null) { return false; }
//	   if (obj == this) { return true; }
//	   if (obj.getClass() != getClass()) {
//	     return false;
//	   }
//	   ScorerData rhs = (ScorerData) obj;
//	   return new EqualsBuilder().append(basicName, rhs.basicName).append(deriver, rhs.deriver).append(elementAggregator, rhs.elementAggregator)
//			   .append(usageSampleAggregator, rhs.usageSampleAggregator).append(derivation, rhs.derivation).append(leftSenseNum, rhs.leftSenseNum)
//			   .append(rightSenseNum, rhs.rightSenseNum).append(specificPos, rhs.specificPos).isEquals();
//	}

	public String getBasicName() {
		return basicName;
	}

//	public void setBasicName(String basicName) {
//		this.basicName = basicName;
//	}

	public Derivation getDerivation() {
		return derivation;
	}

//	public void setDerivation(Derivation derivation) {
//		this.derivation = derivation;
//	}

	public int getLeftSenseNum() {
		return leftSenseNum;
	}

//	public void setLeftSenseNum(int leftSenseNum) {
//		this.leftSenseNum = leftSenseNum;
//	}

	public int getRightSenseNum() {
		return rightSenseNum;
	}

//	public void setRightSenseNum(int rightSenseNum) {
//		this.rightSenseNum = rightSenseNum;
//	}

	public Deriver getDeriver() {
		return deriver;
	}

//	public void setDeriver(Deriver deriver) {
//		this.deriver = deriver;
//	}

	public Aggregator getElementAggregator() {
		return elementAggregator;
	}

//	public void setElementAggregator(Aggregator elementAggregator) {
//		this.elementAggregator = elementAggregator;
//	}

	public SignalMechanismSpecIterator<?> getScorer() {
		return scorer;
	}

//	public void setScorer(SignalMechanismSpecIterator<?> scorer) {
//		this.scorer = scorer;
//	}

	public PartOfSpeech getSpecificPos() {
		return specificPos;
	}

	@Override
	public int hashCode() {
		if (!hasHash) {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((basicName == null) ? 0 : basicName.hashCode());
			result = prime * result
					+ ((derivation == null) ? 0 : derivation.name().hashCode());
			result = prime * result + ((deriver == null) ? 0 : deriver.hashCode());
			result = prime
					* result
					+ ((elementAggregator == null) ? 0 : elementAggregator
							.hashCode());
			result = prime * result + leftSenseNum;
			result = prime * result + rightSenseNum;
			result = prime * result
					+ ((specificPos == null) ? 0 : specificPos.hashCode());
			result = prime
					* result
					+ ((usageSampleAggregator == null) ? 0 : usageSampleAggregator
							.hashCode());
			hash = result;
			hasHash = true;
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScorerData other = (ScorerData) obj;
		if (basicName == null) {
			if (other.basicName != null)
				return false;
		} else if (!basicName.equals(other.basicName))
			return false;
		if (!derivation.name().equals(derivation.name()))
			return false;
		if (deriver == null) {
			if (other.deriver != null)
				return false;
		} else if (!deriver.equals(other.deriver))
			return false;
		if (elementAggregator == null) {
			if (other.elementAggregator != null)
				return false;
		} else if (!elementAggregator.equals(other.elementAggregator))
			return false;
		if (leftSenseNum != other.leftSenseNum)
			return false;
		if (rightSenseNum != other.rightSenseNum)
			return false;
		if (specificPos == null) {
			if (other.specificPos != null)
				return false;
		} else if (!specificPos.equals(other.specificPos))
			return false;
		if (usageSampleAggregator == null) {
			if (other.usageSampleAggregator != null)
				return false;
		} else if (!usageSampleAggregator.equals(other.usageSampleAggregator))
			return false;
		return true;
	}

//	public void setSpecificPos(PartOfSpeech specificPos) {
//		this.specificPos = specificPos;
//	}

}
