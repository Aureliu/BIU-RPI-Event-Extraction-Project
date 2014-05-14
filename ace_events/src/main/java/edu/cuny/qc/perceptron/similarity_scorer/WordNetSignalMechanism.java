package edu.cuny.qc.perceptron.similarity_scorer;

import java.io.File;
import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.uima.jcas.JCas;

import ac.biu.nlp.nlp.ie.onthefly.input.AnnotationUtils;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordNetDictionaryFactory;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;

public class WordNetSignalMechanism extends SignalMechanism {

	static {
		System.err.println("??? WordNetSignalMechanism: Fake Signal Mechanisms");
		System.err.println("??? WordNetSignalMechanism: ignoring spec's POS, using only text's");
		System.err.println("??? WordNetSignalMechanism: if a word has a non-wordnet POS (anything but noun/verb/adj/adv) we return FALSE, but we should return IRRELEVANT (when I figure out what it means... :( )");
		System.err.println("??? WordNetSignalMechanism: if a text or spec doesn't exist in wordnet, we return FALSE, although we should return IRRELEVANT");
	}

	public WordNetSignalMechanism() throws LexicalResourceException, WordNetInitializationException {
		super();
		
		File wordnetDir = new File(WORDNET_DIR);
		resource = new WordnetLexicalResource(
				wordnetDir,
				USE_FIRST_SENSE_ONLY_LEFT,
				USE_FIRST_SENSE_ONLY_RIGHT,
				DEFAULT_RELATIONS,
				CHAINING_LENGTH
				);
		System.err.printf("Worndet params: USE_FIRST_SENSE_ONLY_LEFT=%s, USE_FIRST_SENSE_ONLY_RIGHT=%s, DEFAULT_RELATIONS=%s, CHAINING_LENGTH=%s\n",
				USE_FIRST_SENSE_ONLY_LEFT, USE_FIRST_SENSE_ONLY_RIGHT, DEFAULT_RELATIONS, CHAINING_LENGTH);

		// A dictionary is created and kept in the WordnetLexicalResource,
		// but we don't have access to it, so we create another one
		dictionary =  WordNetDictionaryFactory.newDictionary(wordnetDir, null);
	}
	
