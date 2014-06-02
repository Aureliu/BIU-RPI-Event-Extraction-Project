

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
public class EventArgument extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(EventArgument.class);
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
  protected EventArgument() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public EventArgument(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public EventArgument(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: arg

  /** getter for arg - gets 
   * @generated */
  public BasicArgument getArg() {
    if (EventArgument_Type.featOkTst && ((EventArgument_Type)jcasType).casFeat_arg == null)
      jcasType.jcas.throwFeatMissing("arg", "ac.biu.nlp.nlp.ace_uima.uima.EventArgument");
    return (BasicArgument)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((EventArgument_Type)jcasType).casFeatCode_arg)));}
    
  /** setter for arg - sets  
   * @generated */
  public void setArg(BasicArgument v) {
    if (EventArgument_Type.featOkTst && ((EventArgument_Type)jcasType).casFeat_arg == null)
      jcasType.jcas.throwFeatMissing("arg", "ac.biu.nlp.nlp.ace_uima.uima.EventArgument");
    jcasType.ll_cas.ll_setRefValue(addr, ((EventArgument_Type)jcasType).casFeatCode_arg, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: role

  /** getter for role - gets 
   * @generated */
  public String getRole() {
    if (EventArgument_Type.featOkTst && ((EventArgument_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ace_uima.uima.EventArgument");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EventArgument_Type)jcasType).casFeatCode_role);}
    
  /** setter for role - sets  
   * @generated */
  public void setRole(String v) {
    if (EventArgument_Type.featOkTst && ((EventArgument_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ace_uima.uima.EventArgument");
    jcasType.ll_cas.ll_setStringValue(addr, ((EventArgument_Type)jcasType).casFeatCode_role, v);}    
   
    
  //*--------------*
  //* Feature: event

  /** getter for event - gets 
   * @generated */
  public Event getEvent() {
    if (EventArgument_Type.featOkTst && ((EventArgument_Type)jcasType).casFeat_event == null)
      jcasType.jcas.throwFeatMissing("event", "ac.biu.nlp.nlp.ace_uima.uima.EventArgument");
    return (Event)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((EventArgument_Type)jcasType).casFeatCode_event)));}
    
  /** setter for event - sets  
   * @generated */
  public void setEvent(Event v) {
    if (EventArgument_Type.featOkTst && ((EventArgument_Type)jcasType).casFeat_event == null)
      jcasType.jcas.throwFeatMissing("event", "ac.biu.nlp.nlp.ace_uima.uima.EventArgument");
    jcasType.ll_cas.ll_setRefValue(addr, ((EventArgument_Type)jcasType).casFeatCode_event, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    