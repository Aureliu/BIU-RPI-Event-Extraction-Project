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

import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatUp2GenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatUp2NoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepUp2GenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepUp2NoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepUp2SpecPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatUp2SpecPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepUp2GenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepUp2NoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepUp2GenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepUp2NoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepUp2SpecPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepUp2SpecPosNoContext;

import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatUp3GenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatUp3NoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepUp3GenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepUp3NoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatPrepUp3SpecPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepFlatUp3SpecPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepUp3GenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepUp3NoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepUp3GenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepUp3NoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepUp3SpecPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepUp3SpecPosNoContext;

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
			outFile1.printf("Id^Doc^Sentence^Subtype^Role^Trigger^ArgHead^Fragment^Facet^" + "WordDepNoContext^" +
					"DepNoContext^DepGenPosNoContext^DepSpecPosNoContext^DepPrepNoContext^DepPrepGenPosNoContext^DepPrepSpecPosNoContext^DepFlatNoContext^DepFlatGenPosNoContext^DepFlatSpecPosNoContext^DepFlatPrepNoContext^DepFlatPrepGenPosNoContext^DepFlatPrepSpecPosNoContext" +
					"DepUp2NoContext^DepUp2GenPosNoContext^DepUp2SpecPosNoContext^DepPrepUp2NoContext^DepPrepUp2GenPosNoContext^DepPrepUp2SpecPosNoContext^DepFlatUp2NoContext^DepFlatUp2GenPosNoContext^DepFlatUp2SpecPosNoContext^DepFlatPrepUp2NoContext^DepFlatPrepUp2GenPosNoContext^DepFlatPrepUp2SpecPosNoContext" +
					"DepUp3NoContext^DepUp3GenPosNoContext^DepUp3SpecPosNoContext^DepPrepUp3NoContext^DepPrepUp3GenPosNoContext^DepPrepUp3SpecPosNoContext^DepFlatUp3NoContext^DepFlatUp3GenPosNoContext^DepFlatUp3SpecPosNoContext^DepFlatPrepUp3NoContext^DepFlatPrepUp3GenPosNoContext^DepFlatPrepUp3SpecPosNoContext" +
					"\n");			
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

		case ANALYSIS11up:
			oneScorerClass = SameLinkDepUp2NoContext.class;
			addArgumentDependent(new ScorerData("DP_DEP_U2_NOCON",			SameLinkDepUp2NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U2_GENPOS_NOCON",	SameLinkDepUp2GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U2_SPECPOS_NOCON",	SameLinkDepUp2SpecPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_NOCON",			SameLinkDepPrepUp2NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_GENPOS_NOCON",		SameLinkDepPrepUp2GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_SPECPOS_NOCON",	SameLinkDepPrepUp2SpecPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_NOCON",			SameLinkDepUp3NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_GENPOS_NOCON",	SameLinkDepUp3GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_SPECPOS_NOCON",	SameLinkDepUp3SpecPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_NOCON",			SameLinkDepPrepUp3NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_GENPOS_NOCON",		SameLinkDepPrepUp3GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_SPECPOS_NOCON",	SameLinkDepPrepUp3SpecPosNoContext.inst,	Aggregator.Any.inst		));
			break;

		case ANALYSIS11up2:
			oneScorerClass = SameLinkDepUp2NoContext.class;
			addArgumentDependent(new ScorerData("DP_DEP_U2_NOCON",			SameLinkDepUp2NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U2_GENPOS_NOCON",	SameLinkDepUp2GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U2_SPECPOS_NOCON",	SameLinkDepUp2SpecPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_NOCON",			SameLinkDepPrepUp2NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_GENPOS_NOCON",		SameLinkDepPrepUp2GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_SPECPOS_NOCON",	SameLinkDepPrepUp2SpecPosNoContext.inst,	Aggregator.Any.inst		));
			break;
		case ANALYSIS11up3:
			oneScorerClass = SameLinkDepUp3NoContext.class;
			addArgumentDependent(new ScorerData("DP_DEP_U3_NOCON",			SameLinkDepUp3NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_GENPOS_NOCON",	SameLinkDepUp3GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_SPECPOS_NOCON",	SameLinkDepUp3SpecPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_NOCON",			SameLinkDepPrepUp3NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_GENPOS_NOCON",		SameLinkDepPrepUp3GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_SPECPOS_NOCON",	SameLinkDepPrepUp3SpecPosNoContext.inst,	Aggregator.Any.inst		));
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

		case ANALYSIS11fup:
			oneScorerClass = SameLinkDepFlatUp2NoContext.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_NOCON",			SameLinkDepFlatUp2NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_GENPOS_NOCON",	SameLinkDepFlatUp2GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_SPECPOS_NOCON",	SameLinkDepFlatUp2SpecPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_NOCON",			SameLinkDepFlatPrepUp2NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_GENPOS_NOCON",		SameLinkDepFlatPrepUp2GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_SPECPOS_NOCON",	SameLinkDepFlatPrepUp2SpecPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_NOCON",			SameLinkDepFlatUp3NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_GENPOS_NOCON",	SameLinkDepFlatUp3GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_SPECPOS_NOCON",	SameLinkDepFlatUp3SpecPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_NOCON",			SameLinkDepFlatPrepUp3NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_GENPOS_NOCON",		SameLinkDepFlatPrepUp3GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_SPECPOS_NOCON",	SameLinkDepFlatPrepUp3SpecPosNoContext.inst,	Aggregator.Any.inst		));
			break;

		case ANALYSIS11fup2:
			oneScorerClass = SameLinkDepFlatUp2NoContext.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_NOCON",			SameLinkDepFlatUp2NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_GENPOS_NOCON",	SameLinkDepFlatUp2GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_SPECPOS_NOCON",	SameLinkDepFlatUp2SpecPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_NOCON",			SameLinkDepFlatPrepUp2NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_GENPOS_NOCON",		SameLinkDepFlatPrepUp2GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_SPECPOS_NOCON",	SameLinkDepFlatPrepUp2SpecPosNoContext.inst,	Aggregator.Any.inst		));
			break;
		case ANALYSIS11fup3:
			oneScorerClass = SameLinkDepFlatUp3NoContext.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_NOCON",			SameLinkDepFlatUp3NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_GENPOS_NOCON",	SameLinkDepFlatUp3GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_SPECPOS_NOCON",	SameLinkDepFlatUp3SpecPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_NOCON",			SameLinkDepFlatPrepUp3NoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_GENPOS_NOCON",		SameLinkDepFlatPrepUp3GenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_SPECPOS_NOCON",	SameLinkDepFlatPrepUp3SpecPosNoContext.inst,	Aggregator.Any.inst		));
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

		case ANALYSIS13up:
			oneScorerClass = SameLinkDepUp2NoContextMinThird.class;
			addArgumentDependent(new ScorerData("DP_DEP_U2_NOCON_1/3",			SameLinkDepUp2NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U2_GENPOS_NOCON_1/3",	SameLinkDepUp2GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U2_SPECPOS_NOCON_1/3",	SameLinkDepUp2SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_NOCON_1/3",			SameLinkDepPrepUp2NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_GENPOS_NOCON_1/3",		SameLinkDepPrepUp2GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_SPECPOS_NOCON_1/3",	SameLinkDepPrepUp2SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_NOCON_1/3",			SameLinkDepUp3NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_GENPOS_NOCON_1/3",	SameLinkDepUp3GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_SPECPOS_NOCON_1/3",	SameLinkDepUp3SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_NOCON_1/3",			SameLinkDepPrepUp3NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_GENPOS_NOCON_1/3",		SameLinkDepPrepUp3GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_SPECPOS_NOCON_1/3",	SameLinkDepPrepUp3SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			break;

		case ANALYSIS13up2:
			oneScorerClass = SameLinkDepUp2NoContextMinThird.class;
			addArgumentDependent(new ScorerData("DP_DEP_U2_NOCON_1/3",			SameLinkDepUp2NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U2_GENPOS_NOCON_1/3",	SameLinkDepUp2GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U2_SPECPOS_NOCON_1/3",	SameLinkDepUp2SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_NOCON_1/3",			SameLinkDepPrepUp2NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_GENPOS_NOCON_1/3",		SameLinkDepPrepUp2GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_SPECPOS_NOCON_1/3",	SameLinkDepPrepUp2SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			break;
		case ANALYSIS13up3:
			oneScorerClass = SameLinkDepUp3NoContextMinThird.class;
			addArgumentDependent(new ScorerData("DP_DEP_U3_NOCON_1/3",			SameLinkDepUp3NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_GENPOS_NOCON_1/3",	SameLinkDepUp3GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_SPECPOS_NOCON_1/3",	SameLinkDepUp3SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_NOCON_1/3",			SameLinkDepPrepUp3NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_GENPOS_NOCON_1/3",		SameLinkDepPrepUp3GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_SPECPOS_NOCON_1/3",	SameLinkDepPrepUp3SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
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

		case ANALYSIS13fup:
			oneScorerClass = SameLinkDepFlatUp2NoContextMinThird.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_NOCON_1/3",			SameLinkDepFlatUp2NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_GENPOS_NOCON_1/3",	SameLinkDepFlatUp2GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_SPECPOS_NOCON_1/3",	SameLinkDepFlatUp2SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_NOCON_1/3",			SameLinkDepFlatPrepUp2NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_GENPOS_NOCON_1/3",		SameLinkDepFlatPrepUp2GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_SPECPOS_NOCON_1/3",	SameLinkDepFlatPrepUp2SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_NOCON_1/3",			SameLinkDepFlatUp3NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_GENPOS_NOCON_1/3",	SameLinkDepFlatUp3GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_SPECPOS_NOCON_1/3",	SameLinkDepFlatUp3SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_NOCON_1/3",			SameLinkDepFlatPrepUp3NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_GENPOS_NOCON_1/3",		SameLinkDepFlatPrepUp3GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_SPECPOS_NOCON_1/3",	SameLinkDepFlatPrepUp3SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			break;

		case ANALYSIS13fup2:
			oneScorerClass = SameLinkDepFlatUp2NoContextMinThird.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_NOCON_1/3",			SameLinkDepFlatUp2NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_GENPOS_NOCON_1/3",	SameLinkDepFlatUp2GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_SPECPOS_NOCON_1/3",	SameLinkDepFlatUp2SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_NOCON_1/3",			SameLinkDepFlatPrepUp2NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_GENPOS_NOCON_1/3",		SameLinkDepFlatPrepUp2GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_SPECPOS_NOCON_1/3",	SameLinkDepFlatPrepUp2SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			break;
		case ANALYSIS13fup3:
			oneScorerClass = SameLinkDepFlatUp3NoContextMinThird.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_NOCON_1/3",			SameLinkDepFlatUp3NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_GENPOS_NOCON_1/3",	SameLinkDepFlatUp3GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_SPECPOS_NOCON_1/3",	SameLinkDepFlatUp3SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_NOCON_1/3",			SameLinkDepFlatPrepUp3NoContextMinThird.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_GENPOS_NOCON_1/3",		SameLinkDepFlatPrepUp3GenPosNoContextMinThird.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_SPECPOS_NOCON_1/3",	SameLinkDepFlatPrepUp3SpecPosNoContextMinThird.inst,	Aggregator.Any.inst		));
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

		case ANALYSIS15up:
			oneScorerClass = SameLinkDepUp2NoContextMinFifth.class;
			addArgumentDependent(new ScorerData("DP_DEP_U2_NOCON_1/5",			SameLinkDepUp2NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U2_GENPOS_NOCON_1/5",	SameLinkDepUp2GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U2_SPECPOS_NOCON_1/5",	SameLinkDepUp2SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_NOCON_1/5",			SameLinkDepPrepUp2NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_GENPOS_NOCON_1/5",		SameLinkDepPrepUp2GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_SPECPOS_NOCON_1/5",	SameLinkDepPrepUp2SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_NOCON_1/5",			SameLinkDepUp3NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_GENPOS_NOCON_1/5",	SameLinkDepUp3GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_SPECPOS_NOCON_1/5",	SameLinkDepUp3SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_NOCON_1/5",			SameLinkDepPrepUp3NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_GENPOS_NOCON_1/5",		SameLinkDepPrepUp3GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_SPECPOS_NOCON_1/5",	SameLinkDepPrepUp3SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));

		case ANALYSIS15up2:
			oneScorerClass = SameLinkDepUp2NoContextMinFifth.class;
			addArgumentDependent(new ScorerData("DP_DEP_U2_NOCON_1/5",			SameLinkDepUp2NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U2_GENPOS_NOCON_1/5",	SameLinkDepUp2GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U2_SPECPOS_NOCON_1/5",	SameLinkDepUp2SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_NOCON_1/5",			SameLinkDepPrepUp2NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_GENPOS_NOCON_1/5",		SameLinkDepPrepUp2GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U2_SPECPOS_NOCON_1/5",	SameLinkDepPrepUp2SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			break;
		case ANALYSIS15up3:
			oneScorerClass = SameLinkDepUp3NoContextMinFifth.class;
			addArgumentDependent(new ScorerData("DP_DEP_U3_NOCON_1/5",			SameLinkDepUp3NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_GENPOS_NOCON_1/5",	SameLinkDepUp3GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_U3_SPECPOS_NOCON_1/5",	SameLinkDepUp3SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_NOCON_1/5",			SameLinkDepPrepUp3NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_GENPOS_NOCON_1/5",		SameLinkDepPrepUp3GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_U3_SPECPOS_NOCON_1/5",	SameLinkDepPrepUp3SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));

		case ANALYSIS15f:
			oneScorerClass = SameLinkDepFlatNoContextMinFifth.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_NOCON_1/5",			SameLinkDepFlatNoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_GENPOS_NOCON_1/5",	SameLinkDepFlatGenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_SPECPOS_NOCON_1/5",	SameLinkDepFlatSpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_NOCON_1/5",			SameLinkDepFlatPrepNoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_GENPOS_NOCON_1/5",		SameLinkDepFlatPrepGenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_SPECPOS_NOCON_1/5",	SameLinkDepFlatPrepSpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));

		case ANALYSIS15fup:
			oneScorerClass = SameLinkDepFlatUp2NoContextMinFifth.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_NOCON_1/5",			SameLinkDepFlatUp2NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_GENPOS_NOCON_1/5",	SameLinkDepFlatUp2GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_SPECPOS_NOCON_1/5",	SameLinkDepFlatUp2SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_NOCON_1/5",			SameLinkDepFlatPrepUp2NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_GENPOS_NOCON_1/5",		SameLinkDepFlatPrepUp2GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_SPECPOS_NOCON_1/5",	SameLinkDepFlatPrepUp2SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_NOCON_1/5",			SameLinkDepFlatUp3NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_GENPOS_NOCON_1/5",	SameLinkDepFlatUp3GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_SPECPOS_NOCON_1/5",	SameLinkDepFlatUp3SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_NOCON_1/5",			SameLinkDepFlatPrepUp3NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_GENPOS_NOCON_1/5",		SameLinkDepFlatPrepUp3GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_SPECPOS_NOCON_1/5",	SameLinkDepFlatPrepUp3SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));

		case ANALYSIS15fup2:
			oneScorerClass = SameLinkDepFlatUp2NoContextMinFifth.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_NOCON_1/5",			SameLinkDepFlatUp2NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_GENPOS_NOCON_1/5",	SameLinkDepFlatUp2GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U2_SPECPOS_NOCON_1/5",	SameLinkDepFlatUp2SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_NOCON_1/5",			SameLinkDepFlatPrepUp2NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_GENPOS_NOCON_1/5",		SameLinkDepFlatPrepUp2GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U2_SPECPOS_NOCON_1/5",	SameLinkDepFlatPrepUp2SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			break;
		case ANALYSIS15fup3:
			oneScorerClass = SameLinkDepFlatUp3NoContextMinFifth.class;
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_NOCON_1/5",			SameLinkDepFlatUp3NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_GENPOS_NOCON_1/5",	SameLinkDepFlatUp3GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_U3_SPECPOS_NOCON_1/5",	SameLinkDepFlatUp3SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_NOCON_1/5",			SameLinkDepFlatPrepUp3NoContextMinFifth.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_GENPOS_NOCON_1/5",		SameLinkDepFlatPrepUp3GenPosNoContextMinFifth.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_F_PREP_U3_SPECPOS_NOCON_1/5",	SameLinkDepFlatPrepUp3SpecPosNoContextMinFifth.inst,	Aggregator.Any.inst		));

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

					String entry1 = String.format("%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s^%s\n",
							//outFileId,
							//StringUtils.abbreviate(query.textTriggerToken.getCAS().getJCas().getDocumentText().replace('\n',' '), 40),
							SentenceInstance.currDocId,
							SentenceInstance.currSentInstId,
							SentenceInstance.currEventType,
							SentenceInstance.currRole,
							UimaUtils.annotationToString(textTriggerToken, false, false),
							UimaUtils.annotationToString(textArgHeadAnno).replace('\n',' ').replace("\"", "*"),
							origSentenceStr.replace("\"", "*"),
							//Utils.treeToSurfaceText(linkFrag.getFragmentRoot()),
							facetStr,
							
							outsMap.get("WordDepNoContext"),

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
							outsMap.get("DepFlatPrepSpecPosNoContext"),
							
							outsMap.get("DepUp2NoContext"),
							outsMap.get("DepUp2GenPosNoContext"),
							outsMap.get("DepUp2SpecPosNoContext"),
							outsMap.get("DepPrepUp2NoContext"),
							outsMap.get("DepPrepUp2GenPosNoContext"),
							outsMap.get("DepPrepUp2SpecPosNoContext"),
							outsMap.get("DepFlatUp2NoContext"),
							outsMap.get("DepFlatUp2GenPosNoContext"),
							outsMap.get("DepFlatUp2SpecPosNoContext"),
							outsMap.get("DepFlatPrepUp2NoContext"),
							outsMap.get("DepFlatPrepUp2GenPosNoContext"),
							outsMap.get("DepFlatPrepUp2SpecPosNoContext"),
							
							outsMap.get("DepUp3NoContext"),
							outsMap.get("DepUp3GenPosNoContext"),
							outsMap.get("DepUp3SpecPosNoContext"),
							outsMap.get("DepPrepUp3NoContext"),
							outsMap.get("DepPrepUp3GenPosNoContext"),
							outsMap.get("DepPrepUp3SpecPosNoContext"),
							outsMap.get("DepFlatUp3NoContext"),
							outsMap.get("DepFlatUp3GenPosNoContext"),
							outsMap.get("DepFlatUp3SpecPosNoContext"),
							outsMap.get("DepFlatPrepUp3NoContext"),
							outsMap.get("DepFlatPrepUp3GenPosNoContext"),
							outsMap.get("DepFlatPrepUp3SpecPosNoContext")
							
							
							);
					String entry2 = String.format("%s^%s^%s^%s^%s^%s^%s\nWordDepNoContext:       %s\nDepNoContext:       %s\nDepFlatNoContext:   %s\nDepPrepNoContext:   %s\n%s\n\n",
							//outFileId,
							//StringUtils.abbreviate(query.textTriggerToken.getCAS().getJCas().getDocumentText().replace('\n',' '), 40),
							SentenceInstance.currDocId,
							SentenceInstance.currSentInstId,
							SentenceInstance.currEventType,
							SentenceInstance.currRole,
							UimaUtils.annotationToString(textTriggerToken, false, false),
							UimaUtils.annotationToString(textArgHeadAnno).replace('\n',' ').replace("\"", "*"),
							origSentenceStr.replace("\"", "*"),
							outsMap.get("WordDepNoContext"),
							outsMap.get("DepNoContext"),
							outsMap.get("DepFlatNoContext"),
							outsMap.get("DepPrepNoContext"),
							treePrintStr
							);
					
					// DEBUG
//					if (entry2.contains("combat[2521:2527]")) {
//						System.out.printf("");
//					}
					////
					
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
	
	
	
	public static class SameLinkDepUp2NoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6366228228768519615L;
		public static final SameLinkDepUp2NoContext inst = new SameLinkDepUp2NoContext();
		public SameLinkDepUp2NoContext() {super(TreeoutDepUp2NoContext.class, "DepUp2NoContext");}
	}
	
	public static class SameLinkDepUp2GenPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8728370677766648066L;
		public static final SameLinkDepUp2GenPosNoContext inst = new SameLinkDepUp2GenPosNoContext();
		public SameLinkDepUp2GenPosNoContext() {super(TreeoutDepUp2GenPosNoContext.class, "DepUp2GenPosNoContext");}
	}
	
	public static class SameLinkDepUp2SpecPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = -618387100915856992L;
		public static final SameLinkDepUp2SpecPosNoContext inst = new SameLinkDepUp2SpecPosNoContext();
		public SameLinkDepUp2SpecPosNoContext() {super(TreeoutDepUp2SpecPosNoContext.class, "DepUp2SpecPosNoContext");}
	}
	
	public static class SameLinkDepPrepUp2NoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = -9100040154465906112L;
		public static final SameLinkDepPrepUp2NoContext inst = new SameLinkDepPrepUp2NoContext();
		public SameLinkDepPrepUp2NoContext() {super(TreeoutDepPrepUp2NoContext.class, "DepPrepUp2NoContext");}
	}
	
	public static class SameLinkDepPrepUp2GenPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 339711031156326783L;
		public static final SameLinkDepPrepUp2GenPosNoContext inst = new SameLinkDepPrepUp2GenPosNoContext();
		public SameLinkDepPrepUp2GenPosNoContext() {super(TreeoutDepPrepUp2GenPosNoContext.class, "DepPrepUp2GenPosNoContext");}
	}
	
	public static class SameLinkDepPrepUp2SpecPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3131874517085546789L;
		public static final SameLinkDepPrepUp2SpecPosNoContext inst = new SameLinkDepPrepUp2SpecPosNoContext();
		public SameLinkDepPrepUp2SpecPosNoContext() {super(TreeoutDepPrepUp2SpecPosNoContext.class, "DepPrepUp2SpecPosNoContext");}
	}
	

	public static class SameLinkDepFlatUp2NoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4996151074607789833L;
		public static final SameLinkDepFlatUp2NoContext inst = new SameLinkDepFlatUp2NoContext();
		public SameLinkDepFlatUp2NoContext() {super(TreeoutDepFlatUp2NoContext.class, "DepFlatUp2NoContext");}
	}
	
	public static class SameLinkDepFlatUp2GenPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3132345465806861409L;
		public static final SameLinkDepFlatUp2GenPosNoContext inst = new SameLinkDepFlatUp2GenPosNoContext();
		public SameLinkDepFlatUp2GenPosNoContext() {super(TreeoutDepFlatUp2GenPosNoContext.class, "DepFlatUp2GenPosNoContext");}
	}
	
	public static class SameLinkDepFlatUp2SpecPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4211169453393160190L;
		public static final SameLinkDepFlatUp2SpecPosNoContext inst = new SameLinkDepFlatUp2SpecPosNoContext();
		public SameLinkDepFlatUp2SpecPosNoContext() {super(TreeoutDepFlatUp2SpecPosNoContext.class, "DepFlatUp2SpecPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp2NoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 607840944313778849L;
		public static final SameLinkDepFlatPrepUp2NoContext inst = new SameLinkDepFlatPrepUp2NoContext();
		public SameLinkDepFlatPrepUp2NoContext() {super(TreeoutDepFlatPrepUp2NoContext.class, "DepFlatPrepUp2NoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp2GenPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2100046379332039470L;
		public static final SameLinkDepFlatPrepUp2GenPosNoContext inst = new SameLinkDepFlatPrepUp2GenPosNoContext();
		public SameLinkDepFlatPrepUp2GenPosNoContext() {super(TreeoutDepFlatPrepUp2GenPosNoContext.class, "DepPrepUp2GenPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp2SpecPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2373321145935181628L;
		public static final SameLinkDepFlatPrepUp2SpecPosNoContext inst = new SameLinkDepFlatPrepUp2SpecPosNoContext();
		public SameLinkDepFlatPrepUp2SpecPosNoContext() {super(TreeoutDepFlatPrepUp2SpecPosNoContext.class, "DepFlatPrepUp2SpecPosNoContext");}
	}
	
	
	
	public static class SameLinkDepUp3NoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3811418667558896584L;
		public static final SameLinkDepUp3NoContext inst = new SameLinkDepUp3NoContext();
		public SameLinkDepUp3NoContext() {super(TreeoutDepUp3NoContext.class, "DepUp3NoContext");}
	}
	
	public static class SameLinkDepUp3GenPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2464391878232878646L;
		public static final SameLinkDepUp3GenPosNoContext inst = new SameLinkDepUp3GenPosNoContext();
		public SameLinkDepUp3GenPosNoContext() {super(TreeoutDepUp3GenPosNoContext.class, "DepUp3GenPosNoContext");}
	}
	
	public static class SameLinkDepUp3SpecPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5262950156643874187L;
		public static final SameLinkDepUp3SpecPosNoContext inst = new SameLinkDepUp3SpecPosNoContext();
		public SameLinkDepUp3SpecPosNoContext() {super(TreeoutDepUp3SpecPosNoContext.class, "DepUp3SpecPosNoContext");}
	}
	
	public static class SameLinkDepPrepUp3NoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6790836187437069179L;
		public static final SameLinkDepPrepUp3NoContext inst = new SameLinkDepPrepUp3NoContext();
		public SameLinkDepPrepUp3NoContext() {super(TreeoutDepPrepUp3NoContext.class, "DepPrepUp3NoContext");}
	}
	
	public static class SameLinkDepPrepUp3GenPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6590522238354495919L;
		public static final SameLinkDepPrepUp3GenPosNoContext inst = new SameLinkDepPrepUp3GenPosNoContext();
		public SameLinkDepPrepUp3GenPosNoContext() {super(TreeoutDepPrepUp3GenPosNoContext.class, "DepPrepUp3GenPosNoContext");}
	}
	
	public static class SameLinkDepPrepUp3SpecPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1725292597400084300L;
		public static final SameLinkDepPrepUp3SpecPosNoContext inst = new SameLinkDepPrepUp3SpecPosNoContext();
		public SameLinkDepPrepUp3SpecPosNoContext() {super(TreeoutDepPrepUp3SpecPosNoContext.class, "DepPrepUp3SpecPosNoContext");}
	}
	

	public static class SameLinkDepFlatUp3NoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9021100966258029709L;
		public static final SameLinkDepFlatUp3NoContext inst = new SameLinkDepFlatUp3NoContext();
		public SameLinkDepFlatUp3NoContext() {super(TreeoutDepFlatUp3NoContext.class, "DepFlatUp3NoContext");}
	}
	
	public static class SameLinkDepFlatUp3GenPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1553720860019993260L;
		public static final SameLinkDepFlatUp3GenPosNoContext inst = new SameLinkDepFlatUp3GenPosNoContext();
		public SameLinkDepFlatUp3GenPosNoContext() {super(TreeoutDepFlatUp3GenPosNoContext.class, "DepFlatUp3GenPosNoContext");}
	}
	
	public static class SameLinkDepFlatUp3SpecPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9089103840370145405L;
		public static final SameLinkDepFlatUp3SpecPosNoContext inst = new SameLinkDepFlatUp3SpecPosNoContext();
		public SameLinkDepFlatUp3SpecPosNoContext() {super(TreeoutDepFlatUp3SpecPosNoContext.class, "DepFlatUp3SpecPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp3NoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7602089860597571088L;
		public static final SameLinkDepFlatPrepUp3NoContext inst = new SameLinkDepFlatPrepUp3NoContext();
		public SameLinkDepFlatPrepUp3NoContext() {super(TreeoutDepFlatPrepUp3NoContext.class, "DepFlatPrepUp3NoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp3GenPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8613559836318918113L;
		public static final SameLinkDepFlatPrepUp3GenPosNoContext inst = new SameLinkDepFlatPrepUp3GenPosNoContext();
		public SameLinkDepFlatPrepUp3GenPosNoContext() {super(TreeoutDepFlatPrepUp3GenPosNoContext.class, "DepPrepUp3GenPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp3SpecPosNoContext extends SameLinkOverTreeout {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3331435998820954246L;
		public static final SameLinkDepFlatPrepUp3SpecPosNoContext inst = new SameLinkDepFlatPrepUp3SpecPosNoContext();
		public SameLinkDepFlatPrepUp3SpecPosNoContext() {super(TreeoutDepFlatPrepUp3SpecPosNoContext.class, "DepFlatPrepUp3SpecPosNoContext");}
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


	
	public static class SameLinkDepUp2NoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2792948195409307341L;
		public static final SameLinkDepUp2NoContextMinThird inst = new SameLinkDepUp2NoContextMinThird();
		public SameLinkDepUp2NoContextMinThird() {super(TreeoutDepUp2NoContext.class, "DepUp2NoContext");}
	}
	
	public static class SameLinkDepUp2GenPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5516621923669155468L;
		public static final SameLinkDepUp2GenPosNoContextMinThird inst = new SameLinkDepUp2GenPosNoContextMinThird();
		public SameLinkDepUp2GenPosNoContextMinThird() {super(TreeoutDepUp2GenPosNoContext.class, "DepUp2GenPosNoContext");}
	}
	
	public static class SameLinkDepUp2SpecPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7396186392882195857L;
		public static final SameLinkDepUp2SpecPosNoContextMinThird inst = new SameLinkDepUp2SpecPosNoContextMinThird();
		public SameLinkDepUp2SpecPosNoContextMinThird() {super(TreeoutDepUp2SpecPosNoContext.class, "DepUp2SpecPosNoContext");}
	}
	
	public static class SameLinkDepPrepUp2NoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1316766446400804168L;
		public static final SameLinkDepPrepUp2NoContextMinThird inst = new SameLinkDepPrepUp2NoContextMinThird();
		public SameLinkDepPrepUp2NoContextMinThird() {super(TreeoutDepPrepUp2NoContext.class, "DepPrepUp2NoContext");}
	}
	
	public static class SameLinkDepPrepUp2GenPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = -969464122883583682L;
		public static final SameLinkDepPrepUp2GenPosNoContextMinThird inst = new SameLinkDepPrepUp2GenPosNoContextMinThird();
		public SameLinkDepPrepUp2GenPosNoContextMinThird() {super(TreeoutDepPrepUp2GenPosNoContext.class, "DepPrepUp2GenPosNoContext");}
	}
	
	public static class SameLinkDepPrepUp2SpecPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1737285408631454369L;
		public static final SameLinkDepPrepUp2SpecPosNoContextMinThird inst = new SameLinkDepPrepUp2SpecPosNoContextMinThird();
		public SameLinkDepPrepUp2SpecPosNoContextMinThird() {super(TreeoutDepPrepUp2SpecPosNoContext.class, "DepPrepUp2SpecPosNoContext");}
	}

	
	public static class SameLinkDepFlatUp2NoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6423602148170533528L;
		public static final SameLinkDepFlatUp2NoContextMinThird inst = new SameLinkDepFlatUp2NoContextMinThird();
		public SameLinkDepFlatUp2NoContextMinThird() {super(TreeoutDepFlatUp2NoContext.class, "DepFlatUp2NoContext");}
	}
	
	public static class SameLinkDepFlatUp2GenPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7399915483762443415L;
		public static final SameLinkDepFlatUp2GenPosNoContextMinThird inst = new SameLinkDepFlatUp2GenPosNoContextMinThird();
		public SameLinkDepFlatUp2GenPosNoContextMinThird() {super(TreeoutDepFlatUp2GenPosNoContext.class, "DepFlatUp2GenPosNoContext");}
	}
	
	public static class SameLinkDepFlatUp2SpecPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7985298291332741289L;
		public static final SameLinkDepFlatUp2SpecPosNoContextMinThird inst = new SameLinkDepFlatUp2SpecPosNoContextMinThird();
		public SameLinkDepFlatUp2SpecPosNoContextMinThird() {super(TreeoutDepFlatUp2SpecPosNoContext.class, "DepFlatUp2SpecPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp2NoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4350976731381153485L;
		public static final SameLinkDepFlatPrepUp2NoContextMinThird inst = new SameLinkDepFlatPrepUp2NoContextMinThird();
		public SameLinkDepFlatPrepUp2NoContextMinThird() {super(TreeoutDepFlatPrepUp2NoContext.class, "DepFlatPrepUp2NoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp2GenPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4458254320957554179L;
		public static final SameLinkDepFlatPrepUp2GenPosNoContextMinThird inst = new SameLinkDepFlatPrepUp2GenPosNoContextMinThird();
		public SameLinkDepFlatPrepUp2GenPosNoContextMinThird() {super(TreeoutDepFlatPrepUp2GenPosNoContext.class, "DepFlatPrepUp2GenPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp2SpecPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = -443451570243392212L;
		public static final SameLinkDepFlatPrepUp2SpecPosNoContextMinThird inst = new SameLinkDepFlatPrepUp2SpecPosNoContextMinThird();
		public SameLinkDepFlatPrepUp2SpecPosNoContextMinThird() {super(TreeoutDepFlatPrepUp2SpecPosNoContext.class, "DepFlatPrepUp2SpecPosNoContext");}
	}


	
	public static class SameLinkDepUp3NoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 915643028686841857L;
		public static final SameLinkDepUp3NoContextMinThird inst = new SameLinkDepUp3NoContextMinThird();
		public SameLinkDepUp3NoContextMinThird() {super(TreeoutDepUp3NoContext.class, "DepUp3NoContext");}
	}
	
	public static class SameLinkDepUp3GenPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5460084214466580266L;
		public static final SameLinkDepUp3GenPosNoContextMinThird inst = new SameLinkDepUp3GenPosNoContextMinThird();
		public SameLinkDepUp3GenPosNoContextMinThird() {super(TreeoutDepUp3GenPosNoContext.class, "DepUp3GenPosNoContext");}
	}
	
	public static class SameLinkDepUp3SpecPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 107613049756738157L;
		public static final SameLinkDepUp3SpecPosNoContextMinThird inst = new SameLinkDepUp3SpecPosNoContextMinThird();
		public SameLinkDepUp3SpecPosNoContextMinThird() {super(TreeoutDepUp3SpecPosNoContext.class, "DepUp3SpecPosNoContext");}
	}
	
	public static class SameLinkDepPrepUp3NoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5971166204283653853L;
		public static final SameLinkDepPrepUp3NoContextMinThird inst = new SameLinkDepPrepUp3NoContextMinThird();
		public SameLinkDepPrepUp3NoContextMinThird() {super(TreeoutDepPrepUp3NoContext.class, "DepPrepUp3NoContext");}
	}
	
	public static class SameLinkDepPrepUp3GenPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9169399515395585578L;
		public static final SameLinkDepPrepUp3GenPosNoContextMinThird inst = new SameLinkDepPrepUp3GenPosNoContextMinThird();
		public SameLinkDepPrepUp3GenPosNoContextMinThird() {super(TreeoutDepPrepUp3GenPosNoContext.class, "DepPrepUp3GenPosNoContext");}
	}
	
	public static class SameLinkDepPrepUp3SpecPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3635715031143454262L;
		public static final SameLinkDepPrepUp3SpecPosNoContextMinThird inst = new SameLinkDepPrepUp3SpecPosNoContextMinThird();
		public SameLinkDepPrepUp3SpecPosNoContextMinThird() {super(TreeoutDepPrepUp3SpecPosNoContext.class, "DepPrepUp3SpecPosNoContext");}
	}

	
	public static class SameLinkDepFlatUp3NoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8153408028185185083L;
		public static final SameLinkDepFlatUp3NoContextMinThird inst = new SameLinkDepFlatUp3NoContextMinThird();
		public SameLinkDepFlatUp3NoContextMinThird() {super(TreeoutDepFlatUp3NoContext.class, "DepFlatUp3NoContext");}
	}
	
	public static class SameLinkDepFlatUp3GenPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1157566996803309155L;
		public static final SameLinkDepFlatUp3GenPosNoContextMinThird inst = new SameLinkDepFlatUp3GenPosNoContextMinThird();
		public SameLinkDepFlatUp3GenPosNoContextMinThird() {super(TreeoutDepFlatUp3GenPosNoContext.class, "DepFlatUp3GenPosNoContext");}
	}
	
	public static class SameLinkDepFlatUp3SpecPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5479513762334676979L;
		public static final SameLinkDepFlatUp3SpecPosNoContextMinThird inst = new SameLinkDepFlatUp3SpecPosNoContextMinThird();
		public SameLinkDepFlatUp3SpecPosNoContextMinThird() {super(TreeoutDepFlatUp3SpecPosNoContext.class, "DepFlatUp3SpecPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp3NoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7904708310604711179L;
		public static final SameLinkDepFlatPrepUp3NoContextMinThird inst = new SameLinkDepFlatPrepUp3NoContextMinThird();
		public SameLinkDepFlatPrepUp3NoContextMinThird() {super(TreeoutDepFlatPrepUp3NoContext.class, "DepFlatPrepUp3NoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp3GenPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6755335242958520865L;
		public static final SameLinkDepFlatPrepUp3GenPosNoContextMinThird inst = new SameLinkDepFlatPrepUp3GenPosNoContextMinThird();
		public SameLinkDepFlatPrepUp3GenPosNoContextMinThird() {super(TreeoutDepFlatPrepUp3GenPosNoContext.class, "DepFlatPrepUp3GenPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp3SpecPosNoContextMinThird extends SameLinkOverTreeoutMinimumThird {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3369073084199048450L;
		public static final SameLinkDepFlatPrepUp3SpecPosNoContextMinThird inst = new SameLinkDepFlatPrepUp3SpecPosNoContextMinThird();
		public SameLinkDepFlatPrepUp3SpecPosNoContextMinThird() {super(TreeoutDepFlatPrepUp3SpecPosNoContext.class, "DepFlatPrepUp3SpecPosNoContext");}
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

	
	
	
	public static class SameLinkDepUp2NoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1784049437370482980L;
		public static final SameLinkDepUp2NoContextMinFifth inst = new SameLinkDepUp2NoContextMinFifth();
		public SameLinkDepUp2NoContextMinFifth() {super(TreeoutDepUp2NoContext.class, "DepUp2NoContext");}
	}
	
	public static class SameLinkDepUp2GenPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2281961043210510635L;
		public static final SameLinkDepUp2GenPosNoContextMinFifth inst = new SameLinkDepUp2GenPosNoContextMinFifth();
		public SameLinkDepUp2GenPosNoContextMinFifth() {super(TreeoutDepUp2GenPosNoContext.class, "DepUp2GenPosNoContext");}
	}
	
	public static class SameLinkDepUp2SpecPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8313058592573200157L;
		public static final SameLinkDepUp2SpecPosNoContextMinFifth inst = new SameLinkDepUp2SpecPosNoContextMinFifth();
		public SameLinkDepUp2SpecPosNoContextMinFifth() {super(TreeoutDepUp2SpecPosNoContext.class, "DepUp2SpecPosNoContext");}
	}
	
	public static class SameLinkDepPrepUp2NoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5002999157175634479L;
		public static final SameLinkDepPrepUp2NoContextMinFifth inst = new SameLinkDepPrepUp2NoContextMinFifth();
		public SameLinkDepPrepUp2NoContextMinFifth() {super(TreeoutDepPrepUp2NoContext.class, "DepPrepUp2NoContext");}
	}
	
	public static class SameLinkDepPrepUp2GenPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3320187499399578358L;
		public static final SameLinkDepPrepUp2GenPosNoContextMinFifth inst = new SameLinkDepPrepUp2GenPosNoContextMinFifth();
		public SameLinkDepPrepUp2GenPosNoContextMinFifth() {super(TreeoutDepPrepUp2GenPosNoContext.class, "DepPrepUp2GenPosNoContext");}
	}
	
	public static class SameLinkDepPrepUp2SpecPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6779491753744034045L;
		public static final SameLinkDepPrepUp2SpecPosNoContextMinFifth inst = new SameLinkDepPrepUp2SpecPosNoContextMinFifth();
		public SameLinkDepPrepUp2SpecPosNoContextMinFifth() {super(TreeoutDepPrepUp2SpecPosNoContext.class, "DepPrepUp2SpecPosNoContext");}
	}


	public static class SameLinkDepFlatUp2NoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3761292120676305000L;
		public static final SameLinkDepFlatUp2NoContextMinFifth inst = new SameLinkDepFlatUp2NoContextMinFifth();
		public SameLinkDepFlatUp2NoContextMinFifth() {super(TreeoutDepFlatUp2NoContext.class, "DepFlatUp2NoContext");}
	}
	
	public static class SameLinkDepFlatUp2GenPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4350278287728137547L;
		public static final SameLinkDepFlatUp2GenPosNoContextMinFifth inst = new SameLinkDepFlatUp2GenPosNoContextMinFifth();
		public SameLinkDepFlatUp2GenPosNoContextMinFifth() {super(TreeoutDepFlatUp2GenPosNoContext.class, "DepFlatUp2GenPosNoContext");}
	}
	
	public static class SameLinkDepFlatUp2SpecPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8033732450393410243L;
		public static final SameLinkDepFlatUp2SpecPosNoContextMinFifth inst = new SameLinkDepFlatUp2SpecPosNoContextMinFifth();
		public SameLinkDepFlatUp2SpecPosNoContextMinFifth() {super(TreeoutDepFlatUp2SpecPosNoContext.class, "DepFlatUp2SpecPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp2NoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6039182248562281377L;
		public static final SameLinkDepFlatPrepUp2NoContextMinFifth inst = new SameLinkDepFlatPrepUp2NoContextMinFifth();
		public SameLinkDepFlatPrepUp2NoContextMinFifth() {super(TreeoutDepFlatPrepUp2NoContext.class, "DepFlatPreUp2pNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp2GenPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -768204215014854958L;
		public static final SameLinkDepFlatPrepUp2GenPosNoContextMinFifth inst = new SameLinkDepFlatPrepUp2GenPosNoContextMinFifth();
		public SameLinkDepFlatPrepUp2GenPosNoContextMinFifth() {super(TreeoutDepFlatPrepUp2GenPosNoContext.class, "DepFlatPrepUp2GenPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp2SpecPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7532500799506654634L;
		public static final SameLinkDepFlatPrepUp2SpecPosNoContextMinFifth inst = new SameLinkDepFlatPrepUp2SpecPosNoContextMinFifth();
		public SameLinkDepFlatPrepUp2SpecPosNoContextMinFifth() {super(TreeoutDepFlatPrepUp2SpecPosNoContext.class, "DepFlatPrepUp2SpecPosNoContext");}
	}

	
	
	
	public static class SameLinkDepUp3NoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1274046625784980424L;
		public static final SameLinkDepUp3NoContextMinFifth inst = new SameLinkDepUp3NoContextMinFifth();
		public SameLinkDepUp3NoContextMinFifth() {super(TreeoutDepUp3NoContext.class, "DepUp3NoContext");}
	}
	
	public static class SameLinkDepUp3GenPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -697694676620275096L;
		public static final SameLinkDepUp3GenPosNoContextMinFifth inst = new SameLinkDepUp3GenPosNoContextMinFifth();
		public SameLinkDepUp3GenPosNoContextMinFifth() {super(TreeoutDepUp3GenPosNoContext.class, "DepUp3GenPosNoContext");}
	}
	
	public static class SameLinkDepUp3SpecPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5619408949096481013L;
		public static final SameLinkDepUp3SpecPosNoContextMinFifth inst = new SameLinkDepUp3SpecPosNoContextMinFifth();
		public SameLinkDepUp3SpecPosNoContextMinFifth() {super(TreeoutDepUp3SpecPosNoContext.class, "DepUp3SpecPosNoContext");}
	}
	
	public static class SameLinkDepPrepUp3NoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -630953486014812536L;
		public static final SameLinkDepPrepUp3NoContextMinFifth inst = new SameLinkDepPrepUp3NoContextMinFifth();
		public SameLinkDepPrepUp3NoContextMinFifth() {super(TreeoutDepPrepUp3NoContext.class, "DepPrepUp3NoContext");}
	}
	
	public static class SameLinkDepPrepUp3GenPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7477897868925008899L;
		public static final SameLinkDepPrepUp3GenPosNoContextMinFifth inst = new SameLinkDepPrepUp3GenPosNoContextMinFifth();
		public SameLinkDepPrepUp3GenPosNoContextMinFifth() {super(TreeoutDepPrepUp3GenPosNoContext.class, "DepPrepUp3GenPosNoContext");}
	}
	
	public static class SameLinkDepPrepUp3SpecPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2464826961488213127L;
		public static final SameLinkDepPrepUp3SpecPosNoContextMinFifth inst = new SameLinkDepPrepUp3SpecPosNoContextMinFifth();
		public SameLinkDepPrepUp3SpecPosNoContextMinFifth() {super(TreeoutDepPrepUp3SpecPosNoContext.class, "DepPrepUp3SpecPosNoContext");}
	}


	public static class SameLinkDepFlatUp3NoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1310978990785525406L;
		public static final SameLinkDepFlatUp3NoContextMinFifth inst = new SameLinkDepFlatUp3NoContextMinFifth();
		public SameLinkDepFlatUp3NoContextMinFifth() {super(TreeoutDepFlatUp3NoContext.class, "DepFlatUp3NoContext");}
	}
	
	public static class SameLinkDepFlatUp3GenPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2240503492491430303L;
		public static final SameLinkDepFlatUp3GenPosNoContextMinFifth inst = new SameLinkDepFlatUp3GenPosNoContextMinFifth();
		public SameLinkDepFlatUp3GenPosNoContextMinFifth() {super(TreeoutDepFlatUp3GenPosNoContext.class, "DepFlatUp3GenPosNoContext");}
	}
	
	public static class SameLinkDepFlatUp3SpecPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -373152337569049852L;
		public static final SameLinkDepFlatUp3SpecPosNoContextMinFifth inst = new SameLinkDepFlatUp3SpecPosNoContextMinFifth();
		public SameLinkDepFlatUp3SpecPosNoContextMinFifth() {super(TreeoutDepFlatUp3SpecPosNoContext.class, "DepFlatUp3SpecPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp3NoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2649219893494858609L;
		public static final SameLinkDepFlatPrepUp3NoContextMinFifth inst = new SameLinkDepFlatPrepUp3NoContextMinFifth();
		public SameLinkDepFlatPrepUp3NoContextMinFifth() {super(TreeoutDepFlatPrepUp3NoContext.class, "DepFlatPreUp3pNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp3GenPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2435628736691225076L;
		public static final SameLinkDepFlatPrepUp3GenPosNoContextMinFifth inst = new SameLinkDepFlatPrepUp3GenPosNoContextMinFifth();
		public SameLinkDepFlatPrepUp3GenPosNoContextMinFifth() {super(TreeoutDepFlatPrepUp3GenPosNoContext.class, "DepFlatPrepUp3GenPosNoContext");}
	}
	
	public static class SameLinkDepFlatPrepUp3SpecPosNoContextMinFifth extends SameLinkOverTreeoutMinimumFifth {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7904453488065083968L;
		public static final SameLinkDepFlatPrepUp3SpecPosNoContextMinFifth inst = new SameLinkDepFlatPrepUp3SpecPosNoContextMinFifth();
		public SameLinkDepFlatPrepUp3SpecPosNoContextMinFifth() {super(TreeoutDepFlatPrepUp3SpecPosNoContext.class, "DepFlatPrepUp3SpecPosNoContext");}
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
				//e.printStackTrace(System.err);
				//System.err.printf("#############################################\n");
				err = e.getMessage();
			}
				
			Map<String, String> result = Maps.newHashMap();
			
			result.put("WordDepNoContext",			err!=null?err: TreeToLineString.getStringWordRelCanonicalPos(subroots, false, true));

			result.put("DepNoContext",			err!=null?err: TreeToLineString.getStringRel(subroots, false, true));
			result.put("DepGenPosNoContext",	err!=null?err: TreeToLineString.getStringRelCanonicalPos(subroots, false, true));
			result.put("DepSpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelPos(subroots, false, true));
			result.put("DepFlatNoContext",			err!=null?err: TreeToLineString.getStringRelFlat(subroots, false, true));
			result.put("DepFlatGenPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatCanonicalPos(subroots, false, true));
			result.put("DepFlatSpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatPos(subroots, false, true));

			result.put("DepPrepNoContext",			err!=null?err: TreeToLineString.getStringRelPrep(subrootsNoConj, false, true));
			result.put("DepPrepGenPosNoContext",	err!=null?err: TreeToLineString.getStringRelPrepCanonicalPos(subrootsNoConj, false, true));
			result.put("DepPrepSpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelPrepPos(subrootsNoConj, false, true));
			result.put("DepFlatPrepNoContext",			err!=null?err: TreeToLineString.getStringRelFlatPrep(subrootsNoConj, false, true));
			result.put("DepFlatPrepGenPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatPrepCanonicalPos(subrootsNoConj, false, true));
			result.put("DepFlatPrepSpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatPrepPos(subrootsNoConj, false, true));

			
			
			result.put("DepUp2NoContext",			err!=null?err: TreeToLineString.getStringRelUp2(subroots, false, true));
			result.put("DepUp2GenPosNoContext",	err!=null?err: TreeToLineString.getStringRelUp2CanonicalPos(subroots, false, true));
			result.put("DepUp2SpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelUp2Pos(subroots, false, true));
			result.put("DepFlatUp2NoContext",			err!=null?err: TreeToLineString.getStringRelFlatUp2(subroots, false, true));
			result.put("DepFlatUp2GenPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatUp2CanonicalPos(subroots, false, true));
			result.put("DepFlatUp2SpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatUp2Pos(subroots, false, true));

			result.put("DepPrepUp2NoContext",			err!=null?err: TreeToLineString.getStringRelPrepUp2(subrootsNoConj, false, true));
			result.put("DepPrepUp2GenPosNoContext",	err!=null?err: TreeToLineString.getStringRelPrepUp2CanonicalPos(subrootsNoConj, false, true));
			result.put("DepPrepUp2SpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelPrepUp2Pos(subrootsNoConj, false, true));
			result.put("DepFlatPrepUp2NoContext",			err!=null?err: TreeToLineString.getStringRelFlatPrepUp2(subrootsNoConj, false, true));
			result.put("DepFlatPrepUp2GenPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatPrepUp2CanonicalPos(subrootsNoConj, false, true));
			result.put("DepFlatPrepUp2SpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatPrepUp2Pos(subrootsNoConj, false, true));

			
			
			result.put("DepUp3NoContext",			err!=null?err: TreeToLineString.getStringRelUp3(subroots, false, true));
			result.put("DepUp3GenPosNoContext",	err!=null?err: TreeToLineString.getStringRelUp3CanonicalPos(subroots, false, true));
			result.put("DepUp3SpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelUp3Pos(subroots, false, true));
			result.put("DepFlatUp3NoContext",			err!=null?err: TreeToLineString.getStringRelFlatUp3(subroots, false, true));
			result.put("DepFlatUp3GenPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatUp3CanonicalPos(subroots, false, true));
			result.put("DepFlatUp3SpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatUp3Pos(subroots, false, true));

			result.put("DepPrepUp3NoContext",			err!=null?err: TreeToLineString.getStringRelPrepUp3(subrootsNoConj, false, true));
			result.put("DepPrepUp3GenPosNoContext",	err!=null?err: TreeToLineString.getStringRelPrepUp3CanonicalPos(subrootsNoConj, false, true));
			result.put("DepPrepUp3SpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelPrepUp3Pos(subrootsNoConj, false, true));
			result.put("DepFlatPrepUp3NoContext",			err!=null?err: TreeToLineString.getStringRelFlatPrepUp3(subrootsNoConj, false, true));
			result.put("DepFlatPrepUp3GenPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatPrepUp3CanonicalPos(subrootsNoConj, false, true));
			result.put("DepFlatPrepUp3SpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelFlatPrepUp3Pos(subrootsNoConj, false, true));

			
			
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
