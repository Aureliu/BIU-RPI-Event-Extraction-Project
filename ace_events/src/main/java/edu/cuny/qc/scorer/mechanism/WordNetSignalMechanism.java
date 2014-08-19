package edu.cuny.qc.scorer.mechanism;

import static edu.cuny.qc.scorer.Aggregator.*;
import static edu.cuny.qc.scorer.Derivation.*;
import static edu.cuny.qc.scorer.Deriver.*;

import java.io.File;
import java.nio.channels.IllegalSelectorException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.jcas.tcas.Annotation;
import org.joda.time.Period;

import ac.biu.nlp.nlp.ie.onthefly.input.AnnotationUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import com.google.common.collect.Lists;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.core.Pipeline;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.scorer.Aggregator;
import edu.cuny.qc.scorer.ArgumentExampleScorer;
import edu.cuny.qc.scorer.BasicRulesQuery;
import edu.cuny.qc.scorer.Derivation;
import edu.cuny.qc.scorer.Deriver;
import edu.cuny.qc.scorer.DeriverException;
import edu.cuny.qc.scorer.FullRulesQuery;
import edu.cuny.qc.scorer.Juxtaposition;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.PredicateSeedScorer;
import edu.cuny.qc.scorer.Deriver.NoDerv;
import edu.cuny.qc.scorer.mechanism.NomlexSignalMechanism.NomlexDeriver;
import edu.cuny.qc.util.PosMap;
import edu.cuny.qc.util.Utils;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
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
		System.err.println("??? WordNetSignalMechanism: Using the text's lemma and POS causes errors on some lemmatization errors: like having injured/JJ be lemmatized as injure/JJ which doesn't exist (2014.07.23..8:APW_ENG_20030520.0081:1b:17), or wound/NN lemmatized as wind/NN, which doesn't entail wound/NN (2014.07.23..8:CNN_ENG_20030610_130042.17:2b:4)");
		System.err.println("??? WordNetSignalMechanism: In the argument scorer - maybe remove the duplicity (in Proper Nouns) and make it two scorers?");
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
	public WordNetSignalMechanism(Controller controller) throws SignalMechanismException {
		super(controller);
	}
	
	@Override
	public void init() throws WordNetInitializationException, UnsupportedPosTagStringException, ExecutionException {
		initDictionary();
		
		NOUN = PosMap.byCanonical.get(CanonicalPosTag.N);
		VERB = PosMap.byCanonical.get(CanonicalPosTag.V);
		ADJ = PosMap.byCanonical.get(CanonicalPosTag.ADJ);
		ADV = PosMap.byCanonical.get(CanonicalPosTag.ADV);

	}
	
	public static void initDictionary() throws WordNetInitializationException {
		if (dictionary == null) {
			dictionary = new JwiDictionary(WORDNET_DIR);
		}
	}
	
	@Override
	public void close() {
		dictionary.close();
		for (WordnetLexicalResource resource : cacheResources.asMap().values()) {
			resource.close();
		}
		super.close();
	}


	@Override
	public void addScorers() {

		switch (controller.featureProfile) {
		case TOKEN_BASELINE: break;
		case ANALYSIS:
			// tiny amount for debug
			//addTriggers(SYNONYM_RELATION,   Juxtaposition.ANCESTOR, new Integer[] {1}, ALL_DERIVERS, DERVS_NONE, new Integer[] {1}, new Integer[] {1}, new PartOfSpeech[] {null, /*NOUN, VERB, ADJ, ADV*/}, AGG_ANY_MIN2);
			
			//END of analysis2!
			/// Group A
			addTriggers(SYNONYM_RELATION,   Juxtaposition.ANCESTOR, new Integer[] {1}, ALL_DERIVERS, DERVS_NONE, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, /*VERB, ADJ, ADV*/}, AGG_ANY);
			addTriggers(HYPERNYM_RELATIONS, Juxtaposition.ANCESTOR, LENGTHS_1_2_3, ALL_DERIVERS, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN/*, VERB*/}, AGG_ANY);
			addTriggers(HYPERNYM1_RELATION, Juxtaposition.ANCESTOR, LENGTHS_1_2_3, ALL_DERIVERS, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN/*, VERB*/}, AGG_ANY);
			addTriggers(HYPERNYM2_RELATION, Juxtaposition.ANCESTOR, LENGTHS_1_2_3, ALL_DERIVERS, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN/*, VERB*/}, AGG_ANY);
	
			/// Group B
			addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, new Integer[] {1}, ALL_DERIVERS, DERVS_NONE_AND, new Integer[] {1}, SENSE_NUMS, new PartOfSpeech[] {null/*, NOUN, VERB*/}, AGG_MIN2);
			addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, new Integer[] {2}, ALL_DERIVERS, DERVS_NONE_AND, new Integer[] {1}, SENSE_NUMS, new PartOfSpeech[] {null/*, NOUN, VERB*/}, AGG_MIN3);
			addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, new Integer[] {3}, ALL_DERIVERS, DERVS_TEXT_ORIG_AND_DERV, new Integer[] {-1}, SENSE_NUMS, new PartOfSpeech[] {null/*, NOUN, VERB*/}, AGG_MIN2_MIN3);
	
			addTriggers(ALL_RELATIONS_SMALL,   Juxtaposition.ANCESTOR, LENGTHS_1_2_3_TOP, ALL_DERIVERS, DERVS_ALL, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, VERB/*, ADJ, ADV*/}, ALL_AGGS);
			addTriggers(ALL_RELATIONS_BIG,   Juxtaposition.ANCESTOR, LENGTHS_1_2_3_TOP, ALL_DERIVERS, DERVS_ALL, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, VERB/*, ADJ, ADV*/}, ALL_AGGS);
		
			break;
		case NORMAL:
			//17.7.14 Some conclusions:
			// - all three AllRelations (one big, two smalls) do exactly the same on batch1)
			// - they all so ALMOST exactly the same as hypernym, and he seems to be sligtly better. But this might be circumstantial, I wouldn't take it into account.
			// - cousins suck. From just looking up some examples, they just get weird stuff, nothing useful (yes, almost nothing useful according to AceAnalyzer, and a LOT of garbage)! So remove!
			//    - but if I do decide to use cousins, at least hardocdedly at to it the synset itself - now it says no on the word itself!!!
			// - maybe could add derivations to synonyms.
			
			// Built from 2014.07.02..1__SignalAnalyzer_medium__TESRV2
			// Top F1
			addTrigger(new ScorerData(null, new WordnetTriggerScorer(SYNONYM_RELATION, Juxtaposition.ANCESTOR, 1), NoDerv.inst, Derivation.NONE, -1, 1, null, Any.inst));
			//addTrigger(new ScorerData(null, new WordnetScorer(SYNONYM_RELATION, Juxtaposition.ANCESTOR, 1), new Join(WordnetDervRltdDeriver.inst, NomlexDeriver.inst), Derivation.TEXT_ORIG_AND_DERV, -1, 1, null, Any.inst));
			//addTrigger(new ScorerData(null, new WordnetScorer(SYNONYM_RELATION, Juxtaposition.ANCESTOR, 1), NoDerv.inst, Derivation.NONE, 1, 1, null, Any.inst));

			//addTrigger(new ScorerData(null, new WordnetScorer(HYPERNYM_RELATIONS, Juxtaposition.ANCESTOR, 1), WordnetDervRltdDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, -1, 1, null, Any.inst));
			addTrigger(new ScorerData(null, new WordnetTriggerScorer(HYPERNYM_RELATIONS, Juxtaposition.ANCESTOR, 2), WordnetDervRltdDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, -1, 1, null, Any.inst));
			//addTrigger(new ScorerData(null, new WordnetScorer(HYPERNYM_RELATIONS, Juxtaposition.ANCESTOR, 3), WordnetDervRltdDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, -1, 1, null, Any.inst));

			addTrigger(new ScorerData(null, new WordnetTriggerScorer(ALL_RELATIONS_SMALL, Juxtaposition.ANCESTOR, 2), NoDerv.inst, Derivation.NONE, -1, 1, null, Any.inst));
			addTrigger(new ScorerData(null, new WordnetTriggerScorer(ALL_RELATIONS_SMALL, Juxtaposition.ANCESTOR, 2), WordnetDervRltdDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, -1, 1, null, Any.inst));
			//addTrigger(new ScorerData(null, new WordnetScorer(ALL_RELATIONS_BIG,   Juxtaposition.ANCESTOR, 2), NoDerv.inst, Derivation.NONE, -1, 1, null, Any.inst));

			//addTrigger(new ScorerData(null, new WordnetScorer(HYPERNYM_RELATIONS, Juxtaposition.ANCESTOR, 2), NoDerv.inst, Derivation.NONE, -1, 1, null, Any.inst));

			// Top Precision
			// ONLY_DERVs are horrible!!! remove them!!!
