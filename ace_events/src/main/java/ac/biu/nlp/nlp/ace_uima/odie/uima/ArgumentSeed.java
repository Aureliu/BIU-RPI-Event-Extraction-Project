

/* First created by JCasGen Sun Aug 18 19:18:00 IDT 2013 */
package ac.biu.nlp.nlp.ace_uima.odie.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Aug 19 17:19:04 IDT 2013
 * XML source: C:/Java/Git/lab/nlp-lab/Trunk/Common/Projects/ace_uima/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class ArgumentSeed extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ArgumentSeed.class);
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
  protected ArgumentSeed() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ArgumentSeed(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ArgumentSeed(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ArgumentSeed(JCas jcas, int begin, int end) {
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
  //* Feature: role

  /** getter for role - gets 
   * @generated */
  public String getRole() {
    if (ArgumentSeed_Type.featOkTst && ((ArgumentSeed_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ace_uima.odie.uima.ArgumentSeed");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ArgumentSeed_Type)jcasType).casFeatCode_role);}
    
  /** setter for role - sets  
   * @generated */
  public void setRole(String v) {
    if (ArgumentSeed_Type.featOkTst && ((ArgumentSeed_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ace_uima.odie.uima.ArgumentSeed");
    jcasType.ll_cas.ll_setStringValue(addr, ((ArgumentSeed_Type)jcasType).casFeatCode_role, v);}    
  }

    