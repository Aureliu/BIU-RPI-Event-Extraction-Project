package edu.cuny.qc.perceptron.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import edu.stanford.nlp.trees.TypedDependency;

/**
 * This class maintain a data structure of Graph represents dependency parsed tree
 * @author che
 *
 */
public class DependencyGraph 
{
	static final String TYPE_EXTENSION_FORWORD = "->";
 	static final String TYPE_EXTENSION_BACKWORD = "<-";
	
 	int root;	// the root of the dependency graph
	Vector<GraphNode> vertices; 	//the graph vertices, the first position is null, real index starts from 1
	boolean directed = false;
	
	public Vector<GraphNode> getVertices()
	{
		if (vertices == null)
		{
			vertices = new Vector<GraphNode>();
		}
		return vertices;
	}
	
	public DependencyGraph(Collection<TypedDependency> tdl, int length)
	{
		//initialize vertices, plz note that index starts from 1
		getVertices().setSize(length);
		for(int i=0; i<length; i++)
		{
			GraphNode node = new GraphNode(i);
			getVertices().set(i, node);
		}
		
		//add dependency as edges into graph
		for(Iterator<TypedDependency> iter = tdl.iterator(); iter.hasNext();) 	//Traverse the dependency list
		{
			TypedDependency td = iter.next();
			
			if(td.gov().index() <= 0 || td.dep().index() <= 0)
			{
				// this is the root of the dependency graph
				root = td.gov().index() > 0 ? (td.gov().index() - 1) : (td.dep().index() - 1);
				continue;
			}
			
			GraphEdge edge = new GraphEdge(td);		//One dependency corresponds to one edge in graph(undirected way)
			
			int idx = td.gov().index() - 1;			//idx of governor of this dependency
			if(idx > -1)
			{
				GraphNode vertex = getVertices().get(idx);
				vertex.getEdges().add(edge);
			}
			idx = td.dep().index() - 1;				//idx of dependent of this dependency
			if(idx > -1)
			{
				GraphNode vertex = getVertices().get(idx);
				vertex.getEdges().add(edge);
			}
		}
	}
	
	/**
	 * default constructor
	 */
	public DependencyGraph()
	{
		; // do nothing 
	}
	
	/**
	 * get the shortest path from arg1 to args2
	 * @return
	 */
	public Vector<Integer> getShortestPath(Vector<Integer> arg1, Vector<Integer> arg2)
	{
		int max_weight = -1;
		int min_len = Integer.MAX_VALUE;
		Vector<Integer> ret = null;
		for(int i=0; i<arg1.size(); i++)
		{
			//among all shortest path, get the one with largest weight
			//i.e., the one has most occurrence of anchor values
			int idx = arg1.get(i);
			Vector<Integer> path = new Vector<Integer>();
			getShortestPath(idx, arg2, path);
			int len = path.size();
			if(len <= 0)
			{
				continue;
			}
			
			int weight = path.size();
			
			if(weight > max_weight)
			{
				ret = path;
				max_weight = weight;
				min_len = len;
			}
			else if(weight == max_weight)
			{
				if(len < min_len)
				{
					ret = path;
					max_weight = weight;
					min_len = len;
				}
			}
		}
		return ret;
	} 
	
	public Vector<PathTerm> getShortestPathFeatured(int arg1, int arg2)
	{
		Vector<Integer> vec1 = new Vector<Integer>();
		vec1.add(arg1);
		Vector<Integer> vec2 = new Vector<Integer>();
		vec2.add(arg2);
		return getShortestPathFeatured(vec1, vec2);
	}
	
	/**
	 * get the shortest path from arg1 to args2
	 * @return
	 */
	public Vector<PathTerm> getShortestPathFeatured(Vector<Integer> arg1, Vector<Integer> arg2)
	{
		int min_len = Integer.MAX_VALUE;
		Vector<Integer> ret = null;
		for(int i=0; i<arg1.size(); i++)
		{
			//among all shortest path, get the one with largest weight
			//i.e., the one has most occurrence of anchor values
			int idx = arg1.get(i);
			Vector<Integer> path = new Vector<Integer>();
			getShortestPath(idx, arg2, path);
			int len = path.size();
			if(len <= 0)
			{
				continue;
			}
			
			if(len < min_len)
			{
				ret = path;
				min_len = len;
			}
		}
		Vector<PathTerm> path = new Vector<PathTerm>();
		fillPath(ret, path, this);
		return path;
	}
	
