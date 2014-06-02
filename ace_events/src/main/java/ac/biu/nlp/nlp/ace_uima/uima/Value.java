

/* First created by JCasGen Tue Jul 09 17:25:26 IDT 2013 */
package ac.biu.nlp.nlp.ace_uima.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Mon Jul 15 00:03:28 IDT 2013
 * XML source: C:/Java/Git/lab/nlp-lab/Trunk/Common/Projects/ace_uima/src/main/resources/desc/DummyAE.xml
 * @generated */
public class Value extends BasicArgument {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Value.class);
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
  protected Value() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Value(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Value(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
  //*--------------*
  //* Feature: TYPE

  /** getter for TYPE - gets 
   * @generated */
  public String getTYPE() {
    if (Value_Type.featOkTst && ((Value_Type)jcasType).casFeat_TYPE == null)
      jcasType.jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.Value");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Value_Type)jcasType).casFeatCode_TYPE);}
    
  /** setter for TYPE - sets  
   * @generated */
  public void setTYPE(String v) {
    if (Value_Type.featOkTst && ((Value_Type)jcasType).casFeat_TYPE == null)
      jcasType.jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.Value");
    jcasType.ll_cas.ll_setStringValue(addr, ((Value_Type)jcasType).casFeatCode_TYPE, v);}    
   
    
  //*--------------*
  //* Feature: SUBTYPE

  /** getter for SUBTYPE - gets 
   * @generated */
  public String getSUBTYPE() {
    if (Value_Type.featOkTst && ((Value_Type)jcasType).casFeat_SUBTYPE == null)
      jcasType.jcas.throwFeatMissing("SUBTYPE", "ac.biu.nlp.nlp.ace_uima.uima.Value");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Value_Type)jcasType).casFeatCode_SUBTYPE);}
    
  /** setter for SUBTYPE - sets  
   * @generated */
  public void setSUBTYPE(String v) {
    if (Value_Type.featOkTst && ((Value_Type)jcasType).casFeat_SUBTYPE == null)
      jcasType.jcas.throwFeatMissing("SUBTYPE", "ac.biu.nlp.nlp.ace_uima.uima.Value");
    jcasType.ll_cas.ll_setStringValue(addr, ((Value_Type)jcasType).casFeatCode_SUBTYPE, v);}    
  }

    