

/* First created by JCasGen Sun Jun 29 20:28:54 IDT 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Dec 08 02:19:30 EST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class LemmaByPos extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(LemmaByPos.class);
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
  protected LemmaByPos() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public LemmaByPos(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public LemmaByPos(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public LemmaByPos(JCas jcas, int begin, int end) {
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
  //* Feature: value

  /** getter for value - gets 
   * @generated */
  public String getValue() {
    if (LemmaByPos_Type.featOkTst && ((LemmaByPos_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "ac.biu.nlp.nlp.ie.onthefly.input.uima.LemmaByPos");
    return jcasType.ll_cas.ll_getStringValue(addr, ((LemmaByPos_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets  
   * @generated */
  public void setValue(String v) {
    if (LemmaByPos_Type.featOkTst && ((LemmaByPos_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "ac.biu.nlp.nlp.ie.onthefly.input.uima.LemmaByPos");
    jcasType.ll_cas.ll_setStringValue(addr, ((LemmaByPos_Type)jcasType).casFeatCode_value, v);}    
   
    
  //*--------------*
  //* Feature: posStr

  /** getter for posStr - gets 
   * @generated */
  public String getPosStr() {
    if (LemmaByPos_Type.featOkTst && ((LemmaByPos_Type)jcasType).casFeat_posStr == null)
      jcasType.jcas.throwFeatMissing("posStr", "ac.biu.nlp.nlp.ie.onthefly.input.uima.LemmaByPos");
    return jcasType.ll_cas.ll_getStringValue(addr, ((LemmaByPos_Type)jcasType).casFeatCode_posStr);}
    
  /** setter for posStr - sets  
   * @generated */
  public void setPosStr(String v) {
    if (LemmaByPos_Type.featOkTst && ((LemmaByPos_Type)jcasType).casFeat_posStr == null)
      jcasType.jcas.throwFeatMissing("posStr", "ac.biu.nlp.nlp.ie.onthefly.input.uima.LemmaByPos");
    jcasType.ll_cas.ll_setStringValue(addr, ((LemmaByPos_Type)jcasType).casFeatCode_posStr, v);}    
  }

    