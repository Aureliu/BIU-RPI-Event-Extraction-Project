package edu.cuny.qc.scorer.mechanism;

import static edu.cuny.qc.scorer.mechanism.WordNetSignalMechanism.HYPERNYM_RELATIONS;

import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.scorer.ArgumentExampleScorer;
import edu.cuny.qc.scorer.ArgumentInUsageSampleScorer;
import edu.cuny.qc.scorer.Derivation;
import edu.cuny.qc.scorer.IntersectAiusAndExamples;
import edu.cuny.qc.scorer.Juxtaposition;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.Aggregator.Any;
import edu.cuny.qc.scorer.mechanism.DependencySignalMechanism.SameLinkDepFlatPrepUp3NoContext;
import edu.cuny.qc.scorer.mechanism.DependencySignalMechanism.SameLinkDepFlatUp2NoContext;
import edu.cuny.qc.scorer.mechanism.DependencySignalMechanism.SameLinkDepFlatUp3NoContext;
import edu.cuny.qc.scorer.mechanism.DependencySignalMechanism.SameLinkDepPrepUp2NoContext;
import edu.cuny.qc.scorer.mechanism.DependencySignalMechanism.SameLinkDepUp2NoContext;
import edu.cuny.qc.scorer.mechanism.DependencySignalMechanism.SameLinkDepUp3NoContext;
import edu.cuny.qc.scorer.mechanism.WordNetSignalMechanism.WordnetArgumentScorer;
import edu.cuny.qc.scorer.mechanism.WordNetSignalMechanism.WordnetDervRltdDeriver;

public class IntersectSignalMechanism extends SignalMechanism {

	public IntersectSignalMechanism(Controller controller) throws SignalMechanismException {
		super(controller);
	}

	@Override
	public void addScorers() throws Exception {
		switch (controller.featureProfile) {
		case INTER1:
			ArgumentInUsageSampleScorer[] aiuses1 = new ArgumentInUsageSampleScorer[] {SameLinkDepUp2NoContext.inst};
			//ArgumentInUsageSampleScorer[] aiuses2 = new ArgumentInUsageSampleScorer[] {SameLinkDepFlatUp2NoContext.inst};
			ArgumentInUsageSampleScorer[] aiuses3 = new ArgumentInUsageSampleScorer[] {SameLinkDepPrepUp2NoContext.inst};
			//ArgumentInUsageSampleScorer[] aiuses4 = new ArgumentInUsageSampleScorer[] {SameLinkDepUp3NoContext.inst};
			ArgumentInUsageSampleScorer[] aiuses5 = new ArgumentInUsageSampleScorer[] {SameLinkDepFlatUp3NoContext.inst};
			ArgumentInUsageSampleScorer[] aiuses6 = new ArgumentInUsageSampleScorer[] {SameLinkDepFlatPrepUp3NoContext.inst};
			ArgumentExampleScorer[] examples1 = new ArgumentExampleScorer[] {new WordnetArgumentScorer(HYPERNYM_RELATIONS, Juxtaposition.ANCESTOR, 4)};
			addArgumentDependent(new ScorerData("IN_U2__HYP4",		new IntersectAiusAndExamples(aiuses1, examples1), WordnetDervRltdDeriver.inst, Derivation.NONE, -1, 1, null, Any.inst));
			//addArgumentDependent(new ScorerData("IN_FU2__HYP4",		new IntersectAiusAndExamples(aiuses2, examples1), WordnetDervRltdDeriver.inst, Derivation.NONE, -1, 1, null, Any.inst));
			addArgumentDependent(new ScorerData("IN_PU2__HYP4",		new IntersectAiusAndExamples(aiuses3, examples1), WordnetDervRltdDeriver.inst, Derivation.NONE, -1, 1, null, Any.inst));
			//addArgumentDependent(new ScorerData("IN_U3__HYP4",		new IntersectAiusAndExamples(aiuses4, examples1), WordnetDervRltdDeriver.inst, Derivation.NONE, -1, 1, null, Any.inst));
			addArgumentDependent(new ScorerData("IN_FU3__HYP4",		new IntersectAiusAndExamples(aiuses5, examples1), WordnetDervRltdDeriver.inst, Derivation.NONE, -1, 1, null, Any.inst));
			addArgumentDependent(new ScorerData("IN_FPU3__HYP4",	new IntersectAiusAndExamples(aiuses6, examples1), WordnetDervRltdDeriver.inst, Derivation.NONE, -1, 1, null, Any.inst));
			break;
		default:
			break;
		}
	}

}
