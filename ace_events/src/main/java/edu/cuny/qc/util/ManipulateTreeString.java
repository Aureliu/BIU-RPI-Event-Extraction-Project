package edu.cuny.qc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class ManipulateTreeString {
	public abstract String manipulate(String treeString);
	
	public static abstract class LimitAboveArg extends ManipulateTreeString {
		private static final Pattern PATTERN_DEPS_ABOVE_ARG = Pattern.compile("([\\w\\-\\(\\<\\>\\[\\]]*)\\[ARG\\]");
		public static String manipulateLimit(String treeString, int limit) {
			String[] deps = getDeps(treeString);
			deps = (String[]) ArrayUtils.subarray(deps, deps.length-limit, deps.length);
			String result = StringUtils.join(deps, "(");
			return result;
		}
		private static String[] getDeps(String treeString) {
			String[] deps = new String[] {};
			Matcher matcher = PATTERN_DEPS_ABOVE_ARG.matcher(treeString);
			if (matcher == null || !matcher.find()) {
				return deps;
			}
			String group = matcher.group(1);
			deps = group.split("\\(");
			if (deps[0].equals("<SUBROOT>[PRD]")) {
				deps[0] = "[PRD]";
			}
			return deps;
		}
	}
	
	//// Concrete Classes ////////////////////////////////////////
	
	public static class LimitAboveArg2 extends LimitAboveArg {
		@Override
		public String manipulate(String treeString) {
			return manipulateLimit(treeString, 2);
		}
	}
	
	public static class LimitAboveArg3 extends LimitAboveArg {
		@Override
		public String manipulate(String treeString) {
			return manipulateLimit(treeString, 3);
		}
	}
}
