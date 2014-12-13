

/* First created by JCasGen Fri Jun 27 12:02:31 IDT 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sat Dec 13 00:42:43 EST 2014
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
  //* Feature: treeoutDepUp2NoContext

  /** getter for treeoutDepUp2NoContext - gets 
   * @generated */
  public TreeoutDepUp2NoContext getTreeoutDepUp2NoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepUp2NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepUp2NoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepUp2NoContext)));}
    
  /** setter for treeoutDepUp2NoContext - sets  
   * @generated */
  public void setTreeoutDepUp2NoContext(TreeoutDepUp2NoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepUp2NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepUp2NoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepUp2GenPosNoContext

  /** getter for treeoutDepUp2GenPosNoContext - gets 
   * @generated */
  public TreeoutDepUp2GenPosNoContext getTreeoutDepUp2GenPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepUp2GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepUp2GenPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepUp2GenPosNoContext)));}
    
  /** setter for treeoutDepUp2GenPosNoContext - sets  
   * @generated */
  public void setTreeoutDepUp2GenPosNoContext(TreeoutDepUp2GenPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepUp2GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepUp2GenPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepUp2SpecPosNoContext

  /** getter for treeoutDepUp2SpecPosNoContext - gets 
   * @generated */
  public TreeoutDepUp2SpecPosNoContext getTreeoutDepUp2SpecPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepUp2SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepUp2SpecPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepUp2SpecPosNoContext)));}
    
  /** setter for treeoutDepUp2SpecPosNoContext - sets  
   * @generated */
  public void setTreeoutDepUp2SpecPosNoContext(TreeoutDepUp2SpecPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepUp2SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepUp2SpecPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatUp2NoContext

  /** getter for treeoutDepFlatUp2NoContext - gets 
   * @generated */
  public TreeoutDepFlatUp2NoContext getTreeoutDepFlatUp2NoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatUp2NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatUp2NoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatUp2NoContext)));}
    
  /** setter for treeoutDepFlatUp2NoContext - sets  
   * @generated */
  public void setTreeoutDepFlatUp2NoContext(TreeoutDepFlatUp2NoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatUp2NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatUp2NoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatUp2GenPosNoContext

  /** getter for treeoutDepFlatUp2GenPosNoContext - gets 
   * @generated */
  public TreeoutDepFlatUp2GenPosNoContext getTreeoutDepFlatUp2GenPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatUp2GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatUp2GenPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatUp2GenPosNoContext)));}
    
  /** setter for treeoutDepFlatUp2GenPosNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatUp2GenPosNoContext(TreeoutDepFlatUp2GenPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatUp2GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatUp2GenPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatUp2SpecPosNoContext

  /** getter for treeoutDepFlatUp2SpecPosNoContext - gets 
   * @generated */
  public TreeoutDepFlatUp2SpecPosNoContext getTreeoutDepFlatUp2SpecPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatUp2SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatUp2SpecPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatUp2SpecPosNoContext)));}
    
  /** setter for treeoutDepFlatUp2SpecPosNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatUp2SpecPosNoContext(TreeoutDepFlatUp2SpecPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatUp2SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatUp2SpecPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepPrepUp2NoContext

  /** getter for treeoutDepPrepUp2NoContext - gets 
   * @generated */
  public TreeoutDepPrepUp2NoContext getTreeoutDepPrepUp2NoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepUp2NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepPrepUp2NoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepUp2NoContext)));}
    
  /** setter for treeoutDepPrepUp2NoContext - sets  
   * @generated */
  public void setTreeoutDepPrepUp2NoContext(TreeoutDepPrepUp2NoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepUp2NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepUp2NoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepPrepUp2GenPosNoContext

  /** getter for treeoutDepPrepUp2GenPosNoContext - gets 
   * @generated */
  public TreeoutDepPrepUp2GenPosNoContext getTreeoutDepPrepUp2GenPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepUp2GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepPrepUp2GenPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepUp2GenPosNoContext)));}
    
  /** setter for treeoutDepPrepUp2GenPosNoContext - sets  
   * @generated */
  public void setTreeoutDepPrepUp2GenPosNoContext(TreeoutDepPrepUp2GenPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepUp2GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepUp2GenPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepPrepUp2SpecPosNoContext

  /** getter for treeoutDepPrepUp2SpecPosNoContext - gets 
   * @generated */
  public TreeoutDepPrepUp2SpecPosNoContext getTreeoutDepPrepUp2SpecPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepUp2SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepPrepUp2SpecPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepUp2SpecPosNoContext)));}
    
  /** setter for treeoutDepPrepUp2SpecPosNoContext - sets  
   * @generated */
  public void setTreeoutDepPrepUp2SpecPosNoContext(TreeoutDepPrepUp2SpecPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepUp2SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepUp2SpecPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatPrepUp2NoContext

  /** getter for treeoutDepFlatPrepUp2NoContext - gets 
   * @generated */
  public TreeoutDepFlatPrepUp2NoContext getTreeoutDepFlatPrepUp2NoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepUp2NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatPrepUp2NoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepUp2NoContext)));}
    
  /** setter for treeoutDepFlatPrepUp2NoContext - sets  
   * @generated */
  public void setTreeoutDepFlatPrepUp2NoContext(TreeoutDepFlatPrepUp2NoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepUp2NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepUp2NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepUp2NoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatPrepUp2GenPosNoContext

  /** getter for treeoutDepFlatPrepUp2GenPosNoContext - gets 
   * @generated */
  public TreeoutDepFlatPrepUp2GenPosNoContext getTreeoutDepFlatPrepUp2GenPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepUp2GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatPrepUp2GenPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepUp2GenPosNoContext)));}
    
  /** setter for treeoutDepFlatPrepUp2GenPosNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatPrepUp2GenPosNoContext(TreeoutDepFlatPrepUp2GenPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepUp2GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepUp2GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepUp2GenPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatPrepUp2SpecPosNoContext

  /** getter for treeoutDepFlatPrepUp2SpecPosNoContext - gets 
   * @generated */
  public TreeoutDepFlatPrepUp2SpecPosNoContext getTreeoutDepFlatPrepUp2SpecPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepUp2SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatPrepUp2SpecPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepUp2SpecPosNoContext)));}
    
  /** setter for treeoutDepFlatPrepUp2SpecPosNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatPrepUp2SpecPosNoContext(TreeoutDepFlatPrepUp2SpecPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepUp2SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepUp2SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepUp2SpecPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepUp3NoContext

  /** getter for treeoutDepUp3NoContext - gets 
   * @generated */
  public TreeoutDepUp3NoContext getTreeoutDepUp3NoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepUp3NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepUp3NoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepUp3NoContext)));}
    
  /** setter for treeoutDepUp3NoContext - sets  
   * @generated */
  public void setTreeoutDepUp3NoContext(TreeoutDepUp3NoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepUp3NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepUp3NoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepUp3GenPosNoContext

  /** getter for treeoutDepUp3GenPosNoContext - gets 
   * @generated */
  public TreeoutDepUp3GenPosNoContext getTreeoutDepUp3GenPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepUp3GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepUp3GenPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepUp3GenPosNoContext)));}
    
  /** setter for treeoutDepUp3GenPosNoContext - sets  
   * @generated */
  public void setTreeoutDepUp3GenPosNoContext(TreeoutDepUp3GenPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepUp3GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepUp3GenPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepUp3SpecPosNoContext

  /** getter for treeoutDepUp3SpecPosNoContext - gets 
   * @generated */
  public TreeoutDepUp3SpecPosNoContext getTreeoutDepUp3SpecPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepUp3SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepUp3SpecPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepUp3SpecPosNoContext)));}
    
  /** setter for treeoutDepUp3SpecPosNoContext - sets  
   * @generated */
  public void setTreeoutDepUp3SpecPosNoContext(TreeoutDepUp3SpecPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepUp3SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepUp3SpecPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatUp3NoContext

  /** getter for treeoutDepFlatUp3NoContext - gets 
   * @generated */
  public TreeoutDepFlatUp3NoContext getTreeoutDepFlatUp3NoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatUp3NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatUp3NoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatUp3NoContext)));}
    
  /** setter for treeoutDepFlatUp3NoContext - sets  
   * @generated */
  public void setTreeoutDepFlatUp3NoContext(TreeoutDepFlatUp3NoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatUp3NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatUp3NoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatUp3GenPosNoContext

  /** getter for treeoutDepFlatUp3GenPosNoContext - gets 
   * @generated */
  public TreeoutDepFlatUp3GenPosNoContext getTreeoutDepFlatUp3GenPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatUp3GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatUp3GenPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatUp3GenPosNoContext)));}
    
  /** setter for treeoutDepFlatUp3GenPosNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatUp3GenPosNoContext(TreeoutDepFlatUp3GenPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatUp3GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatUp3GenPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatUp3SpecPosNoContext

  /** getter for treeoutDepFlatUp3SpecPosNoContext - gets 
   * @generated */
  public TreeoutDepFlatUp3SpecPosNoContext getTreeoutDepFlatUp3SpecPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatUp3SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatUp3SpecPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatUp3SpecPosNoContext)));}
    
  /** setter for treeoutDepFlatUp3SpecPosNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatUp3SpecPosNoContext(TreeoutDepFlatUp3SpecPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatUp3SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatUp3SpecPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepPrepUp3NoContext

  /** getter for treeoutDepPrepUp3NoContext - gets 
   * @generated */
  public TreeoutDepPrepUp3NoContext getTreeoutDepPrepUp3NoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepUp3NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepPrepUp3NoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepUp3NoContext)));}
    
  /** setter for treeoutDepPrepUp3NoContext - sets  
   * @generated */
  public void setTreeoutDepPrepUp3NoContext(TreeoutDepPrepUp3NoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepUp3NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepUp3NoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepPrepUp3GenPosNoContext

  /** getter for treeoutDepPrepUp3GenPosNoContext - gets 
   * @generated */
  public TreeoutDepPrepUp3GenPosNoContext getTreeoutDepPrepUp3GenPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepUp3GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepPrepUp3GenPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepUp3GenPosNoContext)));}
    
  /** setter for treeoutDepPrepUp3GenPosNoContext - sets  
   * @generated */
  public void setTreeoutDepPrepUp3GenPosNoContext(TreeoutDepPrepUp3GenPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepUp3GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepUp3GenPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepPrepUp3SpecPosNoContext

  /** getter for treeoutDepPrepUp3SpecPosNoContext - gets 
   * @generated */
  public TreeoutDepPrepUp3SpecPosNoContext getTreeoutDepPrepUp3SpecPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepUp3SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepPrepUp3SpecPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepUp3SpecPosNoContext)));}
    
  /** setter for treeoutDepPrepUp3SpecPosNoContext - sets  
   * @generated */
  public void setTreeoutDepPrepUp3SpecPosNoContext(TreeoutDepPrepUp3SpecPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepPrepUp3SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepPrepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepPrepUp3SpecPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatPrepUp3NoContext

  /** getter for treeoutDepFlatPrepUp3NoContext - gets 
   * @generated */
  public TreeoutDepFlatPrepUp3NoContext getTreeoutDepFlatPrepUp3NoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepUp3NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatPrepUp3NoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepUp3NoContext)));}
    
  /** setter for treeoutDepFlatPrepUp3NoContext - sets  
   * @generated */
  public void setTreeoutDepFlatPrepUp3NoContext(TreeoutDepFlatPrepUp3NoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepUp3NoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepUp3NoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepUp3NoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatPrepUp3GenPosNoContext

  /** getter for treeoutDepFlatPrepUp3GenPosNoContext - gets 
   * @generated */
  public TreeoutDepFlatPrepUp3GenPosNoContext getTreeoutDepFlatPrepUp3GenPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepUp3GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatPrepUp3GenPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepUp3GenPosNoContext)));}
    
  /** setter for treeoutDepFlatPrepUp3GenPosNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatPrepUp3GenPosNoContext(TreeoutDepFlatPrepUp3GenPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepUp3GenPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepUp3GenPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepUp3GenPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: treeoutDepFlatPrepUp3SpecPosNoContext

  /** getter for treeoutDepFlatPrepUp3SpecPosNoContext - gets 
   * @generated */
  public TreeoutDepFlatPrepUp3SpecPosNoContext getTreeoutDepFlatPrepUp3SpecPosNoContext() {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepUp3SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    return (TreeoutDepFlatPrepUp3SpecPosNoContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepUp3SpecPosNoContext)));}
    
  /** setter for treeoutDepFlatPrepUp3SpecPosNoContext - sets  
   * @generated */
  public void setTreeoutDepFlatPrepUp3SpecPosNoContext(TreeoutDepFlatPrepUp3SpecPosNoContext v) {
    if (ArgumentInUsageSample_Type.featOkTst && ((ArgumentInUsageSample_Type)jcasType).casFeat_treeoutDepFlatPrepUp3SpecPosNoContext == null)
      jcasType.jcas.throwFeatMissing("treeoutDepFlatPrepUp3SpecPosNoContext", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentInUsageSample_Type)jcasType).casFeatCode_treeoutDepFlatPrepUp3SpecPosNoContext, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    