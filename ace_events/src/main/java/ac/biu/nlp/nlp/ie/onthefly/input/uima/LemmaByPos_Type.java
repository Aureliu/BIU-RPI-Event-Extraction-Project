
/* First created by JCasGen Sun Jun 29 20:28:54 IDT 2014 */
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
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Sat Dec 13 00:42:43 EST 2014
 * @generated */
public class LemmaByPos_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (LemmaByPos_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = LemmaByPos_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new LemmaByPos(addr, LemmaByPos_Type.this);
  			   LemmaByPos_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new LemmaByPos(addr, LemmaByPos_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = LemmaByPos.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ie.onthefly.input.uima.LemmaByPos");
 
  /** @generated */
  final Feature casFeat_value;
  /** @generated */
  final int     casFeatCode_value;
  /** @generated */ 
  public String getValue(int addr) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "ac.biu.nlp.nlp.ie.onthefly.input.uima.LemmaByPos");
    return ll_cas.ll_getStringValue(addr, casFeatCode_value);
  }
  /** @generated */    
  public void setValue(int addr, String v) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "ac.biu.nlp.nlp.ie.onthefly.input.uima.LemmaByPos");
    ll_cas.ll_setStringValue(addr, casFeatCode_value, v);}
    
  
 
  /** @generated */
  final Feature casFeat_posStr;
  /** @generated */
  final int     casFeatCode_posStr;
  /** @generated */ 
  public String getPosStr(int addr) {
        if (featOkTst && casFeat_posStr == null)
      jcas.throwFeatMissing("posStr", "ac.biu.nlp.nlp.ie.onthefly.input.uima.LemmaByPos");
    return ll_cas.ll_getStringValue(addr, casFeatCode_posStr);
  }
  /** @generated */    
  public void setPosStr(int addr, String v) {
        if (featOkTst && casFeat_posStr == null)
      jcas.throwFeatMissing("posStr", "ac.biu.nlp.nlp.ie.onthefly.input.uima.LemmaByPos");
    ll_cas.ll_setStringValue(addr, casFeatCode_posStr, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public LemmaByPos_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.String", featOkTst);
    casFeatCode_value  = (null == casFeat_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value).getCode();

 
    casFeat_posStr = jcas.getRequiredFeatureDE(casType, "posStr", "uima.cas.String", featOkTst);
    casFeatCode_posStr  = (null == casFeat_posStr) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_posStr).getCode();

  }
}



    