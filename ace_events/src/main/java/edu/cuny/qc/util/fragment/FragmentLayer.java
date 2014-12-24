package edu.cuny.qc.util.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ac.biu.nlp.nlp.ace_uima.AceAbnormalMessage;
import ac.biu.nlp.nlp.ace_uima.AceException;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.util.Utils;
import edu.cuny.qc.util.fragment.TreeFragmentBuilder.TreeFragmentBuilderException;
import eu.excitementproject.eop.common.datastructures.OneToManyBidiMultiHashMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeToLineString;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;
import eu.excitementproject.eop.lap.biu.uima.CasTreeConverter;

public class FragmentLayer {
	public JCas jcas;
	public Map<Token, Collection<Sentence>> tokenIndex;	
	public OneToManyBidiMultiHashMap<Token, BasicNode> token2nodes;
	public BidiMap<Sentence, BasicNode> sentence2root;
	public TreeFragmentBuilder fragmenter;
	public Map<BasicNode, Facet> linkToFacet;
	
	static {
		System.err.println("??? FragmentLayer: I am letting quite a few erorrs from the converter get away, which costs me in lost sentence. Maybe I should inspect them and reduce them (like by removing some more weird tokens). Some examples - stuff with hyphens (sometimes the AceAbnormalError wouldn't show the hyphen, like with President[579:588] which is really President-Elect in CNN_CF_20030304.1900.02), numbers (years and such), stuff with the percent sign, maybe punctuation stuff, etc.\n");
	}
	
	public FragmentLayer(JCas jcas, CasTreeConverter converter) throws FragmentLayerException {
//		try {
			this.jcas = jcas;
			tokenIndex = JCasUtil.indexCovering(jcas, Token.class, Sentence.class);
			
			token2nodes = new OneToManyBidiMultiHashMap<Token,BasicNode>();
			sentence2root = new DualHashBidiMap<Sentence, BasicNode>();
			
			int errors = 0;
			for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
				try {
					BasicNode root = converter.convertSingleSentenceToTree(jcas, sentence);
					/// DEBUG
//					if (sentence.getCoveredText().contains("esterday's")) {
//						System.out.printf("");
//					}
					///
					token2nodes.putAll(converter.getAllTokensToNodes());
					sentence2root.put(sentence, root);
				}
				catch (Exception e) {
					System.err.printf("\n- Got error while working on sentence: '%s'\n", sentence.getCoveredText());
					e.printStackTrace(System.err);
					System.err.printf("================\n\n");
					errors++;
				}
			}
			
			// Yes, we would allow a certain amount of sentences to slip by due to parsing related errors...
			final int ALLOWED_ERRORS = 20;
			if (errors > ALLOWED_ERRORS) {
				throw new FragmentLayerException("got " + errors + " errors while converting CAS (detailed before) - aborting."); 
			}
			
			//converter.convertCasToTrees(jcas);
			//token2nodes = converter.getAllTokensToNodes();
			//sentence2root = converter.getSentenceToRootMap();
			
