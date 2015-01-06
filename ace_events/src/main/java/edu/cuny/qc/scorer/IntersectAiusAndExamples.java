package edu.cuny.qc.scorer;

import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.Argument;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentExample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceMention;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public class IntersectAiusAndExamples extends ArgumentInUsageSampleScorer {
	private static final long serialVersionUID = -7387738918362552305L;

	ArgumentInUsageSampleScorer[] aiusScorers;
	ArgumentExampleScorer[] exampleScorers;

	public IntersectAiusAndExamples(ArgumentInUsageSampleScorer[] aiusScorers, ArgumentExampleScorer[] exampleScorers) {
		this.aiusScorers = aiusScorers;
		this.exampleScorers = exampleScorers;
	}

	@Override
	public void prepareCalc(JCas spec, Token textTriggerToken, Map<Class<?>, Object> textTriggerTokenMap, Argument argument, AceMention aceMention, JCas docJCas, String docAllText, ScorerData scorerData) throws SignalMechanismException {
		// Here we set that we iterate AIUSes, just like any normal ArgumentInUsageSampleScorer
		super.prepareCalc(spec, textTriggerToken, textTriggerTokenMap, argument, aceMention, docJCas, docAllText, scorerData);
		
		// And here, we're prepping our inner scorers
		for (ArgumentInUsageSampleScorer scorer : aiusScorers) {
			scorer.prepareCalc(spec, textTriggerToken, textTriggerTokenMap, argument, aceMention, docJCas, docAllText, scorerData);
		}
		for (ArgumentExampleScorer scorer : exampleScorers) {
			scorer.prepareCalc(spec, argument, aceMention, docAllText, docJCas, scorerData);
		}
	}

	@Override
	public Boolean calcBooleanArgumentDependentScore(ArgumentInUsageSample spec)
			throws SignalMechanismException {
		// "Or" deps
		Boolean aiusResult = false;
		for (ArgumentInUsageSampleScorer scorer : aiusScorers) {
			aiusResult = scorer.calcBooleanArgumentDependentScore(spec);
			if (aiusResult) {
				break;
			}
		}
		
		// "Or" frees
		Boolean exampleResult = false;
		ArgumentExample spec2 = spec.getArgumentExample(); 
		for (ArgumentExampleScorer scorer : exampleScorers) {
			exampleResult = scorer.calcBooleanArgumentFreeScore(spec2);
			if (exampleResult) {
				break;
			}
		}
		
		// "And" the deps and the frees
		Boolean result = aiusResult && exampleResult;
//		System.out.printf("%s: aiusScorers(%s)=[%s,...], exampleScorers(%s)=[%s,...], spec=%s, spec2=%s, aiusResult=%s, exampleResult=%s, result=%s\n",
//				getClass().getSimpleName(), aiusScorers.length, aiusScorers[0].getTypeName(), exampleScorers.length, exampleScorers[0].getTypeName(),
//				UimaUtils.annotationToString(spec), UimaUtils.annotationToString(spec2), aiusResult, exampleResult, result);
		return result;
	}

//	@Override
//	protected void prepareSpecIteration(JCas spec) throws SignalMechanismException {
//		// This should never happen as we override prepareCalc(), which is the only one that should call this method
//		throw new IllegalStateException("prepareSpecIteration() should never be called from " + this.getClass().getSimpleName());
//	}

	@Override
	public void addToHistory() throws SignalMechanismException {
		// Not supporting history, mainly because I don't feel like dealing with that right now, and I haven't been using it anyway
	}

	@Override
	public Boolean calcBoolArgumentInUsageSampleScore(Token textTriggerToken, AceMention textArgMention, Annotation textArgHeadAnno, ArgumentInUsageSample specAius, ScorerData scorerData)	throws SignalMechanismException {
		// This should never happen as we override calcBooleanArgumentDependentScore(), which is the only one that should call this method
		throw new IllegalStateException("calcBoolArgumentInUsageSampleScore() should never be called from " + this.getClass().getSimpleName());
	}
}
