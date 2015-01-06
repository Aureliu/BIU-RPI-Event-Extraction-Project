package edu.cuny.qc.scorer;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;

public class FullRulesQuery {
	private Set<WordNetRelation> relations;
	private int length;
	private Juxtaposition juxt;
	//public Derivation derv;
	//public SynsetScope synsetScope;
	private int leftSenseNum;
	private int rightSenseNum;
	//public LengthType lenType;
	private BasicRulesQuery basicQuery;
	
	private int hash;
	private boolean hasHash = false;

	public FullRulesQuery(Set<WordNetRelation> relations, int length, Juxtaposition juxt, int leftSenseNum, int rightSenseNum, BasicRulesQuery basicQuery) {
		this.relations = relations;
		this.length = length;
		this.juxt = juxt;
		this.leftSenseNum = leftSenseNum;
		this.rightSenseNum = rightSenseNum;
		this.basicQuery = basicQuery;
	}

	public FullRulesQuery(int leftSenseNum, BasicRulesQuery basicQuery) {
		this(null, 1, null, leftSenseNum, 0, basicQuery);
	}
	
//	@Override
//	public int hashCode() {
//	     return new HashCodeBuilder(17, 37).append(relations).append(length).append(juxt).append(leftSenseNum).append(rightSenseNum).append(basicQuery).toHashCode();
//	}
//	
//	@Override
//	public boolean equals(Object obj) {
//	   if (obj == null) { return false; }
//	   if (obj == this) { return true; }
//	   if (obj.getClass() != getClass()) { return false; }
//	   FullRulesQuery rhs = (FullRulesQuery) obj;
//	   return new EqualsBuilder().append(relations, rhs.relations).append(length, rhs.length).append(juxt, rhs.juxt)
//			   .append(leftSenseNum, rhs.leftSenseNum).append(rightSenseNum, rhs.rightSenseNum).append(basicQuery, rhs.basicQuery).isEquals();
//	}

	public BasicRulesQuery getBasicQuery() {
		return basicQuery;
	}

//	public void setBasicQuery(BasicRulesQuery basicQuery) {
//		this.basicQuery = basicQuery;
//	}

	public Juxtaposition getJuxt() {
		return juxt;
	}

//	public void setJuxt(Juxtaposition juxt) {
//		this.juxt = juxt;
//	}

	public int getLeftSenseNum() {
		return leftSenseNum;
	}

//	public void setLeftSenseNum(int leftSenseNum) {
//		this.leftSenseNum = leftSenseNum;
//	}

	public int getLength() {
		return length;
	}

//	public void setLength(int length) {
//		this.length = length;
//	}

	public int getRightSenseNum() {
		return rightSenseNum;
	}

//	public void setRightSenseNum(int rightSenseNum) {
//		this.rightSenseNum = rightSenseNum;
//	}

	public Set<WordNetRelation> getRelations() {
		return relations;
	}

	@Override
	public int hashCode() {
		if (!hasHash) { 
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((basicQuery == null) ? 0 : basicQuery.hashCode());
			result = prime * result + ((juxt == null) ? 0 : juxt.hashCode());
			result = prime * result + leftSenseNum;
			result = prime * result + length;
			result = prime * result
					+ ((relations == null) ? 0 : relations.hashCode());
			result = prime * result + rightSenseNum;
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
		FullRulesQuery other = (FullRulesQuery) obj;
		if (relations == null) {
			if (other.relations != null)
				return false;
		} else if (!relations.equals(other.relations))
			return false;
		if (basicQuery == null) {
			if (other.basicQuery != null)
				return false;
		} else if (!basicQuery.equals(other.basicQuery))
			return false;
		if (length != other.length)
			return false;
		if (juxt != other.juxt)
			return false;
		if (leftSenseNum != other.leftSenseNum)
			return false;
		if (rightSenseNum != other.rightSenseNum)
			return false;
		return true;
	}

//	public void setRelations(Set<WordNetRelation> relations) {
//		this.relations = relations;
//	}
}
