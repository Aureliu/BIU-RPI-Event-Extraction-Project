package edu.cuny.qc.scorer.mechanism;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;

import ac.biu.nlp.nlp.ie.onthefly.input.AnnotationUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.scorer.Aggregator;
import edu.cuny.qc.scorer.Compose;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.SignalMechanismSpecTokenIterator;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordNetDictionaryFactory;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfoWithSenseNumsOnly;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.BasicRuleAnnotations;

public class WordNetSignalMechanism extends SignalMechanism {

	static {
		System.err.println("??? WordNetSignalMechanism: Fake Signal Mechanisms");
		System.err.println("??? WordNetSignalMechanism: ignoring spec's POS, using only text's");
		System.err.println("??? WordNetSignalMechanism: if a word has a non-wordnet POS (anything but noun/verb/adj/adv) we return FALSE, but we should return IRRELEVANT (when I figure out what it means... :( )");
		System.err.println("??? WordNetSignalMechanism: if a text or spec doesn't exist in wordnet, we return FALSE, although we should return IRRELEVANT");
		System.err.println("??? WordNetSignalMechanism: We want to duplicate signals for different POSes (learn different weights), but currently can't, since we don't support (yet) IRRELEVANT");
	}

	@SuppressWarnings("serial")
	public WordNetSignalMechanism() throws LexicalResourceException, WordNetInitializationException {
		super();
		
		//File wordnetDir = new File(WORDNET_DIR);
		resource_chain1 = new WordnetLexicalResource(
				WORDNET_DIR,
				USE_FIRST_SENSE_ONLY_LEFT,
				USE_FIRST_SENSE_ONLY_RIGHT,
				DEFAULT_RELATIONS,
				1
				);
		resource_chain2 = new WordnetLexicalResource(
				WORDNET_DIR,
				USE_FIRST_SENSE_ONLY_LEFT,
				USE_FIRST_SENSE_ONLY_RIGHT,
				DEFAULT_RELATIONS,
				2
				);
		resource_chain3 = new WordnetLexicalResource(
				WORDNET_DIR,
				USE_FIRST_SENSE_ONLY_LEFT,
				USE_FIRST_SENSE_ONLY_RIGHT,
				DEFAULT_RELATIONS,
				3
				);
		resource_chain4 = new WordnetLexicalResource(
				WORDNET_DIR,
				USE_FIRST_SENSE_ONLY_LEFT,
				USE_FIRST_SENSE_ONLY_RIGHT,
				DEFAULT_RELATIONS,
				4
				);
		resource_chain5 = new WordnetLexicalResource(
				WORDNET_DIR,
				USE_FIRST_SENSE_ONLY_LEFT,
				USE_FIRST_SENSE_ONLY_RIGHT,
				DEFAULT_RELATIONS,
				5
				);
		System.err.printf("Worndet params: USE_FIRST_SENSE_ONLY_LEFT=%s, USE_FIRST_SENSE_ONLY_RIGHT=%s, DEFAULT_RELATIONS=%s, CHAINING_LENGTH=1/2/3/4/5\n",
				USE_FIRST_SENSE_ONLY_LEFT, USE_FIRST_SENSE_ONLY_RIGHT, DEFAULT_RELATIONS);

		// A dictionary is created and kept in the WordnetLexicalResource,
		// but we don't have access to it, so we create another one
		dictionary =  WordNetDictionaryFactory.newDictionary(WORDNET_DIR, null);
		
		resourceMap = new HashMap<Integer, WordnetLexicalResource>(){{
			put(1, resource_chain1);
			put(2, resource_chain2);
			put(3, resource_chain3);
			put(4, resource_chain4);
			put(5, resource_chain5);
		}};
		
		cahceDefaultMap = new HashMap<Integer, Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>>(){{
			put(1, cahceDefaultRules_1);
			put(2, cahceDefaultRules_2);
			put(3, cahceDefaultRules_3);
			put(4, cahceDefaultRules_4);
			put(5, cahceDefaultRules_5);
		}};

		cacheHypernymMap = new HashMap<Integer, Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>>(){{
			put(1, cahceHypernymRules_1);
			put(2, cahceHypernymRules_2);
			put(3, cahceHypernymRules_3);
			put(4, cahceHypernymRules_4);
			put(5, cahceHypernymRules_5);
		}};

		cahceDerivationallyRelatedMap = new HashMap<Integer, Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>>(){{
			put(1, cahceDerivationallyRelatedRules_1);
			put(2, cahceDerivationallyRelatedRules_2);
			put(3, cahceDerivationallyRelatedRules_3);
		}};



	}
	
