package edu.cuny.qc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.StringUtil;

/**
 * Convenient static methods for printing a tree in one line, using parentheses to determine nesting.
 * @author Ofer Bronstein
 * @since August 2014
 */
public class TreeToLineString {

	private  TreeToLineString() {}

	
	//// Specific Methods ///////////////////////////////////////////////////////
	
	/////// Single Node
	
	public static String getStringWordRel(BasicNode tree) {
		return getString(tree, new NodeShortString.WordRel());
	}
	
	public static String getStringWordRelPos(BasicNode tree) {
		return getString(tree, new NodeShortString.WordRelPos());
	}
	
	public static String getStringWordRelCanonicalPos(BasicNode tree) {
		return getString(tree, new NodeShortString.WordRelCanonicalPos());
	}
	
	
	/////// Multiple Nodes
	
	public static String getStringRel(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.Rel());
	}

	public static String getStringRelFlat(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlat());
	}

	public static String getStringRelPrep(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPrep());
	}

	public static String getStringRelFlatPrep(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatPrep());
	}

	public static String getStringRelUp2(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.Rel(), new ManipulateTreeString.LimitAboveArg2());
	}

	public static String getStringRelUp3(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.Rel(), new ManipulateTreeString.LimitAboveArg3());
	}

	public static String getStringRelFlatUp2(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlat(), new ManipulateTreeString.LimitAboveArg2());
	}

	public static String getStringRelFlatUp3(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlat(), new ManipulateTreeString.LimitAboveArg3());
	}

	public static String getStringRelPrepUp2(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPrep(), new ManipulateTreeString.LimitAboveArg2());
	}

	public static String getStringRelPrepUp3(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPrep(), new ManipulateTreeString.LimitAboveArg3());
	}

	public static String getStringRelFlatPrepUp2(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatPrep(), new ManipulateTreeString.LimitAboveArg2());
	}

	public static String getStringRelFlatPrepUp3(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatPrep(), new ManipulateTreeString.LimitAboveArg3());
	}

	public static String getStringRelPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPos());
	}

	public static String getStringRelFlatPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatPos());
	}

	public static String getStringRelPrepPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPrepPos());
	}

	public static String getStringRelFlatPrepPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatPrepPos());
	}

	public static String getStringRelUp2Pos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPos(), new ManipulateTreeString.LimitAboveArg2());
	}

	public static String getStringRelUp3Pos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPos(), new ManipulateTreeString.LimitAboveArg3());
	}

	public static String getStringRelFlatUp2Pos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatPos(), new ManipulateTreeString.LimitAboveArg2());
	}

	public static String getStringRelFlatUp3Pos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatPos(), new ManipulateTreeString.LimitAboveArg3());
	}

	public static String getStringRelPrepUp2Pos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPrepPos(), new ManipulateTreeString.LimitAboveArg2());
	}

	public static String getStringRelPrepUp3Pos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPrepPos(), new ManipulateTreeString.LimitAboveArg3());
	}

	public static String getStringRelFlatPrepUp2Pos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatPrepPos(), new ManipulateTreeString.LimitAboveArg2());
	}

	public static String getStringRelFlatPrepUp3Pos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatPrepPos(), new ManipulateTreeString.LimitAboveArg3());
	}

	public static String getStringRelCanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelCanonicalPos());
	}

	public static String getStringRelFlatCanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatCanonicalPos());
	}

	public static String getStringRelPrepCanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPrepCanonicalPos());
	}

	public static String getStringRelFlatPrepCanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatPrepCanonicalPos());
	}

	public static String getStringRelUp2CanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelCanonicalPos(), new ManipulateTreeString.LimitAboveArg2());
	}

	public static String getStringRelUp3CanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelCanonicalPos(), new ManipulateTreeString.LimitAboveArg3());
	}

	public static String getStringRelFlatUp2CanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatCanonicalPos(), new ManipulateTreeString.LimitAboveArg2());
	}

	public static String getStringRelFlatUp3CanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatCanonicalPos(), new ManipulateTreeString.LimitAboveArg3());
	}

	public static String getStringRelPrepUp2CanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPrepCanonicalPos(), new ManipulateTreeString.LimitAboveArg2());
	}

	public static String getStringRelPrepUp3CanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPrepCanonicalPos(), new ManipulateTreeString.LimitAboveArg3());
	}

	public static String getStringRelFlatPrepUp2CanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatPrepCanonicalPos(), new ManipulateTreeString.LimitAboveArg2());
	}

	public static String getStringRelFlatPrepUp3CanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelFlatPrepCanonicalPos(), new ManipulateTreeString.LimitAboveArg3());
	}

	public static String getStringWordRel(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.WordRel());
	}

	public static String getStringWordRelPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.WordRelPos());
	}

	public static String getStringWordRelCanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.WordRelCanonicalPos());
	}

	
	//// Generic Methods ///////////////////////////////////////////////////////

	public static String getString(BasicNode tree, NodeShortString nodeStr) {
		return getString(tree, "(", ")", nodeStr);
	}
	
	public static String getString(List<BasicNode> trees, boolean withContext, boolean withMagicNodes, NodeShortString nodeStr, ManipulateTreeString manipulator) {
		if (trees.isEmpty()) {
			return "(empty-tree)";
		}
		String subrootDep = null;
		if (!withContext) {
			subrootDep = "<SUBROOT>";
		}
		return getString(trees, "(", ")", "#", subrootDep, withMagicNodes, nodeStr, manipulator);
	}
	
	public static String getString(List<BasicNode> trees, boolean withContext, boolean withMagicNodes, NodeShortString nodeStr) {
		return getString(trees, withContext, withMagicNodes, nodeStr, null);
	}
	
	public static String getString(BasicNode root, String pre, String post, String dep, boolean withMagicNodes, NodeShortString str, ManipulateTreeString manipulator) {
		return getStringSubtree(root, str, manipulator, pre, post, dep, withMagicNodes).toString().trim();
	}
	
	public static String getString(BasicNode root, String pre, String post, String dep, boolean withMagicNodes, NodeShortString str) {
		return getStringSubtree(root, str, null, pre, post, dep, withMagicNodes).toString().trim();
	}
	
	public static String getString(BasicNode root, String pre, String post, NodeShortString str) {
		return getStringSubtree(root, str, null, pre, post, null, true).toString().trim();
	}
	
	public static String getString(BasicNode root, String pre, String post, boolean withMagicNodes, NodeShortString str) {
		return getStringSubtree(root, str, null, pre, post, null, withMagicNodes).toString().trim();
	}
	
	public static String getString(Collection<BasicNode> trees, String pre, String post, String treeSeparator, String dep, boolean withMagicNodes, NodeShortString str, ManipulateTreeString manipulator) {
		List<String> strings = new ArrayList<String>(trees.size());
		for (BasicNode root : trees) {
			strings.add(getString(root, pre, post, dep, withMagicNodes, str, manipulator));
		}
		return StringUtil.join(strings, treeSeparator);
	}
	
	public static String getString(Collection<BasicNode> trees, String pre, String post, boolean withMagicNodes, NodeShortString str) {
		return getString(trees, pre, post, null, "#", withMagicNodes, str, null);
	}
	
	protected static <I extends Info> StringBuffer getStringSubtree(BasicNode subtree, NodeShortString str, ManipulateTreeString manipulator, String pre, String post, String dep, boolean withMagicNodes) {
		final String NULL_TREE_STR = "(null)";
		StringBuffer result = new StringBuffer();
		
		if (subtree == null) {
			result.append(NULL_TREE_STR);
		}
		else {
			if (subtree.getInfo().getNodeInfo().getWord() != null) {
				String nodeDep;
				if (dep != null) {
					nodeDep = dep;
				}
				else {
					nodeDep = str.toString(subtree);
				}
				
				// "Magic Node" data should just be added to nodeDep
				if (	withMagicNodes &&
						subtree.getInfo().getNodeInfo().getWordLemma()!=null &&
						MAGIC_NODES.contains(subtree.getInfo().getNodeInfo().getWordLemma())) {
					nodeDep += subtree.getInfo().getNodeInfo().getWordLemma();
				}
				
				result.append(nodeDep);
			}
			
			if (subtree.getChildren() != null) {
				for (BasicNode child : subtree.getChildren()) {
					result.append(pre);
					result.append(getStringSubtree(child, str, null, pre, post, null, withMagicNodes));
					result.append(post);
				}
			}
		}
		
		if (manipulator != null) {
			result = new StringBuffer(manipulator.manipulate(result.toString()));
		}
		
		return result;
	}

	
	// "Magic Nodes" are one with specific importance for a tree/fragment, and should be printed accordingly
	public static final String MAGIC_NODE_PREDICATE = "[PRD]";
	public static final String MAGIC_NODE_ARGUMENT = "[ARG]";
	public static final Set<String> MAGIC_NODES = new HashSet<String>(Arrays.asList(new String[] {MAGIC_NODE_PREDICATE, MAGIC_NODE_ARGUMENT}));
	
}
