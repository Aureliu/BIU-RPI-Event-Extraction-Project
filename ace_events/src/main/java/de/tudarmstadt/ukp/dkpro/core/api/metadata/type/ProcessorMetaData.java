

/* First created by JCasGen Tue Mar 11 18:07:45 IST 2014 */
package de.tudarmstadt.ukp.dkpro.core.api.metadata.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** Information about a CAS processor such as a reader, analysis engine, multiplier, or consumer.
 * Updated by JCasGen Tue Mar 11 18:07:45 IST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/SpecAnnotator.xml
 * @generated */
public class ProcessorMetaData extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ProcessorMetaData.class);
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
  protected ProcessorMetaData() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ProcessorMetaData(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ProcessorMetaData(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: instanceId

  /** getter for instanceId - gets Unique identifier for this processor instance. If the same processor is applied multiple times to the CAS in different configurations, each application has a unique instance ID. In a clustered environment, each cluster node running a processor in a particular configuration should produce the same instanceId.
   * @generated */
  public String getInstanceId() {
    if (ProcessorMetaData_Type.featOkTst && ((ProcessorMetaData_Type)jcasType).casFeat_instanceId == null)
      jcasType.jcas.throwFeatMissing("instanceId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.ProcessorMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ProcessorMetaData_Type)jcasType).casFeatCode_instanceId);}
    
  /** setter for instanceId - sets Unique identifier for this processor instance. If the same processor is applied multiple times to the CAS in different configurations, each application has a unique instance ID. In a clustered environment, each cluster node running a processor in a particular configuration should produce the same instanceId. 
   * @generated */
  public void setInstanceId(String v) {
    if (ProcessorMetaData_Type.featOkTst && ((ProcessorMetaData_Type)jcasType).casFeat_instanceId == null)
      jcasType.jcas.throwFeatMissing("instanceId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.ProcessorMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((ProcessorMetaData_Type)jcasType).casFeatCode_instanceId, v);}    
   
    
  //*--------------*
  //* Feature: name

  /** getter for name - gets Name of the processor as per the 'name' field in the processor meta data.
   * @generated */
  public String getName() {
    if (ProcessorMetaData_Type.featOkTst && ((ProcessorMetaData_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.ProcessorMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ProcessorMetaData_Type)jcasType).casFeatCode_name);}
    
  /** setter for name - sets Name of the processor as per the 'name' field in the processor meta data. 
   * @generated */
  public void setName(String v) {
    if (ProcessorMetaData_Type.featOkTst && ((ProcessorMetaData_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.ProcessorMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((ProcessorMetaData_Type)jcasType).casFeatCode_name, v);}    
   
    
  //*--------------*
  //* Feature: version

  /** getter for version - gets Version of the processor as per the 'version' field in the processor meta data.
   * @generated */
  public String getVersion() {
    if (ProcessorMetaData_Type.featOkTst && ((ProcessorMetaData_Type)jcasType).casFeat_version == null)
      jcasType.jcas.throwFeatMissing("version", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.ProcessorMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ProcessorMetaData_Type)jcasType).casFeatCode_version);}
    
  /** setter for version - sets Version of the processor as per the 'version' field in the processor meta data. 
   * @generated */
  public void setVersion(String v) {
    if (ProcessorMetaData_Type.featOkTst && ((ProcessorMetaData_Type)jcasType).casFeat_version == null)
      jcasType.jcas.throwFeatMissing("version", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.ProcessorMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((ProcessorMetaData_Type)jcasType).casFeatCode_version, v);}    
   
    
  //*--------------*
  //* Feature: annotatorImplementationName

  /** getter for annotatorImplementationName - gets Java class implementing the processor.
   * @generated */
  public String getAnnotatorImplementationName() {
    if (ProcessorMetaData_Type.featOkTst && ((ProcessorMetaData_Type)jcasType).casFeat_annotatorImplementationName == null)
      jcasType.jcas.throwFeatMissing("annotatorImplementationName", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.ProcessorMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ProcessorMetaData_Type)jcasType).casFeatCode_annotatorImplementationName);}
    
  /** setter for annotatorImplementationName - sets Java class implementing the processor. 
   * @generated */
  public void setAnnotatorImplementationName(String v) {
    if (ProcessorMetaData_Type.featOkTst && ((ProcessorMetaData_Type)jcasType).casFeat_annotatorImplementationName == null)
      jcasType.jcas.throwFeatMissing("annotatorImplementationName", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.ProcessorMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((ProcessorMetaData_Type)jcasType).casFeatCode_annotatorImplementationName, v);}    
  }

    