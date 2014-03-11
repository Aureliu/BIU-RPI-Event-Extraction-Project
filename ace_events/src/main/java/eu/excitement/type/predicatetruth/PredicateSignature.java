

/* First created by JCasGen Tue Mar 11 18:07:46 IST 2014 */
package eu.excitement.type.predicatetruth;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** This type represents an implication signature of a predicate.
 * Updated by JCasGen Tue Mar 11 18:07:46 IST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/SpecAnnotator.xml
 * @generated */
public class PredicateSignature extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PredicateSignature.class);
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
  protected PredicateSignature() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public PredicateSignature(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public PredicateSignature(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public PredicateSignature(JCas jcas, int begin, int end) {
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
     
}

    