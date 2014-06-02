package ac.biu.nlp.nlp.ace_uima.analyze;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.BIU.NLP.corpora.ACE.training_set.jaxb.LdcScope;
import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.uimafit.util.JCasUtil;
import org.xml.sax.SAXException;

import ac.biu.nlp.nlp.ace_uima.AceAbnormalMessage;
import ac.biu.nlp.nlp.ace_uima.AceException;
import ac.biu.nlp.nlp.ace_uima.analyze.TreeFragmentBuilder.TreeFragmentBuilderException;
import ac.biu.nlp.nlp.ace_uima.stats.StatsDocument;
import ac.biu.nlp.nlp.ace_uima.stats.StatsDocumentCollection;
import ac.biu.nlp.nlp.ace_uima.stats.StatsException;
import ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention;
import ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMentionExtent;
import ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMentionHead;
import ac.biu.nlp.nlp.ace_uima.uima.Event;
import ac.biu.nlp.nlp.ace_uima.uima.EventArgument;
import ac.biu.nlp.nlp.ace_uima.uima.EventMention;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionAnchor;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionExtent;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionLdcScope;
import ac.biu.nlp.nlp.ace_uima.uima.Timex2Mention;
import ac.biu.nlp.nlp.ace_uima.uima.ValueMention;
import ac.biu.nlp.nlp.ace_uima.utils.AnotherBasicNodeUtils;
import ac.biu.nlp.nlp.ace_uima.utils.FinalHashMap;
import ac.biu.nlp.nlp.ace_uima.utils.IgnoreNullFinalHashMap;
import ac.biu.nlp.nlp.ace_uima.utils.UimaUtils;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.datastructures.OneToManyBidiMultiHashMap;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.representation.pasta.ClausalArgument;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.common.representation.pasta.TypedArgument;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.file.FileFilters;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.common.utilities.log4j.LoggerUtilities;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.en.pasta.PredicateArgumentStructureBuilderFactory;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.Nominalization;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.NomlexException;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.NomlexMapBuilder;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentStructureBuilder;
import eu.excitementproject.eop.lap.biu.uima.CasTreeConverter;
import eu.excitementproject.eop.lap.biu.uima.CasTreeConverterException;

/**
 * Performs various statistics (using the stats mechanism) on the ACE XMIs, and outputs
 * various reports.
 * 
 * @author Ofer Bronstein
 * @since July 2013
 */
public class AceAnalyzer {

