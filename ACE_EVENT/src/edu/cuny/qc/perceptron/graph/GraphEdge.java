package edu.cuny.qc.perceptron.graph;

import edu.stanford.nlp.trees.TypedDependency;

/**
 * This class maintain an edge between two nodes (words)
 * Theoretically, the edge is directed, from governor to dependent 
 * @author che
 *
 */
public class GraphEdge implements java.io.Serializable
{
	private static final long serialVersionUID = 6089242017106130166L;
	
	int governor;		// the index of a governor (or from) node 
	int dependent; 		// the index of a dependent (ro to) node
	String relation; 	//GrammaticalRelation relation.toString. the type of dependency
	
	public int getGovernor()
	{
		return governor;
	}
	
	public int getDependent()
	{
		return dependent;
	}
	
	public String getRelation()
	{
		return relation;
	}
	
	public GraphEdge()
	{
		governor = 0;
		dependent = 0;
		relation = "";
	}
	
	public GraphEdge(int gov, int dept, String relation)
	{
		this.governor = gov;
		this.dependent = dept;
		this.relation = relation;
	}
	
	public GraphEdge(TypedDependency td)
	{
		governor = td.gov().index() - 1;
		dependent = td.dep().index() - 1;
		relation = td.reln().toString();
	}
}
