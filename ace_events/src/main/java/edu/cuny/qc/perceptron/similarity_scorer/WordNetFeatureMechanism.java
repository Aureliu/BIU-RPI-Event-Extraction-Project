package edu.cuny.qc.perceptron.similarity_scorer;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed;

import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.types.FeatureInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordNetDictionaryFactory;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;

public class WordNetFeatureMechanism extends FeatureMechanism {

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
	
	@Override
	public void preprocessSpec(JCas spec) {
		System.err.println("Currently no CAS-processing by WordNetFeatureMechanism - TBD");
	}

	@Override
	public void preprocessTextSentence(SentenceInstance textSentence) {
		throw new NotImplementedException();
	}

	@Override
	public LinkedHashMap<String, Double> scoreTrigger(JCas spec, SentenceInstance textSentence, int i) {
		LinkedHashMap<String, Double> ret = new LinkedHashMap<String, Double>();
		
		ret.put("WORDNET_SAME_SYNSET", Aggregator.any(new SameSynset(textAnno)));
		for (PredicateSeed seed : JCasUtil.select(spec, PredicateSeed.class)) {
			
		}
	}

	@Override
	public LinkedHashMap<String, Double> scoreArgument(JCas spec, SentenceInstance textSentence, int i, AceMention mention) {
		throw new NotImplementedException();
	}

	private class SameSynset extends FeatureMechanismSpecIterator {
		@Override
		public Double calcScore(Annotation text, Annotation spec) {
			dictionary.getSortedSynsetsOf(?, ???) // what should I do with POS? wil null work? explicit handling?
			FeatureInstance.POSITIVE_SCORE;
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
