
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
public class Event_Type extends TOP_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Event_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Event_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Event(addr, Event_Type.this);
  			   Event_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Event(addr, Event_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Event.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ace_uima.uima.Event");
 
  /** @generated */
  final Feature casFeat_ID;
  /** @generated */
  final int     casFeatCode_ID;
  /** @generated */ 
  public String getID(int addr) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ID);
  }
  /** @generated */    
  public void setID(int addr, String v) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    ll_cas.ll_setStringValue(addr, casFeatCode_ID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_TYPE;
  /** @generated */
  final int     casFeatCode_TYPE;
  /** @generated */ 
  public String getTYPE(int addr) {
        if (featOkTst && casFeat_TYPE == null)
      jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TYPE);
  }
  /** @generated */    
  public void setTYPE(int addr, String v) {
        if (featOkTst && casFeat_TYPE == null)
      jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    ll_cas.ll_setStringValue(addr, casFeatCode_TYPE, v);}
    
  
 
  /** @generated */
  final Feature casFeat_SUBTYPE;
  /** @generated */
  final int     casFeatCode_SUBTYPE;
  /** @generated */ 
  public String getSUBTYPE(int addr) {
        if (featOkTst && casFeat_SUBTYPE == null)
      jcas.throwFeatMissing("SUBTYPE", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SUBTYPE);
  }
  /** @generated */    
  public void setSUBTYPE(int addr, String v) {
        if (featOkTst && casFeat_SUBTYPE == null)
      jcas.throwFeatMissing("SUBTYPE", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    ll_cas.ll_setStringValue(addr, casFeatCode_SUBTYPE, v);}
    
  
 
  /** @generated */
  final Feature casFeat_MODALITY;
  /** @generated */
  final int     casFeatCode_MODALITY;
  /** @generated */ 
  public String getMODALITY(int addr) {
        if (featOkTst && casFeat_MODALITY == null)
      jcas.throwFeatMissing("MODALITY", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return ll_cas.ll_getStringValue(addr, casFeatCode_MODALITY);
  }
  /** @generated */    
  public void setMODALITY(int addr, String v) {
        if (featOkTst && casFeat_MODALITY == null)
      jcas.throwFeatMissing("MODALITY", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    ll_cas.ll_setStringValue(addr, casFeatCode_MODALITY, v);}
    
  
 
  /** @generated */
  final Feature casFeat_POLARITY;
  /** @generated */
  final int     casFeatCode_POLARITY;
  /** @generated */ 
  public String getPOLARITY(int addr) {
        if (featOkTst && casFeat_POLARITY == null)
      jcas.throwFeatMissing("POLARITY", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return ll_cas.ll_getStringValue(addr, casFeatCode_POLARITY);
  }
  /** @generated */    
  public void setPOLARITY(int addr, String v) {
        if (featOkTst && casFeat_POLARITY == null)
      jcas.throwFeatMissing("POLARITY", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    ll_cas.ll_setStringValue(addr, casFeatCode_POLARITY, v);}
    
  
 
  /** @generated */
  final Feature casFeat_GENERICITY;
  /** @generated */
  final int     casFeatCode_GENERICITY;
  /** @generated */ 
  public String getGENERICITY(int addr) {
        if (featOkTst && casFeat_GENERICITY == null)
      jcas.throwFeatMissing("GENERICITY", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return ll_cas.ll_getStringValue(addr, casFeatCode_GENERICITY);
  }
  /** @generated */    
  public void setGENERICITY(int addr, String v) {
        if (featOkTst && casFeat_GENERICITY == null)
      jcas.throwFeatMissing("GENERICITY", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    ll_cas.ll_setStringValue(addr, casFeatCode_GENERICITY, v);}
    
  
 
  /** @generated */
  final Feature casFeat_TENSE;
  /** @generated */
  final int     casFeatCode_TENSE;
  /** @generated */ 
  public String getTENSE(int addr) {
        if (featOkTst && casFeat_TENSE == null)
      jcas.throwFeatMissing("TENSE", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TENSE);
  }
  /** @generated */    
  public void setTENSE(int addr, String v) {
        if (featOkTst && casFeat_TENSE == null)
      jcas.throwFeatMissing("TENSE", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    ll_cas.ll_setStringValue(addr, casFeatCode_TENSE, v);}
    
  
 
  /** @generated */
  final Feature casFeat_eventArguments;
  /** @generated */
  final int     casFeatCode_eventArguments;
  /** @generated */ 
  public int getEventArguments(int addr) {
        if (featOkTst && casFeat_eventArguments == null)
      jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments);
  }
  /** @generated */    
  public void setEventArguments(int addr, int v) {
        if (featOkTst && casFeat_eventArguments == null)
      jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    ll_cas.ll_setRefValue(addr, casFeatCode_eventArguments, v);}
    
   /** @generated */
  public int getEventArguments(int addr, int i) {
        if (featOkTst && casFeat_eventArguments == null)
      jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments), i);
  }
   
  /** @generated */ 
  public void setEventArguments(int addr, int i, int v) {
        if (featOkTst && casFeat_eventArguments == null)
      jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventArguments), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_eventMentions;
  /** @generated */
  final int     casFeatCode_eventMentions;
  /** @generated */ 
  public int getEventMentions(int addr) {
        if (featOkTst && casFeat_eventMentions == null)
      jcas.throwFeatMissing("eventMentions", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return ll_cas.ll_getRefValue(addr, casFeatCode_eventMentions);
  }
  /** @generated */    
  public void setEventMentions(int addr, int v) {
        if (featOkTst && casFeat_eventMentions == null)
      jcas.throwFeatMissing("eventMentions", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    ll_cas.ll_setRefValue(addr, casFeatCode_eventMentions, v);}
    
   /** @generated */
  public int getEventMentions(int addr, int i) {
        if (featOkTst && casFeat_eventMentions == null)
      jcas.throwFeatMissing("eventMentions", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventMentions), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_eventMentions), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventMentions), i);
  }
   
  /** @generated */ 
  public void setEventMentions(int addr, int i, int v) {
        if (featOkTst && casFeat_eventMentions == null)
      jcas.throwFeatMissing("eventMentions", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventMentions), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_eventMentions), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventMentions), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Event_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_ID = jcas.getRequiredFeatureDE(casType, "ID", "uima.cas.String", featOkTst);
    casFeatCode_ID  = (null == casFeat_ID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ID).getCode();

 
    casFeat_TYPE = jcas.getRequiredFeatureDE(casType, "TYPE", "uima.cas.String", featOkTst);
    casFeatCode_TYPE  = (null == casFeat_TYPE) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TYPE).getCode();

 
    casFeat_SUBTYPE = jcas.getRequiredFeatureDE(casType, "SUBTYPE", "uima.cas.String", featOkTst);
    casFeatCode_SUBTYPE  = (null == casFeat_SUBTYPE) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SUBTYPE).getCode();

 
    casFeat_MODALITY = jcas.getRequiredFeatureDE(casType, "MODALITY", "uima.cas.String", featOkTst);
    casFeatCode_MODALITY  = (null == casFeat_MODALITY) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_MODALITY).getCode();

 
    casFeat_POLARITY = jcas.getRequiredFeatureDE(casType, "POLARITY", "uima.cas.String", featOkTst);
    casFeatCode_POLARITY  = (null == casFeat_POLARITY) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_POLARITY).getCode();

 
    casFeat_GENERICITY = jcas.getRequiredFeatureDE(casType, "GENERICITY", "uima.cas.String", featOkTst);
    casFeatCode_GENERICITY  = (null == casFeat_GENERICITY) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_GENERICITY).getCode();

 
    casFeat_TENSE = jcas.getRequiredFeatureDE(casType, "TENSE", "uima.cas.String", featOkTst);
    casFeatCode_TENSE  = (null == casFeat_TENSE) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TENSE).getCode();

 
    casFeat_eventArguments = jcas.getRequiredFeatureDE(casType, "eventArguments", "uima.cas.FSArray", featOkTst);
    casFeatCode_eventArguments  = (null == casFeat_eventArguments) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_eventArguments).getCode();

 
    casFeat_eventMentions = jcas.getRequiredFeatureDE(casType, "eventMentions", "uima.cas.FSArray", featOkTst);
    casFeatCode_eventMentions  = (null == casFeat_eventMentions) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_eventMentions).getCode();

  }
}



    