//			addTrigger(new ScorerData(null, new WordnetScorer(ALL_RELATIONS_SMALL, Juxtaposition.ANCESTOR, 3), WordnetDervRltdDeriver.inst, Derivation.SPEC_ONLY_DERV, -1, 1, null, Min3.inst));
//			addTrigger(new ScorerData(null, new WordnetScorer(ALL_RELATIONS_SMALL, Juxtaposition.ANCESTOR, 3), WordnetDervRltdDeriver.inst, Derivation.TEXT_ONLY_DERV, -1, 1, null, Min3.inst));
//			addTrigger(new ScorerData(null, new WordnetScorer(ALL_RELATIONS_SMALL, Juxtaposition.ANCESTOR, 3), WordnetDervRltdDeriver.inst, Derivation.SPEC_ONLY_DERV, 1, -1, null, Min3.inst));
//			addTrigger(new ScorerData(null, new WordnetScorer(ALL_RELATIONS_SMALL, Juxtaposition.ANCESTOR, 3), WordnetDervRltdDeriver.inst, Derivation.SPEC_ONLY_DERV, -1, -1, null, Min3.inst));

			addTrigger(new ScorerData(null, new WordnetTriggerScorer(ALL_RELATIONS_BIG, Juxtaposition.ANCESTOR, 2), NomlexSignalMechanism.NomlexDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, -1, 1, null, Any.inst));
			//addTrigger(new ScorerData(null, new WordnetScorer(ALL_RELATIONS_BIG, Juxtaposition.ANCESTOR, 2), NomlexSignalMechanism.NomlexDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, 1, 1, null, Any.inst));
			//addTrigger(new ScorerData(null, new WordnetScorer(ALL_RELATIONS_BIG, Juxtaposition.ANCESTOR, 1), WordnetDervRltdDeriver.inst, Derivation.SPEC_ORIG_AND_DERV, -1, -1, null, Min3.inst));
			
			// Top Recall
			//addTrigger(new ScorerData(null, new WordnetScorer(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, 2), WordnetDervRltdDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, -1, -1, null, Min2.inst));
			///////////////addTrigger(new ScorerData(null, new WordnetScorer(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, 2), WordnetDervRltdDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, -1, -1, null, Any.inst));
			//addTrigger(new ScorerData(null, new WordnetScorer(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, 3), WordnetDervRltdDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, -1, -1, null, Min3.inst));
			
			//addTrigger(new ScorerData(null, new WordnetScorer(SYNONYM_RELATION, Juxtaposition.ANCESTOR, 1), NoDerv.inst, Derivation.NONE, -1, -1, null, Any.inst));
			//addTrigger(new ScorerData(null, new WordnetScorer(SYNONYM_RELATION, Juxtaposition.ANCESTOR, 1), NoDerv.inst, Derivation.NONE, 1, -1, null, Any.inst));
			
			// Top InfoGain - contained in other categories
			
			addArgumentFree(new ScorerData(null, new WordnetArgumentScorer(SYNONYM_RELATION, Juxtaposition.ANCESTOR, 1), NoDerv.inst, Derivation.NONE, -1, 1, null, Any.inst));
			addArgumentFree(new ScorerData(null, new WordnetArgumentScorer(HYPERNYM_RELATIONS, Juxtaposition.ANCESTOR, 2), WordnetDervRltdDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, -1, 1, null, Any.inst));
			addArgumentFree(new ScorerData(null, new WordnetArgumentScorer(ALL_RELATIONS_SMALL, Juxtaposition.ANCESTOR, 2), NoDerv.inst, Derivation.NONE, -1, 1, null, Any.inst));
			addArgumentFree(new ScorerData(null, new WordnetArgumentScorer(ALL_RELATIONS_SMALL, Juxtaposition.ANCESTOR, 2), WordnetDervRltdDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, -1, 1, null, Any.inst));
			addArgumentFree(new ScorerData(null, new WordnetArgumentScorer(ALL_RELATIONS_BIG, Juxtaposition.ANCESTOR, 2), NomlexSignalMechanism.NomlexDeriver.inst, Derivation.TEXT_ORIG_AND_DERV, -1, 1, null, Any.inst));
			
			break;
		default:
			throw new IllegalStateException("Bad FeatureProfile enum value: " + controller.featureProfile);
		}
		
		//END of analysis1!
