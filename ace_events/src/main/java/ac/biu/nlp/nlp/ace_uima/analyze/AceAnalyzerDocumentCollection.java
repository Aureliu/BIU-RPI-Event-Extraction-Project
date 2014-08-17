package ac.biu.nlp.nlp.ace_uima.analyze;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.cuny.qc.ace.acetypes.AceArgumentType;

import ac.biu.nlp.nlp.ace_uima.stats.FieldName;
import ac.biu.nlp.nlp.ace_uima.stats.StatsDocument;
import ac.biu.nlp.nlp.ace_uima.stats.StatsDocumentCollection;
import ac.biu.nlp.nlp.ace_uima.stats.StatsException;
import ac.biu.nlp.nlp.ace_uima.stats.StatsFieldType;

/**
 * This class defines the specific stats documents.
 * A lot of hard-coded stuff here, in a perfect world most of this
 * class would be in configuration. So for modifying the structure
 * of the docs, you should probably change things here (and in the
 * calling classes that creates the values themselves and assign
 * them to the fields).
 *  
 * @author Ofer Bronstein
 *
 */
public class AceAnalyzerDocumentCollection extends StatsDocumentCollection {

	@Override
	public void updateDocs(Map<String,String> key, String fieldNameLvl1, String fieldNameLvl2, Object element, boolean isDynamic) throws StatsException {
		entityDoc.startUpdate();
		entityDoc.update(key, fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		entityDoc.update(without(key, new String[] {"category"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		entityDoc.update(without(key, new String[] {"EventSubType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		entityDoc.update(without(key, new String[] {"category", "EventSubType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		entityDoc.update(without(key, new String[] {"docId"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		entityDoc.update(without(key, new String[] {"docId", "EventSubType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		entityDoc.update(without(key, new String[] {"folder", "docId"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		entityDoc.update(without(key, new String[] {"folder", "docId", "EventSubType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		entityDoc.update(without(key, new String[] {"folder", "category", "docId",}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		entityDoc.update(without(key, new String[] {"folder", "category", "docId", "EventSubType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		entityDoc.endUpdate();
		
		roleDoc.startUpdate();
		roleDoc.update(key, fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		roleDoc.update(without(key, new String[] {"EventSubType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		roleDoc.update(without(key, new String[] {"EventSubType", "Role"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		roleDoc.update(without(key, new String[] {"EventSubType", "ArgType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		roleDoc.update(without(key, new String[] {"EventSubType", "ArgType", "Role"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		roleDoc.endUpdate();
		
		typePerDoc.startUpdate();
		typePerDoc.update(key, fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		typePerDoc.update(without(key, new String[] {"category", "docId"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		typePerDoc.endUpdate();
	}
	
	public void dumpAsCsvFiles(File entityFile, File roleFile, File typePerDocFile) throws IOException { //add a specific parameter for each specific file
		entityDoc.dumpAsCsv(entityFile);
		roleDoc.dumpAsCsv(roleFile);
		typePerDoc.dumpAsCsv(typePerDocFile);
	}
	
	
	public final static boolean USE_PASTA = true;
	
	// The documents
	
	@SuppressWarnings("serial")
	private StatsDocument entityDoc = new StatsDocument(
			Arrays.asList(new String[] {
					"folder", "category", "docId", "EventSubType"
			}),
			new LinkedHashMap<FieldName,StatsFieldType>() {{
				put(new FieldName("NumSentences", ""), StatsFieldType.COUNT_INT);
				put(new FieldName("SentenceCovered", ""), StatsFieldType.COUNT_DOUBLE);

				put(new FieldName("MentionsPerSentence", "All"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("MentionsPerSentence", "PerType"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("MentionsList", "All"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("MentionsList", "PerType"), StatsFieldType.LIST_COUNTS);
				//put(new FieldName("MentionsSet", "All"), StatsFieldType.LIST_COUNTS);
				//put(new FieldName("MentionsSet", "PerType"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("MentionsAnchorsList", "All"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("MentionsAnchorsList", "PerType"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("TypedMentionsPerSentence", "PerType"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("TypeInSentence", "PerType"), StatsFieldType.COUNT_INT);

//				put(new FieldName("Tense", ""), StatsFieldType.ENUM_REL);
//				put(new FieldName("Modality", ""), StatsFieldType.ENUM_REL);
//				put(new FieldName("Polarity", ""), StatsFieldType.ENUM_REL);
//				put(new FieldName("Genericity", ""), StatsFieldType.ENUM_REL);
				
				put(new FieldName("Events", ""), StatsFieldType.COUNT_INT);
				put(new FieldName("MentionsPerEvent", ""), StatsFieldType.COUNT_INT);
				put(new FieldName("ArgsPerEvent", ""), StatsFieldType.COUNT_INT);
				put(new FieldName("ArgsPerMention", ""), StatsFieldType.COUNT_INT);
				put(new FieldName("Roles", ""), StatsFieldType.LIST_COUNTS);
				
				put(new FieldName("Anchor", ""), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Anchor", "Lemmas"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Anchor", "Tokens"), StatsFieldType.COUNT_INT);
				put(new FieldName("Anchor", "Tokens2"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Anchor", "SpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Anchor", "GenPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Anchor", "TokenSpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Anchor", "LemmaSpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Anchor", "TokenGenPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Anchor", "Dep"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Anchor", "DepToken"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Anchor", "DepGenPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Anchor", "DepSpecPOS"), StatsFieldType.LIST_COUNTS);
				
				put(new FieldName("Extent", ""), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Extent", "Tokens"), StatsFieldType.COUNT_INT);
				put(new FieldName("Extent", "Sentences"), StatsFieldType.COUNT_INT);
				put(new FieldName("Extent", "SpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Extent", "GenPOS"), StatsFieldType.LIST_COUNTS);
				
				put(new FieldName("LdcScope", ""), StatsFieldType.LIST_COUNTS);
				put(new FieldName("LdcScope", "Tokens"), StatsFieldType.COUNT_INT);
				put(new FieldName("LdcScope", "Sentences"), StatsFieldType.COUNT_INT);
				put(new FieldName("LdcScope", "SpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("LdcScope", "GenPOS"), StatsFieldType.LIST_COUNTS);
				
				put(new FieldName("Link", "Dep"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Link", "*Dep"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Link", "DepGenPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Link", "*DepGenPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Link", "DepSpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Link", "*DepSpecPOS"), StatsFieldType.LIST_COUNTS);

				if (USE_PASTA) {
					put(new FieldName("FindLinks", ""), StatsFieldType.ENUM_REL);
					put(new FieldName("PredMissed", "*Dep"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("PredMissed", "*DepGenPOS"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("ArgMissed", "*Dep"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("ArgMissed", "*DepGenPOS"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("LinkMissed", "*Dep"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("LinkMissed", "*DepGenPOS"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("LinkFound", "*Dep"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("LinkFound", "*DepGenPOS"), StatsFieldType.LIST_COUNTS);
					
					put(new FieldName("FindLinkList", "LinkFound"), StatsFieldType.SUM_INT);
					put(new FieldName("FindLinkList", "MissedPredicate"), StatsFieldType.SUM_INT);
					put(new FieldName("FindLinkList", "MissedArgument"), StatsFieldType.SUM_INT);
					put(new FieldName("FindLinkList", "MissedLink"), StatsFieldType.SUM_INT);
					put(new FieldName("FindLinkList", "PastaOnly"), StatsFieldType.SUM_INT);
				}
			}});
	
	@SuppressWarnings("serial")
	private StatsDocument roleDoc = new StatsDocument(
			Arrays.asList(new String[] {
					"EventSubType", "ArgType", "Role"
			}),
			new LinkedHashMap<FieldName,StatsFieldType>() {{
				put(new FieldName("Argument", "SpecType"), StatsFieldType.LIST_COUNTS);
				
				put(new FieldName("EventsPerArg", "All"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("EventsPerArg", "PerRole"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("EventsPerArg2", "All"), StatsFieldType.COUNT_INT);
				put(new FieldName("EventsPerArg2", "PerRole"), StatsFieldType.COUNT_INT);
				put(new FieldName("EventTypesPerArg", "All"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("EventTypesPerArg", "PerRole"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("RolesPerArg", "All"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("RolesPerArg", "PerRole"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("TypedEventTypesPerArg", "PerRole"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("TypedRolesPerArg", "PerRole"), StatsFieldType.LIST_COUNTS);

				put(new FieldName("ArgHead", ""), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgHead", "SpecType"), StatsFieldType.LIST_VALUES);
				put(new FieldName("ArgHead", "Lemmas"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgHead", "Tokens"), StatsFieldType.COUNT_INT);
				put(new FieldName("ArgHead", "Tokens2"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgHead", "SpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgHead", "GenPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgHead", "TokenSpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgHead", "LemmaSpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgHead", "TokenGenPOS"), StatsFieldType.LIST_COUNTS);
				for (AceArgumentType specType : AceArgumentType.values()) {
					put(new FieldName("ArgHead", specType.toString()), StatsFieldType.LIST_COUNTS);
				}
				put(new FieldName("ArgHead", "Dep"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgHead", "DepToken"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgHead", "DepGenPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgHead", "DepSpecPOS"), StatsFieldType.LIST_COUNTS);

				put(new FieldName("ConcreteArgHead", ""), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ConcreteArgHead", "SpecType"), StatsFieldType.LIST_VALUES);
				put(new FieldName("ConcreteArgHead", "Lemmas"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ConcreteArgHead", "Tokens"), StatsFieldType.COUNT_INT);
				put(new FieldName("ConcreteArgHead", "Tokens2"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ConcreteArgHead", "SpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ConcreteArgHead", "GenPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ConcreteArgHead", "TokenSpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ConcreteArgHead", "LemmaSpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ConcreteArgHead", "TokenGenPOS"), StatsFieldType.LIST_COUNTS);
				
				
				put(new FieldName("PROPER", ""), StatsFieldType.LIST_COUNTS);
				put(new FieldName("PR,Multi", ""), StatsFieldType.LIST_VALUES);
				put(new FieldName("PR,Multi", "NullHead"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("PR,Multi", "Head"), StatsFieldType.LIST_VALUES);
				put(new FieldName("PR,Multi", "HeLemma"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("PR,Multi", "HeLemChange"), StatsFieldType.LIST_VALUES);
				put(new FieldName("PR,Multi", "WN"), StatsFieldType.LIST_VALUES);
				put(new FieldName("PR,Multi", "HeWN"), StatsFieldType.LIST_VALUES);
				put(new FieldName("PR,Multi", "HeWNLem"), StatsFieldType.LIST_COUNTS);
				
				put(new FieldName("PR,Single", ""), StatsFieldType.LIST_VALUES);
				put(new FieldName("PR,Single", "Lemma"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("PR,Single", "LemChange"), StatsFieldType.LIST_VALUES);
				put(new FieldName("PR,Single", "WN"), StatsFieldType.LIST_VALUES);
				put(new FieldName("PR,Single", "WNLem"), StatsFieldType.LIST_COUNTS);
				
				put(new FieldName("COMMON", ""), StatsFieldType.LIST_COUNTS);
				put(new FieldName("CO,Multi", ""), StatsFieldType.LIST_VALUES);
				
				put(new FieldName("CO,Single", ""), StatsFieldType.LIST_VALUES);
				put(new FieldName("CO,Single", "Lemma"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("CO,Single", "LemChange"), StatsFieldType.LIST_VALUES);
				put(new FieldName("CO,Single", "WN"), StatsFieldType.LIST_VALUES);
				put(new FieldName("CO,Single", "WNLem"), StatsFieldType.LIST_COUNTS);
				
				
				for (AceArgumentType specType : AceArgumentType.values()) {
					put(new FieldName("ConcreteArgHead", specType.toString()), StatsFieldType.LIST_COUNTS);
				}
				put(new FieldName("ConcreteArgHead", "Dep"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ConcreteArgHead", "DepToken"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ConcreteArgHead", "DepGenPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ConcreteArgHead", "DepSpecPOS"), StatsFieldType.LIST_COUNTS);

				put(new FieldName("ArgExtent", ""), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgExtent", "Tokens"), StatsFieldType.COUNT_INT);
				put(new FieldName("ArgExtent", "Lemmas"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgExtent", "Sentences"), StatsFieldType.COUNT_INT);
				put(new FieldName("ArgExtent", "SpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgExtent", "GenPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgExtent", "TokenSpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgExtent", "LemmaSpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("ArgExtent", "TokenGenPOS"), StatsFieldType.LIST_COUNTS);

				put(new FieldName("Link", "Dep"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Link", "*Dep"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Link", "DepGenPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Link", "*DepGenPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Link", "DepSpecPOS"), StatsFieldType.LIST_COUNTS);
				put(new FieldName("Link", "*DepSpecPOS"), StatsFieldType.LIST_COUNTS);

				if (USE_PASTA) {
					put(new FieldName("FindLinks", ""), StatsFieldType.ENUM_REL);
					put(new FieldName("PredMissed", "*Dep"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("PredMissed", "*DepGenPOS"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("ArgMissed", "*Dep"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("ArgMissed", "*DepGenPOS"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("LinkMissed", "*Dep"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("LinkMissed", "*DepGenPOS"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("LinkFound", "*Dep"), StatsFieldType.LIST_COUNTS);
					put(new FieldName("LinkFound", "*DepGenPOS"), StatsFieldType.LIST_COUNTS);
					
					put(new FieldName("FindLinkList", "LinkFound"), StatsFieldType.SUM_INT);
					put(new FieldName("FindLinkList", "MissedPredicate"), StatsFieldType.SUM_INT);
					put(new FieldName("FindLinkList", "MissedArgument"), StatsFieldType.SUM_INT);
					put(new FieldName("FindLinkList", "MissedLink"), StatsFieldType.SUM_INT);
					put(new FieldName("FindLinkList", "PastaOnly"), StatsFieldType.SUM_INT);
				}

			}});
	
	@SuppressWarnings("serial")
	private StatsDocument typePerDoc = new StatsDocument(
			Arrays.asList(new String[] {
					"category", "docId"
			}),
			new LinkedHashMap<FieldName,StatsFieldType>() {{
				put(new FieldName("EventSubType", ""), StatsFieldType.ENUM_SUM);
			}});

}
