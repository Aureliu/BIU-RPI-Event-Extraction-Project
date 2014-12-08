

/* First created by JCasGen Mon Aug 25 18:38:49 IDT 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Mon Dec 08 02:19:30 EST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class TAll extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TAll.class);
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
  protected TAll() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public TAll(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public TAll(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
  //*--------------*
  //* Feature: cls

  /** getter for cls - gets 
   * @generated */
  public String getCls() {
    if (TAll_Type.featOkTst && ((TAll_Type)jcasType).casFeat_cls == null)
      jcasType.jcas.throwFeatMissing("cls", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TAll_Type)jcasType).casFeatCode_cls);}
    
  /** setter for cls - sets  
   * @generated */
  public void setCls(String v) {
    if (TAll_Type.featOkTst && ((TAll_Type)jcasType).casFeat_cls == null)
      jcasType.jcas.throwFeatMissing("cls", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll");
    jcasType.ll_cas.ll_setStringValue(addr, ((TAll_Type)jcasType).casFeatCode_cls, v);}    
   
    
  //*--------------*
  //* Feature: role

  /** getter for role - gets 
   * @generated */
  public String getRole() {
    if (TAll_Type.featOkTst && ((TAll_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TAll_Type)jcasType).casFeatCode_role);}
    
  /** setter for role - sets  
   * @generated */
  public void setRole(String v) {
    if (TAll_Type.featOkTst && ((TAll_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll");
    jcasType.ll_cas.ll_setStringValue(addr, ((TAll_Type)jcasType).casFeatCode_role, v);}    
   
    
  //*--------------*
  //* Feature: val

  /** getter for val - gets 
   * @generated */
  public String getVal() {
    if (TAll_Type.featOkTst && ((TAll_Type)jcasType).casFeat_val == null)
      jcasType.jcas.throwFeatMissing("val", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TAll_Type)jcasType).casFeatCode_val);}
    
  /** setter for val - sets  
   * @generated */
  public void setVal(String v) {
    if (TAll_Type.featOkTst && ((TAll_Type)jcasType).casFeat_val == null)
      jcasType.jcas.throwFeatMissing("val", "ac.biu.nlp.nlp.ie.onthefly.input.uima.TAll");
    jcasType.ll_cas.ll_setStringValue(addr, ((TAll_Type)jcasType).casFeatCode_val, v);}    
  }

    