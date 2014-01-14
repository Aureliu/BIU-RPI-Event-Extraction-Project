

/* First created by JCasGen Sun Aug 18 19:24:57 IDT 2013 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.cas.StringArray;


/** 
 * Updated by JCasGen Mon Aug 19 17:19:04 IDT 2013
 * XML source: C:/Java/Git/lab/nlp-lab/Trunk/Common/Projects/ace_uima/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class Argument extends TOP {
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

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: role

  /** getter for role - gets 
   * @generated */
  public String getRole() {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ace_uima.odie.uima.Argument");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Argument_Type)jcasType).casFeatCode_role);}
    
  /** setter for role - sets  
   * @generated */
  public void setRole(String v) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ace_uima.odie.uima.Argument");
    jcasType.ll_cas.ll_setStringValue(addr, ((Argument_Type)jcasType).casFeatCode_role, v);}    
   
    
  //*--------------*
  //* Feature: types

  /** getter for types - gets 
   * @generated */
  public StringArray getTypes() {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_types == null)
      jcasType.jcas.throwFeatMissing("types", "ac.biu.nlp.nlp.ace_uima.odie.uima.Argument");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_types)));}
    
  /** setter for types - sets  
   * @generated */
  public void setTypes(StringArray v) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_types == null)
      jcasType.jcas.throwFeatMissing("types", "ac.biu.nlp.nlp.ace_uima.odie.uima.Argument");
    jcasType.ll_cas.ll_setRefValue(addr, ((Argument_Type)jcasType).casFeatCode_types, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for types - gets an indexed value - 
   * @generated */
  public String getTypes(int i) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_types == null)
      jcasType.jcas.throwFeatMissing("types", "ac.biu.nlp.nlp.ace_uima.odie.uima.Argument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_types), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_types), i);}

  /** indexed setter for types - sets an indexed value - 
   * @generated */
  public void setTypes(int i, String v) { 
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_types == null)
      jcasType.jcas.throwFeatMissing("types", "ac.biu.nlp.nlp.ace_uima.odie.uima.Argument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_types), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_types), i, v);}
  }

    