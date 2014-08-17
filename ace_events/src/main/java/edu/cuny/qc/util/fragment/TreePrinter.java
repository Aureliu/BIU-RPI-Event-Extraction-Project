package edu.cuny.qc.util.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.StringUtil;

public class TreePrinter {
	
	public static String getString(BasicNode root, String pre, String post, String dep, SimpleNodeString str) {
		return getStringSubtree(root, str, pre, post, "", "", dep).toString().trim();
	}
	
	public static String getString(BasicNode root, String pre, String post, SimpleNodeString str) {
		return getStringSubtree(root, str, pre, post, "", "", null).toString().trim();
	}
	
	public static String getString(Collection<BasicNode> trees, String pre, String post, String treeSeparator, String dep, SimpleNodeString str) {
		List<String> strings = new ArrayList<String>(trees.size());
		for (BasicNode root : trees) {
			strings.add(getString(root, pre, post, dep, str));
		}
		return StringUtil.join(strings, treeSeparator);
	}
	
	public static String getString(Collection<BasicNode> trees, String pre, String post, SimpleNodeString str) {
		return getString(trees, pre, post, null, "|", str);
	}
	
	protected static <I extends Info> StringBuffer getStringSubtree(BasicNode subtree, SimpleNodeString str, String pre, String post, String preFull, String postFull, String dep) {
		final String NULL_TREE_STR = "(null)";
		StringBuffer result = new StringBuffer();
		
		if (subtree == null) {
			result.append(NULL_TREE_STR);
		}
		else {
			result.append(preFull);
			
			String nodeDep;
			if (dep != null) {
				nodeDep = dep;
			}
			else {
				nodeDep = str.toString(subtree);
			}
			result.append(nodeDep);
			
			if (subtree.getChildren() != null) {
				for (BasicNode child : subtree.getChildren()) {
					result.append(getStringSubtree(child, str, pre, post, preFull+pre, post+postFull, null));
					//result.append(getStringSubtree(child, str, pre, post, preFull, postFull));
				}
			}
			
			result.append(postFull);
		}
		
		return result;

	}

}