	protected void analyzeOneJcas(JCas jcas) throws StatsException, CASException, CasTreeConverterException, UnsupportedPosTagStringException, AceException, TreeAndParentMapException, TreeFragmentBuilderException, PredicateArgumentIdentificationException {
		DocumentMetaData meta = JCasUtil.selectSingle(jcas, DocumentMetaData.class);
		String folder = meta.getCollectionId();
		String docId = meta.getDocumentId();
		String category = getCategory(docId);
		logger.debug("starting file " + docId);
				
		/// DEBUG
		if (docId.contains("AFP_ENG_20030311.0491")) {
			System.out.println("Got it!");
		}
		////
		tokenIndex = JCasUtil.indexCovering(jcas, Token.class, Sentence.class);
		
		// get BIU trees equivalent representation
		converter = new CasTreeConverter();
		converter.convertCasToTrees(jcas);
		token2nodes = converter.getAllTokensToNodes();
		sentence2root = converter.getSentenceToRootMap();
		//logger.trace("--- token2nodes: " + AnotherBasicNodeUtils.getNodesAnnotationString(token2nodes));
		//logger.trace("--- sentence2root: " + AnotherBasicNodeUtils.getNodesAnnotationString(sentence2root));
		
		//TODO - make sure all nodes of the same token also have the same info (now we are just taking the first)
//		info2token = new DualHashBidiMap<Info, Token>();
//		for (Entry<Token, Collection<BasicNode>> entry : token2nodes.entrySet()) {
//			info2token.put(entry.getValue().iterator().next().getInfo(), entry.getKey());
//		}
		logger.debug("- converted to trees");
		
		//Run PASTA to analyze each sentence
		if (AceAnalyzerDocumentCollection.USE_PASTA) {
			predicateToPas = new /*zzz*/IgnoreNullFinalHashMap<BasicNode, PredicateArgumentStructure<Info, BasicNode>>(); //TODO these maps should final!!! we're missing stuff!
			argToPas = new SimpleValueSetMap<BasicNode, PredicateArgumentStructure<Info, BasicNode>>();
			//clauseArgToPas = new /*zzz*/IgnoreNullFinalHashMap<BasicNode, PredicateArgumentStructure<Info, BasicNode>>();
			for (BasicNode root : sentence2root.values()) {
				TreeAndParentMap<Info, BasicNode> map = new TreeAndParentMap<Info, BasicNode>(root);
				PredicateArgumentStructureBuilder<Info, BasicNode> pastaBuilder = pastaFactory.createBuilder(map);
				pastaBuilder.build();
				Set<PredicateArgumentStructure<Info, BasicNode>> pasSet = pastaBuilder.getPredicateArgumentStructures();
				for (PredicateArgumentStructure<Info, BasicNode> pas : pasSet) {
					
					// Decision: A predicate's relevant node is its head
					predicateToPas.put(pas.getPredicate().getHead(), pas);
					
					for (TypedArgument<Info, BasicNode> arg : pas.getArguments()) {
						
						//TODO: Decision: An argumemnt's relevant nodes are all given ones - syntactic, semantic and rep... is this correct?
						argToPas.put(arg.getArgument().getSemanticHead(), pas);
						argToPas.put(arg.getArgument().getSyntacticHead(), pas);
						argToPas.put(arg.getArgument().getSyntacticRepresentative(), pas);
					}
					for (ClausalArgument<Info, BasicNode> clauseArg : pas.getClausalArguments()) {
						//Decision: A clausal argument's relevant nodes are the "clause" and syntactic head (for dup nodes)
						argToPas.put(clauseArg.getClause(), pas);
						argToPas.put(clauseArg.getSyntacticRepresentative(), pas);
					}
	//				for (TypedArgument<Info, BasicNode> arg : pas.getArguments()) {
	//					
	//					//TODO: Decision: An argumemnt's relevant nodes are all given ones - syntactic, semantic and rep... is this correct?
	//					argToPas.put(arg.getArgument().getSemanticHead(), pas);
	//					if (arg.getArgument().getSemanticHead() != arg.getArgument().getSyntacticHead()) {
	//						argToPas.put(arg.getArgument().getSyntacticHead(), pas);
	//					}
	//					if (arg.getArgument().getSyntacticHead() != arg.getArgument().getSyntacticRepresentative()) {
	//						argToPas.put(arg.getArgument().getSyntacticRepresentative(), pas);
	//					}
	//				}
	//				for (ClausalArgument<Info, BasicNode> clauseArg : pas.getClausalArguments()) {
	//					//Decision: A clausal argument's relevant nodes are the "clause" and syntactic head (for dup nodes)
	//					clauseArgToPas.put(clauseArg.getClause(), pas);
	//					clauseArgToPas.put(clauseArg.getSyntacticRepresentative(), pas);
	//				}
				}
			}
			// Remove possible nulls
			// No need! We made it a non-null map!
	//		predicateToPas.remove(null);
	//		argToPas.remove(null);
	//		clauseArgToPas.remove(null);
			logger.debug("- ran PASTA and indexed output");
		}
		
		fragmenter = new TreeFragmentBuilder();
		linkToFacet = new LinkedHashMap<BasicNode, Facet>();
		
		Map<String,String> key = new HashMap<String,String>();
		key.put("folder", folder);
		key.put("category", category);
		key.put("docId", docId);
		//TODO these few lines are a stupid and probably bad hack, since I don't remember how I should handle
		//this situation according to the design. I don't think the blank key fields are a solution at any time.
		//15.5.14 well, I looked into it and maybe the design just doesn't support it -
		//the key must have full values, but then docs.updateDocs also dups it to various rows with some missing values
		//but since "NumSentences" and "SentenceCovered" are by definition nt linked to any specific event subtype,
		//and rather are on the document, we need to to put some "default" value for the event-related stuff
		//maybe that's the solution - physically put the ANY ("*").
		key.put("EventSubType", StatsDocument.ANY);
		key.put("ArgType", StatsDocument.ANY);
		key.put("Role", StatsDocument.ANY);
		
		//Yep, that's a good spot for that...
		System.gc();

		docs.updateDocs(key, "NumSentences", "", JCasUtil.select(jcas, Sentence.class).size());
		docs.updateDocs(key, "SentenceCovered", "", getSentenceCoveredChars(jcas));

		for (Event event : JCasUtil.select(jcas, Event.class)) {
			//TODO debug
			//long size = ObjectSizeFetcher.getObjectSize(docs);
			//logger.info("*** Size: " + size + " bytes");
			
			String subtype = event.getSUBTYPE();

			key.put("EventSubType", subtype);

			docs.updateDocs(key, "Tense", "", event.getTENSE());
			docs.updateDocs(key, "Modality", "", event.getMODALITY());
			docs.updateDocs(key, "Polarity", "", event.getPOLARITY());
			docs.updateDocs(key, "Genericity", "", event.getGENERICITY());
			docs.updateDocs(key, "Events", "", 1);
			docs.updateDocs(key, "MentionsPerEvent", "", event.getEventMentions().size());
			docs.updateDocs(key, "ArgsPerEvent", "", event.getEventArguments().size());
			
			// For type-doc output file - the (only) dynamic field!
			// no no no - this is supposed to be ENUM!!!
			//docs.updateDocs(key, subtype, "", event.getEventMentions().size(), true);
			updateDocsEnumSumField(key, "EventSubType", "", subtype, event.getEventMentions().size());

			for (EventArgument argument : JCasUtil.select(event.getEventArguments(), EventArgument.class)) {
				docs.updateDocs(key, "Roles", "", argument.getRole());
				//logger.info("***** Size: " + ObjectSizeFetcher.getObjectSize(docs) + " bytes");
			}
				
			for (EventMention mention : JCasUtil.select(event.getEventMentions(), EventMention.class)) {
				docs.updateDocs(key, "ArgsPerMention", "", mention.getEventMentionArguments().size());
				
				EventMentionAnchor eventAnchor = mention.getAnchor();
				if (selectCoveredByIndex(jcas, Token.class, eventAnchor.getBegin(), eventAnchor.getEnd(), tokenIndex).values().isEmpty()) {
					logger.warn(String.format("Event anchor '%s'[%s:%s] not covered by Sentence",
							eventAnchor.getCoveredText(), eventAnchor.getBegin(), eventAnchor.getEnd()));
				}
				else {				
					docs.updateDocs(key, "Anchor", "", getText(eventAnchor));
					docs.updateDocs(key, "Anchor", "Lemmas", getLemmas(eventAnchor));						
					docs.updateDocs(key, "Anchor", "Tokens", getNumTokens(eventAnchor));						
					docs.updateDocs(key, "Anchor", "Tokens2", getNumTokens(eventAnchor).toString());						
					docs.updateDocs(key, "Anchor", "SpecPOS", getSpecificPosString(eventAnchor));						
					docs.updateDocs(key, "Anchor", "GenPOS", getGeneralPosString(eventAnchor));
					
					List<BasicNode> eventAnchorFrag = getTreeFragments(eventAnchor);
					docs.updateDocs(key, "Anchor", "Dep", getParenthesesTreeOnlyDependencies(eventAnchorFrag));						
					docs.updateDocs(key, "Anchor", "DepGenPOS", getParenthesesTreeDependenciesGeneralPOS(eventAnchorFrag));						
					docs.updateDocs(key, "Anchor", "DepSpecPOS", getParenthesesTreeDependenciesSpecificPOS(eventAnchorFrag));						
	
					EventMentionExtent eventExtent = mention.getExtent();
					docs.updateDocs(key, "Extent", "", getText(eventExtent));
					docs.updateDocs(key, "Extent", "Tokens", getNumTokens(eventExtent));						
					docs.updateDocs(key, "Extent", "Sentences", getNumSentences(eventExtent, tokenIndex));						
					docs.updateDocs(key, "Extent", "SpecPOS", getSpecificPosString(eventExtent));						
					docs.updateDocs(key, "Extent", "GenPOS", getGeneralPosString(eventExtent));
					addDetails("Anchor.GenPos_" + getGeneralPosString(eventAnchor), getText(eventAnchor) + ": " + getText(eventExtent));

					EventMentionLdcScope eventLdcScope = mention.getLdcScope();
					docs.updateDocs(key, "LdcScope", "", getText(eventLdcScope));
					docs.updateDocs(key, "LdcScope", "Tokens", getNumTokens(eventLdcScope));						
					docs.updateDocs(key, "LdcScope", "Sentences", getNumSentences(eventLdcScope, tokenIndex));						
					docs.updateDocs(key, "LdcScope", "SpecPOS", getSpecificPosString(eventLdcScope));						
					docs.updateDocs(key, "LdcScope", "GenPOS", getGeneralPosString(eventLdcScope));
					//logger.info("*****@@ Size: " + ObjectSizeFetcher.getObjectSize(docs) + " bytes");
				}
				
				for (EventMentionArgument argMention : JCasUtil.select(mention.getEventMentionArguments(), EventMentionArgument.class)) {
					key.put("Role", argMention.getRole());
					key.put("ArgType", argMention.getArgMention().getArg().getType().getShortName());
					
					BasicArgumentMentionExtent argExtent = argMention.getArgMention().getExtent();
					if (selectCoveredByIndex(jcas, Token.class, argExtent.getBegin(), argExtent.getEnd(), tokenIndex).values().isEmpty()) {
						logger.warn(String.format("Event argument extent '%s'[%s:%s] not covered by Sentence",
								argExtent.getCoveredText(), argExtent.getBegin(), argExtent.getEnd()));
					}
					else {
						BasicArgumentMentionHead argHead = argMention.getArgMention().getHead();
//						if (argHead==null) {
//							System.out.println("argHead is null for mention: " + argMention.getArgMention().toString());
//						}
						if (argHead==null && !(argMention.getArgMention() instanceof Timex2Mention || argMention.getArgMention() instanceof ValueMention)) {
							throw new AceException("Got null arg head, for an arg that is not a timex2 nor a value. Arg mention: " + argMention.getArgMention());
						}
						
						docs.updateDocs(key, "ArgExtent", "", getText(argExtent));						
						docs.updateDocs(key, "ArgExtent", "Tokens", getNumTokens(argExtent));						
						docs.updateDocs(key, "ArgExtent", "Sentences", getNumSentences(argExtent, tokenIndex));						
						docs.updateDocs(key, "ArgExtent", "SpecPOS", getSpecificPosString(argExtent));						
						docs.updateDocs(key, "ArgExtent", "GenPOS", getGeneralPosString(argExtent));
						
						docs.updateDocs(key, "ArgHead", "", getText(argHead));						
						docs.updateDocs(key, "ArgHead", "Lemmas", getLemmas(argHead));						
						docs.updateDocs(key, "ArgHead", "Tokens", getNumTokens(argHead));						
						docs.updateDocs(key, "ArgHead", "Tokens2", getNumTokens(argHead).toString());						
						docs.updateDocs(key, "ArgHead", "SpecPOS", getSpecificPosString(argHead));						
						docs.updateDocs(key, "ArgHead", "GenPOS", getGeneralPosString(argHead));
						
						List<BasicNode> argHeadFrag = getTreeFragments(argHead);
						docs.updateDocs(key, "ArgHead", "Dep", getParenthesesTreeOnlyDependencies(argHeadFrag));						
						docs.updateDocs(key, "ArgHead", "DepGenPOS", getParenthesesTreeDependenciesGeneralPOS(argHeadFrag));						
						docs.updateDocs(key, "ArgHead", "DepSpecPOS", getParenthesesTreeDependenciesSpecificPOS(argHeadFrag));
						updateLinkingTreeFrags(key, eventAnchor, argHead, "Link", "Dep", "DepGenPOS", "DepSpecPOS");
						//logger.info("*****@@^^^ Size: " + ObjectSizeFetcher.getObjectSize(docs) + " bytes");
						addDetails("ArgHead.GenPos_" + getGeneralPosString(argHead), getText(argHead) + ": " + getText(argExtent));
					}
				}
			}
		}
		logger.debug("- reported ACE per-event stats");
		
		for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
			key.put("EventSubType", StatsDocument.ANY);
			key.put("ArgType", StatsDocument.ANY);
			key.put("Role", StatsDocument.ANY);
			
			List<EventMentionAnchor> eventMentionAnchors =  JCasUtil.selectCovered(EventMentionAnchor.class, sentence);
			docs.updateDocs(key, "MentionsPerSentence", "All", Integer.toString(eventMentionAnchors.size()));
			
			List<String> subtypesList = new ArrayList<String>(eventMentionAnchors.size());
			List<String> subtypesWithAnchorsList = new ArrayList<String>(eventMentionAnchors.size());
			for (EventMentionAnchor anchor : eventMentionAnchors) {
				String subtype = anchor.getEventMention().getEvent().getSUBTYPE();
				subtypesList.add(subtype);
				subtypesWithAnchorsList.add(String.format("%s/%s", subtype, getLemmas(anchor)));
			}
			Set<String> subtypesSet = new LinkedHashSet<String>(subtypesList);
			if (eventMentionAnchors.size() > 1) {
				docs.updateDocs(key, "MentionsList", "All", subtypesList.toString());
				docs.updateDocs(key, "MentionsSet", "All", subtypesSet.toString());
				docs.updateDocs(key, "MentionsAnchorsList", "All", subtypesWithAnchorsList.toString());
			}

			for (String subtype : subtypesSet) {
				key.put("EventSubType", subtype);
				docs.updateDocs(key, "MentionsPerSentence", "PerType", Integer.toString(eventMentionAnchors.size()));
				docs.updateDocs(key, "MentionsList", "PerType", subtypesList.toString());
				docs.updateDocs(key, "MentionsSet", "PerType", subtypesSet.toString());
				docs.updateDocs(key, "MentionsAnchorsList", "PerType", subtypesWithAnchorsList.toString());
				docs.updateDocs(key, "TypedMentionsPerSentence", "PerType", Integer.toString(Collections.frequency(subtypesList, subtype)));
				docs.updateDocs(key, "TypeInSentence", "PerType", 1);
				
			}
			
			/**
			 * 28.5.2014
			 * This snippet is for iterating the arg mentions in each sentence, and counting when an arg mention belongs
			 * to more than one event mention ("the man was both attacked and killed").
			 * I currently didn't use it because I realized I don't need to iterate, since each argument mention already
			 * has a list of all events it appears with (and with what role).
			 * But this snippet STILL MAY BE USEFUL, if I realize that because of Qi's bad handling of MWE
			 * (Multi-Word Expressions), the identification of "what are two occurrences of the same arg" is a bit
			 * trickier, and I have to check for overlap and such. So the snippet does that. Note also that it must be within
			 * a single sentence, as for each argument we iterate through all other arguments, which could be "the same as" the
			 * current one, and these could be only in the same sentence.
			 * Oh, and it's just python-like pseudocode :)
			 *
			for each EventMentionArguments arg1:
				bool headsDiff = true
				headEquals = 0;
				headOverlaps = 0;
				extentEquals = 0;
				extentOverlaps = 0;
				
				List headEqualsTexts = new List([arg1.head]) //must be list to allow duplicates
				List headEqualsRoles = new List([arg1.role])
				for each other EventMentionArguments arg2: //but yes, each pair will be processed twice, once from each end
					if (arg1.head != null && arg2.head != null)
					if arg1.head.equals(args2.head):
						headEquals++;
						headsDiff = false
						headEqualsTexts.append(arg2.head)
						headEqualsRoles.append(arg2.role)
					elif arg1.head.overlaps(arg2.head):
						headOverlaps++;
						headsDiff = false
						
					if arg1.extent.equals(arg2.extent):
						extentEquals++;
					
						if headsDiff:
							extentEqualsTexts.append(arg2.extent)
							extentEqualsRoles.append(arg2.role)							
					elif arg1.extent.overlaps(arg2.extent):
						extentOverlaps++;
				
				key.role = arg1.role
				update(key, "headsEqual::distribution", headEquals.toString())
				if extentEqualsTexts.size() > 1:
					update(key, "headsEqualTexts::distribution", headEqualsTexts.toString())
					update(key, "headEqualsRoles::distribution", headEqualsRoles.toString())
				...
				**/
		}
		logger.debug("- reported ACE per-sentence stats");

