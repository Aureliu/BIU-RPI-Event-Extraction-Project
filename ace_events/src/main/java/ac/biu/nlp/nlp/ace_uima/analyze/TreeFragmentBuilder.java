package ac.biu.nlp.nlp.ace_uima.analyze;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

public class TreeFragmentBuilder {
	public FragmentAndReference build(BasicNode fullTreeRoot, Set<BasicNode> targetNodes) throws TreeAndParentMapException, TreeFragmentBuilderException {
		//System.out.println("^^^^^^^ 1");
		Set<SimplePathInTree> paths = getAllTargetPaths(fullTreeRoot, targetNodes);
		//System.out.println("^^^^^^^ 2");
		Set<BasicNode> nodesInAllPaths = getAllNodesInPaths(paths);
		//System.out.println("^^^^^^^ 3");
		FragmentAndReference rootFragAndRef = createTreeFragmentByNodes(fullTreeRoot, nodesInAllPaths);
		//System.out.println("^^^^^^^ 4");
		return rootFragAndRef;
	}
	
	public BidiMap<BasicNode, BasicNode> getOrigToNewNodesMapping() {
		return origToNew;
	}
	
	/**
	 * Returns a set of paths in the tree, that contain only the necessary nodes to connect all target nodes. 
	 * @param fullTreeRoot
	 * @param targetNodes
	 * @return paths covering all target nodes and connections between them. If target nodes is empty,
	 * returns an empty set. If there is exactly one target node, a single path is return, with that node
	 * in the from field.
	 * @throws TreeAndParentMapException
	 */
	private Set<SimplePathInTree> getAllTargetPaths(BasicNode fullTreeRoot, Set<BasicNode> targetNodes) throws TreeAndParentMapException {
		Set<SimplePathInTree> result = new HashSet<SimplePathInTree>();
		if (targetNodes.isEmpty()) {
			//System.out.println("^^^^^^^ 1.1");
			return result;
		}
		else if (targetNodes.size() == 1) {
			SimplePathInTree singleNodePath = new SimplePathInTree(targetNodes.iterator().next(), null, null, null, null);
			result.add(singleNodePath);
			//System.out.println("^^^^^^^ 1.2");
			return result;
		}
		else {
			//System.out.println("^^^^^^^ 1.3");
			TreeAndParentMap<Info, BasicNode> treeAndParentMap = new TreeAndParentMap<Info, BasicNode>(fullTreeRoot);
			SimplePathFinder finder = new SimplePathFinder(treeAndParentMap);
			Iterator<BasicNode> iterNodes = targetNodes.iterator();
			BasicNode pivot = iterNodes.next(); // First node is a pivot, we only need to find its path with all the others
			//System.out.println("^^^^^^^ 1.4");
			while (iterNodes.hasNext()) {
				SimplePathInTree path = finder.findPath(pivot, iterNodes.next());
				//System.out.println("^^^^^^^ 1.5");
				result.add(path);
			}
			//System.out.println("^^^^^^^ 1.6");
			return result;
		}
	}
	
	private Set<BasicNode> getAllNodesInPaths(Collection<SimplePathInTree> paths) {
		Set<BasicNode> nodes = new HashSet<BasicNode>();
		for (SimplePathInTree path : paths) {
			if (path.getFrom() != null) {
				nodes.add(path.getFrom());
			}
			if (path.getTo() != null) {
				nodes.add(path.getTo());
			}
			if (path.getLeastCommonAncestor() != null) {
				nodes.add(path.getLeastCommonAncestor());
			}
			if (path.getUpNodes() != null) {
				nodes.addAll(path.getUpNodes());
			}
			if (path.getDownNodes() != null) {
				nodes.addAll(path.getDownNodes());
			}
		}
		return nodes;
	}
	
	private FragmentAndReference createTreeFragmentByNodes(BasicNode fullTreeRoot, Set<BasicNode> requiredNodes) throws TreeFragmentBuilderException {
		origToNew = new DualHashBidiMap<BasicNode, BasicNode>();
		BasicNode foundRoot = findNewTreeRoot(fullTreeRoot, requiredNodes);
		if (foundRoot != null) {
			BasicNode newRoot = createSubTree(foundRoot, requiredNodes);
			copyAntecedents();
			if (!origToNew.keySet().equals(requiredNodes)) {
				throw new TreeFragmentBuilderException("Generated tree nodes do not exactly match required nodes");
			}
			return new FragmentAndReference(newRoot, origToNew.getKey(newRoot));
		}
		return null;
	}
	
	private BasicNode findNewTreeRoot(BasicNode node, Set<BasicNode> requiredNodes) {
		if (requiredNodes.contains(node)) {
			return node;			
		}
		else {
			if (node.getChildren() != null) {
				for (BasicNode child : node.getChildren()) {
					BasicNode newRoot = findNewTreeRoot(child, requiredNodes);
					if (newRoot != null) {
						return newRoot;
					}
				}
			}
			return null;
		}
	}
	
	/**
	 * Inspired by {@link eu.excitementproject.eop.common.representation.parse.tree.TreeCopier#copySubTree(OS)}
	 * @param node
	 * @param requiredNodes
	 * @return
	 */
	private BasicNode createSubTree(BasicNode node, Set<BasicNode> requiredNodes)
	{
		BasicNode generatedNode = new BasicNode(node.getInfo());
		if (node.getChildren()!=null)
		{
			for (BasicNode child : node.getChildren())
			{
				if (requiredNodes.contains(child)) {
					BasicNode generatedChild = createSubTree(child, requiredNodes);
					generatedNode.addChild(generatedChild);
				}
			}
		}
		origToNew.put(node, generatedNode);
		return generatedNode;
	}

	/**
	 * Inspired by {@link eu.excitementproject.eop.common.representation.parse.tree.TreeCopier#copyAntecedents()}
	 */
	private void copyAntecedents()
	{
		for (BasicNode originalNode : origToNew.keySet())
		{
			if (originalNode.getAntecedent()!=null)
			{
				BasicNode originalAntecedent = originalNode.getAntecedent();
				BasicNode generatedAntecedent = origToNew.get(originalAntecedent);
				origToNew.get(originalNode).setAntecedent(generatedAntecedent);
			}
		}
	}
	
	public class TreeFragmentBuilderException extends Exception {
		private static final long serialVersionUID = 4366291041692891400L;
		public TreeFragmentBuilderException(String msg) {
			super(msg);
		}
	}
	
	BidiMap<BasicNode, BasicNode> origToNew = null;
}
