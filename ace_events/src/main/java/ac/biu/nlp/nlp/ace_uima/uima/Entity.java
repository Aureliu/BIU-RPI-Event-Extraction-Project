

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
public class Entity extends BasicArgument {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Entity.class);
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
  protected Entity() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Entity(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Entity(JCas jcas) {
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
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_TYPE == null)
      jcasType.jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.Entity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Entity_Type)jcasType).casFeatCode_TYPE);}
    
  /** setter for TYPE - sets  
   * @generated */
  public void setTYPE(String v) {
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_TYPE == null)
      jcasType.jcas.throwFeatMissing("TYPE", "ac.biu.nlp.nlp.ace_uima.uima.Entity");
    jcasType.ll_cas.ll_setStringValue(addr, ((Entity_Type)jcasType).casFeatCode_TYPE, v);}    
   
    
  //*--------------*
  //* Feature: SUBTYPE

  /** getter for SUBTYPE - gets 
   * @generated */
  public String getSUBTYPE() {
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_SUBTYPE == null)
      jcasType.jcas.throwFeatMissing("SUBTYPE", "ac.biu.nlp.nlp.ace_uima.uima.Entity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Entity_Type)jcasType).casFeatCode_SUBTYPE);}
    
  /** setter for SUBTYPE - sets  
   * @generated */
  public void setSUBTYPE(String v) {
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_SUBTYPE == null)
      jcasType.jcas.throwFeatMissing("SUBTYPE", "ac.biu.nlp.nlp.ace_uima.uima.Entity");
    jcasType.ll_cas.ll_setStringValue(addr, ((Entity_Type)jcasType).casFeatCode_SUBTYPE, v);}    
   
    
  //*--------------*
  //* Feature: CLASS

  /** getter for CLASS - gets 
   * @generated */
  public String getCLASS() {
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_CLASS == null)
      jcasType.jcas.throwFeatMissing("CLASS", "ac.biu.nlp.nlp.ace_uima.uima.Entity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Entity_Type)jcasType).casFeatCode_CLASS);}
    
  /** setter for CLASS - sets  
   * @generated */
  public void setCLASS(String v) {
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_CLASS == null)
      jcasType.jcas.throwFeatMissing("CLASS", "ac.biu.nlp.nlp.ace_uima.uima.Entity");
    jcasType.ll_cas.ll_setStringValue(addr, ((Entity_Type)jcasType).casFeatCode_CLASS, v);}    
  }

    