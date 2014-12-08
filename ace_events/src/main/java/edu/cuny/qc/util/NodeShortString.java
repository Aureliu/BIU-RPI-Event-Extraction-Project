package edu.cuny.qc.util;

import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

public abstract class NodeShortString {
	private static final String ROOT_STR = "<ROOT>";
	public abstract <I extends Info> String toString(AbstractNode<? extends I, ?> node);
	
	/**
	 * how was this list compiled?
	 * - using Stanford's definitions of rels (which is a tree)
	 * - all comp's are together, except for objects, which seem a more prominent class of their own
	 * - all obj's are together (even though syntactically probably dobj and pobj can't appear in the same structure)
	 * - all mod's are together, except for prep, which seems like a prominent class of its own, plus we still want to allow specific prep_* (like prep_of)
	 */
	private static Map<String, String> FLAT_TREE_MAPPING = Maps.newHashMap();
	static {
		// comp
		FLAT_TREE_MAPPING.put("acomp", "comp");
		FLAT_TREE_MAPPING.put("attr", "comp");
		FLAT_TREE_MAPPING.put("ccomp", "comp");
		FLAT_TREE_MAPPING.put("xcomp", "comp");
		FLAT_TREE_MAPPING.put("complm", "comp");
		FLAT_TREE_MAPPING.put("mark", "comp");
		FLAT_TREE_MAPPING.put("rel", "comp");
		
		// obj
		FLAT_TREE_MAPPING.put("dobj", "obj");
		FLAT_TREE_MAPPING.put("iobj", "obj");
		FLAT_TREE_MAPPING.put("pobj", "obj");
		
		// mod
		FLAT_TREE_MAPPING.put("abbrev", "mod");
		FLAT_TREE_MAPPING.put("amod", "mod");
		FLAT_TREE_MAPPING.put("appos", "mod");
		FLAT_TREE_MAPPING.put("advcl", "mod");
		FLAT_TREE_MAPPING.put("purpcl", "mod");
		FLAT_TREE_MAPPING.put("det", "mod");
		FLAT_TREE_MAPPING.put("predet", "mod");
		FLAT_TREE_MAPPING.put("preconj", "mod");
		FLAT_TREE_MAPPING.put("infmod", "mod");
		FLAT_TREE_MAPPING.put("mwe", "mod");
		FLAT_TREE_MAPPING.put("partmod", "mod");
		FLAT_TREE_MAPPING.put("advmod", "mod");
		FLAT_TREE_MAPPING.put("neg", "mod");
		FLAT_TREE_MAPPING.put("rcmod", "mod");
		FLAT_TREE_MAPPING.put("quantmod", "mod");
		FLAT_TREE_MAPPING.put("nn", "mod");
		FLAT_TREE_MAPPING.put("npadvmod", "mod");
		FLAT_TREE_MAPPING.put("tmod", "mod");
		FLAT_TREE_MAPPING.put("num", "mod");
		FLAT_TREE_MAPPING.put("number", "mod");
		FLAT_TREE_MAPPING.put("poss", "mod");
		FLAT_TREE_MAPPING.put("possessive", "mod");
		FLAT_TREE_MAPPING.put("prt", "mod");
	}
	
	public static <I extends Info> String prepConcrete(AbstractNode<? extends I, ?> node) {
		if (node.getInfo().getEdgeInfo().getDependencyRelation() != null &&
				node.getInfo().getEdgeInfo().getDependencyRelation().getStringRepresentation().equals("prep")) {
			return "_" + node.getInfo().getNodeInfo().getWordLemma();
		}
		else {
			return "";
		}
	}
	
	public static <I extends Info> String flatRel(AbstractNode<? extends I, ?> node) {
		String rel = InfoGetFields.getRelation(node.getInfo(), ROOT_STR);
		String flatRel = FLAT_TREE_MAPPING.get(rel);
		if (flatRel != null) {
			return flatRel;
		}
		else {
			return rel;
		}
	}	
	
	//// Concrete Classes ////////////////////////////////////////
	
	public static class Rel extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR);
		}
	}
	
	public static class RelFlat extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return flatRel(node);
		}
	}
	
	public static class RelPrep extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR)+prepConcrete(node);
		}
	}
	
	public static class RelFlatPrep extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return flatRel(node)+prepConcrete(node);
		}
	}
	
	public static class RelPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR)+"->"+InfoGetFields.getPartOfSpeech(node.getInfo());
		}
	}
	
	public static class RelFlatPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return flatRel(node)+"->"+InfoGetFields.getPartOfSpeech(node.getInfo());
		}
	}
	
	public static class RelPrepPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR)+prepConcrete(node)+"->"+InfoGetFields.getPartOfSpeech(node.getInfo());
		}
	}
	
	public static class RelFlatPrepPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return flatRel(node)+prepConcrete(node)+"->"+InfoGetFields.getPartOfSpeech(node.getInfo());
		}
	}
	
	public static class RelCanonicalPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR)+"->"+node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag();
		}
	}
	
	public static class RelFlatCanonicalPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return flatRel(node)+"->"+node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag();
		}
	}
	
	public static class RelPrepCanonicalPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR)+prepConcrete(node)+"->"+node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag();
		}
	}
	
	public static class RelFlatPrepCanonicalPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return flatRel(node)+prepConcrete(node)+"->"+node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag();
		}
	}
	
	public static class WordRel extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR)+"->"+InfoGetFields.getWord(node.getInfo());
		}
	}
	
	public static class WordRelPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR)+"->"+InfoGetFields.getWord(node.getInfo())+"/"+InfoGetFields.getPartOfSpeech(node.getInfo());
		}
	}
	
	public static class WordRelCanonicalPos extends NodeShortString {
		@Override
		public <I extends Info> String toString(AbstractNode<? extends I, ?> node) {
			return InfoGetFields.getRelation(node.getInfo(), ROOT_STR)+"->"+InfoGetFields.getWord(node.getInfo())+"/"+node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag();
		}
	}
}
