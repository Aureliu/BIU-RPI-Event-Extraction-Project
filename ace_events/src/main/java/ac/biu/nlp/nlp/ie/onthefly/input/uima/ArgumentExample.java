
/* First created by JCasGen Wed Mar 05 18:37:19 IST 2014 */
package ac.biu.nlp.nlp.ie.onthefly.input.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

/** 
 * Updated by JCasGen Mon Dec 08 02:19:30 EST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/OdieInputTypes.xml
 * @generated */
public class ArgumentExample extends Annotation {
	/**
	 * @generated
	 * @ordered
	 */
	@SuppressWarnings("hiding")
	public final static int typeIndexID = JCasRegistry.register(ArgumentExample.class);
	/**
	 * @generated
	 * @ordered
	 */
	@SuppressWarnings("hiding")
	public final static int type = typeIndexID;

	/** @generated */
	@Override
	public int getTypeIndexID() {return typeIndexID;}
 
	/**
	 * Never called. Disable default constructor
	 * 
	 * @generated
	 */
	protected ArgumentExample() {/* intentionally empty block */}
    
	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public ArgumentExample(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
	/** @generated */
	public ArgumentExample(JCas jcas) {
    super(jcas);
    readObject();   
  } 

	/** @generated */
	public ArgumentExample(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

	/**
	 * <!-- begin-user-doc --> Write your own initialization here <!--
	 * end-user-doc -->
	 * 
	 * @generated modifiable
	 */
	private void readObject() {/* default - does nothing empty block */
	}

	// *--------------*
	// * Feature: argument

	/**
	 * getter for argument - gets
	 * 
	 * @generated
	 */
	public Argument getArgument() {
    if (ArgumentExample_Type.featOkTst && ((ArgumentExample_Type)jcasType).casFeat_argument == null)
      jcasType.jcas.throwFeatMissing("argument", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample");
    return (Argument)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentExample_Type)jcasType).casFeatCode_argument)));}
    
	/**
	 * setter for argument - sets
	 * 
	 * @generated
	 */
	public void setArgument(Argument v) {
    if (ArgumentExample_Type.featOkTst && ((ArgumentExample_Type)jcasType).casFeat_argument == null)
      jcasType.jcas.throwFeatMissing("argument", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentExample_Type)jcasType).casFeatCode_argument, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
	// *--------------*
	// * Feature: aiuses

	/**
	 * getter for aiuses - gets
	 * 
	 * @generated
	 */
	public FSArray getAiuses() {
    if (ArgumentExample_Type.featOkTst && ((ArgumentExample_Type)jcasType).casFeat_aiuses == null)
      jcasType.jcas.throwFeatMissing("aiuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentExample_Type)jcasType).casFeatCode_aiuses)));}
    
	/**
	 * setter for aiuses - sets
	 * 
	 * @generated
	 */
	public void setAiuses(FSArray v) {
    if (ArgumentExample_Type.featOkTst && ((ArgumentExample_Type)jcasType).casFeat_aiuses == null)
      jcasType.jcas.throwFeatMissing("aiuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArgumentExample_Type)jcasType).casFeatCode_aiuses, jcasType.ll_cas.ll_getFSRef(v));}    
    
	/**
	 * indexed getter for aiuses - gets an indexed value -
	 * 
	 * @generated
	 */
	public ArgumentInUsageSample getAiuses(int i) {
    if (ArgumentExample_Type.featOkTst && ((ArgumentExample_Type)jcasType).casFeat_aiuses == null)
      jcasType.jcas.throwFeatMissing("aiuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentExample_Type)jcasType).casFeatCode_aiuses), i);
    return (ArgumentInUsageSample)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentExample_Type)jcasType).casFeatCode_aiuses), i)));}

	/**
	 * indexed setter for aiuses - sets an indexed value -
	 * 
	 * @generated
	 */
	public void setAiuses(int i, ArgumentInUsageSample v) { 
    if (ArgumentExample_Type.featOkTst && ((ArgumentExample_Type)jcasType).casFeat_aiuses == null)
      jcasType.jcas.throwFeatMissing("aiuses", "ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentExample_Type)jcasType).casFeatCode_aiuses), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ArgumentExample_Type)jcasType).casFeatCode_aiuses), i, jcasType.ll_cas.ll_getFSRef(v));}
  }
