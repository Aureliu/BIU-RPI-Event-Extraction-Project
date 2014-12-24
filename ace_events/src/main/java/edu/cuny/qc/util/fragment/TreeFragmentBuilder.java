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

import com.google.common.collect.BiMap;


import edu.cuny.qc.util.TreeToLineString;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;

public class TreeFragmentBuilder {
	static {
		System.err.printf("??? TreeFragmentBuilder: Silently passing on the weird situation where I have multpile conjs in my resuired nodes, and I don't know which to choose. So I don't choose any. It's in getSingleRequiredConjChild()\n");
		System.err.printf("??? TreeFragmentBuilder: I want to remove a node due to conj, but it's part of target nodes, so I pass silently. ()\n");
		System.err.printf("??? TreeFragmentBuilder: And also something in createTreeFragmentByNodes(). What a shame.\n");
	}
	
	public FragmentAndReference build(BasicNode fullTreeRoot, Set<BasicNode> targetNodes, BiMap<BasicNode, String> magicNodes, boolean removeConj, Facet facet) throws TreeAndParentMapException, TreeFragmentBuilderException {
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
	
	private FragmentAndReference createTreeFragmentByNodes(BasicNode fullTreeRoot, Set<BasicNode> requiredNodes, Set<BasicNode> targetNodes, BiMap<BasicNode, String> magicNodes, boolean removeConj, Facet facet) throws TreeFragmentBuilderException {
		origToNew = new DualHashBidiMap<BasicNode, BasicNode>();
		BasicNode foundRoot = findNewTreeRoot(fullTreeRoot, requiredNodes);
		if (foundRoot != null) {
			BasicNode newRoot = createSubTree(foundRoot, requiredNodes, targetNodes, magicNodes, removeConj);
			copyAntecedents();
			
			/// SilentErrors
			if (!origToNew.keySet().equals(requiredNodes)) {
				System.err.println(getClass().getSimpleName() + ": Generated tree nodes do not exactly match required nodes. origToNew.keySet()=" +
						origToNew.keySet().size() + "  requiredNodes=" + requiredNodes.size());
			}
			///
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
	
	/**
	 * ////// The Method of Your Dreams / Nightmares /////////
	 * <BR>
	 * <BR>
	 * This method created a <b>NEW</b> subtree, which preserves the structure of the roiginal tree, but contains
	 * only the required nodes (these are all the nodes that are between the targetNodes, inclusive).
	 * <BR>
	 * <BR>
	 * This method also handles <b>removeConj</b> -<BR>
	 * Let's take the sentence: <tt>John ate and drank.</tt>
	 * Let's take a situation where "ate" is the subroot, "John" is the [ARG] and "drank" is the [PRD].
	 * We want the returned subtree to have only "John" and "drank", simulating a representation where "ate" and
	 * "drank" are equal, and thus "ate" is irrelevant when "drank" is the [PRD].<BR>
	 * We do it simply y checking if the subroot has a conj child under the requiredNodes, and if so, just "upgrade"
	 * that conj child to be the new subroot. We maintain both the children of the old subroot, and of the new one.
	 * <BR>
	 * <BR>
	 * <b>NOTE</b> that this method is heuristic and not always correct. For instance, it would also process the
	 * sentence: <tt>John ate and Mary drank.</tt> This is because we cannot easily distinguish between these
	 * two cases, bases only on the tree. As an example, see the sentence: <tt>They'll defeat Bush and everybody
	 * will get rich.</tt> in <tt>CNN_CF_20030303.1900.06-1</tt>.<BR>
	 * Yes, maybe if we had just used stanford's appropriate level, this wouldn't have been an issue :)
	 *  
	 * @param subroot the subroot of the current tree we are working on. It could have a special role ([PRD]/[ARG]),
	 * or not. It could also eventually not be part of the output tree, if <tt>removeConj</tt> is applied.
	 * @param requiredNodes a set of all nodes that are on the path between [PRD] and [ARG], inclusive. Note that
	 * this set <b>can change</b>, when <tt>removeConj</tt> is applied.
	 * @param targetNodes just the two nodes - [ARG] and [PRD].
	 * @param magicNodes again, just the two nodes - [ARG] and [PRD], but this time - with these respective roles.
	 * @param removeConj should we remove conj?
	 * @return
	 * @throws TreeFragmentBuilderException
	 */
	private BasicNode createSubTree(BasicNode subroot, Set<BasicNode> requiredNodes, Set<BasicNode> targetNodes, BiMap<BasicNode, String> magicNodes, boolean removeConj) throws TreeFragmentBuilderException
	{
		// Handle "Magic Nodes"
		NodeInfo nodeInfo = subroot.getInfo().getNodeInfo();
		String lemma = nodeInfo.getWordLemma();
		if (magicNodes != null && magicNodes.keySet().contains(subroot)) {
			lemma = magicNodes.get(subroot);
		}
		
		NodeInfo newNodeInfo = new DefaultNodeInfo(nodeInfo.getWord(), lemma, nodeInfo.getSerial(), nodeInfo.getNamedEntityAnnotation(), nodeInfo.getSyntacticInfo());
		Info newInfo = new DefaultInfo(subroot.getInfo().getId(), newNodeInfo, subroot.getInfo().getEdgeInfo());
		BasicNode generatedSubroot = new BasicNode(newInfo);
		
		if (subroot.getChildren()!=null)
		{
			// Work on a copy!!! Don't change original children's list!!!
			List<BasicNode> children = new ArrayList<BasicNode>(subroot.getChildren());
			
			if (removeConj) {
				BasicNode conjChild = getSingleRequiredConjChild(subroot, requiredNodes, magicNodes);
				if (conjChild != null) {
					if (targetNodes.contains(subroot)) {
						/***
						 * There is nothing wrong with this scenario. It just means that the word we are inspecting right now
						 * as a predicate or an argument (== is in targetNodes), happens to be the root of the relevant
						 * subtree, and have a conj on the way to the other magic node.
						 * 
						 * For example, in document CNN_CF_20030303.1900.06-1, in the sentence:
						 * 		If the budget goes through as is, why don't
						 * 		Mr. Begala and Mr. Carville just donate the extra tax money they don't
						 * 		want?"
						 * we have the fragment:
						 * 		Mr. Begala and Mr. Carville
						 * and its treeout:
						 * 		nsubj->Begala/NP(nn->Mr./NP)(cc->and/CONJ)(conj->Carville/NP(nn->Mr./NP))
						 * 
						 * So when we come to check for <Begala[PRD], Mr.[ARG]> (referring to Carville's "Mr."), then Begala
						 * is the head of the subtree, is a predicate, and has its conj Carville on the way to the ARG
						 * (== it's in requiredNodes). so we will just have no conj removal.
						 * By the way, we'll notice that this is kinda of a meaningless path here - having
						 * <Begala[PRD], Mr.[ARG]>. But is seems that many paths here are meaningless, bu should still be 
						 * calculated.
						 */
						
						//throw new TreeFragmentBuilderException("Cannot remove current node on account of conj child, since current node is a target node!");
						/// SilentErrors
//						System.err.printf("%s: Node '%s' wont be conj-removed as it is a target node. Probably some parser error here.\n",
//								this.getClass().getSimpleName(), InfoGetFields.getWord(node.getInfo()), InfoGetFields.getWord(conjChild.getInfo()));
						///
					}
					else {
						BasicNode generatedConjChild = createSubTree(conjChild, requiredNodes, targetNodes, magicNodes, removeConj);
						
						Info upgradedChildInfo = new DefaultInfo(generatedConjChild.getInfo().getId(), generatedConjChild.getInfo().getNodeInfo(), generatedSubroot.getInfo().getEdgeInfo());
						BasicNode upgradedChild = new BasicNode(upgradedChildInfo);
						generatedSubroot = upgradedChild;
						children.remove(conjChild);
						/**
						 * For some reason, this "if" was commented out. This meant that if a magic node was anywhere in
						 * the subtree of the conj (and not the conj itself), then we won't have it in the final paths
						 * (well, I might not be super accurate about the scenario, but it definitely happened that a magic
						 * node wasn't in the final paths). This led to treeouts without [PRD] or [ARG] - not great!
						 * 
						 * The concern is, of course, that there was a reason that this was commented out, and that this
						 * could cause some problems. So let's, uh.... hope that it doesn't?
						 */
						if (conjChild.getChildren() != null) {
							children.addAll(conjChild.getChildren());
						}
						//////////////
						requiredNodes.remove(subroot); //remove discarded from list, since later we verify on this list
						
						subroot = conjChild; //for origToNew
					}
				}
			}
			
			for (BasicNode child : children)
			{
				if (requiredNodes.contains(child)) {
					BasicNode generatedChild = createSubTree(child, requiredNodes, targetNodes, magicNodes, removeConj);
					generatedSubroot.addChild(generatedChild);
				}
			}
		}
		origToNew.put(subroot, generatedSubroot);
		return generatedSubroot;
	}
	
	private BasicNode getSingleRequiredConjChild(BasicNode node, Set<BasicNode> requiredNodes, BiMap<BasicNode, String> magicNodes)  throws TreeFragmentBuilderException {
		Set<BasicNode> conjChildren = new HashSet<BasicNode>();
		for (BasicNode child : node.getChildren()) {
			if (requiredNodes.contains(child) &&
					child.getInfo().getEdgeInfo().getDependencyRelation().getStringRepresentation().equalsIgnoreCase("conj")) {
				conjChildren.add(child);
			}
		}
		/// DEBUG
//		int i=0;
//		for (BasicNode child : conjChildren) {
//			i++;
//			System.err.printf("Node %s/%s/%s/%s, conj child %s/%s: %s/%s/%s/%s, fragment: %s\n",
//					node.getInfo().getNodeInfo().getWord(),
//					node.getInfo().getNodeInfo().getWordLemma(),
//					node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag().toString(),
//					node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getStringRepresentation(),
//					i, 
//					conjChildren.size(),
//					child.getInfo().getNodeInfo().getWord(),
//					child.getInfo().getNodeInfo().getWordLemma(),
//					child.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag().toString(),
//					child.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getStringRepresentation(),
//					TreeToLineString.getStringWordRelCanonicalPos(node)
//					);
//		}
		////
		
		if (conjChildren.isEmpty()) {
			return null;
		}
		if (conjChildren.size() == 1) {
			return conjChildren.iterator().next();
		}
		else {
			/**
			 * Here comes the heuristic part!
			 * We get here when we have 2 conjChildren (both theoretically, and also - I checked).
			 * I think that it will be a waste to give up in this situation - might as well just pick one of them!
			 * 
			 * So our first priority to be by Canonical POS - if only one conj child shares canonical POS with node -
			 * then pick it! It just makes most sense. You probably won'y have an actual interchangeable noun with
			 * a verb. But two nouns or two verbs - be my guest. 
			 * 
			 * Second priority - we will prefer the ones that is a [PRD], because it makes more sense -
			 * If "John and Mary ate and drank", with (node=="ate", [ARG]=Mary, [PRD]=drank), we should upgrade "drank"
			 * (meaning "Mary drank"). I'm not sure what happens if we pick John. I'm not even sure that it's a good
			 * example, as "John" is not in requiredNodes.... whatever.
			 * 
			 * Anyway, if none is [PRD], might as well just return any of them and at least get rid of one conj.
			 * I think. :)
			 * 
			 * Oh, and this totally breaks the interface of this class, which should not be aware of [PRD] and [ARG].
			 * I could care less.
			 */
			/// SilentErrors
			//System.err.printf(getClass().getSimpleName() + ": Got " + conjChildren.size() + " conj children for node of '" + node.getInfo().getNodeInfo().getWord() + "' - not supported!\n");
			///
			Set<BasicNode> conjChildrenSamePos = new HashSet<BasicNode>();
			CanonicalPosTag nodePos = node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag();
			for (BasicNode child : conjChildren) {
				if (child.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag().equals(nodePos)) {
					conjChildrenSamePos.add(child);
				}
			}
			if (conjChildrenSamePos.size() == 1) {
				return conjChildrenSamePos.iterator().next();
			}
			
			BasicNode prd = magicNodes.inverse().get("[PRD]");
			if (prd != null && conjChildren.contains(prd)) {
				return prd;
			}
			else {
				// Just return any of them
				return conjChildren.iterator().next();
			}
			 //return null;
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
