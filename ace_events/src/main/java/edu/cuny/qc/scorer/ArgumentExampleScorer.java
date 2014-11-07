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
import ac.biu.nlp.nlp.ie.onthefly.input.uima.VerbLemma;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceEntity;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceTimexMention;
import edu.cuny.qc.ace.acetypes.AceValueMention;
import edu.cuny.qc.scorer.PredicateSeedScorer.PredicateSeedQuery;
import edu.cuny.qc.util.PosMap;
import edu.cuny.qc.util.Utils;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public abstract class ArgumentExampleScorer extends ArgumentFreeScorer<ArgumentExample> {
	private static final long serialVersionUID = 3212246701761719333L;
	
	static {
		System.err.println("??? ArgumentExampleScorer: currently ignoring entities that have no concrete mentions - should maybe deal with it somehow (if they exist...)");
		System.err.println("??? ArgumentExampleScorer: consider building headToken via deps and not some lexical heuristics (and maybe clean up heuristics as well)");
	}

	@Override
	protected void prepareSpecIteration(JCas spec) throws SignalMechanismException {
		Collection<ArgumentExample> examples = JCasUtil.select(argument.getExamples(), ArgumentExample.class);
		specIterator = examples.iterator();
		docJCasStatic = docJCas; //I know this looks weird, but we need to take the JCas from the super class, and put it in a static field, for the cache (which is static)
	}

	@Override
	public Boolean calcBooleanArgumentScore(ArgumentExample spec) throws SignalMechanismException {
		try {
			// As a decision, Values and Timexes are not considered lexically - they will only be treated in the AIUS level
			if (aceMention instanceof AceTimexMention || aceMention instanceof AceValueMention) {
				return false; //TODO: should be: IRRELEVANT
			}
			
			AceEntityMention entityMention = (AceEntityMention) aceMention;
			/// DEBUG
			//System.out.printf("- getting from cache entity with hash=%s: %s\n", entityMention.entity.hashCode(), entityMention.entity);
			////
			Set<EntityMentionInfo> entityInfos = cacheTextEntityInfoWithDerivations.get(entityMention.entity);
			/// DEBUG
			//System.out.printf("- Entity with hash=%s retrieved!\n", entityMention.entity.hashCode());
			////
			Set<BasicRulesQuery> specDerivations = cacheSpecDerivations.get(spec);
			
			// Calculate score on each concrete mention of the entity
			// this is a hard-coded "or" methodology, with short-circuit
			boolean result = false;
			for (EntityMentionInfo entityMentionInfo : entityInfos) {
				for (BasicRulesQuery textDerv : entityMentionInfo.headTokenLemmaDerivations) {
					for (BasicRulesQuery specDerv : specDerivations) {
						result = calcBoolArgumentExampleScore(entityMentionInfo.corefMention, entityMentionInfo.headAnno, textDerv.lLemma, textDerv.lPos, specDerv.lLemma, specDerv.lPos, scorerData);
						if (result) {
							if (debug) {
								// when a BasicRulesQuery represents only one lemma/POS, it's always on the Left side
								addToHistory(entityMentionInfo.headAnno.getCoveredText(), entityMentionInfo.headToken.getCoveredText(), textDerv.lLemma, textDerv.lPos, specDerv.lLemma, specDerv.lPos, spec);
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

		} catch (ExecutionException e) {
			throw new SignalMechanismException(e);
		}
	}
	
	/**
	 * @deprecated We stopped using it since we decided we will use all entity mentions, not just concrete ones (also pronouns).
	 */
	public static List<AceEntityMention> getConcreteArgumentMentions(AceEntityMention entityMention) {
		List<AceEntityMention> result = Lists.newArrayList();
		for (AceEntityMention otherMention : entityMention.entity.mentions) {
			if (CONCRETE_ENTITY_TYPES.contains(otherMention.type)) {
				result.add(otherMention);
			}
		}
		return result;
	}

	private static Token getHeadToken(Annotation covering, AceEntityMention mention) throws UnsupportedPosTagStringException, ExecutionException {
		if (covering == null) {
			return null;
		}
		Collection<Token> tokens = JCasUtil.selectCovered(Token.class, covering);
		if (tokens.isEmpty()) {
			System.out.printf("  ** Got entity without tokens: '%s'\n", covering.getCoveredText());
			return null;
		}
		
		Token prev = null;
		for (Iterator<Token> iter = tokens.iterator(); iter.hasNext(); ) {
			Token curr = iter.next();
			String text = curr.getCoveredText();
			PartOfSpeech pos = AnnotationUtils.tokenToPOS(curr);
			if (pos.getCanonicalPosTag()==CanonicalPosTag.PP ||
				Utils.PUNCTUATION.contains(text) ||
				(ORG_SUFFIXES.contains(text) && prev!=null) ||
				(text.length()>1 && text.charAt(text.length()-1)=='.' && ORG_SUFFIXES.contains(text.substring(0, text.length()-1)) && prev!=null) ) {
				
				break; 
			}
			prev = curr;
		}
		
		if (prev == null) {
			System.out.printf("  ** Heuristics cannot find head token for entity: '%s'. Tokens are: %s\n", covering.getCoveredText(), JCasUtil.toText(tokens));
			return null;
		}

		mention.headTokenText = prev.getCoveredText().intern();
		//mention.headTokenLemma = prev.getLemma().getValue().intern();
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
	
	public static class EntityMentionInfo {
		public AceEntityMention corefMention;
		public Annotation headAnno;
		public Token headToken;
		public PartOfSpeech headTokenPos;
		public Set<BasicRulesQuery> headTokenLemmaDerivations;
	}
	
	public abstract Boolean calcBoolArgumentExampleScore(AceEntityMention corefeMention, Annotation headAnno, String textHeadTokenStr, PartOfSpeech textHeadTokenPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException;
	
	//public transient Token textToken;
	//public transient PredicateSeed specSeed;
	//public transient PartOfSpeech textPos;
	public static final Set<String> CONCRETE_ENTITY_TYPES = ImmutableSet.of("NAM", "NOM");

	public static Set<String> ORG_SUFFIXES = Sets.newHashSet(Arrays.asList(new String[] {
			"Inc", "Incorporated", "Corp", "Corporation", "Ltd", "Limited", "Co"
	}));

	private static JCas docJCasStatic = null;
	private static LoadingCache<AceEntity, Set<EntityMentionInfo>> cacheTextEntityInfoStatic = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.build(new CacheLoader<AceEntity, Set<EntityMentionInfo>>() {
				public Set<EntityMentionInfo> load(AceEntity entity) throws CASException, UnsupportedPosTagStringException, ExecutionException, DeriverException, SignalMechanismException {
					Set<EntityMentionInfo> result = Sets.newHashSetWithExpectedSize(entity.mentions.size());
					if (entity.mentions.isEmpty()) {
						throw new SignalMechanismException(String.format("Got text entity mention without any coreferrent mentions! (id=%s)", entity.id));
//						System.err.printf("Got text entity mention without any coreferrent mentions! '%s' (id=%s)\n", entityMention.text, entityMention.id);
//						return false; //TODO: should be: IRRELEVANT ?
					}

					/// DEBUG
					//System.out.printf("\n--      (%s) scanning mentions of entity with hash=%s: %s\n", this.getClass().getSimpleName(), entity.hashCode(), entity);
					////

					for (AceEntityMention corefMention : entity.mentions) {
						EntityMentionInfo info = new EntityMentionInfo();
						info.corefMention = corefMention;
						
						// Basically I am not allowed to use textTriggerToken as it changes with each call to prepareSpecIteration()
						// But here I only use it for the JCas, which stays the same for the entire document, so it's fine
						info.headAnno = AnnotationUtils.spanToAnnotation(docJCasStatic, corefMention.head);
						/// DEBUG
//						String headSpan = concreteMention.head.getCoveredText(docAllText);
//						if (headSpan.contains("addam")) {
//							System.err.printf("   *** Span[%s:%s]='%s'\t\tAnno[%s:%s]='%s'\t\t Are they equal? %s\n", concreteMention.head.start(), concreteMention.head.end(), headSpan,
//									headAnno.getBegin(), headAnno.getEnd(), headAnno.getCoveredText(), headAnno.getCoveredText().equals(headSpan));
//						}
						////
						
						/// DEBUG
						//System.out.printf("--- in corefMention=%s, hash=%s. info.headAnno='%s', hash=%s.\n", corefMention, corefMention.hashCode(), info.headAnno.getCoveredText(), info.headAnno.hashCode());
						////

						info.headToken = getHeadToken(info.headAnno, corefMention);
	
						/// DEBUG
//						if (info.headToken != null) {
//							System.out.printf("--- head token received='%s' hash=%s.\n", info.headToken.getCoveredText(), info.headToken.hashCode());
//						}
//						else {
//							System.out.printf("--- head token received... is null!\n");
//						}
						////
						
						// Technically this should never be null, but there are some preprocessing and annotation mistakes, so we just dispose them
						if (info.headToken != null) {
							info.headTokenPos = AnnotationUtils.tokenToPOS(info.headToken);
	
							// Get all text-head-token-lemma derivations
//							info.headTokenLemmaDerivations = scorerData.deriver.getDerivations(
//									info.headToken.getLemma().getValue(), info.headTokenPos, scorerData.derivation.leftOriginal, scorerData.derivation.leftDerivation, scorerData.leftSenseNum);
							
							result.add(info);
						}
					}

					return result;
				}
			});

	private transient LoadingCache<AceEntity, Set<EntityMentionInfo>> cacheTextEntityInfoWithDerivations = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.build(new CacheLoader<AceEntity, Set<EntityMentionInfo>>() {
				public Set<EntityMentionInfo> load(AceEntity entity) throws ExecutionException, DeriverException {
					Set<EntityMentionInfo> result = cacheTextEntityInfoStatic.get(entity);
					for (EntityMentionInfo info : result) {
						if (info.headToken != null) {
							// Get all text-head-token-lemma derivations
							// This is really the only reason we need a non-static (scorer-dependent) processing of text entity - the derivations! (which are indeed scorer dependent)
							info.headTokenLemmaDerivations = scorerData.deriver.getDerivations(
									info.headToken.getLemma().getValue(), info.headTokenPos, scorerData.derivation.leftOriginal, scorerData.derivation.leftDerivation, scorerData.leftSenseNum);
						}
					}
					return result;
				}
			});

	private transient LoadingCache<ArgumentExample, Set<BasicRulesQuery>> cacheSpecDerivations = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.build(new CacheLoader<ArgumentExample, Set<BasicRulesQuery>>() {
				public Set<BasicRulesQuery> load(ArgumentExample spec) throws DeriverException, ExecutionException, CASException {
					// Get all spec derivations, based on the spec token itself, and its possible noun-lemma form (no verb-lemma for arguments! only nouns!)
					Set<String> specForms = new HashSet<String>(Arrays.asList(new String[] {
							spec.getCoveredText(),
							UimaUtils.selectCoveredSingle(NounLemma.class, spec).getValue(),
							//UimaUtils.selectCoveredSingle(spec.getView().getJCas(), VerbLemma.class, spec).getValue(),
					}));
					
					// Arguments in spec are only nouns
					Set<BasicRulesQuery> result = new HashSet<BasicRulesQuery>(5);
					for (String specForm : specForms) {
						result.addAll(scorerData.deriver.getDerivations(
								specForm, PosMap.byCanonical.get(CanonicalPosTag.N), scorerData.derivation.rightOriginal, scorerData.derivation.rightDerivation, scorerData.rightSenseNum));
					}					

					return result;
				}
			});


}
