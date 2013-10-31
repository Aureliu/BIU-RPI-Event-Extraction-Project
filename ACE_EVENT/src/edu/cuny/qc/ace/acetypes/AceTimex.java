// -*- tab-width: 4 -*-
//Title:        JET
//Copyright:    2005
//Author:       Ralph Grishman
//Description:  A Java-based Information Extraction Toolkil
//              (ACE extensions)

package edu.cuny.qc.ace.acetypes;

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

/**
 *  an Ace Timex2 time expression.  The 'id' field is inherited from its
 *  superclass, AceEventArgumentValue.
 */

public class AceTimex extends AceEventArgumentValue {

	/**
	 *  the normalized value of the time expression
	 */
	public String val;
	public String anchorVal, anchorDir, set, mod;
	/**
	 *  a list of the mentions of this time expression (each of type AceTimexMention)
	 */
	public ArrayList<AceTimexMention> mentions = new ArrayList<AceTimexMention>();

	public AceTimex (String id, String val) {
		this.id = id;
		this.val = val;
	}

	/**
	 *  create an AceTimex from the information in the APF file.
	 *  @param timexElement the XML element from the APF file containing
	 *                       information about this time expression
	 *  @param filetext  the text of the document
	 */

	public AceTimex (Element timexElement, String filetext) {
			id = timexElement.getAttribute("ID");
			val = timexElement.getAttribute("VAL");
			anchorVal = timexElement.getAttribute("ANCHOR_VAL");
			anchorDir = timexElement.getAttribute("ANCHOR_DIR");
			set = timexElement.getAttribute("SET");
			mod = timexElement.getAttribute("MOD");
			NodeList mentionElements = timexElement.getElementsByTagName("timex2_mention");
			for (int j=0; j<mentionElements.getLength(); j++) {
				Element mentionElement = (Element) mentionElements.item(j);
				AceTimexMention mention = new AceTimexMention (mentionElement, filetext);
				addMention(mention);
			}
	}

	void addMention (AceTimexMention mention) {
		mentions.add(mention);
		mention.timex = this;
	}

	AceTimexMention findMention (String id) {
		for (int i=0; i<mentions.size(); i++) {
			AceTimexMention mention = (AceTimexMention) mentions.get(i);
			if (mention.id.equals(id)) {
				return mention;
			}
		}
		return null;
	}

	public void write (PrintWriter w) {
		w.print   ("  <timex2 ID=\"" + id + "\"");
		if (val != null && !val.equals(""))
			w.print (" VAL=\"" + val + "\"");
		if (anchorVal != null && !anchorVal.equals(""))
			w.print (" ANCHOR_VAL=\"" + anchorVal + "\"");
		if (anchorDir != null && !anchorDir.equals(""))
			w.print (" ANCHOR_DIR=\"" + anchorDir + "\"");
		if (set != null && !set.equals(""))
			w.print (" SET=\"" + set + "\"");
		if (mod != null && !mod.equals(""))
			w.print (" MOD=\"" + mod + "\"");
		w.println (">");
		for (int i=0; i<mentions.size(); i++) {
			AceTimexMention mention = (AceTimexMention) mentions.get(i);
			mention.write(w);
		}
		w.println ("  </timex2>");
	}

	public String getType () 
	{
			return "Time";
	}
}
