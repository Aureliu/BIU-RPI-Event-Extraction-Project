

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
public class Dependent extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Dependent.class);
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
  protected Dependent() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Dependent(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Dependent(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Dependent(JCas jcas, int begin, int end) {
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
  //* Feature: Governor

  /** getter for Governor - gets 
   * @generated */
  public Token getGovernor() {
    if (Dependent_Type.featOkTst && ((Dependent_Type)jcasType).casFeat_Governor == null)
      jcasType.jcas.throwFeatMissing("Governor", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependent");
    return (Token)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Dependent_Type)jcasType).casFeatCode_Governor)));}
    
  /** setter for Governor - sets  
   * @generated */
  public void setGovernor(Token v) {
    if (Dependent_Type.featOkTst && ((Dependent_Type)jcasType).casFeat_Governor == null)
      jcasType.jcas.throwFeatMissing("Governor", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependent");
    jcasType.ll_cas.ll_setRefValue(addr, ((Dependent_Type)jcasType).casFeatCode_Governor, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: Dependency

  /** getter for Dependency - gets 
   * @generated */
  public Dependency getDependency() {
    if (Dependent_Type.featOkTst && ((Dependent_Type)jcasType).casFeat_Dependency == null)
      jcasType.jcas.throwFeatMissing("Dependency", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependent");
    return (Dependency)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Dependent_Type)jcasType).casFeatCode_Dependency)));}
    
  /** setter for Dependency - sets  
   * @generated */
  public void setDependency(Dependency v) {
    if (Dependent_Type.featOkTst && ((Dependent_Type)jcasType).casFeat_Dependency == null)
      jcasType.jcas.throwFeatMissing("Dependency", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependent");
    jcasType.ll_cas.ll_setRefValue(addr, ((Dependent_Type)jcasType).casFeatCode_Dependency, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    