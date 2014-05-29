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
 *  a mention of an (ACE) Timex2 time expression, with information from the APF ACE key.
 *  The 'id', 'extent', 'jetExtent', and 'text' fields are
 *  inherited from its superclass, AceMention.
 */

public class AceTimexMention extends AceMention {

	AceTimex timex;
	/**
	 *  create a new Timex mention with the specified id and extent.
	 */

	public AceTimexMention (String id, Span extent, String fileText) {
		this.id = id;
		this.extent = AceEntityMention.convertSpan(extent, fileText);
		jetExtent = extent;
		text = fileText.substring(this.extent.start(), this.extent.end()+1);
	}

	/**
	 *  create an AceTimexMention from the information in the APF file.
	 *  @param mentionElement the XML element from the APF file containing
	 *                       information about this mention
	 *  @param fileText      the text of the document, including XML tags
	 */

	public AceTimexMention (Element mentionElement, String fileText) {
		id = mentionElement.getAttribute("ID");
		NodeList extents = mentionElement.getElementsByTagName("extent");
		Element extentElement = (Element) extents.item(0);
		if (extentElement == null) {
			System.err.println ("*** AceTimexMention:  no extent.");
		} else {
			extent = AceEntityMention.decodeCharseq(extentElement);
			jetExtent = AceEntityMention.aceSpanToJetSpan(extent, fileText);
			if (extent.start() <= extent.end() && extent.end() < fileText.length()) {
				text = fileText.substring(extent.start(), extent.end()+1);
			} else {
				text = "";
				System.err.println ("*** AceTimexMention:  invalid extent.");
			}
		}
	}

	public AceEventArgumentValue getParent () {
		return timex;
	}

	public String getType () {
		return timex.getType(); // "Time";
	}

	/**
	 *  writes the AceTimexMention in APF format to 'w'.
	 */

	public void write (PrintWriter w) {
		w.println ("    <timex2_mention ID=\"" + id + "\">");
		w.println ("      <extent>");
		AceEntityMention.writeCharseq (w, extent, text);
		w.println ("      </extent>");
		w.println ("    </timex2_mention>");
	}

}
