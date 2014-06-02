
/* First created by JCasGen Tue Jul 09 16:21:26 IDT 2013 */
package ac.biu.nlp.nlp.ace_uima.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** 
 * Updated by JCasGen Mon Jul 15 00:03:28 IDT 2013
 * @generated */
public class Timex2_Type extends BasicArgument_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Timex2_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Timex2_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Timex2(addr, Timex2_Type.this);
  			   Timex2_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Timex2(addr, Timex2_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Timex2.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ace_uima.uima.Timex2");



  /** @generated */
  final Feature casFeat_VAL;
  /** @generated */
  final int     casFeatCode_VAL;
  /** @generated */ 
  public String getVAL(int addr) {
        if (featOkTst && casFeat_VAL == null)
      jcas.throwFeatMissing("VAL", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return ll_cas.ll_getStringValue(addr, casFeatCode_VAL);
  }
  /** @generated */    
  public void setVAL(int addr, String v) {
        if (featOkTst && casFeat_VAL == null)
      jcas.throwFeatMissing("VAL", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    ll_cas.ll_setStringValue(addr, casFeatCode_VAL, v);}
    
  
 
  /** @generated */
  final Feature casFeat_ANCHOR_VAL;
  /** @generated */
  final int     casFeatCode_ANCHOR_VAL;
  /** @generated */ 
  public String getANCHOR_VAL(int addr) {
        if (featOkTst && casFeat_ANCHOR_VAL == null)
      jcas.throwFeatMissing("ANCHOR_VAL", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ANCHOR_VAL);
  }
  /** @generated */    
  public void setANCHOR_VAL(int addr, String v) {
        if (featOkTst && casFeat_ANCHOR_VAL == null)
      jcas.throwFeatMissing("ANCHOR_VAL", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    ll_cas.ll_setStringValue(addr, casFeatCode_ANCHOR_VAL, v);}
    
  
 
  /** @generated */
  final Feature casFeat_ANCHOR_DIR;
  /** @generated */
  final int     casFeatCode_ANCHOR_DIR;
  /** @generated */ 
  public String getANCHOR_DIR(int addr) {
        if (featOkTst && casFeat_ANCHOR_DIR == null)
      jcas.throwFeatMissing("ANCHOR_DIR", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ANCHOR_DIR);
  }
  /** @generated */    
  public void setANCHOR_DIR(int addr, String v) {
        if (featOkTst && casFeat_ANCHOR_DIR == null)
      jcas.throwFeatMissing("ANCHOR_DIR", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    ll_cas.ll_setStringValue(addr, casFeatCode_ANCHOR_DIR, v);}
    
  
 
  /** @generated */
  final Feature casFeat_COMMENT;
  /** @generated */
  final int     casFeatCode_COMMENT;
  /** @generated */ 
  public String getCOMMENT(int addr) {
        if (featOkTst && casFeat_COMMENT == null)
      jcas.throwFeatMissing("COMMENT", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return ll_cas.ll_getStringValue(addr, casFeatCode_COMMENT);
  }
  /** @generated */    
  public void setCOMMENT(int addr, String v) {
        if (featOkTst && casFeat_COMMENT == null)
      jcas.throwFeatMissing("COMMENT", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    ll_cas.ll_setStringValue(addr, casFeatCode_COMMENT, v);}
    
  
 
  /** @generated */
  final Feature casFeat_MOD;
  /** @generated */
  final int     casFeatCode_MOD;
  /** @generated */ 
  public String getMOD(int addr) {
        if (featOkTst && casFeat_MOD == null)
      jcas.throwFeatMissing("MOD", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return ll_cas.ll_getStringValue(addr, casFeatCode_MOD);
  }
  /** @generated */    
  public void setMOD(int addr, String v) {
        if (featOkTst && casFeat_MOD == null)
      jcas.throwFeatMissing("MOD", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    ll_cas.ll_setStringValue(addr, casFeatCode_MOD, v);}
    
  
 
  /** @generated */
  final Feature casFeat_NON_SPECIFIC;
  /** @generated */
  final int     casFeatCode_NON_SPECIFIC;
  /** @generated */ 
  public String getNON_SPECIFIC(int addr) {
        if (featOkTst && casFeat_NON_SPECIFIC == null)
      jcas.throwFeatMissing("NON_SPECIFIC", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return ll_cas.ll_getStringValue(addr, casFeatCode_NON_SPECIFIC);
  }
  /** @generated */    
  public void setNON_SPECIFIC(int addr, String v) {
        if (featOkTst && casFeat_NON_SPECIFIC == null)
      jcas.throwFeatMissing("NON_SPECIFIC", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    ll_cas.ll_setStringValue(addr, casFeatCode_NON_SPECIFIC, v);}
    
  
 
  /** @generated */
  final Feature casFeat_SET;
  /** @generated */
  final int     casFeatCode_SET;
  /** @generated */ 
  public String getSET(int addr) {
        if (featOkTst && casFeat_SET == null)
      jcas.throwFeatMissing("SET", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SET);
  }
  /** @generated */    
  public void setSET(int addr, String v) {
        if (featOkTst && casFeat_SET == null)
      jcas.throwFeatMissing("SET", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    ll_cas.ll_setStringValue(addr, casFeatCode_SET, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Timex2_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_VAL = jcas.getRequiredFeatureDE(casType, "VAL", "uima.cas.String", featOkTst);
    casFeatCode_VAL  = (null == casFeat_VAL) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_VAL).getCode();

 
    casFeat_ANCHOR_VAL = jcas.getRequiredFeatureDE(casType, "ANCHOR_VAL", "uima.cas.String", featOkTst);
    casFeatCode_ANCHOR_VAL  = (null == casFeat_ANCHOR_VAL) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ANCHOR_VAL).getCode();

 
    casFeat_ANCHOR_DIR = jcas.getRequiredFeatureDE(casType, "ANCHOR_DIR", "uima.cas.String", featOkTst);
    casFeatCode_ANCHOR_DIR  = (null == casFeat_ANCHOR_DIR) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ANCHOR_DIR).getCode();

 
    casFeat_COMMENT = jcas.getRequiredFeatureDE(casType, "COMMENT", "uima.cas.String", featOkTst);
    casFeatCode_COMMENT  = (null == casFeat_COMMENT) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_COMMENT).getCode();

 
    casFeat_MOD = jcas.getRequiredFeatureDE(casType, "MOD", "uima.cas.String", featOkTst);
    casFeatCode_MOD  = (null == casFeat_MOD) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_MOD).getCode();

 
    casFeat_NON_SPECIFIC = jcas.getRequiredFeatureDE(casType, "NON_SPECIFIC", "uima.cas.String", featOkTst);
    casFeatCode_NON_SPECIFIC  = (null == casFeat_NON_SPECIFIC) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_NON_SPECIFIC).getCode();

 
    casFeat_SET = jcas.getRequiredFeatureDE(casType, "SET", "uima.cas.String", featOkTst);
    casFeatCode_SET  = (null == casFeat_SET) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SET).getCode();

  }
}



    