
/* First created by JCasGen Sun Jul 14 13:04:12 IDT 2013 */
package ac.biu.nlp.nlp.ace_uima.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Jul 15 00:03:28 IDT 2013
 * @generated */
public class BasicArgumentMentionHead_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (BasicArgumentMentionHead_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = BasicArgumentMentionHead_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new BasicArgumentMentionHead(addr, BasicArgumentMentionHead_Type.this);
  			   BasicArgumentMentionHead_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new BasicArgumentMentionHead(addr, BasicArgumentMentionHead_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = BasicArgumentMentionHead.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMentionHead");
 
  /** @generated */
  final Feature casFeat_mention;
  /** @generated */
  final int     casFeatCode_mention;
  /** @generated */ 
  public int getMention(int addr) {
        if (featOkTst && casFeat_mention == null)
      jcas.throwFeatMissing("mention", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMentionHead");
    return ll_cas.ll_getRefValue(addr, casFeatCode_mention);
  }
  /** @generated */    
  public void setMention(int addr, int v) {
        if (featOkTst && casFeat_mention == null)
      jcas.throwFeatMissing("mention", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMentionHead");
    ll_cas.ll_setRefValue(addr, casFeatCode_mention, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public BasicArgumentMentionHead_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_mention = jcas.getRequiredFeatureDE(casType, "mention", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention", featOkTst);
    casFeatCode_mention  = (null == casFeat_mention) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_mention).getCode();

  }
}



    