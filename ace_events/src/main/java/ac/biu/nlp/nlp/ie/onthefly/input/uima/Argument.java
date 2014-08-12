

/* First created by JCasGen Wed Mar 05 18:37:19 IST 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sun Aug 10 13:57:30 IDT 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class Argument extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Argument.class);
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
  protected Argument() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Argument(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Argument(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Argument(JCas jcas, int begin, int end) {
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
  //* Feature: role

  /** getter for role - gets 
   * @generated */
  public ArgumentRole getRole() {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    return (ArgumentRole)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_role)));}
    
  /** setter for role - sets  
   * @generated */
  public void setRole(ArgumentRole v) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    jcasType.ll_cas.ll_setRefValue(addr, ((Argument_Type)jcasType).casFeatCode_role, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: types

  /** getter for types - gets 
   * @generated */
  public FSArray getTypes() {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_types == null)
      jcasType.jcas.throwFeatMissing("types", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_types)));}
    
  /** setter for types - sets  
   * @generated */
  public void setTypes(FSArray v) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_types == null)
      jcasType.jcas.throwFeatMissing("types", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    jcasType.ll_cas.ll_setRefValue(addr, ((Argument_Type)jcasType).casFeatCode_types, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for types - gets an indexed value - 
   * @generated */
  public ArgumentType getTypes(int i) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_types == null)
      jcasType.jcas.throwFeatMissing("types", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_types), i);
    return (ArgumentType)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_types), i)));}

  /** indexed setter for types - sets an indexed value - 
   * @generated */
  public void setTypes(int i, ArgumentType v) { 
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_types == null)
      jcasType.jcas.throwFeatMissing("types", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_types), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_types), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: examples

  /** getter for examples - gets 
   * @generated */
  public FSArray getExamples() {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_examples == null)
      jcasType.jcas.throwFeatMissing("examples", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_examples)));}
    
  /** setter for examples - sets  
   * @generated */
  public void setExamples(FSArray v) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_examples == null)
      jcasType.jcas.throwFeatMissing("examples", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    jcasType.ll_cas.ll_setRefValue(addr, ((Argument_Type)jcasType).casFeatCode_examples, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for examples - gets an indexed value - 
   * @generated */
  public ArgumentExample getExamples(int i) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_examples == null)
      jcasType.jcas.throwFeatMissing("examples", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_examples), i);
    return (ArgumentExample)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_examples), i)));}

  /** indexed setter for examples - sets an indexed value - 
   * @generated */
  public void setExamples(int i, ArgumentExample v) { 
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_examples == null)
      jcasType.jcas.throwFeatMissing("examples", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_examples), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_examples), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: marker

  /** getter for marker - gets 
   * @generated */
  public Marker getMarker() {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_marker == null)
      jcasType.jcas.throwFeatMissing("marker", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    return (Marker)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_marker)));}
    
  /** setter for marker - sets  
   * @generated */
  public void setMarker(Marker v) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_marker == null)
      jcasType.jcas.throwFeatMissing("marker", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    jcasType.ll_cas.ll_setRefValue(addr, ((Argument_Type)jcasType).casFeatCode_marker, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: aiuses

  /** getter for aiuses - gets 
   * @generated */
  public FSArray getAiuses() {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_aiuses == null)
      jcasType.jcas.throwFeatMissing("aiuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_aiuses)));}
    
  /** setter for aiuses - sets  
   * @generated */
  public void setAiuses(FSArray v) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_aiuses == null)
      jcasType.jcas.throwFeatMissing("aiuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    jcasType.ll_cas.ll_setRefValue(addr, ((Argument_Type)jcasType).casFeatCode_aiuses, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for aiuses - gets an indexed value - 
   * @generated */
  public ArgumentInUsageSample getAiuses(int i) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_aiuses == null)
      jcasType.jcas.throwFeatMissing("aiuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_aiuses), i);
    return (ArgumentInUsageSample)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_aiuses), i)));}

  /** indexed setter for aiuses - sets an indexed value - 
   * @generated */
  public void setAiuses(int i, ArgumentInUsageSample v) { 
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_aiuses == null)
      jcasType.jcas.throwFeatMissing("aiuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_aiuses), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_aiuses), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    