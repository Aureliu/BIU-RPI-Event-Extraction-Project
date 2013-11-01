package edu.cuny.qc.perceptron.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * this encodes feature in perceptron
 * in general, the feature can be an arbitrary structure with text feature and assignment 
 * @author che
 *
 */
public class Feature implements Serializable
{
	private static final long serialVersionUID = -4513605947358371105L;

	String featureType; 
	
	/*
	 *  node represents feature for trigger label
	 */
	List<FeatureNode> nodes;
	/*
	 *  edge represents edge between nodes, can include label for a (trigger/argument) label
	 */
	List<FeatureEdge> edges;

	/**
	 * default constructor
	 */
	public Feature(String featureType)
	{
		this.featureType = featureType;
	}
	
	public Feature(FeatureNode node, String featureType)
	{
		this(featureType);
		if(node != null)
		{
			nodes = new ArrayList<FeatureNode>();
			nodes.add(node);
		}
	}
	
	public Feature(List<FeatureNode> nodes, List<FeatureEdge> edges, String featureType)
	{
		this(featureType);
		this.nodes = nodes;
		this.edges = edges;
	}
	
	/**
	 * the node in feature structure
	 * @author che
	 *
	 */
	public static class FeatureNode implements Serializable 
	{
		private static final long serialVersionUID = 4352657788006093146L;

		public FeatureNode(String text, String label)
		{
			this.text = text;
			this.label = label;
		}
		
		// the node text feature
		public String text;
		// the node label 
		public String label;
		
		public String toString()
		{
			return "edge feature text:"+text + ";" + "label:" + label;
		}
		
		public boolean equals(Object obj)
		{
			if(obj == null || !(obj instanceof FeatureNode))
			{
				return false;
			}
			FeatureNode node = (FeatureNode) obj;
			return new EqualsBuilder().append(text, node.text).append(label, node.label).isEquals();
		}
		
		public int hashCode()
		{
			return new HashCodeBuilder(17, 37).append(text).append(label).toHashCode();
		}
	}
	
	/**
	 * the edge of feature structure
	 * may include text and label for the trigger-argument link
	 * @author che
	 *
	 */
	public static class FeatureEdge implements Serializable
	{
		private static final long serialVersionUID = -5101249489003633645L;
		
		public String text;
		public String label;
		
		int from;
		int to;
		
		public FeatureEdge(String text, String label, int from, int to)
		{
			this.text = text;
			this.label = label;
			this.from = from;
			this.to = to;
		}
		
		public String toString()
		{
			return "Edge feature: text:"+text + ";" + "label:" + label;
		}
		
		public boolean equals(Object obj)
		{
			if(obj == null || !(obj instanceof FeatureEdge))
			{
				return false;
			}
			FeatureEdge node = (FeatureEdge) obj;
			return new EqualsBuilder().append(text, node.text).append(label, node.label)
				.append(from, node.from).append(to, node.to).isEquals();
		}
		
		public int hashCode()
		{
			return new HashCodeBuilder(17, 37).append(text).append(label).append(from).append(to).toHashCode();
		}
	}
		
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof Feature))
		{
			return false;
		}
		Feature feat = (Feature) obj;
		return new EqualsBuilder().append(featureType, feat.featureType).append(nodes, feat.nodes).append(edges, feat.edges).isEquals();
	}
	
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(featureType).append(nodes).append(edges).toHashCode();
	}
	
	public String toString()
	{
		String ret = featureType + "\n";
		if(nodes != null)
		{
			for(FeatureNode node : nodes)
			{
				ret += node.toString() + "\n";
			}
		}
		if(edges != null)
		{
			for(FeatureEdge edge : edges)
			{
				ret += "" + edge.from + " " + edge.to + " " + edge.toString();		
			}
		}
		return ret;
	}
}
