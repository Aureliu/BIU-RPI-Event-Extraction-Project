
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
 * Updated by JCasGen Sat Aug 16 17:46:46 IDT 2014
 * @generated */
public class PredicateSeed_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (PredicateSeed_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = PredicateSeed_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new PredicateSeed(addr, PredicateSeed_Type.this);
  			   PredicateSeed_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new PredicateSeed(addr, PredicateSeed_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = PredicateSeed.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed");



  /** @generated */
  final Feature casFeat_piuses;
  /** @generated */
  final int     casFeatCode_piuses;
  /** @generated */ 
  public int getPiuses(int addr) {
        if (featOkTst && casFeat_piuses == null)
      jcas.throwFeatMissing("piuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed");
    return ll_cas.ll_getRefValue(addr, casFeatCode_piuses);
  }
  /** @generated */    
  public void setPiuses(int addr, int v) {
        if (featOkTst && casFeat_piuses == null)
      jcas.throwFeatMissing("piuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed");
    ll_cas.ll_setRefValue(addr, casFeatCode_piuses, v);}
    
   /** @generated */
  public int getPiuses(int addr, int i) {
        if (featOkTst && casFeat_piuses == null)
      jcas.throwFeatMissing("piuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_piuses), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_piuses), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_piuses), i);
  }
   
  /** @generated */ 
  public void setPiuses(int addr, int i, int v) {
        if (featOkTst && casFeat_piuses == null)
      jcas.throwFeatMissing("piuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_piuses), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_piuses), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_piuses), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public PredicateSeed_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_piuses = jcas.getRequiredFeatureDE(casType, "piuses", "uima.cas.FSArray", featOkTst);
    casFeatCode_piuses  = (null == casFeat_piuses) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_piuses).getCode();

  }
}



    