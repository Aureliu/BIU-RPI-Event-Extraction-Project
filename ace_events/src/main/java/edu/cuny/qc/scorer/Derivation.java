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
	
	public String toString() {
		switch(this) {
			case NONE: return ""; 
			case TEXT_ORIG_AND_DERV: return "-withTextDerv";
			case TEXT_ONLY_DERV: return "-onlyTextDerv";
			case SPEC_ORIG_AND_DERV: return "-withSpecDerv";
			case SPEC_ONLY_DERV: return "-onlySpecDerv";
			default: throw new IllegalArgumentException(this.name());
		}
	}
	
	public boolean leftOriginal, leftDerivation, rightOriginal, rightDerivation;
	
	public static final Derivation[] DERVS_NONE = {Derivation.NONE}; 
	public static final Derivation[] DERVS_NONE_AND = {Derivation.NONE, Derivation.TEXT_ORIG_AND_DERV, Derivation.SPEC_ORIG_AND_DERV}; 
	public static final Derivation[] DERVS_NONE_ONLY = {Derivation.NONE, Derivation.TEXT_ONLY_DERV, Derivation.SPEC_ONLY_DERV}; 
	public static final Derivation[] DERVS_ONLY = {Derivation.TEXT_ONLY_DERV, Derivation.SPEC_ONLY_DERV}; 
	public static final Derivation[] DERVS_ALL = {Derivation.NONE, Derivation.TEXT_ORIG_AND_DERV, Derivation.SPEC_ORIG_AND_DERV, Derivation.TEXT_ONLY_DERV, Derivation.SPEC_ONLY_DERV}; 
	public static final Derivation[] DERVS_TEXT_ORIG_AND_DERV = {Derivation.TEXT_ORIG_AND_DERV}; 

}