
/* First created by JCasGen Wed Mar 05 18:37:19 IST 2014 */
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
 * Updated by JCasGen Wed Mar 05 18:37:19 IST 2014
 * @generated */
public class Predicate_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Predicate_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Predicate_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Predicate(addr, Predicate_Type.this);
  			   Predicate_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Predicate(addr, Predicate_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Predicate.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate");
 
  /** @generated */
  final Feature casFeat_name;
  /** @generated */
  final int     casFeatCode_name;
  /** @generated */ 
  public int getName(int addr) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate");
    return ll_cas.ll_getRefValue(addr, casFeatCode_name);
  }
  /** @generated */    
  public void setName(int addr, int v) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate");
    ll_cas.ll_setRefValue(addr, casFeatCode_name, v);}
    
  
 
  /** @generated */
  final Feature casFeat_seeds;
  /** @generated */
  final int     casFeatCode_seeds;
  /** @generated */ 
  public int getSeeds(int addr) {
        if (featOkTst && casFeat_seeds == null)
      jcas.throwFeatMissing("seeds", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate");
    return ll_cas.ll_getRefValue(addr, casFeatCode_seeds);
  }
  /** @generated */    
  public void setSeeds(int addr, int v) {
        if (featOkTst && casFeat_seeds == null)
      jcas.throwFeatMissing("seeds", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate");
    ll_cas.ll_setRefValue(addr, casFeatCode_seeds, v);}
    
   /** @generated */
  public int getSeeds(int addr, int i) {
        if (featOkTst && casFeat_seeds == null)
      jcas.throwFeatMissing("seeds", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_seeds), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_seeds), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_seeds), i);
  }
   
  /** @generated */ 
  public void setSeeds(int addr, int i, int v) {
        if (featOkTst && casFeat_seeds == null)
      jcas.throwFeatMissing("seeds", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_seeds), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_seeds), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_seeds), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Predicate_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_name = jcas.getRequiredFeatureDE(casType, "name", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateName", featOkTst);
    casFeatCode_name  = (null == casFeat_name) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_name).getCode();

 
    casFeat_seeds = jcas.getRequiredFeatureDE(casType, "seeds", "uima.cas.FSArray", featOkTst);
    casFeatCode_seeds  = (null == casFeat_seeds) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_seeds).getCode();

  }
}



    