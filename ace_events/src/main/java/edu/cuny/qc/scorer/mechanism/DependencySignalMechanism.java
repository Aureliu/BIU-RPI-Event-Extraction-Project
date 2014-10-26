package edu.cuny.qc.scorer.mechanism;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.tcas.Annotation;

import ac.biu.nlp.nlp.ace_uima.AceAbnormalMessage;
import ac.biu.nlp.nlp.ace_uima.AceException;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.Treeout;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepGenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepGenPosWithContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepGenPosNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepNoContext;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.TreeoutDepPrepSpecPosNoContext;
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
			addArgumentDependent(new ScorerData("DP_DEP_NOCON",			SameLinkDepNoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_GENPOS_NOCON",	SameLinkDepGenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_SPECPOS_NOCON",	SameLinkDepSpecPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_NOCON",			SameLinkDepPrepNoContext.inst,			Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_GENPOS_NOCON",		SameLinkDepPrepGenPosNoContext.inst,	Aggregator.Any.inst		));
			addArgumentDependent(new ScorerData("DP_DEP_PREP_SPECPOS_NOCON",	SameLinkDepPrepSpecPosNoContext.inst,	Aggregator.Any.inst		));
			
//			addArgumentDependent(new ScorerData("DP_DEP_CON",			SameLinkDepWithContext.inst,		Aggregator.Any.inst		));
//			addArgumentDependent(new ScorerData("DP_DEP_GENPOS_CON",	SameLinkDepGenPosWithContext.inst,	Aggregator.Any.inst		));
//			addArgumentDependent(new ScorerData("DP_DEP_SPECPOS_CON",	SameLinkDepSpecPosWithContext.inst,	Aggregator.Any.inst		));
			break;
		case NORMAL:
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
				Map<String, String> outsMap = cacheTextTreeouts.get(new TriggerArgQuery(textTriggerToken, textArgHeadAnno));
				textTreeout = outsMap.get(textOutsMapKey);
				
				boolean result = textTreeout.equals(specTreeout);
				// DEBUG
				//System.out.printf("%s       SameLinkOverTreeout(%s).calc: ***Finishing textTriggerToken=%s, textArgHeadAnno=%s, specAius=%s, specTreeout=%s\n", Utils.detailedLog(), this.getClass().getSimpleName(), UimaUtils.annotationToString(textTriggerToken), UimaUtils.annotationToString(textArgHeadAnno), specAius.getCoveredText(), specTreeout);
				///
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
	.maximumSize(100000)
	.build(new CacheLoader<SpecTreeoutQuery, String>() {
		public String load(SpecTreeoutQuery query) throws CASException {
			Treeout specTreeoutAnno = UimaUtils.selectCoveredSingle(query.specClass, query.specAius);
			String result = specTreeoutAnno.getValue();
			return result;
		}
	});
	
	private static /*transient*/ LoadingCache<TriggerArgQuery, Map<String, String>> cacheTextTreeouts = CacheBuilder.newBuilder()
	.maximumSize(100000)
	.build(new CacheLoader<TriggerArgQuery, Map<String, String>>() {
		public Map<String, String> load(TriggerArgQuery query) throws FragmentLayerException, CASException, AceException, TreeAndParentMapException, TreeFragmentBuilderException {
//			Annotation textTrigger = textAnnos.getKey();
//			Annotation textArg = textAnnos.getValue();
			
			List<BasicNode> subroots = null;
			List<BasicNode> subrootsNoConj = null;
			String err = null;
			try {
				FragmentAndReference linkFrag = textFragmentLayer.getRootLinkingTreeFragment(query.textTriggerToken, query.textArgHeadAnno, false, null);
				subroots = ImmutableList.of(linkFrag.getFragmentRoot());
				FragmentAndReference linkFragNoConj = textFragmentLayer.getRootLinkingTreeFragment(query.textTriggerToken, query.textArgHeadAnno, true, null);
				subrootsNoConj = ImmutableList.of(linkFragNoConj.getFragmentRoot());
			} catch (AceAbnormalMessage e) {
				System.err.printf("DependencySignalMechanism: Got AceAbnormalError while calcing link: %s\n", e.getMessage());
				e.printStackTrace(System.err);
				System.err.printf("#############################################\n");
				err = e.getMessage();
			} catch (Exception e) {
				System.err.printf("DependencySignalMechanism: Got Exception while calcing link: %s\n", e);
				e.printStackTrace(System.err);
				System.err.printf("#############################################\n");
				err = e.getMessage();
			}
				
			Map<String, String> result = Maps.newHashMap();
			result.put("DepNoContext",			err!=null?err: TreeToLineString.getStringRel(subroots, false, true));
			result.put("DepGenPosNoContext",	err!=null?err: TreeToLineString.getStringRelCanonicalPos(subroots, false, true));
			result.put("DepSpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelPos(subroots, false, true));
//			result.put("DepWithContext",		err!=null?err: TreeToLineString.getStringRel(subroots, true, true));
//			result.put("DepGenPosWithContext",	err!=null?err: TreeToLineString.getStringRelCanonicalPos(subroots, true, true));
//			result.put("DepSpecPosWithContext",	err!=null?err: TreeToLineString.getStringRelPos(subroots, true, true));

			result.put("DepPrepNoContext",			err!=null?err: TreeToLineString.getStringRelPrep(subrootsNoConj, false, true));
			result.put("DepPrepGenPosNoContext",	err!=null?err: TreeToLineString.getStringRelPrepCanonicalPos(subrootsNoConj, false, true));
			result.put("DepPrepSpecPosNoContext",	err!=null?err: TreeToLineString.getStringRelPrepPos(subrootsNoConj, false, true));
//			result.put("DepPrepWithContext",		err!=null?err: TreeToLineString.getStringRelPrep(subrootsNoConj, true, true));
//			result.put("DepPrepGenPosWithContext",	err!=null?err: TreeToLineString.getStringRelPrepCanonicalPos(subrootsNoConj, true, true));
//			result.put("DepPrepSpecPosWithContext",	err!=null?err: TreeToLineString.getStringRelPrepPos(subrootsNoConj, true, true));

			return result;
		}
	});
}
