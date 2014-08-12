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
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateInUsageSample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.VerbLemma;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public abstract class PredicateInUsageSampleScorer extends PredicateScorer<PredicateInUsageSample> {
	private static final long serialVersionUID = 6199081370547940433L;

	@Override
	protected void prepareSpecIteration(JCas spec) throws SignalMechanismException {
		try {
			JCas view = spec.getView(SpecAnnotator.SENTENCE_VIEW);
			specIterator = JCasUtil.iterator(view, PredicateInUsageSample.class);
		} catch (CASException e) {
			throw new SignalMechanismException(e);
		}
	}

	@Override
	public Boolean calcBooleanPredicateScore(PredicateInUsageSample spec) throws SignalMechanismException {
		try {
			boolean result = false;
			PartOfSpeech textPos = AnnotationUtils.tokenToPOS(textToken);
			Token specToken = UimaUtils.selectCoveredSingle(spec.getCAS().getJCas(), Token.class, spec);
			PartOfSpeech specPos = AnnotationUtils.tokenToPOS(specToken);
			
			result = calcBoolPredicateInUsageSampleScore(textToken, textTriggerTokenMap, textPos, spec, specPos, scorerData);
			if (result) {
				if (debug) {
					// when a BasicRulesQuery represents only one lemma/POS, it's always on the Left side
					addToHistory(textToken.getCoveredText(), textPos, specToken.getCoveredText(), specPos);
				}
			}

			return result;

		} catch (CASException e) {
			throw new SignalMechanismException(e);
		} catch (UnsupportedPosTagStringException e) {
			throw new SignalMechanismException(e);
		} catch (ExecutionException e) {
			throw new SignalMechanismException(e);
		}
	}
	
//	/**
//	 * The form of the token used to calculate score.
//	 * By default it's the lemma, but it could be other things in other implementations (like surface form).
//	 */
//	public String getForm(Token token) {
//		return token.getLemma().getValue();
//	}
//
	public void addToHistory(String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos) throws SignalMechanismException {
		String specHistory = String.format("%s/%s", specStr, specPos.getCanonicalPosTag());
		String textHistory = String.format("%s/%s", textStr, textPos.getCanonicalPosTag());
		history.put(specHistory.intern(), textHistory.intern());
	}
	
	// What stuff I want this method to have??? for text -just Qi map! They will get Deps from there! From spec - Maybe the PIUS itself? I need directly or indirectly to have: all deps around node, specStr, specPos. But maybe also POSes of others? I don't think so...
	public abstract Boolean calcBoolPredicateInUsageSampleScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, PartOfSpeech textPos, PredicateInUsageSample pius, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException;
	
	//public transient Token textToken;
	//public transient PredicateSeed specSeed;
	//public transient PartOfSpeech textPos;
}
