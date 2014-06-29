package edu.cuny.qc.scorer;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;

public class FullRulesQuery {
	public Set<WordNetRelation> relations;
	public int length;
	public Juxtaposition juxt;
	//public Derivation derv;
	//public SynsetScope synsetScope;
	public int leftSenseNum;
	public int rightSenseNum;
	//public LengthType lenType;
	public BasicRulesQuery basicQuery;

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
	
	@Override
	public int hashCode() {
	     return new HashCodeBuilder(17, 37).append(relations).append(length).append(juxt).append(leftSenseNum).append(rightSenseNum).append(basicQuery).toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
	   if (obj == null) { return false; }
	   if (obj == this) { return true; }
	   if (obj.getClass() != getClass()) { return false; }
	   FullRulesQuery rhs = (FullRulesQuery) obj;
	   return new EqualsBuilder().append(relations, rhs.relations).append(length, rhs.length).append(juxt, rhs.juxt)
			   .append(leftSenseNum, rhs.leftSenseNum).append(rightSenseNum, rhs.rightSenseNum).append(basicQuery, rhs.basicQuery).isEquals();
	}
}
