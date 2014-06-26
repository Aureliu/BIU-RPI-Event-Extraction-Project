package edu.cuny.qc.scorer.mechanism;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.scorer.Aggregator;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.SignalMechanismSpecTokenIterator;
import edu.cuny.qc.util.BrownClusters;

public class BrownClustersSignalMechanism extends SignalMechanism {

	static {
		System.err.println("BrownClustersSignalMechanism: should still add the REAL (probably) method, probably something like making sure that one side's bit string is just a substring of the other.");
		System.err.println("BrownClustersSignalMechanism: Sometimes 'null' is returned by BrownClusters.getSingleton().getBrownCluster(str). WTF?");
	}
	
	@Override
	public void addScorers() {
		addTrigger(new ScorerData("BR_ALL_CLUSTERS_TOK",		SameAllClustersToken.inst,				Aggregator.Any.inst		));
		addTrigger(new ScorerData("BR_ALL_CLUSTERS_LEM",		SameAllClustersLemma.inst,				Aggregator.Any.inst		));
		addTrigger(new ScorerData("BR_LONGEST_CLUSTER_TOK",		SameLongestClusterToken.inst,			Aggregator.Any.inst		));
		addTrigger(new ScorerData("BR_LONGEST_CLUSTER_LEM",		SameLongestClusterLemma.inst,			Aggregator.Any.inst		));
	}

	public BrownClustersSignalMechanism() throws SignalMechanismException {
		super();
	}

	private static class SameAllClustersToken extends SignalMechanismSpecTokenIterator {
		public static final SameAllClustersToken inst = new SameAllClustersToken();
		@Override
		public Boolean calcTokenBooleanScore(Token text, Map<Class<?>, Object> textTriggerTokenMap, Token spec) throws SignalMechanismException
		{
			String textToken = text.getCoveredText();
			String specToken = spec.getCoveredText();
			List<String> textClusters = getBrownCluster(textToken);
			List<String> specClusters = getBrownCluster(specToken);
			if (textClusters == null || specClusters == null) {
				return false;
			}
			return textClusters.equals(specClusters);
		}
	}

	private static class SameAllClustersLemma extends SignalMechanismSpecTokenIterator {
		public static final SameAllClustersToken inst = new SameAllClustersToken();
		@Override
		public Boolean calcTokenBooleanScore(Token text, Map<Class<?>, Object> textTriggerTokenMap, Token spec) throws SignalMechanismException
		{
			String textLemma = text.getLemma().getValue();
			String specLemma = spec.getLemma().getValue();
			List<String> textClusters = getBrownCluster(textLemma);
			List<String> specClusters = getBrownCluster(specLemma);
			if (textClusters == null || specClusters == null) {
				return false;
			}
			return textClusters.equals(specClusters);
		}
	}

	private static class SameLongestClusterToken extends SignalMechanismSpecTokenIterator {
		public static final SameLongestClusterToken inst = new SameLongestClusterToken();
		@Override
		public Boolean calcTokenBooleanScore(Token text, Map<Class<?>, Object> textTriggerTokenMap, Token spec) throws SignalMechanismException
		{
			String textToken = text.getCoveredText();
			String specToken = spec.getCoveredText();
			List<String> textClusters = getBrownCluster(textToken);
			List<String> specClusters = getBrownCluster(specToken);
			if (textClusters == null || specClusters == null) {
				return false;
			}
			String textLongestCluster = textClusters.get(textClusters.size()-1);
			String specLongestCluster = specClusters.get(specClusters.size()-1);
			return textLongestCluster.equals(specLongestCluster);
		}
	}

	private static class SameLongestClusterLemma extends SignalMechanismSpecTokenIterator {
		public static final SameLongestClusterLemma inst = new SameLongestClusterLemma();
		@Override
		public Boolean calcTokenBooleanScore(Token text, Map<Class<?>, Object> textTriggerTokenMap, Token spec) throws SignalMechanismException
		{
			String textLemma = text.getLemma().getValue();
			String specLemma = spec.getLemma().getValue();
			List<String> textClusters = getBrownCluster(textLemma);
			List<String> specClusters = getBrownCluster(specLemma);
			if (textClusters == null || specClusters == null) {
				return false;
			}
			String textLongestCluster = textClusters.get(textClusters.size()-1);
			String specLongestCluster = specClusters.get(specClusters.size()-1);
			return textLongestCluster.equals(specLongestCluster);
		}
	}

	
	private static List<String> getBrownCluster(String str) {
		if (cacheCluster.containsKey(str)) {
			return cacheCluster.get(str);
		}
		else {
			List<String> result = BrownClusters.getSingleton().getBrownCluster(str);
			if (result == null) {
				//System.err.printf("BrownClustersSignalMechanism: cluster==null for: '%s'\n", str);
			}
			cacheCluster.put(str, result);
			return result;
		}
	}
	
	private static Map<String, List<String>> cacheCluster = new LinkedHashMap<String, List<String>>();
}