			fragmenter = new TreeFragmentBuilder();
			linkToFacet = new LinkedHashMap<BasicNode, Facet>();
//		}
//		catch (UnsupportedPosTagStringException e) {
//			throw new FragmentLayerException(e);
//		}
	}
	
	public List<BasicNode> getTreeFragments(Annotation covering) throws CASException, AceException, TreeAndParentMapException, TreeFragmentBuilderException, FragmentLayerException {
		List<BasicNode> result = new ArrayList<BasicNode>();
		if (covering != null) {
			MultiMap<Sentence, Token> sentence2tokens = Utils.getCoveringSentences(covering, tokenIndex);
			for (Entry<Sentence,Collection<Token>> entry : sentence2tokens.entrySet()) {
				FragmentAndReference frag = getFragmentBySentenceAndTokens(entry.getKey(), entry.getValue(), null, false, null);
				result.add(frag.getFragmentRoot());
			}
		}
		return result;
	}
	
	public BasicNode getRoot(Annotation covering) throws CASException, FragmentLayerException {
		Sentence sentence = UimaUtils.selectCoveredSingle(Sentence.class, covering);
		BasicNode root = sentence2root.get(sentence);
		if (root == null) {
			throw new FragmentLayerException(String.format("No root for sentence: '%s'", sentence.getCoveredText()));
		}
		return root;
	}

	public FragmentAndReference getFragmentBySentenceAndTokens(Sentence sentence, Collection<Token> tokens, Map<Token, String> magicTokens, boolean removeConj, Facet facet) throws TreeAndParentMapException, TreeFragmentBuilderException, FragmentLayerException {
		BasicNode root = sentence2root.get(sentence);
		Set<BasicNode> targetNodes = new LinkedHashSet<BasicNode>(tokens.size());
		BiMap<BasicNode, String> magicNodes = HashBiMap.create();
		for (Token token : tokens) {
			Collection<BasicNode> nodes = token2nodes.get(token);
			/// DEBUG
			if (nodes == null) {
				System.out.printf("\n\n\nnull nodes for token %s[%s:%s]!!!!!!! Sentence: %s||||\n\n\n", token.getCoveredText(), token.getBegin(), token.getEnd(), sentence.getCoveredText());
			}
			///
			if (nodes == null) {
				throw new FragmentLayerException("Token " + UimaUtils.annotationToString(token) + " doesn't have a corresponding tree node. Maybe for some reason CasTreeConvereter skipped or had some mistake with it?");
			}
			targetNodes.addAll(nodes); //Also get duplicated nodes!
			
			if (magicTokens != null && magicTokens.keySet().contains(token)) {
				for (BasicNode node : nodes) {
					magicNodes.put(node, magicTokens.get(token));
				}
			}
		}
		/// DEBUG
//		if (sentence.getCoveredText().startsWith("Are you willing to pay")) {
//			System.err.printf("\n\n\n\n\n\nGot cha!!!!!\n\n\n\n");
//		}
		///
		//logger.trace("-------- fragmenter.build(" + AnotherBasicNodeUtils.getNodeString(root) + ", " + AnotherBasicNodeUtils.getNodesString(targetNodes) + ")");
		FragmentAndReference fragRef = fragmenter.build(root, targetNodes, magicNodes, removeConj, facet);
		return fragRef;
	}
	
	/**
	 * Returns a tree fragment of the connection between the roots of the two covering annotations.<BR><BR>
	 * This method assumes that each covering annotation is within sentence boundaries,
	 * otherwise it doesn't make much sense. This is in contrary to {@link #getTreeFragments(Annotation)}
	 * which does not assume that and may return multiple fragments.
	 * @param covering
	 * @return
	 * @throws CASException
	 * @throws AceException
	 * @throws TreeAndParentMapException
	 * @throws TreeFragmentBuilderException
	 * @throws AceAbnormalMessage 
	 * @throws FragmentLayerException 
	 */
	public FragmentAndReference getRootLinkingTreeFragment(Annotation /*EventMentionAnchor*/ eventAnchor, Annotation /*BasicArgumentMentionHead*/ argHead, boolean removeConj, Object /*EventMentionArgument*/ argMention) throws CASException, AceException, TreeAndParentMapException, TreeFragmentBuilderException, AceAbnormalMessage, FragmentLayerException {
		if (eventAnchor == null || argHead == null) {
			/// SilentErrors
			throw new AceAbnormalMessage("NullParam");
			///
		}
		
		//logger.trace("%%% 1");
		
		MultiMap<Sentence, Token> sentence2tokens_1 = Utils.getCoveringSentences(eventAnchor, tokenIndex);
		MultiMap<Sentence, Token> sentence2tokens_2 = Utils.getCoveringSentences(argHead, tokenIndex);
		if (sentence2tokens_1.size() == 0 || sentence2tokens_2.size() == 0) {
			/// SilentErrors
			String err;
			if (sentence2tokens_1.size() == 0 && sentence2tokens_2.size() != 0) {
				err = String.format("trigger=%s", UimaUtils.annotationToString(eventAnchor));
			}
			else if (sentence2tokens_2.size() == 0 && sentence2tokens_1.size() != 0) {
				err = String.format("arg=%s", UimaUtils.annotationToString(argHead));
			}
			else {
				err = String.format("trigger=%s and arg=%s", UimaUtils.annotationToString(eventAnchor), UimaUtils.annotationToString(argHead));
			}
			throw new AceAbnormalMessage(String.format("ERR:No Covering Sentence for %s", err));
//			throw new AceAbnormalMessage("NoCoveringSentence");
			///
		}
		if (sentence2tokens_1.size() > 1 || sentence2tokens_2.size() > 1) {
			throw new AceAbnormalMessage("ERR:Multiple Sentence Annotation", String.format("Got at least one of the two annotations, that does not cover exactly one sentence: " +
					"(%s sentences, %s sentences)", sentence2tokens_1.size(), sentence2tokens_2.size()), null);
		}
		Entry<Sentence,Collection<Token>> s2t1 = sentence2tokens_1.entrySet().iterator().next();
		Entry<Sentence,Collection<Token>> s2t2 = sentence2tokens_2.entrySet().iterator().next();
		if (s2t1.getKey() != s2t2.getKey()) {
			throw new AceAbnormalMessage("ERR:Different Sentences", String.format("Got two annotations in different sentences: sentence1=%s, sentence2=%s",
					s2t1.getKey(), s2t2.getKey()), null);
		}
		Sentence sentence = s2t1.getKey();
		/// DEBUG
		//System.err.printf("FragmentLayer: sentence=%s\n", UimaUtils.annotationToString(sentence));
		////

		//logger.trace("%%% 2");

		// get the fragment of each covering annotation
		FragmentAndReference frag1 = getFragmentBySentenceAndTokens(sentence, s2t1.getValue(), null, false, null);
		FragmentAndReference frag2 = getFragmentBySentenceAndTokens(sentence, s2t2.getValue(), null, false, null);
		//logger.trace("%%% 3");

		// and now... get the fragment containing the roots of both fragments!
		// this is the connecting fragment
		Token root1 = token2nodes.getSingleKeyOf(frag1.getOrigReference());
		Token root2 = token2nodes.getSingleKeyOf(frag2.getOrigReference());
//		Token root1 = info2token.get(frag1.getInfo());
//		Token root2 = info2token.get(frag2.getInfo());
		//logger.trace("%%% 4");

		//TODO remove, for debug
//		List<BasicNode> n = new ArrayList<BasicNode>();
//		for (BasicNode nn : token2nodes.values()) {
//			if (frag1.getInfo().getNodeInfo().getWord().equals(nn.getInfo().getNodeInfo().getWord())) {
//				n.add(nn);
//			}
//		}
		//TODO finish
		
		Facet facet = new Facet(frag1.getOrigReference(), frag2.getOrigReference(), eventAnchor, (EventMentionArgument) argMention, sentence);

		Map<Token, String> magicTokens = new HashMap<Token, String>();
		magicTokens.put(root1, TreeToLineString.MAGIC_NODE_PREDICATE);
		magicTokens.put(root2, TreeToLineString.MAGIC_NODE_ARGUMENT);
		
		List<Token> bothRoots = Arrays.asList(new Token[] {root1, root2});
		FragmentAndReference connectingFrag = getFragmentBySentenceAndTokens(sentence, bothRoots, magicTokens, removeConj, facet);
		//logger.trace("%%% 5");

		linkToFacet.put(connectingFrag.getFragmentRoot(), facet);
		return connectingFrag;
	}

}
