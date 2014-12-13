

/* First created by JCasGen Sat Dec 13 00:42:43 EST 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Sat Dec 13 00:42:43 EST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class TreeoutDepFlatPrepUp3GenPosNoContext extends Treeout {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TreeoutDepFlatPrepUp3GenPosNoContext.class);
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
  protected TreeoutDepFlatPrepUp3GenPosNoContext() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public TreeoutDepFlatPrepUp3GenPosNoContext(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public TreeoutDepFlatPrepUp3GenPosNoContext(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public TreeoutDepFlatPrepUp3GenPosNoContext(JCas jcas, int begin, int end) {
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

    