package edu.cuny.qc.scorer.mechanism;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.jcas.tcas.Annotation;

import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.scorer.ArgumentExampleScorer;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.resource.SimilarityStorageBasedLexicalResource;
import eu.excitementproject.eop.distsim.util.Configuration;

/**
 * TODO:
 * - copy the XML config files (from the locations written in TryRedis) into the project (/conf). Also fix their
 *   contents (like paths), somehow in a way that will work transparently also on the server.
 * - finish init() - load all  resources.
 * - write calcBoolArgumentExampleScore(). inside the for loop I should calc for each resource. Of course use some caches here.
 *   Take most implementation from TryRedis.
 * - write addScorers(): basically different configs according to the consts lists at the bottom. Don't do more than ~20.
 *
 */
public class RedisSignalMechanism extends SignalMechanism {

	public RedisSignalMechanism(Controller controller) throws SignalMechanismException {
		super(controller);
	}

	@Override
	public void init() {
		/* TODO 18.11.2014
		BAP = loadRedis(???);
		...
		...
		*/
	}
	
	@Override
	public void close() {
		// Currently nothing, maybe later
		super.close();
	}
	
	@Override
	public void addScorers() {
		switch (controller.featureProfile) {
		case TOKEN_BASELINE:
		case ANALYSIS1:
		case ANALYSIS2:
			break;
		case ANALYSIS3:
			/* TODO 18.11.2014

			...
			*/
			break;
		case NORMAL:
			/* TODO 18.11.2014
			...
			*/
			break;
		default:
			//throw new IllegalStateException("Bad FeatureProfile enum value: " + controller.featureProfile);
			break;
		}
	}
	
//	public enum RedisResource {
//		BAP,
//		LIN_DEP,
//		LIN_PROX
//	}
	
	public static class RedisArgumentScorer extends ArgumentExampleScorer {
		private static final long serialVersionUID = -5870102640752369612L;

		Set<LexicalResource<? extends RuleInfo>> resources;
		List<Integer> rankThresholds;
		List<Double> confidenceThresholds1;
		List<Double> confidenceThresholds2;
		int numRules;
		int minimalAmount;
		
		public RedisArgumentScorer(Set<LexicalResource<? extends RuleInfo>> resources,
				List<Integer> rankThresholds, List<Double> confidenceThresholds1,
				List<Double> confidenceThresholds2, int numRules, int minimalAmount) {
			this.resources = resources;
			this.rankThresholds = rankThresholds;
			this.confidenceThresholds1 = confidenceThresholds1;
			this.confidenceThresholds2 = confidenceThresholds2;
			this.numRules = numRules;
			this.minimalAmount = minimalAmount;
		}
		
		@Override public String getTypeName() {
			String resourcesStr="";
			if (resources.equals(ALL_RESOURCES)) {resourcesStr = "ALL_RESOURCES";}
			else if (resources.equals(BAP_LIN_DEP)) {resourcesStr = "BAP_LIN_DEP";}
			else if (resources.equals(BAP_LIN_PROX)) {resourcesStr = "BAP_LIN_PROX";}
			else if (resources.equals(LIN_DEP_LIN_PROX)) {resourcesStr = "LIN_DEP_LIN_PROX";}
			else if (resources.size()==1) {
				LexicalResource<? extends RuleInfo> res = resources.iterator().next();
				if (res == BAP) {resourcesStr = "BAP";}
				if (res == LIN_DEP) {resourcesStr = "LIN_DEP";}
				if (res == LIN_PROX) {resourcesStr = "LIN_PROX";}
			}
			else {
				resourcesStr = StringUtils.join(this.resources, "_");
			}
			
			String ranksStr="";
			if (rankThresholds.equals(RANKS_20_1)) {ranksStr="RANKS_20_1";}
			else if (rankThresholds.equals(RANKS_20_2)) {ranksStr="RANKS_20_2";}
			else if (rankThresholds.equals(RANKS_50_1)) {ranksStr="RANKS_50_1";}
			else {ranksStr=StringUtils.join(rankThresholds, "_");}
			
			String conf1Str="";
			if (confidenceThresholds1.equals(CONF1_20_1)) {ranksStr="CONF1_20_1";}
			else if (confidenceThresholds1.equals(CONF1_20_2)) {ranksStr="CONF1_20_2";}
			else if (confidenceThresholds1.equals(CONF1_50_1)) {ranksStr="CONF1_50_1";}
			else {ranksStr=StringUtils.join(confidenceThresholds1, "_");}
			
			String conf2Str="";
			if (confidenceThresholds2.equals(CONF2_20_1)) {ranksStr="CONF2_20_1";}
			else if (confidenceThresholds2.equals(CONF2_20_2)) {ranksStr="CONF2_20_2";}
			else if (confidenceThresholds2.equals(CONF2_50_1)) {ranksStr="CONF2_50_1";}
			else {ranksStr=StringUtils.join(confidenceThresholds2, "_");}
			
			return String.format("RS__%s__%s__%s__%s", resourcesStr, ranksStr, conf1Str, conf2Str);
		}

