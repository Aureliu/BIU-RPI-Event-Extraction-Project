
/* First created by JCasGen Mon Aug 25 18:38:49 IDT 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.cas.TOP_Type;

/** 
 * Updated by JCasGen Mon Dec 08 02:19:30 EST 2014
 * @generated */
public class TAll_Type extends TOP_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TAll_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TAll_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TAll(addr, TAll_Type.this);
  			   TAll_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TAll(addr, TAll_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TAll.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll");



  /** @generated */
  final Feature casFeat_cls;
  /** @generated */
  final int     casFeatCode_cls;
  /** @generated */ 
  public String getCls(int addr) {
        if (featOkTst && casFeat_cls == null)
      jcas.throwFeatMissing("cls", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll");
    return ll_cas.ll_getStringValue(addr, casFeatCode_cls);
  }
  /** @generated */    
  public void setCls(int addr, String v) {
        if (featOkTst && casFeat_cls == null)
      jcas.throwFeatMissing("cls", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll");
    ll_cas.ll_setStringValue(addr, casFeatCode_cls, v);}
    
  
 
  /** @generated */
  final Feature casFeat_role;
  /** @generated */
  final int     casFeatCode_role;
  /** @generated */ 
  public String getRole(int addr) {
        if (featOkTst && casFeat_role == null)
      jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll");
    return ll_cas.ll_getStringValue(addr, casFeatCode_role);
  }
  /** @generated */    
  public void setRole(int addr, String v) {
        if (featOkTst && casFeat_role == null)
      jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll");
    ll_cas.ll_setStringValue(addr, casFeatCode_role, v);}
    
  
 
  /** @generated */
  final Feature casFeat_val;
  /** @generated */
  final int     casFeatCode_val;
  /** @generated */ 
  public String getVal(int addr) {
        if (featOkTst && casFeat_val == null)
      jcas.throwFeatMissing("val", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll");
    return ll_cas.ll_getStringValue(addr, casFeatCode_val);
  }
  /** @generated */    
  public void setVal(int addr, String v) {
        if (featOkTst && casFeat_val == null)
      jcas.throwFeatMissing("val", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll");
    ll_cas.ll_setStringValue(addr, casFeatCode_val, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public TAll_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_cls = jcas.getRequiredFeatureDE(casType, "cls", "uima.cas.String", featOkTst);
    casFeatCode_cls  = (null == casFeat_cls) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_cls).getCode();

 
    casFeat_role = jcas.getRequiredFeatureDE(casType, "role", "uima.cas.String", featOkTst);
    casFeatCode_role  = (null == casFeat_role) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_role).getCode();

 
    casFeat_val = jcas.getRequiredFeatureDE(casType, "val", "uima.cas.String", featOkTst);
    casFeatCode_val  = (null == casFeat_val) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_val).getCode();

  }
}



    