	@Override
	public void close() {
		dictionary.close();
		for (WordnetLexicalResource resource : resourceMap.values()) {
			resource.close();
		}
		super.close();
	}
	
//	@Override
//	public void preprocessSpec(JCas spec) throws SignalMechanismException {
//		System.err.println("Currently no CAS-processing by WordNetSignalMechanism - TBD");
//	}
//
//	@Override
//	public void preprocessTextSentence(SentenceInstance textSentence) throws SignalMechanismException {
//		throw new NotImplementedException();
//	}

	@Override
	public void addScorers() {
		addTrigger(new ScorerData("WN_SYNSET",			SameSynset.inst,			Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_HYPERNYM_1",		IsSpecHypernym_1.inst,		Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_HYPERNYM_2",		IsSpecHypernym_2.inst,		Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_HYPERNYM_2",		IsSpecHypernym_2.inst,		Aggregator.Min2.inst		));
		addTrigger(new ScorerData("WN_HYPERNYM_3",		IsSpecHypernym_3.inst,		Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_HYPERNYM_4",		IsSpecHypernym_4.inst,		Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_HYPERNYM_5",		IsSpecHypernym_5.inst,		Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_HYPERNYM_5",		IsSpecHypernym_5.inst,		Aggregator.Min2.inst		));
		addTrigger(new ScorerData("WN_DERV_RELATED",	IsSpecDervRelated.inst,		Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_ENTAILED_1",		IsSpecEntailed_1.inst,		Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_ENTAILED_1",		IsSpecEntailed_1.inst,		Aggregator.Min2.inst	));
		addTrigger(new ScorerData("WN_ENTAILED_2",		IsSpecEntailed_2.inst,		Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_ENTAILED_2",		IsSpecEntailed_2.inst,		Aggregator.Min2.inst		));
		addTrigger(new ScorerData("WN_ENTAILED_3",		IsSpecEntailed_3.inst,		Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_ENTAILED_4",		IsSpecEntailed_4.inst,		Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_ENTAILED_5",		IsSpecEntailed_5.inst,		Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_ENTAILED_5",		IsSpecEntailed_5.inst,		Aggregator.Min2.inst		));
		addTrigger(new ScorerData("WN_SYNSET|WN_HYPERNYM_2",	new Compose.Or(SameSynset.inst, IsSpecHypernym_2.inst),	Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_SYNSET|WN_HYPERNYM_4",	new Compose.Or(SameSynset.inst, IsSpecHypernym_4.inst),	Aggregator.Any.inst		));
		addTrigger(new ScorerData("WN_SYNSET|WN_HYPERNYM_4",	new Compose.Or(SameSynset.inst, IsSpecHypernym_4.inst),	Aggregator.Min2.inst	));

		//addTrigger(new ScorerData("FAKE_PREKNOWN_TRIGGERS",				PreknownTriggers.inst,			Aggregator.Any.inst));
		
		addArgument(new ScorerData("xxWN_SYNSET",		SameSynset.inst,			Aggregator.Any.inst		));
		addArgument(new ScorerData("xxWN_HYPERNYM_1",	IsSpecHypernym_1.inst,		Aggregator.Any.inst		));
		addArgument(new ScorerData("xxWN_ENTAILED_1",	IsSpecEntailed_1.inst,		Aggregator.Any.inst		));
	}

	private enum Juxtaposition {
		ANCESTOR,
		COUSIN,
	}
	private enum Derivation {
		NONE,
		ONLY_TEXT,
		ONLY_SPEC,
		// BOTH  // this sounds too far
	}
	private enum SynsetScope {
		BOTH_FIRSTS(1,1),
		TEXT_ALL_SPEC_FIRST(-1,1),
		TEXT_FIRST_SPEC_ALL(1,-1),
		BOTH_ALL(-1,-1);
		private SynsetScope(int leftSenseNum, int rightSenseNum) {
			this.leftSenseNum = leftSenseNum;
			this.rightSenseNum = rightSenseNum;
		}
		public int leftSenseNum, rightSenseNum;
	}
//	private enum LengthType {
//		FIXED,
//		RELATIVE
//	}
	private static class WordnetScorer extends SignalMechanismSpecTokenIterator {
		@Override public String getTypeName() {
			//put params in name!
		}
		public Set<WordNetRelation> relations;
		public Juxtaposition juxt;
		public int length;
		public Derivation derv; 
		public SynsetScope synsetScope;
		public PartOfSpeech pos;
//		public LengthType lenType;
		
		public WordnetLexicalResource resource;
		
		private LoadingCache<FullRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cacheRules = CacheBuilder.newBuilder()
				.recordStats()
				.maximumWeight(100000)
				.weigher(new Weigher<FullRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>() {
					public int weigh(FullRulesQuery k, List<LexicalRule<? extends WordnetRuleInfo>> v) { return v.size(); }
				})
				.build(new CacheLoader<FullRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>() {
					public List<LexicalRule<? extends WordnetRuleInfo>> load(FullRulesQuery key) {
						BasicRulesQuery q = key.basicQuery;
						WordnetRuleInfo info = new WordnetRuleInfoWithSenseNumsOnly(key.synsetScope.leftSenseNum, key.synsetScope.rightSenseNum);
						switch (key.juxt) {
						case ANCESTOR: break;
						case COUSIN: break;
						default: throw new IllegalArgumentException("juxt=" + key.juxt);
						}
						List<LexicalRule<? extends WordnetRuleInfo>> rules = resource.getRules(q.lLemma, q.lPos, q.rLemma, q.rPos, key.relations, info);
						if (!rules.isEmpty()) {
							List<WordNetRelation> ruleRelations = new ArrayList<WordNetRelation>(rules.size());
							for (LexicalRule<? extends WordnetRuleInfo> rule : rules) {
								ruleRelations.add(rule.getInfo().getTypedRelation());
							}
							System.err.printf("Wordnet: TRUE! %s/%s-->%s/%s\t(juxt=%s, length=%s, scope=%s)\n",
									q.lLemma, q.lPos, q.rLemma, q.rPos, key.juxt, key.length, key.synsetScope);
						}
			            return rules;
					}
				});

		
		@Override
		public Boolean calcTokenBooleanScore(Token text, Map<Class<?>, Object> textTriggerTokenMap, Token spec) throws SignalMechanismException {
			try {
				PartOfSpeech textPos = AnnotationUtils.tokenToPOS(text);
				String textLemma = text.getLemma().getValue();
				String specLemma = spec.getLemma().getValue();
				
				
				
				
				
				
				List<LexicalRule<? extends WordnetRuleInfo>> rules = getHypernymRules(
									textLemma,
									textPos,
									specLemma,
									textPos,
									getChainingLength());
				
				
				resource = getResource(chainingLength);
				Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cache = cacheHypernymMap.get(chainingLength);
				String title = String.format("Wordnet.Hypernym(chain=%s)", chainingLength);
				return getRules(title, false, resource, cache, HYPERNYM_RELATION, textLemma, textPos, specLemma, specPos);

				

				return !rules.isEmpty();
			} catch (LexicalResourceException e) {
				throw new SignalMechanismException(e);
			} catch (UnsupportedPosTagStringException e) {
				throw new SignalMechanismException(e);
			}
		}
		
		private static WordnetLexicalResource getResource(int chainingLength) throws LexicalResourceException {
			WordnetLexicalResource result = resourceMap.get(chainingLength);
			if (result == null) {
				result = new WordnetLexicalResource(
						WORDNET_DIR,
						false,				// this will be overridden in the queries
						false,				// this will be overridden in the queries
						DEFAULT_RELATIONS,	// this will be overridden in the queries
						chainingLength
						);
				resourceMap.put(chainingLength, result);
			}
			return result;
		}
	}
	
	private static class SameSynset extends SignalMechanismSpecTokenIterator {
		public static final SameSynset inst = new SameSynset();
		@Override
		public Boolean calcTokenBooleanScore(Token text, Map<Class<?>, Object> textTriggerTokenMap, Token spec) throws SignalMechanismException
		{
			try {
				PartOfSpeech textPos = AnnotationUtils.tokenToPOS(text);
				String textLemma = text.getLemma().getValue();
				String specLemma = spec.getLemma().getValue();
				
				Boolean result = isOverlappingSynsets(textLemma, textPos, specLemma, textPos);
				return result;
				
//				WordNetPartOfSpeech textWnPos = WordNetPartOfSpeech.toWordNetPartOfspeech(textPos);
//				
//				if (textWnPos == null) {
//					return false;
//				}
//				
//				String textLemma = text.getLemma().getValue();
//				String specLemma = spec.getLemma().getValue();
//				Set<Synset> textSynsets = getSynsetsOf(textLemma, textWnPos);
//				//Use text's POS also for spec
//				Set<Synset> specSynsets = getSynsetsOf(specLemma, textWnPos);
//				
//				if (textSynsets.isEmpty() || specSynsets.isEmpty()) {
////					if (textSynsets.isEmpty()) {
////						System.err.printf("WordNetSignalMechanism: Empty Synset for text: '%s' (pos=%s)\n", textLemma, textWnPos);
////					}
////					if (specSynsets.isEmpty()) {
////						//System.err.printf("WordNetSignalMechanism: Empty Synset for spec: '%s' (pos=%s)\n", specLemma, textWnPos);
////					}
//					return false;
//				}
//					
//				boolean differentSynsets = Collections.disjoint(textSynsets, specSynsets);
//				//DEBUG
//				if (!differentSynsets) {
//					System.err.printf("Wordnet.SameSynset: TRUE! (%s,%s)\n", textLemma, specLemma);
//				}
//				return !differentSynsets;
			} catch (WordNetException e) {
				throw new SignalMechanismException(e);
			} catch (UnsupportedPosTagStringException e) {
				throw new SignalMechanismException(e);
			}
		}
	}
	
	private static abstract class IsSpecHypernym extends SignalMechanismSpecTokenIterator {
		public abstract int getChainingLength();
		@Override
		public Boolean calcTokenBooleanScore(Token text, Map<Class<?>, Object> textTriggerTokenMap, Token spec) throws SignalMechanismException
		{
			try {
				PartOfSpeech textPos = AnnotationUtils.tokenToPOS(text);
				//Set<WordNetRelation> hypernym = new HashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {WordNetRelation.HYPERNYM}));
				String textLemma = text.getLemma().getValue();
				String specLemma = spec.getLemma().getValue();
				List<LexicalRule<? extends WordnetRuleInfo>> rules = getHypernymRules(
									textLemma,
									textPos,
									specLemma,
									textPos,
									getChainingLength());
				//DEBUG
//				if (!rules.isEmpty()) {
//					System.err.printf("Wordnet.IsSpecHypernym: TRUE! %s-->%s\n", textLemma, specLemma);
//				}
				return !rules.isEmpty();
			} catch (LexicalResourceException e) {
				throw new SignalMechanismException(e);
			} catch (UnsupportedPosTagStringException e) {
				throw new SignalMechanismException(e);
			}
		}
	}
	
	private static class IsSpecHypernym_1 extends IsSpecHypernym {
		public static final IsSpecHypernym_1 inst = new IsSpecHypernym_1();
		@Override public int getChainingLength() { return 1; }
	}
	
	private static class IsSpecHypernym_2 extends IsSpecHypernym {
		public static final IsSpecHypernym_2 inst = new IsSpecHypernym_2();
		@Override public int getChainingLength() { return 2; }
	}
	
	private static class IsSpecHypernym_3 extends IsSpecHypernym {
		public static final IsSpecHypernym_3 inst = new IsSpecHypernym_3();
		@Override public int getChainingLength() { return 3; }
	}
	
	private static class IsSpecHypernym_4 extends IsSpecHypernym {
		public static final IsSpecHypernym_4 inst = new IsSpecHypernym_4();
		@Override public int getChainingLength() { return 4; }
	}
	
	private static class IsSpecHypernym_5 extends IsSpecHypernym {
		public static final IsSpecHypernym_5 inst = new IsSpecHypernym_5();
		@Override public int getChainingLength() { return 5; }
	}
	
	private static class IsSpecDervRelated extends SignalMechanismSpecTokenIterator {
		public static final IsSpecDervRelated inst = new IsSpecDervRelated();
		@Override
		public Boolean calcTokenBooleanScore(Token text, Map<Class<?>, Object> textTriggerTokenMap, Token spec) throws SignalMechanismException
		{
			try {
				PartOfSpeech textPos = AnnotationUtils.tokenToPOS(text);
				//Set<WordNetRelation> hypernym = new HashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {WordNetRelation.HYPERNYM}));
				String textLemma = text.getLemma().getValue();
				String specLemma = spec.getLemma().getValue();
				List<LexicalRule<? extends WordnetRuleInfo>> rules = getDerivationallyRelatedRules(
									textLemma,
									textPos,
									specLemma,
									textPos,
									1);
				//DEBUG
//				if (!rules.isEmpty()) {
//					System.err.printf("Wordnet.IsSpecHypernym: TRUE! %s-->%s\n", textLemma, specLemma);
//				}
				return !rules.isEmpty();
			} catch (LexicalResourceException e) {
				throw new SignalMechanismException(e);
			} catch (UnsupportedPosTagStringException e) {
				throw new SignalMechanismException(e);
			}
		}
	}
	
	private static abstract class IsSpecEntailed extends SignalMechanismSpecTokenIterator {
		public abstract int getChainingLength();
		@Override
		public Boolean calcTokenBooleanScore(Token text, Map<Class<?>, Object> textTriggerTokenMap, Token spec) throws SignalMechanismException
		{
			try {
				PartOfSpeech textPos = AnnotationUtils.tokenToPOS(text);
				String textLemma = text.getLemma().getValue();
				String specLemma = spec.getLemma().getValue();
				List<LexicalRule<? extends WordnetRuleInfo>> rules = getDefaultRules(
									textLemma,
									textPos,
									specLemma,
									textPos,
									getChainingLength());
				//DEBUG
//				if (!rules.isEmpty()) {
//					List<WordNetRelation> relations = new ArrayList<WordNetRelation>(rules.size());
//					for (LexicalRule<? extends WordnetRuleInfo> rule : rules) {
//						relations.add(rule.getInfo().getTypedRelation());
//					}
//					System.err.printf("Wordnet.IsSpecEntailed: TRUE! %s-->%s (%d: %s)\n", textLemma, specLemma, rules.size(), relations);
//				}
				return !rules.isEmpty();
			} catch (LexicalResourceException e) {
				throw new SignalMechanismException(e);
			} catch (UnsupportedPosTagStringException e) {
				throw new SignalMechanismException(e);
			}
		}
	}
	
	private static class IsSpecEntailed_1 extends IsSpecEntailed {
		public static final IsSpecEntailed_1 inst = new IsSpecEntailed_1();
		@Override public int getChainingLength() {return 1;}
	}

	private static class IsSpecEntailed_2 extends IsSpecEntailed {
		public static final IsSpecEntailed_2 inst = new IsSpecEntailed_2();
		@Override public int getChainingLength() {return 2;}
	}

	private static class IsSpecEntailed_3 extends IsSpecEntailed {
		public static final IsSpecEntailed_3 inst = new IsSpecEntailed_3();
		@Override public int getChainingLength() {return 3;}
	}

	private static class IsSpecEntailed_4 extends IsSpecEntailed {
		public static final IsSpecEntailed_4 inst = new IsSpecEntailed_4();
		@Override public int getChainingLength() {return 4;}
	}

	private static class IsSpecEntailed_5 extends IsSpecEntailed {
		public static final IsSpecEntailed_5 inst = new IsSpecEntailed_5();
		@Override public int getChainingLength() {return 5;}
	}

	
	private static class BasicRulesQuery {
		@Override public int hashCode() {
		     return new HashCodeBuilder(17, 37).append(lLemma).append(rLemma).append(lPos).append(rPos).toHashCode();
		}
		@Override public boolean equals(Object obj) {
		   if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) { return false; }
		   BasicRulesQuery rhs = (BasicRulesQuery) obj;
		   return new EqualsBuilder().append(lLemma, rhs.lLemma).append(rLemma, rhs.rLemma).append(lPos, rhs.lPos).append(rPos, rhs.rPos).isEquals();
		}
		public String toString() {
			return String.format("%s(%s/%s-->%s/%s)", BasicRulesQuery.class.getSimpleName(), lLemma, lPos, rLemma, rPos);
		}
		public BasicRulesQuery(String lLemma, String rLemma, PartOfSpeech lPos,
				PartOfSpeech rPos/*, Set<WordNetRelation> relations*/) {
			this.lLemma = lLemma;
			this.rLemma = rLemma;
			this.lPos = lPos;
			this.rPos = rPos;
			//this.relations = relations;
		}
		public String lLemma, rLemma;
		public PartOfSpeech lPos, rPos;
		//public Set<WordNetRelation> relations;
	}
	
	private static class FullRulesQuery {
		@Override public int hashCode() {
		     return new HashCodeBuilder(17, 37).append(relations).append(length).append(synsetScope).append(basicQuery).toHashCode();
		}
		@Override public boolean equals(Object obj) {
		   if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) { return false; }
		   FullRulesQuery rhs = (FullRulesQuery) obj;
		   return new EqualsBuilder().append(relations, rhs.relations).append(length, rhs.length).append(synsetScope, rhs.synsetScope).append(basicQuery, rhs.basicQuery).isEquals();
		}
		public Set<WordNetRelation> relations;
		public int length;
		//public Derivation derv;
		public SynsetScope synsetScope;
		//public LengthType lenType;
		public BasicRulesQuery basicQuery; xxx
	}
	
	private static Boolean isOverlappingSynsets(String textLemma, PartOfSpeech textPos, String specLemma, PartOfSpeech specPos) throws WordNetException {
		BasicRulesQuery query = new BasicRulesQuery(textLemma, specLemma, textPos, specPos);
		Boolean result = null;//cacheOverlappingSynsets.get(query); //TODO commented this due to memory issues
		if (result == null) {
			WordNetPartOfSpeech textWnPos = WordNetPartOfSpeech.toWordNetPartOfspeech(textPos);
			WordNetPartOfSpeech specWnPos = WordNetPartOfSpeech.toWordNetPartOfspeech(specPos);

			if (textWnPos==null || specWnPos==null) {
				result = false;
				////System.err.printf("Wordnet.SameSynset: Got WordNetPartOfSpeech==null for %s or %s\n", textPos, specPos);
			}
			else {
				Set<Synset> textSynsets = getSynsetsOf(textLemma, textWnPos);
				Set<Synset> specSynsets = getSynsetsOf(specLemma, specWnPos);
				
				result = !Collections.disjoint(textSynsets, specSynsets);
				//DEBUG
				if (result) {
					//System.err.printf("Wordnet.SameSynset: TRUE! (%s,%s)\n", textLemma, specLemma); //TODO commented this due to memory issues
				}				
			}
			//cacheOverlappingSynsets.put(query, result); //TODO commented this due to memory issues
		}
		return result;
	}
	
	private static Set<Synset> getSynsetsOf(String text, WordNetPartOfSpeech pos) throws WordNetException {
		Entry<String, WordNetPartOfSpeech> query = new AbstractMap.SimpleEntry<String, WordNetPartOfSpeech>(text, pos);
		Set<Synset> result = cacheSynset.get(query);
		if (result == null) {
			result = dictionary.getSynsetsOf(text, pos);
			cacheSynset.put(query, result);
			if (result.isEmpty()) {
				System.err.printf("WordNetSignalMechanism: Empty Synset for: '%s' (pos=%s)\n", text, pos);
			}
		}
		return result;
	}
//	private WordnetLexicalResource getResourceByChain(int chainingLength) {
//		switch (chainingLength) {
//		case 1: return resource_chain1;
//		case 2: return resource_chain2;
//		case 3: return resource_chain3;
//		default: throw new IllegalArgumentException("Wordnet chaining length must be 1/2/3, got: " + chainingLength);
//		}
//	}
	
	private static List<LexicalRule<? extends WordnetRuleInfo>> getRules(
			String title, boolean printRelations, WordnetLexicalResource resource,
			Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cache, Set<WordNetRelation> relations, 
			String textLemma, PartOfSpeech textPos, String specLemma, PartOfSpeech specPos) throws LexicalResourceException {
		BasicRulesQuery query = new BasicRulesQuery(textLemma, specLemma, textPos, specPos);
		List<LexicalRule<? extends WordnetRuleInfo>> result = cache.get(query);
		if (result == null) {
			result = resource.getRules(textLemma, textPos, specLemma, specPos, relations, null);
			cache.put(query, result);
			
			if (!result.isEmpty()) {
				String extra = "";
				if (printRelations) {
					List<WordNetRelation> ruleRelations = new ArrayList<WordNetRelation>(result.size());
					for (LexicalRule<? extends WordnetRuleInfo> rule : result) {
						ruleRelations.add(rule.getInfo().getTypedRelation());
					}
					extra = String.format(" (%d: %s)", result.size(), ruleRelations);
				}
				System.err.printf("%s: \tTRUE! %s-->%s%s\n", title, textLemma, specLemma, extra);
			}
		}
		return result;	

	}
	
	private static List<LexicalRule<? extends WordnetRuleInfo>> getDefaultRules(String textLemma, PartOfSpeech textPos, String specLemma, PartOfSpeech specPos, int chainingLength) throws LexicalResourceException {
		WordnetLexicalResource resource = resourceMap.get(chainingLength);
		Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cache = cahceDefaultMap.get(chainingLength);
		String title = String.format("Wordnet.Default(chain=%s)", chainingLength);
		return getRules(title, true, resource, cache, DEFAULT_RELATIONS, textLemma, textPos, specLemma, specPos);
	}
	private static List<LexicalRule<? extends WordnetRuleInfo>> getHypernymRules(String textLemma, PartOfSpeech textPos, String specLemma, PartOfSpeech specPos, int chainingLength) throws LexicalResourceException {
		/// DEBUG
//		if ((textLemma.equals("wage") && specLemma.equals("fight")) ||
//			(textLemma.equals("invade") && specLemma.equals("attack"))) {
//			System.out.println("Pausing!!!!");
//		}
		////
		
		WordnetLexicalResource resource = resourceMap.get(chainingLength);
		Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cache = cacheHypernymMap.get(chainingLength);
		String title = String.format("Wordnet.Hypernym(chain=%s)", chainingLength);
		return getRules(title, false, resource, cache, HYPERNYM_RELATION, textLemma, textPos, specLemma, specPos);
	}
	private static List<LexicalRule<? extends WordnetRuleInfo>> getDerivationallyRelatedRules(String textLemma, PartOfSpeech textPos, String specLemma, PartOfSpeech specPos, int chainingLength) throws LexicalResourceException {
		WordnetLexicalResource resource = resourceMap.get(chainingLength);
		Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cache = cahceDerivationallyRelatedMap.get(chainingLength);
		String title = String.format("Wordnet.DervRltd(chain=%s)", chainingLength);
		return getRules(title, false, resource, cache, DERVRTD_RELATION, textLemma, textPos, specLemma, specPos);
	}
	
	private static WordnetLexicalResource resource_chain1;
	private static WordnetLexicalResource resource_chain2;
	private static WordnetLexicalResource resource_chain3;
	private static WordnetLexicalResource resource_chain4;
	private static WordnetLexicalResource resource_chain5;
	private static Dictionary dictionary;
	
	private static Map<Entry<String, WordNetPartOfSpeech>, Set<Synset>> cacheSynset = new HashMap<Entry<String, WordNetPartOfSpeech>, Set<Synset>>();
	private static Map<BasicRulesQuery, Boolean> cacheOverlappingSynsets = new HashMap<BasicRulesQuery, Boolean>();
	private static Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceDefaultRules_1 = new HashMap<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	private static Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceDefaultRules_2 = new HashMap<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	private static Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceDefaultRules_3 = new HashMap<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	private static Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceDefaultRules_4 = new HashMap<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	private static Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceDefaultRules_5 = new HashMap<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	private static Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceHypernymRules_1 = new HashMap<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	private static Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceHypernymRules_2 = new HashMap<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	private static Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceHypernymRules_3 = new HashMap<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	private static Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceHypernymRules_4 = new HashMap<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	private static Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceHypernymRules_5 = new HashMap<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	private static Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceDerivationallyRelatedRules_1 = new HashMap<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	private static Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceDerivationallyRelatedRules_2 = new HashMap<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	private static Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceDerivationallyRelatedRules_3 = new HashMap<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	
	private static Map<Integer, WordnetLexicalResource> resourceMap;
	private static Map<Integer, Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>> cahceDefaultMap;
	private static Map<Integer, Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>> cacheHypernymMap;
	private static Map<Integer, Map<BasicRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>> cahceDerivationallyRelatedMap;
	
	private static final String WORDNET_DIR_PATH = "src/main/resources/data/Wordnet3.0";
	private static final File WORDNET_DIR = new File(WORDNET_DIR_PATH);
//	private static final Boolean USE_FIRST_SENSE_ONLY_LEFT = true;
//	private static final Boolean USE_FIRST_SENSE_ONLY_RIGHT = true;
	//private static final Integer CHAINING_LENGTH = 1;
	private static final Set<WordNetRelation> DEFAULT_RELATIONS = new LinkedHashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.SYNONYM,
			WordNetRelation.DERIVATIONALLY_RELATED,
			WordNetRelation.HYPERNYM,
			WordNetRelation.INSTANCE_HYPERNYM,
			WordNetRelation.MEMBER_HOLONYM,
			WordNetRelation.PART_HOLONYM,
			WordNetRelation.ENTAILMENT,
			WordNetRelation.SUBSTANCE_MERONYM
	}));
	private static final Set<WordNetRelation> HYPERNYM_RELATION = new LinkedHashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.HYPERNYM
	}));
	private static final Set<WordNetRelation> DERVRTD_RELATION = new LinkedHashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.DERIVATIONALLY_RELATED
	}));
}
