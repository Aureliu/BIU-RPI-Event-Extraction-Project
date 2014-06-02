

/* First created by JCasGen Tue Jul 09 16:21:26 IDT 2013 */
package ac.biu.nlp.nlp.ace_uima.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Jul 15 00:03:28 IDT 2013
 * XML source: C:/Java/Git/lab/nlp-lab/Trunk/Common/Projects/ace_uima/src/main/resources/desc/DummyAE.xml
 * @generated */
public class EventMentionLdcScope extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(EventMentionLdcScope.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected EventMentionLdcScope() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public EventMentionLdcScope(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public EventMentionLdcScope(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public EventMentionLdcScope(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: eventMention

  /** getter for eventMention - gets 
   * @generated */
  public EventMention getEventMention() {
    if (EventMentionLdcScope_Type.featOkTst && ((EventMentionLdcScope_Type)jcasType).casFeat_eventMention == null)
      jcasType.jcas.throwFeatMissing("eventMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionLdcScope");
    return (EventMention)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((EventMentionLdcScope_Type)jcasType).casFeatCode_eventMention)));}
    
  /** setter for eventMention - sets  
   * @generated */
  public void setEventMention(EventMention v) {
    if (EventMentionLdcScope_Type.featOkTst && ((EventMentionLdcScope_Type)jcasType).casFeat_eventMention == null)
      jcasType.jcas.throwFeatMissing("eventMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionLdcScope");
    jcasType.ll_cas.ll_setRefValue(addr, ((EventMentionLdcScope_Type)jcasType).casFeatCode_eventMention, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    