package edu.cuny.qc.perceptron.folds;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;

import com.google.common.collect.Lists;

import edu.cuny.qc.perceptron.core.ArgOMethod;
import edu.cuny.qc.perceptron.core.SentenceSortingMethod;
import edu.cuny.qc.scorer.FeatureProfile;

public class Run {
	//public Map<String, JCas> trainEvents, devEvents, testEvents;
	
	public List<JCas> trainEvents;
	public LinkedHashSet<JCas> devEvents; // this is a set and not a list since for dev, the order doesn't matter (for train it does)
	public JCas testEvent;
	public String suffix;
	public int id, idPerTest, trainMentions, devMentions;
	public SentenceSortingMethod sentenceSortingMethod;
	public ArgOMethod argOMethod;
	public FeatureProfile featureProfile;
	
	public void calcSuffix() {
		suffix = String.format("%03d_%02d_Train%02d_Dev%02d", id, idPerTest, trainEvents.size(), devEvents.size());
	}
	
	@Override
	public int hashCode() {
	     return new HashCodeBuilder(131, 97).append(trainEvents).append(devEvents).append(testEvent).append(sentenceSortingMethod).append(argOMethod).append(featureProfile).toHashCode();
	}
	@Override
	public boolean equals(Object obj) {
	   if (obj == null) { return false; }
	   if (obj == this) { return true; }
	   if (obj.getClass() != getClass()) {
	     return false;
	   }
	   Run rhs = (Run) obj;
	   return new EqualsBuilder().append(trainEvents, rhs.trainEvents).append(devEvents, rhs.devEvents)
			   .append(testEvent, rhs.testEvent).append(sentenceSortingMethod, rhs.sentenceSortingMethod)
			   .append(argOMethod, rhs.argOMethod).append(featureProfile, rhs.featureProfile).isEquals();
	}

	public String toString() {
		try {
			String testEventLabel = SpecAnnotator.getSpecLabel(testEvent);
			return String.format("%s(%s,%s, test=%s, %s train, %s dev, method=%s argO=%s profile=%s)", getClass().getSimpleName(), id, idPerTest, testEventLabel, trainEvents.size(), devEvents.size(), sentenceSortingMethod, argOMethod, featureProfile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String toStringFull() throws CASException {
		List<String> trainLabels = Lists.newArrayListWithCapacity(trainEvents.size());
		for (JCas ev : trainEvents) {
			trainLabels.add(SpecAnnotator.getSpecLabel(ev));
		}
		//Collections.sort(trainLabels);
		List<String> devLabels = Lists.newArrayListWithCapacity(devEvents.size());
		for (JCas ev : devEvents) {
			devLabels.add(SpecAnnotator.getSpecLabel(ev));
		}
		Collections.sort(devLabels);
		String testLabel = SpecAnnotator.getSpecLabel(testEvent);
		return String.format("%s(%s,%s, test=%s\n\ttrain(%s, %s mentions)=%s\n\tdev(%s, %s mentions)=%s\n\tsentenceSortingMethod=%s\n\targOMethod=%s  )",
				getClass().getSimpleName(), id, idPerTest, testLabel, trainEvents.size(), trainMentions, StringUtils.join(trainLabels, ", "),
				devEvents.size(), devMentions, StringUtils.join(devLabels, ", "), sentenceSortingMethod, argOMethod);
	}
	
	public static Run shallowCopy(Run orig) {
		Run newRun = new Run();
		newRun.trainEvents = orig.trainEvents;
		newRun.devEvents = orig.devEvents;
		newRun.testEvent = orig.testEvent;
		newRun.suffix = orig.suffix;
		newRun.id = orig.id;
		newRun.idPerTest = orig.idPerTest;
		newRun.trainMentions = orig.trainMentions;
		newRun.devMentions = orig.devMentions;
		newRun.sentenceSortingMethod = orig.sentenceSortingMethod;
		newRun.argOMethod = orig.argOMethod;
		newRun.featureProfile = orig.featureProfile;
		return newRun;
	}		
}