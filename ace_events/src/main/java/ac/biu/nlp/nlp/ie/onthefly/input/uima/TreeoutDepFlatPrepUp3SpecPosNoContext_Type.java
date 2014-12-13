
/* First created by JCasGen Sat Dec 13 00:42:43 EST 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** 
 * Updated by JCasGen Sat Dec 13 00:42:43 EST 2014
 * @generated */
public class TreeoutDepFlatPrepUp3SpecPosNoContext_Type extends Treeout_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TreeoutDepFlatPrepUp3SpecPosNoContext_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TreeoutDepFlatPrepUp3SpecPosNoContext_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TreeoutDepFlatPrepUp3SpecPosNoContext(addr, TreeoutDepFlatPrepUp3SpecPosNoContext_Type.this);
  			   TreeoutDepFlatPrepUp3SpecPosNoContext_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TreeoutDepFlatPrepUp3SpecPosNoContext(addr, TreeoutDepFlatPrepUp3SpecPosNoContext_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TreeoutDepFlatPrepUp3SpecPosNoContext.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepUp3SpecPosNoContext");



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public TreeoutDepFlatPrepUp3SpecPosNoContext_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    