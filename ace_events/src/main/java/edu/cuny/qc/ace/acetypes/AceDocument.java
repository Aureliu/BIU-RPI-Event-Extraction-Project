// -*- tab-width: 4 -*-
//Title:        JET
//Copyright:    2003, 2004, 2005
//Author:       Ralph Grishman
//Description:  A Java-based Information Extraction Toolkil
//              (ACE extensions)

package edu.cuny.qc.ace.acetypes;

import java.util.*;
import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;

import javax.xml.parsers.*;

/**
 *  an Ace Document, including entities, time expressions, relations,
 *  and values, either obtained from an APF file or generated by the
 *  system.
 */

public class AceDocument implements java.io.Serializable {

	private static final long serialVersionUID = -4776730511018454749L;

	/**
	 *  true for 2004 or 2005 APF format
	 */

	public static boolean ace2004 = true;

	/**
	 *  true for 2005 APF format
	 */
	public static boolean ace2005 = true;


	private static DocumentBuilder builder = null;
	private String fileText;
	private StringBuffer fileTextWithXML;

	/**
	 *  the name of the source file
	 */

	public String sourceFile;

	/**
	 *  the type of source:  newswire or bnews
	 */

	public String sourceType;

	/**
	 *  the document ID
	 */

	public String docID;
	/**
	 *  a list of the entities in the document
	 */
	public ArrayList<AceEntity> entities = new ArrayList<AceEntity>();
	/**
	 *  a list of the time expressions in the document
	 */
	public ArrayList<AceTimex> timeExpressions = new ArrayList<AceTimex>();
	/**
	 *  a list of the value expressions in the document
	 */
	public ArrayList<AceValue> values = new ArrayList<AceValue>();
	/**
	 *  a list of the relations in the document
	 */
	public ArrayList<AceRelation> relations = new ArrayList<AceRelation>();
	/**
	 *  a list of the events in the document
	 */
	public ArrayList<AceEvent> events = new ArrayList<AceEvent>();
	
	/**
	 * Qi: all mentions of events / values / entities 
	 */
	public List<AceEventMention> eventMentions = new ArrayList<AceEventMention>();
	public List<AceEntityMention> entityMentions = new ArrayList<AceEntityMention>();
	public List<AceValueMention> valueMentions = new ArrayList<AceValueMention>();
	public List<AceTimexMention> timexMentions = new ArrayList<AceTimexMention>();
	public List<AceRelationMention> relationMentions = new ArrayList<AceRelationMention>();
	
	/**
	 * all mentions including events and relations
	 */
	public List<AceMention> allMentionsList = new ArrayList<AceMention>();
	
	//private List<JCas> specs = null;
	private Boolean filtered = false;
	
	private static final String encoding = "UTF-8";//"ISO-8859-1";  // default:  ISO-LATIN-1

	public AceDocument (String sourceFile, String sourceType, String docID, String docText) {
		this.sourceFile = sourceFile;
		this.sourceType = sourceType;
		this.docID = docID;
		fileText = docText;
	}

	/**
	 * by default, use ACE 2005 format 2005
	 * @param textFileName
	 * @param APFfileName
	 * @throws IOException
	 */
	public AceDocument (String textFileName, String APFfileName) throws IOException {
		this(textFileName, APFfileName, "2005");
	}
	
	/**
	 *  create a new AceDocument from the source document in 'textFileName'
	 *  and the APF file 'APFfileName'
	 * @throws Exception 
	 */
	public AceDocument (String textFileName, String APFfileName, String year) throws IOException {
		
		if(year.equals("2004"))
		{
			ace2005 = false;
			ace2004 = true;
		}
		else // by default use 2005 format
		{
			ace2005 = true;
			ace2004 = true;
		}
		
		try {
		// initialize APF reader
		if (builder == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			builder = factory.newDocumentBuilder();
			
			// Qi: make it igoring TDT
			builder.setEntityResolver(new EntityResolver() {
		        @Override
		        public InputSource resolveEntity(String publicId, String systemId)
		                throws SAXException, IOException {
		                return new InputSource(new StringReader(""));
		        }
		    });
			
			}
		
			analyzeDocument (textFileName, APFfileName);
			
			// Qi: sometimes, one Value is accross another Value, but they have the same TYPE
			try
			{
				removeDuplicateValues();
				removeDuplicateEntities();
			} 
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
			
			
		} catch (SAXException e) {
			System.err.println ("AceDocument:  Exception in initializing APF reader: " + e);
		} catch (IOException e) {
			System.err.println ("AceDocument:  Exception in initializing APF reader: " + e);
		} catch (ParserConfigurationException e) {
			System.err.println ("AceDocument:  Exception in initializing APF reader: " + e);
		}
	}

