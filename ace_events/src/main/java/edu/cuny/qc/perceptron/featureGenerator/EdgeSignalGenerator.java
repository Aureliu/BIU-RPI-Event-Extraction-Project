package edu.cuny.qc.perceptron.featureGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;

import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceEventArgumentValue;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.ace.acetypes.AceTimexMention;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.graph.DependencyGraph;
import edu.cuny.qc.perceptron.graph.GraphEdge;
import edu.cuny.qc.perceptron.graph.GraphNode;
import edu.cuny.qc.perceptron.graph.DependencyGraph.PathTerm;
import edu.cuny.qc.perceptron.similarity_scorer.SignalMechanism;
import edu.cuny.qc.perceptron.similarity_scorer.SignalMechanismException;
import edu.cuny.qc.perceptron.types.SignalInstance;
import edu.cuny.qc.perceptron.types.SignalType;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.Sentence.Sent_Attribute;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.util.Span;
import edu.cuny.qc.util.TokenAnnotations;
import edu.stanford.nlp.trees.Tree;

/**
 * This is feature generator for edges (a.k.a. link between trigger and argument)
 * @author che
 *
 */
public class EdgeSignalGenerator
{
	public static Map<String, Map<String, Map<String, SignalInstance>>> get_edge_text_signals(SentenceInstance sent, int i, AceMention mention, Perceptron perceptron)
	{
		try {
			Map<String, Map<String, Map<String, SignalInstance>>> ret = new LinkedHashMap<String, Map<String, Map<String, SignalInstance>>>();
			
			LinkedHashMap<String, BigDecimal> scoredSignals;
			for (JCas spec : sent.types.specs) {
				Map<String, Map<String, SignalInstance>> specSignals = new LinkedHashMap<String, Map<String, SignalInstance>>();
				String label = SpecAnnotator.getSpecLabel(spec);
				ret.put(label, specSignals);
				
				for (Argument argument : SpecAnnotator.getSpecArguments(spec)) {
					Map<String, SignalInstance> roleSignals = new LinkedHashMap<String, SignalInstance>();
					String role = argument.getRole().getCoveredText();
					specSignals.put(role, roleSignals);
	
					for (SignalMechanism mechanism : perceptron.signalMechanisms) {
						scoredSignals = mechanism.scoreArgument(spec, argument, sent, i, mention);
						for (Entry<String, BigDecimal> scoredSignal : scoredSignals.entrySet()) {
							SignalInstance signal = new SignalInstance(scoredSignal.getKey(), SignalType.ARGUMENT, scoredSignal.getValue());
							roleSignals.put(signal.name, signal);
							perceptron.argumentSignalNames.add(signal.name);
						}
					}
				}
			}
			
			return ret;
		} catch (CASException e) {
			throw new RuntimeException(e);
		} catch (SignalMechanismException e) {
			throw new RuntimeException(e);
		}
//		List<Map<Class<?>, Object>> tokens = (List<Map<Class<?>, Object>>) sent.get(SentenceInstance.InstanceAnnotations.Token_FEATURE_MAPs);
//		Map<Class<?>, Object> token_trigger = tokens.get(i);
//		Vector<Integer> headIndices = mention.getHeadIndices();
//		Vector<Integer> extentIndices = mention.getExtentIndices();
//		List<String> featureLine = new ArrayList<String>();
//		
//		String feature = "";
//		// trigger
//		String trigger = (String) token_trigger.get(TokenAnnotations.LemmaAnnotation.class);
//		feature = "Trigger=" + trigger;
//		featureLine.add(feature); 
//		
//		// type of argument entity mention
//		String type = mention.getType();
//		feature = "EntityType=" + type; 
//		featureLine.add(feature);
//		
//		// subtype of entity
//		if(mention instanceof AceEntityMention)
//		{
//			String subtype = ((AceEntityMention)mention).getSubType();
//			feature = "EntitySubType=" + subtype; 
//			featureLine.add(feature);
//		}
//		
//		// type of GPE role
//		if(mention instanceof AceEntityMention)
//		{
//			AceEntityMention entityMention = (AceEntityMention) mention;
//			String role = entityMention.getRole();
//			if(role != null && !role.equals(""))
//			{
//				feature = "EntityGEPRole=" + role; 
//				featureLine.add(feature);
//			}
//		}
//		
//		// head of entity/timex mention
//		if(mention instanceof AceEntityMention || mention instanceof AceTimexMention)
//		{
//			for(Integer index : headIndices)
//			{
//				// head can be more than one word
//				String head = (String) tokens.get(index).get(TokenAnnotations.LemmaAnnotation.class);
//				feature = "Head=" + head;
//				featureLine.add(feature);
//			}
//		}
//		
//		// NOTE: ignore Heads for Values
////		else // value mention
////		{
////			int index = headIndices.get(0);
////			String head = (String) tokens.get(index).get(TokenAnnotations.TextAnnotation.class);
////			feature = "Head=" + head;
////			featureLine.add(feature);
////		}
//		
//		// head of NAM or NOM mentions for the entity
//		if(mention instanceof AceEntityMention)
//		{
//			AceEntityMention NAMMention = ((AceEntityMention) mention).getNAMMention();
//			if(NAMMention != mention)
//			{
//				String[] heads = NAMMention.getHeadText().split("\\s");
//				for(String head : heads)
//				{
//					feature = "NAMHead=" + head.toLowerCase();
//					featureLine.add(feature);
//				}
//			}
//		}
//		
//		// check if the current entity mention is a modifier for other entity
//		// e.g. US Chinese, and Russia diplomats. US/Chinese/Russia are modifiers of diplomats
//		boolean isModifierEntity = isModifierOfOtherEntity(sent, mention);
//		if(isModifierEntity)
//		{
//			feature = "isModifierEntity=" + true;
//			featureLine.add(feature);
//		}
//		
//		// neighbor words
//		feature = getNeighborWords(tokens, i, mention, "EntityW-1", new int[]{-1});
//		if(feature != null)
//			featureLine.add(feature);
//		feature = getNeighborWords(tokens, i, mention, "EntityW-2", new int[]{-2});
//		if(feature != null)
//			featureLine.add(feature);
//		feature = getNeighborWords(tokens, i, mention, "EntityW-2W-1", new int[]{-2,-1});
//		if(feature != null)
//			featureLine.add(feature);
//		feature = getNeighborWords(tokens, i, mention, "EntityW1", new int[]{1});
//		if(feature != null)
//			featureLine.add(feature);
//		feature = getNeighborWords(tokens, i, mention, "EntityW2", new int[]{2});
//		if(feature != null)
//			featureLine.add(feature);
//		feature = getNeighborWords(tokens, i, mention, "EntityW1W2", new int[]{1,2});
//		if(feature != null)
//			featureLine.add(feature);
//		
//		// relative position of the entity, before or after the trigger
//		String position = "";
//		if(headIndices.get(0) > i)
//		{
//			position = "AfterTrigger";
//		}
//		else if(headIndices.get(headIndices.size() - 1) < i)
//		{
//			position = "BeforeTrigger";
//		}
//		featureLine.add(position);
//		
//		// the entity is the nearest entity
//		String is_nearest = isNearestEntity(sent, mention, i);
//		if(is_nearest != null)
//		{
//			feature = "nearest=" + is_nearest;
//			featureLine.add(feature);
//		}
//		
//		// dependencies of the entity
//		for(Integer index : headIndices)
//		{
//			Map<Class<?>, Object> token_head = tokens.get(index);
//			Vector<String> dep_features = (Vector<String>) token_head.get(TokenAnnotations.DependencyAnnotation.class);
//			if(dep_features != null)
//			{
//				featureLine.addAll(dep_features);
//			}
//		}
//		
//		// dependency paths between entity and trigger
//		List<String> paths = getDependencyPaths(sent, headIndices, i);
//		feature = "";
//		if(paths != null && paths.size() != 0)
//		{
//			for(String path : paths)
//			{
//				int distance = (path.split("#").length + 1) / 2;
//				// skip very long dependency path
//				if(distance <= 5)
//				{
//					feature = "Path=" + path;
//					featureLine.add(feature);
//				}
//			}
//		}
//		
//		int depDistance = Integer.MAX_VALUE;
//		// distance between entity and trigger in dependency tree (graph)
//		if(paths != null && paths.size() > 0)
//		{
//			depDistance = (paths.get(0).split("#").length + 1) / 2;
//			if(depDistance > 4)
//			{
//				feature = "DepDistance>4";
//			}
//			else
//			{
//				feature = "DepDistance=" + depDistance;
//			}
//			featureLine.add(feature);
//		}
//		else if(paths == null)
//		{
//			feature = "DepDistance=" + "INF";
//			featureLine.add(feature);
//		}
//		
//		// to do: get shortest dependency path by coreference
////		List<String> paths_corf = getDependencyPathsCoref(sent, mention, i);
////		feature = "";
////		if(paths_corf != null && paths_corf.size() != 0)
////		{
////			for(String path : paths_corf)
////			{
////				int distance = (path.split("#").length + 1) / 2;
////				if(distance < depDistance && distance <= 3)
////				{
////					feature = "CorfPath=" + path;
////					featureLine.add(feature);
////				}
////			}
////		}
//		
//		// surface distance between trigger and entity
//		int surf_distance = getSurfaceDistanceLog(extentIndices, i);
//		feature = "SurfDistance=" + surf_distance;
//		featureLine.add(feature);
//		
//		// whether they (entity and trigger) are in the same clause (according to parsing tree)
//		Boolean sameClause = isSameClause(headIndices, tokens, i);
//		if(sameClause != null)
//		{
//			feature = "sameClause=" + sameClause;
//			featureLine.add(feature);
//		}
//		
//		// check this feature only when the mention and trigger is in the same clause
//		if(sameClause != null && sameClause == true)
//		{
//			String isOnlyOneEntity = isOnlyOneEntity(sent, mention);
//			if(isOnlyOneEntity != null)
//			{
//				feature = "onlyMentionOf=" + is_nearest;
//				featureLine.add(feature);
//			}
//		}
//		
//		// whether the scope of the entity covers the trigger
//		if(extentIndices.contains(i))
//		{
//			feature = "overlap=true";
//			featureLine.add(feature);
//		}
//		else
//		{
//			feature = "overlap=false";
//			featureLine.add(feature);
//		}
//		
//		// check whether Entity and Trigger are separated by a puncuation
//		boolean separate = isSeparatedBypunctuation(tokens, headIndices, i);
//		feature = "separate=" + separate;
//		featureLine.add(feature);
//		
//		// the common root of the entity and trigger in the parse tree
//		List<List<Tree>> pathsInParse = new ArrayList<List<Tree>>();
//		Tree tree = (Tree) sent.get(InstanceAnnotations.ParseTree);
//		Tree commonRoot = getCommonRootInParse(tree, headIndices, i, pathsInParse);
//		if(commonRoot != null)
//		{
//			feature = "commonRoot=" + commonRoot.value();
//			featureLine.add(feature);
//		}
//		
//		// the depth of the common root to the trigger node
//		if(commonRoot != null)
//		{
//			Tree triggerNodeInParse = tree.getLeaves().get(i);
//			int commonRootDepth = commonRoot.depth(triggerNodeInParse);
//			if(commonRootDepth < 5)
//			{
//				feature = "commonRootDepth=" + commonRootDepth;
//				featureLine.add(feature);
//			}
//		}
//			
//		// the Path from Trigger to Entity
//		String pathInParse = getPathInParse(tree, commonRoot, pathsInParse);
//		if(pathInParse != null)
//		{
//			feature = "pathInParse=" + pathInParse;
//			featureLine.add(feature);
//		}
//		
//		return featureLine;
	}
	
