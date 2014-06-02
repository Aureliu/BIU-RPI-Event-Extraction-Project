

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
public class BasicArgument extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(BasicArgument.class);
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
  protected BasicArgument() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public BasicArgument(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public BasicArgument(JCas jcas) {
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
    if (BasicArgument_Type.featOkTst && ((BasicArgument_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    return jcasType.ll_cas.ll_getStringValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets  
   * @generated */
  public void setID(String v) {
    if (BasicArgument_Type.featOkTst && ((BasicArgument_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    jcasType.ll_cas.ll_setStringValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: mentions

  /** getter for mentions - gets 
   * @generated */
  public FSArray getMentions() {
    if (BasicArgument_Type.featOkTst && ((BasicArgument_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_mentions)));}
    
  /** setter for mentions - sets  
   * @generated */
  public void setMentions(FSArray v) {
    if (BasicArgument_Type.featOkTst && ((BasicArgument_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    jcasType.ll_cas.ll_setRefValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_mentions, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for mentions - gets an indexed value - 
   * @generated */
  public BasicArgumentMention getMentions(int i) {
    if (BasicArgument_Type.featOkTst && ((BasicArgument_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_mentions), i);
    return (BasicArgumentMention)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_mentions), i)));}

  /** indexed setter for mentions - sets an indexed value - 
   * @generated */
  public void setMentions(int i, BasicArgumentMention v) { 
    if (BasicArgument_Type.featOkTst && ((BasicArgument_Type)jcasType).casFeat_mentions == null)
      jcasType.jcas.throwFeatMissing("mentions", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_mentions), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_mentions), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: eventArguments

  /** getter for eventArguments - gets 
   * @generated */
  public FSArray getEventArguments() {
    if (BasicArgument_Type.featOkTst && ((BasicArgument_Type)jcasType).casFeat_eventArguments == null)
      jcasType.jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_eventArguments)));}
    
  /** setter for eventArguments - sets  
   * @generated */
  public void setEventArguments(FSArray v) {
    if (BasicArgument_Type.featOkTst && ((BasicArgument_Type)jcasType).casFeat_eventArguments == null)
      jcasType.jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    jcasType.ll_cas.ll_setRefValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_eventArguments, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for eventArguments - gets an indexed value - 
   * @generated */
  public EventArgument getEventArguments(int i) {
    if (BasicArgument_Type.featOkTst && ((BasicArgument_Type)jcasType).casFeat_eventArguments == null)
      jcasType.jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_eventArguments), i);
    return (EventArgument)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_eventArguments), i)));}

  /** indexed setter for eventArguments - sets an indexed value - 
   * @generated */
  public void setEventArguments(int i, EventArgument v) { 
    if (BasicArgument_Type.featOkTst && ((BasicArgument_Type)jcasType).casFeat_eventArguments == null)
      jcasType.jcas.throwFeatMissing("eventArguments", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_eventArguments), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgument_Type)jcasType).casFeatCode_eventArguments), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    