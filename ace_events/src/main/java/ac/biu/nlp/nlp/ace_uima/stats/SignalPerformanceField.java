package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.cuny.qc.perceptron.core.Evaluator;
import edu.cuny.qc.perceptron.core.Evaluator.Score;
import edu.cuny.qc.perceptron.core.EvaluatorLoose;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import eu.excitementproject.eop.common.utilities.math.MathUtils;

public abstract class SignalPerformanceField extends StatsField {

	public SignalPerformanceField(FieldName name) {
		super(name);
	}

	@Override
	public void addElement(Object element) {
		SentenceAssignment assn = (SentenceAssignment) element;
		elements.add(assn);
	}

	@Override
	public List<String> getSubtitles() {
		//return Arrays.asList(new String[] {"gold", "signal", "F-correct", "F-precision", "F-recall", "F-F1", "I-correct", "I-precision", "I-recall", "I-F1", "F=I"});
		// CONCLUSION: Well, it seems that F and I are always identical! so no need for them!!!
		return Arrays.asList(new String[] {"total", "gold", "signal", "correct", "precision", "recall", "F1", "GoldEntropy", "SignalEntropy", "NotSignalEntropy", "WeightedAvg(Signal,NonSignal)Entropy", "InfoGain"});
	}

	@Override
	public List<String> getValues() {
//		List<SentenceInstance> goldInstances = new ArrayList<SentenceInstance>(elements.size());
//		for (SentenceAssignment assn : elements) {
//			goldInstances.add(assn.inst);
//		}
		List<SentenceAssignment> goldTargets = new ArrayList<SentenceAssignment>(elements.size());
		for (SentenceAssignment assn : elements) {
			goldTargets.add(assn.target);
		}
		//Score s = evaluator.evaluate(elements, goldTargets, -1, false);
		
		List<Double> doubles = getDoubles(elements, goldTargets);
		List<String> result = new ArrayList<String>(doubles.size());
		for (Double d : doubles) {
			result.add("" + d);
		}
		//result.add("" + (getLastBool(s)?"T":"F")); //see CONCLUSION
		return result;
	}
	
	public abstract List<Double> getDoubles(List<SentenceAssignment> results, List<SentenceAssignment> goldTargets);
	public abstract Boolean getLastBool(Score s);

	private List<SentenceAssignment> elements = new ArrayList<SentenceAssignment>();
	
	//public static List<SentenceInstance> goldInstances;
	
	public static class TriggerSignalPerformanceField extends SignalPerformanceField {
		public TriggerSignalPerformanceField(FieldName name) { super(name);	}
		private static final Evaluator evaluator = new Evaluator();
		@Override public List<Double> getDoubles(List<SentenceAssignment> results, List<SentenceAssignment> goldTargets) {
			//return Arrays.asList(new Double[] {s.count_trigger_gold, s.count_trigger_ans, s.count_trigger_correct, s.trigger_precision, s.trigger_recall, s.trigger_F1, s.count_trigger_correct_idt, s.trigger_precision_idt, s.trigger_recall_idt, s.trigger_F1_idt});
			// see CONCLUSION
			Score s = new Score(-1);
			evaluator.evaluteTrigger(results, goldTargets, s);			
			s.calculateHarmonic_mean();

			return Arrays.asList(new Double[] {s.count_trigger_total, s.count_trigger_gold, s.count_trigger_ans, s.count_trigger_correct, s.trigger_precision, s.trigger_recall, s.trigger_F1,
					s.trigger_info_gain.goldEntropy, s.trigger_info_gain.ansTrueEntropy, s.trigger_info_gain.ansFalseEntropy, s.trigger_info_gain.weightedAverageAnsEntropy, s.trigger_info_gain.informationGain});
		}
		@Override public Boolean getLastBool(Score s) {
			return s.trigger_F1==s.trigger_F1_idt;
		}
	}
	public static class ArgumentDependentSignalPerformanceField extends SignalPerformanceField {
		public ArgumentDependentSignalPerformanceField(FieldName name) { super(name);	}
		private static final Evaluator evaluator = new Evaluator();
		@Override public List<Double> getDoubles(List<SentenceAssignment> results, List<SentenceAssignment> goldTargets) {
			//return Arrays.asList(new Double[] {s.count_arg_gold, s.count_arg_ans, s.count_arg_correct, s.arg_precision, s.arg_recall, s.arg_F1, s.count_arg_correct_idt, s.arg_precision_idt, s.arg_recall_idt, s.arg_F1_idt});
			// see CONCLUSION
			Score s = new Score(-1);
			evaluator.evaluteArgument(results, goldTargets, null, s);			
			s.calculateHarmonic_mean();

			return Arrays.asList(new Double[] {s.count_arg_total, s.count_arg_gold, s.count_arg_ans, s.count_arg_correct, s.arg_precision, s.arg_recall, s.arg_F1,
					s.arg_info_gain.goldEntropy, s.arg_info_gain.ansTrueEntropy, s.arg_info_gain.ansFalseEntropy, s.arg_info_gain.weightedAverageAnsEntropy, s.arg_info_gain.informationGain});
		}
		@Override public Boolean getLastBool(Score s) {
			return s.arg_F1==s.arg_F1_idt;
		}
	}
	public static class ArgumentFreeSignalPerformanceField extends SignalPerformanceField {
		private static final Evaluator evaluator = new EvaluatorLoose();
		public ArgumentFreeSignalPerformanceField(FieldName name) { super(name);	}
		@Override public List<Double> getDoubles(List<SentenceAssignment> results, List<SentenceAssignment> goldTargets) {
			//return Arrays.asList(new Double[] {s.count_arg_gold, s.count_arg_ans, s.count_arg_correct, s.arg_precision, s.arg_recall, s.arg_F1, s.count_arg_correct_idt, s.arg_precision_idt, s.arg_recall_idt, s.arg_F1_idt});
			// see CONCLUSION
			Score s = new Score(-1);
			evaluator.evaluteArgument(results, goldTargets, 0, s);			
			s.calculateHarmonic_mean();
			
			return Arrays.asList(new Double[] {s.count_arg_total, s.count_arg_gold, s.count_arg_ans, s.count_arg_correct, s.arg_precision, s.arg_recall, s.arg_F1,
					s.arg_info_gain.goldEntropy, s.arg_info_gain.ansTrueEntropy, s.arg_info_gain.ansFalseEntropy, s.arg_info_gain.weightedAverageAnsEntropy, s.arg_info_gain.informationGain});
		}
		@Override public Boolean getLastBool(Score s) {
			return s.arg_F1==s.arg_F1_idt;
		}
	}
}
