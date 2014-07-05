package edu.cuny.qc.scorer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import ac.biu.nlp.nlp.ie.onthefly.input.AnnotationUtils;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.NounLemma;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.VerbLemma;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.perceptron.types.SignalInstance;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public abstract class PredicateSeedScorerTEMP extends SignalMechanismSpecIterator {

	private static final long serialVersionUID = -2424604187161763995L;
	static {System.err.println("Consider using Guava caches to cache signal values for specific textToken-specToken pairs (maybe also with their lemmas and/or POSes). Maybe also/instead, cache some intermediate values, like a lemma's WordNet sysnet.");}

	public SignalMechanismSpecIterator init(JCas spec, String viewName, AnnotationFS covering, Class<? extends Annotation> type, Token textAnno, Map<Class<?>, Object> textTriggerTokenMap, ScorerData scorerData) throws SignalMechanismException {
		return super.init(spec, viewName, covering, type, textAnno, textTriggerTokenMap, scorerData);
	}
	
	@Override
	public BigDecimal calcScore(Annotation text, Map<Class<?>, Object> textTriggerTokenMap, Annotation spec, ScorerData scorerData) throws SignalMechanismException {
		try {
			if (text.getClass().equals(Token.class)) {
				textToken = (Token) text;
			}
			else {
				textToken = UimaUtils.selectCoveredSingle(text.getView().getJCas(), Token.class, text);
			}
			
			if (spec.getClass().equals(PredicateSeed.class)) {
				specSeed = (PredicateSeed) spec;
			}
			else {
				specSeed = UimaUtils.selectCoveredSingle(spec.getView().getJCas(), PredicateSeed.class, spec);
			}
			
			textPos = AnnotationUtils.tokenToPOS(textToken);
			
			//hard-codedly, specificPos only refers to the original text token, not any derivations
			if (scorerData.specificPos != null && !scorerData.specificPos.equals(textPos)) {
				return SignalInstance.toDouble(false); //TODO: should be: IRRELEVANT
			}			

			// Get all text derivations
			Set<BasicRulesQuery> textDerivations = scorerData.deriver.getDerivations(
					getForm(textToken), textPos, scorerData.derivation.leftOriginal, scorerData.derivation.leftDerivation, scorerData.leftSenseNum);
			
			// Get all spec derivations, based on the spec token itself, and its possible noun-lemma and verb-lemma forms
			Set<String> specForms = new HashSet<String>(Arrays.asList(new String[] {
					specSeed.getCoveredText(),
					UimaUtils.selectCoveredSingle(specSeed.getView().getJCas(), NounLemma.class, specSeed).getValue(),
					UimaUtils.selectCoveredSingle(specSeed.getView().getJCas(), VerbLemma.class, specSeed).getValue(),
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
					result = calcTokenBooleanScore(textToken, textTriggerTokenMap, textDerv.lLemma, textDerv.lPos, specDerv.lLemma, specDerv.lPos, scorerData);
					if (result) {
						if (debug) {
							// when a BasicRulesQuery represents only one lemma/POS, it's always on the Left side
							addToHistory(textDerv.lLemma, textDerv.lPos, specDerv.lLemma, specDerv.lPos);
						}
						break;
					}
				}
				if (result) {
					break;
				}
			}
			return SignalInstance.toDouble(result);
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

	public void addToHistory(String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos) throws SignalMechanismException {
		String specBase = specSeed.getCoveredText();
		String textBase = getForm(textToken);
		String specExtra = specBase.equals(specStr)?"":String.format("(%s)",specStr);
		String textExtra = textBase.equals(textStr)?"":String.format("(%s)",textStr);
		
		String specHistory = String.format("%s%s/%s", specBase, specExtra, specPos.getCanonicalPosTag());
		String textHistory = String.format("%s%s/%s", textBase, textExtra, textPos.getCanonicalPosTag());
		history.put(specHistory.intern(), textHistory.intern());
	}

	/**
	 * Not supporting BigDecimal/Double results for now!
	 */
//	public BigDecimal calcTokenScore(Token text, Map<Class<?>, Object> textTriggerTokenMap, Token spec) throws SignalMechanismException {
//		Boolean boolResult = calcTokenBooleanScore(text, textTriggerTokenMap, spec);
//		return SignalInstance.toDouble(boolResult);
//	}
	
	public Boolean calcTokenBooleanScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException {
		throw new UnsupportedOperationException("calcTokenBooleanScore must be implemented in subclass");
	}
	
	public transient Token textToken;
	public transient PredicateSeed specSeed;
	public transient PartOfSpeech textPos;
}
