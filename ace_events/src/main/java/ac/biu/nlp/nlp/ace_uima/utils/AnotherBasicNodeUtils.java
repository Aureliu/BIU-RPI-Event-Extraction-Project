package ac.biu.nlp.nlp.ace_uima.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections15.MultiMap;
import org.apache.uima.jcas.tcas.Annotation;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

public class AnotherBasicNodeUtils {
	public static String getNodeString(BasicNode node) {
		if (node==null) {
			return "(null)<>";
		}
		return String.format("%s<%s>", node.getInfo().getId(), node.hashCode());
	}
	
	public static String getAnnotationString(Annotation anno) {
		if (anno==null) {
			return "(null)[]";
		}
		return String.format("[%s:%s]", anno.getBegin(), anno.getEnd());
	}
	
	public static String getNodesString(Map<BasicNode, BasicNode> map) {
		StringBuffer buffer = new StringBuffer("(" + map.size() + "){");
		for (Entry<BasicNode, BasicNode> entry : map.entrySet()) {
			buffer.append(String.format("%s:%s, ", getNodeString(entry.getKey()), getNodeString(entry.getValue())));
		}
		buffer.append("}");
		return buffer.toString();
	}
	
	public static <S extends Annotation> String getNodesAnnotationString(Map<S, BasicNode> map) {
		StringBuffer buffer = new StringBuffer("(" + map.size() + "){");
		for (Entry<S, BasicNode> entry : map.entrySet()) {
			buffer.append(String.format("%s:%s, ", getAnnotationString(entry.getKey()), getNodeString(entry.getValue())));
		}
		buffer.append("}");
		return buffer.toString();
	}
	
	public static <S extends Annotation> String getNodesAnnotationString(MultiMap<S, BasicNode> map) {
		StringBuffer buffer = new StringBuffer("(" + map.size() + "){");
		for (Entry<S, Collection<BasicNode>> entry : map.entrySet()) {
			buffer.append(String.format("%s:%s, ", getAnnotationString(entry.getKey()), getNodesString(entry.getValue())));
		}
		buffer.append("}");
		return buffer.toString();
	}
	
	public static String getNodesString(Collection<BasicNode> nodes) {
		StringBuffer buffer = new StringBuffer("(" + nodes.size() + ")[");
		for (BasicNode node : nodes) {
			buffer.append(String.format("%s, ", getNodeString(node)));
		}
		buffer.append("]");
		return buffer.toString();
	}

}
