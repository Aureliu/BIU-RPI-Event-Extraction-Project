
/* First created by JCasGen Mon Aug 19 17:19:05 IDT 2013 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.cas.TOP_Type;

/** 
 * Updated by JCasGen Mon Aug 19 17:19:05 IDT 2013
 * @generated */
public class InputMetadata_Type extends TOP_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (InputMetadata_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = InputMetadata_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new InputMetadata(addr, InputMetadata_Type.this);
  			   InputMetadata_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new InputMetadata(addr, InputMetadata_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = InputMetadata.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ace_uima.odie.uima.InputMetadata");
 
  /** @generated */
  final Feature casFeat_inputFilePath;
  /** @generated */
  final int     casFeatCode_inputFilePath;
  /** @generated */ 
  public String getInputFilePath(int addr) {
        if (featOkTst && casFeat_inputFilePath == null)
      jcas.throwFeatMissing("inputFilePath", "ac.biu.nlp.nlp.ace_uima.odie.uima.InputMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_inputFilePath);
  }
  /** @generated */    
  public void setInputFilePath(int addr, String v) {
        if (featOkTst && casFeat_inputFilePath == null)
      jcas.throwFeatMissing("inputFilePath", "ac.biu.nlp.nlp.ace_uima.odie.uima.InputMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_inputFilePath, v);}
    
  
 
  /** @generated */
  final Feature casFeat_corpusXmiFolderPath;
  /** @generated */
  final int     casFeatCode_corpusXmiFolderPath;
  /** @generated */ 
  public String getCorpusXmiFolderPath(int addr) {
        if (featOkTst && casFeat_corpusXmiFolderPath == null)
      jcas.throwFeatMissing("corpusXmiFolderPath", "ac.biu.nlp.nlp.ace_uima.odie.uima.InputMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_corpusXmiFolderPath);
  }
  /** @generated */    
  public void setCorpusXmiFolderPath(int addr, String v) {
        if (featOkTst && casFeat_corpusXmiFolderPath == null)
      jcas.throwFeatMissing("corpusXmiFolderPath", "ac.biu.nlp.nlp.ace_uima.odie.uima.InputMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_corpusXmiFolderPath, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public InputMetadata_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_inputFilePath = jcas.getRequiredFeatureDE(casType, "inputFilePath", "uima.cas.String", featOkTst);
    casFeatCode_inputFilePath  = (null == casFeat_inputFilePath) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_inputFilePath).getCode();

 
    casFeat_corpusXmiFolderPath = jcas.getRequiredFeatureDE(casType, "corpusXmiFolderPath", "uima.cas.String", featOkTst);
    casFeatCode_corpusXmiFolderPath  = (null == casFeat_corpusXmiFolderPath) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_corpusXmiFolderPath).getCode();

  }
}



    