package edu.cuny.qc.scorer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.types.SignalType;
import edu.cuny.qc.scorer.mechanism.BrownClustersSignalMechanism;
import edu.cuny.qc.scorer.mechanism.DependencySignalMechanism;
import edu.cuny.qc.scorer.mechanism.POSSignalMechanism;
import edu.cuny.qc.scorer.mechanism.PlainSignalMechanism;
import edu.cuny.qc.scorer.mechanism.WordNetSignalMechanism;

public class SignalMechanismsContainer {
	public List<SignalMechanism> signalMechanisms;
	public Set<ScorerData> triggerScorers = new LinkedHashSet<ScorerData>();
	public Set<ScorerData> argumentScorers = new LinkedHashSet<ScorerData>();

	public SignalMechanismsContainer(Controller controller) throws SignalMechanismException {
		signalMechanisms = new ArrayList<SignalMechanism>();
				
		signalMechanisms.add(new PlainSignalMechanism(controller));
		signalMechanisms.add(new WordNetSignalMechanism(controller));
		signalMechanisms.add(new BrownClustersSignalMechanism(controller));
		signalMechanisms.add(new POSSignalMechanism(controller));
		signalMechanisms.add(new DependencySignalMechanism(controller));
		
		for (SignalMechanism mechanism : signalMechanisms) {
			triggerScorers.addAll(mechanism.scorers.get(SignalType.TRIGGER));
		}
	}
	
	public void logSignalMechanismsPreSentence() {
		for (SignalMechanism signalMechanism : signalMechanisms) {
			signalMechanism.logPreSentence();
		}
	}
	public void logSignalMechanismsPreDocument() {
		for (SignalMechanism signalMechanism : signalMechanisms) {
			signalMechanism.logPreDocument();
		}
	}
	public void logSignalMechanismsPreDocumentBunch() {
		for (SignalMechanism signalMechanism : signalMechanisms) {
			signalMechanism.logPreDocumentBunch();
		}
	}

	public void close() {
		for (SignalMechanism signalMechanism : signalMechanisms) {
			signalMechanism.close();
		}
	}

}
