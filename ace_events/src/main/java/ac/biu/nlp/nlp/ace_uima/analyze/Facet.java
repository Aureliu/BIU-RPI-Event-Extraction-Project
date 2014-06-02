package ac.biu.nlp.nlp.ace_uima.analyze;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

public class Facet {
	public Facet(BasicNode predicateHead, BasicNode argumentHead) {
		this.predicateHead = predicateHead;
		this.argumentHead = argumentHead;
	}
	
	public BasicNode getPredicateHead() {
		return predicateHead;
	}

	public BasicNode getArgumentHead() {
		return argumentHead;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((argumentHead == null) ? 0 : argumentHead.hashCode());
		result = prime * result
				+ ((predicateHead == null) ? 0 : predicateHead.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Facet other = (Facet) obj;
		if (argumentHead == null) {
			if (other.argumentHead != null) {
				return false;
			}
		} else if (!argumentHead.getInfo().equals(other.argumentHead.getInfo())) {
			return false;
		}
		if (predicateHead == null) {
			if (other.predicateHead != null) {
				return false;
			}
		} else if (!predicateHead.getInfo().equals(other.predicateHead.getInfo())) {
			return false;
		}
		return true;
	}
	
	private BasicNode predicateHead;
	private BasicNode argumentHead;
}
