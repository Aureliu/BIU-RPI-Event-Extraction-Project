

/* First created by JCasGen Tue Mar 11 18:07:45 IST 2014 */
package de.tudarmstadt.ukp.dkpro.core.api.coref.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.AnnotationBase;


/** 
 * Updated by JCasGen Tue Mar 11 18:07:45 IST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/SpecAnnotator.xml
 * @generated */
public class CoreferenceChain extends AnnotationBase {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CoreferenceChain.class);
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
  protected CoreferenceChain() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public CoreferenceChain(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public CoreferenceChain(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: first

  /** getter for first - gets 
   * @generated */
  public CoreferenceLink getFirst() {
    if (CoreferenceChain_Type.featOkTst && ((CoreferenceChain_Type)jcasType).casFeat_first == null)
      jcasType.jcas.throwFeatMissing("first", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain");
    return (CoreferenceLink)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CoreferenceChain_Type)jcasType).casFeatCode_first)));}
    
  /** setter for first - sets  
   * @generated */
  public void setFirst(CoreferenceLink v) {
    if (CoreferenceChain_Type.featOkTst && ((CoreferenceChain_Type)jcasType).casFeat_first == null)
      jcasType.jcas.throwFeatMissing("first", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain");
    jcasType.ll_cas.ll_setRefValue(addr, ((CoreferenceChain_Type)jcasType).casFeatCode_first, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    