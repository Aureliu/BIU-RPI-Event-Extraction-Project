

/* First created by JCasGen Tue Mar 11 18:07:45 IST 2014 */
package de.tudarmstadt.ukp.dkpro.core.api.metadata.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.DocumentAnnotation;


/** 
 * Updated by JCasGen Tue Mar 11 18:07:45 IST 2014
 * XML source: C:/Java/Git/breep/ace_events/src/main/resources/desc/SpecAnnotator.xml
 * @generated */
public class DocumentMetaData extends DocumentAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DocumentMetaData.class);
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
  protected DocumentMetaData() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public DocumentMetaData(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public DocumentMetaData(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public DocumentMetaData(JCas jcas, int begin, int end) {
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
     
 
    
  //*--------------*
  //* Feature: documentTitle

  /** getter for documentTitle - gets The human readable title of the document.
   * @generated */
  public String getDocumentTitle() {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentTitle == null)
      jcasType.jcas.throwFeatMissing("documentTitle", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentTitle);}
    
  /** setter for documentTitle - sets The human readable title of the document. 
   * @generated */
  public void setDocumentTitle(String v) {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentTitle == null)
      jcasType.jcas.throwFeatMissing("documentTitle", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentTitle, v);}    
   
    
  //*--------------*
  //* Feature: documentId

  /** getter for documentId - gets The id of the document.
   * @generated */
  public String getDocumentId() {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentId == null)
      jcasType.jcas.throwFeatMissing("documentId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentId);}
    
  /** setter for documentId - sets The id of the document. 
   * @generated */
  public void setDocumentId(String v) {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentId == null)
      jcasType.jcas.throwFeatMissing("documentId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentId, v);}    
   
    
  //*--------------*
  //* Feature: documentUri

  /** getter for documentUri - gets The URI of the document.
   * @generated */
  public String getDocumentUri() {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentUri == null)
      jcasType.jcas.throwFeatMissing("documentUri", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentUri);}
    
  /** setter for documentUri - sets The URI of the document. 
   * @generated */
  public void setDocumentUri(String v) {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentUri == null)
      jcasType.jcas.throwFeatMissing("documentUri", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentUri, v);}    
   
    
  //*--------------*
  //* Feature: collectionId

  /** getter for collectionId - gets The ID of the whole document collection.
   * @generated */
  public String getCollectionId() {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_collectionId == null)
      jcasType.jcas.throwFeatMissing("collectionId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_collectionId);}
    
  /** setter for collectionId - sets The ID of the whole document collection. 
   * @generated */
  public void setCollectionId(String v) {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_collectionId == null)
      jcasType.jcas.throwFeatMissing("collectionId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_collectionId, v);}    
   
    
  //*--------------*
  //* Feature: documentBaseUri

  /** getter for documentBaseUri - gets Base URI of the document.
   * @generated */
  public String getDocumentBaseUri() {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentBaseUri == null)
      jcasType.jcas.throwFeatMissing("documentBaseUri", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentBaseUri);}
    
  /** setter for documentBaseUri - sets Base URI of the document. 
   * @generated */
  public void setDocumentBaseUri(String v) {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentBaseUri == null)
      jcasType.jcas.throwFeatMissing("documentBaseUri", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentBaseUri, v);}    
   
    
  //*--------------*
  //* Feature: isLastSegment

  /** getter for isLastSegment - gets CAS de-multipliers need to know whether a CAS is the last multiplied segment.
Thus CAS multipliers should set this field to true for the last CAS they produce.
   * @generated */
  public boolean getIsLastSegment() {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_isLastSegment == null)
      jcasType.jcas.throwFeatMissing("isLastSegment", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_isLastSegment);}
    
  /** setter for isLastSegment - sets CAS de-multipliers need to know whether a CAS is the last multiplied segment.
Thus CAS multipliers should set this field to true for the last CAS they produce. 
   * @generated */
  public void setIsLastSegment(boolean v) {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_isLastSegment == null)
      jcasType.jcas.throwFeatMissing("isLastSegment", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_isLastSegment, v);}    
  }

    