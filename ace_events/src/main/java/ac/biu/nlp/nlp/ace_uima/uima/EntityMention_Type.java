
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
public class EntityMention_Type extends BasicArgumentMention_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (EntityMention_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = EntityMention_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new EntityMention(addr, EntityMention_Type.this);
  			   EntityMention_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new EntityMention(addr, EntityMention_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = EntityMention.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ace_uima.uima.EntityMention");
 
  /** @generated */
  final Feature casFeat_TYPE;
  /** @generated */
  final int     casFeatCode_TYPE;
  /** @generated */ 
  public String getTYPE(int addr) {
        if (featOkTst && casFeat_TYPE == null)
      jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.EntityMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TYPE);
  }
  /** @generated */    
  public void setTYPE(int addr, String v) {
        if (featOkTst && casFeat_TYPE == null)
      jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.EntityMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_TYPE, v);}
    
  
 
  /** @generated */
  final Feature casFeat_LDCTYPE;
  /** @generated */
  final int     casFeatCode_LDCTYPE;
  /** @generated */ 
  public String getLDCTYPE(int addr) {
        if (featOkTst && casFeat_LDCTYPE == null)
      jcas.throwFeatMissing("LDCTYPE", "ac.biu.nlp.nlp.ace_uima.uima.EntityMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_LDCTYPE);
  }
  /** @generated */    
  public void setLDCTYPE(int addr, String v) {
        if (featOkTst && casFeat_LDCTYPE == null)
      jcas.throwFeatMissing("LDCTYPE", "ac.biu.nlp.nlp.ace_uima.uima.EntityMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_LDCTYPE, v);}
    
  
 
  /** @generated */
  final Feature casFeat_LDCATR;
  /** @generated */
  final int     casFeatCode_LDCATR;
  /** @generated */ 
  public String getLDCATR(int addr) {
        if (featOkTst && casFeat_LDCATR == null)
      jcas.throwFeatMissing("LDCATR", "ac.biu.nlp.nlp.ace_uima.uima.EntityMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_LDCATR);
  }
  /** @generated */    
  public void setLDCATR(int addr, String v) {
        if (featOkTst && casFeat_LDCATR == null)
      jcas.throwFeatMissing("LDCATR", "ac.biu.nlp.nlp.ace_uima.uima.EntityMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_LDCATR, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public EntityMention_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_TYPE = jcas.getRequiredFeatureDE(casType, "TYPE", "uima.cas.String", featOkTst);
    casFeatCode_TYPE  = (null == casFeat_TYPE) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TYPE).getCode();

 
    casFeat_LDCTYPE = jcas.getRequiredFeatureDE(casType, "LDCTYPE", "uima.cas.String", featOkTst);
    casFeatCode_LDCTYPE  = (null == casFeat_LDCTYPE) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_LDCTYPE).getCode();

 
    casFeat_LDCATR = jcas.getRequiredFeatureDE(casType, "LDCATR", "uima.cas.String", featOkTst);
    casFeatCode_LDCATR  = (null == casFeat_LDCATR) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_LDCATR).getCode();

  }
}



    