// -*- tab-width: 4 -*-
//Title:        JET
//Copyright:    2005
//Author:       Ralph Grishman
//Description:  A Java-based Information Extraction Toolkil
//              (ACE extensions)

package edu.cuny.qc.ace.acetypes;

/**
 *  the value of an AceEvent argument:  either a AceEntity, AceValue, or AceTimex.
 */

public abstract class AceEventArgumentValue implements java.io.Serializable {

	public String id;
	
	public String getType() {
		throw new UnsupportedOperationException("This is an optional method.");
	}

}
