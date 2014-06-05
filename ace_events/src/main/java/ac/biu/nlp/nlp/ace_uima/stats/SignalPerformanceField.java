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
		return Arrays.asList(new String[] {"gold", "signal", "F-correct", "F-precision", "I-recall", "I-F1", "I-correct", "I-precision", "I-recall", "I-F1"});
	}

	@Override
	public List<String> getValues() {
		Score s = evaluator.evaluate(elements, goldInstances);
		List<Double> doubles = getDoubles(s);
		List<String> result = new ArrayList<String>(doubles.size());
		for (Double d : doubles) {
			result.add("" + d);
		}
		return result;
	}
	
	public abstract List<Double> getDoubles(Score s);

	private List<SentenceAssignment> elements = new ArrayList<SentenceAssignment>();
	
	private static final Evaluator evaluator = new EvaluatorLoose();
	public static List<SentenceInstance> goldInstances;
	
	public static class TriggerSignalPerformanceField extends SignalPerformanceField {
		public TriggerSignalPerformanceField(FieldName name) { super(name);	}
		@Override public List<Double> getDoubles(Score s) {
			return Arrays.asList(new Double[] {s.count_trigger_gold, s.count_trigger_ans, s.count_trigger_correct, s.trigger_precision, s.trigger_recall, s.trigger_F1, s.count_trigger_correct_idt, s.trigger_precision_idt, s.trigger_recall_idt, s.trigger_F1_idt});
		}
	}
	public static class ArgumentSignalPerformanceField extends SignalPerformanceField {
		public ArgumentSignalPerformanceField(FieldName name) { super(name);	}
		@Override public List<Double> getDoubles(Score s) {
			return Arrays.asList(new Double[] {s.count_arg_gold, s.count_arg_ans, s.count_arg_correct, s.arg_precision, s.arg_recall, s.arg_F1, s.count_arg_correct_idt, s.arg_precision_idt, s.arg_recall_idt, s.arg_F1_idt});
		}
	}
}