	@Override
	public void close() {
		dictionary.close();
		resource.close();
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
	public LinkedHashMap<String, BigDecimal> scoreTriggerToken(JCas spec, SentenceInstance textSentence, Token textTriggerToken) throws SignalMechanismException {
		LinkedHashMap<String, BigDecimal> ret = new LinkedHashMap<String, BigDecimal>();
		
		ret.put("WORDNET_FAKE_LETTER_E", Aggregator.any(new TextHasLetterE().init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken)));
		ret.put("WORDNET_FAKE_LETTER_X", Aggregator.any(new TextHasLetterX().init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken)));
		ret.put("WORDNET_FAKE_PREKNOWN_TRIGGERS",     Aggregator.any(new PreknownTriggers().init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken)));
		ret.put("WORDNET_FAKE_NOT_PREKNOWN_TRIGGERS", Aggregator.any(new NotPreknownTriggers().init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken)));
		ret.put("WORDNET_SAME_SYNSET",   Aggregator.any(new SameSynset()    .init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken)));
		ret.put("WORDNET_SPEC_HYPERNYM", Aggregator.any(new IsSpecHypernym().init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken)));
		ret.put("WORDNET_SPEC_ENTAILED", Aggregator.any(new IsSpecEntailed().init(spec, SpecAnnotator.TOKEN_VIEW, null, PredicateSeed.class, textTriggerToken)));
		
		return ret;
	}

	@Override
	public LinkedHashMap<String, BigDecimal> scoreArgumentFirstHeadToken(JCas spec, Argument argument, SentenceInstance textSentence, Token textTriggerToken, Token textArgToken) throws SignalMechanismException {
		LinkedHashMap<String, BigDecimal> ret = new LinkedHashMap<String, BigDecimal>();
		
//		//ret.put("WORDNET_SAME_SYNSET",   Aggregator.any(new SameSynset()    .init(spec, null, argument, ArgumentExample.class, textArgToken)));
//		//ret.put("WORDNET_SPEC_HYPERNYM", Aggregator.any(new IsSpecHypernym().init(spec, null, argument, ArgumentExample.class, textArgToken)));
//		ret.put("WORDNET_SPEC_ENTAILED", Aggregator.any(new IsSpecEntailed().init(spec, null, argument, ArgumentExample.class, textArgToken)));
		
		return ret;
	}

	private class TextHasLetterE extends SignalMechanismSpecTokenIterator {
		@Override
		public Boolean calcTokenBooleanScore(Token text, Token spec) throws SignalMechanismException
		{
			String textLemma = text.getLemma().getValue();
			return textLemma.contains("e");
		}
	}
	
	private class TextHasLetterX extends SignalMechanismSpecTokenIterator {
		@Override
		public Boolean calcTokenBooleanScore(Token text, Token spec) throws SignalMechanismException
		{
			String textLemma = text.getLemma().getValue();
			return textLemma.contains("x");
		}
	}
	
	private static class PreknownTriggers extends SignalMechanismSpecTokenIterator {
		public static final List<String> PREKNOWN_ATTACK_TRIGGERS = Arrays.asList(new String[] {
				"ambush", "attack", "battle", "battlefront", "blast", "blow", "bomb", "bombing", "combat",
				"conflict", "defend", "destroy", "drop", "engage", "explosion", "fight", "fighting", "fire",
				"hit", "hold", "insurgency", "invade", "invasion", /*"it",*/ "kill", "launch", "occupy", "pummel",
				"resistance", "response", "sept.", "shoot", "take", "terrorism", "threaten", "use",
				"violence",	"war",
		});
		@Override
		public Boolean calcTokenBooleanScore(Token text, Token spec) throws SignalMechanismException
		{
			String textLemma = text.getLemma().getValue();
			return PREKNOWN_ATTACK_TRIGGERS.contains(textLemma);
		}
	}
	
	private static class NotPreknownTriggers extends SignalMechanismSpecTokenIterator {
		@Override
		public Boolean calcTokenBooleanScore(Token text, Token spec) throws SignalMechanismException
		{
			String textLemma = text.getLemma().getValue();
			return !PreknownTriggers.PREKNOWN_ATTACK_TRIGGERS.contains(textLemma);
		}
	}
	
	private class SameSynset extends SignalMechanismSpecTokenIterator {
		@Override
		public Boolean calcTokenBooleanScore(Token text, Token spec) throws SignalMechanismException
		{
			try {
				PartOfSpeech textPos = AnnotationUtils.tokenToPOS(text);
				WordNetPartOfSpeech textWnPos = WordNetPartOfSpeech.toWordNetPartOfspeech(textPos);
				
				if (textWnPos == null) {
					return false;
				}
				
				String textLemma = text.getLemma().getValue();
				String specLemma = spec.getLemma().getValue();
				Set<Synset> textSynsets = getSynsetsOf(textLemma, textWnPos);
				//Use text's POS also for spec
				Set<Synset> specSynsets = getSynsetsOf(specLemma, textWnPos);
				
				if (textSynsets.isEmpty() || specSynsets.isEmpty()) {
//					if (textSynsets.isEmpty()) {
//						System.err.printf("WordNetSignalMechanism: Empty Synset for text: '%s' (pos=%s)\n", textLemma, textWnPos);
//					}
//					if (specSynsets.isEmpty()) {
//						//System.err.printf("WordNetSignalMechanism: Empty Synset for spec: '%s' (pos=%s)\n", specLemma, textWnPos);
//					}
					return false;
				}
					
				boolean differentSynsets = Collections.disjoint(textSynsets, specSynsets);
				//DEBUG
				if (!differentSynsets) {
					System.err.printf("Wordnet.SameSynset: TRUE! (%s,%s)\n", textLemma, specLemma);
				}
				return !differentSynsets;
			} catch (WordNetException e) {
				throw new SignalMechanismException(e);
			} catch (UnsupportedPosTagStringException e) {
				throw new SignalMechanismException(e);
			}
		}
	}
	
	private class IsSpecHypernym extends SignalMechanismSpecTokenIterator {
		@Override
		public Boolean calcTokenBooleanScore(Token text, Token spec) throws SignalMechanismException
		{
			try {
				PartOfSpeech textPos = AnnotationUtils.tokenToPOS(text);
				Set<WordNetRelation> hypernym = new HashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {WordNetRelation.HYPERNYM}));
				String textLemma = text.getLemma().getValue();
				String specLemma = spec.getLemma().getValue();
				List<LexicalRule<? extends WordnetRuleInfo>> rules = getHypernymRules(
									textLemma,
									textPos,
									specLemma,
									textPos/*,
									hypernym,
									null*/);
				//DEBUG
				if (!rules.isEmpty()) {
					System.err.printf("Wordnet.IsSpecHypernym: TRUE! %s-->%s\n", textLemma, specLemma);
				}
				return !rules.isEmpty();
			} catch (LexicalResourceException e) {
				throw new SignalMechanismException(e);
			} catch (UnsupportedPosTagStringException e) {
				throw new SignalMechanismException(e);
			}
		}
	}
	
	private class IsSpecEntailed extends SignalMechanismSpecTokenIterator {
		@Override
		public Boolean calcTokenBooleanScore(Token text, Token spec) throws SignalMechanismException
		{
			try {
				PartOfSpeech textPos = AnnotationUtils.tokenToPOS(text);
				String textLemma = text.getLemma().getValue();
				String specLemma = spec.getLemma().getValue();
				List<LexicalRule<? extends WordnetRuleInfo>> rules = getDefaultRules(
									textLemma,
									textPos,
									specLemma,
									textPos);
				//DEBUG
				if (!rules.isEmpty()) {
					List<WordNetRelation> relations = new ArrayList<WordNetRelation>(rules.size());
					for (LexicalRule<? extends WordnetRuleInfo> rule : rules) {
						relations.add(rule.getInfo().getTypedRelation());
					}
					System.err.printf("Wordnet.IsSpecEntailed: TRUE! %s-->%s (%d: %s)\n", textLemma, specLemma, rules.size(), relations);
				}
				return !rules.isEmpty();
			} catch (LexicalResourceException e) {
				throw new SignalMechanismException(e);
			} catch (UnsupportedPosTagStringException e) {
				throw new SignalMechanismException(e);
			}
		}
	}
	
	
	// TODO These very naive caches should probably be thought through and get a better implementation
	private class RulesQuery {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((lLemma == null) ? 0 : lLemma.hashCode());
			result = prime * result + ((lPos == null) ? 0 : lPos.hashCode());
			result = prime * result
					+ ((rLemma == null) ? 0 : rLemma.hashCode());
			result = prime * result + ((rPos == null) ? 0 : rPos.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RulesQuery other = (RulesQuery) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (lLemma == null) {
				if (other.lLemma != null)
					return false;
			} else if (!lLemma.equals(other.lLemma))
				return false;
			if (lPos == null) {
				if (other.lPos != null)
					return false;
			} else if (!lPos.equals(other.lPos))
				return false;
			if (rLemma == null) {
				if (other.rLemma != null)
					return false;
			} else if (!rLemma.equals(other.rLemma))
				return false;
			if (rPos == null) {
				if (other.rPos != null)
					return false;
			} else if (!rPos.equals(other.rPos))
				return false;
			return true;
		}
		public RulesQuery(String lLemma, String rLemma, PartOfSpeech lPos,
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
		private WordNetSignalMechanism getOuterType() {
			return WordNetSignalMechanism.this;
		}
	}
	private Set<Synset> getSynsetsOf(String text, WordNetPartOfSpeech pos) throws WordNetException {
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
	private List<LexicalRule<? extends WordnetRuleInfo>> getDefaultRules(String textLemma, PartOfSpeech textPos, String specLemma, PartOfSpeech specPos) throws LexicalResourceException {
		RulesQuery query = new RulesQuery(textLemma, specLemma, textPos, specPos);
		List<LexicalRule<? extends WordnetRuleInfo>> result = cahceDefaultRules.get(query);
		if (result == null) {
			result = resource.getRules(textLemma, textPos, specLemma, textPos);
			cahceDefaultRules.put(query, result);			
		}
		return result;	
	}
	private List<LexicalRule<? extends WordnetRuleInfo>> getHypernymRules(String textLemma, PartOfSpeech textPos, String specLemma, PartOfSpeech specPos) throws LexicalResourceException {
		RulesQuery query = new RulesQuery(textLemma, specLemma, textPos, specPos);
		List<LexicalRule<? extends WordnetRuleInfo>> result = cahceDefaultRules.get(query);
		if (result == null) {
			Set<WordNetRelation> hypernym = new HashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {WordNetRelation.HYPERNYM}));
			result = resource.getRules(textLemma, textPos, specLemma, textPos, hypernym, null);
			cahceDefaultRules.put(query, result);			
		}
		return result;	
	}
	
	private WordnetLexicalResource resource;
	private Dictionary dictionary;
	
	private Map<Entry<String, WordNetPartOfSpeech>, Set<Synset>> cacheSynset = new HashMap<Entry<String, WordNetPartOfSpeech>, Set<Synset>>();
	private Map<RulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceDefaultRules = new HashMap<RulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	private Map<RulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>> cahceHypernymRules = new HashMap<RulesQuery, List<LexicalRule<? extends WordnetRuleInfo>>>();
	
	private static final String WORDNET_DIR = "src/main/resources/data/Wordnet3.0";
	private static final Boolean USE_FIRST_SENSE_ONLY_LEFT = true;
	private static final Boolean USE_FIRST_SENSE_ONLY_RIGHT = true;
	private static final Integer CHAINING_LENGTH = 1;
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
}