//		/// Group A
//		addTriggers(SYNONYM_RELATION,   Juxtaposition.ANCESTOR, new Integer[] {1}, ALL_DERIVERS, DERVS_NONE, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, /*VERB, ADJ, ADV*/}, AGG_ANY);
//		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.ANCESTOR, ALL_LENGTHS_WITH_TOP, ALL_DERIVERS, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN/*, VERB*/}, AGG_ANY);
//		addTriggers(HYPERNYM1_RELATION, Juxtaposition.ANCESTOR, ALL_LENGTHS_WITH_TOP, ALL_DERIVERS, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN/*, VERB*/}, AGG_ANY);
//		addTriggers(HYPERNYM2_RELATION, Juxtaposition.ANCESTOR, ALL_LENGTHS_WITH_TOP, ALL_DERIVERS, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN/*, VERB*/}, AGG_ANY);
//
//		/// Group B
//		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, new Integer[] {1}, ALL_DERIVERS, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null/*, NOUN, VERB*/}, AGG_ANY_MIN2);
//		addTriggers(HYPERNYM_RELATIONS, Juxtaposition.COUSIN_STRICT, new Integer[] {2, 3}, ALL_DERIVERS, DERVS_NONE_AND, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null/*, NOUN, VERB*/}, AGG_MIN2_MIN3);
//
//		addTriggers(ALL_RELATIONS_SMALL,   Juxtaposition.ANCESTOR, LENGTHS_1_2_3_TOP, ALL_DERIVERS, DERVS_ALL, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, VERB/*, ADJ, ADV*/}, ALL_AGGS);
//		addTriggers(ALL_RELATIONS_BIG,   Juxtaposition.ANCESTOR, LENGTHS_1_2_3_TOP, ALL_DERIVERS, DERVS_ALL, SENSE_NUMS, SENSE_NUMS, new PartOfSpeech[] {null, NOUN, VERB/*, ADJ, ADV*/}, ALL_AGGS);

		
		
		
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
	
	public void addTriggers(Set<WordNetRelation> relations, Juxtaposition juxt, Integer[] lengths, Deriver[] derivers, Derivation[] dervs, Integer[] leftSenses, Integer[] rightSenses, PartOfSpeech[] specificPoses, Aggregator[] aggs) {
		System.out.printf("%s ^^^ addTriggers(): Building %s (%sx%sx%sx%sx%sx%s) combinations: lengths=%s, dervs=%s, leftSenses=%s, rightSenses=%s, specificPoses=%s, aggs=%s. Fixed options: relations=%s, juxt=%s\n",
				Utils.detailedLog(),
				lengths.length*dervs.length*leftSenses.length*rightSenses.length*specificPoses.length*aggs.length,
				lengths.length, dervs.length, leftSenses.length, rightSenses.length, specificPoses.length, aggs.length,
				Arrays.toString(lengths), Arrays.toString(dervs), Arrays.toString(leftSenses), Arrays.toString(rightSenses), Arrays.toString(specificPoses), Arrays.toString(aggs),
				relations, juxt);
		for (Integer length : lengths) {
			for (Deriver deriver : derivers) {
				for (Derivation derv : dervs) {
					for (Integer leftSense : leftSenses) {
						for (Integer rightSense : rightSenses) {
							for (PartOfSpeech specificPos : specificPoses) {
								for (Aggregator agg : aggs) {
	//								System.out.printf("%s ^^^^^ addTriggers(): Starting single combination: relations=%s, just=%s, length=%s, derv=%s, leftSense=%s, rightSense=%s, specificPos=%s, agg=%s\n",
	//										Pipeline.detailedLog(),
	//										relations, juxt, length, derv, leftSense, rightSense, specificPos, agg);
									
									WordnetTriggerScorer scorer = new WordnetTriggerScorer(relations, juxt, length);
									addTrigger(new ScorerData(null, scorer, deriver, derv, leftSense, rightSense, specificPos, agg));
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void logCacheStats() {
		System.out.printf("@@ Exist:        %s\n", cacheStats(cacheExist));
		System.out.printf("@@ CousinsLoose: %s\n", cacheStats(cacheCousinsLoose));
		System.out.printf("@@ CousinsStrict:%s\n", cacheStats(cacheCousinsStrict));
		System.out.printf("@@ Rules:        %s\n", cacheStats(cacheRules));
		System.out.printf("@@ Bools:        %s\n", cacheStats(cacheBools));
		System.out.printf("@@ Derv:         %s\n", cacheStats(WordnetDervRltdDeriver.cacheDerv));
		System.out.printf("@@ Resources:    %s\n", cacheStats(cacheResources));
	}
	
	@Override
	public void entrypointPreSentence(SentenceInstance inst) {
		if (DO_SENTENCE_LOGGING) {
			if (usedCaches) {
				System.out.printf("%s @@@ PreSentence cache stats:\n", Utils.detailedLog());
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
	public void entrypointPreDocument(Document doc) {
		System.out.printf("%s @@@@ PreDocument cache stats:\n", Utils.detailedLog());
		logCacheStats();
	}
//	@Override
//	public void entrypointPreDocumentBunch() {
//		System.out.printf("%s @@@@@ PreDocumentBunch cache stats:\n", Utils.detailedLog());
//		logCacheStats();
//	}

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
		private static final long serialVersionUID = 818014062327518891L;
		public static final WordnetDervRltdDeriver inst = new WordnetDervRltdDeriver();
		private WordnetDervRltdDeriver() {} //private c-tor
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
							BasicRulesQuery res = new BasicRulesQuery(rule.getRLemma(), rule.getRPos(), null, null);
							result.add(res);
						}
						return result;
					}
				});
	}
	
	public static class WordnetTriggerScorer extends PredicateSeedScorer {
		private static final long serialVersionUID = 7293139399085559241L;
		public Set<WordNetRelation> relations;
		public Juxtaposition juxt;
		public int length;
				
		static {
			try {
				initDictionary();
			} catch (Exception e) {
				System.err.printf("\n\n\nGot exception '%s' while initing dict in %s - This should never happen, as it is supposed to be inited beforehand in the signal mechanism!!!!\n\n\n",
						e, WordnetTriggerScorer.class.getSimpleName());
			}
		}
		
		public WordnetTriggerScorer(Set<WordNetRelation> relations,
				Juxtaposition juxt, int length) {
			this.relations = relations;
			this.juxt = juxt;
			this.length = length;			
		}

		@Override public String getTypeName() {
			String rels;
			if (this.relations.equals(ALL_RELATIONS_BIG)) {
				rels = "AllRelsBig";
			}
			else if (this.relations.equals(ALL_RELATIONS_SMALL)) {
				rels = "AllRelsSmall";
			}
			else if (this.relations.equals(HYPERNYM_RELATIONS)) {
				rels = "Hypernyms";
			}
			else {
				rels = StringUtils.join(this.relations, "_");
			}
			
			String lengthStr = "" + length;
			if (length == -1) {
				lengthStr = "Top";
			}
	
			return String.format("WN__%s__%s_Len%s", rels, juxt, lengthStr);
		}


		@Override
		public Boolean calcBoolPredicateSeedScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException {
			try {
				WordNetPartOfSpeech textWnPos = WordNetPartOfSpeech.toWordNetPartOfspeech(textPos);
				if (textWnPos == null) {
					return false;  //TODO: should be: IRRELEVANT
				}				
				
				usedCaches = true;
				BasicRulesQuery leftQuery = new BasicRulesQuery(textStr, textPos, null, null); 
				if (!cacheExist.get(leftQuery)) {
					return false;  //TODO: should be: IRRELEVANT
				}
				
				BasicRulesQuery rightQuery = new BasicRulesQuery(specStr, specPos, null, null);
				if (!cacheExist.get(rightQuery)) {
					return false;  //TODO: should be: IRRELEVANT
				}
				
				int realLength = length;
				if (length == -1) {
					// Go all the way up the wordnet tree
					realLength = MAXIMUM_WORDNET_LENGTH;
				}
				
				BasicRulesQuery basicQuery = new BasicRulesQuery(textStr, textPos, specStr, specPos);
				FullRulesQuery fullQuery = new FullRulesQuery(this.relations, realLength, this.juxt, scorerData.leftSenseNum, scorerData.rightSenseNum, basicQuery);
				boolean result = cacheBools.get(fullQuery);
				return result;
				
			} catch (ExecutionException e) {
				throw new SignalMechanismException(e);
			}
		}
	}
	
	public static class WordnetArgumentScorer extends ArgumentExampleScorer {
		public Set<WordNetRelation> relations;
		public Juxtaposition juxt;
		public int length;
				
		static {
			try {
				initDictionary();
			} catch (Exception e) {
				System.err.printf("\n\n\nGot exception '%s' while initing dict in %s - This should never happen, as it is supposed to be inited beforehand in the signal mechanism!!!!\n\n\n",
						e, WordnetTriggerScorer.class.getSimpleName());
			}
		}
		
		public WordnetArgumentScorer(Set<WordNetRelation> relations,
				Juxtaposition juxt, int length) {
			this.relations = relations;
			this.juxt = juxt;
			this.length = length;			
		}

		@Override public String getTypeName() {
			String rels;
			if (this.relations.equals(ALL_RELATIONS_BIG)) {
				rels = "AllRelsBig";
			}
			else if (this.relations.equals(ALL_RELATIONS_SMALL)) {
				rels = "AllRelsSmall";
			}
			else if (this.relations.equals(HYPERNYM_RELATIONS)) {
				rels = "Hypernyms";
			}
			else {
				rels = StringUtils.join(this.relations, "_");
			}
			
			String lengthStr = "" + length;
			if (length == -1) {
				lengthStr = "Top";
			}
	
			return String.format("WN__%s__%s_Len%s", rels, juxt, lengthStr);
		}


		@Override
		public Boolean calcBoolArgumentExampleScore(AceEntityMention corefMention, Annotation headAnno, String textHeadTokenStr, PartOfSpeech textHeadTokenPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException {
			try {
				BasicRulesQuery rightQuery = new BasicRulesQuery(specStr, specPos, null, null);
				if (!cacheExist.get(rightQuery)) {
					return false;  //TODO: should be: IRRELEVANT
				}
				
				List<BasicRulesQuery> queries = Lists.newArrayListWithCapacity(2);
				if (corefMention.type.equalsIgnoreCase("NAM")) { // Proper Noun
					queries.add(new BasicRulesQuery(headAnno.getCoveredText(), PosMap.byCanonical.get(CanonicalPosTag.N), null, null)); //all tokens
					queries.add(new BasicRulesQuery(textHeadTokenStr, textHeadTokenPos, null, null)); //only head token
				}
				else { // Common Noun ("NOM"), Pronouns, etc.
					queries.add(new BasicRulesQuery(textHeadTokenStr, textHeadTokenPos, null, null)); //only head token
				}
				
				// Hard-coded "or" method
				boolean result = false;
				for (BasicRulesQuery leftQuery : queries) {
					WordNetPartOfSpeech textWnPos = WordNetPartOfSpeech.toWordNetPartOfspeech(leftQuery.lPos);
					if (textWnPos == null) {
						continue;  //TODO: should be: IRRELEVANT
					}
					
					usedCaches = true;
					if (!cacheExist.get(leftQuery)) {
						return false;  //TODO: should be: IRRELEVANT
					}
					
					int realLength = length;
					if (length == -1) {
						// Go all the way up the wordnet tree
						realLength = MAXIMUM_WORDNET_LENGTH;
					}
					
					BasicRulesQuery basicQuery = new BasicRulesQuery(leftQuery.lLemma, leftQuery.lPos, specStr, specPos);
					FullRulesQuery fullQuery = new FullRulesQuery(this.relations, realLength, this.juxt, scorerData.leftSenseNum, scorerData.rightSenseNum, basicQuery);
					
					result = cacheBools.get(fullQuery);
					if (result) {
						break;
					}
				}
				
				return result;
				
			} catch (ExecutionException e) {
				throw new SignalMechanismException(e);
			}
		}
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
			//TODO: perhaps change weight to size. This way we'll limit the number of entries (makes sense) and not maximum size of value's list (which doesn't seem to limit anything)
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
					//String extra = "";
					Boolean booleanScore;
					if (key.juxt == Juxtaposition.ANCESTOR) {
						// Original use of cacheRules - but that caches seemed to grow uncontrollably, and really contribute nothing
//						List<LexicalRule<? extends WordnetRuleInfo>> rules = cacheRules.get(key);
//						booleanScore = !rules.isEmpty();
						
						// Current use - get the rules directly, no caching
						WordnetRuleInfo info = new WordnetRuleInfoWithSenseNumsOnly(key.leftSenseNum, key.rightSenseNum);
						WordnetLexicalResource resource = cacheResources.get(key.length);
						List<LexicalRule<? extends WordnetRuleInfo>> rules = resource.getRules(q.lLemma, q.lPos, q.rLemma, q.rPos, key.relations, info);
						booleanScore = !rules.isEmpty();
						
						
						
						// because bottom part is commented out
//						if (booleanScore) {
//							List<WordNetRelation> ruleRelations = new ArrayList<WordNetRelation>(rules.size());
//							for (LexicalRule<? extends WordnetRuleInfo> rule : rules) {
//								ruleRelations.add(rule.getInfo().getTypedRelation());
//							}
//							extra = String.format(", relations=%s", ruleRelations);
//						}
					}
					else if (key.juxt == Juxtaposition.COUSIN_STRICT || key.juxt == Juxtaposition.COUSIN_LOOSE) {
						//TODO: this is messy, with the different kinds of hypernyms...
						if (!key.relations.equals(HYPERNYM_RELATIONS)) {
							throw new IllegalArgumentException("juxt=COUSIN_*, but relations is not exactly HYPERNYM. relations=" + key.relations);
						}

						BasicRulesQuery basicQueryOnlyLeft = new BasicRulesQuery(q.lLemma, q.lPos, null, null);
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
//					if (booleanScore) {
//						System.err.printf("Wordnet: TRUE! %s/%s-->%s/%s\t(juxt=%s, length=%s, leftSenseNum=%s, rightSenseNum=%s%s)\n",
//								q.lLemma, q.lPos, q.rLemma, q.rPos, key.juxt, key.length, key.leftSenseNum, key.rightSenseNum, extra);
//					}
					
		            return booleanScore;
				}
			});
	
	private static JwiDictionary dictionary = null;
	private static boolean usedCaches = false;
	private static boolean reportedNoCaches = false;
	private static boolean DO_SENTENCE_LOGGING = false;
	
	//public static String WORDNET_DIR_PATH = ;
	public static File WORDNET_DIR = new File("../ace_events_large_resources/src/main/resources/data/Wordnet3.0");
	private static final int MAXIMUM_WORDNET_LENGTH = 100;
	
	public static final Set<WordNetRelation> ALL_RELATIONS_SMALL = new LinkedHashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.SYNONYM,
			WordNetRelation.DERIVATIONALLY_RELATED,
			WordNetRelation.HYPERNYM,
			WordNetRelation.INSTANCE_HYPERNYM,
			WordNetRelation.MEMBER_HOLONYM,
			WordNetRelation.PART_HOLONYM,
			WordNetRelation.ENTAILMENT,
			WordNetRelation.SUBSTANCE_MERONYM
	}));
	public static final Set<WordNetRelation> ALL_RELATIONS_BIG = new LinkedHashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
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
	public static final Set<WordNetRelation> HYPERNYM_RELATIONS = new LinkedHashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.SYNONYM,
			WordNetRelation.HYPERNYM,
			WordNetRelation.INSTANCE_HYPERNYM
	}));
	public static final Set<WordNetRelation> HYPERNYM1_RELATION = new HashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.HYPERNYM,
	}));
	public static final Set<WordNetRelation> HYPERNYM2_RELATION = new HashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.INSTANCE_HYPERNYM
	}));
	public static final Set<WordNetRelation> DERVRTD_RELATION = new HashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.DERIVATIONALLY_RELATED
	}));
	public static final Set<WordNetRelation> SYNONYM_RELATION = new HashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.SYNONYM
	}));
	private static final Integer[] LENGTHS_1_2_3 = {1, 2, 3};
	private static final Integer[] LENGTHS_1_2_3_TOP = {-1, 1, 2, 3};
	private static final Integer[] ALL_LIMITED_LENGTHS = {1, 2, 3, 4, 5, 6, 7};
	private static final Integer[] ALL_LENGTHS_WITH_TOP = {-1, 1, 2, 3, 4, 5, 6, 7};
	private static final Integer[] SENSE_NUMS = {1, -1};
	private static PartOfSpeech NOUN, VERB, ADJ, ADV;
}
