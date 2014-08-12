
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
 * Updated by JCasGen Sun Aug 10 13:57:30 IDT 2014
 * @generated */
public class ArgumentExample_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ArgumentExample_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ArgumentExample_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ArgumentExample(addr, ArgumentExample_Type.this);
  			   ArgumentExample_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ArgumentExample(addr, ArgumentExample_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ArgumentExample.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample");



  /** @generated */
  final Feature casFeat_argument;
  /** @generated */
  final int     casFeatCode_argument;
  /** @generated */ 
  public int getArgument(int addr) {
        if (featOkTst && casFeat_argument == null)
      jcas.throwFeatMissing("argument", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_argument);
  }
  /** @generated */    
  public void setArgument(int addr, int v) {
        if (featOkTst && casFeat_argument == null)
      jcas.throwFeatMissing("argument", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample");
    ll_cas.ll_setRefValue(addr, casFeatCode_argument, v);}
    
  
 
  /** @generated */
  final Feature casFeat_aiuses;
  /** @generated */
  final int     casFeatCode_aiuses;
  /** @generated */ 
  public int getAiuses(int addr) {
        if (featOkTst && casFeat_aiuses == null)
      jcas.throwFeatMissing("aiuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_aiuses);
  }
  /** @generated */    
  public void setAiuses(int addr, int v) {
        if (featOkTst && casFeat_aiuses == null)
      jcas.throwFeatMissing("aiuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample");
    ll_cas.ll_setRefValue(addr, casFeatCode_aiuses, v);}
    
   /** @generated */
  public int getAiuses(int addr, int i) {
        if (featOkTst && casFeat_aiuses == null)
      jcas.throwFeatMissing("aiuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_aiuses), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_aiuses), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_aiuses), i);
  }
   
  /** @generated */ 
  public void setAiuses(int addr, int i, int v) {
        if (featOkTst && casFeat_aiuses == null)
      jcas.throwFeatMissing("aiuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_aiuses), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_aiuses), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_aiuses), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ArgumentExample_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_argument = jcas.getRequiredFeatureDE(casType, "argument", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument", featOkTst);
    casFeatCode_argument  = (null == casFeat_argument) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_argument).getCode();

 
    casFeat_aiuses = jcas.getRequiredFeatureDE(casType, "aiuses", "uima.cas.FSArray", featOkTst);
    casFeatCode_aiuses  = (null == casFeat_aiuses) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_aiuses).getCode();

  }
}



    