package edu.cuny.qc.scorer.mechanism;

import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.DB_DRIVER;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.DB_URL;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.LIMIT_NUMBER_OF_RULES;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.RULES_TABLE_NAME;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.TEMPLATES_TABLE_NAME;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.exceptions.JedisConnectionException;
import weka.core.Tee;

import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecHandler;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.PredicateSeed;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

import edu.cuny.qc.scorer.mechanism.WordNetSignalMechanism;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Perceptron;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.core.component.lexicalknowledge.geo.GeoLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiExtractionType;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiRuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.distsim.resource.SimilarityStorageBasedLexicalResource;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.transformations.operations.rules.distsim.DistSimParameters;
import eu.excitementproject.eop.transformations.operations.rules.distsimnew.DirtDBRuleBase;
import eu.excitementproject.eop.transformations.utilities.Constants;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

/***
 Almost an exact copy of the playground_m class: oferbr.playground_m.try1.TryRedis
 Ofer Bronstein, June 1, 2015
***/
public class TryLexicalResources {
	private static final int RULES_FROM_RESOURCE = 70;//20;
	private static final int CHAINED_RULES = 70;//20;
	private static final double MIN_CONFIDENCE = 0.05;
	private static final String EMPTY = "(none)";
	private static final String WORDNET_PATH = "../ace_events_large_resources/src/main/resources/data/Wordnet3.0";
	private static final String CONTROLLER_PARAMS =
					"beamSize=4 maxIterNum=20 skipNonEventSent=true useArguments=false avgArguments=true useGlobalFeature=false " +
					"addNeverSeenFeatures=true crossSent=false crossSentReranking=false order=0 evaluatorType=1 learnBigrams=true logLevel=3 " +
					"oMethod=G0P- serialization=BZ2 featureProfile=ANALYSIS calcDebugSignalsAnyway=false docsChunk=20";
	
//	private static String theLemma;;
//	private static PartOfSpeech thePos;

//	private static void setWord(String lemma, PartOfSpeech pos) {
//		theLemma = lemma;
//		thePos = pos;
//	}
//	
	public static boolean firstHopThreshold(String resourceName, List<String> specWords, LexicalRule<? extends RuleInfo> rule, int rank) {
		if (specWords.contains(rule.getRLemma())) {
			if (dontFilterResource(resourceName)) {
				return true;
			}
			
			return rule.getConfidence() >= MIN_CONFIDENCE;
		}
		else {
			return false;
		}
	}
	
	public static boolean secondHopThreshold(String resourceName, List<String> specWords, LexicalRule<? extends RuleInfo> rule1, int rule1Rank, LexicalRule<? extends RuleInfo> rule2, int rule2Rank) {
		if (specWords.contains(rule2.getRLemma())) {
			if (dontFilterResource(resourceName)) {
				return true;
			}
			
			if (rule1Rank <= 5) {
				return rule1.getConfidence()>=MIN_CONFIDENCE && rule2.getConfidence()>=MIN_CONFIDENCE;
			}
			else if (rule1Rank <= 10) {
				if (rule2Rank>15) {
					return false;
				}
				else if (rule2Rank>10){
					return rule1.getConfidence()>=MIN_CONFIDENCE && rule2.getConfidence()>=0.1;
				}
				else {
					return rule1.getConfidence()>=MIN_CONFIDENCE && rule2.getConfidence()>=MIN_CONFIDENCE;
				}
			}
			else if (rule1Rank <= 15) {
				return rule1.getConfidence()>=MIN_CONFIDENCE && rule2Rank<=10 && rule2.getConfidence()>=0.1;
			}
			else { //rule1Rank > 15
				return rule1.getConfidence()>=MIN_CONFIDENCE && rule2Rank<=5 && rule2.getConfidence()>=0.14;
			}
		}
		else {
			return false;
		}
	}
	
