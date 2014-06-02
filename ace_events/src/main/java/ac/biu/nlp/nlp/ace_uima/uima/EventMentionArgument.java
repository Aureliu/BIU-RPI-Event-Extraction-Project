

/* First created by JCasGen Tue Jul 09 16:21:26 IDT 2013 */
package ac.biu.nlp.nlp.ace_uima.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Mon Jul 15 00:03:28 IDT 2013
 * XML source: C:/Java/Git/lab/nlp-lab/Trunk/Common/Projects/ace_uima/src/main/resources/desc/DummyAE.xml
 * @generated */
public class EventMentionArgument extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(EventMentionArgument.class);
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
  protected EventMentionArgument() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public EventMentionArgument(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public EventMentionArgument(JCas jcas) {
    super(jcas);
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
    if (EventMentionArgument_Type.featOkTst && ((EventMentionArgument_Type)jcasType).casFeat_eventMention == null)
      jcasType.jcas.throwFeatMissing("eventMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    return (EventMention)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((EventMentionArgument_Type)jcasType).casFeatCode_eventMention)));}
    
  /** setter for eventMention - sets  
   * @generated */
  public void setEventMention(EventMention v) {
    if (EventMentionArgument_Type.featOkTst && ((EventMentionArgument_Type)jcasType).casFeat_eventMention == null)
      jcasType.jcas.throwFeatMissing("eventMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    jcasType.ll_cas.ll_setRefValue(addr, ((EventMentionArgument_Type)jcasType).casFeatCode_eventMention, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: argMention

  /** getter for argMention - gets 
   * @generated */
  public BasicArgumentMention getArgMention() {
    if (EventMentionArgument_Type.featOkTst && ((EventMentionArgument_Type)jcasType).casFeat_argMention == null)
      jcasType.jcas.throwFeatMissing("argMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    return (BasicArgumentMention)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((EventMentionArgument_Type)jcasType).casFeatCode_argMention)));}
    
  /** setter for argMention - sets  
   * @generated */
  public void setArgMention(BasicArgumentMention v) {
    if (EventMentionArgument_Type.featOkTst && ((EventMentionArgument_Type)jcasType).casFeat_argMention == null)
      jcasType.jcas.throwFeatMissing("argMention", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    jcasType.ll_cas.ll_setRefValue(addr, ((EventMentionArgument_Type)jcasType).casFeatCode_argMention, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: eventArgument

  /** getter for eventArgument - gets 
   * @generated */
  public EventArgument getEventArgument() {
    if (EventMentionArgument_Type.featOkTst && ((EventMentionArgument_Type)jcasType).casFeat_eventArgument == null)
      jcasType.jcas.throwFeatMissing("eventArgument", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    return (EventArgument)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((EventMentionArgument_Type)jcasType).casFeatCode_eventArgument)));}
    
  /** setter for eventArgument - sets  
   * @generated */
  public void setEventArgument(EventArgument v) {
    if (EventMentionArgument_Type.featOkTst && ((EventMentionArgument_Type)jcasType).casFeat_eventArgument == null)
      jcasType.jcas.throwFeatMissing("eventArgument", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    jcasType.ll_cas.ll_setRefValue(addr, ((EventMentionArgument_Type)jcasType).casFeatCode_eventArgument, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: role

  /** getter for role - gets 
   * @generated */
  public String getRole() {
    if (EventMentionArgument_Type.featOkTst && ((EventMentionArgument_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EventMentionArgument_Type)jcasType).casFeatCode_role);}
    
  /** setter for role - sets  
   * @generated */
  public void setRole(String v) {
    if (EventMentionArgument_Type.featOkTst && ((EventMentionArgument_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument");
    jcasType.ll_cas.ll_setStringValue(addr, ((EventMentionArgument_Type)jcasType).casFeatCode_role, v);}    
  }

    