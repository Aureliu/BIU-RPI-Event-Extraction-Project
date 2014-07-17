package edu.cuny.qc.util;

import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Utils {
	public static List<String> logOnlyTheseSentences = null;

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
}
