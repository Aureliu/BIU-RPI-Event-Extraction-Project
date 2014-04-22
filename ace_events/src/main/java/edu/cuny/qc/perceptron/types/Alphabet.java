/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */




/**
   @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */

package edu.cuny.qc.perceptron.types;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *  A mapping between integers and objects where the mapping in each
 * direction is efficient.  Integers are assigned consecutively, starting
 * at zero, as objects are added to the Alphabet.  Objects can not be
 * deleted from the Alphabet and thus the integers are never reused.
 * <p>
 * The most common use of an alphabet is as a dictionary of feature names
 * associated with a {@link edu.cuny.qc.perceptron.types.FeatureVector} in an
 * {@link edu.cuny.qc.perceptron.types.Instance}. In a simple document
 * classification usage,
 * each unique word in a document would be a unique entry in the Alphabet
 * with a unique integer associated with it.   FeatureVectors rely on
 * the integer part of the mapping to efficiently represent the subset of
 * the Alphabet present in the FeatureVector.
 * @see FeatureVector
 * @see cc.mallet.pipe.Pipe
 */
public class Alphabet implements Serializable
{
	private static final long serialVersionUID = 491382627057328065L;
	
//	HashMap<Object, Integer> map;
	TObjectIntHashMap<Object> map;
	ArrayList<Object> entries;
	
	public Alphabet (int capacity)
	{
//		this.map = new HashMap<Object, Integer> (capacity);
		this.map = new TObjectIntHashMap<Object>();
		this.entries = new ArrayList<Object> (capacity);
	}

	public Alphabet ()
	{
		this (100);
	}
	
	public Alphabet (Object[] entries) {
		this (entries.length);
		for (Object entry : entries)
			this.lookupIndex(entry);
	}

	/** Return -1 if entry isn't present. */
	public int lookupIndex (Object entry, boolean addIfNotPresent)
	{
		Integer retIndex = -1;
		if(map.containsKey(entry))
		{
			retIndex = map.get(entry);
		}
		else if (addIfNotPresent) 
		{
			retIndex = entries.size();
			map.put (entry, retIndex);
			entries.add (entry);
		}
		else
		{
			retIndex = -1;
		}
		return retIndex;
	}

	public int lookupIndex (Object entry)
	{
		return lookupIndex (entry, true);
	}

	public Object lookupObject (int index)
	{
		return entries.get(index);
	}

	public Object[] toArray () {
		return entries.toArray();
	}

	/**
	 * Returns an array containing all the entries in the Alphabet.
	 *  The runtime type of the returned array is the runtime type of in.
	 *  If in is large enough to hold everything in the alphabet, then it
	 *  it used.  The returned array is such that for all entries <tt>obj</tt>,
	 *  <tt>ret[lookupIndex(obj)] = obj</tt> .
	 */ 
	public Object[] toArray (Object[] in) {
		return entries.toArray (in);
	}

	// xxx This should disable the iterator's remove method...
	public Iterator<Object> iterator () {
		return entries.iterator();
	}

	public Object[] lookupObjects (int[] indices)
	{
		Object[] ret = new Object[indices.length];
		for (int i = 0; i < indices.length; i++)
			ret[i] = entries.get(indices[i]);
		return ret;
	}

	/**
	 * Returns an array of the objects corresponding to
	 * @param indices An array of indices to look up
	 * @param buf An array to store the returned objects in.
	 * @return An array of values from this Alphabet.  The runtime type of the array is the same as buf
	 */
	public Object[] lookupObjects (int[] indices, Object[] buf)
	{
		for (int i = 0; i < indices.length; i++)
			buf[i] = entries.get(indices[i]);
		return buf;
	}

	public int[] lookupIndices (Object[] objects, boolean addIfNotPresent)
	{
		int[] ret = new int[objects.length];
		for (int i = 0; i < objects.length; i++)
			ret[i] = lookupIndex (objects[i], addIfNotPresent);
		return ret;
	}

	public boolean contains(Object entry)
	{
		return map.containsKey(entry);
	}

	public int size ()
	{
		return entries.size();
	}

	/** Return String representation of all Alphabet entries, each
	separated by a newline. */
	public String toString()
	{
		// To avoid freezing of debugger whenever displaying this object
		final int PRINT_THRESHOLD = 20;
		String full = "";
		if (entries.size() <= PRINT_THRESHOLD) {
			full = entries.toString();
		}
		return String.format("%s(%s items)%s", Alphabet.class.getSimpleName(), entries.size(), full);		
	}

	public void dump () { dump (System.out); }

	public void dump (PrintStream out)
	{
		dump (new PrintWriter (new OutputStreamWriter (out), true));
	}

	public void dump (PrintWriter out)
	{
		for (int i = 0; i < entries.size(); i++) {
			out.println (i+" => "+entries.get (i));
		}
	}

}
