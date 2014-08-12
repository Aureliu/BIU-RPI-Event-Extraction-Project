package edu.cuny.qc.scorer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.ie.onthefly.input.AnnotationUtils;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.NounLemma;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceTimexMention;
import edu.cuny.qc.ace.acetypes.AceValueMention;
import edu.cuny.qc.util.PosMap;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public abstract class ArgumentExampleScorer extends ArgumentScorer<ArgumentExample> {
	private static final long serialVersionUID = 3212246701761719333L;
	
	static {
		System.err.println("??? ArgumentExampleScorer: currently ignoring entities that have no concrete mentions - should maybe deal with it somehow (if they exist...)");
		System.err.println("??? ArgumentExampleScorer: consider building headToken via deps and not some lexical heuristics (and maybe clean up heuristics as well)");
	}

	@Override
	protected void prepareSpecIteration(JCas spec) throws SignalMechanismException {
		Collection<ArgumentExample> examples = JCasUtil.select(argument.getExamples(), ArgumentExample.class);
		specIterator = examples.iterator();
	}

	@Override
	public Boolean calcBooleanArgumentScore(ArgumentExample spec) throws SignalMechanismException {
		try {
			// As a decision, Values and Timexes are not considered lexically - they will only be treated in the AIUS level
			if (aceMention instanceof AceTimexMention || aceMention instanceof AceValueMention) {
				return false; //TODO: should be: IRRELEVANT
			}
			
			AceEntityMention entityMention = (AceEntityMention) aceMention;
			List<AceEntityMention> concreteMentions = getConcreteArgumentMentions(entityMention);
			if (concreteMentions.isEmpty()) {
				//throw new SignalMechanismException(String.format("Got text entity mention without any coreferrent concrete mentions! '%s' (id=%s)", entityMention.text, entityMention.id));
				System.err.printf("Got text entity mention without any coreferrent concrete mentions! '%s' (id=%s)\n", entityMention.text, entityMention.id);
				return false; //TODO: should be: IRRELEVANT ?
			}
			
			// Calculate score on each concrete mention of the entity
			// this is a hard-coded "or" methodology, with short-circuit
			boolean result = false;
			for (AceEntityMention concreteMention : concreteMentions) {
				//String headSpan = concreteMention.head.getCoveredText(docAllText);
				Annotation headAnno = AnnotationUtils.spanToAnnotation(textTriggerToken.getCAS().getJCas(), concreteMention.head);
				/// DEBUG
//				String headSpan = concreteMention.head.getCoveredText(docAllText);
//				if (headSpan.contains("addam")) {
//					System.err.printf("   *** Span[%s:%s]='%s'\t\tAnno[%s:%s]='%s'\t\t Are they equal? %s\n", concreteMention.head.start(), concreteMention.head.end(), headSpan,
//							headAnno.getBegin(), headAnno.getEnd(), headAnno.getCoveredText(), headAnno.getCoveredText().equals(headSpan));
//				}
				////
				
				Token headToken = getHeadToken(headAnno);
				PartOfSpeech headTokenPos = AnnotationUtils.tokenToPOS(headToken);

				
				// Get all text-head-token-lemma derivations
				Set<BasicRulesQuery> headTokenLemmaDerivations = scorerData.deriver.getDerivations(
						headToken.getLemma().getValue(), headTokenPos, scorerData.derivation.leftOriginal, scorerData.derivation.leftDerivation, scorerData.leftSenseNum);
				
				// Get all spec derivations, based on the spec token itself, and its possible noun-lemma form (no verb-lemma for arguments! only nouns!)
				Set<String> specForms = new HashSet<String>(Arrays.asList(new String[] {
						spec.getCoveredText(),
						UimaUtils.selectCoveredSingle(spec.getView().getJCas(), NounLemma.class, spec).getValue(),
						//UimaUtils.selectCoveredSingle(spec.getView().getJCas(), VerbLemma.class, spec).getValue(),
				}));
				
				// Arguments in spec are only nouns
				Set<BasicRulesQuery> specDerivations = new HashSet<BasicRulesQuery>(5);
				for (String specForm : specForms) {
					specDerivations.addAll(scorerData.deriver.getDerivations(
							specForm, PosMap.byCanonical.get(CanonicalPosTag.N), scorerData.derivation.rightOriginal, scorerData.derivation.rightDerivation, scorerData.rightSenseNum));
				}					

				// Got through all the derivations, continuing the hard-coded "or" method
				for (BasicRulesQuery textDerv : headTokenLemmaDerivations) {
					for (BasicRulesQuery specDerv : specDerivations) {
						result = calcBoolArgumentExampleScore(concreteMention, headAnno, textDerv.lLemma, textDerv.lPos, specDerv.lLemma, specDerv.lPos, scorerData);
						if (result) {
							if (debug) {
								// when a BasicRulesQuery represents only one lemma/POS, it's always on the Left side
								addToHistory(headAnno.getCoveredText(), headToken.getCoveredText(), textDerv.lLemma, textDerv.lPos, specDerv.lLemma, specDerv.lPos, spec);
							}
							break;
						}
					}
					if (result) {
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
	
	public static List<AceEntityMention> getConcreteArgumentMentions(AceEntityMention entityMention) {
		List<AceEntityMention> result = Lists.newArrayList();
		for (AceEntityMention otherMention : entityMention.entity.mentions) {
			if (CONCRETE_ENTITY_TYPES.contains(otherMention.type)) {
				result.add(otherMention);
			}
		}
		return result;
	}

	private Token getHeadToken(Annotation covering) throws UnsupportedPosTagStringException, ExecutionException {
		if (covering == null) {
			return null;
		}
		Collection<Token> tokens = JCasUtil.selectCovered(Token.class, covering);
		//int i=-1;
		Token prev = null;
		for (Iterator<Token> iter = tokens.iterator(); iter.hasNext(); ) {
			//i++;
			Token curr = iter.next();
			String text = curr.getCoveredText();
			PartOfSpeech pos = AnnotationUtils.tokenToPOS(curr);
			if (pos.getCanonicalPosTag()==CanonicalPosTag.PP ||
				PUNCTUATION.contains(text) ||
				(ORG_SUFFIXES.contains(text) && prev!=null) ||
				(text.length()>1 && text.charAt(text.length()-1)=='.' && ORG_SUFFIXES.contains(text.substring(0, text.length()-1)) && prev!=null) ) {
				
				break; 
			}
			prev = curr;
		}
		return prev;
	}

	public void addToHistory(String textHead, String textHeadToken, String textDerv, PartOfSpeech textDervPos, String specStr, PartOfSpeech specPos, ArgumentExample spec) throws SignalMechanismException {
		String specBase = spec.getCoveredText();
		String specExtra = specBase.equals(specStr)?"":String.format("(%s)",specStr);
		String textExtra = textHeadToken.equals(textDerv)?"":String.format("(%s)",textDerv);
		
		String specHistory = String.format("%s%s/%s", specBase, specExtra, specPos.getCanonicalPosTag());
		String textHistory = String.format("%s/%s%s/%s", textHead, textHeadToken, textExtra, textDervPos.getCanonicalPosTag());
		history.put(specHistory.intern(), textHistory.intern());
	}
	
	public abstract Boolean calcBoolArgumentExampleScore(AceEntityMention concreteMention, Annotation headAnno, String textHeadTokenStr, PartOfSpeech textHeadTokenPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException;
	
	//public transient Token textToken;
	//public transient PredicateSeed specSeed;
	//public transient PartOfSpeech textPos;
	public static final Set<String> CONCRETE_ENTITY_TYPES = ImmutableSet.of("NAM", "NOM");

	public static Set<String> ORG_SUFFIXES = Sets.newHashSet(Arrays.asList(new String[] {
			"Inc", "Incorporated", "Corp", "Corporation", "Ltd", "Limited", "Co"
	}));
	public static Set<String> PUNCTUATION = Sets.newHashSet(Arrays.asList(new String[] {
			".", ",", "!", "?", ":", "@", "#", "$", "%"
	}));

}
