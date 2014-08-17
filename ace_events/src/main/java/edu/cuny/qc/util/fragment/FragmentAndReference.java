package edu.cuny.qc.util.fragment;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

/**
 * Holds two nodes - a root to some tree fragment (as built by
 * {@link TreeFragmentBuilder}, and its corresponding node in
 * the original tree (that the fragment was constructed from).<BR>
 * 
 * This is required as nodes in a fragment are new nodes, unrelated
 * to ones in the original tree (they share the same
 * {@link eu.excitementproject.eop.common.representation.parse.representation.basic.Info},
 * but they are different objects).
 *  
 * @author Ofer Bronstein
 * @since August 2013
 */
public class FragmentAndReference {

	public FragmentAndReference(BasicNode fragmentRoot, BasicNode origReference, Facet facet) {
		this.fragmentRoot = fragmentRoot;
		this.origReference = origReference;
		this.facet = facet;
	}
	public BasicNode getFragmentRoot() {
		return fragmentRoot;
	}
	public BasicNode getOrigReference() {
		return origReference;
	}
	
	private BasicNode fragmentRoot;
	private BasicNode origReference;
	public Facet facet;
}
