package edu.cuny.qc.ace.analysis.forEvent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringBufferInputStream;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import edu.cuny.qc.util.Span;

/**
 * this class serves to encode a apf file
 * @author che
 *
 */
public class ApfReader {
	
	
	private Document doc = null;
	
	public ApfReader(String filePath) throws DocumentException
	{
		doc = getDocument(filePath);
	}
	
	public static String convertEncoding2UTF8(String sen) 
	{
		String ret;
		try {
			byte[] utf8Bytes = sen.getBytes("UTF8");
			ret = new String(utf8Bytes, "UTF8");
		} catch (UnsupportedEncodingException e) {
			System.err.println("error");
			return sen;
		}
		return ret;
	}
	
	/**
	 * get all coreferences
	 * @param arg is an entity
	 * @return
	 */
	public static Vector<String> getCoreference(Node entity)
	{
		Vector<String> names = new Vector<String>();
		if(entity == null)
		{
			return names;
		}
		List<Node> list_mentions = entity.selectNodes("./entity_mention");
		
		for(Node mention : list_mentions)
		{
			String temp = mention.selectSingleNode("./head/charseq").getStringValue();
			//temp = convertEncoding2UTF8(temp);
			names.add(temp);
		}
		return names;
	}
	
	public static Vector<Node> getCoreferenceNodes(Node entity)
	{
		Vector<Node> names = new Vector<Node>();
		if(entity == null)
		{
			return names;
		}
		List<Node> list_mentions = entity.selectNodes("./entity_mention");
		
		for(Node mention : list_mentions)
		{
			Node temp = mention.selectSingleNode("./head/charseq");
			//temp = convertEncoding2UTF8(temp);
			names.add(temp);
		}
		return names;
	}
	
	/**
	 * return names of an entity
	 * @param arg
	 * @param name
	 * @return
	 */
	public static Vector<String> getNames(Node arg)
	{
		Vector<String> names = new Vector<String>();
		String REFID = arg.valueOf("@REFID");
		if(REFID.equals(""))
		{
			return names;
		}
		Node entity = arg.selectSingleNode("//source_file/document/entity[@ID = '" + REFID +"']");
		if(entity == null)
		{
			return names;
		}
		List<Node> list_mentions = entity.selectNodes("./entity_mention");
		
		for(Node mention : list_mentions)
		{
			String temp = mention.selectSingleNode("./head/charseq").getStringValue();
			//temp = convertEncoding2UTF8(temp);
			names.add(temp);
		}
		return names;
	}
	
	/**
	 * check if a entity arg has a name
	 * @return
	 */
	public static boolean hasName(Node arg, String name)
	{
		String REFID = arg.valueOf("@REFID");
		if(REFID.equals(""))
		{
			return false;
		}
		Node entity = arg.selectSingleNode("//source_file/document/entity[@ID = '" + REFID +"']");
		if(entity == null)
		{
			return false;
		}
		List<Node> list_mentions = entity.selectNodes("./entity_mention[@TYPE = 'NAM']");
		
		for(Node mention : list_mentions)
		{
			String temp = mention.selectSingleNode("./head/charseq").getStringValue();
			if(temp.trim().equals(name))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * get a Document Object from a xml file
	 * @param filePath full path of xml file
	 * @return a Document Object related with xml file
	 * @throws DocumentException 
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public static Document getDocument(String filePath) throws DocumentException 
	{
		EntityResolver resolver = new EntityResolver() {
		    public InputSource resolveEntity(String publicId, String systemId) {
		    	return new InputSource(new StringBufferInputStream(""));
		    }
		};

		SAXReader reader = new SAXReader();
		reader.setEntityResolver(resolver );
		
		reader.setEncoding("GBK");

        Document document = null;
		document = reader.read(filePath);
         
		return document;
	}

	/**
	 * get a list of sentence spans from inline file
	 * @return
	 */
	public List<Span> getSentSpans()
	{
		ArrayList<Span> ret = new ArrayList<Span>();
		List<Node> sents = getSentences();
		for(Node sent : sents)
		{
			int start = Integer.parseInt(sent.valueOf("@start"));
			int end = Integer.parseInt(sent.valueOf("@end"));
			Span span = new Span(start, end);
			ret.add(span);
		}
		return ret;
	}
	
	/**
	 * get list of sentence nodes 
	 * @return
	 */
	public List<Node> getSentences()
	{
		List<Node> list_entity = doc.selectNodes("//DOC/BODY/TEXT/sentence");
		return list_entity;
	}
	
	/**
	 * 
	 * @return the list of all entities
	 */
	public List<Node> getEntities()
	{
		List<Node> list_entity = doc.selectNodes("//source_file/document/entity");
		return list_entity;
	}
	
	/**
	 * 
	 * @return the list of all relations and events
	 */
	public List<Node> getRelations()
	{
		List<Node> list_relation = doc.selectNodes("//source_file/document/relation");
		return list_relation;
	}
	
	public List<Node> getEvents()
	{
		List<Node> list_event = doc.selectNodes("//source_file/document/event");
		return list_event;
	}
	
	public List<Node> getEventMentions()
	{
		List<Node> list_event_mentions = doc.selectNodes("//source_file/document/event/event_mention");
		return list_event_mentions;
	}
	
	public List<Node> getRelationMentions()
	{
		List<Node> list_relation = doc.selectNodes("//source_file/document/relation_mention");
		return list_relation;
	}
	
	
	
	public static void main(String[] args) throws IOException
	{
	
	}
}
