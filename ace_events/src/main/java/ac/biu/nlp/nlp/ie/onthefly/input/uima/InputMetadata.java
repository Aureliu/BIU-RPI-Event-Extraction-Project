

/* First created by JCasGen Mon Aug 19 17:19:05 IDT 2013 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Mon Aug 19 17:19:05 IDT 2013
 * XML source: C:/Java/Git/lab/nlp-lab/Trunk/Common/Projects/ace_uima/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class InputMetadata extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(InputMetadata.class);
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
  protected InputMetadata() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public InputMetadata(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public InputMetadata(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: inputFilePath

  /** getter for inputFilePath - gets 
   * @generated */
  public String getInputFilePath() {
    if (InputMetadata_Type.featOkTst && ((InputMetadata_Type)jcasType).casFeat_inputFilePath == null)
      jcasType.jcas.throwFeatMissing("inputFilePath", "ac.biu.nlp.nlp.ace_uima.odie.uima.InputMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((InputMetadata_Type)jcasType).casFeatCode_inputFilePath);}
    
  /** setter for inputFilePath - sets  
   * @generated */
  public void setInputFilePath(String v) {
    if (InputMetadata_Type.featOkTst && ((InputMetadata_Type)jcasType).casFeat_inputFilePath == null)
      jcasType.jcas.throwFeatMissing("inputFilePath", "ac.biu.nlp.nlp.ace_uima.odie.uima.InputMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((InputMetadata_Type)jcasType).casFeatCode_inputFilePath, v);}    
   
    
  //*--------------*
  //* Feature: corpusXmiFolderPath

  /** getter for corpusXmiFolderPath - gets 
   * @generated */
  public String getCorpusXmiFolderPath() {
    if (InputMetadata_Type.featOkTst && ((InputMetadata_Type)jcasType).casFeat_corpusXmiFolderPath == null)
      jcasType.jcas.throwFeatMissing("corpusXmiFolderPath", "ac.biu.nlp.nlp.ace_uima.odie.uima.InputMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((InputMetadata_Type)jcasType).casFeatCode_corpusXmiFolderPath);}
    
  /** setter for corpusXmiFolderPath - sets  
   * @generated */
  public void setCorpusXmiFolderPath(String v) {
    if (InputMetadata_Type.featOkTst && ((InputMetadata_Type)jcasType).casFeat_corpusXmiFolderPath == null)
      jcasType.jcas.throwFeatMissing("corpusXmiFolderPath", "ac.biu.nlp.nlp.ace_uima.odie.uima.InputMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((InputMetadata_Type)jcasType).casFeatCode_corpusXmiFolderPath, v);}    
  }

    