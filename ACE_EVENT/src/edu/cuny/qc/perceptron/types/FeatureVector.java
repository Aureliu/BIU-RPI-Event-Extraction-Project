package edu.cuny.qc.perceptron.types;

import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.management.RuntimeErrorException;

import edu.cuny.qc.perceptron.core.Decoder;

public class FeatureVector implements Serializable
{	
	private static final long serialVersionUID = 9029197104497329900L;

	// different from Mallet, just use a JDK HashMap to restore the sparse vector
	HashMap<Object, Double> map;
	
	public Map<Object, Double> getMap()
	{
		return map;
	}
	
	public FeatureVector (Object[] feats, double[] values, int capacity) 
	{
		this(capacity);
		for(int key=0; key<feats.length; key++)
		{
			double value = values[key];
			Object feature = feats[key];
			map.put(feature, value);
		}
	}
	
	public FeatureVector ()
	{
		this(50);
	}
	
	public FeatureVector (int capacity) 
	{
		map = new HashMap<Object, Double>(capacity);
	}

	public Double get(Object key)
	{
		return this.map.get(key);
	}
	
	/**
	 * Qi: add a value into the feature vector
	 * different from add, use += if value exists already
	 * @param index
	 * @param value
	 */
	public void add(Object feat, double value)
	{
		// first, check if index already exists
		Double value_exist = map.get(feat);
		if(value_exist == null)
		{
			map.put(feat, value);
		}
		else
		{
			value_exist += value;
			map.put(feat, value_exist);
		}
	}

	public FeatureVector clone()
	{
		FeatureVector fv = new FeatureVector();
		fv.map = (HashMap<Object, Double>) map.clone();
		return fv;
	}

