
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
public class EventMentionArgument_Type extends TOP_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (EventMentionArgument_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = EventMentionArgument_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new EventMentionArgument(addr, EventMentionArgument_Type.this);
  			   EventMentionArgument_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new EventMentionArgument(addr, EventMentionArgument_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = EventMentionArgument.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
 
  /** @generated */
  final Feature casFeat_eventMention;
  /** @generated */
  final int     casFeatCode_eventMention;
  /** @generated */ 
  public int getEventMention(int addr) {
        if (featOkTst && casFeat_eventMention == null)
      jcas.throwFeatMissing("eventMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    return ll_cas.ll_getRefValue(addr, casFeatCode_eventMention);
  }
  /** @generated */    
  public void setEventMention(int addr, int v) {
        if (featOkTst && casFeat_eventMention == null)
      jcas.throwFeatMissing("eventMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    ll_cas.ll_setRefValue(addr, casFeatCode_eventMention, v);}
    
  
 
  /** @generated */
  final Feature casFeat_argMention;
  /** @generated */
  final int     casFeatCode_argMention;
  /** @generated */ 
  public int getArgMention(int addr) {
        if (featOkTst && casFeat_argMention == null)
      jcas.throwFeatMissing("argMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    return ll_cas.ll_getRefValue(addr, casFeatCode_argMention);
  }
  /** @generated */    
  public void setArgMention(int addr, int v) {
        if (featOkTst && casFeat_argMention == null)
      jcas.throwFeatMissing("argMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    ll_cas.ll_setRefValue(addr, casFeatCode_argMention, v);}
    
  
 
  /** @generated */
  final Feature casFeat_eventArgument;
  /** @generated */
  final int     casFeatCode_eventArgument;
  /** @generated */ 
  public int getEventArgument(int addr) {
        if (featOkTst && casFeat_eventArgument == null)
      jcas.throwFeatMissing("eventArgument", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    return ll_cas.ll_getRefValue(addr, casFeatCode_eventArgument);
  }
  /** @generated */    
  public void setEventArgument(int addr, int v) {
        if (featOkTst && casFeat_eventArgument == null)
      jcas.throwFeatMissing("eventArgument", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    ll_cas.ll_setRefValue(addr, casFeatCode_eventArgument, v);}
    
  
 
  /** @generated */
  final Feature casFeat_role;
  /** @generated */
  final int     casFeatCode_role;
  /** @generated */ 
  public String getRole(int addr) {
        if (featOkTst && casFeat_role == null)
      jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    return ll_cas.ll_getStringValue(addr, casFeatCode_role);
  }
  /** @generated */    
  public void setRole(int addr, String v) {
        if (featOkTst && casFeat_role == null)
      jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    ll_cas.ll_setStringValue(addr, casFeatCode_role, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public EventMentionArgument_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_eventMention = jcas.getRequiredFeatureDE(casType, "eventMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMention", featOkTst);
    casFeatCode_eventMention  = (null == casFeat_eventMention) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_eventMention).getCode();

 
    casFeat_argMention = jcas.getRequiredFeatureDE(casType, "argMention", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention", featOkTst);
    casFeatCode_argMention  = (null == casFeat_argMention) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_argMention).getCode();

 
    casFeat_eventArgument = jcas.getRequiredFeatureDE(casType, "eventArgument", "ac.biu.nlp.nlp.ace_uima.uima.EventArgument", featOkTst);
    casFeatCode_eventArgument  = (null == casFeat_eventArgument) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_eventArgument).getCode();

 
    casFeat_role = jcas.getRequiredFeatureDE(casType, "role", "uima.cas.String", featOkTst);
    casFeatCode_role  = (null == casFeat_role) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_role).getCode();

  }
}



    