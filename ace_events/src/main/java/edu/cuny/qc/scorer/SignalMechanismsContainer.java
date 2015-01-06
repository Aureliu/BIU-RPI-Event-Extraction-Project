package edu.cuny.qc.scorer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.jcas.JCas;

import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SignalType;
import edu.cuny.qc.scorer.mechanism.BrownClustersSignalMechanism;
import edu.cuny.qc.scorer.mechanism.DependencySignalMechanism;
import edu.cuny.qc.scorer.mechanism.IntersectSignalMechanism;
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
		signalMechanisms.add(new IntersectSignalMechanism(controller));
		
		for (SignalMechanism mechanism : signalMechanisms) {
			triggerScorers.addAll(mechanism.scorers.get(SignalType.TRIGGER));
			argumentScorers.addAll(mechanism.scorers.get(SignalType.ARGUMENT_DEPENDENT));
			argumentScorers.addAll(mechanism.scorers.get(SignalType.ARGUMENT_FREE));
		}
	}
	
	public void entrypointSignalMechanismsPreSpec(JCas spec) throws SignalMechanismException {
		for (SignalMechanism signalMechanism : signalMechanisms) {
			signalMechanism.entrypointPreSpec(spec);
		}
	}
	public void entrypointSignalMechanismsPreSentence(SentenceInstance inst) throws SignalMechanismException {
		for (SignalMechanism signalMechanism : signalMechanisms) {
			signalMechanism.entrypointPreSentence(inst);
		}
	}
	public void entrypointSignalMechanismsPreDocument(Document doc) throws SignalMechanismException {
		SignalMechanism currSM = null;
		try {
			for (SignalMechanism signalMechanism : signalMechanisms) {
				currSM = signalMechanism;
				signalMechanism.entrypointPreDocument(doc);
			}
		}
		catch (SignalMechanismException e) {
			throw new SignalMechanismException(String.format("Got exception processing doc '%s'%s", doc.docID,
					currSM!=null?" in signal mechanism " + currSM.getClass().getSimpleName():""), e);
		}
	}
//	public void entrypointSignalMechanismsPreDocumentBunch() {
//		for (SignalMechanism signalMechanism : signalMechanisms) {
//			signalMechanism.entrypointPreDocumentBunch();
//		}
//	}

	public void close() {
		for (SignalMechanism signalMechanism : signalMechanisms) {
			signalMechanism.close();
		}
	}

	public String toString() {
		return signalMechanisms.toString();
	}
}
