

/* First created by JCasGen Wed Mar 05 18:37:19 IST 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Nov 17 02:12:27 EST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class UsageSample extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(UsageSample.class);
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
  protected UsageSample() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public UsageSample(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public UsageSample(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public UsageSample(JCas jcas, int begin, int end) {
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
  //* Feature: text

  /** getter for text - gets 
   * @generated */
  public String getText() {
    if (UsageSample_Type.featOkTst && ((UsageSample_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UsageSample_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets  
   * @generated */
  public void setText(String v) {
    if (UsageSample_Type.featOkTst && ((UsageSample_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample");
    jcasType.ll_cas.ll_setStringValue(addr, ((UsageSample_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: treeout

  /** getter for treeout - gets 
   * @generated */
  public String getTreeout() {
    if (UsageSample_Type.featOkTst && ((UsageSample_Type)jcasType).casFeat_treeout == null)
      jcasType.jcas.throwFeatMissing("treeout", "ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UsageSample_Type)jcasType).casFeatCode_treeout);}
    
  /** setter for treeout - sets  
   * @generated */
  public void setTreeout(String v) {
    if (UsageSample_Type.featOkTst && ((UsageSample_Type)jcasType).casFeat_treeout == null)
      jcasType.jcas.throwFeatMissing("treeout", "ac.biu.nlp.nlp.ie.onthefly.input.uima.UsageSample");
    jcasType.ll_cas.ll_setStringValue(addr, ((UsageSample_Type)jcasType).casFeatCode_treeout, v);}    
  }

    