	public static LexicalResource<? extends RuleInfo> loadRedis(String name, String configFile) throws Exception {
		try {
		    ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(configFile)));
			ConfigurationParams confParams = confFile.getModuleConfiguration(Configuration.KNOWLEDGE_RESOURCE);
			LexicalResource<? extends RuleInfo> result = new SimilarityStorageBasedLexicalResource(confParams);
			return result;
		} catch (JedisConnectionException e) {
			System.err.printf("- Could not connect to resource '%s' - skipping it in this run.\n", name);
			return null;
		}
	}
	
	public static WikiLexicalResource loadWikiMysql(String name, File stopWordsFile, Set<WikiExtractionType> permittedExtractionTypes, String dbConnectionString, String dbUser, String dbPassword, Double coocurrenceThreshold) {
		try {
			return new WikiLexicalResource(stopWordsFile, permittedExtractionTypes,
					dbConnectionString, dbUser, dbPassword, coocurrenceThreshold);
		} catch (LexicalResourceException e) {
			System.err.printf("Could not create resource '%s', skipping it. Swallowed exception: %s\n", name, e);
			return null;
		}
	}
	
	public static GeoLexicalResource loadGeo(String name, String dbConnectionString, String tableName) {
		try {
			return new GeoLexicalResource(dbConnectionString, tableName);
		} catch (LexicalResourceException e) {
			System.err.printf("Could not create resource '%s', skipping it. Swallowed exception: %s\n", name, e);
			return null;
		}
	}
	/**
	 * this is for loading stuff like framenet, reverb, original dirt, unary/binary lin, nd basically every knowledge resource
	 * that under {@link eu.excitementproject.eop.transformations.builtin_knowledge.KnowledgeResource} has "true" as the second
	 * parameter in the c-tor (isDirtLike).
	 * Apparently these resources are not lexical resources, yet are all loaded with the class DirtDBRuleBase, which works with
	 * BIUTEE trees. So this obviously makes things more complicated.
	 * Use only if you absolutely have to.
	 */