	/**
	 *  Qi: sometimes, one Value is accross another Value, but they have the same TYPE
	 *  to fix this problem, just remove one of them, keep the one which is argument for events/relations
	 * @throws Exception 
	 */
	private void removeDuplicateValues() throws Exception
	{
		for(int i=0; i<this.valueMentions.size()-1; i++)
		{
			AceValueMention value1 = this.valueMentions.get(i);
			for(int j=i+1; j<this.valueMentions.size(); j++)
			{
				AceValueMention value2 = this.valueMentions.get(j);
				if(value1.extent.overlap(value2.extent) && value1.getType().equals(value2.getType()))
				{
					if(isArgument(value1) && (!isArgument(value2)))
					{
						// remove value2
						this.valueMentions.remove(value2);
						this.allMentionsList.remove(value2);
						this.values.remove(value2.getParent());
						System.err.println("duplicate values " + this.docID + "\t" + value1);
						j--;
						i--;
					}
					else if((!isArgument(value1)) && isArgument(value2))
					{
						// remove value1
						this.valueMentions.remove(value1);
						this.allMentionsList.remove(value1);
						this.values.remove(value1.getParent());
						i--;
						System.err.println("duplicate values " + this.docID + "\t" + value2);
						break;
					}
					else if(isSameArgument(value1, value2))
					{
						// remove value1
						this.valueMentions.remove(value1);
						this.allMentionsList.remove(value1);
						this.values.remove(value1.getParent());
						removeArg(value1, value2);
						i--;
						System.err.println("duplicate" + this.docID + "\t" + value1 + "\t" + value2);
						break;
					}
					else if((!isArgument(value1)) && (!isArgument(value2)))
					{
						// remove value1
						this.valueMentions.remove(value1);
						this.allMentionsList.remove(value1);
						this.values.remove(value1.getParent());
						i--;
						System.err.println("duplicate" + this.docID + "\t" + value1 + "\t" + value2);
						break;
					}
					else
					{
						throw new Exception("two values have overlapping extent");
					}
				}
			}
		}
	}

	/**
	 * in case that value1 and value2 are duplicate, remove value1, 
	 * replace the Value1 argument with Value2 argument
	 * @param value1
	 * @param value2
	 */
	private void removeArg(AceValueMention value1, AceValueMention value2)
	{
		for(AceEventMention event : this.eventMentions)
		{
			if(event.arguments == null)
			{
				continue;
			}
			String role1 = null;
			AceEventMentionArgument old_arg = null;
			for(AceEventMentionArgument arg : event.arguments)
			{
				if(arg.value.equals(value1))
				{
					role1 = arg.role;
					old_arg = arg;
				}
			}
			String role2 = null;
			for(AceEventMentionArgument arg : event.arguments)
			{
				if(arg.value.equals(value2))
				{
					role2 = arg.role;
				}
			}
			if(role1 != null && role1.equals(role2))
			{
				event.arguments.remove(old_arg);
			}
			else if(role1 != null)
			{
				old_arg.value = value2;
			}
		}
	}

