

/* First created by JCasGen Mon Aug 04 21:24:55 IDT 2014 */
package de.tudarmstadt.ukp.dkpro.core.mecab.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;


/** 
 * Updated by JCasGen Mon Aug 04 21:24:55 IDT 2014
 * XML source: C:/Java/Git/breep/ace_events/TypeSystem.xml
 * @generated */
public class JapaneseToken extends Token {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(JapaneseToken.class);
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
  protected JapaneseToken() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public JapaneseToken(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public JapaneseToken(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public JapaneseToken(JCas jcas, int begin, int end) {
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
  //* Feature: kana

  /** getter for kana - gets 
   * @generated */
  public String getKana() {
    if (JapaneseToken_Type.featOkTst && ((JapaneseToken_Type)jcasType).casFeat_kana == null)
      jcasType.jcas.throwFeatMissing("kana", "de.tudarmstadt.ukp.dkpro.core.mecab.type.JapaneseToken");
    return jcasType.ll_cas.ll_getStringValue(addr, ((JapaneseToken_Type)jcasType).casFeatCode_kana);}
    
  /** setter for kana - sets  
   * @generated */
  public void setKana(String v) {
    if (JapaneseToken_Type.featOkTst && ((JapaneseToken_Type)jcasType).casFeat_kana == null)
      jcasType.jcas.throwFeatMissing("kana", "de.tudarmstadt.ukp.dkpro.core.mecab.type.JapaneseToken");
    jcasType.ll_cas.ll_setStringValue(addr, ((JapaneseToken_Type)jcasType).casFeatCode_kana, v);}    
   
    
  //*--------------*
  //* Feature: ibo

  /** getter for ibo - gets 
   * @generated */
  public String getIbo() {
    if (JapaneseToken_Type.featOkTst && ((JapaneseToken_Type)jcasType).casFeat_ibo == null)
      jcasType.jcas.throwFeatMissing("ibo", "de.tudarmstadt.ukp.dkpro.core.mecab.type.JapaneseToken");
    return jcasType.ll_cas.ll_getStringValue(addr, ((JapaneseToken_Type)jcasType).casFeatCode_ibo);}
    
  /** setter for ibo - sets  
   * @generated */
  public void setIbo(String v) {
    if (JapaneseToken_Type.featOkTst && ((JapaneseToken_Type)jcasType).casFeat_ibo == null)
      jcasType.jcas.throwFeatMissing("ibo", "de.tudarmstadt.ukp.dkpro.core.mecab.type.JapaneseToken");
    jcasType.ll_cas.ll_setStringValue(addr, ((JapaneseToken_Type)jcasType).casFeatCode_ibo, v);}    
   
    
  //*--------------*
  //* Feature: kei

  /** getter for kei - gets 
   * @generated */
  public String getKei() {
    if (JapaneseToken_Type.featOkTst && ((JapaneseToken_Type)jcasType).casFeat_kei == null)
      jcasType.jcas.throwFeatMissing("kei", "de.tudarmstadt.ukp.dkpro.core.mecab.type.JapaneseToken");
    return jcasType.ll_cas.ll_getStringValue(addr, ((JapaneseToken_Type)jcasType).casFeatCode_kei);}
    
  /** setter for kei - sets  
   * @generated */
  public void setKei(String v) {
    if (JapaneseToken_Type.featOkTst && ((JapaneseToken_Type)jcasType).casFeat_kei == null)
      jcasType.jcas.throwFeatMissing("kei", "de.tudarmstadt.ukp.dkpro.core.mecab.type.JapaneseToken");
    jcasType.ll_cas.ll_setStringValue(addr, ((JapaneseToken_Type)jcasType).casFeatCode_kei, v);}    
   
    
  //*--------------*
  //* Feature: dan

  /** getter for dan - gets 
   * @generated */
  public String getDan() {
    if (JapaneseToken_Type.featOkTst && ((JapaneseToken_Type)jcasType).casFeat_dan == null)
      jcasType.jcas.throwFeatMissing("dan", "de.tudarmstadt.ukp.dkpro.core.mecab.type.JapaneseToken");
    return jcasType.ll_cas.ll_getStringValue(addr, ((JapaneseToken_Type)jcasType).casFeatCode_dan);}
    
  /** setter for dan - sets  
   * @generated */
  public void setDan(String v) {
    if (JapaneseToken_Type.featOkTst && ((JapaneseToken_Type)jcasType).casFeat_dan == null)
      jcasType.jcas.throwFeatMissing("dan", "de.tudarmstadt.ukp.dkpro.core.mecab.type.JapaneseToken");
    jcasType.ll_cas.ll_setStringValue(addr, ((JapaneseToken_Type)jcasType).casFeatCode_dan, v);}    
  }

    