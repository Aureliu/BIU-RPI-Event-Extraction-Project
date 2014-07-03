package edu.cuny.qc.scorer;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.cuny.qc.scorer.mechanism.NomlexSignalMechanism;
import edu.cuny.qc.scorer.mechanism.WordNetSignalMechanism.WordnetDervRltdDeriver;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

public abstract class Deriver implements Serializable {
	private static final long serialVersionUID = 2232471629182535996L;

	public String getTypeName() {return getClass().getSimpleName().intern(); }
	
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
		private static final long serialVersionUID = -4080707104054016636L;
		public static final NoDerv inst = new NoDerv();
		private NoDerv() {} //private c-tor
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
		private static final long serialVersionUID = 4913245001094581246L;
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
		@Override public String getTypeName() {
			List<String> names = Lists.newArrayListWithCapacity(derivers.size());
			for (Deriver deriver : derivers) {
				names.add(deriver.getTypeName());
			}
			return String.format("%s(%s)", getClass().getSimpleName(), StringUtils.join(names, ","));
		}
		@Override public Set<BasicRulesQuery> buildDerivations(FullRulesQuery query) throws DeriverException {
			Set<BasicRulesQuery> result = Sets.newHashSet();
			for (Deriver deriver : derivers) {
				result.addAll(deriver.buildDerivations(query));
			}
			return result;
		}
		@Override public int hashCode() {
		     return new HashCodeBuilder(19, 39).append(derivers).toHashCode();
		}
		@Override public boolean equals(Object obj) {
			   if (obj == null) { return false; }
			   if (obj == this) { return true; }
			   if (obj.getClass() != getClass()) { return false; }
			   Join rhs = (Join) obj;
			   return new EqualsBuilder().appendSuper(super.equals(obj)).append(derivers, rhs.derivers).isEquals();
		}
	}
	
	public static final Deriver[] ALL_DERIVERS = {/*NoDerv.inst,*/ WordnetDervRltdDeriver.inst, NomlexSignalMechanism.NomlexDeriver.inst};

}
