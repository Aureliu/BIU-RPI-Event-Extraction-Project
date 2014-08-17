package edu.cuny.qc.util.fragment;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ac.biu.nlp.nlp.ace_uima.utils.AnotherBasicNodeUtils;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;


/**
 * Taken from Omer Levy, who adapted it to BasicNode from
 * {@link eu.excitementproject.eop.transformations.utilities.parsetreeutils.Pathfinder}
 * <BR>
 * <BR>
 * Given a parse-tree-node to be moved, and given the new parent of that node
 * (i.e., the parent into which the node should be moved), this class finds the
 * path (represented as {@link PathInTree}) from the node to its new parent, in the
 * original parse-tree.
 * 
 * @author Asher Stern
 * @since Jan 12, 2011
 *
 */
public class SimplePathFinder
{
	public SimplePathFinder(TreeAndParentMap<Info, BasicNode> tree)
	{
		super();
		this.tree = tree;
		
	}

	
	public SimplePathInTree findPath(BasicNode textNodeToMove, BasicNode textNodeToBeParent)
	{

		//System.out.println("^^^^^^^ 1.4.1");		
		BasicNode leastCommonAncestor = findLca(textNodeToMove,textNodeToBeParent);
		
		//System.out.printf("~~~nodes=%s<%s>,%s<%s> lca=%s<%s>\n%s\n%s\n\n", textNodeToMove.getInfo().getId(), textNodeToMove.hashCode(),
		//		textNodeToBeParent.getInfo().getId(), textNodeToBeParent.hashCode(),
		//		leastCommonAncestor.getInfo().getId(), leastCommonAncestor.hashCode(),
		//		AbstractNodeUtils.getIndentedString(tree.getTree()), AnotherBasicNodeUtils.getNodesString(tree.getParentMap()));

		//System.out.println("^^^^^^^ 1.4.2");		
		List<BasicNode> upList = findUpNodes(textNodeToMove,textNodeToBeParent,leastCommonAncestor);
		//System.out.println("^^^^^^^ 1.4.3");		
		List<BasicNode> downList = findDownNodes(textNodeToMove,textNodeToBeParent,leastCommonAncestor);
		//System.out.println("^^^^^^^ 1.4.4");		
		return new SimplePathInTree(textNodeToMove, textNodeToBeParent, leastCommonAncestor, upList, downList);
	}
	
	
	private BasicNode findLca(BasicNode textNodeToMove, BasicNode textNodeToBeParent)
	{
		BasicNode ret = null;
		
		Set<BasicNode> nodes_from_root = new LinkedHashSet<BasicNode>();
		BasicNode current = textNodeToMove;
		nodes_from_root.add(current);
		
		//TODO debug
		//if (!tree.getParentMap().containsKey(current)) {
		//	System.out.printf("  &&&&&&&&&&& normal node is not in parent map! current=%s\n", AnotherBasicNodeUtils.getNodeString(current));
		//}
			
			
		while(tree.getParentMap().containsKey(current))
		{
			BasicNode parent = tree.getParentMap().get(current);
			nodes_from_root.add(parent);
			current = parent;
		}
		
		current = textNodeToBeParent;
		while ( (current!=null) && (!nodes_from_root.contains(current)) )
		{
			current = tree.getParentMap().get(current);
		}
		
		if (null==current)
		{
			ret = textNodeToBeParent;
		}
		else
		{
			ret = current;
		}
		//System.out.printf("      current=%s\n", AnotherBasicNodeUtils.getNodeString(current));
		//System.out.printf("      nodes_from_root=%s\n", AnotherBasicNodeUtils.getNodesString(nodes_from_root));
		return ret;
	}
	
	private List<BasicNode> findUpNodes(BasicNode textNodeToMove, BasicNode textNodeToBeParent, BasicNode leastCommonAncestor)
	{
		List<BasicNode> ret = new LinkedList<BasicNode>();
		if (leastCommonAncestor==textNodeToMove)
		{
			// do nothing - empty list
		}
		else
		{
			BasicNode current = tree.getParentMap().get(textNodeToMove);
			while(current != leastCommonAncestor)
			{
				ret.add(current);
				current = tree.getParentMap().get(current);
				if (current == null) {
					//System.out.printf("@@@ current=%s, ret.size=%s\n", current, ret.size());		
				}
			}
		}
		return ret;
	}

	
	private List<BasicNode> findDownNodes(BasicNode textNodeToMove, BasicNode textNodeToBeParent, BasicNode leastCommonAncestor)
	{
		List<BasicNode> ret = new LinkedList<BasicNode>();
		if (leastCommonAncestor==textNodeToBeParent)
		{
			// do nothing
		}
		else
		{
			BasicNode current = tree.getParentMap().get(textNodeToBeParent);
			while (current!=leastCommonAncestor)
			{
				ret.add(current);
				current=tree.getParentMap().get(current);
			}
		}
		Collections.reverse(ret);
		return ret;
	}
	
	private TreeAndParentMap<Info,BasicNode> tree;
}
