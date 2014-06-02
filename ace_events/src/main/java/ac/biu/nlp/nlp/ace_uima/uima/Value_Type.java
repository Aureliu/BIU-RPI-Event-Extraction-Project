
/* First created by JCasGen Tue Jul 09 17:25:26 IDT 2013 */
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
public class Value_Type extends BasicArgument_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Value_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Value_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Value(addr, Value_Type.this);
  			   Value_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Value(addr, Value_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Value.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ace_uima.uima.Value");



  /** @generated */
  final Feature casFeat_TYPE;
  /** @generated */
  final int     casFeatCode_TYPE;
  /** @generated */ 
  public String getTYPE(int addr) {
        if (featOkTst && casFeat_TYPE == null)
      jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.Value");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TYPE);
  }
  /** @generated */    
  public void setTYPE(int addr, String v) {
        if (featOkTst && casFeat_TYPE == null)
      jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.Value");
    ll_cas.ll_setStringValue(addr, casFeatCode_TYPE, v);}
    
  
 
  /** @generated */
  final Feature casFeat_SUBTYPE;
  /** @generated */
  final int     casFeatCode_SUBTYPE;
  /** @generated */ 
  public String getSUBTYPE(int addr) {
        if (featOkTst && casFeat_SUBTYPE == null)
      jcas.throwFeatMissing("SUBTYPE", "ac.biu.nlp.nlp.ace_uima.uima.Value");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SUBTYPE);
  }
  /** @generated */    
  public void setSUBTYPE(int addr, String v) {
        if (featOkTst && casFeat_SUBTYPE == null)
      jcas.throwFeatMissing("SUBTYPE", "ac.biu.nlp.nlp.ace_uima.uima.Value");
    ll_cas.ll_setStringValue(addr, casFeatCode_SUBTYPE, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Value_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_TYPE = jcas.getRequiredFeatureDE(casType, "TYPE", "uima.cas.String", featOkTst);
    casFeatCode_TYPE  = (null == casFeat_TYPE) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TYPE).getCode();

 
    casFeat_SUBTYPE = jcas.getRequiredFeatureDE(casType, "SUBTYPE", "uima.cas.String", featOkTst);
    casFeatCode_SUBTYPE  = (null == casFeat_SUBTYPE) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SUBTYPE).getCode();

  }
}



    