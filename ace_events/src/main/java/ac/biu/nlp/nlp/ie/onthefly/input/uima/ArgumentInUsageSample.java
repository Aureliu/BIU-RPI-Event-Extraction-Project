

/* First created by JCasGen Fri Jun 27 12:02:31 IDT 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sun Aug 10 13:57:30 IDT 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class ArgumentInUsageSample extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ArgumentInUsageSample.class);
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
  protected ArgumentInUsageSample() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ArgumentInUsageSample(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ArgumentInUsageSample(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ArgumentInUsageSample(JCas jcas, int begin, int end) {
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
  //* Feature: argumentExample

  /** getter for argumentExample - gets 
   * @generated */
  public ArgumentExample getArgumentExample() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_argumentExample == null)
      jcasType.jcas.throwFeatMissing("argumentExample", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (ArgumentExample)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_argumentExample)));}
    
  /** setter for argumentExample - sets  
   * @generated */
  public void setArgumentExample(ArgumentExample v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_argumentExample == null)
      jcasType.jcas.throwFeatMissing("argumentExample", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_argumentExample, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: pius

  /** getter for pius - gets 
   * @generated */
  public PredicateInUsageSample getPius() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_pius == null)
      jcasType.jcas.throwFeatMissing("pius", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (PredicateInUsageSample)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_pius)));}
    
  /** setter for pius - sets  
   * @generated */
  public void setPius(PredicateInUsageSample v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_pius == null)
      jcasType.jcas.throwFeatMissing("pius", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_pius, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    