

/* First created by JCasGen Fri Jun 27 12:02:31 IDT 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Aug 18 12:56:04 IDT 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class PredicateInUsageSample extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PredicateInUsageSample.class);
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
  protected PredicateInUsageSample() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public PredicateInUsageSample(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public PredicateInUsageSample(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public PredicateInUsageSample(JCas jcas, int begin, int end) {
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
  //* Feature: predicateSeed

  /** getter for predicateSeed - gets 
   * @generated */
  public PredicateSeed getPredicateSeed() {
    if (PredicateInUsageSample_Type.featOkTst && ((PredicateInUsageSample_Type)jcasType).casFeat_predicateSeed == null)
      jcasType.jcas.throwFeatMissing("predicateSeed", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateInUsageSample");
    return (PredicateSeed)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((PredicateInUsageSample_Type)jcasType).casFeatCode_predicateSeed)));}
    
  /** setter for predicateSeed - sets  
   * @generated */
  public void setPredicateSeed(PredicateSeed v) {
    if (PredicateInUsageSample_Type.featOkTst && ((PredicateInUsageSample_Type)jcasType).casFeat_predicateSeed == null)
      jcasType.jcas.throwFeatMissing("predicateSeed", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((PredicateInUsageSample_Type)jcasType).casFeatCode_predicateSeed, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    