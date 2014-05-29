// -*- tab-width: 4 -*-
//Title:        JET
//Copyright:    2005
//Author:       Ralph Grishman
//Description:  A Java-based Information Extraction Toolkil
//              (ACE extensions)

package edu.cuny.qc.ace.acetypes;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *  an Ace value (an entity which is not coreferenced, such as a monetary
 *  amount).  The 'id' field is inherited from its superclass,
 *  AceEventArgumentValue.
 */

public class AceValue extends AceEventArgumentValue {

	String type;

	String subtype;
	/**
	 *  a list of the mentions of this value expression (each of type
	 *  AceValueMention).  Although a value can only have one mention,
	 *  we allow a list for parallelism with Entities and Timex2's.
	 */
	public ArrayList<AceValueMention> mentions = new ArrayList<AceValueMention>();

	public AceValue (String id, String type, String subtype) {
		this.type = type;
		this.subtype = subtype;
		this.id = id;
	}

	/**
	 *  create an AceValue from the information in the APF file.
	 *  @param valueElement the XML element from the APF file containing
	 *                       information about this value expression
	 *  @param filetext  the text of the document
	 */

	public AceValue (Element valueElement, String filetext) {
			id = valueElement.getAttribute("ID");
			type = valueElement.getAttribute("TYPE");
			subtype = valueElement.getAttribute("SUBTYPE");
			if (subtype == null)
				subtype = "";
			NodeList mentionElements = valueElement.getElementsByTagName("value_mention");
			for (int j=0; j<mentionElements.getLength(); j++) {
				Element mentionElement = (Element) mentionElements.item(j);
				AceValueMention mention = new AceValueMention (mentionElement, filetext);
				addMention(mention);
			}
	}

	void addMention (AceValueMention mention) {
		mentions.add(mention);
		mention.value = this;
	}

	AceValueMention findMention (String id) {
		for (int i=0; i<mentions.size(); i++) {
			AceValueMention mention = (AceValueMention) mentions.get(i);
			if (mention.id.equals(id)) {
				return mention;
			}
		}
		return null;
	}

	public void write (PrintWriter w) {
		w.print ("  <value ID=\"" + id + "\" TYPE=\"" + type + "\"");
		if (subtype != null && !subtype.equals(""))
			w.print (" SUBTYPE=\"" + subtype + "\"");
		w.println (">");
		for (int i=0; i<mentions.size(); i++) {
			AceValueMention mention = (AceValueMention) mentions.get(i);
			mention.write(w);
		}
		w.println ("  </value>");
	}
}
