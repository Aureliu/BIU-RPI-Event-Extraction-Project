package edu.cuny.qc.scorer.mechanism;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.fop.fo.OneCharIterator;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation;

import ac.biu.nlp.nlp.ace_uima.AceAbnormalMessage;
import ac.biu.nlp.nlp.ace_uima.AceException;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Treeout;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatGenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepGenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepSpecPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatSpecPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepGenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepGenPosWithContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepGenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepSpecPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepSpecPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepSpecPosWithContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepWithContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.VAll;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.graph.GraphEdge;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.scorer.Aggregator;
import edu.cuny.qc.scorer.ArgumentInUsageSampleScorer;
import edu.cuny.qc.scorer.PredicateSeedScorer;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.SignalMechanismSpecIterator;
import edu.cuny.qc.util.TokenAnnotations;
import edu.cuny.qc.util.TreeToLineString;
import edu.cuny.qc.util.Utils;
import edu.cuny.qc.util.fragment.FragmentAndReference;
import edu.cuny.qc.util.fragment.FragmentLayer;
import edu.cuny.qc.util.fragment.FragmentLayerException;
import edu.cuny.qc.util.fragment.TreeFragmentBuilder.TreeFragmentBuilderException;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public class DependencySignalMechanism extends SignalMechanism {

	static {
		System.err.println("??? DependencySignalMechanism: Here we don't use the heuristics in ArgumentExampleScorer's getHeadToken(). I assume that the fragment stuff take care of getting the true dep head out of the entire arg mention head, but I'm not sufre... May I should check :)");
	}
	
	public DependencySignalMechanism(Controller controller) throws SignalMechanismException {
		super(controller);
		
		try {
			outFile1= new PrintStream(new File(Utils.OUTPUT_FOLDER, "TextTreeouts1.txt"));
			outFile1.printf("Id^Doc^Sentence^Fragment^Facet^Trigger^ArgHead^Role^DepNoContext^DepGenPosNoContext^DepSpecPosNoContext^DepPrepNoContext^DepPrepGenPosNoContext^DepPrepSpecPosNoContext^DepFlatNoContext^DepFlatGenPosNoContext^DepFlatSpecPosNoContext^DepFlatPrepNoContext^DepFlatPrepGenPosNoContext^DepFlatPrepSpecPosNoContext\n");			
			outFile2 = new PrintStream(new File(Utils.OUTPUT_FOLDER, "TextTreeouts2.txt"));
			outFile2.printf("Id^Doc^Trigger^ArgHead^Sentence^Role\n");
			entries1 = new LinkedHashSet<String>();
			entries2 = new LinkedHashSet<String>();
		} catch (IOException e) {
			throw new SignalMechanismException(e);
		}
	}

	@Override
	public void addScorers() throws UnsupportedPosTagStringException {
		switch(controller.featureProfile) {
		case TOKEN_BASELINE: break;
		//case ANALYSIS1: //fall-through 
		//case ANALYSIS:
		case ANALYSIS11:
			oneScorerClass = SameLinkDepNoContext.class;
			addArgumentDependent(new ScorerData("DP_DEP_NOCON",			SameLinkDepNoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_GENPOS_NOCON",	SameLinkDepGenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_SPECPOS_NOCON",	SameLinkDepSpecPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_NOCON",			SameLinkDepPrepNoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_GENPOS_NOCON",		SameLinkDepPrepGenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_SPECPOS_NOCON",	SameLinkDepPrepSpecPosNoContext.inst,	Aggregator.Any.inst		));
			break;

		case ANALYSIS11f:
			oneScorerClass = SameLinkDepFlatNoContext.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_NOCON",			SameLinkDepFlatNoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_GENPOS_NOCON",	SameLinkDepFlatGenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_SPECPOS_NOCON",	SameLinkDepFlatSpecPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_NOCON",			SameLinkDepFlatPrepNoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_GENPOS_NOCON",		SameLinkDepFlatPrepGenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_SPECPOS_NOCON",	SameLinkDepFlatPrepSpecPosNoContext.inst,	Aggregator.Any.inst		));
			break;

		case ANALYSIS12:
			oneScorerClass = SameLinkDepNoContextMinHalf.class;
			addArgumentDependent(new ScorerData("DP_DEP_NOCON_1/2",			SameLinkDepNoContextMinHalf.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_GENPOS_NOCON_1/2",	SameLinkDepGenPosNoContextMinHalf.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_SPECPOS_NOCON_1/2",	SameLinkDepSpecPosNoContextMinHalf.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_NOCON_1/2",			SameLinkDepPrepNoContextMinHalf.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_GENPOS_NOCON_1/2",		SameLinkDepPrepGenPosNoContextMinHalf.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_SPECPOS_NOCON_1/2",	SameLinkDepPrepSpecPosNoContextMinHalf.inst,	Aggregator.Any.inst		));
			break;

		case ANALYSIS12f:
			oneScorerClass = SameLinkDepFlatNoContextMinHalf.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_NOCON_1/2",			SameLinkDepFlatNoContextMinHalf.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_GENPOS_NOCON_1/2",	SameLinkDepFlatGenPosNoContextMinHalf.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_SPECPOS_NOCON_1/2",	SameLinkDepFlatSpecPosNoContextMinHalf.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_NOCON_1/2",			SameLinkDepFlatPrepNoContextMinHalf.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_GENPOS_NOCON_1/2",		SameLinkDepFlatPrepGenPosNoContextMinHalf.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_SPECPOS_NOCON_1/2",	SameLinkDepFlatPrepSpecPosNoContextMinHalf.inst,	Aggregator.Any.inst		));
			break;

		case ANALYSIS13:
			oneScorerClass = SameLinkDepNoContextMinThird.class;
			addArgumentDependent(new ScorerData("DP_DEP_NOCON_1/3",			SameLinkDepNoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_GENPOS_NOCON_1/3",	SameLinkDepGenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_SPECPOS_NOCON_1/3",	SameLinkDepSpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_NOCON_1/3",			SameLinkDepPrepNoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_GENPOS_NOCON_1/3",		SameLinkDepPrepGenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_SPECPOS_NOCON_1/3",	SameLinkDepPrepSpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			break;

		case ANALYSIS13f:
			oneScorerClass = SameLinkDepFlatNoContextMinThird.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_NOCON_1/3",			SameLinkDepFlatNoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_GENPOS_NOCON_1/3",	SameLinkDepFlatGenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_SPECPOS_NOCON_1/3",	SameLinkDepFlatSpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_NOCON_1/3",			SameLinkDepFlatPrepNoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_GENPOS_NOCON_1/3",		SameLinkDepFlatPrepGenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_SPECPOS_NOCON_1/3",	SameLinkDepFlatPrepSpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			break;

		case ANALYSIS14:
			oneScorerClass = SameLinkDepNoContextMinQuarter.class;
			addArgumentDependent(new ScorerData("DP_DEP_NOCON_1/4",			SameLinkDepNoContextMinQuarter.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_GENPOS_NOCON_1/4",	SameLinkDepGenPosNoContextMinQuarter.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_SPECPOS_NOCON_1/4",	SameLinkDepSpecPosNoContextMinQuarter.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_NOCON_1/4",			SameLinkDepPrepNoContextMinQuarter.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_GENPOS_NOCON_1/4",		SameLinkDepPrepGenPosNoContextMinQuarter.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_SPECPOS_NOCON_1/4",	SameLinkDepPrepSpecPosNoContextMinQuarter.inst,	Aggregator.Any.inst		));
			break;

		case ANALYSIS14f:
			oneScorerClass = SameLinkDepFlatNoContextMinQuarter.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_NOCON_1/4",			SameLinkDepFlatNoContextMinQuarter.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_GENPOS_NOCON_1/4",	SameLinkDepFlatGenPosNoContextMinQuarter.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_SPECPOS_NOCON_1/4",	SameLinkDepFlatSpecPosNoContextMinQuarter.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_NOCON_1/4",			SameLinkDepFlatPrepNoContextMinQuarter.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_GENPOS_NOCON_1/4",		SameLinkDepFlatPrepGenPosNoContextMinQuarter.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_SPECPOS_NOCON_1/4",	SameLinkDepFlatPrepSpecPosNoContextMinQuarter.inst,	Aggregator.Any.inst		));
			break;

		case ANALYSIS15:
			oneScorerClass = SameLinkDepNoContextMinFifth.class;
			addArgumentDependent(new ScorerData("DP_DEP_NOCON_1/5",			SameLinkDepNoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_GENPOS_NOCON_1/5",	SameLinkDepGenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_SPECPOS_NOCON_1/5",	SameLinkDepSpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_NOCON_1/5",			SameLinkDepPrepNoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_GENPOS_NOCON_1/5",		SameLinkDepPrepGenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_SPECPOS_NOCON_1/5",	SameLinkDepPrepSpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));

		case ANALYSIS15f:
			oneScorerClass = SameLinkDepFlatNoContextMinFifth.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_NOCON_1/5",			SameLinkDepFlatNoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_GENPOS_NOCON_1/5",	SameLinkDepFlatGenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_SPECPOS_NOCON_1/5",	SameLinkDepFlatSpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_NOCON_1/5",			SameLinkDepFlatPrepNoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_GENPOS_NOCON_1/5",		SameLinkDepFlatPrepGenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_SPECPOS_NOCON_1/5",	SameLinkDepFlatPrepSpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));

//			addArgumentDependent(new ScorerData("DP_DEP_CON",			SameLinkDepWithContext.inst,		Aggregator.Any.inst		));
//			addArgumentDependent(new ScorerData("DP_DEP_GENPOS_CON",	SameLinkDepGenPosWithContext.inst,	Aggregator.Any.inst		));
//			addArgumentDependent(new ScorerData("DP_DEP_SPECPOS_CON",	SameLinkDepSpecPosWithContext.inst,	Aggregator.Any.inst		));
			break;
		case ANALYSIS2:
		case ANALYSIS3:
			break;
		case NORMAL:
			oneScorerClass = SameLinkDepGenPosNoContext.class;
			//addTrigger(new ScorerData(null, new Or(new OneDepUp("pobj"), new OneDepUp("dobj"), new OneDepUp("nsubj")), true));

//			addArgumentDependent(new ScorerData("DP_DEP_NOCON",			SameLinkDepNoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_GENPOS_NOCON",		SameLinkDepGenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_GENPOS_NOCON",	SameLinkDepPrepGenPosNoContext.inst,	Aggregator.Any.inst		));
//			addArgumentDependent(new ScorerData("DP_DEP_SPECPOS_NOCON",	SameLinkDepSpecPosNoContext.inst,	Aggregator.Any.inst		));
//			addArgumentDependent(new ScorerData("DP_DEP_CON",			SameLinkDepWithContext.inst,		Aggregator.Any.inst		));
//			addArgumentDependent(new ScorerData("DP_DEP_GENPOS_CON",	SameLinkDepGenPosWithContext.inst,	Aggregator.Any.inst		));
//			addArgumentDependent(new ScorerData("DP_DEP_SPECPOS_CON",	SameLinkDepSpecPosWithContext.inst,	Aggregator.Any.inst		));
			
			break;
		default:
			//throw new IllegalStateException("Bad FeatureProfile enum value: " + controller.featureProfile);
			break;
		}

	}

//	@Override
//	public void entrypointPreSpec(JCas spec) throws SignalMechanismException {
//		try {
//			specFragmentLayer = cacheSpecFragmentLayers.get(spec);
//		} catch (ExecutionException e) {
//			throw new SignalMechanismException(e);
//		}
//	}
	
	@Override
	public void entrypointPreDocument(Document doc) throws SignalMechanismException {
		try {
			textFragmentLayer = new FragmentLayer(doc.jcas, Document.converter);
		} catch (FragmentLayerException e) {
			throw new SignalMechanismException(e);
		}
	}
	
	
	public static class OneDepUp extends PredicateSeedScorer {
		private static final long serialVersionUID = 5805470654188632623L;
		public String relation;
		public OneDepUp(String relation) {
			this.relation = relation;
		}
		@Override public String getTypeName() {
			return "DepUp_" + relation;
		}
		@Override
		public Boolean calcBoolPredicateSeedScore(Token textToken, Map<Class<?>, Object> textTriggerTokenMap, String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, ScorerData scorerData) throws SignalMechanismException
		{
			List<GraphEdge> toParents = (List<GraphEdge>) textTriggerTokenMap.get(TokenAnnotations.EdgesToParents.class);
			for (GraphEdge edge : toParents) {
				if (edge.getRelation().equals(relation)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public static class SpecTreeoutQuery {
		public Class<? extends Treeout> specClass;
		public ArgumentInUsageSample specAius;
		public SpecTreeoutQuery(Class<? extends Treeout> specClass, ArgumentInUsageSample specAius) {
			this.specClass = specClass; this.specAius = specAius;
		}
		@Override public int hashCode() {
			final int prime = 269;
			int result = 1;
			result = prime*result + specClass.hashCode();
			result = prime*result + UimaUtils.hashCodeAnnotationByTypeAndSpan(specAius);
			return result;
		}
		@Override public boolean equals(Object obj) {
			if (obj == null) { return false; }
			if (obj == this) { return true; }
			if (obj.getClass() != getClass()) { return false; }
			SpecTreeoutQuery rhs = (SpecTreeoutQuery) obj;
			return UimaUtils.equalsAnnotationByTypeAndSpan(specAius, rhs.specAius) && specClass==rhs.specClass;
		}
	}
	
	public static class SpecListTreeoutsQuery {
		public Class<? extends Treeout> specClass;
		public List<ArgumentInUsageSample> specAiuses;
		public List<ArgumentInUsageSample> specAiusesSorted;
		public SpecListTreeoutsQuery(Class<? extends Treeout> specClass, List<ArgumentInUsageSample> specAiuses) {
			this.specClass = specClass;
			this.specAiuses = specAiuses;
			this.specAiusesSorted = Lists.newArrayList(this.specAiuses);
			Collections.sort(this.specAiusesSorted, new Comparator<ArgumentInUsageSample>() {
				@Override public int compare(ArgumentInUsageSample aius1, ArgumentInUsageSample aius2) {
					return UimaUtils.hashCodeAnnotationByTypeAndSpan(aius1) - UimaUtils.hashCodeAnnotationByTypeAndSpan(aius2);
				}
			});
		}
		@Override public int hashCode() {
			final int prime = 271;
			int result = 1;
			
			// this assumes that specAiuses can have repetitions, and its order doesn't matter
			int listHash = 0;
			for (ArgumentInUsageSample aius : specAiuses) {
				listHash += UimaUtils.hashCodeAnnotationByTypeAndSpan(aius);
			}
			
			result = prime*result + specClass.hashCode();
			result = prime*result + listHash;
			return result;
		}
		@Override public boolean equals(Object obj) {
			if (obj == null) { return false; }
			if (obj == this) { return true; }
			if (obj.getClass() != getClass()) { return false; }
			SpecListTreeoutsQuery rhs = (SpecListTreeoutsQuery) obj;
			if (specClass!=rhs.specClass) { return false; }
			
			// this assumes that specAiuses can have repetitions, and its order doesn't matter
			if (this.specAiusesSorted.size() != rhs.specAiusesSorted.size()) { return false;}
			Iterator<ArgumentInUsageSample> iter1=this.specAiusesSorted.iterator();
			Iterator<ArgumentInUsageSample> iter2=rhs.specAiusesSorted.iterator();
			while (iter1.hasNext()) {
				ArgumentInUsageSample next1 = iter1.next();
				ArgumentInUsageSample next2 = iter2.next();
				if (!UimaUtils.equalsAnnotationByTypeAndSpan(next1, next2)) {
					return false;
				}
			}
			
			return true;
		}
	}
	
	public static class TriggerArgQuery {
		public Token textTriggerToken;
		public Annotation textArgHeadAnno;
		public TriggerArgQuery(Token textTriggerToken, Annotation textArgHeadAnno) {
			this.textTriggerToken = textTriggerToken; this.textArgHeadAnno = textArgHeadAnno;
		}
		@Override public int hashCode() {
			final int prime = 139;
			int result = 1;
			result = prime*result + UimaUtils.hashCodeAnnotationByTypeAndSpan(textTriggerToken);
			result = prime*result + UimaUtils.hashCodeAnnotationByTypeAndSpan(textArgHeadAnno);
			return result;
		}
		@Override public boolean equals(Object obj) {
			if (obj == null) { return false; }
			if (obj == this) { return true; }
			if (obj.getClass() != getClass()) { return false; }
			TriggerArgQuery rhs = (TriggerArgQuery) obj;
			return UimaUtils.equalsAnnotationByTypeAndSpan(textTriggerToken, rhs.textTriggerToken) && UimaUtils.equalsAnnotationByTypeAndSpan(textArgHeadAnno, rhs.textArgHeadAnno);
//			return new EqualsBuilder().append(textTriggerToken, rhs.textTriggerToken).append(textArgHeadAnno, rhs.textArgHeadAnno).isEquals();
		}
	}
	
	public static abstract class SameLinkOverTreeout extends ArgumentInUsageSampleScorer {
		private static final long serialVersionUID = -6165360939857557992L;
		public Class<? extends Treeout> specClass;
		public String textOutsMapKey;
		public String specTreeout, textTreeout;
		public SameLinkOverTreeout(Class<? extends Treeout> specClass, String textOutsMapKey) {
			this.specClass = specClass;
			this.textOutsMapKey = textOutsMapKey;
		}
		@Override
		public Boolean calcBoolPredicateSeedScore(Token textTriggerToken, AceMention textArgMention, Annotation textArgHeadAnno, ArgumentInUsageSample specAius, ScorerData scorerData) throws SignalMechanismException {
			try {
				specTreeout = cacheSpecTreeouts.get(new SpecTreeoutQuery(specClass, specAius));

				// DEBUG
				//System.out.printf("%s       SameLinkOverTreeout(%s).calc: Starting textTriggerToken=%s, textArgHeadAnno=%s, specAius=%s, specTreeout=%s\n", Utils.detailedLog(), this.getClass().getSimpleName(), UimaUtils.annotationToString(textTriggerToken), UimaUtils.annotationToString(textArgHeadAnno), specAius.getCoveredText(), specTreeout);
				///
				TriggerArgQuery treeoutsQuery = new TriggerArgQuery(textTriggerToken, textArgHeadAnno);
				Map<String, String> outsMap = cacheTextTreeouts.get(treeoutsQuery);
				textTreeout = outsMap.get(textOutsMapKey);
				
				boolean result = textTreeout.equals(specTreeout);
				// DEBUG
				//System.out.printf("%s       SameLinkOverTreeout(%s).calc: ***Finishing textTriggerToken=%s, textArgHeadAnno=%s, specAius=%s, specTreeout=%s\n", Utils.detailedLog(), this.getClass().getSimpleName(), UimaUtils.annotationToString(textTriggerToken), UimaUtils.annotationToString(textArgHeadAnno), specAius.getCoveredText(), specTreeout);
				///
				
				if (this.getClass().equals(oneScorerClass)) {
					System.out.printf("");
				}
				if (SentenceInstance.currArgCandIsArg && (this.getClass().equals(oneScorerClass))) {
					
					String origSentenceStr = "-Error-";
					String facetStr = "-Error-";
					String treePrintStr = "   **** Got an error while building tree! ****";
					try {
						FragmentAndReference linkFrag = cacheTextTreeFragments.get(treeoutsQuery);
						origSentenceStr = Utils.treeToSurfaceText(linkFrag.getOrigReference());
						facetStr = linkFrag.facet.toString().replace('\n',' ');
						treePrintStr = TreeStringGenerator.treeToStringFull(linkFrag.getFragmentRoot());
					}
					catch (Exception e) {
						// do nothing, errors will silently be printed in output files
					}

					String entry1 = String.format("%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s\n",
							//outFileId,
							//StringUtils.abbreviate(query.textTriggerToken.getCAS().getJCas().getDocumentText().replace('\n',' '), 40),
							SentenceInstance.currDocId,
							SentenceInstance.currSentInstId,
							SentenceInstance.currEventType,
							SentenceInstance.currRole,
							UimaUtils.annotationToString(textTriggerToken, false, false),
							UimaUtils.annotationToString(textArgHeadAnno).replace('\n',' '),
							origSentenceStr,
							//Utils.treeToSurfaceText(linkFrag.getFragmentRoot()),
							facetStr,
							outsMap.get("DepNoContext"),
							outsMap.get("DepGenPosNoContext"),
							outsMap.get("DepSpecPosNoContext"),
							outsMap.get("DepPrepNoContext"),
							outsMap.get("DepPrepGenPosNoContext"),
							outsMap.get("DepPrepSpecPosNoContext"),
							outsMap.get("DepFlatNoContext"),
							outsMap.get("DepFlatGenPosNoContext"),
							outsMap.get("DepFlatSpecPosNoContext"),
							outsMap.get("DepFlatPrepNoContext"),
							outsMap.get("DepFlatPrepGenPosNoContext"),
							outsMap.get("DepFlatPrepSpecPosNoContext")
							);
					String entry2 = String.format("%s^%s^%s^%s^%s^%s^%s\nDepNoContext:       %s\nDepFlatNoContext:   %s\nDepPrepNoContext:   %s\n%s\n\n",
							//outFileId,
							//StringUtils.abbreviate(query.textTriggerToken.getCAS().getJCas().getDocumentText().replace('\n',' '), 40),
							SentenceInstance.currDocId,
							SentenceInstance.currSentInstId,
							SentenceInstance.currEventType,
							SentenceInstance.currRole,
							UimaUtils.annotationToString(textTriggerToken, false, false),
							UimaUtils.annotationToString(textArgHeadAnno).replace('\n',' '),
							origSentenceStr,
							outsMap.get("DepNoContext"),
							outsMap.get("DepFlatNoContext"),
							outsMap.get("DepPrepNoContext"),
							treePrintStr
							);
					
					if (!entries1.contains(entry1) && !entries2.contains(entry2)) {
						entries1.add(entry1);
						entries2.add(entry2);
						outFileId++;
						outFile1.printf("%s^%s", outFileId, entry1);
						outFile2.printf("%s^%s", outFileId, entry2);
					}
				}

				return result;
			} catch (ExecutionException e) {
				throw new SignalMechanismException(e);
			}
		}
		
		@Override
		public void addToHistory() throws SignalMechanismException {
			history.put(specTreeout.intern(), textTreeout.intern());
		}
	}
	
	public static class SameLinkDepNoContext extends SameLinkOverTreeout {
		private static final long serialVersionUID = 5008898037812828289L;
		public static final SameLinkDepNoContext inst = new SameLinkDepNoContext();
		public SameLinkDepNoContext() {super(TreeoutDepNoContext.class, "DepNoContext");}
	}
	
	public static class SameLinkDepGenPosNoContext extends SameLinkOverTreeout {
		private static final long serialVersionUID = 2610479496129721802L;
		public static final SameLinkDepGenPosNoContext inst = new SameLinkDepGenPosNoContext();
		public SameLinkDepGenPosNoContext() {super(TreeoutDepGenPosNoContext.class, "DepGenPosNoContext");}
	}
	
	public static class SameLinkDepSpecPosNoContext extends SameLinkOverTreeout {
		private static final long serialVersionUID = -3995028677111576039L;
		public static final SameLinkDepSpecPosNoContext inst = new SameLinkDepSpecPosNoContext();
		public SameLinkDepSpecPosNoContext() {super(TreeoutDepSpecPosNoContext.class, "DepSpecPosNoContext");}
	}
	
	public static class SameLinkDepPrepNoContext extends SameLinkOverTreeout {
		private static final long serialVersionUID = -3889859804888325372L;
		public static final SameLinkDepPrepNoContext inst = new SameLinkDepPrepNoContext();
		public SameLinkDepPrepNoContext() {super(TreeoutDepPrepNoContext.class, "DepPrepNoContext");}
	}
	
	public static class SameLinkDepPrepGenPosNoContext extends SameLinkOverTreeout {
		private static final long serialVersionUID = 4968227265598879023L;
		public static final SameLinkDepPrepGenPosNoContext inst = new SameLinkDepPrepGenPosNoContext();
		public SameLinkDepPrepGenPosNoContext() {super(TreeoutDepPrepGenPosNoContext.class, "DepPrepGenPosNoContext");}
	}
	
	public static class SameLinkDepPrepSpecPosNoContext extends SameLinkOverTreeout {
		private static final long serialVersionUID = 817021276873360255L;
		public static final SameLinkDepPrepSpecPosNoContext inst = new SameLinkDepPrepSpecPosNoContext();
		public SameLinkDepPrepSpecPosNoContext() {super(TreeoutDepPrepSpecPosNoContext.class, "DepPrepSpecPosNoContext");}
	}
	

	public static class SameLinkDepFlatNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8584063640163413554L;
		public static final SameLinkDepFlatNoContext inst = new SameLinkDepFlatNoContext();
		public SameLinkDepFlatNoContext() {super(TreeoutDepFlatNoContext.class, "DepFlatNoContext");}
	}
	
	public static class SameLinkDepFlatGenPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6321595143185288461L;
		public static final SameLinkDepFlatGenPosNoContext inst = new SameLinkDepFlatGenPosNoContext();
		public SameLinkDepFlatGenPosNoContext() {super(TreeoutDepFlatGenPosNoContext.class, "DepFlatGenPosNoContext");}
	}
	
	public static class SameLinkDepFlatSpecPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6574297455692725299L;
		public static final SameLinkDepFlatSpecPosNoContext inst = new SameLinkDepFlatSpecPosNoContext();
		public SameLinkDepFlatSpecPosNoContext() {super(TreeoutDepFlatSpecPosNoContext.class, "DepFlatSpecPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4034771381066822990L;
		public static final SameLinkDepFlatPrepNoContext inst = new SameLinkDepFlatPrepNoContext();
		public SameLinkDepFlatPrepNoContext() {super(TreeoutDepFlatPrepNoContext.class, "DepFlatPrepNoContext");}
	}
	
	public static class SameLinkDepFlatPrepGenPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6104214684353432714L;
		public static final SameLinkDepFlatPrepGenPosNoContext inst = new SameLinkDepFlatPrepGenPosNoContext();
		public SameLinkDepFlatPrepGenPosNoContext() {super(TreeoutDepFlatPrepGenPosNoContext.class, "DepPrepGenPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepSpecPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2512844813222387990L;
		public static final SameLinkDepFlatPrepSpecPosNoContext inst = new SameLinkDepFlatPrepSpecPosNoContext();
		public SameLinkDepFlatPrepSpecPosNoContext() {super(TreeoutDepFlatPrepSpecPosNoContext.class, "DepFlatPrepSpecPosNoContext");}
	}
	
	
	
	
//	public static class SameLinkDepWithContext extends SameLinkOverTreeout {
//		private static final long serialVersionUID = 1694575499404281966L;
//		public static final SameLinkDepWithContext inst = new SameLinkDepWithContext();
//		public SameLinkDepWithContext() {super(TreeoutDepWithContext.class, "DepWithContext");}
//	}
//	
//	public static class SameLinkDepGenPosWithContext extends SameLinkOverTreeout {
//		private static final long serialVersionUID = 1235776745686238698L;
//		public static final SameLinkDepGenPosWithContext inst = new SameLinkDepGenPosWithContext();
//		public SameLinkDepGenPosWithContext() {super(TreeoutDepGenPosWithContext.class, "DepGenPosWithContext");}
//	}
//	
//	public static class SameLinkDepSpecPosWithContext extends SameLinkOverTreeout {
//		private static final long serialVersionUID = 5285506462168848680L;
//		public static final SameLinkDepSpecPosWithContext inst = new SameLinkDepSpecPosWithContext();
//		public SameLinkDepSpecPosWithContext() {super(TreeoutDepSpecPosWithContext.class, "DepSpecPosWithContext");}
//	}
	
	public static abstract class SameLinkOverTreeoutOnlyFrequesntAiuses extends SameLinkOverTreeout {
		private static final long serialVersionUID = 8672008272926471560L;
		public double minPercent;
		public SameLinkOverTreeoutOnlyFrequesntAiuses(Class<? extends Treeout> specClass, String textOutsMapKey, double minPercent) {
			super(specClass, textOutsMapKey);
			this.minPercent = minPercent;
		}

		private int sum(Collection<Integer> nums) {
			int result = 0;
			for (Integer num : nums) {
				result += num;
			}
			return result;
		}
		// the second parameter is there historically (because of the implementation mistake I did on 16.11.2014 - we ignore it
		@Override
		protected boolean includeAius(ArgumentInUsageSample aius, List<ArgumentInUsageSample> aiusesSameRole) throws Exception {
			try {
				/// DEBUG
//				if (aius.getCoveredText().contains("stones")) {
//					System.out.printf("\n\n\n\n\nMAYBE\n\n\n\n\n");
//				}
				////
				Feature treeoutFeature = SpecAnnotator.getAiusTreeoutFeature(specClass, aius);
				Treeout treeout = (Treeout) aius.getFeatureValue(treeoutFeature);
				VAll vAll = treeout.getVAll();
				Map<String, Integer> frequencies = cacheAiusFrequencies.get(vAll);
				/// DEBUG
//				if (frequencies == null) {
//					System.out.printf("\n\n\n\n\nfrequencies == null\n\n\n\n\n");
//				}
				////
				Integer sum = sum(frequencies.values());
				/// DEBUG
//				if (aius == null || aius.getArgumentExample()==null || aius.getArgumentExample().getArgument() == null ||
//						aius.getArgumentExample().getArgument().getRole() == null) {
//					System.out.printf("\n\n\n\n\naius (or something derved) == null\n\n\n\n\n");
//				}
				////
				String role = aius.getArgumentExample().getArgument().getRole().getCoveredText();
				/// DEBUG
//				if (role == null) {
//					System.out.printf("\n\n\n\n\role == null\n\n\n\n\n");
//				}
				////
				Integer aiusFreq = frequencies.get(role);
				/// DEBUG
//				if (aiusFreq == null) {
//					System.out.printf("\n\n\n\naiusFreq == null\n\n\n\n\n");
//				}
				////
				double aiusRelativeFreq = ((double) aiusFreq) / sum;
				/// DEBUG
	//			System.out.printf("DependencySignalMechanism.SameLinkOverTreeoutOnlyFrequesntAiuses: \"%s\" appears %s times out of %s, which is %s. minPercent=%s, so including? %s\n",
	//					role, aiusFreq, sum, aiusRelativeFreq, minPercent, aiusRelativeFreq >= minPercent);
				///
				return aiusRelativeFreq >= minPercent;
			}
			catch (Exception e) {
				throw new Exception(e);
			}
		}

	}
	
	///////// Min 1/3 ///////////////////////
	public static abstract class SameLinkOverTreeoutMinimumThird extends SameLinkOverTreeoutOnlyFrequesntAiuses {
		private static final long serialVersionUID = 6496448605251765612L;
		private static final double MIN_PERCENT = 1.0/3;
		public SameLinkOverTreeoutMinimumThird(Class<? extends Treeout> specClass, String textOutsMapKey) {
			super(specClass, textOutsMapKey, MIN_PERCENT);
		}
	}

	public static class SameLinkDepNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		private static final long serialVersionUID = -6357105616985441064L;
		public static final SameLinkDepNoContextMinThird inst = new SameLinkDepNoContextMinThird();
		public SameLinkDepNoContextMinThird() {super(TreeoutDepNoContext.class, "DepNoContext");}
	}
	
	public static class SameLinkDepGenPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		private static final long serialVersionUID = -4337124019519476439L;
		public static final SameLinkDepGenPosNoContextMinThird inst = new SameLinkDepGenPosNoContextMinThird();
		public SameLinkDepGenPosNoContextMinThird() {super(TreeoutDepGenPosNoContext.class, "DepGenPosNoContext");}
	}
	
	public static class SameLinkDepSpecPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		private static final long serialVersionUID = -4419488905887644263L;
		public static final SameLinkDepSpecPosNoContextMinThird inst = new SameLinkDepSpecPosNoContextMinThird();
		public SameLinkDepSpecPosNoContextMinThird() {super(TreeoutDepSpecPosNoContext.class, "DepSpecPosNoContext");}
	}
	
	public static class SameLinkDepPrepNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		private static final long serialVersionUID = -7835777695118531109L;
		public static final SameLinkDepPrepNoContextMinThird inst = new SameLinkDepPrepNoContextMinThird();
		public SameLinkDepPrepNoContextMinThird() {super(TreeoutDepPrepNoContext.class, "DepPrepNoContext");}
	}
	
	public static class SameLinkDepPrepGenPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		private static final long serialVersionUID = 8362852044381125271L;
		public static final SameLinkDepPrepGenPosNoContextMinThird inst = new SameLinkDepPrepGenPosNoContextMinThird();
		public SameLinkDepPrepGenPosNoContextMinThird() {super(TreeoutDepPrepGenPosNoContext.class, "DepPrepGenPosNoContext");}
	}
	
	public static class SameLinkDepPrepSpecPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		private static final long serialVersionUID = -462636602605463316L;
		public static final SameLinkDepPrepSpecPosNoContextMinThird inst = new SameLinkDepPrepSpecPosNoContextMinThird();
		public SameLinkDepPrepSpecPosNoContextMinThird() {super(TreeoutDepPrepSpecPosNoContext.class, "DepPrepSpecPosNoContext");}
	}

	
	public static class SameLinkDepFlatNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4617173080028539989L;
		public static final SameLinkDepFlatNoContextMinThird inst = new SameLinkDepFlatNoContextMinThird();
		public SameLinkDepFlatNoContextMinThird() {super(TreeoutDepFlatNoContext.class, "DepFlatNoContext");}
	}
	
	public static class SameLinkDepFlatGenPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7161792192642179321L;
		public static final SameLinkDepFlatGenPosNoContextMinThird inst = new SameLinkDepFlatGenPosNoContextMinThird();
		public SameLinkDepFlatGenPosNoContextMinThird() {super(TreeoutDepFlatGenPosNoContext.class, "DepFlatGenPosNoContext");}
	}
	
	public static class SameLinkDepFlatSpecPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2009152831068923462L;
		public static final SameLinkDepFlatSpecPosNoContextMinThird inst = new SameLinkDepFlatSpecPosNoContextMinThird();
		public SameLinkDepFlatSpecPosNoContextMinThird() {super(TreeoutDepFlatSpecPosNoContext.class, "DepFlatSpecPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1820977110815964114L;
		public static final SameLinkDepFlatPrepNoContextMinThird inst = new SameLinkDepFlatPrepNoContextMinThird();
		public SameLinkDepFlatPrepNoContextMinThird() {super(TreeoutDepFlatPrepNoContext.class, "DepFlatPrepNoContext");}
	}
	
	public static class SameLinkDepFlatPrepGenPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1226400346093284756L;
		public static final SameLinkDepFlatPrepGenPosNoContextMinThird inst = new SameLinkDepFlatPrepGenPosNoContextMinThird();
		public SameLinkDepFlatPrepGenPosNoContextMinThird() {super(TreeoutDepFlatPrepGenPosNoContext.class, "DepFlatPrepGenPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepSpecPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2778349714667163724L;
		public static final SameLinkDepFlatPrepSpecPosNoContextMinThird inst = new SameLinkDepFlatPrepSpecPosNoContextMinThird();
		public SameLinkDepFlatPrepSpecPosNoContextMinThird() {super(TreeoutDepFlatPrepSpecPosNoContext.class, "DepFlatPrepSpecPosNoContext");}
	}


	
	///////// Min 1/2 ///////////////////////
	public static abstract class SameLinkOverTreeoutMinimumHalf extends SameLinkOverTreeoutOnlyFrequesntAiuses {
		private static final long serialVersionUID = -1700397165964757235L;
		private static final double MIN_PERCENT = 1.0/2;
		public SameLinkOverTreeoutMinimumHalf(Class<? extends Treeout> specClass, String textOutsMapKey) {
			super(specClass, textOutsMapKey, MIN_PERCENT);
		}
	}

	public static class SameLinkDepNoContextMinHalf extends SameLinkOverTreeoutMinimumHalf {
		private static final long serialVersionUID = 8978953472771506397L;
		public static final SameLinkDepNoContextMinHalf inst = new SameLinkDepNoContextMinHalf();
		public SameLinkDepNoContextMinHalf() {super(TreeoutDepNoContext.class, "DepNoContext");}
	}
	
	public static class SameLinkDepGenPosNoContextMinHalf extends SameLinkOverTreeoutMinimumHalf {
		private static final long serialVersionUID = -9035839148508994501L;
		public static final SameLinkDepGenPosNoContextMinHalf inst = new SameLinkDepGenPosNoContextMinHalf();
		public SameLinkDepGenPosNoContextMinHalf() {super(TreeoutDepGenPosNoContext.class, "DepGenPosNoContext");}
	}
	
	public static class SameLinkDepSpecPosNoContextMinHalf extends SameLinkOverTreeoutMinimumHalf {
		private static final long serialVersionUID = 8748138959597170056L;
		public static final SameLinkDepSpecPosNoContextMinHalf inst = new SameLinkDepSpecPosNoContextMinHalf();
		public SameLinkDepSpecPosNoContextMinHalf() {super(TreeoutDepSpecPosNoContext.class, "DepSpecPosNoContext");}
	}
	
	public static class SameLinkDepPrepNoContextMinHalf extends SameLinkOverTreeoutMinimumHalf {
		private static final long serialVersionUID = -1241229038066426568L;
		public static final SameLinkDepPrepNoContextMinHalf inst = new SameLinkDepPrepNoContextMinHalf();
		public SameLinkDepPrepNoContextMinHalf() {super(TreeoutDepPrepNoContext.class, "DepPrepNoContext");}
	}
	
	public static class SameLinkDepPrepGenPosNoContextMinHalf extends SameLinkOverTreeoutMinimumHalf {
		private static final long serialVersionUID = 3635011747498225134L;
		public static final SameLinkDepPrepGenPosNoContextMinHalf inst = new SameLinkDepPrepGenPosNoContextMinHalf();
		public SameLinkDepPrepGenPosNoContextMinHalf() {super(TreeoutDepPrepGenPosNoContext.class, "DepPrepGenPosNoContext");}
	}
	
	public static class SameLinkDepPrepSpecPosNoContextMinHalf extends SameLinkOverTreeoutMinimumHalf {
		private static final long serialVersionUID = -4624906863136656669L;
		public static final SameLinkDepPrepSpecPosNoContextMinHalf inst = new SameLinkDepPrepSpecPosNoContextMinHalf();
		public SameLinkDepPrepSpecPosNoContextMinHalf() {super(TreeoutDepPrepSpecPosNoContext.class, "DepPrepSpecPosNoContext");}
	}

	
	
	public static class SameLinkDepFlatNoContextMinHalf extends SameLinkOverTreeoutMinimumHalf {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4506339837740141499L;
		public static final SameLinkDepFlatNoContextMinHalf inst = new SameLinkDepFlatNoContextMinHalf();
		public SameLinkDepFlatNoContextMinHalf() {super(TreeoutDepFlatNoContext.class, "DepFlatNoContext");}
	}
	
	public static class SameLinkDepFlatGenPosNoContextMinHalf extends SameLinkOverTreeoutMinimumHalf {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3754021517493725941L;
		public static final SameLinkDepFlatGenPosNoContextMinHalf inst = new SameLinkDepFlatGenPosNoContextMinHalf();
		public SameLinkDepFlatGenPosNoContextMinHalf() {super(TreeoutDepFlatGenPosNoContext.class, "DepFlatGenPosNoContext");}
	}
	
	public static class SameLinkDepFlatSpecPosNoContextMinHalf extends SameLinkOverTreeoutMinimumHalf {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6179040875334924010L;
		public static final SameLinkDepFlatSpecPosNoContextMinHalf inst = new SameLinkDepFlatSpecPosNoContextMinHalf();
		public SameLinkDepFlatSpecPosNoContextMinHalf() {super(TreeoutDepFlatSpecPosNoContext.class, "DepFlatSpecPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepNoContextMinHalf extends SameLinkOverTreeoutMinimumHalf {
		/**
		 * 
		 */
		private static final long serialVersionUID = -80152859850015347L;
		public static final SameLinkDepFlatPrepNoContextMinHalf inst = new SameLinkDepFlatPrepNoContextMinHalf();
		public SameLinkDepFlatPrepNoContextMinHalf() {super(TreeoutDepFlatPrepNoContext.class, "DepFlatPrepNoContext");}
	}
	
	public static class SameLinkDepFlatPrepGenPosNoContextMinHalf extends SameLinkOverTreeoutMinimumHalf {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8055302465475022117L;
		public static final SameLinkDepFlatPrepGenPosNoContextMinHalf inst = new SameLinkDepFlatPrepGenPosNoContextMinHalf();
		public SameLinkDepFlatPrepGenPosNoContextMinHalf() {super(TreeoutDepFlatPrepGenPosNoContext.class, "DepFlatPrepGenPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepSpecPosNoContextMinHalf extends SameLinkOverTreeoutMinimumHalf {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6149344044847332607L;
		public static final SameLinkDepFlatPrepSpecPosNoContextMinHalf inst = new SameLinkDepFlatPrepSpecPosNoContextMinHalf();
		public SameLinkDepFlatPrepSpecPosNoContextMinHalf() {super(TreeoutDepFlatPrepSpecPosNoContext.class, "DepFlatPrepSpecPosNoContext");}
	}


	///////// Min 1/4 ///////////////////////
	public static abstract class SameLinkOverTreeoutMinimumQuarter extends SameLinkOverTreeoutOnlyFrequesntAiuses {
		private static final long serialVersionUID = -569981252266551136L;
		private static final double MIN_PERCENT = 1.0/4;
		public SameLinkOverTreeoutMinimumQuarter(Class<? extends Treeout> specClass, String textOutsMapKey) {
			super(specClass, textOutsMapKey, MIN_PERCENT);
		}
	}

	public static class SameLinkDepNoContextMinQuarter extends SameLinkOverTreeoutMinimumQuarter {
		private static final long serialVersionUID = -7173718057746872577L;
		public static final SameLinkDepNoContextMinQuarter inst = new SameLinkDepNoContextMinQuarter();
		public SameLinkDepNoContextMinQuarter() {super(TreeoutDepNoContext.class, "DepNoContext");}
	}
	
	public static class SameLinkDepGenPosNoContextMinQuarter extends SameLinkOverTreeoutMinimumQuarter {
		private static final long serialVersionUID = -178651800815307952L;
		public static final SameLinkDepGenPosNoContextMinQuarter inst = new SameLinkDepGenPosNoContextMinQuarter();
		public SameLinkDepGenPosNoContextMinQuarter() {super(TreeoutDepGenPosNoContext.class, "DepGenPosNoContext");}
	}
	
	public static class SameLinkDepSpecPosNoContextMinQuarter extends SameLinkOverTreeoutMinimumQuarter {
		private static final long serialVersionUID = 1359447618199848839L;
		public static final SameLinkDepSpecPosNoContextMinQuarter inst = new SameLinkDepSpecPosNoContextMinQuarter();
		public SameLinkDepSpecPosNoContextMinQuarter() {super(TreeoutDepSpecPosNoContext.class, "DepSpecPosNoContext");}
	}
	
	public static class SameLinkDepPrepNoContextMinQuarter extends SameLinkOverTreeoutMinimumQuarter {
		private static final long serialVersionUID = -491806036694648836L;
		public static final SameLinkDepPrepNoContextMinQuarter inst = new SameLinkDepPrepNoContextMinQuarter();
		public SameLinkDepPrepNoContextMinQuarter() {super(TreeoutDepPrepNoContext.class, "DepPrepNoContext");}
	}
	
	public static class SameLinkDepPrepGenPosNoContextMinQuarter extends SameLinkOverTreeoutMinimumQuarter {
		private static final long serialVersionUID = -8640741798336940119L;
		public static final SameLinkDepPrepGenPosNoContextMinQuarter inst = new SameLinkDepPrepGenPosNoContextMinQuarter();
		public SameLinkDepPrepGenPosNoContextMinQuarter() {super(TreeoutDepPrepGenPosNoContext.class, "DepPrepGenPosNoContext");}
	}
	
	public static class SameLinkDepPrepSpecPosNoContextMinQuarter extends SameLinkOverTreeoutMinimumQuarter {
		private static final long serialVersionUID = -903918531183705036L;
		public static final SameLinkDepPrepSpecPosNoContextMinQuarter inst = new SameLinkDepPrepSpecPosNoContextMinQuarter();
		public SameLinkDepPrepSpecPosNoContextMinQuarter() {super(TreeoutDepPrepSpecPosNoContext.class, "DepPrepSpecPosNoContext");}
	}

	
	public static class SameLinkDepFlatNoContextMinQuarter extends SameLinkOverTreeoutMinimumQuarter {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7303768195071442739L;
		public static final SameLinkDepFlatNoContextMinQuarter inst = new SameLinkDepFlatNoContextMinQuarter();
		public SameLinkDepFlatNoContextMinQuarter() {super(TreeoutDepFlatNoContext.class, "DepFlatNoContext");}
	}
	
	public static class SameLinkDepFlatGenPosNoContextMinQuarter extends SameLinkOverTreeoutMinimumQuarter {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7775985330264997325L;
		public static final SameLinkDepFlatGenPosNoContextMinQuarter inst = new SameLinkDepFlatGenPosNoContextMinQuarter();
		public SameLinkDepFlatGenPosNoContextMinQuarter() {super(TreeoutDepFlatGenPosNoContext.class, "DepFlatGenPosNoContext");}
	}
	
	public static class SameLinkDepFlatSpecPosNoContextMinQuarter extends SameLinkOverTreeoutMinimumQuarter {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7977700068386985911L;
		public static final SameLinkDepFlatSpecPosNoContextMinQuarter inst = new SameLinkDepFlatSpecPosNoContextMinQuarter();
		public SameLinkDepFlatSpecPosNoContextMinQuarter() {super(TreeoutDepFlatSpecPosNoContext.class, "DepFlatSpecPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepNoContextMinQuarter extends SameLinkOverTreeoutMinimumQuarter {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5421363421947378549L;
		public static final SameLinkDepFlatPrepNoContextMinQuarter inst = new SameLinkDepFlatPrepNoContextMinQuarter();
		public SameLinkDepFlatPrepNoContextMinQuarter() {super(TreeoutDepFlatPrepNoContext.class, "DepFlatPrepNoContext");}
	}
	
	public static class SameLinkDepFlatPrepGenPosNoContextMinQuarter extends SameLinkOverTreeoutMinimumQuarter {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6120837562966666352L;
		public static final SameLinkDepFlatPrepGenPosNoContextMinQuarter inst = new SameLinkDepFlatPrepGenPosNoContextMinQuarter();
		public SameLinkDepFlatPrepGenPosNoContextMinQuarter() {super(TreeoutDepFlatPrepGenPosNoContext.class, "DepFlatPrepGenPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepSpecPosNoContextMinQuarter extends SameLinkOverTreeoutMinimumQuarter {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8810137129188227013L;
		public static final SameLinkDepFlatPrepSpecPosNoContextMinQuarter inst = new SameLinkDepFlatPrepSpecPosNoContextMinQuarter();
		public SameLinkDepFlatPrepSpecPosNoContextMinQuarter() {super(TreeoutDepFlatPrepSpecPosNoContext.class, "DepFlatPrepSpecPosNoContext");}
	}


	///////// Min 1/5 ///////////////////////
	public static abstract class SameLinkOverTreeoutMinimumFifth extends SameLinkOverTreeoutOnlyFrequesntAiuses {
		private static final long serialVersionUID = -3114591036337649553L;
		private static final double MIN_PERCENT = 1.0/5;
		public SameLinkOverTreeoutMinimumFifth(Class<? extends Treeout> specClass, String textOutsMapKey) {
			super(specClass, textOutsMapKey, MIN_PERCENT);
		}
	}

	public static class SameLinkDepNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		private static final long serialVersionUID = -4304671459420513779L;
		public static final SameLinkDepNoContextMinFifth inst = new SameLinkDepNoContextMinFifth();
		public SameLinkDepNoContextMinFifth() {super(TreeoutDepNoContext.class, "DepNoContext");}
	}
	
	public static class SameLinkDepGenPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		private static final long serialVersionUID = -8650107758809734374L;
		public static final SameLinkDepGenPosNoContextMinFifth inst = new SameLinkDepGenPosNoContextMinFifth();
		public SameLinkDepGenPosNoContextMinFifth() {super(TreeoutDepGenPosNoContext.class, "DepGenPosNoContext");}
	}
	
	public static class SameLinkDepSpecPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		private static final long serialVersionUID = 6427556916345255280L;
		public static final SameLinkDepSpecPosNoContextMinFifth inst = new SameLinkDepSpecPosNoContextMinFifth();
		public SameLinkDepSpecPosNoContextMinFifth() {super(TreeoutDepSpecPosNoContext.class, "DepSpecPosNoContext");}
	}
	
	public static class SameLinkDepPrepNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		private static final long serialVersionUID = -2476013574526252453L;
		public static final SameLinkDepPrepNoContextMinFifth inst = new SameLinkDepPrepNoContextMinFifth();
		public SameLinkDepPrepNoContextMinFifth() {super(TreeoutDepPrepNoContext.class, "DepPrepNoContext");}
	}
	
	public static class SameLinkDepPrepGenPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		private static final long serialVersionUID = 7904358228008215820L;
		public static final SameLinkDepPrepGenPosNoContextMinFifth inst = new SameLinkDepPrepGenPosNoContextMinFifth();
		public SameLinkDepPrepGenPosNoContextMinFifth() {super(TreeoutDepPrepGenPosNoContext.class, "DepPrepGenPosNoContext");}
	}
	
	public static class SameLinkDepPrepSpecPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		private static final long serialVersionUID = 3244700973912308506L;
		public static final SameLinkDepPrepSpecPosNoContextMinFifth inst = new SameLinkDepPrepSpecPosNoContextMinFifth();
		public SameLinkDepPrepSpecPosNoContextMinFifth() {super(TreeoutDepPrepSpecPosNoContext.class, "DepPrepSpecPosNoContext");}
	}


	public static class SameLinkDepFlatNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8826556901052641381L;
		public static final SameLinkDepFlatNoContextMinFifth inst = new SameLinkDepFlatNoContextMinFifth();
		public SameLinkDepFlatNoContextMinFifth() {super(TreeoutDepFlatNoContext.class, "DepFlatNoContext");}
	}
	
	public static class SameLinkDepFlatGenPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -965729620599021448L;
		public static final SameLinkDepFlatGenPosNoContextMinFifth inst = new SameLinkDepFlatGenPosNoContextMinFifth();
		public SameLinkDepFlatGenPosNoContextMinFifth() {super(TreeoutDepFlatGenPosNoContext.class, "DepFlatGenPosNoContext");}
	}
	
	public static class SameLinkDepFlatSpecPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = 842126788414705372L;
		public static final SameLinkDepFlatSpecPosNoContextMinFifth inst = new SameLinkDepFlatSpecPosNoContextMinFifth();
		public SameLinkDepFlatSpecPosNoContextMinFifth() {super(TreeoutDepFlatSpecPosNoContext.class, "DepFlatSpecPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4246531246267976332L;
		public static final SameLinkDepFlatPrepNoContextMinFifth inst = new SameLinkDepFlatPrepNoContextMinFifth();
		public SameLinkDepFlatPrepNoContextMinFifth() {super(TreeoutDepFlatPrepNoContext.class, "DepFlatPrepNoContext");}
	}
	
	public static class SameLinkDepFlatPrepGenPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7015224018139918785L;
		public static final SameLinkDepFlatPrepGenPosNoContextMinFifth inst = new SameLinkDepFlatPrepGenPosNoContextMinFifth();
		public SameLinkDepFlatPrepGenPosNoContextMinFifth() {super(TreeoutDepFlatPrepGenPosNoContext.class, "DepFlatPrepGenPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepSpecPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5928812320004969968L;
		public static final SameLinkDepFlatPrepSpecPosNoContextMinFifth inst = new SameLinkDepFlatPrepSpecPosNoContextMinFifth();
		public SameLinkDepFlatPrepSpecPosNoContextMinFifth() {super(TreeoutDepFlatPrepSpecPosNoContext.class, "DepFlatPrepSpecPosNoContext");}
	}

	
	public static /*transient*/ FragmentLayer textFragmentLayer;
//	public FragmentLayer specFragmentLayer;
//	
//	private transient LoadingCache<JCas, FragmentLayer> cacheSpecFragmentLayers = CacheBuilder.newBuilder()
//	.maximumSize(100)
//	.build(new CacheLoader<JCas, FragmentLayer>() {
//		public FragmentLayer load(JCas spec) throws FragmentLayerException {
//			FragmentLayer result = new FragmentLayer(spec, Document.converter);
//			return result;
//		}
//	});
	
	private static LoadingCache<SpecTreeoutQuery, String> cacheSpecTreeouts = CacheBuilder.newBuilder()
	.maximumSize(1000)
	.build(new CacheLoader<SpecTreeoutQuery, String>() {
		public String load(SpecTreeoutQuery query) throws CASException {
			Treeout specTreeoutAnno = UimaUtils.selectCoveredSingle(query.specClass, query.specAius);
			String result = specTreeoutAnno.getValue();
			return result;
		}
	});
	
//	private static LoadingCache<SpecListTreeoutsQuery, Map<String, Integer>> cacheAiusFrequencies = CacheBuilder.newBuilder()
//	.maximumSize(100000)
//	.build(new CacheLoader<SpecListTreeoutsQuery, Map<String, Integer>>() {
//		public Map<String, Integer> load(SpecListTreeoutsQuery query) throws ExecutionException {
//			Map<String, Integer> result = Maps.newHashMap();
//			for (ArgumentInUsageSample aius : query.specAiuses) {
//				String specTreeout = cacheSpecTreeouts.get(new SpecTreeoutQuery(query.specClass, aius));
//				Integer count = result.get(specTreeout);
//				if (count == null) {
//					count = 0;
//				}
//				result.put(specTreeout, count+1);
//			}
//			return result;
//		}
//	});
	
	private static LoadingCache<VAll, Map<String, Integer>> cacheAiusFrequencies = CacheBuilder.newBuilder()
	.maximumSize(1000)
	.build(new CacheLoader<VAll, Map<String, Integer>>() {
		public Map<String, Integer> load(VAll vAll) throws ExecutionException {
			return SpecAnnotator.getFrequenciesFromVAll(vAll);
		}
	});

	private static /*transient*/ LoadingCache<TriggerArgQuery, FragmentAndReference> cacheTextTreeFragments = CacheBuilder.newBuilder()
	.maximumSize(1000)
	.build(new CacheLoader<TriggerArgQuery, FragmentAndReference>() {
		public FragmentAndReference load(TriggerArgQuery query) throws CASException, AceException, TreeAndParentMapException, TreeFragmentBuilderException, FragmentLayerException {
			try {
				FragmentAndReference linkFrag = textFragmentLayer.getRootLinkingTreeFragment(query.textTriggerToken, query.textArgHeadAnno, false, null);
				return linkFrag;
			} catch (AceAbnormalMessage e) {
				throw new FragmentLayerException(e);
			}
		}
	});
	
	private static /*transient*/ LoadingCache<TriggerArgQuery, FragmentAndReference> cacheTextTreeFragmentsNoConj = CacheBuilder.newBuilder()
	.maximumSize(1000)
	.build(new CacheLoader<TriggerArgQuery, FragmentAndReference>() {
		public FragmentAndReference load(TriggerArgQuery query) throws CASException, AceException, TreeAndParentMapException, TreeFragmentBuilderException, FragmentLayerException {
			try {
				FragmentAndReference linkFragNoConj = textFragmentLayer.getRootLinkingTreeFragment(query.textTriggerToken, query.textArgHeadAnno, true, null);
				return linkFragNoConj;
			} catch (AceAbnormalMessage e) {
				throw new FragmentLayerException(e);
			}
		}
	});
	
	private static /*transient*/ LoadingCache<TriggerArgQuery, Map<String, String>> cacheTextTreeouts = CacheBuilder.newBuilder()
	.maximumSize(1000)
	.build(new CacheLoader<TriggerArgQuery, Map<String, String>>() {
		public Map<String, String> load(TriggerArgQuery query) throws FragmentLayerException, CASException, AceException, TreeAndParentMapException, TreeFragmentBuilderException, TreeStringGeneratorException {
//			Annotation textTrigger = textAnnos.getKey();
//			Annotation textArg = textAnnos.getValue();
			
			List<BasicNode> subroots = null;
			List<BasicNode> subrootsNoConj = null;
			String err = null;
			FragmentAndReference linkFrag = null;
			try {
				linkFrag = cacheTextTreeFragments.get(query);
				subroots = ImmutableList.of(linkFrag.getFragmentRoot());
				FragmentAndReference linkFragNoConj = cacheTextTreeFragmentsNoConj.get(query);
				subrootsNoConj = ImmutableList.of(linkFragNoConj.getFragmentRoot());
			} catch (Exception e) {
				System.err.printf("DependencySignalMechanism: Got error while calcing link: %s\n", e);
				e.printStackTrace(System.err);
				System.err.printf("#############################################\n");
				err = e.getMessage();
			}
				
			Map<String, String> result = Maps.newHashMap();
			result.put("DepNoContext",			err!=null?err: TreeToLineString.getStringRel(subroots, false, true));
			result.put("DepGenPosNoContext",	err!=null?err: TreeToLineString.getStringRelCanonicalPos(subroots, false, true));
			result.put("DepSpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelPos(subroots, false, true));
			result.put("DepFlatNoContext",			err!=null?err: TreeToLineString.getStringRelFlat(subroots, false, true));
			result.put("DepFlatGenPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatCanonicalPos(subroots, false, true));
			result.put("DepFlatSpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatPos(subroots, false, true));
//			result.put("DepWithContext",		err!=null?err: TreeToLineString.getStringRel(subroots, true, true));
//			result.put("DepGenPosWithContext",	err!=null?err: TreeToLineString.getStringRelCanonicalPos(subroots, true, true));
//			result.put("DepSpecPosWithContext",	err!=null?err: TreeToLineString.getStringRelPos(subroots, true, true));

			result.put("DepPrepNoContext",			err!=null?err: TreeToLineString.getStringRelPrep(subrootsNoConj, false, true));
			result.put("DepPrepGenPosNoContext",	err!=null?err: TreeToLineString.getStringRelPrepCanonicalPos(subrootsNoConj, false, true));
			result.put("DepPrepSpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelPrepPos(subrootsNoConj, false, true));
			result.put("DepFlatPrepNoContext",			err!=null?err: TreeToLineString.getStringRelFlatPrep(subrootsNoConj, false, true));
			result.put("DepFlatPrepGenPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatPrepCanonicalPos(subrootsNoConj, false, true));
			result.put("DepFlatPrepSpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatPrepPos(subrootsNoConj, false, true));
//			result.put("DepPrepWithContext",		err!=null?err: TreeToLineString.getStringRelPrep(subrootsNoConj, true, true));
//			result.put("DepPrepGenPosWithContext",	err!=null?err: TreeToLineString.getStringRelPrepCanonicalPos(subrootsNoConj, true, true));
//			result.put("DepPrepSpecPosWithContext",	err!=null?err: TreeToLineString.getStringRelPrepPos(subrootsNoConj, true, true));

			return result;
		}
	});
	
	private static PrintStream outFile1;
	private static PrintStream outFile2;
	private static int outFileId = 0;
	private static Class<? extends ArgumentInUsageSampleScorer> oneScorerClass;
	private static LinkedHashSet<String> entries1;
	private static LinkedHashSet<String> entries2;
}
