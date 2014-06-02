
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
import org.apache.uima.jcas.cas.TOP_Type;

/** 
 * Updated by JCasGen Mon Jul 15 00:03:28 IDT 2013
 * @generated */
public class BasicArgument_Type extends TOP_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (BasicArgument_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = BasicArgument_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new BasicArgument(addr, BasicArgument_Type.this);
  			   BasicArgument_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new BasicArgument(addr, BasicArgument_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = BasicArgument.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
 
  /** @generated */
  final Feature casFeat_ID;
  /** @generated */
  final int     casFeatCode_ID;
  /** @generated */ 
  public String getID(int addr) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ID);
  }
  /** @generated */    
  public void setID(int addr, String v) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    ll_cas.ll_setStringValue(addr, casFeatCode_ID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_mentions;
  /** @generated */
  final int     casFeatCode_mentions;
  /** @generated */ 
  public int getMentions(int addr) {
        if (featOkTst && casFeat_mentions == null)
      jcas.throwFeatMissing("mentions", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    return ll_cas.ll_getRefValue(addr, casFeatCode_mentions);
  }
  /** @generated */    
  public void setMentions(int addr, int v) {
        if (featOkTst && casFeat_mentions == null)
      jcas.throwFeatMissing("mentions", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    ll_cas.ll_setRefValue(addr, casFeatCode_mentions, v);}
    
   /** @generated */
  public int getMentions(int addr, int i) {
        if (featOkTst && casFeat_mentions == null)
      jcas.throwFeatMissing("mentions", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_mentions), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_mentions), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_mentions), i);
  }
   
  /** @generated */ 
  public void setMentions(int addr, int i, int v) {
        if (featOkTst && casFeat_mentions == null)
      jcas.throwFeatMissing("mentions", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_mentions), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_mentions), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_mentions), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_eventArguments;
  /** @generated */
  final int     casFeatCode_eventArguments;
  /** @generated */ 
  public int getEventArguments(int addr) {
        if (featOkTst && casFeat_eventArguments == null)
      jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    return ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments);
  }
  /** @generated */    
  public void setEventArguments(int addr, int v) {
        if (featOkTst && casFeat_eventArguments == null)
      jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    ll_cas.ll_setRefValue(addr, casFeatCode_eventArguments, v);}
    
   /** @generated */
  public int getEventArguments(int addr, int i) {
        if (featOkTst && casFeat_eventArguments == null)
      jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments), i);
  }
   
  /** @generated */ 
  public void setEventArguments(int addr, int i, int v) {
        if (featOkTst && casFeat_eventArguments == null)
      jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public BasicArgument_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_ID = jcas.getRequiredFeatureDE(casType, "ID", "uima.cas.String", featOkTst);
    casFeatCode_ID  = (null == casFeat_ID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ID).getCode();

 
    casFeat_mentions = jcas.getRequiredFeatureDE(casType, "mentions", "uima.cas.FSArray", featOkTst);
    casFeatCode_mentions  = (null == casFeat_mentions) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_mentions).getCode();

 
    casFeat_eventArguments = jcas.getRequiredFeatureDE(casType, "eventArguments", "uima.cas.FSArray", featOkTst);
    casFeatCode_eventArguments  = (null == casFeat_eventArguments) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_eventArguments).getCode();

  }
}



    