	private void removeDuplicateEntities() throws Exception
	{
		for(int i=0; i<this.entityMentions.size()-1; i++)
		{
			AceEntityMention value1 = this.entityMentions.get(i);
			for(int j=i+1; j<this.entityMentions.size(); j++)
			{
				AceEntityMention value2 = this.entityMentions.get(j);
				if(value1.head.overlap(value2.head) && value1.getType().equals(value2.getType()))
				{
					if(isArgument(value1) && (!isArgument(value2)))
					{
						// remove value2
						this.entityMentions.remove(value2);
						this.allMentionsList.remove(value2);
						this.entities.remove(value2.getParent());
						System.err.println("duplicate" + this.docID + "\t" + value1);
						j--;
						i--;
					}
					else if((!isArgument(value1)) && isArgument(value2))
					{
						// remove value1
						this.entityMentions.remove(value1);
						this.allMentionsList.remove(value1);
						this.entities.remove(value1.getParent());
						i--;
						System.err.println("duplicate" + this.docID + "\t" + value2);
						break;
					}
					else if(isSameArgument(value1, value2))
					{
						// remove value1
						this.entityMentions.remove(value1);
						this.allMentionsList.remove(value1);
						this.entities.remove(value1.getParent());
						i--;
						System.err.println("duplicate" + this.docID + "\t" + value1 + "\t" + value2);
						break;
					}
					else if((!isArgument(value1)) && (!isArgument(value2)))
					{
						// remove value1
						this.entityMentions.remove(value1);
						this.allMentionsList.remove(value1);
						this.entities.remove(value1.getParent());
						i--;
						System.err.println("duplicate" + this.docID + "\t" + value1 + "\t" + value2);
						break;
					}
					else
					{
						throw new Exception("two values have overlapping extent");
					}
				}
			}
		}
	}
	
