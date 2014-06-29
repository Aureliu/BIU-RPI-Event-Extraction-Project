package edu.cuny.qc.scorer.mechanism;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Period;

import ac.biu.nlp.nlp.ie.onthefly.input.AnnotationUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.perceptron.core.Pipeline;
import edu.cuny.qc.scorer.Aggregator;
import edu.cuny.qc.scorer.BasicRulesQuery;
import edu.cuny.qc.scorer.Derivation;
import edu.cuny.qc.scorer.Deriver;
import edu.cuny.qc.scorer.DeriverException;
import edu.cuny.qc.scorer.FullRulesQuery;
import edu.cuny.qc.scorer.Juxtaposition;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.SignalMechanismSpecTokenIterator;
import edu.cuny.qc.scorer.Deriver.NoDerv;
import edu.cuny.qc.util.TokenAnnotations.DervWordnetAnnotation;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech.PennPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfoWithSenseNumsOnly;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwi.JwiDictionary;

public class WordNetSignalMechanism extends SignalMechanism {

	static {
		System.err.println("??? WordNetSignalMechanism: Fake Signal Mechanisms");
		System.err.println("??? WordNetSignalMechanism: ignoring spec's POS, using only text's");
		System.err.println("??? WordNetSignalMechanism: if a word has a non-wordnet POS (anything but noun/verb/adj/adv) we return FALSE, but we should return IRRELEVANT (when I figure out what it means... :( )");
		System.err.println("??? WordNetSignalMechanism: if a text or spec doesn't exist in wordnet, we return FALSE, although we should return IRRELEVANT");
		System.err.println("??? WordNetSignalMechanism: We want to duplicate signals for different POSes (learn different weights), but currently can't, since we don't support (yet) IRRELEVANT");
		System.err.println("??? WordNetSignalMechanism: get*Cousins methdos return lemmas, and when a lemma is MWE then there are '_' between tokens. Maybe I should address that somehow, like turn all '_' into space. But does it ever happen that I get a query with MWE?");
		System.err.println("??? WordNetSignalMechanism: Probably won't solve and it's just for documentation: when I get a DervRelated, I'm not getting just lemmas, I'm getting synsets (actually, a single term from some synset). So when I continue calculation, techincally the most accurate thing to do would have been to use only this synset as LHS, and not first/all synsets of the lemma.");
		System.err.println("??? WordNetSignalMechanism: When getting DervRelated, I only take words related to my lemma, and not to other lemmas in its synset (that's what I automatically get from the resource, and what makes somewhat more sense). I don't think this behavior needs changing - using all synset sounds a bit too noisy.");
	}

//	public WordNetSignalMechanism() throws UnsupportedPosTagStringException, WordNetInitializationException {
//		super();
//		dictionary = new JwiDictionary(WORDNET_DIR);
//		
//		NOUN = new PennPartOfSpeech(PennPosTag.NN);
//		VERB = new PennPartOfSpeech(PennPosTag.VB);
//		ADJ = new PennPartOfSpeech(PennPosTag.JJ);
//		ADV = new PennPartOfSpeech(PennPosTag.RB);
//	}
//	
	public WordNetSignalMechanism() throws SignalMechanismException {
		super();
	}
	
	@Override
	public void init() throws WordNetInitializationException, UnsupportedPosTagStringException {
		dictionary = new JwiDictionary(WORDNET_DIR);
		
		NOUN = new PennPartOfSpeech(PennPosTag.NN);
		VERB = new PennPartOfSpeech(PennPosTag.VB);
		ADJ = new PennPartOfSpeech(PennPosTag.JJ);
		ADV = new PennPartOfSpeech(PennPosTag.RB);

	}
	
	@Override
	public void close() {
		dictionary.close();
		for (WordnetLexicalResource resource : WordnetScorer.cacheResources.asMap().values()) {
			resource.close();
		}
		super.close();
	}


