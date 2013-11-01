package edu.cuny.qc.perceptron.graph;

import java.util.ArrayList;

/**
 * A node in dependency parsed graph, and represent a word and its attributes of this node
 * @author che
 *
 */
public class GraphNode implements java.io.Serializable, Cloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int index; 					// store the index of the word in the sentence
	public double weight; 				//the weight of this word (term)
	
	public ArrayList<GraphEdge> edges; 		//the edges of this Node (vertex)
	
	/**
	 * this constructor doesn't rely on Tree root, that's suitable for Javiers new distance supervision format
	 * the attribute of each node is: value, stem, entity_type, pos, generalized_pos
	 */
	public GraphNode(int idx)
	{
		index = idx;		
		weight = 1;
		edges = new ArrayList<GraphEdge>();
	}

	private GraphNode()
	{
		; // do nothing
	}
	
	public ArrayList<GraphEdge> getEdges()
	{
		if(edges == null)
		{
			edges = new ArrayList<GraphEdge>();
		}
		return edges;
	}
	
	/**
	 * make a copy of this object, copy everything expect edges
	 */
	public Object clone()
	{
		GraphNode new_node = new GraphNode();
		new_node.index = this.index;
		new_node.weight = this.weight;
		new_node.edges = null;
		return new_node;	
	}
	
	public String toString()
	{
		String ret = "" + index;
		return ret;
	}
}
