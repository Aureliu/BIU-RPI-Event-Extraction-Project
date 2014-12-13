

/* First created by JCasGen Wed Mar 05 18:37:19 IST 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sat Dec 13 00:42:43 EST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class Predicate extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Predicate.class);
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
  protected Predicate() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Predicate(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Predicate(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Predicate(JCas jcas, int begin, int end) {
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
  //* Feature: name

  /** getter for name - gets 
   * @generated */
  public PredicateName getName() {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate");
    return (PredicateName)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_name)));}
    
  /** setter for name - sets  
   * @generated */
  public void setName(PredicateName v) {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate");
    jcasType.ll_cas.ll_setRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_name, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: seeds

  /** getter for seeds - gets 
   * @generated */
  public FSArray getSeeds() {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_seeds == null)
      jcasType.jcas.throwFeatMissing("seeds", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_seeds)));}
    
  /** setter for seeds - sets  
   * @generated */
  public void setSeeds(FSArray v) {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_seeds == null)
      jcasType.jcas.throwFeatMissing("seeds", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate");
    jcasType.ll_cas.ll_setRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_seeds, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for seeds - gets an indexed value - 
   * @generated */
  public PredicateSeed getSeeds(int i) {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_seeds == null)
      jcasType.jcas.throwFeatMissing("seeds", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_seeds), i);
    return (PredicateSeed)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_seeds), i)));}

  /** indexed setter for seeds - sets an indexed value - 
   * @generated */
  public void setSeeds(int i, PredicateSeed v) { 
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_seeds == null)
      jcasType.jcas.throwFeatMissing("seeds", "ac.biu.nlp.nlp.ie.onthefly.input.uima.Predicate");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_seeds), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_seeds), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    