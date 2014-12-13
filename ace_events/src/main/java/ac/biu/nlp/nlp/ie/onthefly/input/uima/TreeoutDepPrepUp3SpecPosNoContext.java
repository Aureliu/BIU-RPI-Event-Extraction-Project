

/* First created by JCasGen Sat Dec 13 00:42:44 EST 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Sat Dec 13 00:42:44 EST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class TreeoutDepPrepUp3SpecPosNoContext extends Treeout {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TreeoutDepPrepUp3SpecPosNoContext.class);
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
  protected TreeoutDepPrepUp3SpecPosNoContext() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public TreeoutDepPrepUp3SpecPosNoContext(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public TreeoutDepPrepUp3SpecPosNoContext(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public TreeoutDepPrepUp3SpecPosNoContext(JCas jcas, int begin, int end) {
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

    