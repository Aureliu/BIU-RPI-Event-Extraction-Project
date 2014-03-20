package edu.cuny.qc.perceptron.similarity_scorer;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.uima.jcas.JCas;

import ac.biu.nlp.nlp.ie.onthefly.input.AnnotationUtils;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
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

public class WordNetFeatureMechanism extends FeatureMechanism {

	static {System.err.println("WordNetFeatureMechanism: ignoring spec's POS, using only text's");}

	public WordNetFeatureMechanism() throws LexicalResourceException, WordNetInitializationException {
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
//	public void preprocessSpec(JCas spec) throws FeatureMechanismException {
//		System.err.println("Currently no CAS-processing by WordNetFeatureMechanism - TBD");
//	}
//
//	@Override
//	public void preprocessTextSentence(SentenceInstance textSentence) throws FeatureMechanismException {
//		throw new NotImplementedException();
//	}

	@Override
	public LinkedHashMap<String, Double> scoreTrigger(JCas spec, SentenceInstance textSentence, int i) throws FeatureMechanismException {
		LinkedHashMap<String, Double> ret = new LinkedHashMap<String, Double>();
		
		List<Token> textAnnos = (List<Token>) textSentence.get(InstanceAnnotations.TokenAnnotations);
		Token textAnno = textAnnos.get(i);
		
		ret.put("WORDNET_SAME_SYNSET", Aggregator.any(new SameSynset().init(spec, SpecAnnotator.TOKEN_VIEW, PredicateSeed.class, textAnno)));
		ret.put("WORDNET_SPEC_HYPERNYM", Aggregator.any(new IsSpecHypernym().init(spec, SpecAnnotator.TOKEN_VIEW, PredicateSeed.class, textAnno)));
		ret.put("WORDNET_SPEC_ENTAILED", Aggregator.any(new IsSpecEntailed().init(spec, SpecAnnotator.TOKEN_VIEW, PredicateSeed.class, textAnno)));
		
		return ret;
	}

	@Override
	public LinkedHashMap<String, Double> scoreArgument(JCas spec, SentenceInstance textSentence, int i, AceMention mention) throws FeatureMechanismException {
		throw new NotImplementedException();
	}

	private class SameSynset extends FeatureMechanismSpecTokenIterator {
		@Override
		public Boolean calcTokenBooleanScore(Token text, Token spec) throws FeatureMechanismException
		{
			try {
				PartOfSpeech textPos = new PennPartOfSpeech(text.getPos().getPosValue());
				WordNetPartOfSpeech textWnPos = WordNetPartOfSpeech.toWordNetPartOfspeech(textPos);
				
				Set<Synset> textSynsets = dictionary.getSynsetsOf(text.getLemma().getValue(), textWnPos);
				//Use text's POS also for spec
				Set<Synset> specSynsets = dictionary.getSynsetsOf(spec.getLemma().getValue(), textWnPos);
				
				boolean differentSynsets = Collections.disjoint(textSynsets, specSynsets);
				return !differentSynsets;
			} catch (WordNetException e) {
				throw new FeatureMechanismException(e);
			} catch (UnsupportedPosTagStringException e) {
				throw new FeatureMechanismException(e);
			}
		}
	}
	
	private class IsSpecHypernym extends FeatureMechanismSpecTokenIterator {
		@Override
		public Boolean calcTokenBooleanScore(Token text, Token spec) throws FeatureMechanismException
		{
			try {
				PartOfSpeech textPos = AnnotationUtils.tokenToPOS(text);
				Set<WordNetRelation> hypernym = new HashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {WordNetRelation.HYPERNYM}));
				List<LexicalRule<? extends WordnetRuleInfo>> rules = resource.getRules(	text.getLemma().getValue(),
									textPos,
									text.getLemma().getValue(),
									textPos,
									hypernym,
									null);
				return !rules.isEmpty();
			} catch (LexicalResourceException e) {
				throw new FeatureMechanismException(e);
			} catch (UnsupportedPosTagStringException e) {
				throw new FeatureMechanismException(e);
			}
		}
	}
	
	private class IsSpecEntailed extends FeatureMechanismSpecTokenIterator {
		@Override
		public Boolean calcTokenBooleanScore(Token text, Token spec) throws FeatureMechanismException
		{
			try {
				PartOfSpeech textPos = AnnotationUtils.tokenToPOS(text);
				List<LexicalRule<? extends WordnetRuleInfo>> rules = resource.getRules(	text.getLemma().getValue(),
									textPos,
									text.getLemma().getValue(),
									textPos);
				return !rules.isEmpty();
			} catch (LexicalResourceException e) {
				throw new FeatureMechanismException(e);
			} catch (UnsupportedPosTagStringException e) {
				throw new FeatureMechanismException(e);
			}
		}
	}
	
	private WordnetLexicalResource resource;
	private Dictionary dictionary;
	
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