//	public static ?? loadDirtLike() {
//		Class.forName(params.get(DB_DRIVER));
//		String dbUrl = params.get(DB_URL);
//		Connection connection = DriverManager.getConnection(dbUrl);
//		String templates = params.get(TEMPLATES_TABLE_NAME);
//		String rules = params.get(RULES_TABLE_NAME);
//		int limit = params.getInt(LIMIT_NUMBER_OF_RULES);
//		DistSimParameters distSimParameters = new DistSimParameters(templates, rules, limit, Constants.DEFAULT_DIRT_LIKE_RESOURCES_CACHE_SIZE, Constants.DEFAULT_DIRT_LIKE_RESOURCES_CACHE_SIZE);
//
//		return new DirtDBRuleBase(connection,ruleBaseName,distSimParameters,parser);
//
//	}
	
	public static String info(LexicalRule<? extends RuleInfo> rule) {
		RuleInfo info = rule.getInfo();
		if (info != null && info instanceof WikiRuleInfo) {
			WikiRuleInfo wikiInfo = (WikiRuleInfo) info;
			return String.format("/%s", wikiInfo.getBestExtractionType());
		}
		else if (info != null && info instanceof WordnetRuleInfo) {
			WordnetRuleInfo wordnetInfo = (WordnetRuleInfo) info;
			return String.format("/%s", wordnetInfo.getTypedRelation());
		}
		else { 
			return "";
		}
	}
	
	public static boolean dontFilterResource(String resourceName) {
		if (resourceName.toLowerCase().startsWith("wordnet") ||
			resourceName.toLowerCase().startsWith("wiki")) {
			return true;
		}
		
		return false;
	}
	
	public static void checkKnowledgeResources(String lemma, PartOfSpeech pos, List<String> specWords) throws Exception {
		Map<String, LexicalResource<? extends RuleInfo>> resources = Maps.newLinkedHashMap();
		Map<String, Multimap<String, String>> useful = Maps.newLinkedHashMap();
		
		//resources.put("bap", loadRedis("bap", "C:\\lab\\Redis\\configurations\\bap\\knowledge-resource.xml"));
		//resources.put("lin-dep", loadRedis("lin-dep", "C:\\lab\\Redis\\configurations\\lin\\dependency\\knowledge-resource.xml"));
		//resources.put("lin-prox", loadRedis("lin-prox", "C:\\lab\\Redis\\configurations\\lin\\proximity\\knowledge-resource.xml"));
		////resources.put("reverb", load("C:\\lab\\Redis\\configurations\\reverb\\knowledge-resource.xml"));
		//resources.put("wiki(few extraction types)", loadWikiMysql("wiki(few extraction types)", new File("C:/Lab/workdir/stopwords-Eyal.txt"), ImmutableSet.of(WikiExtractionType.REDIRECT, WikiExtractionType.BE_COMP, WikiExtractionType.BE_COMP_IDIRECT, WikiExtractionType.ALL_NOUNS_TOP),
		//		"jdbc:mysql://te-srv1:3306/wikikb"/*"?user=root&password=Miescuel2"*/, "root", "Miescuel1", 0.01));
		//resources.put("wiki(all extraction types)", loadWikiMysql("wiki(all extraction types)", new File("C:/Lab/workdir/stopwords-Eyal.txt"), Sets.newHashSet(WikiExtractionType.values()),
		//		"jdbc:mysql://te-srv1:3306/wikikb"/*"?user=root&password=Miescuel2"*/, "root", "Miescuel1", 0.01));
		//resources.put("geo", loadGeo("geo", "jdbc:mysql://te-srv1:3306/geo?user=root&password=Miescuel1","tipster"));
		resources.put("wordnet-SYNONYM",      new WordnetLexicalResource(new File(WORDNET_PATH), false, true, WordNetSignalMechanism.SYNONYM_RELATION, 1));
		resources.put("wordnet-Hypernyms",    new WordnetLexicalResource(new File(WORDNET_PATH), false, true, WordNetSignalMechanism.HYPERNYM_RELATIONS, 2));
		resources.put("wordnet-AllRelsSmall", new WordnetLexicalResource(new File(WORDNET_PATH), false, true, WordNetSignalMechanism.ALL_RELATIONS_SMALL, 2));
		
		for (String resourceName : resources.keySet()) {
			if (resources.get(resourceName) != null) {
				Multimap<String, String> resourceUseful = LinkedHashMultimap.create();
				for (String specWord : specWords) {
					resourceUseful.put(specWord, EMPTY);
				}
				useful.put(resourceName, resourceUseful);
				
				LexicalResource<? extends RuleInfo> resource = resources.get(resourceName);
				List<? extends LexicalRule<? extends RuleInfo>> rules = resource.getRulesForLeft(lemma, pos);
				int trimmed = dontFilterResource(resourceName) ? rules.size() : Math.min(RULES_FROM_RESOURCE, rules.size());
				System.out.printf("%s: %s rules (shwoing %s)\n", resourceName, rules.size(), trimmed);
				int i=0;
				for (LexicalRule<? extends RuleInfo> rule : rules.subList(0, trimmed)) {
					i++;
					String ruleOut = String.format("  %s/%s\t--> %s/%s/%s/%.4f%s", rule.getLLemma(), rule.getLPos(), rule.getRLemma(), rule.getRPos(), i, rule.getConfidence(), info(rule));
					System.out.printf(ruleOut);
	//				if (rule.getRLemma().contains("sra")) {
	//					System.err.printf("delete this debug!!!\n");
	//				}
					if (firstHopThreshold(resourceName, specWords, rule, i)) {
						resourceUseful.put(rule.getRLemma(), ruleOut);
					}
					List<? extends LexicalRule<? extends RuleInfo>> rules2 = resource.getRulesForLeft(rule.getRLemma(), rule.getRPos());
					int trimmed2 = Math.min(CHAINED_RULES, rules2.size());
					if (trimmed2 > 0) {
						System.out.printf("\t==> ");
						int j=0;
						for (LexicalRule<? extends RuleInfo> rule2 : rules2.subList(0, trimmed2)) {
							j++;
							String rule2Out = String.format("%s/%s/%s/%.4f%s\t", rule2.getRLemma(), rule2.getRPos(), j, rule2.getConfidence(), info(rule));
							System.out.printf(rule2Out);
							if (secondHopThreshold(resourceName,specWords, rule, i, rule2, j)) {
								resourceUseful.put(rule2.getRLemma(), String.format("%s\t==> %s", ruleOut, rule2Out));
							}
						}
					}
					System.out.printf("\n");
				}
				System.out.printf("\n");
			}
		}
		
		System.out.printf("************************\nOnly useful rules for spec:\n\n");
		for (String resourceName : useful.keySet()) {
			int count = 0;
			Map<String, Collection<String>> multi = useful.get(resourceName).asMap();
			for (String specWord : multi.keySet()) {
				Collection<String> rulesOut = multi.get(specWord);
				count += rulesOut.size()-1; //-1 because of EMPTY
			}
			System.out.printf("%s (%s rules):\n", resourceName, count);
			for (String specWord : multi.keySet()) {
				System.out.printf("\t%s\n", specWord);
				Collection<String> rulesOut = Lists.newArrayList(multi.get(specWord));
				rulesOut.remove(EMPTY);
				for (String ruleOut : rulesOut) {
					System.out.printf("\t\t%s\n", ruleOut);
				}
			}
		}
	}
	
	public static void mainOrig(String[] args) throws Exception {
		System.err.println("Hello, World!!!");
		final PartOfSpeech N = new ByCanonicalPartOfSpeech(CanonicalPosTag.NN.name());
		final PartOfSpeech V = new ByCanonicalPartOfSpeech(CanonicalPosTag.V.name());
		final PartOfSpeech ADJ = new ByCanonicalPartOfSpeech(CanonicalPosTag.ADJ.name());

		//String[] specWords = {"force", "israel", "Israel", "terrorist", "Terrorist", "man", "demonstrator", "gunman"};
		//String[] specWords = {"Palestinian", "palestinian", "Fallujah", "fallujah", "warship", "soldier", "cafe", "businessman", "car", "garage", "woman"};
//		String[] specWords = {	"attack", "clash", "conflict", "fighting", "gunfire", "shoot", "bomb", "explode",
//				"battle", "coup", "war", "terrorism", "terrorist", "activity", "blow", "throw",
//				"stabbing", "explosion", "fire"};
//		String[] specWords = {"harm", "injure", "injured", "hurt", "wounded", "hospitalized", "disabled"};
//		String[] specWords = {"elect", "elected", "election"};
		//String[] specWords = {"gas", "bullets"};
		//String[] specWords = {"bomb", "weapon"};
		
		// Be-Born Predicate
		String[] specWords = {"he", "I", "Jane Doe", "Jesus", "John Bobert Bond", "Osama bin Laden", "Palestinian", "people", "person", "Shenson", "suspect"};
		
		String text = "U.S.";
		
		String logName = String.format("C:/Temp/redis/%1$tH_%1$tM_%1$tS__%2$s.log", new Date(), text);
		Tee tee = new Tee(System.out);
		tee.add(new PrintStream(logName));
		System.setOut(tee);
		checkKnowledgeResources(text, N, Arrays.asList(specWords));

	}

	public static void main(String[] args) throws Exception {
		System.err.printf("Hello, World!!!\nGot these %s args: %s\nCaveat:  we are not handling derivations. I hope we can live with that.\n\n", args.length, Arrays.asList(args));
		String subtype = args[0];
		String text = args[1].replace("_", " ");

		Controller controller = new Controller();
		controller.setValueFromArguments(StringUtils.split(CONTROLLER_PARAMS), false);
		Perceptron.controllerStatic = controller;

		final PartOfSpeech N = new ByCanonicalPartOfSpeech(CanonicalPosTag.NN.name());
		final PartOfSpeech V = new ByCanonicalPartOfSpeech(CanonicalPosTag.V.name());
		final PartOfSpeech ADJ = new ByCanonicalPartOfSpeech(CanonicalPosTag.ADJ.name());
		Map<String, PartOfSpeech> poses = Maps.newLinkedHashMap();
		poses.put("N", N);
		poses.put("V", V);
		poses.put("ADJ", ADJ);
		PartOfSpeech pos = poses.get(args[2].toUpperCase());
		if (pos == null)
		{
			System.err.printf("Invalid POS: %s\n", args[2]);
			return;
		}

		List<String> allSpecs = SpecHandler.readSpecListFile(new File("src/main/resources/specs/speclist-full.txt"));
		TypesContainer types = new TypesContainer(allSpecs, false);
		JCas spec = types.namedSpecs.get(subtype);
		if (spec == null)
		{
			System.err.printf("Invalid subtype: %s\n", subtype);
			return;
		}
		List<String> seeds = JCasUtil.toText(JCasUtil.select(spec.getView(SpecAnnotator.TOKEN_VIEW), PredicateSeed.class));

		String logName = String.format("TryLexicalResource_%1$tH_%1$tM_%1$tS__%2$s.log", new Date(), args[0]);
		Tee tee = new Tee(System.out);
		tee.add(new PrintStream(logName));
		System.setOut(tee);

		checkKnowledgeResources(text, pos, seeds);
	}
}
