

/* First created by JCasGen Tue Mar 11 18:07:45 IST 2014 */
package de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;


/** 
 * Updated by JCasGen Tue Mar 11 18:07:45 IST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/SpecAnnotator.xml
 * @generated */
public class Governor extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Governor.class);
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
  protected Governor() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Governor(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Governor(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Governor(JCas jcas, int begin, int end) {
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
  //* Feature: Dependent

  /** getter for Dependent - gets 
   * @generated */
  public Token getDependent() {
    if (Governor_Type.featOkTst && ((Governor_Type)jcasType).casFeat_Dependent == null)
      jcasType.jcas.throwFeatMissing("Dependent", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Governor");
    return (Token)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Governor_Type)jcasType).casFeatCode_Dependent)));}
    
  /** setter for Dependent - sets  
   * @generated */
  public void setDependent(Token v) {
    if (Governor_Type.featOkTst && ((Governor_Type)jcasType).casFeat_Dependent == null)
      jcasType.jcas.throwFeatMissing("Dependent", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Governor");
    jcasType.ll_cas.ll_setRefValue(addr, ((Governor_Type)jcasType).casFeatCode_Dependent, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: Dependency

  /** getter for Dependency - gets 
   * @generated */
  public Dependency getDependency() {
    if (Governor_Type.featOkTst && ((Governor_Type)jcasType).casFeat_Dependency == null)
      jcasType.jcas.throwFeatMissing("Dependency", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Governor");
    return (Dependency)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Governor_Type)jcasType).casFeatCode_Dependency)));}
    
  /** setter for Dependency - sets  
   * @generated */
  public void setDependency(Dependency v) {
    if (Governor_Type.featOkTst && ((Governor_Type)jcasType).casFeat_Dependency == null)
      jcasType.jcas.throwFeatMissing("Dependency", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Governor");
    jcasType.ll_cas.ll_setRefValue(addr, ((Governor_Type)jcasType).casFeatCode_Dependency, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    