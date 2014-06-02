
/* First created by JCasGen Tue Jul 09 16:21:26 IDT 2013 */
package ac.biu.nlp.nlp.ace_uima.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** 
 * Updated by JCasGen Mon Jul 15 00:03:28 IDT 2013
 * @generated */
public class Entity_Type extends BasicArgument_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Entity_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Entity_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Entity(addr, Entity_Type.this);
  			   Entity_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Entity(addr, Entity_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Entity.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ace_uima.uima.Entity");
 
  /** @generated */
  final Feature casFeat_TYPE;
  /** @generated */
  final int     casFeatCode_TYPE;
  /** @generated */ 
  public String getTYPE(int addr) {
        if (featOkTst && casFeat_TYPE == null)
      jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.Entity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TYPE);
  }
  /** @generated */    
  public void setTYPE(int addr, String v) {
        if (featOkTst && casFeat_TYPE == null)
      jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.Entity");
    ll_cas.ll_setStringValue(addr, casFeatCode_TYPE, v);}
    
  
 
  /** @generated */
  final Feature casFeat_SUBTYPE;
  /** @generated */
  final int     casFeatCode_SUBTYPE;
  /** @generated */ 
  public String getSUBTYPE(int addr) {
        if (featOkTst && casFeat_SUBTYPE == null)
      jcas.throwFeatMissing("SUBTYPE", "ac.biu.nlp.nlp.ace_uima.uima.Entity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SUBTYPE);
  }
  /** @generated */    
  public void setSUBTYPE(int addr, String v) {
        if (featOkTst && casFeat_SUBTYPE == null)
      jcas.throwFeatMissing("SUBTYPE", "ac.biu.nlp.nlp.ace_uima.uima.Entity");
    ll_cas.ll_setStringValue(addr, casFeatCode_SUBTYPE, v);}
    
  
 
  /** @generated */
  final Feature casFeat_CLASS;
  /** @generated */
  final int     casFeatCode_CLASS;
  /** @generated */ 
  public String getCLASS(int addr) {
        if (featOkTst && casFeat_CLASS == null)
      jcas.throwFeatMissing("CLASS", "ac.biu.nlp.nlp.ace_uima.uima.Entity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_CLASS);
  }
  /** @generated */    
  public void setCLASS(int addr, String v) {
        if (featOkTst && casFeat_CLASS == null)
      jcas.throwFeatMissing("CLASS", "ac.biu.nlp.nlp.ace_uima.uima.Entity");
    ll_cas.ll_setStringValue(addr, casFeatCode_CLASS, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Entity_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_TYPE = jcas.getRequiredFeatureDE(casType, "TYPE", "uima.cas.String", featOkTst);
    casFeatCode_TYPE  = (null == casFeat_TYPE) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TYPE).getCode();

 
    casFeat_SUBTYPE = jcas.getRequiredFeatureDE(casType, "SUBTYPE", "uima.cas.String", featOkTst);
    casFeatCode_SUBTYPE  = (null == casFeat_SUBTYPE) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SUBTYPE).getCode();

 
    casFeat_CLASS = jcas.getRequiredFeatureDE(casType, "CLASS", "uima.cas.String", featOkTst);
    casFeatCode_CLASS  = (null == casFeat_CLASS) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_CLASS).getCode();

  }
}



    