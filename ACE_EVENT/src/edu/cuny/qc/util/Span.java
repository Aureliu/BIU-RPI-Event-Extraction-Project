// -*- tab-width: 4 -*-
//Title:        JET
//Version:      1.00
//Copyright:    Copyright (c) 1999
//Author:       Ralph Grishman
//Description:  A Java-based Information Extraction Tool

package edu.cuny.qc.util;

/**
 * A portion of a document, represented by its starting and ending character
 * positions, and a pointer to the document.
 * This start and end offests are consistent with ACE apf file, the end point is the index of the last character
 */

public class Span implements Comparable {
	int start;
	int end;

	/**
	 * Creates a span from position <I>s</I> up to position <I>e</I>, with a
	 * null document pointer.
	 */

	public Span(int s, int e) {
		start = s;
		end = e;
	}

	/**
	 * Returns the start of the span.
	 */

	public int start() {
		return start;
	}

	/**
	 * Returns the end of the span.
	 */

	public int end() {
		return end;
	}

	/**
	 * sets the start of the span to 's'.
	 */

	public void setStart(int s) {
		start = s;
	}

	/**
	 * sets the end of the span to 's'.
	 */

	public void setEnd(int e) {
		end = e;
	}

	/**
	 * Returns true if the start and end of the spans are both equal.
	 */

	public boolean equals(Object o) {
			if (o instanceof Span) {
			Span s = (Span) o;
			return (start == s.start) && (end == s.end);
		} else {
			return false;
		}
	}

	public boolean overlap(Span s) {
		if ((start > s.end) || (end < s.start))
			return false;
		else
			return true;
	}

	/**
	 * Returns a hashcode which is a function of the start and end values (so
	 * that, as required for hashing, equal spans have equal hashCodes.
	 */

	public int hashCode() {
		return start * 513 + end;
	}

	/**
	 * returns true if s is larger than this
	 * @param s
	 * @return
	 */
	public boolean smallerThan(Span s)
	{
		return (start >= s.start) && (end < s.end) || (start > s.start) && (end <= s.end);
	}
	
	/**
	 * Returns true if Span 's' contains the span.
	 */
	public boolean within(Span s) {
		return (start >= s.start) && (end <= s.end);
	}
	
	public boolean contains(int offset)
	{
		return (start <= offset) && (end >= offset);
	}

	/**
	 * compares this Span to Object o, which must be a Span. Returns -1 if the
	 * start of this span precedes the start of s, or they have the same start
	 * and the end of this span precedes the end of s. Returns +1 if the start
	 * of this span follows the start of s, or they have the same start and the
	 * end of this span follows the end of s. Otherwise returns 0.
	 */

	public int compareTo(Object o) {
		if (!(o instanceof Span))
			throw new ClassCastException();
		Span s = (Span) o;
		if (start < s.start)
			return -1;
		if (start > s.start)
			return +1;
		if (end < s.end)
			return -1;
		if (end > s.end)
			return +1;
		return 0;
	}

	/**
	 * Returns a printable form of the span, "[start-end]".
	 */

	public String toString() {
		return "[" + start + " - " + end + "]";
	}

	/**
	 * Qi: to compatible with openNLP span
	 * @param text
	 * @return
	 */
	public String getCoveredText(String text)
	{
		return text.substring(start, end+1);
	}
}
