package edu.cuny.qc.scorer.mechanism;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.tcas.Annotation;

import ac.biu.nlp.nlp.ace_uima.AceAbnormalMessage;
import ac.biu.nlp.nlp.ace_uima.AceException;
import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Treeout;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepGenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepGenPosWithContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepSpecPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepSpecPosWithContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepWithContext;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.graph.GraphEdge;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.scorer.Aggregator;
import edu.cuny.qc.scorer.ArgumentInUsageSampleScorer;
import edu.cuny.qc.scorer.PredicateSeedScorer;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.util.TokenAnnotations;
import edu.cuny.qc.util.Utils;
import edu.cuny.qc.util.fragment.FragmentAndReference;
import edu.cuny.qc.util.fragment.FragmentLayer;
import edu.cuny.qc.util.fragment.FragmentLayerException;
import edu.cuny.qc.util.fragment.TreeFragmentBuilder.TreeFragmentBuilderException;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeToLineString;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public class DependencySignalMechanism extends SignalMechanism {

	static {
		System.err.println("??? DependencySignalMechanism: Here we don't use the heuristics in ArgumentExampleScorer's getHeadToken(). I assume that the fragment stuff take care of getting the true dep head out of the entire arg mention head, but I'm not sufre... May I should check :)");
	}
	
	public DependencySignalMechanism(Controller controller) throws SignalMechanismException {
		super(controller);
	}

	@Override
	public void addScorers() throws UnsupportedPosTagStringException {
		switch(controller.featureProfile) {
		case TOKEN_BASELINE: break;
		case ANALYSIS: //fall-through, analyze exactly all normal scorers 
		case NORMAL:
			//addTrigger(new ScorerData(null, new Or(new OneDepUp("pobj"), new OneDepUp("dobj"), new OneDepUp("nsubj")), true));

			addArgumentDependent(new ScorerData("DP_DEP_NOCON",			SameLinkDepNoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_GENPOS_NOCON",	SameLinkDepGenPosNoContext.inst,	Aggregator.Any.inst		));
//			addArgumentDependent(new ScorerData("DP_DEP_SPECPOS_NOCON",	SameLinkDepSpecPosNoContext.inst,	Aggregator.Any.inst		));
//			addArgumentDependent(new ScorerData("DP_DEP_CON",			SameLinkDepWithContext.inst,		Aggregator.Any.inst		));
//			addArgumentDependent(new ScorerData("DP_DEP_GENPOS_CON",	SameLinkDepGenPosWithContext.inst,	Aggregator.Any.inst		));
//			addArgumentDependent(new ScorerData("DP_DEP_SPECPOS_CON",	SameLinkDepSpecPosWithContext.inst,	Aggregator.Any.inst		));
			
			break;
		default:
			throw new IllegalStateException("Bad FeatureProfile enum value: " + controller.featureProfile);
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
				Treeout specTreeoutAnno = UimaUtils.selectCoveredSingle(specClass, specAius);
				specTreeout = specTreeoutAnno.getValue();

				// DEBUG
				//System.out.printf("%s       SameLinkOverTreeout(%s).calc: Starting textTriggerToken=%s, textArgHeadAnno=%s, specAius=%s, specTreeout=%s\n", Utils.detailedLog(), this.getClass().getSimpleName(), UimaUtils.annotationToString(textTriggerToken), UimaUtils.annotationToString(textArgHeadAnno), specAius.getCoveredText(), specTreeout);
				///
				Map<String, String> outsMap = cacheTextTreeouts.get(new SimpleEntry<Annotation, Annotation>(textTriggerToken, textArgHeadAnno));
				textTreeout = outsMap.get(textOutsMapKey);
				
				boolean result = textTreeout.equals(specTreeout);
				// DEBUG
				//System.out.printf("%s       SameLinkOverTreeout(%s).calc: ***Finishing textTriggerToken=%s, textArgHeadAnno=%s, specAius=%s, specTreeout=%s\n", Utils.detailedLog(), this.getClass().getSimpleName(), UimaUtils.annotationToString(textTriggerToken), UimaUtils.annotationToString(textArgHeadAnno), specAius.getCoveredText(), specTreeout);
				///
				return result;
			} catch (ExecutionException e) {
				throw new SignalMechanismException(e);
			} catch (CASException e) {
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
	
	public static class SameLinkDepWithContext extends SameLinkOverTreeout {
		private static final long serialVersionUID = 1694575499404281966L;
		public static final SameLinkDepWithContext inst = new SameLinkDepWithContext();
		public SameLinkDepWithContext() {super(TreeoutDepWithContext.class, "DepWithContext");}
	}
	
	public static class SameLinkDepGenPosWithContext extends SameLinkOverTreeout {
		private static final long serialVersionUID = 1235776745686238698L;
		public static final SameLinkDepGenPosWithContext inst = new SameLinkDepGenPosWithContext();
		public SameLinkDepGenPosWithContext() {super(TreeoutDepGenPosWithContext.class, "DepGenPosWithContext");}
	}
	
	public static class SameLinkDepSpecPosWithContext extends SameLinkOverTreeout {
		private static final long serialVersionUID = 5285506462168848680L;
		public static final SameLinkDepSpecPosWithContext inst = new SameLinkDepSpecPosWithContext();
		public SameLinkDepSpecPosWithContext() {super(TreeoutDepSpecPosWithContext.class, "DepSpecPosWithContext");}
	}
	
//	public static class TreeoutsContainer {
//		public String depNoContext, depGenPosNoContext, depSpecPosNoContext, depWithContext, depGenPosWithContext, depSpecPosWithContext;
//	}

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
	private static /*transient*/ LoadingCache<Entry<? extends Annotation, ? extends Annotation>, Map<String, String>> cacheTextTreeouts = CacheBuilder.newBuilder()
	.maximumSize(100000)
	.build(new CacheLoader<Entry<? extends Annotation, ? extends Annotation>, Map<String, String>>() {
		public Map<String, String> load(Entry<? extends Annotation, ? extends Annotation> textAnnos) throws FragmentLayerException, CASException, AceException, TreeAndParentMapException, TreeFragmentBuilderException {
			try {
				Annotation textTrigger = textAnnos.getKey();
				Annotation textArg = textAnnos.getValue();
				
				FragmentAndReference linkFrag = textFragmentLayer.getRootLinkingTreeFragment(textTrigger, textArg, null);
				List<BasicNode> subroots = ImmutableList.of(linkFrag.getFragmentRoot());
				
				Map<String, String> result = Maps.newHashMap();
				result.put("DepNoContext",			TreeToLineString.getStringRel(subroots, false));
				result.put("DepGenPosNoContext",	TreeToLineString.getStringRelCanonicalPos(subroots, false));
				result.put("DepSpecPosNoContext",	TreeToLineString.getStringRelPos(subroots, false));
				result.put("DepWithContext",		TreeToLineString.getStringRel(subroots, true));
				result.put("DepGenPosWithContext",	TreeToLineString.getStringRelCanonicalPos(subroots, true));
				result.put("DepSpecPosWithContext",	TreeToLineString.getStringRelPos(subroots, true));

				return result;
			} catch (AceAbnormalMessage e) {
				throw new FragmentLayerException(e);
			}
		}
	});
}
