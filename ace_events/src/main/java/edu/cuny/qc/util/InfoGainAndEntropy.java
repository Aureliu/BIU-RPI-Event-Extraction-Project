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

	public static double infoGain(double tp, double fp, double fn, double tn) {
		double total = tp + fp + fn + tn;
		double goldTrue = tp + fn;
		double goldFalse = fp + tn;
		double ansTrue = tp + fp;
		double ansFalse = fn + tn;
				
		double goldEntropy = entropy(goldTrue, goldFalse);
		double ansTrueEntropy = entropy(tp, fp);
		double ansFalseEntropy = entropy(fn, tn);
		
		double weightedAverageAnsEntropy = ansTrueEntropy*ansTrue/total + ansFalseEntropy*ansFalse/total;
		double informationGain = goldEntropy - weightedAverageAnsEntropy;
		
		return informationGain;
	}
	
	public static double entropy(double count1, double count2) {
		double total = count1 + count2;
		double ratio1 = count1 / total;
		double ratio2 = count2 / total;
		double ratio1_log = MathUtils.log(2, ratio1);
		double ratio2_log = MathUtils.log(2, ratio2);
		double entropy = -ratio1*ratio1_log -ratio2*ratio2_log;
		return entropy;
	}
	

}
