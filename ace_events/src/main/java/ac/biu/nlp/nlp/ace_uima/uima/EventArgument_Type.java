
/* First created by JCasGen Tue Jul 09 16:21:26 IDT 2013 */
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
import org.apache.uima.jcas.cas.TOP_Type;

/** 
 * Updated by JCasGen Mon Jul 15 00:03:28 IDT 2013
 * @generated */
public class EventArgument_Type extends TOP_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (EventArgument_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = EventArgument_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new EventArgument(addr, EventArgument_Type.this);
  			   EventArgument_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new EventArgument(addr, EventArgument_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = EventArgument.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ac.biu.nlp.nlp.ace_uima.uima.EventArgument");
 
  /** @generated */
  final Feature casFeat_arg;
  /** @generated */
  final int     casFeatCode_arg;
  /** @generated */ 
  public int getArg(int addr) {
        if (featOkTst && casFeat_arg == null)
      jcas.throwFeatMissing("arg", "ac.biu.nlp.nlp.ace_uima.uima.EventArgument");
    return ll_cas.ll_getRefValue(addr, casFeatCode_arg);
  }
  /** @generated */    
  public void setArg(int addr, int v) {
        if (featOkTst && casFeat_arg == null)
      jcas.throwFeatMissing("arg", "ac.biu.nlp.nlp.ace_uima.uima.EventArgument");
    ll_cas.ll_setRefValue(addr, casFeatCode_arg, v);}
    
  
 
  /** @generated */
  final Feature casFeat_role;
  /** @generated */
  final int     casFeatCode_role;
  /** @generated */ 
  public String getRole(int addr) {
        if (featOkTst && casFeat_role == null)
      jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ace_uima.uima.EventArgument");
    return ll_cas.ll_getStringValue(addr, casFeatCode_role);
  }
  /** @generated */    
  public void setRole(int addr, String v) {
        if (featOkTst && casFeat_role == null)
      jcas.throwFeatMissing("role", "ac.biu.nlp.nlp.ace_uima.uima.EventArgument");
    ll_cas.ll_setStringValue(addr, casFeatCode_role, v);}
    
  
 
  /** @generated */
  final Feature casFeat_event;
  /** @generated */
  final int     casFeatCode_event;
  /** @generated */ 
  public int getEvent(int addr) {
        if (featOkTst && casFeat_event == null)
      jcas.throwFeatMissing("event", "ac.biu.nlp.nlp.ace_uima.uima.EventArgument");
    return ll_cas.ll_getRefValue(addr, casFeatCode_event);
  }
  /** @generated */    
  public void setEvent(int addr, int v) {
        if (featOkTst && casFeat_event == null)
      jcas.throwFeatMissing("event", "ac.biu.nlp.nlp.ace_uima.uima.EventArgument");
    ll_cas.ll_setRefValue(addr, casFeatCode_event, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public EventArgument_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_arg = jcas.getRequiredFeatureDE(casType, "arg", "ac.biu.nlp.nlp.ace_uima.uima.BasicArgument", featOkTst);
    casFeatCode_arg  = (null == casFeat_arg) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_arg).getCode();

 
    casFeat_role = jcas.getRequiredFeatureDE(casType, "role", "uima.cas.String", featOkTst);
    casFeatCode_role  = (null == casFeat_role) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_role).getCode();

 
    casFeat_event = jcas.getRequiredFeatureDE(casType, "event", "ac.biu.nlp.nlp.ace_uima.uima.Event", featOkTst);
    casFeatCode_event  = (null == casFeat_event) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_event).getCode();

  }
}



    