		@Override
		public Boolean calcBoolArgumentExampleScore(
				AceEntityMention corefeMention, Annotation headAnno,
				String textHeadTokenStr, PartOfSpeech textHeadTokenPos,
				String specStr, PartOfSpeech specPos, ScorerData scorerData)
				throws SignalMechanismException {

			int amountTrue = 0;
			for (LexicalResource<? extends RuleInfo> res : resources) {
				
			}
			return amountTrue >= minimalAmount;
		}
	}
	
	public static LexicalResource<? extends RuleInfo> loadRedis(String configFile) throws Exception {
	    ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(configFile)));
		ConfigurationParams confParams = confFile.getModuleConfiguration(Configuration.KNOWLEDGE_RESOURCE);
		LexicalResource<? extends RuleInfo> result = new SimilarityStorageBasedLexicalResource(confParams);
		return result;
	}

	public static LexicalResource<? extends RuleInfo> BAP;
	public static LexicalResource<? extends RuleInfo> LIN_DEP;
	public static LexicalResource<? extends RuleInfo> LIN_PROX;
	
	
	public static final Set<LexicalResource<? extends RuleInfo>> ALL_RESOURCES = ImmutableSet.of(BAP, LIN_DEP, LIN_PROX);
	public static final Set<LexicalResource<? extends RuleInfo>> BAP_LIN_DEP = ImmutableSet.of(BAP, LIN_DEP);
	public static final Set<LexicalResource<? extends RuleInfo>> BAP_LIN_PROX = ImmutableSet.of(BAP, LIN_PROX);
	public static final Set<LexicalResource<? extends RuleInfo>> LIN_DEP_LIN_PROX = ImmutableSet.of(LIN_DEP, LIN_PROX);

	public static final List<Integer> RANKS_20_1 = ImmutableList.of(5, 10, 15);
	public static final List<Double> CONF1_20_1 = ImmutableList.of(0.05, 0.05, 0.05, 0.05, 0.05);
	public static final List<Double> CONF2_20_1 = ImmutableList.of(0.05, 0.05, 0.1, 0.1, 0.14);

	public static final List<Integer> RANKS_20_2 = ImmutableList.of(5, 10);
	public static final List<Double> CONF1_20_2 = ImmutableList.of(0.05, 0.05, 0.05);
	public static final List<Double> CONF2_20_2 = ImmutableList.of(0.1, 0.1, 0.14);

	public static final List<Integer> RANKS_50_1 = ImmutableList.of(10, 20, 30);
	public static final List<Double> CONF1_50_1 = ImmutableList.of(0.05, 0.05, 0.05, 0.1, 0.1);
	public static final List<Double> CONF2_50_1 = ImmutableList.of(0.05, 0.05, 0.14, 0.14, 0.2);
}
