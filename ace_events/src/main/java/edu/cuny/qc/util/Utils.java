package edu.cuny.qc.util;

import java.io.PrintStream;

public class Utils {

	public static void print(PrintStream out, String prefix, String postfix, String delimiter, Object...args) {
		StringBuffer sb = new StringBuffer(prefix);
		for (int i=0; i<args.length; i++) {
			if (i!=0) {
				sb.append(delimiter);
			}
			sb.append(args[i]);
		}
		sb.append(postfix);
		out.print(sb.toString());
	}
}