	/**
	 * check if the current entity mention is a modifier for other entity
	 * e.g. US Chinese, and Russia diplomats. US/Chinese/Russia are modifiers of diplomats
	 * The idea is if an entity's head appears in the extent of other entity, then it's modifier
	 * @param sent
	 * @param mention
	 * @return
	 */
	private static boolean isModifierOfOtherEntity(SentenceInstance sent,
			AceMention mention)
	{
		if(!(mention instanceof AceEntityMention))
		{
			return false;
		}
		for(AceMention other : sent.eventArgCandidates)
		{
			if(other != mention && other instanceof AceEntityMention)
			{
				Span extent = other.extent;
				Span head = ((AceEntityMention) other).head;
				Span headOrigin = ((AceEntityMention) mention).head;
				if(headOrigin.within(extent) && headOrigin.start() < head.start())
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * check if this mention is the only one entity in a particular type
	 * @param sent
	 * @param mention
	 * @param i
	 * @return type if the mention is the only one in the sentence with the type
	 */
	private static String isOnlyOneEntity(SentenceInstance sent, AceMention mention)
	{
		String type = mention.getType();
		int count = 0;
		for(AceMention temp : sent.eventArgCandidates)
		{
			String type_temp = temp.getType();
			if(type_temp.equals(type))
			{
				count++;
			}
		}
		if(count == 1)
		{
			return type;
		}
		return null;
	}

	/**
	 * return the type of the entity if the entity is nearest of this type
	 * otherwise return null
	 * @param sent
	 * @param mention
	 * @param i
	 * @return
	 */
	private static String isNearestEntity(SentenceInstance sent, AceMention mention, int i)
	{
		int dist = getSurfaceDistance(mention.getHeadIndices(), i);
		for(AceMention candidate : sent.eventArgCandidates)
		{
			if(!candidate.equals(mention) && candidate.getType().equals(mention.getType()))
			{
				int new_dist = getSurfaceDistance(candidate.getHeadIndices(), i);
				if(dist > new_dist)
				{
					return null;
				}
			}
		}
		return mention.getType();
	}

	/**
	 * given the indice of Entity and trigger, get the distance in surface text
	 * @param headIndices
	 * @param i
	 * @return
	 */
	private static int getSurfaceDistance(Vector<Integer> headIndices, int i)
	{
		if(headIndices.contains(i))
		{
			return 0;
		}
		int min = i;
		int max = headIndices.get(0);
		if(min > max)
		{
			max = i;
			min = headIndices.get(headIndices.size() - 1);
		}
		return max - min;
	}
	
	/**
	 * given the indice of Entity and trigger, get the distance in surface text
	 * @param headIndices
	 * @param i
	 * @return
	 */
	private static int getSurfaceDistanceLog(Vector<Integer> headIndices, int i)
	{
		if(headIndices.contains(i))
		{
			return 0;
		}
		int min = i;
		int max = headIndices.get(0);
		if(min > max)
		{
			max = i;
			min = headIndices.get(headIndices.size() - 1);
		}
		double distance = max - min;
		int ret = (int) (Math.log(distance) / Math.log(2));
		return ret;
	}

	/**
	 * check if the entity scope and the trigger is across punctuation
	 * @param tokens 
	 * @param headIndices
	 * @param i
	 * @return
	 */
	private static boolean isSeparatedBypunctuation(List<Map<Class<?>, Object>> tokens, Vector<Integer> headIndices, int i)
	{
		int lower = i;
		int upper = headIndices.get(0);
		if(lower > upper)
		{
			lower = headIndices.get(headIndices.size() - 1);
			upper = i;
		}
		if(lower >= upper)
		{
			return false;
		}
		for(int t=lower+1; t<upper; t++)
		{
			String text = (String) tokens.get(t).get(TokenAnnotations.TextAnnotation.class);
			if(text.matches("\"|,|\\?"))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * given the scope of entity head, and the trigger, get the common root 
	 * @param sent
	 * @param headIndices
	 * @param i
	 * @return
	 */
	private static Tree getCommonRootInParse(Tree tree,
			Vector<Integer> headIndices, int i, List<List<Tree>> paths)
	{
		if(tree == null)
		{
			return null;
		}
		
		List<Tree> leaves = tree.getLeaves();
		for(int index : headIndices)
		{
			Tree leaf = leaves.get(index);
			List<Tree> path = path2Root(tree, leaf);
			paths.add(path);
		}
		Tree leaf = leaves.get(i);
		List<Tree> path = path2Root(tree, leaf);
		paths.add(path);
		
		Tree commonRoot = tree;
		for(int dep=0; dep<tree.depth(); dep++)
		{
			boolean violate = false;
			for(int j=0; j<paths.size()-1; j++)
			{
				List<Tree> path1 = paths.get(j);
				List<Tree> path2 = paths.get(j+1);
				if(path1.size() <= dep || path2.size() <= dep)
				{
					violate = true;
					break;
				}
				Tree node1 = path1.get(path1.size() - 1 - dep);
				Tree node2 = path2.get(path2.size() - 1 - dep);
				if(!node1.equals(node2))
				{
					violate = true;
					break;
				}
			}
			if(violate)
			{
				break;
			}
			else
			{
				commonRoot = paths.get(0).get(paths.get(0).size() - 1 - dep);
			}
		}
		
		return commonRoot;
	}

	private static List<Tree> path2Root(Tree tree, Tree leaf)
	{
		List<Tree> path = new ArrayList<Tree>();
		Tree parent = leaf.parent(tree);
		path.add(parent);
		while(!parent.equals(tree))
		{
			parent = parent.parent(tree);
			path.add(parent);
		}
		return path;
	}

	protected static String getPathInParse(Tree root, Tree commonRoot, List<List<Tree>> paths)
	{
		Tree rootOfEntity = commonRoot;
		if(paths.size() > 2)
		{
			for(int dep=0; dep<root.depth(); dep++)
			{
				boolean violate = false;
			
				for(int j=0; j<paths.size()-2; j++)
				{
					List<Tree> path1 = paths.get(j);
					List<Tree> path2 = paths.get(j+1);
					if(path1.size() <= dep || path2.size() <= dep)
					{
						violate = true;
						break;
					}
					Tree node1 = path1.get(path1.size() - 1 - dep);
					Tree node2 = path2.get(path2.size() - 1 - dep);
					if(!node1.equals(node2))
					{
						violate = true;
						break;
					}
				}
				if(violate)
				{
					break;
				}
				else
				{
					rootOfEntity = paths.get(0).get(paths.get(0).size() - 1 - dep);
				}
			}
		}
		
		String ret = "";
		// from trigger node to CommonRoot
		List<Tree> triggerPath = paths.get(paths.size() - 1);
		for(int j=1; j<triggerPath.size(); j++)
		{
			Tree node = triggerPath.get(j);
			if(node.equals(commonRoot))
			{
				break;
			}
			ret += node.value() + "#";
		}
		ret += commonRoot.value() + "|";
		// from CommonRoot to rootOfEntity
		while(!rootOfEntity.equals(commonRoot))
		{
			ret += rootOfEntity.value() + "#";
			rootOfEntity = rootOfEntity.parent(root);
		}
		
		return ret;
	}
	
	/**
	 * check if the entity and the trigger are in the same minimal clause
	 * @param headIndices
	 * @param tokens
	 * @param i
	 * @return
	 */
	private static Boolean isSameClause(Vector<Integer> headIndices, List<Map<Class<?>, Object>> tokens, int i)
	{
		Integer clause_num_token = (Integer) tokens.get(i).get(TokenAnnotations.ClauseAnnotation.class);
		if(clause_num_token == null)
		{
			return null;
		}
		for(Integer index : headIndices)
		{
			Integer clause_num = (Integer) tokens.get(index).get(TokenAnnotations.ClauseAnnotation.class);
			if(clause_num != null && clause_num_token.equals(clause_num))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * find the shortest dependnecy path between entity and trigger
	 * by using corefernced entity mentions
	 * @param sent
	 * @param mention
	 * @param i
	 * @return
	 */
	private static List<String> getDependencyPathsCoref(SentenceInstance sent, AceMention mention , int i)
	{
		Vector<Integer> headIndices = new Vector<Integer>();
		AceEventArgumentValue parent = mention.getParent();
		if(parent == null)
		{
			return null;
		}
		for(AceMention another : sent.eventArgCandidates)
		{
			if(another == mention)
			{
				continue;
			}
			
			AceEventArgumentValue parentAnother = another.getParent();
			
			if(parent.equals(parentAnother))
			{
				// coreference mention
				
				List<Integer> indices = another.getHeadIndices();
				headIndices.addAll(indices);
			}
		}
		
		if(headIndices.size() <= 0)
		{
			return null;
		}
		
		return getDependencyPaths(sent, headIndices, i);
	}
	
	/**
	 * get the shortest dependency paths between argument and trigger
	 * use entity information to normalize paths
	 * @param sent
	 * @param headIndices
	 * @return
	 */
	private static List<String> getDependencyPaths(SentenceInstance sent, Vector<Integer> headIndices, int i)
	{
		List<Map<Class<?>, Object>> tokens = (List<Map<Class<?>, Object>>) sent.get(SentenceInstance.InstanceAnnotations.Token_FEATURE_MAPs);
		DependencyGraph graph = (DependencyGraph) sent.get(SentenceInstance.InstanceAnnotations.DepGraph);
		if(graph == null)
		{
			return null;
		}
		Vector<Integer> temp = new Vector<Integer>();
		temp.add(i); // the node of trigger
		Vector<PathTerm> path = graph.getShortestPathFeatured(headIndices, temp);
		if(path == null || path.size() == 0)
		{
			return null;
		}
		List<String> ret = new ArrayList<String>();
		// path from Entity Head (exclusively) to Trigger (exclusively)
		String path1 = "";
		for(int index = 1; index < path.size() - 1; index++)
		{
			PathTerm term = path.get(index);
			// if the term is a token, then try to use entity type to normalize it
			if(term.isVertex)
			{
				int token_index = term.vertex.index;
				String word = "";
				String entity_type = getEntityType(tokens, token_index);
				if(entity_type == null)
				{
					word = (String) tokens.get(token_index).get(TokenAnnotations.LemmaAnnotation.class);
				}
				else
				{
					word = entity_type;
				}
				path1 += word + "#";
			}
			else
			{
				path1 += term.toString() + "#";
			}
		}
		ret.add(path1);
		// same path but normalized by pos
		if(path.size() - 2 > 1)
		{
			String path2 = "";
			for(int index = 1; index < path.size() - 1; index++)
			{
				PathTerm term = path.get(index);
				// if the term is a token, then try to use entity type to normalize it
				if(term.isVertex)
				{
					int token_index = term.vertex.index;
					String pos = "";
					pos = (String) tokens.get(token_index).get(TokenAnnotations.PartOfSpeechAnnotation.class);
					path2 += pos + "#";
				}
				else
				{
					path2 += term.toString() + "#";
				}
			}
			ret.add(path2);
		}
		return ret;
	}
	
	/**
	 * given a sent, and a token index, determine if this token is within an entity, 
	 * if so, return the entity type 
	 * @param tokens
	 * @param term_index
	 * @return
	 */
	private static String getEntityType(List<Map<Class<?>, Object>> tokens, int token_index)
	{
		List<String> entityInfo = (List<String>) tokens.get(token_index).get(TokenAnnotations.EntityAnnotation.class);
		if(entityInfo != null)
		{
			return entityInfo.get(0);
		}
		return null;
	}

	/**
	 * get the dependency words of entity head which are outof entity scope
	 * @param sent
	 * @param headIndices
	 * @return
	 */
	protected static Vector<String> getDependencyFeatures(Sentence sent, Vector<Integer> headIndices)
	{
		List<Map<Class<?>, Object>> tokens = (List<Map<Class<?>, Object>>) sent.get(Sent_Attribute.Token_FEATURE_MAPs);
		DependencyGraph graph = (DependencyGraph) sent.get(Sent_Attribute.DepGraph);
		Vector<String> ret = new Vector<String>();
		// traverse each dependency link
		if(graph == null)
		{
			return ret;
		}
		for(Integer i : headIndices)
		{
			GraphNode node = graph.getVertices().get(i);
			for(GraphEdge edge : node.edges)
			{
				int index = edge.getGovernor();
				String label = "Gov";
				if(index == i)
				{
					index = edge.getDependent();
					label = "Dep";
				}
				// the head doesn't contain the index, only track the dependencies out of the head
				if(!headIndices.contains(index))
				{
					Map<Class<?>, Object> related_token = tokens.get(index);
					String related_word = (String) related_token.get(TokenAnnotations.TextAnnotation.class);
					if(related_word != null)
					{
						String feature = label + "=" + edge.getRelation() + "_" + related_word;
						ret.add(feature);
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * get the neighbor words / neighbor bigrams as features
	 * @param sent
	 * @param mention
	 * @param featureLine
	 */
	protected static String getNeighborWords(List<Map<Class<?>, Object>> sent, int trigger_index, AceMention mention, String featurePrefix, int[] offsets)
	{
		final String Delimiter = "#";	
		String value = "";
		Vector<Integer> extentIndices = null;
		if(mention instanceof AceEntityMention)
		{
			extentIndices = mention.getExtentIndices();
		}
		else
		{
			extentIndices = mention.getHeadIndices();
		}
		for(int offset : offsets)
		{
			int index = 0;
			String word = "";
			if(offset < 0)
			{
				index = extentIndices.get(0) + offset;
			}
			else
			{
				index = extentIndices.get(extentIndices.size() - 1) + offset;
			}
			if(index == trigger_index)
			{
				// use trigger word to normalize
				word = "TriggerWord";
			}
			else if(index < sent.size() && index >= 0)
			{
				Map<Class<?>, Object> token_offset = sent.get(index);
				word = (String) token_offset.get(TokenAnnotations.LemmaAnnotation.class);
			}
			else
			{
				return null;
			}
			if(value.length() > 0)
			{
				value += Delimiter;
			}
			value += word;
		}
		String feature = featurePrefix + "=" + value;
		return feature;
	}
	
//	public static void main(String[] args)
//	{
//		String text = "\"";
//		if(text.matches("\"|,|\\?"))
//		{
//			System.out.println(true);
//		}
//	}
}
