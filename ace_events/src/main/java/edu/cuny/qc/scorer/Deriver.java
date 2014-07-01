package edu.cuny.qc.scorer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.cuny.qc.scorer.mechanism.NomlexSignalMechanism;
import edu.cuny.qc.scorer.mechanism.WordNetSignalMechanism.WordnetDervRltdDeriver;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

public abstract class Deriver {
	public String getTypeName() {return getClass().getSimpleName(); }
	
	public abstract String getSuffix();
	public abstract Set<BasicRulesQuery> buildDerivations(FullRulesQuery query) throws DeriverException;
	//public abstract Class<?> getTokenAnnotationMarker();
	
	public Set<BasicRulesQuery> getDerivations(String lemma, PartOfSpeech pos, boolean takeOriginal, boolean takeDerivations, int senseNum) throws DeriverException {
		Set<BasicRulesQuery> result = new HashSet<BasicRulesQuery>(3);
		BasicRulesQuery q = new BasicRulesQuery(lemma, pos, null, null); // when a BasicRulesQuery represents only one lemma/POS, it's always on the Left side
		if (takeOriginal) {
			result.add(q);
		}
		if (takeDerivations) {
			//Class<?> tokenAnnotationMarker = getTokenAnnotationMarker();
			//Set<BasicRulesQuery> derivations = (Set<BasicRulesQuery>) textTriggerTokenMap.get(tokenAnnotationMarker);
//			if (derivations == null) {
//				FullRulesQuery query = new FullRulesQuery(null, 1, null, senseNum, 0, q);
//				derivations = buildDerivations(query);
//				textTriggerTokenMap.put(tokenAnnotationMarker, derivations);
//			}
			FullRulesQuery query = new FullRulesQuery(senseNum, q);
			Set<BasicRulesQuery> derivations = buildDerivations(query);
			result.addAll(derivations);
		}
		return result;
	}
	
	public static class NoDerv extends Deriver {
		public static final NoDerv inst = new NoDerv();
		@Override public String getSuffix() { return ""; }
		@Override public Set<BasicRulesQuery> buildDerivations(FullRulesQuery query) throws DeriverException {
			throw new IllegalStateException("NoDerv Deriver is never supposed to get to this method");
		}
		@Override public Set<BasicRulesQuery> getDerivations(String lemma, PartOfSpeech pos, boolean takeOriginal, boolean takeDerivations, int senseNum) throws DeriverException {
			BasicRulesQuery q = new BasicRulesQuery(lemma, pos, null, null);
			return Collections.singleton(q);
		}
	}
	
	public static class Join extends Deriver {
		List<Deriver> derivers;
		public Join(Deriver... args) {
			derivers = Lists.newArrayList(args);
		}
		@Override public String getSuffix() {
			List<String> suffixes = Lists.newArrayListWithCapacity(derivers.size());
			for (Deriver deriver : derivers) {
				suffixes.add(deriver.getSuffix());
			}
			return "--" + StringUtils.join(suffixes, "-") + "-";
		}
		@Override public Set<BasicRulesQuery> buildDerivations(FullRulesQuery query) throws DeriverException {
			Set<BasicRulesQuery> result = Sets.newHashSet();
			for (Deriver deriver : derivers) {
				result.addAll(deriver.buildDerivations(query));
			}
			return result;
		}
	}
	
	public static final Deriver[] ALL_DERIVERS = {/*NoDerv.inst,*/ WordnetDervRltdDeriver.inst, NomlexSignalMechanism.NomlexDeriver.inst};

}
