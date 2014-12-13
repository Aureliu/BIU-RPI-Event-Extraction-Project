
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
 * Updated by JCasGen Sat Dec 13 00:42:43 EST 2014
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
  final Feature casFeat_treeoutDepUp2NoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepUp2NoContext;
  /** @generated */ 
  public int getTreeoutDepUp2NoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepUp2NoContext == null)
      jcas.throwFeatMissing("treeoutDepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepUp2NoContext);
  }
  /** @generated */    
  public void setTreeoutDepUp2NoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepUp2NoContext == null)
      jcas.throwFeatMissing("treeoutDepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepUp2NoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepUp2GenPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepUp2GenPosNoContext;
  /** @generated */ 
  public int getTreeoutDepUp2GenPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepUp2GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepUp2GenPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepUp2GenPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepUp2GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepUp2GenPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepUp2SpecPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepUp2SpecPosNoContext;
  /** @generated */ 
  public int getTreeoutDepUp2SpecPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepUp2SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepUp2SpecPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepUp2SpecPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepUp2SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepUp2SpecPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatUp2NoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatUp2NoContext;
  /** @generated */ 
  public int getTreeoutDepFlatUp2NoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatUp2NoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatUp2NoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatUp2NoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatUp2NoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatUp2NoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatUp2GenPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatUp2GenPosNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatUp2GenPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatUp2GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatUp2GenPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatUp2GenPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatUp2GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatUp2GenPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatUp2SpecPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatUp2SpecPosNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatUp2SpecPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatUp2SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatUp2SpecPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatUp2SpecPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatUp2SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatUp2SpecPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepPrepUp2NoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepPrepUp2NoContext;
  /** @generated */ 
  public int getTreeoutDepPrepUp2NoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepPrepUp2NoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepPrepUp2NoContext);
  }
  /** @generated */    
  public void setTreeoutDepPrepUp2NoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepPrepUp2NoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepPrepUp2NoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepPrepUp2GenPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepPrepUp2GenPosNoContext;
  /** @generated */ 
  public int getTreeoutDepPrepUp2GenPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepPrepUp2GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepPrepUp2GenPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepPrepUp2GenPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepPrepUp2GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepPrepUp2GenPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepPrepUp2SpecPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepPrepUp2SpecPosNoContext;
  /** @generated */ 
  public int getTreeoutDepPrepUp2SpecPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepPrepUp2SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepPrepUp2SpecPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepPrepUp2SpecPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepPrepUp2SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepPrepUp2SpecPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatPrepUp2NoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatPrepUp2NoContext;
  /** @generated */ 
  public int getTreeoutDepFlatPrepUp2NoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatPrepUp2NoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatPrepUp2NoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatPrepUp2NoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatPrepUp2NoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatPrepUp2NoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatPrepUp2GenPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatPrepUp2GenPosNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatPrepUp2GenPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatPrepUp2GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatPrepUp2GenPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatPrepUp2GenPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatPrepUp2GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatPrepUp2GenPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatPrepUp2SpecPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatPrepUp2SpecPosNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatPrepUp2SpecPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatPrepUp2SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatPrepUp2SpecPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatPrepUp2SpecPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatPrepUp2SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatPrepUp2SpecPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepUp3NoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepUp3NoContext;
  /** @generated */ 
  public int getTreeoutDepUp3NoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepUp3NoContext == null)
      jcas.throwFeatMissing("treeoutDepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepUp3NoContext);
  }
  /** @generated */    
  public void setTreeoutDepUp3NoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepUp3NoContext == null)
      jcas.throwFeatMissing("treeoutDepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepUp3NoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepUp3GenPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepUp3GenPosNoContext;
  /** @generated */ 
  public int getTreeoutDepUp3GenPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepUp3GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepUp3GenPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepUp3GenPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepUp3GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepUp3GenPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepUp3SpecPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepUp3SpecPosNoContext;
  /** @generated */ 
  public int getTreeoutDepUp3SpecPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepUp3SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepUp3SpecPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepUp3SpecPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepUp3SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepUp3SpecPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatUp3NoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatUp3NoContext;
  /** @generated */ 
  public int getTreeoutDepFlatUp3NoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatUp3NoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatUp3NoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatUp3NoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatUp3NoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatUp3NoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatUp3GenPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatUp3GenPosNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatUp3GenPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatUp3GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatUp3GenPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatUp3GenPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatUp3GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatUp3GenPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatUp3SpecPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatUp3SpecPosNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatUp3SpecPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatUp3SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatUp3SpecPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatUp3SpecPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatUp3SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatUp3SpecPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepPrepUp3NoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepPrepUp3NoContext;
  /** @generated */ 
  public int getTreeoutDepPrepUp3NoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepPrepUp3NoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepPrepUp3NoContext);
  }
  /** @generated */    
  public void setTreeoutDepPrepUp3NoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepPrepUp3NoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepPrepUp3NoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepPrepUp3GenPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepPrepUp3GenPosNoContext;
  /** @generated */ 
  public int getTreeoutDepPrepUp3GenPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepPrepUp3GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepPrepUp3GenPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepPrepUp3GenPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepPrepUp3GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepPrepUp3GenPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepPrepUp3SpecPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepPrepUp3SpecPosNoContext;
  /** @generated */ 
  public int getTreeoutDepPrepUp3SpecPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepPrepUp3SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepPrepUp3SpecPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepPrepUp3SpecPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepPrepUp3SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepPrepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepPrepUp3SpecPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatPrepUp3NoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatPrepUp3NoContext;
  /** @generated */ 
  public int getTreeoutDepFlatPrepUp3NoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatPrepUp3NoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatPrepUp3NoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatPrepUp3NoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatPrepUp3NoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatPrepUp3NoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatPrepUp3GenPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatPrepUp3GenPosNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatPrepUp3GenPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatPrepUp3GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatPrepUp3GenPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatPrepUp3GenPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatPrepUp3GenPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatPrepUp3GenPosNoContext, v);}
    
  
 
  /** @generated */
  final Feature casFeat_treeoutDepFlatPrepUp3SpecPosNoContext;
  /** @generated */
  final int     casFeatCode_treeoutDepFlatPrepUp3SpecPosNoContext;
  /** @generated */ 
  public int getTreeoutDepFlatPrepUp3SpecPosNoContext(int addr) {
        if (featOkTst && casFeat_treeoutDepFlatPrepUp3SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return ll_cas.ll_getRefValue(addr, casFeatCode_treeoutDepFlatPrepUp3SpecPosNoContext);
  }
  /** @generated */    
  public void setTreeoutDepFlatPrepUp3SpecPosNoContext(int addr, int v) {
        if (featOkTst && casFeat_treeoutDepFlatPrepUp3SpecPosNoContext == null)
      jcas.throwFeatMissing("treeoutDepFlatPrepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    ll_cas.ll_setRefValue(addr, casFeatCode_treeoutDepFlatPrepUp3SpecPosNoContext, v);}
    
  



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

 
    casFeat_treeoutDepUp2NoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepUp2NoContext", featOkTst);
    casFeatCode_treeoutDepUp2NoContext  = (null == casFeat_treeoutDepUp2NoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepUp2NoContext).getCode();

 
    casFeat_treeoutDepUp2GenPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepUp2GenPosNoContext", featOkTst);
    casFeatCode_treeoutDepUp2GenPosNoContext  = (null == casFeat_treeoutDepUp2GenPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepUp2GenPosNoContext).getCode();

 
    casFeat_treeoutDepUp2SpecPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepUp2SpecPosNoContext", featOkTst);
    casFeatCode_treeoutDepUp2SpecPosNoContext  = (null == casFeat_treeoutDepUp2SpecPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepUp2SpecPosNoContext).getCode();

 
    casFeat_treeoutDepFlatUp2NoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatUp2NoContext", featOkTst);
    casFeatCode_treeoutDepFlatUp2NoContext  = (null == casFeat_treeoutDepFlatUp2NoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatUp2NoContext).getCode();

 
    casFeat_treeoutDepFlatUp2GenPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatUp2GenPosNoContext", featOkTst);
    casFeatCode_treeoutDepFlatUp2GenPosNoContext  = (null == casFeat_treeoutDepFlatUp2GenPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatUp2GenPosNoContext).getCode();

 
    casFeat_treeoutDepFlatUp2SpecPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatUp2SpecPosNoContext", featOkTst);
    casFeatCode_treeoutDepFlatUp2SpecPosNoContext  = (null == casFeat_treeoutDepFlatUp2SpecPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatUp2SpecPosNoContext).getCode();

 
    casFeat_treeoutDepPrepUp2NoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepPrepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepUp2NoContext", featOkTst);
    casFeatCode_treeoutDepPrepUp2NoContext  = (null == casFeat_treeoutDepPrepUp2NoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepPrepUp2NoContext).getCode();

 
    casFeat_treeoutDepPrepUp2GenPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepPrepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepUp2GenPosNoContext", featOkTst);
    casFeatCode_treeoutDepPrepUp2GenPosNoContext  = (null == casFeat_treeoutDepPrepUp2GenPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepPrepUp2GenPosNoContext).getCode();

 
    casFeat_treeoutDepPrepUp2SpecPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepPrepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepUp2SpecPosNoContext", featOkTst);
    casFeatCode_treeoutDepPrepUp2SpecPosNoContext  = (null == casFeat_treeoutDepPrepUp2SpecPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepPrepUp2SpecPosNoContext).getCode();

 
    casFeat_treeoutDepFlatPrepUp2NoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatPrepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepUp2NoContext", featOkTst);
    casFeatCode_treeoutDepFlatPrepUp2NoContext  = (null == casFeat_treeoutDepFlatPrepUp2NoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatPrepUp2NoContext).getCode();

 
    casFeat_treeoutDepFlatPrepUp2GenPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatPrepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepUp2GenPosNoContext", featOkTst);
    casFeatCode_treeoutDepFlatPrepUp2GenPosNoContext  = (null == casFeat_treeoutDepFlatPrepUp2GenPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatPrepUp2GenPosNoContext).getCode();

 
    casFeat_treeoutDepFlatPrepUp2SpecPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatPrepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepUp2SpecPosNoContext", featOkTst);
    casFeatCode_treeoutDepFlatPrepUp2SpecPosNoContext  = (null == casFeat_treeoutDepFlatPrepUp2SpecPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatPrepUp2SpecPosNoContext).getCode();

 
    casFeat_treeoutDepUp3NoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepUp3NoContext", featOkTst);
    casFeatCode_treeoutDepUp3NoContext  = (null == casFeat_treeoutDepUp3NoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepUp3NoContext).getCode();

 
    casFeat_treeoutDepUp3GenPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepUp3GenPosNoContext", featOkTst);
    casFeatCode_treeoutDepUp3GenPosNoContext  = (null == casFeat_treeoutDepUp3GenPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepUp3GenPosNoContext).getCode();

 
    casFeat_treeoutDepUp3SpecPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepUp3SpecPosNoContext", featOkTst);
    casFeatCode_treeoutDepUp3SpecPosNoContext  = (null == casFeat_treeoutDepUp3SpecPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepUp3SpecPosNoContext).getCode();

 
    casFeat_treeoutDepFlatUp3NoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatUp3NoContext", featOkTst);
    casFeatCode_treeoutDepFlatUp3NoContext  = (null == casFeat_treeoutDepFlatUp3NoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatUp3NoContext).getCode();

 
    casFeat_treeoutDepFlatUp3GenPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatUp3GenPosNoContext", featOkTst);
    casFeatCode_treeoutDepFlatUp3GenPosNoContext  = (null == casFeat_treeoutDepFlatUp3GenPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatUp3GenPosNoContext).getCode();

 
    casFeat_treeoutDepFlatUp3SpecPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatUp3SpecPosNoContext", featOkTst);
    casFeatCode_treeoutDepFlatUp3SpecPosNoContext  = (null == casFeat_treeoutDepFlatUp3SpecPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatUp3SpecPosNoContext).getCode();

 
    casFeat_treeoutDepPrepUp3NoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepPrepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepUp3NoContext", featOkTst);
    casFeatCode_treeoutDepPrepUp3NoContext  = (null == casFeat_treeoutDepPrepUp3NoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepPrepUp3NoContext).getCode();

 
    casFeat_treeoutDepPrepUp3GenPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepPrepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepUp3GenPosNoContext", featOkTst);
    casFeatCode_treeoutDepPrepUp3GenPosNoContext  = (null == casFeat_treeoutDepPrepUp3GenPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepPrepUp3GenPosNoContext).getCode();

 
    casFeat_treeoutDepPrepUp3SpecPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepPrepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepUp3SpecPosNoContext", featOkTst);
    casFeatCode_treeoutDepPrepUp3SpecPosNoContext  = (null == casFeat_treeoutDepPrepUp3SpecPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepPrepUp3SpecPosNoContext).getCode();

 
    casFeat_treeoutDepFlatPrepUp3NoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatPrepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepUp3NoContext", featOkTst);
    casFeatCode_treeoutDepFlatPrepUp3NoContext  = (null == casFeat_treeoutDepFlatPrepUp3NoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatPrepUp3NoContext).getCode();

 
    casFeat_treeoutDepFlatPrepUp3GenPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatPrepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepUp3GenPosNoContext", featOkTst);
    casFeatCode_treeoutDepFlatPrepUp3GenPosNoContext  = (null == casFeat_treeoutDepFlatPrepUp3GenPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatPrepUp3GenPosNoContext).getCode();

 
    casFeat_treeoutDepFlatPrepUp3SpecPosNoContext = jcas.getRequiredFeatureDE(casType, "treeoutDepFlatPrepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepUp3SpecPosNoContext", featOkTst);
    casFeatCode_treeoutDepFlatPrepUp3SpecPosNoContext  = (null == casFeat_treeoutDepFlatPrepUp3SpecPosNoContext) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_treeoutDepFlatPrepUp3SpecPosNoContext).getCode();

  }
}



    