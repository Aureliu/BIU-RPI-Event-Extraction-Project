

/* First created by JCasGen Tue Jul 09 16:21:26 IDT 2013 */
package ac.biu.nlp.nlp.ace_uima.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Mon Jul 15 00:03:28 IDT 2013
 * XML source: C:/Java/Git/lab/nlp-lab/Trunk/Common/Projects/ace_uima/src/main/resources/desc/DummyAE.xml
 * @generated */
public class EventMention extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(EventMention.class);
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
  protected EventMention() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public EventMention(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public EventMention(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: event

  /** getter for event - gets 
   * @generated */
  public Event getEvent() {
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_event == null)
      jcasType.jcas.throwFeatMissing("event", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    return (Event)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_event)));}
    
  /** setter for event - sets  
   * @generated */
  public void setEvent(Event v) {
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_event == null)
      jcasType.jcas.throwFeatMissing("event", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    jcasType.ll_cas.ll_setRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_event, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: ID

  /** getter for ID - gets 
   * @generated */
  public String getID() {
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EventMention_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets  
   * @generated */
  public void setID(String v) {
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((EventMention_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: extent

  /** getter for extent - gets 
   * @generated */
  public EventMentionExtent getExtent() {
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_extent == null)
      jcasType.jcas.throwFeatMissing("extent", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    return (EventMentionExtent)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_extent)));}
    
  /** setter for extent - sets  
   * @generated */
  public void setExtent(EventMentionExtent v) {
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_extent == null)
      jcasType.jcas.throwFeatMissing("extent", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    jcasType.ll_cas.ll_setRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_extent, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: ldcScope

  /** getter for ldcScope - gets 
   * @generated */
  public EventMentionLdcScope getLdcScope() {
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_ldcScope == null)
      jcasType.jcas.throwFeatMissing("ldcScope", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    return (EventMentionLdcScope)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_ldcScope)));}
    
  /** setter for ldcScope - sets  
   * @generated */
  public void setLdcScope(EventMentionLdcScope v) {
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_ldcScope == null)
      jcasType.jcas.throwFeatMissing("ldcScope", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    jcasType.ll_cas.ll_setRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_ldcScope, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: anchor

  /** getter for anchor - gets 
   * @generated */
  public EventMentionAnchor getAnchor() {
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_anchor == null)
      jcasType.jcas.throwFeatMissing("anchor", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    return (EventMentionAnchor)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_anchor)));}
    
  /** setter for anchor - sets  
   * @generated */
  public void setAnchor(EventMentionAnchor v) {
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_anchor == null)
      jcasType.jcas.throwFeatMissing("anchor", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    jcasType.ll_cas.ll_setRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_anchor, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: eventMentionArguments

  /** getter for eventMentionArguments - gets 
   * @generated */
  public FSArray getEventMentionArguments() {
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_eventMentionArguments == null)
      jcasType.jcas.throwFeatMissing("eventMentionArguments", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_eventMentionArguments)));}
    
  /** setter for eventMentionArguments - sets  
   * @generated */
  public void setEventMentionArguments(FSArray v) {
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_eventMentionArguments == null)
      jcasType.jcas.throwFeatMissing("eventMentionArguments", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    jcasType.ll_cas.ll_setRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_eventMentionArguments, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for eventMentionArguments - gets an indexed value - 
   * @generated */
  public EventMentionArgument getEventMentionArguments(int i) {
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_eventMentionArguments == null)
      jcasType.jcas.throwFeatMissing("eventMentionArguments", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_eventMentionArguments), i);
    return (EventMentionArgument)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_eventMentionArguments), i)));}

  /** indexed setter for eventMentionArguments - sets an indexed value - 
   * @generated */
  public void setEventMentionArguments(int i, EventMentionArgument v) { 
    if (EventMention_Type.featOkTst && ((EventMention_Type)jcasType).casFeat_eventMentionArguments == null)
      jcasType.jcas.throwFeatMissing("eventMentionArguments", "ac.biu.nlp.nlp.ace_uima.uima.EventMention");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_eventMentionArguments), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((EventMention_Type)jcasType).casFeatCode_eventMentionArguments), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    