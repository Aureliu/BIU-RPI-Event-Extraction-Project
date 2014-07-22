package edu.cuny.qc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.ace_uima.analyze.SignalAnalyzer;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

public class Utils {
	public static List<String> logOnlyTheseSentences = null;
	private static final Runtime runtime = Runtime.getRuntime();
	public static final double MB = 1024.0*1024;
	private static final Random random = new Random();

	public static double inMB(long bytes) {
		return bytes / MB;
	}
	public static String detailedLog() {
		long max = runtime.maxMemory();
		long total = runtime.totalMemory();
		return String.format("[%1$tH:%1$tM:%1$tS.%1$tL max=%2$.2f, total=%3$.2f, used=%4$.2f]",
				new Date(),
				max==Long.MAX_VALUE? "no limit" : inMB(max), 
				inMB(total), 
				inMB(total - runtime.freeMemory()));
	}
	
	public static void print(PrintStream out, String prefix, String postfix, String delimiter, Object sentID, Object...args) {
		if (out != null) {
			String sentIDStr = null;
			if (sentID!=null) {
				sentIDStr = sentID.toString();
			}
			if (logOnlyTheseSentences == null || logOnlyTheseSentences.size()==0 || sentIDStr==null || sentIDStr.isEmpty() ||
					sentIDStr.equals(Logs.POST_ITERATION_MARK) || logOnlyTheseSentences.contains(sentIDStr)) {
				out.print(prefix + StringUtils.join(args, delimiter) + postfix);
			}
		}
	}
	
	public static void fileInit(File f) throws FileNotFoundException {
		File prev = new File(f.getAbsolutePath() + ".previous");
		if (prev.isFile()) {
			prev.delete();
		}
		if (f.isFile()) {
			f.renameTo(prev);
		}
		PrintStream p = new PrintStream(f);
		p.printf("(file is writable - verified)");
		p.close();
	}

	public static <K,V> void addToMultimap(Multimap<K,V> multi, Map<K,V> map) {
		for (Entry<K,V> entry : map.entrySet()) {
			multi.put(entry.getKey(), entry.getValue());
		}
	}
	
	public static int randInt(int minInclusive, int maxInclusive) {
		int result = random.nextInt((maxInclusive - minInclusive) + 1) + minInclusive;
		return result;
	}
	
	/**
	 * Doesn't keep original order.
	 */
	public static <T> List<T> sample(List<T> elements, int amount) {
		if (amount > elements.size()) {
			throw new IllegalArgumentException(String.format("Got amount %s which is more than the list's size (%s)", amount, elements.size()));
		}
		List<T> result = Lists.newArrayListWithCapacity(amount);
		ImmutableSortedSet<Integer> indexesImm = ContiguousSet.create(Range.closed(0, elements.size()-1), DiscreteDomain.integers());
		List<Integer> indexes = Lists.newArrayList(indexesImm);
		for (int i=0; i<amount; i++) {
			int ii = randInt(0, indexes.size()-1);
			int index = indexes.remove(ii);
			T element = elements.get(index);
			result.add(element);
		}
		return result;
	}
	public static Logger handleLog() throws IOException {
		File target = new File("./target/classes/log4j.properties");
		Files.createParentDirs(target);
		Files.copy(new File("./log4j.properties"), target);
		return Logger.getLogger(SignalAnalyzer.class);
	}

}
