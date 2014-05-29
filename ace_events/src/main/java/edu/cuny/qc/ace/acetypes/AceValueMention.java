// -*- tab-width: 4 -*-
//Title:        JET
//Copyright:    2005
//Author:       Ralph Grishman
//Description:  A Java-based Information Extraction Toolkil
//              (ACE extensions)

package edu.cuny.qc.ace.acetypes;

import java.io.PrintWriter;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.cuny.qc.util.Span;

/**
 *  a mention of an (ACE) value, with information from the APF ACE key.
 *  The 'id', 'extent', 'jetExtent', and 'text' fields are
 *  inherited from its superclass, AceMention.
 */

public class AceValueMention extends AceMention {

	AceValue value;

	/**
	 *  create a new Value mention with the specified id and extent.
	 */

	public AceValueMention (String id, Span extent, String fileText) {
		this.id = id;
		this.extent = AceEntityMention.convertSpan(extent, fileText);
		jetExtent = extent;
		text = fileText.substring(this.extent.start(), this.extent.end()+1);
	}

	/**
	 *  create an AceValueMention from the information in the APF file.
	 *  @param mentionElement the XML element from the APF file containing
	 *                       information about this mention
	 *  @param fileText      the text of the document, including XML tags
	 */

	public AceValueMention (Element mentionElement, String fileText) {
		id = mentionElement.getAttribute("ID");
		NodeList extents = mentionElement.getElementsByTagName("extent");
		Element extentElement = (Element) extents.item(0);
		if (extentElement == null) {
			System.err.println ("*** AceValueMention:  no extent.");
		} else {
			extent = AceEntityMention.decodeCharseq(extentElement);
			jetExtent = AceEntityMention.aceSpanToJetSpan(extent, fileText);
			text = fileText.substring(extent.start(), extent.end()+1);
		}
	}

	public AceEventArgumentValue getParent () {
		return value;
	}

	public String getType () {
		return value.type;
	}

	/**
	 *  returns 'true' if 'o' is an AceValueMention with the same extent.
	 */

	public boolean equals (Object o) {
		return (o instanceof AceValueMention) && (((AceValueMention)o).extent).equals(extent);
	}

	/**
	 *  writes the AceValueMention in APF format to 'w'.
	 */

	public void write (PrintWriter w) {
		w.println ("    <value_mention ID=\"" + id + "\">");
		w.println ("      <extent>");
		AceEntityMention.writeCharseq (w, extent, text);
		w.println ("      </extent>");
		w.println ("    </value_mention>");
	}

}
