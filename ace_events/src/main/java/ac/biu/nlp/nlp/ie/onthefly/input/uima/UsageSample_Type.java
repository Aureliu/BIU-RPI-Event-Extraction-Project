
/* First created by JCasGen Wed Mar 05 18:37:19 IST 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Dec 08 02:19:30 EST 2014
 * @generated */
public class UsageSample_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (UsageSample_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = UsageSample_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new UsageSample(addr, UsageSample_Type.this);
  			   UsageSample_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new UsageSample(addr, UsageSample_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = UsageSample.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample");



  /** @generated */
  final Feature casFeat_text;
  /** @generated */
  final int     casFeatCode_text;
  /** @generated */ 
  public String getText(int addr) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample");
    return ll_cas.ll_getStringValue(addr, casFeatCode_text);
  }
  /** @generated */    
  public void setText(int addr, String v) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample");
    ll_cas.ll_setStringValue(addr, casFeatCode_text, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeout;
  /** @generated */
  final int     casFeatCode_treeout;
  /** @generated */ 
  public String getTreeout(int addr) {
        if (featOkTst && casFeat_treeout == null)
      jcas.throwFeatMissing("treeout", "ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample");
    return ll_cas.ll_getStringValue(addr, casFeatCode_treeout);
  }
  /** @generated */    
  public void setTreeout(int addr, String v) {
        if (featOkTst && casFeat_treeout == null)
      jcas.throwFeatMissing("treeout", "ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample");
    ll_cas.ll_setStringValue(addr, casFeatCode_treeout, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public UsageSample_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_text = jcas.getRequiredFeatureDE(casType, "text", "uima.cas.String", featOkTst);
    casFeatCode_text  = (null == casFeat_text) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_text).getCode();

 
    casFeat_treeout = jcas.getRequiredFeatureDE(casType, "treeout", "uima.cas.String", featOkTst);
    casFeatCode_treeout  = (null == casFeat_treeout) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeout).getCode();

  }
}



    