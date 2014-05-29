// -*- tab-width: 4 -*-
//Title:        JET
//Copyright:    2005
//Author:       Ralph Grishman
//Description:  A Java-based Information Extraction Toolkil
//              (ACE extensions)

package edu.cuny.qc.ace.acetypes;

import java.io.PrintWriter;

import edu.cuny.qc.util.Span;

public class AceEventAnchor extends AceMention {

	Span head;

	Span jetHead;

	boolean passive = false;

	public AceEventAnchor (Span head, Span jetHead, String text) { // Qi
		this.head = head;
		this.jetHead = jetHead;
		this.extent = head;
		this.jetExtent = jetHead;
		this.text = text;
		
	}

	public AceEventArgumentValue getParent () {return null;};

	public String getType () {return null;};

	public Span getJetHead() {
		return jetHead;
	}

	int NP_SEARCH_WINDOW = 40;

	@Override
	public void write(PrintWriter w)
	{
		w.print("<Anchor>");
		w.print(text);
		w.print("</Anchor>");
	}
}
