

/* First created by JCasGen Tue Jul 09 16:21:26 IDT 2013 */
package ac.biu.nlp.nlp.ace_uima.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;


/** 
 * Updated by JCasGen Mon Jul 15 00:03:28 IDT 2013
 * XML source: C:/Java/Git/lab/nlp-lab/Trunk/Common/Projects/ace_uima/src/main/resources/desc/DummyAE.xml
 * @generated */
public class EntityMention extends BasicArgumentMention {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(EntityMention.class);
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
  protected EntityMention() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public EntityMention(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public EntityMention(JCas jcas) {
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
    if (EntityMention_Type.featOkTst && ((EntityMention_Type)jcasType).casFeat_TYPE == null)
      jcasType.jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.EntityMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EntityMention_Type)jcasType).casFeatCode_TYPE);}
    
  /** setter for TYPE - sets  
   * @generated */
  public void setTYPE(String v) {
    if (EntityMention_Type.featOkTst && ((EntityMention_Type)jcasType).casFeat_TYPE == null)
      jcasType.jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.EntityMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((EntityMention_Type)jcasType).casFeatCode_TYPE, v);}    
   
    
  //*--------------*
  //* Feature: LDCTYPE

  /** getter for LDCTYPE - gets 
   * @generated */
  public String getLDCTYPE() {
    if (EntityMention_Type.featOkTst && ((EntityMention_Type)jcasType).casFeat_LDCTYPE == null)
      jcasType.jcas.throwFeatMissing("LDCTYPE", "ac.biu.nlp.nlp.ace_uima.uima.EntityMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EntityMention_Type)jcasType).casFeatCode_LDCTYPE);}
    
  /** setter for LDCTYPE - sets  
   * @generated */
  public void setLDCTYPE(String v) {
    if (EntityMention_Type.featOkTst && ((EntityMention_Type)jcasType).casFeat_LDCTYPE == null)
      jcasType.jcas.throwFeatMissing("LDCTYPE", "ac.biu.nlp.nlp.ace_uima.uima.EntityMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((EntityMention_Type)jcasType).casFeatCode_LDCTYPE, v);}    
   
    
  //*--------------*
  //* Feature: LDCATR

  /** getter for LDCATR - gets 
   * @generated */
  public String getLDCATR() {
    if (EntityMention_Type.featOkTst && ((EntityMention_Type)jcasType).casFeat_LDCATR == null)
      jcasType.jcas.throwFeatMissing("LDCATR", "ac.biu.nlp.nlp.ace_uima.uima.EntityMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EntityMention_Type)jcasType).casFeatCode_LDCATR);}
    
  /** setter for LDCATR - sets  
   * @generated */
  public void setLDCATR(String v) {
    if (EntityMention_Type.featOkTst && ((EntityMention_Type)jcasType).casFeat_LDCATR == null)
      jcasType.jcas.throwFeatMissing("LDCATR", "ac.biu.nlp.nlp.ace_uima.uima.EntityMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((EntityMention_Type)jcasType).casFeatCode_LDCATR, v);}    
  }

    