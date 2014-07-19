package edu.cuny.qc.scorer.mechanism;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.common.collect.Sets;

import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.scorer.BasicRulesQuery;
import edu.cuny.qc.scorer.Deriver;
import edu.cuny.qc.scorer.DeriverException;
import edu.cuny.qc.scorer.FullRulesQuery;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.util.NomlexPlus;
import edu.cuny.qc.util.NomlexPlus.NomlexPlusDictionary;
import edu.cuny.qc.util.PosMap;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

public class NomlexSignalMechanism extends SignalMechanism {

	public NomlexSignalMechanism(Controller controller) throws SignalMechanismException {
		super(controller);
		try {
			// no need to store the NomlexPlus object - we load it here,
			// and all relevant info is references by the NomlexPlusDictionary enum values
			new NomlexPlus(nomlexFile);
		} catch (IOException e) {
			throw new SignalMechanismException(e);
		}
	}

	@Override
	public void addScorers() throws Exception {
		// No scorers for now!
	}

	public static class NomlexDeriver extends Deriver {
		private static final long serialVersionUID = -4749124787568723978L;
		public static final NomlexDeriver inst = new NomlexDeriver();
		private NomlexDeriver() {} //private c-tor
		@Override public String getSuffix() { return "-NmxDrv"; }

		@Override
		public Set<BasicRulesQuery> buildDerivations(FullRulesQuery query) throws DeriverException {
			try {
				Set<BasicRulesQuery> result = Sets.newHashSet();
				BasicRulesQuery q = query.basicQuery;
				CanonicalPosTag pos = q.lPos.getCanonicalPosTag();
				for (NomlexPlusDictionary dict : derivationDicts) {
					if (pos==dict.tagOfOther) {
						Collection<String> others = dict.mapToNoun.get(q.lLemma);
						for (String other : others) {
							PartOfSpeech otherPos = PosMap.byCanonical.get(dict.tagOfOther);
							result.add(new BasicRulesQuery(other, otherPos, null, null)); 
						}
					}
				}
				return result;
			} catch (ExecutionException e) {
				throw new DeriverException(e);
			}
		}
	}
	
	private static final File nomlexFile = new File("src/main/resources/data/nomlex-plus.txt");
	private static NomlexPlusDictionary[] derivationDicts =
		{NomlexPlusDictionary.NOM, NomlexPlusDictionary.NOMADJ, NomlexPlusDictionary.ABLENOM}; 
}
