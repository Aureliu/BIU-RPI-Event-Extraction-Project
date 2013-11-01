// -*- tab-width: 4 -*-
//Title:        JET
//Copyright:    2005
//Author:       Ralph Grishman
//Description:  A Java-based Information Extraction Toolkil
//              (ACE extensions)

package edu.cuny.qc.ace.acetypes;

import java.io.PrintWriter;
import java.util.Vector;

import edu.cuny.qc.util.Span;

/**
 *  the value of an AceEventMention argument:  an
 *  AceEntityMention, an AceTimexMention, an AceValueMention, or an AceEventAnchor.
 */

public abstract class AceMention implements Comparable {

	/**
	 * Qi: indices of head tokens in the sentence
	 */
	public Vector<Integer> headIndices;
	public Vector<Integer> extentIndices;
	
	/**
	 * calculate the indice of each head token accodring to span of 
	 * token in this sentence. For Value/Timex, this corrresponds to extent, for Entity, this corresponds to head
	 * @param tokenSpans
	 */
	public void setExtentIndices(Span[] tokenSpans)
	{
		if(extentIndices == null)
		{
			extentIndices = new Vector<Integer>();
			int i = 0;
			for(Span tokenSpan : tokenSpans)
			{
				if(this.extent.overlap(tokenSpan))
				{
					extentIndices.add(i);
				}
				i++;
			}
		}
	}
	
	/**
	 * calculate the indice of each head token accodring to span of 
	 * token in this sentence. For Value/Timex, this corrresponds to extent, for Entity, this corresponds to head
	 * @param tokenSpans
	 */
	public void setHeadIndices(Span[] tokenSpans)
	{
		if(headIndices == null && extentIndices == null)
		{
			setExtentIndices(tokenSpans);
		}
		headIndices = extentIndices;
	}
	
	public Vector<Integer> getExtentIndices()
	{
		return extentIndices;
	}
	
	public Vector<Integer> getHeadIndices()
	{
		return headIndices;
	}
	
	public String toString()
	{
		String ret = "";
		ret += getType() + ":";
		ret += text;
		return ret;
	}
	
	public String id;
	/**
	 *  the extent of the mention, with start and end positions based on
	 *  ACE offsets (excluding XML tags).
	 */
	public Span extent;
	/**
	 *  the extent of the mention, with start and end positions based on
	 *  Jet offsets (including all following whitespace).
	 */
	public Span jetExtent;
	/**
	 *  the text of the extent.
	 */
	public String text;
	/**
	 *  the parent (the entity, value, or timex containing this mention)
	 */
	public abstract AceEventArgumentValue getParent ();
	
	/**
	 *  the type (of the parent entity, value, or timex).
	 */
	public abstract String getType ();
	
	public Span getJetHead() {
		return jetExtent;
	}

	public String getHeadText () {
		return text;
	}

	/**
	 *  returns a positive, zero, or negative integer depending on whether the
	 *  start of the head of 'o' follows, is the same as, or precedes the head
	 *  of this AceMention.
	 */

	public int compareTo (Object o) {
		if (!(o instanceof AceMention)) throw new ClassCastException();
		int d = getJetHead().compareTo(((AceMention)o).getJetHead());
		if (d != 0)
			return d;
		else
			return typeCode(this) - typeCode((AceMention)o);
	}

	private int typeCode (AceMention o) {
		if (o instanceof AceValueMention) return 3;
		if (o instanceof AceTimexMention) return 2;
		if (o instanceof AceEntityMention) return 1;
		return 0;
	}
	
	public abstract void write (PrintWriter w);
}