	@Override
	public void addScorers() {
		//END of analysis1!
		/// Group A
		addTriggers(SYNONYM_RELATION,   Juxtaposition.ANCESTOR, new Integer[] {1}, DERVS_NONE, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, /*VERB, ADJ, ADV*/}, AGG_ANY);
		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.ANCESTOR, ALL_LENGTHS_WITH_TOP, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN/*, VERB*/}, AGG_ANY);
		addTriggers(HYPERNYM1_RELATION, Juxtaposition.ANCESTOR, ALL_LENGTHS_WITH_TOP, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN/*, VERB*/}, AGG_ANY);
		addTriggers(HYPERNYM2_RELATION, Juxtaposition.ANCESTOR, ALL_LENGTHS_WITH_TOP, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN/*, VERB*/}, AGG_ANY);

		/// Group B
		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, new Integer[] {1}, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null/*, NOUN, VERB*/}, AGG_ANY_MIN2);
		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, new Integer[] {2, 3}, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null/*, NOUN, VERB*/}, AGG_MIN2_MIN3);

		addTriggers(ALL_RELATIONS_SMALL,   Juxtaposition.ANCESTOR, LENGTHS_1_2_3_TOP, DERVS_ALL, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, VERB/*, ADJ, ADV*/}, ALL_AGGS);
		addTriggers(ALL_RELATIONS_BIG,   Juxtaposition.ANCESTOR, LENGTHS_1_2_3_TOP, DERVS_ALL, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, VERB/*, ADJ, ADV*/}, ALL_AGGS);
		
		
		
		
		///////////////////////////////
//		addTriggers(SYNONYM_RELATION,   Juxtaposition.ANCESTOR, new Integer[] {1}, DERVS_NONE_ONLY, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, VERB/*, ADJ, ADV*/}, AGG_ANY_MIN2);
//
//		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.ANCESTOR, ALL_LENGTHS_WITH_TOP, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, VERB}, AGG_ANY);
//		//addTriggers(HYPERNYM_RELATIONS, Juxtaposition.ANCESTOR, ALL_LENGTHS_WITH_TOP, DERVS_ONLY,     SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, ADJ, ADV},   ALL_AGGS);
//		
//		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, LENGTHS_1_2_3_TOP, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null/*, NOUN, VERB*/}, AGG_ANY);
//		//addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, ALL_LIMITED_LENGTHS, DERVS_ONLY,     SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, ADJ, ADV},   ALL_AGGS);
//		
//		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_LOOSE, LENGTHS_1_2_3, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null/*, NOUN, VERB*/}, AGG_ANY);
//		//addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_LOOSE, ALL_LIMITED_LENGTHS, DERVS_ONLY,     SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, ADJ, ADV},   ALL_AGGS);
		///////////////////////
		
		
		
		
