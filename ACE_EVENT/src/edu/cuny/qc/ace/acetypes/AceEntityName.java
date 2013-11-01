// -*- tab-width: 4 -*-
package edu.cuny.qc.ace.acetypes;

import java.util.*;
import java.io.*;


import org.w3c.dom.*;
import org.xml.sax.*;

import edu.cuny.qc.util.Span;

import javax.xml.parsers.*;

/**
 *  an Ace Entity Name, with information from the APF ACE key.
 */

public class AceEntityName {
	/**
	 *  the extent of the mention, with start and end positions based on
	 *  ACE offsets (excluding XML tags).
	 */
	public Span extent;

	public String text;

	public AceEntityName (Span extent, String fileText) {
		this.extent = AceEntityMention.convertSpan(extent, fileText);
		text = fileText.substring(this.extent.start(), this.extent.end()+1);
	}

	/**
	 *  create an AceEntityName from the information in the APF file.
	 *  @param nameElement   the XML element from the APF file containing
	 *                       information about this mention
	 *  @param fileText      the text of the document, including XML tags
	 */

	public AceEntityName (Element nameElement, String fileText) {
		extent = AceEntityMention.decodeCharseq(nameElement);
		text = fileText.substring(extent.start(), extent.end()+1).replace('\n',' ').replace('"',' ');
	}

	/*
	 *  write the name in the format required for an APF file
	 */

	void write (PrintWriter w) {
		String cleanText = text.replace('\n',' ').replace('"',' ');
		w.println ("      <name NAME=\"" + cleanText + "\">");
		AceEntityMention.writeCharseq (w, extent, text);
		w.println ("      </name>");
	}
}
