package edu.cuny.qc.scorer;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

public class BasicRulesQuery {
	
	public String lLemma, rLemma;
	public PartOfSpeech lPos, rPos;
	
	public BasicRulesQuery(String lLemma, String rLemma, PartOfSpeech lPos,
			PartOfSpeech rPos) {
		this.lLemma = lLemma;
		this.rLemma = rLemma;
		this.lPos = lPos;
		this.rPos = rPos;
	}
	
	@Override
	public int hashCode() {
	     return new HashCodeBuilder(17, 37).append(lLemma).append(rLemma).append(lPos).append(rPos).toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
	   if (obj == null) { return false; }
	   if (obj == this) { return true; }
	   if (obj.getClass() != getClass()) { return false; }
	   BasicRulesQuery rhs = (BasicRulesQuery) obj;
	   return new EqualsBuilder().append(lLemma, rhs.lLemma).append(rLemma, rhs.rLemma).append(lPos, rhs.lPos).append(rPos, rhs.rPos).isEquals();
	}
	public String toString() {
		return String.format("%s(%s/%s-->%s/%s)", BasicRulesQuery.class.getSimpleName(), lLemma, lPos, rLemma, rPos);
	}
}
