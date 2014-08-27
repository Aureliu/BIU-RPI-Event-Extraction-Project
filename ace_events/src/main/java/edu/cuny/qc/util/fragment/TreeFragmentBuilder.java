package edu.cuny.qc.util.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;


import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

public class TreeFragmentBuilder {
	static {
		System.err.printf("??? TreeFragmentBuilder: Silently passing on the weird situation where I have multpile conjs in my resuired nodes, and I don't know which to choose. So I don't choose any. It's in getSingleRequiredConjChild()\n");
		System.err.printf("??? TreeFragmentBuilder: I want to remove a node due to conj, but it's part of target nodes, so I pass silently. ()\n");
		System.err.printf("??? TreeFragmentBuilder: And also something in createTreeFragmentByNodes(). What a shame.\n");
	}
	
	public FragmentAndReference build(BasicNode fullTreeRoot, Set<BasicNode> targetNodes, Map<BasicNode, String> magicNodes, boolean removeConj, Facet facet) throws TreeAndParentMapException, TreeFragmentBuilderException {
		//System.out.println("^^^^^^^ 1");
		Set<SimplePathInTree> paths = getAllTargetPaths(fullTreeRoot, targetNodes);
		//System.out.println("^^^^^^^ 2");
		Set<BasicNode> nodesInAllPaths = getAllNodesInPaths(paths);
		//System.out.println("^^^^^^^ 3");
		FragmentAndReference rootFragAndRef = createTreeFragmentByNodes(fullTreeRoot, nodesInAllPaths, targetNodes, magicNodes, removeConj, facet);
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
	
	private FragmentAndReference createTreeFragmentByNodes(BasicNode fullTreeRoot, Set<BasicNode> requiredNodes, Set<BasicNode> targetNodes, Map<BasicNode, String> magicNodes, boolean removeConj, Facet facet) throws TreeFragmentBuilderException {
		origToNew = new DualHashBidiMap<BasicNode, BasicNode>();
		BasicNode foundRoot = findNewTreeRoot(fullTreeRoot, requiredNodes);
		if (foundRoot != null) {
			BasicNode newRoot = createSubTree(foundRoot, requiredNodes, targetNodes, magicNodes, removeConj);
			copyAntecedents();
			if (!origToNew.keySet().equals(requiredNodes)) {
				//throw new TreeFragmentBuilderException("Generated tree nodes do not exactly match required nodes");
//				System.err.println(getClass().getSimpleName() + ": Generated tree nodes do not exactly match required nodes. origToNew.keySet()=" +
//						origToNew.keySet() + "  requiredNodes=%s" + requiredNodes);
				System.err.println(getClass().getSimpleName() + ": Generated tree nodes do not exactly match required nodes. origToNew.keySet()=" +
						origToNew.keySet().size() + "  requiredNodes=%s" + requiredNodes.size());
			}
			return new FragmentAndReference(newRoot, origToNew.getKey(newRoot), facet);
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
	private BasicNode createSubTree(BasicNode node, Set<BasicNode> requiredNodes, Set<BasicNode> targetNodes, Map<BasicNode, String> magicNodes, boolean removeConj) throws TreeFragmentBuilderException
	{
		// Handle "Magic Nodes"
		NodeInfo nodeInfo = node.getInfo().getNodeInfo();
		String lemma = nodeInfo.getWordLemma();
		if (magicNodes != null && magicNodes.keySet().contains(node)) {
			lemma = magicNodes.get(node);
		}
		
		NodeInfo newNodeInfo = new DefaultNodeInfo(nodeInfo.getWord(), lemma, nodeInfo.getSerial(), nodeInfo.getNamedEntityAnnotation(), nodeInfo.getSyntacticInfo());
		Info newInfo = new DefaultInfo(node.getInfo().getId(), newNodeInfo, node.getInfo().getEdgeInfo());
		BasicNode generatedNode = new BasicNode(newInfo);
		
		if (node.getChildren()!=null)
		{
			// Work on a copy!!! Don't change original children's list!!!
			List<BasicNode> children = new ArrayList<BasicNode>(node.getChildren());
			
			if (removeConj) {
				BasicNode conjChild = getSingleRequiredConjChild(node, requiredNodes);
				if (conjChild != null) {
					if (targetNodes.contains(node)) {
						//throw new TreeFragmentBuilderException("Cannot remove current node on account of conj child, since current node is a target node!");
						//System.err.printf("%s: Node '%s' is a target node, but was supposed to be removed due to a single required conj child '%s'. It will not be removed, but there is probably some wrong parse, as this shouldn't happen normally.\n",
						System.err.printf("%s: Node '%s' wont be conj-removed as it is a target node. Probably some parser error here.\n",
								this.getClass().getSimpleName(), InfoGetFields.getWord(node.getInfo()), InfoGetFields.getWord(conjChild.getInfo()));
					}
					else {
						BasicNode generatedConjChild = createSubTree(conjChild, requiredNodes, targetNodes, magicNodes, removeConj);
						
						Info upgradedChildInfo = new DefaultInfo(generatedConjChild.getInfo().getId(), generatedConjChild.getInfo().getNodeInfo(), generatedNode.getInfo().getEdgeInfo());
						BasicNode upgradedChild = new BasicNode(upgradedChildInfo);
						generatedNode = upgradedChild;
						children.remove(conjChild);
//						if (conjChild.getChildren() != null) {
//							children.addAll(conjChild.getChildren());
//						}
						requiredNodes.remove(node); //remove discarded from list, since later we verify on this list
						
						node = conjChild; //for origToNew
					}
				}
			}
			
			for (BasicNode child : children)
			{
				if (requiredNodes.contains(child)) {
					BasicNode generatedChild = createSubTree(child, requiredNodes, targetNodes, magicNodes, removeConj);
					generatedNode.addChild(generatedChild);
				}
			}
		}
		origToNew.put(node, generatedNode);
		return generatedNode;
	}
	
	private BasicNode getSingleRequiredConjChild(BasicNode node, Set<BasicNode> requiredNodes)  throws TreeFragmentBuilderException {
		Set<BasicNode> conjChildren = new HashSet<BasicNode>();
		for (BasicNode child : node.getChildren()) {
			if (requiredNodes.contains(child) &&
					child.getInfo().getEdgeInfo().getDependencyRelation().getStringRepresentation().equalsIgnoreCase("conj")) {
				conjChildren.add(child);
			}
		}
		
		if (conjChildren.isEmpty()) {
			return null;
		}
		if (conjChildren.size() == 1) {
			return conjChildren.iterator().next();
		}
		else {
			 //throw new TreeFragmentBuilderException("Got " + conjChildren.size() + " conj children for node of '" + node.getInfo().getNodeInfo().getWord() + "' - not supported!");
			 System.err.printf(getClass().getSimpleName() + ": Got " + conjChildren.size() + " conj children for node of '" + node.getInfo().getNodeInfo().getWord() + "' - not supported!\n");
			 return null;
		}
	}

//	private int getNumRequiredChildren(Set<BasicNode> requiredNodes, BasicNode node) {
//		Set<BasicNode> requiredNodesCopy = new HashSet<BasicNode>(requiredNodes);
//		requiredNodesCopy.retainAll(node.getChildren());
//		int numRequiredChildren = requiredNodesCopy.size();
//		return numRequiredChildren;
//	}

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
