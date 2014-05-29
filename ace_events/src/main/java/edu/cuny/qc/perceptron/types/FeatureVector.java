package edu.cuny.qc.perceptron.types;

import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import java_cup.internal_error;

import javax.management.RuntimeErrorException;

import org.apache.commons.lang3.StringUtils;

import edu.cuny.qc.perceptron.core.Decoder;
import edu.cuny.qc.perceptron.core.Perceptron;

public class FeatureVector implements Serializable
{	
	private static final long serialVersionUID = 9029197104497329900L;

	// different from Mallet, just use a JDK HashMap to restore the sparse vector
	HashMap<Object, BigDecimal> map;
	
	static {
		System.err.printf("??? FeatureVector.addDelta2: comparing to Double values, probably a bad idea. Maybe they should just be boolean? must they really be Double?");
		System.err.printf("??? FeatureVector.addDelta2: Currently ignoring if the signal made a mistake. Maybe its weight should be punished.");
	}
	
	public Map<Object, BigDecimal> getMap()
	{
		return map;
	}
	
//	public FeatureVector (Object[] feats, double[] values, int capacity) 
//	{
//		this(capacity);
//		for(int key=0; key<feats.length; key++)
//		{
//			double value = values[key];
//			Object feature = feats[key];
//			map.put(feature, value);
//		}
//	}
	
	public FeatureVector ()
	{
		this(20);
	}
	
	public FeatureVector (int capacity) 
	{
		map = new LinkedHashMap<Object, BigDecimal>(capacity);
	}

	public BigDecimal get(Object key)
	{
		return this.map.get(key);
	}
	
	/**
	 * Qi: add a value into the feature vector
	 * different from add, use += if value exists already
	 * @param index
	 * @param value
	 */
	public void add(Object feat, BigDecimal value)
	{
		// first, check if index already exists
		BigDecimal value_exist = map.get(feat);
		if(value_exist == null)
		{
			map.put(feat, value);
		}
		else
		{
			value_exist = value_exist.add(value);
			map.put(feat, value_exist);
		}
	}

	public FeatureVector clone()
	{
		FeatureVector fv = new FeatureVector();
		fv.map = (HashMap<Object, BigDecimal>) map.clone();
		return fv;
	}

	public final BigDecimal dotProduct (FeatureVector fv) 
	{
		BigDecimal ret = BigDecimal.ZERO;
		Map<Object, BigDecimal> map1 = map;
		Map<Object, BigDecimal> map2 = fv.map;
		if(map2.size() < map1.size())
		{
			map1 = fv.map;
			map2 = map;
		}
		for(Object key : map1.keySet())
		{
			BigDecimal value2 = map2.get(key);
			if(value2 != null)
			{
				BigDecimal value1 = map1.get(key);
				ret = ret.add(value1.multiply(value2)); // ret += value1 * value2;
			}
		}
		
		////
		//TODO DEBUG
		if (PRINT_FEATURE_VECTORS && numWrittenOutputFiles < MAX_OUTPUT_FILES) {
//			try {
//				double d = random.nextDouble();
//				final FeatureVector fv1 = fv; // in order to use fv in the Comparable object, we need a "final" reference to it
//				if (d<PROB_FOR_OUTPUT_FILE) {
//					// Output!
//					numWrittenOutputFiles++;
//					PrintStream vectorsOut = new PrintStream(new File(String.format("%s%sRandomlyChosenDotProduct.%02d__%f.txt", Decoder.outDir, File.separator, numWrittenOutputFiles, ret)));
//					vectorsOut.printf("%-110s\tSentenceAssignment(%d)\t\tWeights(%d)\t\tdotProduct=%f\n", " ", map.size(), fv.map.size(), ret);
//					vectorsOut.printf("%-110s\t----------------------\t\t-----------\n", " ");
//					Set<Object> allKeysSet = new HashSet<Object>(map.keySet());
//					allKeysSet.addAll(fv.map.keySet());
//					List<Object> allKeys = new ArrayList<Object>(allKeysSet);
//					Collections.sort(allKeys, new Comparator<Object>() {
//						@Override
//						public int compare(Object o1, Object o2) {
////							if (((String) o1).equals((String) o2)) {
////								return 0;
////							}
////							if (map.containsKey(o1) && !fv1.map.containsKey(o2)) {
////								return -1;
////							}
////							if (!map.containsKey(o1) && fv1.map.containsKey(o2)) {
////								return 1;
////							}
//							return ((String) o1).compareTo((String) o2);
//						}
//						
//					});
//					for (Object key : allKeys) {
//						Double m1 = map.get(key);
//						Double m2 = fv.map.get(key);
//						String str1 = (m1==null? "-" : m1.toString());
//						String str2 = (m2==null? "-" : m2.toString());
//						vectorsOut.printf("%-110s\t%s\t\t%s\n", key, str1, str2);
//					}
//					vectorsOut.close();
//				}
//			}
//			catch (Exception e) {
//				throw new RuntimeException(e);
//			}
		}
		////
		return ret;
	}
	////
	//TODO DEBUG
	private static final boolean PRINT_FEATURE_VECTORS = false; // This ability is now turned off! Put "true" here to turn on!
	private static final int MAX_OUTPUT_FILES = 20;//10;
	private static final double PROB_FOR_OUTPUT_FILE = 0.01;//0.001;
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
	public void addDelta(FeatureVector fv1, FeatureVector fv2, BigDecimal factor)
	{
		for(Object key : fv1.map.keySet())
		{
			BigDecimal value1 = fv1.get(key);
			BigDecimal value2 = fv2.get(key);
			if(value2 == null)
			{
				value2 = BigDecimal.ZERO;
			}
			BigDecimal value = factor.multiply(value1.subtract(value2));//(value1 - value2) * factor;
			if(!value.equals(BigDecimal.ZERO))
			{
				this.add(key, value);
				//System.out.printf("  - [%s,%s,%s] %-70s\t += %s\n", value1, value2, factor, key, value);
			}
		}
		for(Object key : fv2.map.keySet())
		{
			BigDecimal value1 = fv1.get(key);
			BigDecimal value2 = fv2.get(key);
			if(value1 == null)
			{
				BigDecimal value = factor.multiply(value2.negate());//(0.0 - value2) * factor;
				if(!value.equals(BigDecimal.ZERO))
				{
					this.add(key, value);
					//System.out.printf("  @ [%s,%s,%s] %-70s\t += %s\n", value1, value2, factor, key, value);
				}
			}
		}
	}
	