		for (BasicArgumentMention arg : JCasUtil.select(jcas, BasicArgumentMention.class)) {
			//key.put("EventSubType", arg1.getEventMention().getEvent().getSUBTYPE());
			//key.put("Role", arg1.getRole());
			key.put("EventSubType", StatsDocument.ANY);
			key.put("Role", StatsDocument.ANY);
			key.put("ArgType", arg.getArg().getType().getShortName());
			
			int size = arg.getEventMentionArguments().size();
			List<String> roles = new ArrayList<String>(size);
			List<String> types = new ArrayList<String>(size);
			
			// Iterating through event mentions twice - collecting data, and only afterwards recording it
			for (EventMentionArgument mention : JCasUtil.select(arg.getEventMentionArguments(), EventMentionArgument.class)) {
				roles.add(mention.getRole());
				types.add(mention.getEventMention().getEvent().getSUBTYPE());
			}
			
			docs.updateDocs(key, "EventsPerArg", "All", Integer.toString(size));
			if (size > 1) {
				docs.updateDocs(key, "EventsPerArg2", "All", size);
				docs.updateDocs(key, "EventTypesPerArg", "All", types.toString());
				docs.updateDocs(key, "RolesPerArg", "All", roles.toString());
			}
			
			for (EventMentionArgument mention : JCasUtil.select(arg.getEventMentionArguments(), EventMentionArgument.class)) {
				String type = mention.getEventMention().getEvent().getSUBTYPE();
				String role = mention.getRole();
				key.put("EventSubType", type);
				key.put("Role", role);
				
				docs.updateDocs(key, "EventsPerArg", "PerRole", Integer.toString(size));
				docs.updateDocs(key, "EventsPerArg2", "PerRole", size);
				docs.updateDocs(key, "EventTypesPerArg", "PerRole", types.toString());
				docs.updateDocs(key, "RolesPerArg", "PerRole", roles.toString());
				docs.updateDocs(key, "TypedEventTypesPerArg", "PerRole", Integer.toString(Collections.frequency(types, type)));
				docs.updateDocs(key, "TypedRolesPerArg", "PerRole", Integer.toString(Collections.frequency(roles, role)));
			}
		}
		logger.debug("- reported ACE per-argument stats");		

