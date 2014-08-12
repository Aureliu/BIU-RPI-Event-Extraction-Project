

/* First created by JCasGen Wed Mar 05 18:37:19 IST 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sun Aug 10 13:57:31 IDT 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class PredicateSeed extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PredicateSeed.class);
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
  protected PredicateSeed() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public PredicateSeed(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public PredicateSeed(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public PredicateSeed(JCas jcas, int begin, int end) {
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
  //* Feature: piuses

  /** getter for piuses - gets 
   * @generated */
  public FSArray getPiuses() {
    if (PredicateSeed_Type.featOkTst && ((PredicateSeed_Type)jcasType).casFeat_piuses == null)
      jcasType.jcas.throwFeatMissing("piuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((PredicateSeed_Type)jcasType).casFeatCode_piuses)));}
    
  /** setter for piuses - sets  
   * @generated */
  public void setPiuses(FSArray v) {
    if (PredicateSeed_Type.featOkTst && ((PredicateSeed_Type)jcasType).casFeat_piuses == null)
      jcasType.jcas.throwFeatMissing("piuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed");
    jcasType.ll_cas.ll_setRefValue(addr, ((PredicateSeed_Type)jcasType).casFeatCode_piuses, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for piuses - gets an indexed value - 
   * @generated */
  public PredicateInUsageSample getPiuses(int i) {
    if (PredicateSeed_Type.featOkTst && ((PredicateSeed_Type)jcasType).casFeat_piuses == null)
      jcasType.jcas.throwFeatMissing("piuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((PredicateSeed_Type)jcasType).casFeatCode_piuses), i);
    return (PredicateInUsageSample)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((PredicateSeed_Type)jcasType).casFeatCode_piuses), i)));}

  /** indexed setter for piuses - sets an indexed value - 
   * @generated */
  public void setPiuses(int i, PredicateInUsageSample v) { 
    if (PredicateSeed_Type.featOkTst && ((PredicateSeed_Type)jcasType).casFeat_piuses == null)
      jcasType.jcas.throwFeatMissing("piuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((PredicateSeed_Type)jcasType).casFeatCode_piuses), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((PredicateSeed_Type)jcasType).casFeatCode_piuses), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    