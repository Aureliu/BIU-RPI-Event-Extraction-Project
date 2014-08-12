
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
public class ArgumentInUsageSample_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ArgumentInUsageSample_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ArgumentInUsageSample_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ArgumentInUsageSample(addr, ArgumentInUsageSample_Type.this);
  			   ArgumentInUsageSample_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ArgumentInUsageSample(addr, ArgumentInUsageSample_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ArgumentInUsageSample.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
 
  /** @generated */
  final Feature casFeat_argumentExample;
  /** @generated */
  final int     casFeatCode_argumentExample;
  /** @generated */ 
  public int getArgumentExample(int addr) {
        if (featOkTst && casFeat_argumentExample == null)
      jcas.throwFeatMissing("argumentExample", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_argumentExample);
  }
  /** @generated */    
  public void setArgumentExample(int addr, int v) {
        if (featOkTst && casFeat_argumentExample == null)
      jcas.throwFeatMissing("argumentExample", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_argumentExample, v);}
    
  
 
  /** @generated */
  final Feature casFeat_pius;
  /** @generated */
  final int     casFeatCode_pius;
  /** @generated */ 
  public int getPius(int addr) {
        if (featOkTst && casFeat_pius == null)
      jcas.throwFeatMissing("pius", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_pius);
  }
  /** @generated */    
  public void setPius(int addr, int v) {
        if (featOkTst && casFeat_pius == null)
      jcas.throwFeatMissing("pius", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_pius, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ArgumentInUsageSample_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_argumentExample = jcas.getRequiredFeatureDE(casType, "argumentExample", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample", featOkTst);
    casFeatCode_argumentExample  = (null == casFeat_argumentExample) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_argumentExample).getCode();

 
    casFeat_pius = jcas.getRequiredFeatureDE(casType, "pius", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateInUsageSample", featOkTst);
    casFeatCode_pius  = (null == casFeat_pius) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pius).getCode();

  }
}



    