		if (AceAnalyzerDocumentCollection.USE_PASTA) {
			// some PASTA post-processing
			key.put("EventSubType", StatsDocument.ANY); // reset key - not relevant here
			key.put("ArgType", StatsDocument.ANY);
			key.put("Role", StatsDocument.ANY);
			Set<PredicateArgumentStructure<Info, BasicNode>> foundPases = new HashSet<PredicateArgumentStructure<Info, BasicNode>>();
			for (Entry<BasicNode, Facet> entry : linkToFacet.entrySet()) {
				List<BasicNode> link = Arrays.asList(new BasicNode[] {entry.getKey()});
				BasicNode predicate = entry.getValue().getPredicateHead();
				BasicNode argument = entry.getValue().getArgumentHead();
				
				PredicateArgumentStructure<Info, BasicNode> predicatePas = predicateToPas.get(predicate);
				if (predicatePas == null) {
					docs.updateDocs(key, "FindLinks", "", "MissedPredicate");
					docs.updateDocs(key, "MissedPredicate", "Dep", getParenthesesTreeOnlyDependencies(link));						
					docs.updateDocs(key, "MissedPredicate", "DepGenPOS", getParenthesesTreeDependenciesGeneralPOS(link));
					addDetails("MissedPredicate", printIndented(link));
					//TODO if pasta didn't find this link, check also if this link is a coreference to something that pasta did find
					continue;
				}
		
				ImmutableSet<PredicateArgumentStructure<Info, BasicNode>> argPases = argToPas.get(argument);
				if (argPases.isEmpty()) {
					docs.updateDocs(key, "FindLinks", "", "MissedArgument");
					docs.updateDocs(key, "MissedArgument", "Dep", getParenthesesTreeOnlyDependencies(link));						
					docs.updateDocs(key, "MissedArgument", "DepGenPOS", getParenthesesTreeDependenciesGeneralPOS(link));						
					addDetails("MissedArgument", printIndented(link));
					continue;
				}
				if (!argPases.contains(predicatePas)) {
					docs.updateDocs(key, "FindLinks", "", "MissedLink");
					docs.updateDocs(key, "MissedLink", "Dep", getParenthesesTreeOnlyDependencies(link));						
					docs.updateDocs(key, "MissedLink", "DepGenPOS", getParenthesesTreeDependenciesGeneralPOS(link));						
					addDetails("MissedLink", printIndented(link));
					continue;
				}
				docs.updateDocs(key, "FindLinks", "", "LinkFound");
				docs.updateDocs(key, "LinkFoundLink", "Dep", getParenthesesTreeOnlyDependencies(link));						
				docs.updateDocs(key, "LinkFoundLink", "DepGenPOS", getParenthesesTreeDependenciesGeneralPOS(link));						
				addDetails("LinkFound", printIndented(link));
				foundPases.add(predicatePas);
	
				
	//			PredicateArgumentStructure<Info, BasicNode> argPas = null;
	//			PredicateArgumentStructure<Info, BasicNode> regularArgPas = argToPas.get(argument);
	//			PredicateArgumentStructure<Info, BasicNode> clauseArgPas = clauseArgToPas.get(argument);
	//			if (regularArgPas == null) {
	//				if (clauseArgPas == null) {
	//					docs.updateDocs(key, "FindingLinks", "MissedArgument");
	//					docs.updateDocs(key, "MissedArgumentLinkDependencies", getParenthesesTreeOnlyDependencies(link));						
	//					docs.updateDocs(key, "MissedArgumentLinkDependenciesGenPOS", getParenthesesTreeDependenciesGeneralPOS(link));						
	//					continue;
	//				}
	//				else {
	//					argPas = clauseArgPas;
	//				}
	//			}
	//			else {
	//				if (clauseArgPas == null) {
	//					argPas = regularArgPas;
	//				}
	//				else {
	//					throw new AceException("Got node that is both regular argument and clausal argument: " + argument);
	//				}
	//			}
	//			
	//			if (argPas != predicatePas) {
	//				docs.updateDocs(key, "FindingLinks", "MissedPredArgLink");
	//				docs.updateDocs(key, "MissedPredArgLinkLinkDependencies", getParenthesesTreeOnlyDependencies(link));						
	//				docs.updateDocs(key, "MissedPredArgLinkLinkDependenciesGenPOS", getParenthesesTreeDependenciesGeneralPOS(link));						
	//				continue;
	//			}
	//			
	//			docs.updateDocs(key, "FindingLinks", "LinkFound");
	//			docs.updateDocs(key, "LinkFoundLinkDependencies", getParenthesesTreeOnlyDependencies(link));						
	//			docs.updateDocs(key, "LinkFoundLinkDependenciesGenPOS", getParenthesesTreeDependenciesGeneralPOS(link));						
	//			foundPases.add(predicatePas);
			}
			logger.debug("- reported most PASTA-related stats");
	
			// report PASTA's pases that are not annotated in ACE
			for (PredicateArgumentStructure<Info, BasicNode> pas : predicateToPas.values()) {
				if (!foundPases.contains(pas)) {
					docs.updateDocs(key, "FindLinks", "", "PastaOnly");
					//TODO also add printed dependency fragments of the pasta-only links
					// The difficulty is that we don't have them in fragment form - you have to dig
					// in the pas, and for every argument+clausel argument take the list of nodes which
					// is the path to the predicate and somehow restore the tree-structure to make
					// it a fragment (maybe use similar ways to how we build the current fragments)
					// TODO and also with details
				}
			}
			logger.debug("- finished reporting PASTA-related stats");
		}
	}
	
	private void updateDocsEnumSumField(Map<String, String> key, String fieldNameLvl1, String fieldNameLvl2, String enumValue, Integer amount) throws StatsException {
		for (int i=0; i<amount; i++) {
			docs.updateDocs(key, fieldNameLvl1, fieldNameLvl2, enumValue);
		}
	}

	protected void addDetails(String filename, String content) {
		if (detailedFiles.containsKey(filename)) {
			detailedFiles.get(filename).add(content);
		}
		else {
			List<String> list = new ArrayList<String>();
			detailedFiles.put(filename, list);
			list.add(content);
		}
	}

	protected String getCategory(String docId) {
		if (devFileIds.contains(docId)) {
			return "Dev";
		}
		if (trainFileIds.contains(docId)) {
			return "Train";
		}
		if (testFileIds.contains(docId)) {
			return "Test";
		}
		return "Unknown";
	}
	
	protected Double getSentenceCoveredChars(JCas jcas) {
		Collection<Sentence> sentences = JCasUtil.select(jcas, Sentence.class);
		int countSentenceChars = 0;
		for (Sentence sentence : sentences) {
			countSentenceChars += sentence.getCoveredText().length();
		}
		return ((double) countSentenceChars) / jcas.getDocumentText().length();
	}

	protected String getText(Annotation covering) {
		if (covering == null) {
			return "(null)";
		}
		else {
			return covering.getCoveredText();
		}
	}
	
	protected Integer getNumSentences(Annotation covered, Map<Token, Collection<Sentence>> tokenIndex) throws CASException, AceException {
		return getCoveringSentences(covered, tokenIndex).size();
	}
	
	protected MultiMap<Sentence,Token> getCoveringSentences(Annotation covered, Map<Token, Collection<Sentence>> tokenIndex) throws CASException, AceException {
		MultiMap<Sentence,Token> result = new MultiHashMap<Sentence,Token>();
		// TODO Horrible HACK!!!!
		// This is because qi's modified dataset sometimes just doesn't have an ldc scope, so we skip it silently.
		// and since it's null, we can't event check: covered instanceof EventMentionLdcScope :(
		if (covered==null) {
			return result;
		}
		
		MultiMap<Token,Sentence> map = selectCoveredByIndex(covered.getCAS().getJCas(), Token.class, covered.getBegin(), covered.getEnd(), tokenIndex);

		// A token can only have one sentence
		for (Entry<Token,Collection<Sentence>> entry : map.entrySet()) {
			if (entry.getValue().size() != 1) {
				throw new AceException("Found token that does not have exactly one sentence, it has " + entry.getValue().size() + " sentences: " + entry.getKey());
			}
			result.put(entry.getValue().iterator().next(), entry.getKey());
		}
		return result;
	}
	
	protected Integer getNumTokens(Annotation covering) {
		if (covering == null) {
			return -1;
		}
		else {
			return JCasUtil.selectCovered(Token.class, covering).size();
		}
	}
	
	/**
	 * Given some begin..end span, gets all annotations of type {@code T} (usually {@link Token})
	 * in the span, and returns a mapping between each one of them, and its covering annotation of
	 * type {@code S} (usually {@link Sentence}). Each {@code T} annotation may have more than
	 * a single covering {@code S} annotation.
	 * This uses a pre-constructed index of {@code T}-type annotations to their covering 
	 * {@code S}-type annotations.<BR>
	 * <BR>
	 * For example, this is good for finding all the sentences that this span is under (note that the span
	 * does NOT need to cover each sentence fully, even sentences that only have one token in the span are
	 * retrieved).
	 * @param jcas JCas holding the annotations
	 * @param tClass type of mediating annotation, used in the index (usually {@link Token})
	 * @param begin begin offset of requested span
	 * @param end end offset of requested span
	 * @param t2sIndex an pre-constructed index between {@code T} and {@code S} annotations. Can
	 * be constructed using {@link JCasUtil#indexCovering(JCas, Class, Class)}
	 * @return a multimap between {@code T} annotations and their covering {@code S} annotations
	 */
	protected <T extends Annotation,S extends Annotation> MultiMap<T,S> selectCoveredByIndex(JCas jcas, Class<T> tClass, int begin, int end, Map<T, Collection<S>> t2sIndex) {
		List<T> tList = JCasUtil.selectCovered(jcas, tClass, begin, end);
		MultiMap<T,S> t2s = new MultiHashMap<T,S>();
		for (T t : tList) {
			t2s.putAll(t, t2sIndex.get(t));
		}
		return t2s;
		
	}

	protected String getSpecificPosString(Annotation covering) {
		if (covering == null) {
			return "(null)";
		}
		else {
			List<POS> poses = JCasUtil.selectCovered(POS.class, covering);
			List<String> posTexts = new ArrayList<String>(poses.size());
			for (POS pos : poses) {
				posTexts.add(pos.getPosValue());
			}
			return StringUtil.join(posTexts, " ");
		}
	}

	protected String getLemmas(Annotation covering) {
		if (covering == null) {
			return "(null)";
		}
		else {
			List<Token> tokens = JCasUtil.selectCovered(Token.class, covering);
			List<String> lemmaTexts = new ArrayList<String>(tokens.size());
			for (Token token : tokens) {
				lemmaTexts.add(token.getLemma().getValue());
			}
			return StringUtil.join(lemmaTexts, " ");
		}
	}

	protected String getGeneralPosString(Annotation covering) {
		if (covering == null) {
			return "(null)";
		}
		else {
			List<POS> poses = JCasUtil.selectCovered(POS.class, covering);
			List<String> posTexts = new ArrayList<String>(poses.size());
			for (POS pos : poses) {
				posTexts.add(pos.getType().getShortName());
			}
			return StringUtil.join(posTexts, " ");
		}
	}
	
	protected List<BasicNode> getTreeFragments(Annotation covering) throws CASException, AceException, TreeAndParentMapException, TreeFragmentBuilderException {
		List<BasicNode> result = new ArrayList<BasicNode>();
		if (covering != null) {
			MultiMap<Sentence, Token> sentence2tokens = getCoveringSentences(covering, tokenIndex);
			for (Entry<Sentence,Collection<Token>> entry : sentence2tokens.entrySet()) {
				FragmentAndReference frag = getFragmentBySentenceAndTokens(entry.getKey(), entry.getValue());
				result.add(frag.getFragmentRoot());
			}
		}
		return result;
	}

	/**
	 * Returns a tree fragment of the connection between the roots of the two covering annotations.<BR><BR>
	 * This method assumes that each covering annotation is within sentence boundaries,
	 * otherwise it doesn't make much sense. This is in contrary to {@link #getTreeFragments(Annotation)}
	 * which does not assume that and may return multiple fragments.
	 * @param covering
	 * @return
	 * @throws CASException
	 * @throws AceException
	 * @throws TreeAndParentMapException
	 * @throws TreeFragmentBuilderException
	 * @throws AceAbnormalMessage 
	 */
	protected BasicNode getRootLinkingTreeFragment(Annotation covering1, Annotation covering2) throws CASException, AceException, TreeAndParentMapException, TreeFragmentBuilderException, AceAbnormalMessage {
		if (covering1 == null || covering2 == null) {
			throw new AceAbnormalMessage("NullParam");
		}
		
		//logger.trace("%%% 1");
		
		MultiMap<Sentence, Token> sentence2tokens_1 = getCoveringSentences(covering1, tokenIndex);
		MultiMap<Sentence, Token> sentence2tokens_2 = getCoveringSentences(covering2, tokenIndex);
		if (sentence2tokens_1.size() == 0 || sentence2tokens_2.size() == 0) {
			throw new AceAbnormalMessage("ERR:No Covering Sentence"
					//, String.format("Got at least one of the two annotations, that is not covered by any sentence: " +
					//"(%s sentences, %s sentences)", sentence2tokens_1.size(), sentence2tokens_2.size()), logger
					);
		}
		if (sentence2tokens_1.size() > 1 || sentence2tokens_2.size() > 1) {
			throw new AceAbnormalMessage("ERR:Multiple Sentence Annotation", String.format("Got at least one of the two annotations, that does not cover exactly one sentence: " +
					"(%s sentences, %s sentences)", sentence2tokens_1.size(), sentence2tokens_2.size()), logger);
		}
		Entry<Sentence,Collection<Token>> s2t1 = sentence2tokens_1.entrySet().iterator().next();
		Entry<Sentence,Collection<Token>> s2t2 = sentence2tokens_2.entrySet().iterator().next();
		if (s2t1.getKey() != s2t2.getKey()) {
			throw new AceAbnormalMessage("ERR:Different Sentences", String.format("Got two annotations in different sentences: sentence1=%s, sentence2=%s",
					s2t1.getKey(), s2t2.getKey()), logger);
		}
		Sentence sentence = s2t1.getKey();

		//logger.trace("%%% 2");

		// get the fragment of each covering annotation
		FragmentAndReference frag1 = getFragmentBySentenceAndTokens(sentence, s2t1.getValue());
		FragmentAndReference frag2 = getFragmentBySentenceAndTokens(sentence, s2t2.getValue());
		//logger.trace("%%% 3");

		// and now... get the fragment containing the roots of both fragments!
		// this is the connecting fragment
		Token root1 = token2nodes.getSingleKeyOf(frag1.getOrigReference());
		Token root2 = token2nodes.getSingleKeyOf(frag2.getOrigReference());
//		Token root1 = info2token.get(frag1.getInfo());
//		Token root2 = info2token.get(frag2.getInfo());
		//logger.trace("%%% 4");

		//TODO remove, for debug
//		List<BasicNode> n = new ArrayList<BasicNode>();
//		for (BasicNode nn : token2nodes.values()) {
//			if (frag1.getInfo().getNodeInfo().getWord().equals(nn.getInfo().getNodeInfo().getWord())) {
//				n.add(nn);
//			}
//		}
		//TODO finish
		
		List<Token> bothRoots = Arrays.asList(new Token[] {root1, root2});
		FragmentAndReference connectingFrag = getFragmentBySentenceAndTokens(sentence, bothRoots);
		//logger.trace("%%% 5");

		// Store this
		Facet facet = new Facet(frag1.getOrigReference(), frag2.getOrigReference());
		linkToFacet.put(connectingFrag.getFragmentRoot(), facet);
		return connectingFrag.getFragmentRoot();
	}
	
	protected FragmentAndReference getFragmentBySentenceAndTokens(Sentence sentence, Collection<Token> tokens) throws TreeAndParentMapException, TreeFragmentBuilderException {
		BasicNode root = sentence2root.get(sentence);
		Set<BasicNode> targetNodes = new LinkedHashSet<BasicNode>(tokens.size());
		for (Token token : tokens) {
			targetNodes.addAll(token2nodes.get(token)); //Also get duplicated nodes!
		}
		//logger.trace("-------- fragmenter.build(" + AnotherBasicNodeUtils.getNodeString(root) + ", " + AnotherBasicNodeUtils.getNodesString(targetNodes) + ")");
		FragmentAndReference fragRef = fragmenter.build(root, targetNodes);
		return fragRef;
	}
	
	protected void updateLinkingTreeFrags(Map<String, String> key, EventMentionAnchor eventAnchor,
			BasicArgumentMentionHead argHead, String field, String fieldDep, String fieldDepGenPos,
			String fieldDepSpecPos) throws CASException, AceException, TreeAndParentMapException, TreeFragmentBuilderException, StatsException {
		List<BasicNode> roots = null;
		String abnormal = null;
		try {
			//throw new AceAbnormalMessage("Frags disabled");
			roots = Arrays.asList(new BasicNode[] {getRootLinkingTreeFragment(eventAnchor, argHead)});
		} catch (AceAbnormalMessage e) {
			abnormal = e.getMessage();
		}
		
		docs.updateDocs(key, field, fieldDep,        abnormal!=null ? abnormal : getParenthesesTreeOnlyDependencies(roots));
		docs.updateDocs(key, field, fieldDepGenPos,  abnormal!=null ? abnormal : getParenthesesTreeDependenciesGeneralPOS(roots));
		docs.updateDocs(key, field, fieldDepSpecPos, abnormal!=null ? abnormal : getParenthesesTreeDependenciesSpecificPOS(roots));
	}