	/**
	 * Search the shortest path from start index to end index
	 * @param start
	 * @param end
	 * @return
	 */
	private double getShortestPath(int start, Vector<Integer> end, Vector<Integer> shortestPath)
	{
		ArrayList<Integer> optimal = new ArrayList<Integer>();	//Store the optimal nodes during searching
		ArrayList<Integer> source = new ArrayList<Integer>();
		Double distance[] = new Double[getVertices().size()];	//Store the distance during searching
		int pre[] = new int[getVertices().size()];				//Store the optimal pre-node during searching
		
		for(int i=0; i<distance.length; i++)					//initialize
		{
			source.add(i);
			distance[i] = Double.MAX_VALUE;						//MAX_VALUE stands for infinite
			pre[i] = -1;							
		}
		distance[start] = 0.0d;
		
		while(true)
		{
			double min = Double.MAX_VALUE;
			int min_idx = 0;
			boolean hasPath = false;							//if all remaining distances are MAX_VALUE, that would be false
			for(int i=0; i<source.size(); i++)
			{
				int idx = source.get(i);
				if(distance[idx] < min)
				{
					hasPath = true;					
					min = distance[idx];
					min_idx = idx;
				}
			}
			
			if(hasPath == false)					// the remaining are all infinite (MAX_VALUE)
			{
				break;
			}
			
			optimal.add(min_idx); 					// get one optimal vertex in this iteration
			source.remove(new Integer(min_idx));	// remove the optimal one in source
			if(optimal.containsAll(end))			// optimal paths to all end points are found
			{
				break;
			}
				
			//update distance using new optimal one
			for(GraphEdge edge : getVertices().get(min_idx).edges)
			{
				int another_node = 0;
				if(min_idx == edge.dependent)
				{
					another_node = edge.governor;
				}
				else
				{
					another_node = edge.dependent;
				}
				double cost = getVertices().get(another_node).weight;
				
				if(distance[another_node] > distance[min_idx] + cost)
				{
					distance[another_node] = distance[min_idx] + cost;
					pre[another_node] = min_idx;
				}
			}
		} //end while
		
		//Go back to build shortest path
		Double min_end_distance = Double.MAX_VALUE;			//find the nearest point in the scope of end points
		int end_point = -1;
		for(int i=0; i<end.size(); i++)
		{
			int temp = end.get(i);
			if(distance[temp] < min_end_distance)
			{
				min_end_distance = distance[temp];
				end_point = temp;
			}
		}
		
		int path_node = end_point;							//go back to build the shortest path
		while(path_node != -1)
		{
			shortestPath.add(path_node);
			path_node = pre[path_node];
		}
		
		return min_end_distance;
	}
	
	/**
	 * print this graph, for test purpose
	 */
	public String toString()
	{
		String ret = "";
		for(GraphNode vertex : getVertices())
		{
			if(vertex == null)
			{
				continue;
			}
			ret += (vertex.index +"-->");	//the vertex
			for(GraphEdge edge : vertex.edges)
			{
				int dep = edge.dependent;
				String relation = edge.relation;
				if(dep != vertex.index)
				{
					ret += relation + "("+ vertices.get(dep).index + ")" + "-->";
				}
			}
			ret += "\n";
		}
		return ret;
	}

	/**
 	 * fill the content into path with indice
 	 * in order to remove objects that we will not to use again (e.g. graph object) 
 	 * path should not contain reference to members of graph object, instead, make new objects for vertex in path
 	 * @param vec
 	 * @param path
 	 * @param graph
	 * @return 
 	 */
 	static protected void fillPath(Vector<Integer> vec, Vector<PathTerm> path, DependencyGraph graph)
 	{
 		if(vec == null || vec.size() == 0)
 		{
 			return;
 		}
 		
 		int first = vec.get(vec.size() - 1 );
 		GraphNode node_first = graph.getVertices().get(first);
 		PathTerm term = new PathTerm();
			term.isVertex = true;
			term.vertex = (GraphNode) node_first.clone();
			path.add(term);
			
 		GraphNode node_second;
 		for(int i=vec.size()-2; i >= 0; i--)
 		{
 			int second = vec.get(i);
 			node_second = graph.getVertices().get(second);
 			
 			ArrayList<GraphEdge> edges = node_first.edges;
 			String type = "";
 			for(int j=0; j<edges.size(); j++)
 			{
 				GraphEdge edge = edges.get(j);
 				if(edge.dependent == second)
 				{
 					type = edge.relation + TYPE_EXTENSION_FORWORD;
 					break;
 				}
 				else if(edge.governor == second)
 				{
 					type = edge.relation + TYPE_EXTENSION_BACKWORD;
 					break;
 				}
 			}
 			
 			term = new PathTerm();
 			term.isVertex = false;
 			term.edgeType = type;
 			path.add(term);
 			
 			term = new PathTerm();
 			term.isVertex = true;
 			term.vertex = (GraphNode) node_second.clone();
 			path.add(term);
 			
 			node_first = node_second;
 			first = second;
 		}
 	}
 	
 

	 /**
	  * this class maintain a term in Shortest Path
	  * the term can be a vertex of Dependency Graph, or an edge-type (String, by now) of Dependency Graph
	  * @author che
	  *
	  */
	 public static class PathTerm implements java.io.Serializable
	 {
		private static final long serialVersionUID = -4617467949244923885L;
		
		public boolean isVertex;		//true if it's a vertex, false if it's an edge type
	 	public GraphNode vertex; 
	 	public String edgeType;			//the edge-type, i.e. the type of dependency, directed
		
	 	/**
	 	 * print the content, for DEBUG purpose
	 	 */
	 	public String toString() {
	 		String ret = "";
			if(isVertex)
			{
				ret = vertex.toString();
			}
			else
			{
				ret = "-" + edgeType;
			}
			return ret;
		}
	 }

}

