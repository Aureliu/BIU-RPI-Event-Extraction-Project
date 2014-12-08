

/* First created by JCasGen Fri Jun 27 12:02:31 IDT 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Dec 08 02:19:30 EST 2014
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
   
    
  //*--------------*
  //* Feature: sample

  /** getter for sample - gets 
   * @generated */
  public UsageSample getSample() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_sample == null)
      jcasType.jcas.throwFeatMissing("sample", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (UsageSample)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_sample)));}
    
  /** setter for sample - sets  
   * @generated */
  public void setSample(UsageSample v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_sample == null)
      jcasType.jcas.throwFeatMissing("sample", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_sample, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepNoContext

  /** getter for treeoutDepNoContext - gets 
   * @generated */
  public TreeoutDepNoContext getTreeoutDepNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepNoContext)));}
    
  /** setter for treeoutDepNoContext - sets  
   * @generated */
  public void setTreeoutDepNoContext(TreeoutDepNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepGenPosNoContext

  /** getter for treeoutDepGenPosNoContext - gets 
   * @generated */
  public TreeoutDepGenPosNoContext getTreeoutDepGenPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepGenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepGenPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepGenPosNoContext)));}
    
  /** setter for treeoutDepGenPosNoContext - sets  
   * @generated */
  public void setTreeoutDepGenPosNoContext(TreeoutDepGenPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepGenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepGenPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepSpecPosNoContext

  /** getter for treeoutDepSpecPosNoContext - gets 
   * @generated */
  public TreeoutDepSpecPosNoContext getTreeoutDepSpecPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepSpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepSpecPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepSpecPosNoContext)));}
    
  /** setter for treeoutDepSpecPosNoContext - sets  
   * @generated */
  public void setTreeoutDepSpecPosNoContext(TreeoutDepSpecPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepSpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepSpecPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatNoContext

  /** getter for treeoutDepFlatNoContext - gets 
   * @generated */
  public TreeoutDepFlatNoContext getTreeoutDepFlatNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatNoContext)));}
    
  /** setter for treeoutDepFlatNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatNoContext(TreeoutDepFlatNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatGenPosNoContext

  /** getter for treeoutDepFlatGenPosNoContext - gets 
   * @generated */
  public TreeoutDepFlatGenPosNoContext getTreeoutDepFlatGenPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatGenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatGenPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatGenPosNoContext)));}
    
  /** setter for treeoutDepFlatGenPosNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatGenPosNoContext(TreeoutDepFlatGenPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatGenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatGenPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatSpecPosNoContext

  /** getter for treeoutDepFlatSpecPosNoContext - gets 
   * @generated */
  public TreeoutDepFlatSpecPosNoContext getTreeoutDepFlatSpecPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatSpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatSpecPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatSpecPosNoContext)));}
    
  /** setter for treeoutDepFlatSpecPosNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatSpecPosNoContext(TreeoutDepFlatSpecPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatSpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatSpecPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepWithContext

  /** getter for treeoutDepWithContext - gets 
   * @generated */
  public TreeoutDepWithContext getTreeoutDepWithContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepWithContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepWithContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepWithContext)));}
    
  /** setter for treeoutDepWithContext - sets  
   * @generated */
  public void setTreeoutDepWithContext(TreeoutDepWithContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepWithContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepWithContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepGenPosWithContext

  /** getter for treeoutDepGenPosWithContext - gets 
   * @generated */
  public TreeoutDepGenPosWithContext getTreeoutDepGenPosWithContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepGenPosWithContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepGenPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepGenPosWithContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepGenPosWithContext)));}
    
  /** setter for treeoutDepGenPosWithContext - sets  
   * @generated */
  public void setTreeoutDepGenPosWithContext(TreeoutDepGenPosWithContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepGenPosWithContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepGenPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepGenPosWithContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepSpecPosWithContext

  /** getter for treeoutDepSpecPosWithContext - gets 
   * @generated */
  public TreeoutDepSpecPosWithContext getTreeoutDepSpecPosWithContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepSpecPosWithContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepSpecPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepSpecPosWithContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepSpecPosWithContext)));}
    
  /** setter for treeoutDepSpecPosWithContext - sets  
   * @generated */
  public void setTreeoutDepSpecPosWithContext(TreeoutDepSpecPosWithContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepSpecPosWithContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepSpecPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepSpecPosWithContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepPrepNoContext

  /** getter for treeoutDepPrepNoContext - gets 
   * @generated */
  public TreeoutDepPrepNoContext getTreeoutDepPrepNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepPrepNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepNoContext)));}
    
  /** setter for treeoutDepPrepNoContext - sets  
   * @generated */
  public void setTreeoutDepPrepNoContext(TreeoutDepPrepNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepPrepGenPosNoContext

  /** getter for treeoutDepPrepGenPosNoContext - gets 
   * @generated */
  public TreeoutDepPrepGenPosNoContext getTreeoutDepPrepGenPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepGenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepPrepGenPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepGenPosNoContext)));}
    
  /** setter for treeoutDepPrepGenPosNoContext - sets  
   * @generated */
  public void setTreeoutDepPrepGenPosNoContext(TreeoutDepPrepGenPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepGenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepGenPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepPrepSpecPosNoContext

  /** getter for treeoutDepPrepSpecPosNoContext - gets 
   * @generated */
  public TreeoutDepPrepSpecPosNoContext getTreeoutDepPrepSpecPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepSpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepPrepSpecPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepSpecPosNoContext)));}
    
  /** setter for treeoutDepPrepSpecPosNoContext - sets  
   * @generated */
  public void setTreeoutDepPrepSpecPosNoContext(TreeoutDepPrepSpecPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepSpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepSpecPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatPrepNoContext

  /** getter for treeoutDepFlatPrepNoContext - gets 
   * @generated */
  public TreeoutDepFlatPrepNoContext getTreeoutDepFlatPrepNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatPrepNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepNoContext)));}
    
  /** setter for treeoutDepFlatPrepNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatPrepNoContext(TreeoutDepFlatPrepNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatPrepGenPosNoContext

  /** getter for treeoutDepFlatPrepGenPosNoContext - gets 
   * @generated */
  public TreeoutDepFlatPrepGenPosNoContext getTreeoutDepFlatPrepGenPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepGenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatPrepGenPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepGenPosNoContext)));}
    
  /** setter for treeoutDepFlatPrepGenPosNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatPrepGenPosNoContext(TreeoutDepFlatPrepGenPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepGenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepGenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepGenPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatPrepSpecPosNoContext

  /** getter for treeoutDepFlatPrepSpecPosNoContext - gets 
   * @generated */
  public TreeoutDepFlatPrepSpecPosNoContext getTreeoutDepFlatPrepSpecPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepSpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatPrepSpecPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepSpecPosNoContext)));}
    
  /** setter for treeoutDepFlatPrepSpecPosNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatPrepSpecPosNoContext(TreeoutDepFlatPrepSpecPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepSpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepSpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepSpecPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepPrepWithContext

  /** getter for treeoutDepPrepWithContext - gets 
   * @generated */
  public TreeoutDepPrepWithContext getTreeoutDepPrepWithContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepWithContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepPrepWithContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepWithContext)));}
    
  /** setter for treeoutDepPrepWithContext - sets  
   * @generated */
  public void setTreeoutDepPrepWithContext(TreeoutDepPrepWithContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepWithContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepWithContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepPrepGenPosWithContext

  /** getter for treeoutDepPrepGenPosWithContext - gets 
   * @generated */
  public TreeoutDepPrepGenPosWithContext getTreeoutDepPrepGenPosWithContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepGenPosWithContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepGenPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepPrepGenPosWithContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepGenPosWithContext)));}
    
  /** setter for treeoutDepPrepGenPosWithContext - sets  
   * @generated */
  public void setTreeoutDepPrepGenPosWithContext(TreeoutDepPrepGenPosWithContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepGenPosWithContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepGenPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepGenPosWithContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepPrepSpecPosWithContext

  /** getter for treeoutDepPrepSpecPosWithContext - gets 
   * @generated */
  public TreeoutDepPrepSpecPosWithContext getTreeoutDepPrepSpecPosWithContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepSpecPosWithContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepSpecPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepPrepSpecPosWithContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepSpecPosWithContext)));}
    
  /** setter for treeoutDepPrepSpecPosWithContext - sets  
   * @generated */
  public void setTreeoutDepPrepSpecPosWithContext(TreeoutDepPrepSpecPosWithContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepSpecPosWithContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepSpecPosWithContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepSpecPosWithContext, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    