//		addTriggers(SYNONYM_RELATION,   Juxtaposition.ANCESTOR, new Integer[] {1}, DERVS_NONE_ONLY, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, VERB, ADJ, ADV}, ALL_AGGS);
//
//		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.ANCESTOR, ALL_LENGTHS_WITH_TOP, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, VERB}, ALL_AGGS);
//		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.ANCESTOR, ALL_LENGTHS_WITH_TOP, DERVS_ONLY,     SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, ADJ, ADV},   ALL_AGGS);
//		
//		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, ALL_LIMITED_LENGTHS, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, VERB}, ALL_AGGS);
//		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, ALL_LIMITED_LENGTHS, DERVS_ONLY,     SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, ADJ, ADV},   ALL_AGGS);
//		
//		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_LOOSE, ALL_LIMITED_LENGTHS, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, VERB}, ALL_AGGS);
//		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_LOOSE, ALL_LIMITED_LENGTHS, DERVS_ONLY,     SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, ADJ, ADV},   ALL_AGGS);
//		
		
		
//		addTrigger(new ScorerData("WN_SYNSET",			SameSynset.inst,			Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_HYPERNYM_1",		IsSpecHypernym_1.inst,		Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_HYPERNYM_2",		IsSpecHypernym_2.inst,		Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_HYPERNYM_2",		IsSpecHypernym_2.inst,		Aggregator.Min2.inst		));
//		addTrigger(new ScorerData("WN_HYPERNYM_3",		IsSpecHypernym_3.inst,		Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_HYPERNYM_4",		IsSpecHypernym_4.inst,		Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_HYPERNYM_5",		IsSpecHypernym_5.inst,		Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_HYPERNYM_5",		IsSpecHypernym_5.inst,		Aggregator.Min2.inst		));
//		addTrigger(new ScorerData("WN_DERV_RELATED",	IsSpecDervRelated.inst,		Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_ENTAILED_1",		IsSpecEntailed_1.inst,		Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_ENTAILED_1",		IsSpecEntailed_1.inst,		Aggregator.Min2.inst	));
//		addTrigger(new ScorerData("WN_ENTAILED_2",		IsSpecEntailed_2.inst,		Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_ENTAILED_2",		IsSpecEntailed_2.inst,		Aggregator.Min2.inst		));
//		addTrigger(new ScorerData("WN_ENTAILED_3",		IsSpecEntailed_3.inst,		Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_ENTAILED_4",		IsSpecEntailed_4.inst,		Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_ENTAILED_5",		IsSpecEntailed_5.inst,		Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_ENTAILED_5",		IsSpecEntailed_5.inst,		Aggregator.Min2.inst		));
//		addTrigger(new ScorerData("WN_SYNSET|WN_HYPERNYM_2",	new Compose.Or(SameSynset.inst, IsSpecHypernym_2.inst),	Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_SYNSET|WN_HYPERNYM_4",	new Compose.Or(SameSynset.inst, IsSpecHypernym_4.inst),	Aggregator.Any.inst		));
//		addTrigger(new ScorerData("WN_SYNSET|WN_HYPERNYM_4",	new Compose.Or(SameSynset.inst, IsSpecHypernym_4.inst),	Aggregator.Min2.inst	));
//
//		//addTrigger(new ScorerData("FAKE_PREKNOWN_TRIGGERS",				PreknownTriggers.inst,			Aggregator.Any.inst));
//		
//		addArgument(new ScorerData("xxWN_SYNSET",		SameSynset.inst,			Aggregator.Any.inst		));
//		addArgument(new ScorerData("xxWN_HYPERNYM_1",	IsSpecHypernym_1.inst,		Aggregator.Any.inst		));
//		addArgument(new ScorerData("xxWN_ENTAILED_1",	IsSpecEntailed_1.inst,		Aggregator.Any.inst		));
	}

	public static String cacheStats(LoadingCache<?,?> cache) {
		CacheStats stats = cache.stats();
		Period totalLoadTime = new Period(Math.round(stats.totalLoadTime()/1000000));
		String averageLoadPenalty;
		double d = stats.averageLoadPenalty()/1000000;
		if (d<1000) {
			averageLoadPenalty = String.format("%10.3fms", d);
		}
		else {
			averageLoadPenalty = String.format("%12s", new Period(Math.round(d)));
		}
		return String.format("size=%8s, hitCount=%12s, missCount=%12s, hitRate=%.4f, exc=%d, evictionCount=%12d, totalLoadTime=%15s, averageLoadPenalty=%s",
				cache.size(), stats.hitCount(), stats.missCount(), stats.hitRate(), stats.loadExceptionCount(), stats.evictionCount(),
				totalLoadTime, averageLoadPenalty);
	}
	
	public void addTriggers(Set<WordNetRelation> relations, Juxtaposition juxt, Integer[] lengths, Derivation[] dervs, Integer[] leftSenses, Integer[] rightSenses, PartOfSpeech[] specificPoses, Aggregator[] aggs) {
		System.out.printf("%s ^^^ addTriggers(): Building %s (%sx%sx%sx%sx%sx%s) combinations: lengths=%s, dervs=%s, leftSenses=%s, rightSenses=%s, specificPoses=%s, aggs=%s. Fixed options: relations=%s, juxt=%s\n",
				Pipeline.detailedLog(),
				lengths.length*dervs.length*leftSenses.length*rightSenses.length*specificPoses.length*aggs.length,
				lengths.length, dervs.length, leftSenses.length, rightSenses.length, specificPoses.length, aggs.length,
				Arrays.toString(lengths), Arrays.toString(dervs), Arrays.toString(leftSenses), Arrays.toString(rightSenses), Arrays.toString(specificPoses), Arrays.toString(aggs),
				relations, juxt);
		for (Integer length : lengths) {
			for (Derivation derv : dervs) {
				for (Integer leftSense : leftSenses) {
					for (Integer rightSense : rightSenses) {
						for (PartOfSpeech specificPos : specificPoses) {
							for (Aggregator agg : aggs) {
//								System.out.printf("%s ^^^^^ addTriggers(): Starting single combination: relations=%s, just=%s, length=%s, derv=%s, leftSense=%s, rightSense=%s, specificPos=%s, agg=%s\n",
//										Pipeline.detailedLog(),
//										relations, juxt, length, derv, leftSense, rightSense, specificPos, agg);
								
								WordnetScorer scorer = new WordnetScorer(relations, juxt, length, derv, leftSense, rightSense, specificPos);
								addTrigger(new ScorerData(scorer, agg));
							}
						}
					}
				}
			}
		}
	}
	
	public void logCacheStats() {
		System.out.printf("@@ Exist:        %s\n", cacheStats(WordnetScorer.cacheExist));
		System.out.printf("@@ CousinsLoose: %s\n", cacheStats(WordnetScorer.cacheCousinsLoose));
		System.out.printf("@@ CousinsStrict:%s\n", cacheStats(WordnetScorer.cacheCousinsStrict));
		System.out.printf("@@ Rules:        %s\n", cacheStats(WordnetScorer.cacheRules));
		System.out.printf("@@ Bools:        %s\n", cacheStats(WordnetScorer.cacheBools));
		System.out.printf("@@ Derv:         %s\n", cacheStats(WordnetScorer.cacheDerv));
		System.out.printf("@@ Resources:    %s\n", cacheStats(WordnetScorer.cacheResources));
	}
	
	@Override
	public void logPreSentence() {
		if (DO_SENTENCE_LOGGING) {
			if (usedCaches) {
				System.out.printf("%s @@@ PreSentence cache stats:\n", Pipeline.detailedLog());
				logCacheStats();
			}
			else {
				if (!reportedNoCaches) {
					System.out.printf("@@@ PreSentence cache - nothing to print, haven't used caches yet!\n");
					reportedNoCaches = true;
				}
			}
		}
	}
	@Override
	public void logPreDocument() {
		System.out.printf("%s @@@@ PreDocument cache stats:\n", Pipeline.detailedLog());
		logCacheStats();
	}
	@Override
	public void logPreDocumentBunch() {
		System.out.printf("%s @@@@@ PreDocumentBunch cache stats:\n", Pipeline.detailedLog());
		logCacheStats();
	}

	private static LoadingCache<Integer, WordnetLexicalResource> cacheResources = CacheBuilder.newBuilder()
			.maximumSize(50)
			.build(new CacheLoader<Integer, WordnetLexicalResource>() {
				public WordnetLexicalResource load(Integer length) throws LexicalResourceException {
					WordnetLexicalResource result = new WordnetLexicalResource(WORDNET_DIR,	false, false, null, length);
					return result;
				}
			});


	//	private enum SynsetScope {
