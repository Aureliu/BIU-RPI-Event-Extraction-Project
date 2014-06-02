

/* First created by JCasGen Tue Jul 09 16:21:26 IDT 2013 */
package ac.biu.nlp.nlp.ace_uima.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Jul 15 00:03:28 IDT 2013
 * XML source: C:/Java/Git/lab/nlp-lab/Trunk/Common/Projects/ace_uima/src/main/resources/desc/DummyAE.xml
 * @generated */
public class BasicArgumentMentionExtent extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(BasicArgumentMentionExtent.class);
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
  protected BasicArgumentMentionExtent() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public BasicArgumentMentionExtent(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public BasicArgumentMentionExtent(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public BasicArgumentMentionExtent(JCas jcas, int begin, int end) {
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
  //* Feature: mention

  /** getter for mention - gets 
   * @generated */
  public BasicArgumentMention getMention() {
    if (BasicArgumentMentionExtent_Type.featOkTst && ((BasicArgumentMentionExtent_Type)jcasType).casFeat_mention == null)
      jcasType.jcas.throwFeatMissing("mention", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMentionExtent");
    return (BasicArgumentMention)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((BasicArgumentMentionExtent_Type)jcasType).casFeatCode_mention)));}
    
  /** setter for mention - sets  
   * @generated */
  public void setMention(BasicArgumentMention v) {
    if (BasicArgumentMentionExtent_Type.featOkTst && ((BasicArgumentMentionExtent_Type)jcasType).casFeat_mention == null)
      jcasType.jcas.throwFeatMissing("mention", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMentionExtent");
    jcasType.ll_cas.ll_setRefValue(addr, ((BasicArgumentMentionExtent_Type)jcasType).casFeatCode_mention, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    