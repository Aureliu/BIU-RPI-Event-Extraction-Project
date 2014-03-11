

/* First created by JCasGen Tue Mar 11 18:07:45 IST 2014 */
package de.tudarmstadt.ukp.dkpro.core.api.segmentation.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Mar 11 18:07:45 IST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/SpecAnnotator.xml
 * @generated */
public class Stem extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Stem.class);
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
  protected Stem() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Stem(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Stem(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Stem(JCas jcas, int begin, int end) {
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
  //* Feature: value

  /** getter for value - gets 
   * @generated */
  public String getValue() {
    if (Stem_Type.featOkTst && ((Stem_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Stem");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Stem_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets  
   * @generated */
  public void setValue(String v) {
    if (Stem_Type.featOkTst && ((Stem_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Stem");
    jcasType.ll_cas.ll_setStringValue(addr, ((Stem_Type)jcasType).casFeatCode_value, v);}    
  }

    