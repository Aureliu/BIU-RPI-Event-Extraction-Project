package edu.cuny.qc.scorer;

public enum Derivation {
	NONE(true, false, true, false),
	TEXT_ORIG_AND_DERV(true, true, true, false),
	TEXT_ONLY_DERV(false, true, true, false),
	SPEC_ORIG_AND_DERV(true, false, true, true),
	SPEC_ONLY_DERV(true, false, false, true);
	// everything with both text and spec derv, sounds a little too far
	
	private Derivation(boolean leftOriginal, boolean leftDerivation, boolean rightOriginal, boolean rightDerivation) {
		this.leftOriginal = leftOriginal;
		this.leftDerivation = leftDerivation;
		this.rightOriginal = rightOriginal;
		this.rightDerivation = rightDerivation;
	}
	
	public boolean leftOriginal, leftDerivation, rightOriginal, rightDerivation;
}