
/* First created by JCasGen Tue Mar 11 18:07:46 IST 2014 */
package eu.excitement.type.temporal;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Aug 04 21:24:55 IDT 2014
 * @generated */
public class TemporalExpression_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TemporalExpression_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TemporalExpression_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TemporalExpression(addr, TemporalExpression_Type.this);
  			   TemporalExpression_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TemporalExpression(addr, TemporalExpression_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TemporalExpression.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("eu.excitement.type.temporal.TemporalExpression");
 
  /** @generated */
  final Feature casFeat_text;
  /** @generated */
  final int     casFeatCode_text;
  /** @generated */ 
  public String getText(int addr) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "eu.excitement.type.temporal.TemporalExpression");
    return ll_cas.ll_getStringValue(addr, casFeatCode_text);
  }
  /** @generated */    
  public void setText(int addr, String v) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "eu.excitement.type.temporal.TemporalExpression");
    ll_cas.ll_setStringValue(addr, casFeatCode_text, v);}
    
  
 
  /** @generated */
  final Feature casFeat_resolvedTime;
  /** @generated */
  final int     casFeatCode_resolvedTime;
  /** @generated */ 
  public String getResolvedTime(int addr) {
        if (featOkTst && casFeat_resolvedTime == null)
      jcas.throwFeatMissing("resolvedTime", "eu.excitement.type.temporal.TemporalExpression");
    return ll_cas.ll_getStringValue(addr, casFeatCode_resolvedTime);
  }
  /** @generated */    
  public void setResolvedTime(int addr, String v) {
        if (featOkTst && casFeat_resolvedTime == null)
      jcas.throwFeatMissing("resolvedTime", "eu.excitement.type.temporal.TemporalExpression");
    ll_cas.ll_setStringValue(addr, casFeatCode_resolvedTime, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public TemporalExpression_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_text = jcas.getRequiredFeatureDE(casType, "text", "uima.cas.String", featOkTst);
    casFeatCode_text  = (null == casFeat_text) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_text).getCode();

 
    casFeat_resolvedTime = jcas.getRequiredFeatureDE(casType, "resolvedTime", "uima.cas.String", featOkTst);
    casFeatCode_resolvedTime  = (null == casFeat_resolvedTime) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_resolvedTime).getCode();

  }
}



    