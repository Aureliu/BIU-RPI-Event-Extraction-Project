package edu.cuny.qc.scorer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;

import ac.biu.nlp.nlp.ie.onthefly.input.AnnotationUtils;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.NounLemma;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.VerbLemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfoWithSenseNumsOnly;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;

public abstract class PredicateSeedScorer extends PredicateScorer<PredicateSeed> {

	private static final long serialVersionUID = -2424604187161763995L;

	@Override
	protected void prepareSpecIteration(JCas spec) throws SignalMechanismException {
		try {
			JCas view = spec.getView(SpecAnnotator.TOKEN_VIEW);
			specIterator = JCasUtil.iterator(view, PredicateSeed.class);
			
			textPos = AnnotationUtils.tokenToPOS(textToken);

		} catch (CASException e) {
			throw new SignalMechanismException(e);
		} catch (UnsupportedPosTagStringException e) {
			throw new SignalMechanismException(e);
		} catch (ExecutionException e) {
			throw new SignalMechanismException(e);
		}
	}

	@Override
	public Boolean calcBooleanPredicateScore(PredicateSeed spec) throws SignalMechanismException {
		try {
			
			//hard-codedly, specificPos only refers to the original text token, not any derivations
			if (scorerData.specificPos != null && !scorerData.specificPos.equals(textPos)) {
				return false; //TODO: should be: IRRELEVANT
			}
			
			BasicRulesQuery textQuery = new BasicRulesQuery(getForm(textToken), textPos, null, null);
			Set<BasicRulesQuery> textDerivations = cacheTextDerivations.get(textQuery);
			
			PredicateSeedQuery specQuery = new PredicateSeedQuery(spec, textPos);
			Set<BasicRulesQuery> specDerivations = cacheSpecDerivations.get(specQuery);					

			// Calculate score on each combination of text-derivation and spec-derivation
			// this is a hard-coded "or" methodology, with short-circuit
			boolean result = false;
			for (BasicRulesQuery textDerv : textDerivations) {
				for (BasicRulesQuery specDerv : specDerivations) {
					result = calcBoolPredicateSeedScore(textToken, textTriggerTokenMap, textDerv.lLemma, textDerv.lPos, specDerv.lLemma, specDerv.lPos, scorerData);
					if (result) {
						if (debug) {
							// when a BasicRulesQuery represents only one lemma/POS, it's always on the Left side
							addToHistory(textDerv.lLemma, textDerv.lPos, specDerv.lLemma, specDerv.lPos, spec);
						}
						break;
					}
				}
				if (result) {
					break;
				}
			}
			return result;

		} catch (ExecutionException e) {
			throw new SignalMechanismException(e);
		}
	}
	
	/**
	 * The form of the token used to calculate score.
	 * By default it's the lemma, but it could be other things in other implementations (like surface form).
	 */
	public String getForm(Token token) {
		return token.getLemma().getValue();
	}

	public void addToHistory(String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, PredicateSeed spec) throws SignalMechanismException {
		String specBase = spec.getCoveredText();
		String textBase = getForm(textToken);
		String specExtra = specBase.equals(specStr)?"":String.format("(%s)",specStr);
		String textExtra = textBase.equals(textStr)?"":String.format("(%s)",textStr);
		
		String specHistory = String.format("%s%s/%s", specBase, specExtra, specPos.getCanonicalPosTag());
		String textHistory = String.format("%s%s/%s", textBase, textExtra, textPos.getCanonicalPosTag());
		history.put(specHistory.intern(), textHistory.intern());
	}
	
	public static class PredicateSeedQuery {
		public PredicateSeed predicateSeed;
		public PartOfSpeech specPos;
		public PredicateSeedQuery(PredicateSeed predicateSeed, PartOfSpeech specPos) {
			this.predicateSeed = predicateSeed;
			this.specPos = specPos;
		}
		@Override public int hashCode() {
		     return new HashCodeBuilder(19, 37).append(predicateSeed).append(specPos).toHashCode();
		}
		@Override public boolean equals(Object obj) {
		   if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) { return false; }
		   PredicateSeedQuery rhs = (PredicateSeedQuery) obj;
		   return new EqualsBuilder().append(predicateSeed, rhs.predicateSeed).append(specPos, rhs.specPos).isEquals();
		}
	}
	
	public abstract Boolean calcBoolPredicateSeedScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException;
	
	private transient PartOfSpeech textPos;	
	private transient LoadingCache<BasicRulesQuery, Set<BasicRulesQuery>> cacheTextDerivations = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.build(new CacheLoader<BasicRulesQuery, Set<BasicRulesQuery>>() {
				public Set<BasicRulesQuery> load(BasicRulesQuery query) throws DeriverException {
					Set<BasicRulesQuery> result = scorerData.deriver.getDerivations(
							query.lLemma, query.lPos, scorerData.derivation.leftOriginal, scorerData.derivation.leftDerivation, scorerData.leftSenseNum);
					return result;
				}
			});
	private transient LoadingCache<PredicateSeedQuery, Set<BasicRulesQuery>> cacheSpecDerivations = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.build(new CacheLoader<PredicateSeedQuery, Set<BasicRulesQuery>>() {
				public Set<BasicRulesQuery> load(PredicateSeedQuery spec) throws CASException, DeriverException {
					// Get all spec derivations, based on the spec token itself, and its possible noun-lemma and verb-lemma forms
					Set<String> specForms = new HashSet<String>(Arrays.asList(new String[] {
							spec.predicateSeed.getCoveredText(),
							UimaUtils.selectCoveredSingle(spec.predicateSeed.getView().getJCas(), NounLemma.class, spec.predicateSeed).getValue(),
							UimaUtils.selectCoveredSingle(spec.predicateSeed.getView().getJCas(), VerbLemma.class, spec.predicateSeed).getValue(),
					}));
					Set<BasicRulesQuery> result = new HashSet<BasicRulesQuery>(5);
					for (String specForm : specForms) {
						result.addAll(scorerData.deriver.getDerivations(
								specForm, spec.specPos, scorerData.derivation.rightOriginal, scorerData.derivation.rightDerivation, scorerData.rightSenseNum));
					}					
					return result;
				}
			});

}
