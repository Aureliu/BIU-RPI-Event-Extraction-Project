
/* First created by JCasGen Fri Jun 27 12:02:31 IDT 2014 */
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
 * Updated by JCasGen Sun Aug 10 13:57:30 IDT 2014
 * @generated */
public class PredicateInUsageSample_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (PredicateInUsageSample_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = PredicateInUsageSample_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new PredicateInUsageSample(addr, PredicateInUsageSample_Type.this);
  			   PredicateInUsageSample_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new PredicateInUsageSample(addr, PredicateInUsageSample_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = PredicateInUsageSample.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateInUsageSample");
 
  /** @generated */
  final Feature casFeat_predicateSeed;
  /** @generated */
  final int     casFeatCode_predicateSeed;
  /** @generated */ 
  public int getPredicateSeed(int addr) {
        if (featOkTst && casFeat_predicateSeed == null)
      jcas.throwFeatMissing("predicateSeed", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_predicateSeed);
  }
  /** @generated */    
  public void setPredicateSeed(int addr, int v) {
        if (featOkTst && casFeat_predicateSeed == null)
      jcas.throwFeatMissing("predicateSeed", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_predicateSeed, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public PredicateInUsageSample_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_predicateSeed = jcas.getRequiredFeatureDE(casType, "predicateSeed", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed", featOkTst);
    casFeatCode_predicateSeed  = (null == casFeat_predicateSeed) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_predicateSeed).getCode();

  }
}



    