	public void addDelta2(FeatureVector targetFV, FeatureVector assnFV, BigDecimal factor) {
		if (targetFV.map.size() != assnFV.map.size()) {
			throw new IllegalStateException(String.format("Unequal feature vectors sizes: |Target|=%d features, |Assignment|=%d features", targetFV.map.size(), assnFV.map.size()));
		}
		for(Object key : targetFV.map.keySet())
		{
			BigDecimal targetVal = targetFV.get(key);
			BigDecimal assnVal = assnFV.get(key);
			
			if (targetVal.equals(assnVal)) {
				this.add(key, factor);
			}
//			else {
//				do some crazy shit, like decrease some value from the feature's weight
//			}
		}
	}

	
	// add indices in v if they are not in this, and then plusEquals(v, factor) 
//	public void plusEquals (FeatureVector v) 
//	{
//		plusEquals(v, 1.0);
//	}
	
	// add indices in v if they are not in this, and then plusEquals(v, factor) 
//	public void plusEquals (FeatureVector fv, double factor) 
//	{
//		for(Object key : fv.map.keySet())
//		{
//			Double value_new = fv.map.get(key) * factor; 
//			Double value = map.get(key);
//			if(value == null)
//			{
//				map.put(key, value_new);
//			}
//			else
//			{
//				map.put(key, value + value_new);
//			}
//		}
//	}
	
//	public void multiply(double factor)
//	{
//		for(Object key : map.keySet())
//		{
//			Double value = map.get(key);
//			map.put(key, value * factor);
//		}
//	}
	
	public String toString() {
		final int PRINT_THRESHOLD = 20;
		String full = "";
		if (map.size() <= PRINT_THRESHOLD) {
			full = toStringFull(true);
		}
		return String.format("%s(%s items)%s", FeatureVector.class.getSimpleName(), map.size(), full);		
	}
	
	public String toStringFull()
	{
		return toStringFull(false);
	}
	
	public String toString (FeatureVector weights)
	{
		//Thread.currentThread().dumpStack();
		StringBuffer sb = new StringBuffer ();
		
	    for(Object key : map.keySet()) 
	    {
	    	BigDecimal value = map.get(key);
			sb.append (key);
			sb.append ("=");
			sb.append (value);
			sb.append (' ');
			sb.append(weights.get(key));	// weight
			sb.append ('\n');
	    }
		return sb.toString();
	}
	
	public String toStringFull (boolean onOneLine)
	{
		//Thread.currentThread().dumpStack();
		StringBuffer sb = new StringBuffer ();

		List<String> keys = new ArrayList<String>(map.size());
		for (Object key : map.keySet()) {
			keys.add((String) key);
		}
		Collections.sort(keys);
		
	    for(Object key : keys) 
	    {
	    	BigDecimal value = map.get(key);
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

	private static String stringify(List<?> list) {
		ArrayList<String> strs = new ArrayList<String>(list.size());
		for (Object o : list) {
			strs.add(Perceptron.FMT.format(o));
		}
		return StringUtils.join(strs, ',');
	}
	
	public String toStringOnlyValues() {
		final int START_TO=15, END_FROM=5;
		String content;
		List<?> values = new ArrayList<BigDecimal>(map.values());
		if (values.size() <= START_TO + END_FROM) {
			content = stringify(values);
		}
		else {
			List<?> start = values.subList(0, START_TO);
			List<?> end = values.subList(values.size()-END_FROM, values.size());
			content = stringify(start) + "..." + stringify(end);
		}
		return /*"(" +*/ content /*+ ")"*/;
	}
	
	public int size()
	{
		return map.size();
	}
	}
