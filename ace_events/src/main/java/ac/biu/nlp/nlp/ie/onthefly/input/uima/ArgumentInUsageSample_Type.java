
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
 * Updated by JCasGen Mon Dec 08 02:19:30 EST 2014
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
    
  
 
  /** @generated */
  final Feature casFeat_sample;
  /** @generated */
  final int     casFeatCode_sample;
  /** @generated */ 
  public int getSample(int addr) {
        if (featOkTst && casFeat_sample == null)
      jcas.throwFeatMissing("sample", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_sample);
  }
  /** @generated */    
  public void setSample(int addr, int v) {
        if (featOkTst && casFeat_sample == null)
      jcas.throwFeatMissing("sample", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_sample, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepNoContext;
  /** @generated */ 
  public int getTreeoutDepNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepNoContext == null)
      jcas.throwFeatMissing("treeoutDepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepNoContext);
  }
  /** @generated */    
  public void setTreeoutDepNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepNoContext == null)
      jcas.throwFeatMissing("treeoutDepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepGenPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepGenPosNoContext;
  /** @generated */ 
  public int getTreeoutDepGenPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepGenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepGenPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepGenPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepGenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepGenPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepSpecPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepSpecPosNoContext;
  /** @generated */ 
  public int getTreeoutDepSpecPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepSpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepSpecPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepSpecPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepSpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepSpecPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatGenPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatGenPosNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatGenPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatGenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatGenPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatGenPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatGenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatGenPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatSpecPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatSpecPosNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatSpecPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatSpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatSpecPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatSpecPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatSpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatSpecPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepWithContext;
  /** @generated */
  final int     casFeatCode_treeoutDepWithContext;
  /** @generated */ 
  public int getTreeoutDepWithContext(int addr) {
        if (featOkTst && casFeat_treeoutDepWithContext == null)
      jcas.throwFeatMissing("treeoutDepWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepWithContext);
  }
  /** @generated */    
  public void setTreeoutDepWithContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepWithContext == null)
      jcas.throwFeatMissing("treeoutDepWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepWithContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepGenPosWithContext;
  /** @generated */
  final int     casFeatCode_treeoutDepGenPosWithContext;
  /** @generated */ 
  public int getTreeoutDepGenPosWithContext(int addr) {
        if (featOkTst && casFeat_treeoutDepGenPosWithContext == null)
      jcas.throwFeatMissing("treeoutDepGenPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepGenPosWithContext);
  }
  /** @generated */    
  public void setTreeoutDepGenPosWithContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepGenPosWithContext == null)
      jcas.throwFeatMissing("treeoutDepGenPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepGenPosWithContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepSpecPosWithContext;
  /** @generated */
  final int     casFeatCode_treeoutDepSpecPosWithContext;
  /** @generated */ 
  public int getTreeoutDepSpecPosWithContext(int addr) {
        if (featOkTst && casFeat_treeoutDepSpecPosWithContext == null)
      jcas.throwFeatMissing("treeoutDepSpecPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepSpecPosWithContext);
  }
  /** @generated */    
  public void setTreeoutDepSpecPosWithContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepSpecPosWithContext == null)
      jcas.throwFeatMissing("treeoutDepSpecPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepSpecPosWithContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepPrepNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepPrepNoContext;
  /** @generated */ 
  public int getTreeoutDepPrepNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepPrepNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepPrepNoContext);
  }
  /** @generated */    
  public void setTreeoutDepPrepNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepPrepNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepPrepNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepPrepGenPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepPrepGenPosNoContext;
  /** @generated */ 
  public int getTreeoutDepPrepGenPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepPrepGenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepPrepGenPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepPrepGenPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepPrepGenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepPrepGenPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepPrepSpecPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepPrepSpecPosNoContext;
  /** @generated */ 
  public int getTreeoutDepPrepSpecPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepPrepSpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepPrepSpecPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepPrepSpecPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepPrepSpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepPrepSpecPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatPrepNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatPrepNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatPrepNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatPrepNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatPrepNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatPrepNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatPrepNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatPrepNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatPrepGenPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatPrepGenPosNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatPrepGenPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatPrepGenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatPrepGenPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatPrepGenPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatPrepGenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatPrepGenPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatPrepSpecPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatPrepSpecPosNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatPrepSpecPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatPrepSpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatPrepSpecPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatPrepSpecPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatPrepSpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatPrepSpecPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepPrepWithContext;
  /** @generated */
  final int     casFeatCode_treeoutDepPrepWithContext;
  /** @generated */ 
  public int getTreeoutDepPrepWithContext(int addr) {
        if (featOkTst && casFeat_treeoutDepPrepWithContext == null)
      jcas.throwFeatMissing("treeoutDepPrepWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepPrepWithContext);
  }
  /** @generated */    
  public void setTreeoutDepPrepWithContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepPrepWithContext == null)
      jcas.throwFeatMissing("treeoutDepPrepWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepPrepWithContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepPrepGenPosWithContext;
  /** @generated */
  final int     casFeatCode_treeoutDepPrepGenPosWithContext;
  /** @generated */ 
  public int getTreeoutDepPrepGenPosWithContext(int addr) {
        if (featOkTst && casFeat_treeoutDepPrepGenPosWithContext == null)
      jcas.throwFeatMissing("treeoutDepPrepGenPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepPrepGenPosWithContext);
  }
  /** @generated */    
  public void setTreeoutDepPrepGenPosWithContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepPrepGenPosWithContext == null)
      jcas.throwFeatMissing("treeoutDepPrepGenPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepPrepGenPosWithContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepPrepSpecPosWithContext;
  /** @generated */
  final int     casFeatCode_treeoutDepPrepSpecPosWithContext;
  /** @generated */ 
  public int getTreeoutDepPrepSpecPosWithContext(int addr) {
        if (featOkTst && casFeat_treeoutDepPrepSpecPosWithContext == null)
      jcas.throwFeatMissing("treeoutDepPrepSpecPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepPrepSpecPosWithContext);
  }
  /** @generated */    
  public void setTreeoutDepPrepSpecPosWithContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepPrepSpecPosWithContext == null)
      jcas.throwFeatMissing("treeoutDepPrepSpecPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepPrepSpecPosWithContext, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ArgumentInUsageSample_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_argumentExample = jcas.getRequiredFeatureDE(casType, "argumentExample", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample", featOkTst);
    casFeatCode_argumentExample  = (null == casFeat_argumentExample) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_argumentExample).getCode();

 
    casFeat_pius = jcas.getRequiredFeatureDE(casType, "pius", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateInUsageSample", featOkTst);
    casFeatCode_pius  = (null == casFeat_pius) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pius).getCode();

 
    casFeat_sample = jcas.getRequiredFeatureDE(casType, "sample", "ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample", featOkTst);
    casFeatCode_sample  = (null == casFeat_sample) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_sample).getCode();

 
    casFeat_treeoutDepNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepNoContext", featOkTst);
    casFeatCode_treeoutDepNoContext  = (null == casFeat_treeoutDepNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepNoContext).getCode();

 
    casFeat_treeoutDepGenPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepGenPosNoContext", featOkTst);
    casFeatCode_treeoutDepGenPosNoContext  = (null == casFeat_treeoutDepGenPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepGenPosNoContext).getCode();

 
    casFeat_treeoutDepSpecPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepSpecPosNoContext", featOkTst);
    casFeatCode_treeoutDepSpecPosNoContext  = (null == casFeat_treeoutDepSpecPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepSpecPosNoContext).getCode();

 
    casFeat_treeoutDepFlatNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatNoContext", featOkTst);
    casFeatCode_treeoutDepFlatNoContext  = (null == casFeat_treeoutDepFlatNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatNoContext).getCode();

 
    casFeat_treeoutDepFlatGenPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatGenPosNoContext", featOkTst);
    casFeatCode_treeoutDepFlatGenPosNoContext  = (null == casFeat_treeoutDepFlatGenPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatGenPosNoContext).getCode();

 
    casFeat_treeoutDepFlatSpecPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatSpecPosNoContext", featOkTst);
    casFeatCode_treeoutDepFlatSpecPosNoContext  = (null == casFeat_treeoutDepFlatSpecPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatSpecPosNoContext).getCode();

 
    casFeat_treeoutDepWithContext = jcas.getRequiredFeatureDE(casType, "treeoutDepWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepWithContext", featOkTst);
    casFeatCode_treeoutDepWithContext  = (null == casFeat_treeoutDepWithContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepWithContext).getCode();

 
    casFeat_treeoutDepGenPosWithContext = jcas.getRequiredFeatureDE(casType, "treeoutDepGenPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepGenPosWithContext", featOkTst);
    casFeatCode_treeoutDepGenPosWithContext  = (null == casFeat_treeoutDepGenPosWithContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepGenPosWithContext).getCode();

 
    casFeat_treeoutDepSpecPosWithContext = jcas.getRequiredFeatureDE(casType, "treeoutDepSpecPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepSpecPosWithContext", featOkTst);
    casFeatCode_treeoutDepSpecPosWithContext  = (null == casFeat_treeoutDepSpecPosWithContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepSpecPosWithContext).getCode();

 
    casFeat_treeoutDepPrepNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepPrepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepNoContext", featOkTst);
    casFeatCode_treeoutDepPrepNoContext  = (null == casFeat_treeoutDepPrepNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepPrepNoContext).getCode();

 
    casFeat_treeoutDepPrepGenPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepPrepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepGenPosNoContext", featOkTst);
    casFeatCode_treeoutDepPrepGenPosNoContext  = (null == casFeat_treeoutDepPrepGenPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepPrepGenPosNoContext).getCode();

 
    casFeat_treeoutDepPrepSpecPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepPrepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepSpecPosNoContext", featOkTst);
    casFeatCode_treeoutDepPrepSpecPosNoContext  = (null == casFeat_treeoutDepPrepSpecPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepPrepSpecPosNoContext).getCode();

 
    casFeat_treeoutDepFlatPrepNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatPrepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepNoContext", featOkTst);
    casFeatCode_treeoutDepFlatPrepNoContext  = (null == casFeat_treeoutDepFlatPrepNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatPrepNoContext).getCode();

 
    casFeat_treeoutDepFlatPrepGenPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatPrepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepGenPosNoContext", featOkTst);
    casFeatCode_treeoutDepFlatPrepGenPosNoContext  = (null == casFeat_treeoutDepFlatPrepGenPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatPrepGenPosNoContext).getCode();

 
    casFeat_treeoutDepFlatPrepSpecPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatPrepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepSpecPosNoContext", featOkTst);
    casFeatCode_treeoutDepFlatPrepSpecPosNoContext  = (null == casFeat_treeoutDepFlatPrepSpecPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatPrepSpecPosNoContext).getCode();

 
    casFeat_treeoutDepPrepWithContext = jcas.getRequiredFeatureDE(casType, "treeoutDepPrepWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepWithContext", featOkTst);
    casFeatCode_treeoutDepPrepWithContext  = (null == casFeat_treeoutDepPrepWithContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepPrepWithContext).getCode();

 
    casFeat_treeoutDepPrepGenPosWithContext = jcas.getRequiredFeatureDE(casType, "treeoutDepPrepGenPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepGenPosWithContext", featOkTst);
    casFeatCode_treeoutDepPrepGenPosWithContext  = (null == casFeat_treeoutDepPrepGenPosWithContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepPrepGenPosWithContext).getCode();

 
    casFeat_treeoutDepPrepSpecPosWithContext = jcas.getRequiredFeatureDE(casType, "treeoutDepPrepSpecPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepSpecPosWithContext", featOkTst);
    casFeatCode_treeoutDepPrepSpecPosWithContext  = (null == casFeat_treeoutDepPrepSpecPosWithContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepPrepSpecPosWithContext).getCode();

  }
}



    