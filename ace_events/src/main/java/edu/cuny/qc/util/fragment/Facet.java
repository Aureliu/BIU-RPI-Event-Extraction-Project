package edu.cuny.qc.util.fragment;

import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionAnchor;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

public class Facet {
	public Facet(BasicNode predicateHead, BasicNode argumentHead, Annotation /*EventMentionAnchor*/ predicateHeadAnno, EventMentionArgument argAnno, Sentence sentence) {
		this.predicateHead = predicateHead;
		this.argumentHead = argumentHead;
		this.predicateHeadAnno = predicateHeadAnno;
		this.argAnno = argAnno;
		this.sentence = sentence;
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
	public Annotation /*EventMentionAnchor*/ predicateHeadAnno;
	public EventMentionArgument argAnno;
	public Sentence sentence;
}
