package edu.cuny.qc.util;

import java.io.PrintStream;

import org.apache.commons.lang3.StringUtils;

public class Utils {

	public static void print(PrintStream out, String prefix, String postfix, String delimiter, Object...args) {
		if (out != null) {
			out.print(prefix + StringUtils.join(args, delimiter) + postfix);
		}
	}
}
