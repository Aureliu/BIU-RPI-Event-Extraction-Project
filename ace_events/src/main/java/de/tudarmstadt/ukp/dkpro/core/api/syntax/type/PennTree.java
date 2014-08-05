

/* First created by JCasGen Mon Aug 04 21:24:46 IDT 2014 */
package de.tudarmstadt.ukp.dkpro.core.api.syntax.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Aug 04 21:24:46 IDT 2014
 * XML source: C:/Java/Git/breep/ace_events/TypeSystem.xml
 * @generated */
public class PennTree extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PennTree.class);
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
  protected PennTree() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public PennTree(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public PennTree(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public PennTree(JCas jcas, int begin, int end) {
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
  //* Feature: PennTree

  /** getter for PennTree - gets 
   * @generated */
  public String getPennTree() {
    if (PennTree_Type.featOkTst && ((PennTree_Type)jcasType).casFeat_PennTree == null)
      jcasType.jcas.throwFeatMissing("PennTree", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.PennTree");
    return jcasType.ll_cas.ll_getStringValue(addr, ((PennTree_Type)jcasType).casFeatCode_PennTree);}
    
  /** setter for PennTree - sets  
   * @generated */
  public void setPennTree(String v) {
    if (PennTree_Type.featOkTst && ((PennTree_Type)jcasType).casFeat_PennTree == null)
      jcasType.jcas.throwFeatMissing("PennTree", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.PennTree");
    jcasType.ll_cas.ll_setStringValue(addr, ((PennTree_Type)jcasType).casFeatCode_PennTree, v);}    
   
    
  //*--------------*
  //* Feature: TransformationNames

  /** getter for TransformationNames - gets 
   * @generated */
  public String getTransformationNames() {
    if (PennTree_Type.featOkTst && ((PennTree_Type)jcasType).casFeat_TransformationNames == null)
      jcasType.jcas.throwFeatMissing("TransformationNames", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.PennTree");
    return jcasType.ll_cas.ll_getStringValue(addr, ((PennTree_Type)jcasType).casFeatCode_TransformationNames);}
    
  /** setter for TransformationNames - sets  
   * @generated */
  public void setTransformationNames(String v) {
    if (PennTree_Type.featOkTst && ((PennTree_Type)jcasType).casFeat_TransformationNames == null)
      jcasType.jcas.throwFeatMissing("TransformationNames", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.PennTree");
    jcasType.ll_cas.ll_setStringValue(addr, ((PennTree_Type)jcasType).casFeatCode_TransformationNames, v);}    
  }

    