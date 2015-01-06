package edu.cuny.qc.scorer;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

/**
 * Can represent:
 * <ol>
 * <li> A query of a single lemma/pos pair. In this case, the data will be on the left side, and the right side is ignored.
 * <li> A query of two lemma/pos pairs.
 * <li> A result - also of one lemma/pos pair (again on left side), or of two.
 * </ol>
 */
public class BasicRulesQuery {
	
	private String lLemma, rLemma;
	private PartOfSpeech lPos, rPos;
	private int hash;
	private boolean hasHash = false;
	
	public BasicRulesQuery(String lLemma, PartOfSpeech lPos, String rLemma, PartOfSpeech rPos) {
		this.lLemma = lLemma;
		this.rLemma = rLemma;
		this.lPos = lPos;
		this.rPos = rPos;
	}
	
//	@Override
//	public int hashCode1() {
//		if (!hasHash) {
//			hash = new HashCodeBuilder(17, 37).append(lLemma).append(rLemma).append(lPos).append(rPos).toHashCode();
//			hasHash = true;
//		}
//	     return hash;
//	}
//	
//	@Override
//	public boolean equals1(Object obj) {
//	   if (obj == null) { return false; }
//	   if (obj == this) { return true; }
//	   if (obj.getClass() != getClass()) { return false; }
//	   BasicRulesQuery rhs = (BasicRulesQuery) obj;
//	   return new EqualsBuilder().append(lLemma, rhs.lLemma).append(rLemma, rhs.rLemma).append(lPos, rhs.lPos).append(rPos, rhs.rPos).isEquals();
//	}
	public String toString() {
		return String.format("%s(%s/%s-->%s/%s)", BasicRulesQuery.class.getSimpleName(), lLemma, lPos, rLemma, rPos);
	}

	public PartOfSpeech getlPos() {
		return lPos;
	}

//	public void setlPos(PartOfSpeech lPos) {
//		this.lPos = lPos;
//	}

	public String getlLemma() {
		return lLemma;
	}

//	public void setlLemma(String lLemma) {
//		this.lLemma = lLemma;
//	}

	public String getrLemma() {
		return rLemma;
	}

//	public void setrLemma(String rLemma) {
//		this.rLemma = rLemma;
//	}

	public PartOfSpeech getrPos() {
		return rPos;
	}

	@Override
	public int hashCode() {
		if (!hasHash) {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((lLemma == null) ? 0 : lLemma.hashCode());
			result = prime * result + ((lPos == null) ? 0 : lPos.hashCode());
			result = prime * result + ((rLemma == null) ? 0 : rLemma.hashCode());
			result = prime * result + ((rPos == null) ? 0 : rPos.hashCode());
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
		BasicRulesQuery other = (BasicRulesQuery) obj;
		if (lLemma == null) {
			if (other.lLemma != null)
				return false;
		} else if (!lLemma.equals(other.lLemma))
			return false;
		if (lPos == null) {
			if (other.lPos != null)
				return false;
		} else if (!lPos.equals(other.lPos))
			return false;
		if (rLemma == null) {
			if (other.rLemma != null)
				return false;
		} else if (!rLemma.equals(other.rLemma))
			return false;
		if (rPos == null) {
			if (other.rPos != null)
				return false;
		} else if (!rPos.equals(other.rPos))
			return false;
		return true;
	}

//	public void setrPos(PartOfSpeech rPos) {
//		this.rPos = rPos;
//	}
}
