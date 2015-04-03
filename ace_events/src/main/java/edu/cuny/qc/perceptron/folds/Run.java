package edu.cuny.qc.perceptron.folds;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;

import com.google.common.collect.Lists;

import edu.cuny.qc.perceptron.core.ArgOMethod;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.core.SentenceSortingMethod;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.scorer.FeatureProfile;
import edu.cuny.qc.util.Logs;

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
	public Set<SentenceInstance> trainInsts;
	public Set<SentenceInstance> devInsts;
	public int restrictAmount;
	public BigDecimal restrictProportion;
	public Perceptron model;
	
	public void calcSuffix() {
		suffix = String.format("%03d_%02d_Train%s_%s__Dev%s_%s", id, idPerTest, Logs.size(trainEvents, "%02d"), Logs.size(trainInsts, "%04d"), Logs.size(devEvents, "%02d"), Logs.size(devInsts, "%04d"));
	}
	
	/**
	 * Explicitly calc the hash of a SentenceInstance set, specifically relying on the DocID and SentInstID.
	 * This should have been just implemented inside SentenceInstance, but it currently doesn't have a hashCode()
	 * implementation, and I don't want to add one now.
	 * 
	 * @param set
	 * @return
	 */
	private int hashSentenceInstanceSet(Set<SentenceInstance> set) {
		int hash = 0;
		for (SentenceInstance inst : set) {
			hash += (inst==null ? 0 : new HashCodeBuilder(191, 251).append(inst.docID).append(inst.sentInstID).toHashCode());
		}
		return hash;
	}
	
	private boolean equalsSentenceInstanceSet(Set<SentenceInstance> set1, Set<SentenceInstance> set2) {
		if (set1.size() != set2.size()) {
			return false;
		}
		for (SentenceInstance o1 : set1) {
			boolean found = false;
			for (SentenceInstance o2 : set2) {
				if (o1==null ? o2==null : o1.docID.equals(o2.docID) && o1.sentInstID.equals(o2.sentInstID)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;		
	}
	
	@Override
	public int hashCode() {
		// Explicitly calc the hash of both lists, as SentenceInstance doesn't have a proper hashCode() method, and we don't want to add one now
		int trainInstsHash = hashSentenceInstanceSet(trainInsts);
		int devInstsHash = hashSentenceInstanceSet(devInsts);
		
		// We can add the hash of the lists as if the hash is the field itself - since appending an int
		//does exactly the same as appending an object with the int as the object's hash
	    return new HashCodeBuilder(131, 97).append(trainEvents).append(devEvents).append(testEvent).append(sentenceSortingMethod)
	    		.append(argOMethod).append(featureProfile).append(trainInstsHash).append(devInstsHash).toHashCode();
	}
	@Override
	public boolean equals(Object obj) {
	   if (obj == null) { return false; }
	   if (obj == this) { return true; }
	   if (obj.getClass() != getClass()) {
	     return false;
	   }
	   Run rhs = (Run) obj;
	   if (!equalsSentenceInstanceSet(trainInsts, rhs.trainInsts) || !equalsSentenceInstanceSet(devInsts, rhs.devInsts)) {
		   return false;
	   }
	   return new EqualsBuilder().append(trainEvents, rhs.trainEvents).append(devEvents, rhs.devEvents)
			   .append(testEvent, rhs.testEvent).append(sentenceSortingMethod, rhs.sentenceSortingMethod)
			   .append(argOMethod, rhs.argOMethod).append(featureProfile, rhs.featureProfile).isEquals();
	}

	public String toString() {
		try {
			String testEventLabel = SpecAnnotator.getSpecLabel(testEvent);
			return String.format("%s(%s,%s, test=%s, %s trainEvs, %s devEvs, %s trainInsts, %s devInsts, " + 
					//"method=%s argO=%s " + 
					"profile=%s)",
					getClass().getSimpleName(), id, idPerTest, testEventLabel, Logs.size(trainEvents), Logs.size(devEvents), Logs.size(trainInsts), Logs.size(devInsts),
					//sentenceSortingMethod, argOMethod,
					featureProfile);
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
		return String.format("%s(%s,%s, test=%s\n\ttrain(%s types, %s insts, %s mentions)=%s\n\tdev(%s, %s insts, %s mentions)=%s\n" +
				//"\tsentenceSortingMethod=%s\n\targOMethod=%s\n" +
				"\tfeatureProfile=%s\n\trestrictAmount=%s restrictProportion=%s  )",
				getClass().getSimpleName(), id, idPerTest, testLabel, Logs.size(trainEvents), Logs.size(trainInsts), trainMentions, StringUtils.join(trainLabels, ", "),
				Logs.size(devEvents), Logs.size(devInsts), devMentions, StringUtils.join(devLabels, ", "),
				//sentenceSortingMethod, argOMethod,
				featureProfile, restrictAmount, restrictProportion);
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
		newRun.model = orig.model;
		return newRun;
	}		
}