package edu.cuny.qc.util;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Strings;

import edu.cuny.qc.perceptron.core.Perceptron;

public class WeightTracer {

	private List<String> FEATURES_TO_PRINT = Arrays.asList(new String[] {
			"BigramFeature:	WORDNET_SAME_SYNSET",
			"BigramFeature:	WORDNET_SPEC_HYPERNYM",
			"BigramFeature:	WORDNET_FAKE_LETTER_E",
			});

	
	public WeightTracer(Perceptron perceptron) {
		this.perceptron = perceptron;
	}

	public String getFeaturesString() {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<FEATURES_TO_PRINT.size(); i++) {
			sb.append(String.format("%f|", perceptron.getWeights().get(FEATURES_TO_PRINT.get(i))));
		}
		for (int i=0; i<FEATURES_TO_PRINT.size(); i++) {
			String out = null;
			if (perceptron.getAvg_weights() == null) {
				out = "N/A";
			}
			else {
				out = String.format("%f", perceptron.getAvg_weights().get(FEATURES_TO_PRINT.get(i)));
			}
			sb.append(String.format("%s|", out));
		}
		return sb.toString();
	}
	
	public String getFeaturesStringTitle() {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<FEATURES_TO_PRINT.size(); i++) {
			sb.append("weight:"+FEATURES_TO_PRINT.get(i)+"|");
		}
		for (int i=0; i<FEATURES_TO_PRINT.size(); i++) {
			sb.append("avg:" + FEATURES_TO_PRINT.get(i)+"|");
		}
		return sb.toString();
	}
	
	public String getFeaturesStringSkip() {
		return Strings.repeat("|", FEATURES_TO_PRINT.size()*2);
	}
	
	
	private Perceptron perceptron = null;
}
