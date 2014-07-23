package edu.cuny.qc.perceptron.core;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

import edu.cuny.qc.perceptron.types.SentenceInstance;

public enum SentenceSortingMethod {
	DOC_SENT_SPEC(new Comparator<SentenceInstance>() {
		@Override public int compare(SentenceInstance inst1, SentenceInstance inst2) {
			return new CompareToBuilder().append(inst1.doc.docLine, inst2.doc.docLine).append(inst1.sentID, inst2.sentID).append(inst1.specLetter, inst2.specLetter).toComparison();
		}
	}),
	DOC_SPEC_SENT(new Comparator<SentenceInstance>() {
		@Override public int compare(SentenceInstance inst1, SentenceInstance inst2) {
			return new CompareToBuilder().append(inst1.doc.docLine, inst2.doc.docLine).append(inst1.specLetter, inst2.specLetter).append(inst1.sentID, inst2.sentID).toComparison();
		}
	}),
	SPEC_DOC_SENT(new Comparator<SentenceInstance>() {
		@Override public int compare(SentenceInstance inst1, SentenceInstance inst2) {
			return new CompareToBuilder().append(inst1.specLetter, inst2.specLetter).append(inst1.doc.docLine, inst2.doc.docLine).append(inst1.sentID, inst2.sentID).toComparison();
		}
	});
	
	// Technically there are 3 more option (e.g. SENT_DOC_SPEC), but they don't preserve the order of doc-sent, which doesn't seem to make much sense or be effective
	
	private SentenceSortingMethod(Comparator<SentenceInstance> comparator) {
		this.comparator = comparator;
	}
	
	public Comparator<SentenceInstance> comparator;
}
