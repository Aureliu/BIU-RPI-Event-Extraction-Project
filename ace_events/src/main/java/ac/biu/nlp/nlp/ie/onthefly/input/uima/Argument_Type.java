
/* First created by JCasGen Wed Mar 05 18:37:19 IST 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Sun Jun 29 20:28:54 IDT 2014
 * @generated */
public class Argument_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Argument_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Argument_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Argument(addr, Argument_Type.this);
  			   Argument_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Argument(addr, Argument_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Argument.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
 
  /** @generated */
  final Feature casFeat_role;
  /** @generated */
  final int     casFeatCode_role;
  /** @generated */ 
  public int getRole(int addr) {
        if (featOkTst && casFeat_role == null)
      jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    return ll_cas.ll_getRefValue(addr, casFeatCode_role);
  }
  /** @generated */    
  public void setRole(int addr, int v) {
        if (featOkTst && casFeat_role == null)
      jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    ll_cas.ll_setRefValue(addr, casFeatCode_role, v);}
    
  
 
  /** @generated */
  final Feature casFeat_types;
  /** @generated */
  final int     casFeatCode_types;
  /** @generated */ 
  public int getTypes(int addr) {
        if (featOkTst && casFeat_types == null)
      jcas.throwFeatMissing("types", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    return ll_cas.ll_getRefValue(addr, casFeatCode_types);
  }
  /** @generated */    
  public void setTypes(int addr, int v) {
        if (featOkTst && casFeat_types == null)
      jcas.throwFeatMissing("types", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    ll_cas.ll_setRefValue(addr, casFeatCode_types, v);}
    
   /** @generated */
  public int getTypes(int addr, int i) {
        if (featOkTst && casFeat_types == null)
      jcas.throwFeatMissing("types", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_types), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_types), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_types), i);
  }
   
  /** @generated */ 
  public void setTypes(int addr, int i, int v) {
        if (featOkTst && casFeat_types == null)
      jcas.throwFeatMissing("types", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_types), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_types), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_types), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_examples;
  /** @generated */
  final int     casFeatCode_examples;
  /** @generated */ 
  public int getExamples(int addr) {
        if (featOkTst && casFeat_examples == null)
      jcas.throwFeatMissing("examples", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    return ll_cas.ll_getRefValue(addr, casFeatCode_examples);
  }
  /** @generated */    
  public void setExamples(int addr, int v) {
        if (featOkTst && casFeat_examples == null)
      jcas.throwFeatMissing("examples", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    ll_cas.ll_setRefValue(addr, casFeatCode_examples, v);}
    
   /** @generated */
  public int getExamples(int addr, int i) {
        if (featOkTst && casFeat_examples == null)
      jcas.throwFeatMissing("examples", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_examples), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_examples), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_examples), i);
  }
   
  /** @generated */ 
  public void setExamples(int addr, int i, int v) {
        if (featOkTst && casFeat_examples == null)
      jcas.throwFeatMissing("examples", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_examples), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_examples), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_examples), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Argument_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_role = jcas.getRequiredFeatureDE(casType, "role", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentRole", featOkTst);
    casFeatCode_role  = (null == casFeat_role) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_role).getCode();

 
    casFeat_types = jcas.getRequiredFeatureDE(casType, "types", "uima.cas.FSArray", featOkTst);
    casFeatCode_types  = (null == casFeat_types) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_types).getCode();

 
    casFeat_examples = jcas.getRequiredFeatureDE(casType, "examples", "uima.cas.FSArray", featOkTst);
    casFeatCode_examples  = (null == casFeat_examples) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_examples).getCode();

  }
}



    