package ac.biu.nlp.nlp.ace_uima.analyze;

import java.util.List;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;


/**
 * Taken from Omer Levy, who adapted it to BasicNode from
 * {@link eu.excitementproject.eop.transformations.utilities.parsetreeutils.PathInTree}
 * <BR>
 * <BR>
 * A "path-in-tree" is a path from one node in the tree to another node in
 * the same tree.
 * <P>
 * The information about such a path is
 * <UL>
 * <LI>The node in which the path starts</LI>
 * <LI>The node in which the path ends, the least common ancestor in that path (i.e.
 * the whole path exists in the sub tree rooted by that least-common-ancestor)</LI>
 * <LI>And all the nodes along the path</LI>
 * </UL>
 * 
 * 
 * @author Asher Stern
 * @since Jan 12, 2011
 *
 */
public class SimplePathInTree
{
	public SimplePathInTree(BasicNode from, BasicNode to,
			BasicNode leastCommonAncestor, List<BasicNode> upNodes,
			List<BasicNode> downNodes)
	{
		super();
		this.from = from;
		this.to = to;
		this.leastCommonAncestor = leastCommonAncestor;
		this.upNodes = upNodes;
		this.downNodes = downNodes;
	}
	
	
	
	public BasicNode getFrom()
	{
		return from;
	}
	public BasicNode getTo()
	{
		return to;
	}
	public BasicNode getLeastCommonAncestor()
	{
		return leastCommonAncestor;
	}
	public List<BasicNode> getUpNodes()
	{
		return upNodes;
	}
	public List<BasicNode> getDownNodes()
	{
		return downNodes;
	}



	private final BasicNode from; // the text node to be moved
	private final BasicNode to; // the new parent
	private final BasicNode leastCommonAncestor;
	private final List<BasicNode> upNodes; // not including from, to, leastCommonAncestor
	private final List<BasicNode> downNodes; // not including from, to, leastCommonAncestor
}
