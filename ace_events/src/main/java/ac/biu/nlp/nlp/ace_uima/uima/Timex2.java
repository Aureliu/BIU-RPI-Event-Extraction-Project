

/* First created by JCasGen Tue Jul 09 16:21:26 IDT 2013 */
package ac.biu.nlp.nlp.ace_uima.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Mon Jul 15 00:03:28 IDT 2013
 * XML source: C:/Java/Git/lab/nlp-lab/Trunk/Common/Projects/ace_uima/src/main/resources/desc/DummyAE.xml
 * @generated */
public class Timex2 extends BasicArgument {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Timex2.class);
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
  protected Timex2() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Timex2(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Timex2(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
  //*--------------*
  //* Feature: VAL

  /** getter for VAL - gets 
   * @generated */
  public String getVAL() {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_VAL == null)
      jcasType.jcas.throwFeatMissing("VAL", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_VAL);}
    
  /** setter for VAL - sets  
   * @generated */
  public void setVAL(String v) {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_VAL == null)
      jcasType.jcas.throwFeatMissing("VAL", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    jcasType.ll_cas.ll_setStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_VAL, v);}    
   
    
  //*--------------*
  //* Feature: ANCHOR_VAL

  /** getter for ANCHOR_VAL - gets 
   * @generated */
  public String getANCHOR_VAL() {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_ANCHOR_VAL == null)
      jcasType.jcas.throwFeatMissing("ANCHOR_VAL", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_ANCHOR_VAL);}
    
  /** setter for ANCHOR_VAL - sets  
   * @generated */
  public void setANCHOR_VAL(String v) {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_ANCHOR_VAL == null)
      jcasType.jcas.throwFeatMissing("ANCHOR_VAL", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    jcasType.ll_cas.ll_setStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_ANCHOR_VAL, v);}    
   
    
  //*--------------*
  //* Feature: ANCHOR_DIR

  /** getter for ANCHOR_DIR - gets 
   * @generated */
  public String getANCHOR_DIR() {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_ANCHOR_DIR == null)
      jcasType.jcas.throwFeatMissing("ANCHOR_DIR", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_ANCHOR_DIR);}
    
  /** setter for ANCHOR_DIR - sets  
   * @generated */
  public void setANCHOR_DIR(String v) {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_ANCHOR_DIR == null)
      jcasType.jcas.throwFeatMissing("ANCHOR_DIR", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    jcasType.ll_cas.ll_setStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_ANCHOR_DIR, v);}    
   
    
  //*--------------*
  //* Feature: COMMENT

  /** getter for COMMENT - gets 
   * @generated */
  public String getCOMMENT() {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_COMMENT == null)
      jcasType.jcas.throwFeatMissing("COMMENT", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_COMMENT);}
    
  /** setter for COMMENT - sets  
   * @generated */
  public void setCOMMENT(String v) {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_COMMENT == null)
      jcasType.jcas.throwFeatMissing("COMMENT", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    jcasType.ll_cas.ll_setStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_COMMENT, v);}    
   
    
  //*--------------*
  //* Feature: MOD

  /** getter for MOD - gets 
   * @generated */
  public String getMOD() {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_MOD == null)
      jcasType.jcas.throwFeatMissing("MOD", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_MOD);}
    
  /** setter for MOD - sets  
   * @generated */
  public void setMOD(String v) {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_MOD == null)
      jcasType.jcas.throwFeatMissing("MOD", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    jcasType.ll_cas.ll_setStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_MOD, v);}    
   
    
  //*--------------*
  //* Feature: NON_SPECIFIC

  /** getter for NON_SPECIFIC - gets 
   * @generated */
  public String getNON_SPECIFIC() {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_NON_SPECIFIC == null)
      jcasType.jcas.throwFeatMissing("NON_SPECIFIC", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_NON_SPECIFIC);}
    
  /** setter for NON_SPECIFIC - sets  
   * @generated */
  public void setNON_SPECIFIC(String v) {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_NON_SPECIFIC == null)
      jcasType.jcas.throwFeatMissing("NON_SPECIFIC", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    jcasType.ll_cas.ll_setStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_NON_SPECIFIC, v);}    
   
    
  //*--------------*
  //* Feature: SET

  /** getter for SET - gets 
   * @generated */
  public String getSET() {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_SET == null)
      jcasType.jcas.throwFeatMissing("SET", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_SET);}
    
  /** setter for SET - sets  
   * @generated */
  public void setSET(String v) {
    if (Timex2_Type.featOkTst && ((Timex2_Type)jcasType).casFeat_SET == null)
      jcasType.jcas.throwFeatMissing("SET", "ac.biu.nlp.nlp.ace_uima.uima.Timex2");
    jcasType.ll_cas.ll_setStringValue(addr, ((Timex2_Type)jcasType).casFeatCode_SET, v);}    
  }

    