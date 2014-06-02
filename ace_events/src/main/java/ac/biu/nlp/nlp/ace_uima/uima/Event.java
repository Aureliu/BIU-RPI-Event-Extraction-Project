

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
public class Event extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Event.class);
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
  protected Event() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Event(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Event(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: ID

  /** getter for ID - gets 
   * @generated */
  public String getID() {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Event_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets  
   * @generated */
  public void setID(String v) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    jcasType.ll_cas.ll_setStringValue(addr, ((Event_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: TYPE

  /** getter for TYPE - gets 
   * @generated */
  public String getTYPE() {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_TYPE == null)
      jcasType.jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Event_Type)jcasType).casFeatCode_TYPE);}
    
  /** setter for TYPE - sets  
   * @generated */
  public void setTYPE(String v) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_TYPE == null)
      jcasType.jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    jcasType.ll_cas.ll_setStringValue(addr, ((Event_Type)jcasType).casFeatCode_TYPE, v);}    
   
    
  //*--------------*
  //* Feature: SUBTYPE

  /** getter for SUBTYPE - gets 
   * @generated */
  public String getSUBTYPE() {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_SUBTYPE == null)
      jcasType.jcas.throwFeatMissing("SUBTYPE", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Event_Type)jcasType).casFeatCode_SUBTYPE);}
    
  /** setter for SUBTYPE - sets  
   * @generated */
  public void setSUBTYPE(String v) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_SUBTYPE == null)
      jcasType.jcas.throwFeatMissing("SUBTYPE", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    jcasType.ll_cas.ll_setStringValue(addr, ((Event_Type)jcasType).casFeatCode_SUBTYPE, v);}    
   
    
  //*--------------*
  //* Feature: MODALITY

  /** getter for MODALITY - gets 
   * @generated */
  public String getMODALITY() {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_MODALITY == null)
      jcasType.jcas.throwFeatMissing("MODALITY", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Event_Type)jcasType).casFeatCode_MODALITY);}
    
  /** setter for MODALITY - sets  
   * @generated */
  public void setMODALITY(String v) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_MODALITY == null)
      jcasType.jcas.throwFeatMissing("MODALITY", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    jcasType.ll_cas.ll_setStringValue(addr, ((Event_Type)jcasType).casFeatCode_MODALITY, v);}    
   
    
  //*--------------*
  //* Feature: POLARITY

  /** getter for POLARITY - gets 
   * @generated */
  public String getPOLARITY() {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_POLARITY == null)
      jcasType.jcas.throwFeatMissing("POLARITY", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Event_Type)jcasType).casFeatCode_POLARITY);}
    
  /** setter for POLARITY - sets  
   * @generated */
  public void setPOLARITY(String v) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_POLARITY == null)
      jcasType.jcas.throwFeatMissing("POLARITY", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    jcasType.ll_cas.ll_setStringValue(addr, ((Event_Type)jcasType).casFeatCode_POLARITY, v);}    
   
    
  //*--------------*
  //* Feature: GENERICITY

  /** getter for GENERICITY - gets 
   * @generated */
  public String getGENERICITY() {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_GENERICITY == null)
      jcasType.jcas.throwFeatMissing("GENERICITY", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Event_Type)jcasType).casFeatCode_GENERICITY);}
    
  /** setter for GENERICITY - sets  
   * @generated */
  public void setGENERICITY(String v) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_GENERICITY == null)
      jcasType.jcas.throwFeatMissing("GENERICITY", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    jcasType.ll_cas.ll_setStringValue(addr, ((Event_Type)jcasType).casFeatCode_GENERICITY, v);}    
   
    
  //*--------------*
  //* Feature: TENSE

  /** getter for TENSE - gets 
   * @generated */
  public String getTENSE() {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_TENSE == null)
      jcasType.jcas.throwFeatMissing("TENSE", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Event_Type)jcasType).casFeatCode_TENSE);}
    
  /** setter for TENSE - sets  
   * @generated */
  public void setTENSE(String v) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_TENSE == null)
      jcasType.jcas.throwFeatMissing("TENSE", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    jcasType.ll_cas.ll_setStringValue(addr, ((Event_Type)jcasType).casFeatCode_TENSE, v);}    
   
    
  //*--------------*
  //* Feature: eventArguments

  /** getter for eventArguments - gets 
   * @generated */
  public FSArray getEventArguments() {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_eventArguments == null)
      jcasType.jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type)jcasType).casFeatCode_eventArguments)));}
    
  /** setter for eventArguments - sets  
   * @generated */
  public void setEventArguments(FSArray v) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_eventArguments == null)
      jcasType.jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    jcasType.ll_cas.ll_setRefValue(addr, ((Event_Type)jcasType).casFeatCode_eventArguments, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for eventArguments - gets an indexed value - 
   * @generated */
  public EventArgument getEventArguments(int i) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_eventArguments == null)
      jcasType.jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type)jcasType).casFeatCode_eventArguments), i);
    return (EventArgument)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type)jcasType).casFeatCode_eventArguments), i)));}

  /** indexed setter for eventArguments - sets an indexed value - 
   * @generated */
  public void setEventArguments(int i, EventArgument v) { 
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_eventArguments == null)
      jcasType.jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type)jcasType).casFeatCode_eventArguments), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type)jcasType).casFeatCode_eventArguments), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: eventMentions

  /** getter for eventMentions - gets 
   * @generated */
  public FSArray getEventMentions() {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_eventMentions == null)
      jcasType.jcas.throwFeatMissing("eventMentions", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type)jcasType).casFeatCode_eventMentions)));}
    
  /** setter for eventMentions - sets  
   * @generated */
  public void setEventMentions(FSArray v) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_eventMentions == null)
      jcasType.jcas.throwFeatMissing("eventMentions", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    jcasType.ll_cas.ll_setRefValue(addr, ((Event_Type)jcasType).casFeatCode_eventMentions, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for eventMentions - gets an indexed value - 
   * @generated */
  public EventMention getEventMentions(int i) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_eventMentions == null)
      jcasType.jcas.throwFeatMissing("eventMentions", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type)jcasType).casFeatCode_eventMentions), i);
    return (EventMention)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type)jcasType).casFeatCode_eventMentions), i)));}

  /** indexed setter for eventMentions - sets an indexed value - 
   * @generated */
  public void setEventMentions(int i, EventMention v) { 
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_eventMentions == null)
      jcasType.jcas.throwFeatMissing("eventMentions", "ac.biu.nlp.nlp.ace_uima.uima.Event");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type)jcasType).casFeatCode_eventMentions), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type)jcasType).casFeatCode_eventMentions), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    