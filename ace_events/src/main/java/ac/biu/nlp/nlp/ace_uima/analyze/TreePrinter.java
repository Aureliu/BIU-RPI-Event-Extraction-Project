package ac.biu.nlp.nlp.ace_uima.analyze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.StringUtil;

public class TreePrinter {
	
	public static String getString(BasicNode root, String pre, String post, SimpleNodeString str) {
		return getStringSubtree(root, str, pre, post, "", "").toString().trim();
	}
	
	public static String getString(Collection<BasicNode> trees, String pre, String post, String treeSeparator, SimpleNodeString str) {
		List<String> strings = new ArrayList<String>(trees.size());
		for (BasicNode root : trees) {
			strings.add(getString(root, pre, post, str));
		}
		return StringUtil.join(strings, treeSeparator);
	}
	
	public static String getString(Collection<BasicNode> trees, String pre, String post, SimpleNodeString str) {
		return getString(trees, pre, post, "|", str);
	}
	
	protected static <I extends Info> StringBuffer getStringSubtree(BasicNode subtree, SimpleNodeString str, String pre, String post, String preFull, String postFull) {
		final String NULL_TREE_STR = "(null)";
		StringBuffer result = new StringBuffer();
		
		if (subtree == null) {
			result.append(NULL_TREE_STR);
		}
		else {
			result.append(preFull);
			result.append(str.toString(subtree));
			
			if (subtree.getChildren() != null) {
				for (BasicNode child : subtree.getChildren()) {
					result.append(getStringSubtree(child, str, pre, post, preFull+pre, post+postFull));
				}
			}
			
			result.append(postFull);
		}
		
		return result;

	}

}
