
/* First created by JCasGen Mon Aug 25 18:56:30 IDT 2014 */
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
public class VAll_Type extends TOP_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (VAll_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = VAll_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new VAll(addr, VAll_Type.this);
  			   VAll_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new VAll(addr, VAll_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = VAll.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll");
 
  /** @generated */
  final Feature casFeat_cls;
  /** @generated */
  final int     casFeatCode_cls;
  /** @generated */ 
  public String getCls(int addr) {
        if (featOkTst && casFeat_cls == null)
      jcas.throwFeatMissing("cls", "ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll");
    return ll_cas.ll_getStringValue(addr, casFeatCode_cls);
  }
  /** @generated */    
  public void setCls(int addr, String v) {
        if (featOkTst && casFeat_cls == null)
      jcas.throwFeatMissing("cls", "ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll");
    ll_cas.ll_setStringValue(addr, casFeatCode_cls, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeout;
  /** @generated */
  final int     casFeatCode_treeout;
  /** @generated */ 
  public String getTreeout(int addr) {
        if (featOkTst && casFeat_treeout == null)
      jcas.throwFeatMissing("treeout", "ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll");
    return ll_cas.ll_getStringValue(addr, casFeatCode_treeout);
  }
  /** @generated */    
  public void setTreeout(int addr, String v) {
        if (featOkTst && casFeat_treeout == null)
      jcas.throwFeatMissing("treeout", "ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll");
    ll_cas.ll_setStringValue(addr, casFeatCode_treeout, v);}
    
  
 
  /** @generated */
  final Feature casFeat_val;
  /** @generated */
  final int     casFeatCode_val;
  /** @generated */ 
  public String getVal(int addr) {
        if (featOkTst && casFeat_val == null)
      jcas.throwFeatMissing("val", "ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll");
    return ll_cas.ll_getStringValue(addr, casFeatCode_val);
  }
  /** @generated */    
  public void setVal(int addr, String v) {
        if (featOkTst && casFeat_val == null)
      jcas.throwFeatMissing("val", "ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll");
    ll_cas.ll_setStringValue(addr, casFeatCode_val, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public VAll_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_cls = jcas.getRequiredFeatureDE(casType, "cls", "uima.cas.String", featOkTst);
    casFeatCode_cls  = (null == casFeat_cls) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_cls).getCode();

 
    casFeat_treeout = jcas.getRequiredFeatureDE(casType, "treeout", "uima.cas.String", featOkTst);
    casFeatCode_treeout  = (null == casFeat_treeout) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeout).getCode();

 
    casFeat_val = jcas.getRequiredFeatureDE(casType, "val", "uima.cas.String", featOkTst);
    casFeatCode_val  = (null == casFeat_val) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_val).getCode();

  }
}



    