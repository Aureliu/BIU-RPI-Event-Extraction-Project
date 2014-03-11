
/* First created by JCasGen Wed Mar 05 18:37:19 IST 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Tue Mar 11 18:07:45 IST 2014
 * @generated */
public class ArgumentRole_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ArgumentRole_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ArgumentRole_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ArgumentRole(addr, ArgumentRole_Type.this);
  			   ArgumentRole_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ArgumentRole(addr, ArgumentRole_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ArgumentRole.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentRole");



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ArgumentRole_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    