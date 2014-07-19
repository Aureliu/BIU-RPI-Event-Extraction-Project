package edu.cuny.qc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Multimap;

public class Utils {
	public static List<String> logOnlyTheseSentences = null;
	private static final Runtime runtime = Runtime.getRuntime();
	public static final double MB = 1024.0*1024;

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
}
