package edu.cuny.qc.util;

import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import edu.cuny.qc.perceptron.core.Perceptron;

public class Utils {
	public static List<String> logOnlyTheseSentences = null;

	public static void print(PrintStream out, String prefix, String postfix, String delimiter, String sentID, Object...args) {
		if (out != null) {
			if (logOnlyTheseSentences == null || logOnlyTheseSentences.size()==0 || sentID==null || sentID.isEmpty() ||
					sentID.equals(Perceptron.POST_ITERATION_MARK) || logOnlyTheseSentences.contains(sentID)) {
				out.print(prefix + StringUtils.join(args, delimiter) + postfix);
			}
		}
	}
}
