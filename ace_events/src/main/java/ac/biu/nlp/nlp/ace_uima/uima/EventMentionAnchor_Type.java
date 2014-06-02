
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
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Jul 15 00:03:28 IDT 2013
 * @generated */
public class EventMentionAnchor_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (EventMentionAnchor_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = EventMentionAnchor_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new EventMentionAnchor(addr, EventMentionAnchor_Type.this);
  			   EventMentionAnchor_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new EventMentionAnchor(addr, EventMentionAnchor_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = EventMentionAnchor.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ace_uima.uima.EventMentionAnchor");
 
  /** @generated */
  final Feature casFeat_eventMention;
  /** @generated */
  final int     casFeatCode_eventMention;
  /** @generated */ 
  public int getEventMention(int addr) {
        if (featOkTst && casFeat_eventMention == null)
      jcas.throwFeatMissing("eventMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionAnchor");
    return ll_cas.ll_getRefValue(addr, casFeatCode_eventMention);
  }
  /** @generated */    
  public void setEventMention(int addr, int v) {
        if (featOkTst && casFeat_eventMention == null)
      jcas.throwFeatMissing("eventMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionAnchor");
    ll_cas.ll_setRefValue(addr, casFeatCode_eventMention, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public EventMentionAnchor_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_eventMention = jcas.getRequiredFeatureDE(casType, "eventMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMention", featOkTst);
    casFeatCode_eventMention  = (null == casFeat_eventMention) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_eventMention).getCode();

  }
}



    