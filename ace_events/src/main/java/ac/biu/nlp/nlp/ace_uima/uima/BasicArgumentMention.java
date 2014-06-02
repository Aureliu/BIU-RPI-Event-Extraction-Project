

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
public class BasicArgumentMention extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(BasicArgumentMention.class);
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
  protected BasicArgumentMention() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public BasicArgumentMention(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public BasicArgumentMention(JCas jcas) {
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
    if (BasicArgumentMention_Type.featOkTst && ((BasicArgumentMention_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets  
   * @generated */
  public void setID(String v) {
    if (BasicArgumentMention_Type.featOkTst && ((BasicArgumentMention_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: arg

  /** getter for arg - gets 
   * @generated */
  public BasicArgument getArg() {
    if (BasicArgumentMention_Type.featOkTst && ((BasicArgumentMention_Type)jcasType).casFeat_arg == null)
      jcasType.jcas.throwFeatMissing("arg", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention");
    return (BasicArgument)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_arg)));}
    
  /** setter for arg - sets  
   * @generated */
  public void setArg(BasicArgument v) {
    if (BasicArgumentMention_Type.featOkTst && ((BasicArgumentMention_Type)jcasType).casFeat_arg == null)
      jcasType.jcas.throwFeatMissing("arg", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention");
    jcasType.ll_cas.ll_setRefValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_arg, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: extent

  /** getter for extent - gets 
   * @generated */
  public BasicArgumentMentionExtent getExtent() {
    if (BasicArgumentMention_Type.featOkTst && ((BasicArgumentMention_Type)jcasType).casFeat_extent == null)
      jcasType.jcas.throwFeatMissing("extent", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention");
    return (BasicArgumentMentionExtent)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_extent)));}
    
  /** setter for extent - sets  
   * @generated */
  public void setExtent(BasicArgumentMentionExtent v) {
    if (BasicArgumentMention_Type.featOkTst && ((BasicArgumentMention_Type)jcasType).casFeat_extent == null)
      jcasType.jcas.throwFeatMissing("extent", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention");
    jcasType.ll_cas.ll_setRefValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_extent, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: eventMentionArguments

  /** getter for eventMentionArguments - gets 
   * @generated */
  public FSArray getEventMentionArguments() {
    if (BasicArgumentMention_Type.featOkTst && ((BasicArgumentMention_Type)jcasType).casFeat_eventMentionArguments == null)
      jcasType.jcas.throwFeatMissing("eventMentionArguments", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_eventMentionArguments)));}
    
  /** setter for eventMentionArguments - sets  
   * @generated */
  public void setEventMentionArguments(FSArray v) {
    if (BasicArgumentMention_Type.featOkTst && ((BasicArgumentMention_Type)jcasType).casFeat_eventMentionArguments == null)
      jcasType.jcas.throwFeatMissing("eventMentionArguments", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention");
    jcasType.ll_cas.ll_setRefValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_eventMentionArguments, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for eventMentionArguments - gets an indexed value - 
   * @generated */
  public EventMentionArgument getEventMentionArguments(int i) {
    if (BasicArgumentMention_Type.featOkTst && ((BasicArgumentMention_Type)jcasType).casFeat_eventMentionArguments == null)
      jcasType.jcas.throwFeatMissing("eventMentionArguments", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_eventMentionArguments), i);
    return (EventMentionArgument)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_eventMentionArguments), i)));}

  /** indexed setter for eventMentionArguments - sets an indexed value - 
   * @generated */
  public void setEventMentionArguments(int i, EventMentionArgument v) { 
    if (BasicArgumentMention_Type.featOkTst && ((BasicArgumentMention_Type)jcasType).casFeat_eventMentionArguments == null)
      jcasType.jcas.throwFeatMissing("eventMentionArguments", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_eventMentionArguments), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_eventMentionArguments), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: head

  /** getter for head - gets 
   * @generated */
  public BasicArgumentMentionHead getHead() {
    if (BasicArgumentMention_Type.featOkTst && ((BasicArgumentMention_Type)jcasType).casFeat_head == null)
      jcasType.jcas.throwFeatMissing("head", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention");
    return (BasicArgumentMentionHead)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_head)));}
    
  /** setter for head - sets  
   * @generated */
  public void setHead(BasicArgumentMentionHead v) {
    if (BasicArgumentMention_Type.featOkTst && ((BasicArgumentMention_Type)jcasType).casFeat_head == null)
      jcasType.jcas.throwFeatMissing("head", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention");
    jcasType.ll_cas.ll_setRefValue(addr, ((BasicArgumentMention_Type)jcasType).casFeatCode_head, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    