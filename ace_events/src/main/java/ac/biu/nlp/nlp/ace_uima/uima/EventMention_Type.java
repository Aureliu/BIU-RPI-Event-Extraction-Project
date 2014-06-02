
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
public class EventMention_Type extends TOP_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (EventMention_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = EventMention_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new EventMention(addr, EventMention_Type.this);
  			   EventMention_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new EventMention(addr, EventMention_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = EventMention.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ace_uima.uima.EventMention");
 
  /** @generated */
  final Feature casFeat_event;
  /** @generated */
  final int     casFeatCode_event;
  /** @generated */ 
  public int getEvent(int addr) {
        if (featOkTst && casFeat_event == null)
      jcas.throwFeatMissing("event", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    return ll_cas.ll_getRefValue(addr, casFeatCode_event);
  }
  /** @generated */    
  public void setEvent(int addr, int v) {
        if (featOkTst && casFeat_event == null)
      jcas.throwFeatMissing("event", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    ll_cas.ll_setRefValue(addr, casFeatCode_event, v);}
    
  
 
  /** @generated */
  final Feature casFeat_ID;
  /** @generated */
  final int     casFeatCode_ID;
  /** @generated */ 
  public String getID(int addr) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ID);
  }
  /** @generated */    
  public void setID(int addr, String v) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_ID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_extent;
  /** @generated */
  final int     casFeatCode_extent;
  /** @generated */ 
  public int getExtent(int addr) {
        if (featOkTst && casFeat_extent == null)
      jcas.throwFeatMissing("extent", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    return ll_cas.ll_getRefValue(addr, casFeatCode_extent);
  }
  /** @generated */    
  public void setExtent(int addr, int v) {
        if (featOkTst && casFeat_extent == null)
      jcas.throwFeatMissing("extent", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    ll_cas.ll_setRefValue(addr, casFeatCode_extent, v);}
    
  
 
  /** @generated */
  final Feature casFeat_ldcScope;
  /** @generated */
  final int     casFeatCode_ldcScope;
  /** @generated */ 
  public int getLdcScope(int addr) {
        if (featOkTst && casFeat_ldcScope == null)
      jcas.throwFeatMissing("ldcScope", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    return ll_cas.ll_getRefValue(addr, casFeatCode_ldcScope);
  }
  /** @generated */    
  public void setLdcScope(int addr, int v) {
        if (featOkTst && casFeat_ldcScope == null)
      jcas.throwFeatMissing("ldcScope", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    ll_cas.ll_setRefValue(addr, casFeatCode_ldcScope, v);}
    
  
 
  /** @generated */
  final Feature casFeat_anchor;
  /** @generated */
  final int     casFeatCode_anchor;
  /** @generated */ 
  public int getAnchor(int addr) {
        if (featOkTst && casFeat_anchor == null)
      jcas.throwFeatMissing("anchor", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    return ll_cas.ll_getRefValue(addr, casFeatCode_anchor);
  }
  /** @generated */    
  public void setAnchor(int addr, int v) {
        if (featOkTst && casFeat_anchor == null)
      jcas.throwFeatMissing("anchor", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    ll_cas.ll_setRefValue(addr, casFeatCode_anchor, v);}
    
  
 
  /** @generated */
  final Feature casFeat_eventMentionArguments;
  /** @generated */
  final int     casFeatCode_eventMentionArguments;
  /** @generated */ 
  public int getEventMentionArguments(int addr) {
        if (featOkTst && casFeat_eventMentionArguments == null)
      jcas.throwFeatMissing("eventMentionArguments", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    return ll_cas.ll_getRefValue(addr, casFeatCode_eventMentionArguments);
  }
  /** @generated */    
  public void setEventMentionArguments(int addr, int v) {
        if (featOkTst && casFeat_eventMentionArguments == null)
      jcas.throwFeatMissing("eventMentionArguments", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    ll_cas.ll_setRefValue(addr, casFeatCode_eventMentionArguments, v);}
    
   /** @generated */
  public int getEventMentionArguments(int addr, int i) {
        if (featOkTst && casFeat_eventMentionArguments == null)
      jcas.throwFeatMissing("eventMentionArguments", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventMentionArguments), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_eventMentionArguments), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventMentionArguments), i);
  }
   
  /** @generated */ 
  public void setEventMentionArguments(int addr, int i, int v) {
        if (featOkTst && casFeat_eventMentionArguments == null)
      jcas.throwFeatMissing("eventMentionArguments", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventMentionArguments), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_eventMentionArguments), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventMentionArguments), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public EventMention_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_event = jcas.getRequiredFeatureDE(casType, "event", "ac.biu.nlp.nlp.ace_uima.uima.Event", featOkTst);
    casFeatCode_event  = (null == casFeat_event) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_event).getCode();

 
    casFeat_ID = jcas.getRequiredFeatureDE(casType, "ID", "uima.cas.String", featOkTst);
    casFeatCode_ID  = (null == casFeat_ID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ID).getCode();

 
    casFeat_extent = jcas.getRequiredFeatureDE(casType, "extent", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionExtent", featOkTst);
    casFeatCode_extent  = (null == casFeat_extent) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_extent).getCode();

 
    casFeat_ldcScope = jcas.getRequiredFeatureDE(casType, "ldcScope", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionLdcScope", featOkTst);
    casFeatCode_ldcScope  = (null == casFeat_ldcScope) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ldcScope).getCode();

 
    casFeat_anchor = jcas.getRequiredFeatureDE(casType, "anchor", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionAnchor", featOkTst);
    casFeatCode_anchor  = (null == casFeat_anchor) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_anchor).getCode();

 
    casFeat_eventMentionArguments = jcas.getRequiredFeatureDE(casType, "eventMentionArguments", "uima.cas.FSArray", featOkTst);
    casFeatCode_eventMentionArguments  = (null == casFeat_eventMentionArguments) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_eventMentionArguments).getCode();

  }
}



    