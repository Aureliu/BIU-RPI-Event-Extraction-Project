

/* First created by JCasGen Mon Aug 25 18:56:30 IDT 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Sat Dec 13 00:42:44 EST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class VAll extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(VAll.class);
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
  protected VAll() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public VAll(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public VAll(JCas jcas) {
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
    if (VAll_Type.featOkTst && ((VAll_Type)jcasType).casFeat_cls == null)
      jcasType.jcas.throwFeatMissing("cls", "ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll");
    return jcasType.ll_cas.ll_getStringValue(addr, ((VAll_Type)jcasType).casFeatCode_cls);}
    
  /** setter for cls - sets  
   * @generated */
  public void setCls(String v) {
    if (VAll_Type.featOkTst && ((VAll_Type)jcasType).casFeat_cls == null)
      jcasType.jcas.throwFeatMissing("cls", "ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll");
    jcasType.ll_cas.ll_setStringValue(addr, ((VAll_Type)jcasType).casFeatCode_cls, v);}    
   
    
  //*--------------*
  //* Feature: treeout

  /** getter for treeout - gets 
   * @generated */
  public String getTreeout() {
    if (VAll_Type.featOkTst && ((VAll_Type)jcasType).casFeat_treeout == null)
      jcasType.jcas.throwFeatMissing("treeout", "ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll");
    return jcasType.ll_cas.ll_getStringValue(addr, ((VAll_Type)jcasType).casFeatCode_treeout);}
    
  /** setter for treeout - sets  
   * @generated */
  public void setTreeout(String v) {
    if (VAll_Type.featOkTst && ((VAll_Type)jcasType).casFeat_treeout == null)
      jcasType.jcas.throwFeatMissing("treeout", "ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll");
    jcasType.ll_cas.ll_setStringValue(addr, ((VAll_Type)jcasType).casFeatCode_treeout, v);}    
   
    
  //*--------------*
  //* Feature: val

  /** getter for val - gets 
   * @generated */
  public String getVal() {
    if (VAll_Type.featOkTst && ((VAll_Type)jcasType).casFeat_val == null)
      jcasType.jcas.throwFeatMissing("val", "ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll");
    return jcasType.ll_cas.ll_getStringValue(addr, ((VAll_Type)jcasType).casFeatCode_val);}
    
  /** setter for val - sets  
   * @generated */
  public void setVal(String v) {
    if (VAll_Type.featOkTst && ((VAll_Type)jcasType).casFeat_val == null)
      jcasType.jcas.throwFeatMissing("val", "ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll");
    jcasType.ll_cas.ll_setStringValue(addr, ((VAll_Type)jcasType).casFeatCode_val, v);}    
  }

    