//	protected BasicNode getMinimalTreeFragment(BasicNode root, Set<BasicNode> nodes) {
//		
//		// get the minimal subtree containing all nodes
//		Map<BasicNode, Set<BasicNode>> setsContainingNodes = new HashMap<BasicNode, Set<BasicNode>>();
//		Set<BasicNode> entireTree = AbstractNodeUtils.treeToSet(root);
//		for (BasicNode subtreeRoot : entireTree) {
//			Set<BasicNode> subtree = AbstractNodeUtils.treeToSet(subtreeRoot);
//			if (subtree.containsAll(nodes)) {
//				setsContainingNodes.put(subtreeRoot, subtree);
//			}
//		}
//		// the smallest set is the minimal tree
//		 Entry<BasicNode, Set<BasicNode>> minimal = Collections.min(setsContainingNodes.entrySet(), new Comparator<Entry<BasicNode, Set<BasicNode>>>() {
//			@Override public int compare(Entry<BasicNode, Set<BasicNode>> o1,
//					Entry<BasicNode, Set<BasicNode>> o2) {
//				return o1.getValue().size() - o2.getValue().size();
//			}
//		});
//	}
	protected String printIndented(List<BasicNode> roots) {
		List<String> strs = new ArrayList<String>(roots.size());
		for (BasicNode root : roots) {
			strs.add(AbstractNodeUtils.getIndentedString(root));
		}
		return StringUtil.join(strs, "\n");
	}
	
	protected String getParenthesesTreeOnlyDependencies(List<BasicNode> trees) {
		if (trees.isEmpty()) {
			return "(null)";
		}
		return TreePrinter.getString(trees, "( ", " )", "#", new SimpleNodeString() {
			@Override public String toString(BasicNode node) {
				return " "+InfoGetFields.getRelation(node.getInfo(), "<ROOT>")+" ";
			}
		});
	}

	protected String getParenthesesTreeDependenciesSpecificPOS(List<BasicNode> trees) {
		if (trees.isEmpty()) {
			return "(null)";
		}
		return TreePrinter.getString(trees, "( ", " )", "#", new SimpleNodeString() {
			@Override public String toString(BasicNode node) {
				return " "+InfoGetFields.getRelation(node.getInfo(), "<ROOT>")+"->"+InfoGetFields.getPartOfSpeech(node.getInfo())+" ";
			}
		});
	}

	protected String getParenthesesTreeDependenciesGeneralPOS(List<BasicNode> trees) {
		if (trees.isEmpty()) {
			return "(null)";
		}
		return TreePrinter.getString(trees, "( ", " )", "#", new SimpleNodeString() {
			@Override public String toString(BasicNode node) {
				return " "+InfoGetFields.getRelation(node.getInfo(), "<ROOT>")+"->"+node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag()+" ";
			}
		});
	}

	protected AceAnalyzer() {
		//do nothing
	}
	
	public AceAnalyzer(File nomlexFile, File classRoleTableFile) throws NomlexException, IOException {
		// Pasta init
		NomlexMapBuilder nomlexMapBuilder = new NomlexMapBuilder(nomlexFile.getPath(),classRoleTableFile.getPath());
		nomlexMapBuilder.build();
		ImmutableMap<String, Nominalization> nomlexMap = nomlexMapBuilder.getNomlexMap(); 
		pastaFactory = new PredicateArgumentStructureBuilderFactory<Info, BasicNode>(nomlexMap);
		
		// doc categories init
		devFileIds = FileUtils.loadFileToString(AceAnalyzer.class.getResource("/qi/new_filelist_ACE_dev").getPath());
		trainFileIds = FileUtils.loadFileToString(AceAnalyzer.class.getResource("/qi/new_filelist_ACE_training").getPath());
		testFileIds = FileUtils.loadFileToString(AceAnalyzer.class.getResource("/qi/new_filelist_ACE_test").getPath());
	}
	
	public void analyzeFolder(String xmiFolderPath, String entityStatsOutputPath, String roleStatsOutputFile, String typePerDocOutputFile, String detailedOutputFolderPath) throws AceException, LAPException, IOException, StatsException, InvalidXMLException, ResourceInitializationException, SAXException, CASException, CasTreeConverterException, UnsupportedPosTagStringException, TreeAndParentMapException, TreeFragmentBuilderException, PredicateArgumentIdentificationException {
		
		File detailedFolder = new File(detailedOutputFolderPath);
		if (!detailedFolder.isDirectory()) {
			throw new AceException("Directory " + detailedFolder + " does not exist");
		}
		
		File folder = new File(xmiFolderPath);
		File[] xmiFiles = folder.listFiles(new FileFilters.ExtFileFilter("xmi"));
		if (xmiFiles == null) {
			throw new AceException("Directory " + folder + " does not exist");
		}
		if (xmiFiles.length==0) {
			throw new AceException("Must have at least one preprocessed XMI in " + folder);
		}
		for (File xmi : xmiFiles) {
			JCas jcas = UimaUtils.loadXmi(xmi, "/desc/DummyAE.xml");
			analyzeOneJcas(jcas);
		}
		
		writeOutput(entityStatsOutputPath, roleStatsOutputFile, typePerDocOutputFile, detailedOutputFolderPath);
	}
	
	protected void writeOutput(String entityStatsOutputPath, String roleStatsOutputFile, String typePerDocOutputFile, String detailedOutputFolderPath) throws IOException {
		docs.dumpAsCsvFiles(new File(entityStatsOutputPath), new File(roleStatsOutputFile), new File(typePerDocOutputFile));
		dumpDetailedFiles(detailedOutputFolderPath);
	}
	
	protected void dumpDetailedFiles(String detailedOutputFolderPath) throws IOException {
		final String GLUE = "\n\n==============\n\n";
		dumpDetailedFiles(detailedOutputFolderPath, GLUE);
	}

	protected void dumpDetailedFiles(String detailedOutputFolderPath, String glue) throws IOException {
		final String EXT = ".txt";
		for (Entry<String, List<String>> entry : detailedFiles.entrySet()) {
			File file = new File(detailedOutputFolderPath, entry.getKey() + EXT);
			String out = StringUtil.join(entry.getValue(), glue);
			FileUtils.writeFile(file, out);
		}
	}

	protected static void initLog() {
		final String LOG4J_PROPERTIES = "log4j.properties";
		
		// Use the file log4j.properties to initialize log4j
		PropertyConfigurator.configure(LOG4J_PROPERTIES);
		
		// Pick the logger, and start writing log messages
		logger = Logger.getLogger(AceAnalyzer.class);
		
		// Register the log-file(s) (if exist(s)) as file(s) to be saved by ExperimentManager.
		for (Appender appender : LoggerUtilities.getAllAppendersIncludingParents(logger))
		{
			// cannot avoid RTTI, since current implementation of log4j provides
			// no other alternative.
			if (appender instanceof FileAppender)
			{
				File file = new File(((FileAppender)appender).getFile());
				ExperimentManager.getInstance().register(file);
			}
		}
		
	}
	public static void main(String args[]) throws AnalysisEngineProcessException, ResourceInitializationException, LAPException, InstantiationException, IOException, AceException, InvalidXMLException, SAXException, StatsException, CASException, CasTreeConverterException, UnsupportedPosTagStringException, TreeAndParentMapException, TreeFragmentBuilderException, PredicateArgumentIdentificationException {
		if (args.length != 5) {
			System.err.println("USAGE: AceAnalyzer <xmi input folder> <entity stats output file> <role stats output file> <type-per-doc output file> <detailed output folder>");
			return;
		}
		//initLog();
		File nomlexFile = new File("C:\\Java\\Git\\lab\\nlp-lab\\Trunk\\asher\\predargs\\src\\main\\resources\\nomlex\\nomlex-plus.txt");
		File classRoleTableFile = new File("C:\\Java\\Git\\lab\\nlp-lab\\Trunk\\asher\\predargs\\src\\main\\resources\\nomlex\\ClassRoleTable.txt");
		new AceAnalyzer(nomlexFile, classRoleTableFile).analyzeFolder(args[0], args[1], args[2], args[3], args[4]);
	}
	
	protected AceAnalyzerDocumentCollection docs = new AceAnalyzerDocumentCollection();
	protected Map<String, List<String>> detailedFiles = new LinkedHashMap<String, List<String>>();
	
	protected Map<Token, Collection<Sentence>> tokenIndex;	
	protected CasTreeConverter converter;
	protected OneToManyBidiMultiHashMap<Token, BasicNode> token2nodes;
	//protected BidiMap<Info, Token> info2token;
	protected BidiMap<Sentence, BasicNode> sentence2root;
	protected TreeFragmentBuilder fragmenter;
	protected PredicateArgumentStructureBuilderFactory<Info, BasicNode> pastaFactory;
	protected Map<BasicNode, PredicateArgumentStructure<Info, BasicNode>> predicateToPas;
	protected ValueSetMap<BasicNode, PredicateArgumentStructure<Info, BasicNode>> argToPas;
	//protected MultiMap<BasicNode, PredicateArgumentStructure<Info, BasicNode>> clauseArgToPas;
	protected Map<BasicNode, Facet> linkToFacet;
	protected String devFileIds;
	protected String trainFileIds;
	protected String testFileIds;
	

	protected static Logger logger = Logger.getLogger(AceAnalyzer.class);
}
