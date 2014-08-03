package edu.cuny.qc.scorer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.ie.onthefly.input.AnnotationUtils;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.NounLemma;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.VerbLemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public abstract class PredicateSeedScorer extends PredicateScorer<PredicateSeed> {

	private static final long serialVersionUID = -2424604187161763995L;

	@Override
	protected void prepareSpecIteration(JCas spec) throws SignalMechanismException {
		try {
			JCas view = spec.getView(SpecAnnotator.TOKEN_VIEW);
			specIterator = JCasUtil.iterator(view, PredicateSeed.class);
		} catch (CASException e) {
			throw new SignalMechanismException(e);
		}
	}

	@Override
	public Boolean calcBooleanPredicateScore(Token text, Map<Class<?>, Object> textTriggerTokenMap, PredicateSeed spec, ScorerData scorerData) throws SignalMechanismException {
		try {
			PartOfSpeech textPos = AnnotationUtils.tokenToPOS(textToken);
			
			//hard-codedly, specificPos only refers to the original text token, not any derivations
			if (scorerData.specificPos != null && !scorerData.specificPos.equals(textPos)) {
				return false; //TODO: should be: IRRELEVANT
			}
			
			// Get all text derivations
			Set<BasicRulesQuery> textDerivations = scorerData.deriver.getDerivations(
					getForm(textToken), textPos, scorerData.derivation.leftOriginal, scorerData.derivation.leftDerivation, scorerData.leftSenseNum);
			
			// Get all spec derivations, based on the spec token itself, and its possible noun-lemma and verb-lemma forms
			Set<String> specForms = new HashSet<String>(Arrays.asList(new String[] {
					spec.getCoveredText(),
					UimaUtils.selectCoveredSingle(spec.getView().getJCas(), NounLemma.class, spec).getValue(),
					UimaUtils.selectCoveredSingle(spec.getView().getJCas(), VerbLemma.class, spec).getValue(),
			}));
			Set<BasicRulesQuery> specDerivations = new HashSet<BasicRulesQuery>(5);
			for (String specForm : specForms) {
				specDerivations.addAll(scorerData.deriver.getDerivations(
						specForm, textPos, scorerData.derivation.rightOriginal, scorerData.derivation.rightDerivation, scorerData.rightSenseNum));
			}					

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

		} catch (CASException e) {
			throw new SignalMechanismException(e);
		} catch (DeriverException e) {
			throw new SignalMechanismException(e);
		} catch (UnsupportedPosTagStringException e) {
			throw new SignalMechanismException(e);
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
	
	/**
	 * NOTE: Technically, we could have also put here stuff from the PIUS, but for now it just doesn't seem relevant for any predicate signal
	 */
	public abstract Boolean calcBoolPredicateSeedScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException;
	
	//public transient Token textToken;
	//public transient PredicateSeed specSeed;
	//public transient PartOfSpeech textPos;
}
