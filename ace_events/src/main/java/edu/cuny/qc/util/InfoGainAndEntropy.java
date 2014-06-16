package edu.cuny.qc.util;

import org.apache.commons.math.util.MathUtils;

/**
 * Simply enough, this class has static methods that calculate Information Gain and Entropy.
 * 
 * @author Ofer Bronstein
 * @since June 2014
 */
public class InfoGainAndEntropy {
	
	static {
		System.err.println("InfoGainAndEntropy: getting NaN. It's when precision is either 0 or 1 (so there probably is some division in 0). fix somehow, maybe according to online presentation.");
	}

	public static InfoGainResult infoGain(double tp, double fp, double fn, double tn) {
		InfoGainResult result = new InfoGainResult();
		
		result.total = tp + fp + fn + tn;
		result.goldTrue = tp + fn;
		result.goldFalse = fp + tn;
		result.ansTrue = tp + fp;
		result.ansFalse = fn + tn;
				
		result.goldEntropy = entropy(result.goldTrue, result.goldFalse);
		result.ansTrueEntropy = entropy(tp, fp);
		result.ansFalseEntropy = entropy(fn, tn);
		
		result.weightedAverageAnsEntropy = result.ansTrueEntropy*result.ansTrue/result.total + result.ansFalseEntropy*result.ansFalse/result.total;
		result.informationGain = result.goldEntropy - result.weightedAverageAnsEntropy;
		
		return result;
	}
	
	public static double entropy(double count1, double count2) {
		if (count1==0 || count2==0) {
			return 0;
		}
		double total = count1 + count2;
		double ratio1 = count1 / total;
		double ratio2 = count2 / total;
		double ratio1_log = MathUtils.log(2, ratio1);
		double ratio2_log = MathUtils.log(2, ratio2);
		double entropy = -ratio1*ratio1_log -ratio2*ratio2_log;
		return entropy;
	}
	
	public static class InfoGainResult {
		public double total;
		public double goldTrue;
		public double goldFalse;
		public double ansTrue;
		public double ansFalse;
				
		public double goldEntropy;
		public double ansTrueEntropy;
		public double ansFalseEntropy;
		
		public double weightedAverageAnsEntropy;
		public double informationGain;
	}
}