	private boolean isSameArgument(AceMention value1, AceMention value2)
	{
		for(AceEventMention event : this.eventMentions)
		{
			if(event.arguments == null)
			{
				continue;
			}
			String role1 = null;
			for(AceEventMentionArgument arg : event.arguments)
			{
				if(arg.value.equals(value1))
				{
					role1 = arg.role;
				}
			}
			String role2 = null;
			for(AceEventMentionArgument arg : event.arguments)
			{
				if(arg.value.equals(value2))
				{
					role2 = arg.role;
				}
			}
			
			if(role1 != null && role1.equals(role2))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isArgument(AceMention value)
	{
		for(AceEventMention event : this.eventMentions)
		{
			if(event.arguments == null)
			{
				continue;
			}
			for(AceEventMentionArgument arg : event.arguments)
			{
				if(arg.value.equals(value))
				{
					return true;
				}
			}
		}
		return false;
	}

	private void analyzeDocument (String textFileName, String APFfileName)
	    throws SAXException, IOException {
		Document apfDoc = builder.parse(APFfileName);
		fileTextWithXML = readDocument(textFileName);
		fileText = eraseXML(fileTextWithXML);
		readAPFdocument (apfDoc, fileText);
	}

	/**
	 *  read APF document and create entities and relations
	 */

	void readAPFdocument (Document apfDoc, String fileText) {
		NodeList sourceFileElements = apfDoc.getElementsByTagName("source_file");
		Element sourceFileElement = (Element) sourceFileElements.item(0);
		sourceFile = sourceFileElement.getAttribute("URI");
		sourceType = sourceFileElement.getAttribute("SOURCE");

		NodeList documentElements = apfDoc.getElementsByTagName("document");
		Element documentElement = (Element) documentElements.item(0);
		docID = documentElement.getAttribute("DOCID");

//		if (Ace.perfectMentions & !Ace.perfectEntities) {
//			readPerfectMentions (apfDoc, fileText);
//			return;
//		}

		NodeList entityElements = apfDoc.getElementsByTagName("entity");
		for (int i=0; i<entityElements.getLength(); i++) {
			Element entityElement = (Element) entityElements.item(i);
			AceEntity entity = new AceEntity (entityElement, fileText);
			addEntity(entity);
		}
		NodeList valueElements = apfDoc.getElementsByTagName("value");
		for (int i=0; i<valueElements.getLength(); i++) {
			Element valueElement = (Element) valueElements.item(i);
			AceValue value = new AceValue (valueElement, fileText);
			addValue(value);
		}
		NodeList timexElements = apfDoc.getElementsByTagName("timex2");
		for (int i=0; i<timexElements.getLength(); i++) {
			Element timexElement = (Element) timexElements.item(i);
			AceTimex timex = new AceTimex (timexElement, fileText);
			addTimeExpression(timex);
		}
		NodeList relationElements = apfDoc.getElementsByTagName("relation");
		for (int i=0; i<relationElements.getLength(); i++) {
			Element relationElement = (Element) relationElements.item(i);
			AceRelation relation = new AceRelation (relationElement, this, fileText);
			addRelation(relation);
		}
		NodeList eventElements = apfDoc.getElementsByTagName("event");
		for (int i=0; i<eventElements.getLength(); i++) {
			Element eventElement = (Element) eventElements.item(i);
			AceEvent event = new AceEvent (eventElement, this, fileText);
			addEvent(event);
		}
	}

	public void addEntity (AceEntity entity) {
		entities.add(entity);
		allMentionsList.addAll(entity.mentions);
		this.entityMentions.addAll(entity.mentions);
	}

	public void addValue (AceValue value) {
		values.add(value);
		allMentionsList.addAll(value.mentions);
		this.valueMentions.addAll(value.mentions);
	}

	public void addTimeExpression (AceTimex timex) {
		timeExpressions.add(timex);
		allMentionsList.addAll(timex.mentions);
		this.timexMentions.addAll(timex.mentions);
	}

	public void addRelation (AceRelation relation) {
		relations.add(relation);
		allMentionsList.addAll(relation.mentions);
		this.relationMentions.addAll(relation.mentions);
	}

	public void addEvent (AceEvent event) {
		events.add(event);
		allMentionsList.addAll(event.mentions);
		this.eventMentions.addAll(event.mentions);
	}

	public void filterBySpecs(TypesContainer types) {
		filterBySpecs(types, filtered, eventMentions, entityMentions, valueMentions, timexMentions, allMentionsList, events, entities, timeExpressions, values);
	}
	
	/**
	 * NOTE! (Ofer, 21/5/14) At first I thought I should remove also all the arguments
	 * (entities, timex, values) from their lists, but now I realized that positively I should leave them be,
	 * and remove only event mentions and the events, and specific roles from event mentions. This is because:
	 * 1. The evaluation is only according to events anyway, it doesn't hurt to have many entities lying around.
	 * 2. I got to a case where I accidently deleted an entity that was an argument of a VALID event.
	 *    Maybe it got deleted because it was also in another event which was invalid, I dunno. The point is that 
	 *    I got an exception for that later (got a tried toget arg candidate -1). This happened in:
	 *    "src\\main\\resources\\corpus\\qi\\bc/timex2norm/CNN_CF_20030304.1900.04", sentID=65
	 */
	public static void filterBySpecs(TypesContainer types, Boolean isFiltered,
			List<AceEventMention> eventMentions,
			List<AceEntityMention> entityMentions,
			List<AceValueMention> valueMentions,
			List<AceTimexMention> timexMentions,
			List<AceMention> allMentionsList,
			List<AceEvent> events,
			List<AceEntity> entities,
			List<AceTimex> timeExpressions,
			List<AceValue> values)
	{
		if (isFiltered) {
			throw new IllegalStateException("Can only filter by specs once per document");
		}
		
		if (eventMentions == null) {
			throw new IllegalStateException("Got eventMentions==null: All parameters are optional, except for eventMentions");
		}
		
		for (Iterator<AceEventMention> eventMentionIter = eventMentions.iterator(); eventMentionIter.hasNext();) {
			AceEventMention em = eventMentionIter.next();
			
			if (!types.triggerTypes.contains(em.event.subtype)) {
				eventMentionIter.remove();
				remove(allMentionsList, em);
				remove(events, em.event);
				
//				for (Iterator<AceEventMentionArgument> argMentionIter = em.arguments.iterator(); argMentionIter.hasNext();) {
//					AceEventMentionArgument am = argMentionIter.next();
//					remove(entityMentions, am.value);
//					remove(valueMentions, am.value);
//					remove(timexMentions, am.value);
//					remove(allMentionsList, am.value);
//					remove(entities, am.value.getParent());
//					remove(timeExpressions, am.value.getParent());
//					remove(values, am.value.getParent());
//				}
			}
			
			else {
				Set<String> possibleRoles = types.argumentRoles.get(em.event.subtype);
				for (Iterator<AceEventMentionArgument> argMentionIter = em.arguments.iterator(); argMentionIter.hasNext();) {
					AceEventMentionArgument am = argMentionIter.next();
					String role = types.getCanonicalRoleName(am.role);
					Set<String> possibleTypes = types.roleEntityTypes.get(role);
					String argType = am.value.getType();
					if (!possibleRoles.contains(role) || !possibleTypes.contains(argType)) {
						argMentionIter.remove();
//						remove(entityMentions, am.value);
//						remove(valueMentions, am.value);
//						remove(timexMentions, am.value);
//						remove(allMentionsList, am.value);
//						remove(entities, am.value.getParent());
//						remove(timeExpressions, am.value.getParent());
//						remove(values, am.value.getParent());
					}
				}	
			}
		}		
		isFiltered = true;
	}
	
	private static void remove(List<?> list, Object o) {
		if (list != null) {
			list.remove(o);
		}
	}

	/*  assumes elementType is a leaf element type */

	static String getElementText (Element e, String elementType) {
		NodeList typeList = e.getElementsByTagName(elementType);
		Element typeElement = (Element) typeList.item(0);
		String text = (String) typeElement.getFirstChild().getNodeValue();
		return text;
	}

	void readPerfectMentions (Document apfDoc, String fileText) {
		NodeList mentionElements = apfDoc.getElementsByTagName("entity_mention");
		for (int i=0; i<mentionElements.getLength(); i++) {
			Element mentionElement = (Element) mentionElements.item(i);
			String entityId = "E" + mentionElement.getAttribute("ID");
			String type = mentionElement.getAttribute("ENTITY_TYPE");
			if (AceEntity.standardType.containsKey(type))
					type = (String) AceEntity.standardType.get(type);
			String subtype = mentionElement.getAttribute("ENTITY_SUBTYPE");
			// adjust for missing subtypes in training data
			if ((!type.equals("PERSON")) && (!type.equals("")) && subtype.equals(""))
				subtype = "Other";
			AceEntityMention mention = new AceEntityMention (mentionElement, fileText);
			AceEntity entity = new AceEntity (entityId, type, subtype, false);
			entity.addMention(mention);
			addEntity(entity);
		}
	}

	/**
	 *  read file 'fileName' and return its contents as a StringBuffer
	 */

	static StringBuffer readDocument (String fileName) throws IOException {
		File file = new File(fileName);
		String line;
		BufferedReader reader = new BufferedReader (
			// (new FileReader(file));
			new InputStreamReader (new FileInputStream(file), encoding));
		StringBuffer fileText = new StringBuffer();
		while((line = reader.readLine()) != null)
			fileText.append(line + "\n");
		reader.close();
		return fileText;
	}

	/**
	 *  compute ACEoffsetMap, a map from ACE offsets (which exclude XML tags
	 *  to Jet offsets (which include all characters in the file)
	 */

	static String eraseXML (StringBuffer fileTextWithXML) {
		boolean inTag = false;
		int length = fileTextWithXML.length();
		StringBuffer fileText = new StringBuffer();
		for (int i=0; i<length; i++) {
			char c = fileTextWithXML.charAt(i);
			if(c == '<') inTag = true;
			if (!inTag) fileText.append(c);
			if(c == '>') inTag = false;
		}
		return fileText.toString();
	}

	

	/**
	 *  returns the AceEntity with ID 'id', or null if no such AceEntity.
	 */

	AceEntity findEntity (String id) {
		for (int i=0; i<entities.size(); i++) {
			AceEntity entity = (AceEntity) entities.get(i);
			if (entity.id.equals(id)) {
				return entity;
			}
		}
		System.err.println ("*** unable to find entity with id " + id);
		return null;
	}

	AceEventArgumentValue findEntityValueTimex (String id) {
		for (int i=0; i<values.size(); i++) {
			AceValue value = (AceValue) values.get(i);
			if (value.id.equals(id)) {
				return value;
			}
		}
		for (int i=0; i<timeExpressions.size(); i++) {
			AceTimex timex = (AceTimex) timeExpressions.get(i);
			if (timex.id.equals(id)) {
				return timex;
			}
		}
		return findEntity (id);
	}

	/**
	 *  returns the AceEntityMention with ID 'id', or null if no such AceEntity.
	 */

	AceEntityMention findEntityMention (String id) {
		for (int i=0; i<entities.size(); i++) {
			AceEntity entity = (AceEntity) entities.get(i);
			AceEntityMention mention = entity.findMention(id);
			if (mention != null) {
				return mention;
			}
		}
		System.err.println ("*** unable to find entity mention with id " + id);
		return null;
	}

	/**
	 *  returns the AceEntityMention, AceValueMention, or AceTimexMention with
	 *  ID 'id', or null if no such object exists.
	 */

	AceMention findMention (String id) {
		for (int i=0; i<values.size(); i++) {
			AceValue value = (AceValue) values.get(i);
			AceValueMention mention = value.findMention(id);
			if (mention != null) {
				return mention;
			}
		}
		for (int i=0; i<timeExpressions.size(); i++) {
			AceTimex timex = (AceTimex) timeExpressions.get(i);
			AceTimexMention mention = timex.findMention(id);
			if (mention != null) {
				return mention;
			}
		}
		return findEntityMention (id);
	}
	
	/**
	 *  returns an ArrayList of all entity, value, and timex mentions in the document, ordered
	 *  according to the position of their heads (the order defined for
	 *  EntityMentions).  Note:  this assumes that entities, values, and time
	 *  expressions are added only using methods addEntity, addValue, and
	 *  addTimeExpression.
	 */

	List<AceMention> getAllMentions () {
		return allMentionsList;
	}

	/**
	 *  writes the AceDocument to 'w' in APF format.
	 */

	public void write (PrintWriter w) {
		w.println ("<?xml version=\"1.0\"?>");
		w.println ("<!DOCTYPE source_file SYSTEM \"apf.v5.1.1.dtd\">");
		w.print   ("<source_file URI=\"" + sourceFile + "\"");
		w.println (" SOURCE=\"" + sourceType + "\" TYPE=\"text\">");
		w.println ("<document DOCID=\"" + docID + "\">");
		for (int i=0; i<entities.size(); i++) {
			AceEntity entity = (AceEntity) entities.get(i);
			entity.write(w);
		}
		for (int i=0; i<values.size(); i++) {
			AceValue value = (AceValue) values.get(i);
			value.write(w);
		}
		for (int i=0; i<timeExpressions.size(); i++) {
			AceTimex timex = (AceTimex) timeExpressions.get(i);
			timex.write(w);
		}
		for (int i=0; i<relations.size(); i++) {
			AceRelation relation = (AceRelation) relations.get(i);
			relation.write(w);
		}
		for (int i=0; i<events.size(); i++) {
			AceEvent event = (AceEvent) events.get(i);
			event.write(w);
		}
		w.println ("</document>");
		w.println ("</source_file>");
		w.close();
	}

	public static void main (String[] args) throws Exception 
	{
		// Qi: test the ace document object
		String ace = "/Users/che/Data/ACE/ACE2005-TrainingData-V6.0/English/nw/timex2norm/";
		String xmlFile = ace + "XIN_ENG_20030423.0011.apf.xml";
		String textFile = ace + "XIN_ENG_20030423.0011.sgm";
	
		AceDocument ad = new AceDocument(textFile, xmlFile);
		ad.write(new PrintWriter(System.out));
	}
}