	public final double dotProduct (FeatureVector fv) 
	{
		double ret = 0.0;
		Map<Object, Double> map1 = map;
		Map<Object, Double> map2 = fv.map;
		if(map2.size() < map1.size())
		{
			map1 = fv.map;
			map2 = map;
		}
		for(Object key : map1.keySet())
		{
			Double value2 = map2.get(key);
			if(value2 != null)
			{
				Double value1 = map1.get(key);
				ret += value1 * value2;
			}
		}
		
		////
		//TODO DEBUG
		if (PRINT_FEATURE_VECTORS && numWrittenOutputFiles < MAX_OUTPUT_FILES) {
			try {
				double d = random.nextDouble();
				final FeatureVector fv1 = fv; // in order to use fv in the Comparable object, we need a "final" reference to it
				if (d<PROB_FOR_OUTPUT_FILE) {
					// Output!
					numWrittenOutputFiles++;
					PrintStream vectorsOut = new PrintStream(new File(String.format("%s%sRandomlyChosenDotProduct.%02d.txt", Decoder.outDir, File.separator, numWrittenOutputFiles)));
					vectorsOut.printf("%-110s\tSentenceAssignment(%d)\t\tWeights(%d)\t\tdotProduct=%f\n", " ", map.size(), fv.map.size(), ret);
					vectorsOut.printf("%-110s\t----------------------\t\t-----------\n", " ");
					Set<Object> allKeysSet = new HashSet<Object>(map.keySet());
					allKeysSet.addAll(fv.map.keySet());
					List<Object> allKeys = new ArrayList<Object>(allKeysSet);
					Collections.sort(allKeys, new Comparator<Object>() {
						@Override
						public int compare(Object o1, Object o2) {
//							if (((String) o1).equals((String) o2)) {
//								return 0;
//							}
//							if (map.containsKey(o1) && !fv1.map.containsKey(o2)) {
//								return -1;
//							}
//							if (!map.containsKey(o1) && fv1.map.containsKey(o2)) {
//								return 1;
//							}
							return ((String) o1).compareTo((String) o2);
						}
						
					});
					for (Object key : allKeys) {
						Double m1 = map.get(key);
						Double m2 = fv.map.get(key);
						String str1 = (m1==null? "-" : m1.toString());
						String str2 = (m2==null? "-" : m2.toString());
						vectorsOut.printf("%-110s\t%s\t\t%s\n", key, str1, str2);
					}
					vectorsOut.close();
				}
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		////
		return ret;
	}
	////
	//TODO DEBUG
	private static final boolean PRINT_FEATURE_VECTORS = false; // This ability is now turned off! Put "true" here to turn on!
	private static final int MAX_OUTPUT_FILES = 10;
	private static final double PROB_FOR_OUTPUT_FILE = 0.001;
	private static int numWrittenOutputFiles = 0;
	private static Random random = new Random();
	////

	/**
	 * this = this + (fv1 - fv2) * factor
	 * this function is for updating parameters in perceptron
	 * @param fv1
	 * @param fv2
	 * @param factor
	 */
	public void addDelta(FeatureVector fv1, FeatureVector fv2, double factor)
	{
		for(Object key : fv1.map.keySet())
		{
			Double value1 = fv1.get(key);
			Double value2 = fv2.get(key);
			if(value2 == null)
			{
				value2 = 0.0;
			}
			double value = (value1 - value2) * factor;
			if(value != 0.0)
			{
				this.add(key, value);
			}
		}
		for(Object key : fv2.map.keySet())
		{
			Double value1 = fv1.get(key);
			Double value2 = fv2.get(key);
			if(value1 == null)
			{
				double value = (0.0 - value2) * factor;
				if(value != 0.0)
				{
					this.add(key, value);
				}
			}
		}
	}
	
	// add indices in v if they are not in this, and then plusEquals(v, factor) 
	public void plusEquals (FeatureVector v) 
	{
		plusEquals(v, 1.0);
	}
	
	// add indices in v if they are not in this, and then plusEquals(v, factor) 
	public void plusEquals (FeatureVector fv, double factor) 
	{
		for(Object key : fv.map.keySet())
		{
			Double value_new = fv.map.get(key) * factor; 
			Double value = map.get(key);
			if(value == null)
			{
				map.put(key, value_new);
			}
			else
			{
				map.put(key, value + value_new);
			}
		}
	}
	
	public void multiply(double factor)
	{
		for(Object key : map.keySet())
		{
			Double value = map.get(key);
			map.put(key, value * factor);
		}
	}
	
	public String toString()
	{
		return toString(false);
	}
	
	public String toString (FeatureVector weights)
	{
		//Thread.currentThread().dumpStack();
		StringBuffer sb = new StringBuffer ();
		
	    for(Object key : map.keySet()) 
	    {
			Double value = map.get(key);
			sb.append (key);
			sb.append ("=");
			sb.append (value);
			sb.append (' ');
			sb.append(weights.get(key));	// weight
			sb.append ('\n');
	    }
		return sb.toString();
	}
	
	public String toString (boolean onOneLine)
	{
		//Thread.currentThread().dumpStack();
		StringBuffer sb = new StringBuffer ();
		
	    for(Object key : map.keySet()) 
	    {
			Double value = map.get(key);
			sb.append (key);
			sb.append ("=");
			sb.append (value);
			if (!onOneLine)
			    sb.append ("\n");
			else
			    sb.append (' ');
	    }
		return sb.toString();
	}

	public int size()
	{
		return map.size();
	}
	
	public static void main(String[] args)
	{
		// test map operations
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		map.put(1, 1.0);
		map.put(2, 2.0);
		map.put(3, 3.0);
		
		HashMap<Integer, Double> map2 = (HashMap<Integer, Double>) map.clone();
		map.put(3, 4.0);
		for(Integer key : map2.keySet())
		{
			Double value = map2.get(key);
			System.out.println(key + " " + value);
		}
		
		Double temp = map2.get(3);
		temp++;
		for(Integer key : map2.keySet())
		{
			Double value = map2.get(key);
			System.out.println(key + " " + value);
		}
	}
}