//		BOTH_FIRSTS(1,1),
//		TEXT_ALL_SPEC_FIRST(-1,1),
//		TEXT_FIRST_SPEC_ALL(1,-1),
//		BOTH_ALL(-1,-1);
//		private SynsetScope(int leftSenseNum, int rightSenseNum) {
//			this.leftSenseNum = leftSenseNum;
//			this.rightSenseNum = rightSenseNum;
//		}
//		public int leftSenseNum, rightSenseNum;
//	}
//	private enum LengthType {
//		FIXED,
//		RELATIVE
//	}
	
	public static class WordnetDervRltdDeriver extends Deriver {
		public static final WordnetDervRltdDeriver inst = new WordnetDervRltdDeriver();
		@Override public String getSuffix() { return "-WnDrv";}
		//@Override public Class<?> getTokenAnnotationMarker() { return DervWordnetAnnotation.class; }
		@Override public Set<BasicRulesQuery> buildDerivations(FullRulesQuery query) throws DeriverException {
			try {
				return cacheDerv.get(query);
			} catch (ExecutionException e) {
				throw new DeriverException(e);
			}
		}
		
		private static LoadingCache<FullRulesQuery, Set<BasicRulesQuery>> cacheDerv = CacheBuilder.newBuilder()
				.recordStats()
				.maximumWeight(100000)
				.weigher(new Weigher<FullRulesQuery, Set<BasicRulesQuery>>() {
					public int weigh(FullRulesQuery k, Set<BasicRulesQuery> v) { return v.size(); }
				})
				.build(new CacheLoader<FullRulesQuery, Set<BasicRulesQuery>>() {
					public Set<BasicRulesQuery> load(FullRulesQuery key) throws LexicalResourceException, ExecutionException {
						Set<BasicRulesQuery> result = new HashSet<BasicRulesQuery>();
						BasicRulesQuery q = key.basicQuery;
						WordnetLexicalResource resource = cacheResources.get(1);
						
						// Take all derv-related forms (that's the -1)
						WordnetRuleInfo info = new WordnetRuleInfoWithSenseNumsOnly(key.leftSenseNum, -1);
						
						List<LexicalRule<? extends WordnetRuleInfo>> rules = resource.getRulesForLeft(q.lLemma, q.lPos, DERVRTD_RELATION, info);
						for (LexicalRule<? extends WordnetRuleInfo> rule : rules) {
							// when a BasicRulesQuery represents only one lemma/POS, it's always on the Left side
							BasicRulesQuery res = new BasicRulesQuery(rule.getRLemma(), null, rule.getRPos(), null);
							result.add(res);
						}
						return result;
					}
				});
	}
	
	private static class WordnetScorer extends SignalMechanismSpecTokenIterator {
		public Set<WordNetRelation> relations;
		public Juxtaposition juxt;
		public int length;
		public Derivation derv; 
		//public SynsetScope synsetScope;
		public int leftSenseNum;
		public int rightSenseNum;
		public PartOfSpeech specificPos;
//		public LengthType lenType;
				
		public WordnetScorer(Set<WordNetRelation> relations,
				Juxtaposition juxt, int length, Derivation derv,
				int leftSenseNum, int rightSenseNum, PartOfSpeech specificPos) {
			this.relations = relations;
			this.juxt = juxt;
			this.length = length;
			this.derv = derv;
			this.leftSenseNum = leftSenseNum;
			this.rightSenseNum = rightSenseNum;
			this.specificPos = specificPos;
		}

		@Override public String getTypeName() {
			String rels;
			if (this.relations.equals(ALL_RELATIONS_BIG)) {
				rels = "AllRelsBig";
			}
			else if (this.relations.equals(ALL_RELATIONS_SMALL)) {
				rels = "AllRelsSmall";
			}
			else {
				rels = StringUtils.join(this.relations, "_");
			}
			
			String lengthStr = "" + length;
			if (length == -1) {
				lengthStr = "Top";
			}
			
			String leftSenseStr="" + leftSenseNum, rightSenseStr="" + rightSenseNum;
			if (leftSenseNum==-1) {
				leftSenseStr = "All";
			}
			if (rightSenseNum==-1) {
				rightSenseStr = "All";
			}
			
			String dervStr="";
			if (derv==Derivation.TEXT_ORIG_AND_DERV) {
				dervStr = "_withTextDerv";
			}
			else if (derv==Derivation.TEXT_ONLY_DERV) {
				dervStr = "_onlyTextDerv";
			}
			else if (derv==Derivation.SPEC_ORIG_AND_DERV) {
				dervStr = "_withSpecDerv";
			}
			else if (derv==Derivation.SPEC_ONLY_DERV) {
				dervStr = "_onlySpecDerv";
			}
			
			String posStr="";
			if (specificPos != null) {
				posStr = String.format("_just%s", specificPos);
			}
			
			return String.format("WN__%s__%s_Len%s_Text%sSense_Spec%sSense_%s%s",
					rels, juxt, lengthStr, leftSenseStr, rightSenseStr, dervStr, posStr);
		}

		private static LoadingCache<BasicRulesQuery, Boolean> cacheExist = CacheBuilder.newBuilder()
				.recordStats()
				.maximumSize(10000000)
				.build(new CacheLoader<BasicRulesQuery, Boolean>() {
					public Boolean load(BasicRulesQuery key) throws WordNetException {
						WordNetPartOfSpeech lWnPos = WordNetPartOfSpeech.toWordNetPartOfspeech(key.lPos);
						int synsets = dictionary.getNumberOfSynsets(key.lLemma, lWnPos);
						return synsets > 0;
					}
				});

		private static LoadingCache<FullRulesQuery, Set<String>> cacheCousinsLoose = CacheBuilder.newBuilder()
				.recordStats()
				.maximumWeight(2000000000)
				.weigher(new Weigher<FullRulesQuery, Set<String>>() {
					public int weigh(FullRulesQuery k, Set<String> v) { return v.size(); }
				})
				.build(new CacheLoader<FullRulesQuery, Set<String>>() {
					public Set<String> load(FullRulesQuery key) throws LexicalResourceException, WordNetException {
						BasicRulesQuery q = key.basicQuery;
						WordNetPartOfSpeech lWnPos = WordNetPartOfSpeech.toWordNetPartOfspeech(q.lPos);
						Set<String> result;
						if (key.leftSenseNum == 1) {
							//if (dictionary.getNumberOfSynsets(q.lLemma, lWnPos)!=0) {
								result = dictionary.getLooseCousinTerms(q.lLemma, lWnPos, 1, key.length);
							//}
							//else {
							//	result = new HashSet<String>();  //TODO: should be: IRRELEVANT
							//}
						}
						else {
							result = dictionary.getLooseCousinTerms(q.lLemma, lWnPos, key.length);
						}
						return result;
					}
				});
		private static LoadingCache<FullRulesQuery, Set<String>> cacheCousinsStrict = CacheBuilder.newBuilder()
				.recordStats()
				.maximumWeight(2000000000)
				.weigher(new Weigher<FullRulesQuery, Set<String>>() {
					public int weigh(FullRulesQuery k, Set<String> v) { return v.size(); }
				})
				.build(new CacheLoader<FullRulesQuery, Set<String>>() {
					public Set<String> load(FullRulesQuery key) throws LexicalResourceException, WordNetException {
						BasicRulesQuery q = key.basicQuery;
						WordNetPartOfSpeech lWnPos = WordNetPartOfSpeech.toWordNetPartOfspeech(q.lPos);
						Set<String> result;
						if (key.leftSenseNum == 1) {
							//if (dictionary.getNumberOfSynsets(q.lLemma, lWnPos)!=0) {
								result = dictionary.getStrictCousinTerms(q.lLemma, lWnPos, 1, key.length);
							//}
							//else {
							//	result = new HashSet<String>();  //TODO: should be: IRRELEVANT
							//}
						}
						else {
							result = dictionary.getStrictCousinTerms(q.lLemma, lWnPos, key.length);
						}
						return result;
					}
				});
		
		private static LoadingCache<FullRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cacheRules = CacheBuilder.newBuilder()
				.recordStats()
				.maximumWeight(5000  /*100000*/)
				.weigher(new Weigher<FullRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>() {
					public int weigh(FullRulesQuery k, List<LexicalRule<? extends WordnetRuleInfo>> v) { return v.size(); }
				})
				.build(new CacheLoader<FullRulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>() {
					public List<LexicalRule<? extends WordnetRuleInfo>> load(FullRulesQuery key) throws LexicalResourceException, WordNetException, ExecutionException {
						BasicRulesQuery q = key.basicQuery;
						WordnetRuleInfo info = new WordnetRuleInfoWithSenseNumsOnly(key.leftSenseNum, key.rightSenseNum);
						WordnetLexicalResource resource = cacheResources.get(key.length);
						List<LexicalRule<? extends WordnetRuleInfo>> rules = resource.getRules(q.lLemma, q.lPos, q.rLemma, q.rPos, key.relations, info);
						return rules;
					}
				});
		
		private static LoadingCache<FullRulesQuery, Boolean> cacheBools = CacheBuilder.newBuilder()
				.recordStats()
				.maximumSize(7500000)
				.build(new CacheLoader<FullRulesQuery, Boolean>() {
					public Boolean load(FullRulesQuery key) throws LexicalResourceException, ExecutionException {
						BasicRulesQuery q = key.basicQuery;
						String extra = "";
						Boolean booleanScore;
						if (key.juxt == Juxtaposition.ANCESTOR) {
							List<LexicalRule<? extends WordnetRuleInfo>> rules = cacheRules.get(key);
							booleanScore = !rules.isEmpty();
							// because bottom part is commented out
//							if (booleanScore) {
//								List<WordNetRelation> ruleRelations = new ArrayList<WordNetRelation>(rules.size());
//								for (LexicalRule<? extends WordnetRuleInfo> rule : rules) {
//									ruleRelations.add(rule.getInfo().getTypedRelation());
//								}
//								extra = String.format(", relations=%s", ruleRelations);
//							}
						}
						else if (key.juxt == Juxtaposition.COUSIN_STRICT || key.juxt == Juxtaposition.COUSIN_LOOSE) {
							//TODO: this is messy, with the different kinds of hypernyms...
							if (!key.relations.equals(HYPERNYM_RELATIONS)) {
								throw new IllegalArgumentException("juxt=COUSIN_*, but relations is not exactly HYPERNYM. relations=" + key.relations);
							}

							BasicRulesQuery basicQueryOnlyLeft = new BasicRulesQuery(q.lLemma, null, q.lPos, null);
							FullRulesQuery keyOnlyLeft = new FullRulesQuery(null, key.length, null, key.leftSenseNum, key.rightSenseNum, basicQueryOnlyLeft);
							Set<String> lemmas;
							if (key.juxt == Juxtaposition.COUSIN_STRICT) {
								lemmas = cacheCousinsStrict.get(keyOnlyLeft);

							}
							else {
								lemmas = cacheCousinsLoose.get(keyOnlyLeft);

							}
							booleanScore = lemmas.contains(q.rLemma);
						}
						else {
							throw new IllegalArgumentException("juxt=" + key.juxt);
						}

						//Ofer: not that useful, takes a lot of space and time
//						if (booleanScore) {
//							System.err.printf("Wordnet: TRUE! %s/%s-->%s/%s\t(juxt=%s, length=%s, leftSenseNum=%s, rightSenseNum=%s%s)\n",
//									q.lLemma, q.lPos, q.rLemma, q.rPos, key.juxt, key.length, key.leftSenseNum, key.rightSenseNum, extra);
//						}
						
			            return booleanScore;
					}
				});
		
		@Override
		public Boolean calcTokenBooleanScore(Token text, Map<Class<?>, Object> textTriggerTokenMap, Token spec) throws SignalMechanismException {
			try {
				//hard-codedly, specificPos only refers to the original text token, not any derivations
				if (this.specificPos != null && !this.specificPos.equals(textPos)) {
					return false; //TODO: should be: IRRELEVANT
				}			
				WordNetPartOfSpeech textWnPos = WordNetPartOfSpeech.toWordNetPartOfspeech(textPos);
				if (textWnPos == null) {
					return false;  //TODO: should be: IRRELEVANT
				}				
				
				usedCaches = true;
				String textLemma = text.getLemma().getValue();
				BasicRulesQuery leftQuery = new BasicRulesQuery(textLemma, null, textPos, null); // when a BasicRulesQuery represents only one lemma/POS, it's always on the Left side
				if (!cacheExist.get(leftQuery)) {
					return false;  //TODO: should be: IRRELEVANT
				}
				
				PartOfSpeech specPos = textPos; //spec is always as having the same POS as text, mostly since wordnet doesn't (hardly) have and cross-POS-relations 
				String specLemma = spec.getLemma().getValue();
				BasicRulesQuery rightQuery = new BasicRulesQuery(specLemma, null, specPos, null); // when a BasicRulesQuery represents only one lemma/POS, it's always on the Left side
				if (!cacheExist.get(rightQuery)) {
					return false;  //TODO: should be: IRRELEVANT
				}
				
				int realLength = length;
				if (length == -1) {
					// Go all the way up the wordnet tree
					realLength = MAXIMUM_WORDNET_LENGTH;
				}
				
				Set<BasicRulesQuery> leftSides = new HashSet<BasicRulesQuery>(3);
				if (this.derv.leftOriginal) {
					leftSides.add(leftQuery);
				}
				Set<BasicRulesQuery> rightSides = new HashSet<BasicRulesQuery>(3);
				if (this.derv.rightOriginal) {
					rightSides.add(rightQuery);
				}

				// Note that we always put stuff on the left side. This way cacheDerv doesn't need to know on which real side it works on.
				// Also, cacheDerv itself puts everything in the left side, so that we don't need to know later if there were any derivations or not 
				if (this.derv.leftDerivation) {
					FullRulesQuery dervQuery = new FullRulesQuery(null, 0, null, this.leftSenseNum, 0, leftQuery);
					leftSides.addAll(cacheDerv.get(dervQuery));
				}
				if (this.derv.rightDerivation) {
					FullRulesQuery dervQuery = new FullRulesQuery(null, 0, null, this.rightSenseNum, 0, rightQuery);
					rightSides.addAll(cacheDerv.get(dervQuery));
				}
				
				// Our queries are all combinations of left sides and right sides
				// This is hard-coded "Any" mode - when we get one True, we stop and return true
				boolean result = false;
				for (BasicRulesQuery leftSide : leftSides) {
					for (BasicRulesQuery rightSide : rightSides) {
						BasicRulesQuery basicQuery = new BasicRulesQuery(leftSide.lLemma, rightSide.lLemma, leftSide.lPos, rightSide.lPos);
						FullRulesQuery fullQuery = new FullRulesQuery(this.relations, realLength, this.juxt, this.leftSenseNum, this.rightSenseNum, basicQuery);
						result = cacheBools.get(fullQuery);
						if (result) {
							break;
						}
					}
					if (result) {
						break;
					}
				}
				
				return result;
				
			} catch (UnsupportedPosTagStringException e) {
				throw new SignalMechanismException(e);
			} catch (ExecutionException e) {
				throw new SignalMechanismException(e);
			}
		}
	}
	
	private static JwiDictionary dictionary;
	private static boolean usedCaches = false;
	private static boolean reportedNoCaches = false;
	private static boolean DO_SENTENCE_LOGGING = false;
	
	private static final String WORDNET_DIR_PATH = "src/main/resources/data/Wordnet3.0";
	private static final File WORDNET_DIR = new File(WORDNET_DIR_PATH);
	private static final int MAXIMUM_WORDNET_LENGTH = 100;
	
	private static final Set<WordNetRelation> ALL_RELATIONS_SMALL = new LinkedHashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.SYNONYM,
			WordNetRelation.DERIVATIONALLY_RELATED,
			WordNetRelation.HYPERNYM,
			WordNetRelation.INSTANCE_HYPERNYM,
			WordNetRelation.MEMBER_HOLONYM,
			WordNetRelation.PART_HOLONYM,
			WordNetRelation.ENTAILMENT,
			WordNetRelation.SUBSTANCE_MERONYM
	}));
	private static final Set<WordNetRelation> ALL_RELATIONS_BIG = new LinkedHashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.SYNONYM,
			WordNetRelation.DERIVATIONALLY_RELATED,
			WordNetRelation.HYPERNYM,
			WordNetRelation.INSTANCE_HYPERNYM,
			WordNetRelation.MEMBER_HOLONYM,
			WordNetRelation.PART_HOLONYM,
			WordNetRelation.SUBSTANCE_HOLONYM,
			WordNetRelation.MEMBER_MERONYM,
			WordNetRelation.PART_MERONYM,
			WordNetRelation.SUBSTANCE_MERONYM,
			WordNetRelation.ENTAILMENT,
			WordNetRelation.CAUSE,
			WordNetRelation.VERB_GROUP
	}));
	private static final Set<WordNetRelation> HYPERNYM_RELATIONS = new LinkedHashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.HYPERNYM,
			WordNetRelation.INSTANCE_HYPERNYM
	}));
	private static final Set<WordNetRelation> HYPERNYM1_RELATION = new LinkedHashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.HYPERNYM,
	}));
	private static final Set<WordNetRelation> HYPERNYM2_RELATION = new LinkedHashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.INSTANCE_HYPERNYM
	}));
	private static final Set<WordNetRelation> DERVRTD_RELATION = new LinkedHashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.DERIVATIONALLY_RELATED
	}));
	private static final Set<WordNetRelation> SYNONYM_RELATION = new LinkedHashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.SYNONYM
	}));
	private static final Integer[] LENGTHS_1_2_3 = {1, 2, 3};
	private static final Integer[] LENGTHS_1_2_3_TOP = {-1, 1, 2, 3};
	private static final Integer[] ALL_LIMITED_LENGTHS = {1, 2, 3, 4, 5, 6, 7};
	private static final Integer[] ALL_LENGTHS_WITH_TOP = {-1, 1, 2, 3, 4, 5, 6, 7};
	private static final Derivation[] DERVS_NONE = {Derivation.NONE}; 
	private static final Derivation[] DERVS_NONE_AND = {Derivation.NONE, Derivation.TEXT_ORIG_AND_DERV, Derivation.SPEC_ORIG_AND_DERV}; 
	private static final Derivation[] DERVS_NONE_ONLY = {Derivation.NONE, Derivation.TEXT_ONLY_DERV, Derivation.SPEC_ONLY_DERV}; 
	private static final Derivation[] DERVS_ONLY = {Derivation.TEXT_ONLY_DERV, Derivation.SPEC_ONLY_DERV}; 
	private static final Derivation[] DERVS_ALL = {Derivation.NONE, Derivation.TEXT_ORIG_AND_DERV, Derivation.SPEC_ORIG_AND_DERV, Derivation.TEXT_ONLY_DERV, Derivation.SPEC_ONLY_DERV}; 
	private static final Integer[] SENSE_NUMS = {1, -1};
	private static final Aggregator[] ALL_AGGS = {Aggregator.Any.inst, Aggregator.Min2.inst, Aggregator.Min3.inst, /*Aggregator.Min4.inst, */Aggregator.MinHalf.inst};
	private static final Aggregator[] AGG_ANY = {Aggregator.Any.inst};
	private static final Aggregator[] AGG_ANY_MIN2 = {Aggregator.Any.inst, Aggregator.Min2.inst};
	private static final Aggregator[] AGG_MIN2_MIN3 = {Aggregator.Min2.inst, Aggregator.Min3.inst};
	private static PartOfSpeech NOUN, VERB, ADJ, ADV;
}
