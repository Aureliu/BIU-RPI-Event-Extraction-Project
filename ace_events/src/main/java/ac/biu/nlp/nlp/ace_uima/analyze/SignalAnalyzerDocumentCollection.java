package ac.biu.nlp.nlp.ace_uima.analyze;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import ac.biu.nlp.nlp.ace_uima.stats.FieldName;
import ac.biu.nlp.nlp.ace_uima.stats.StatsDocument;
import ac.biu.nlp.nlp.ace_uima.stats.StatsDocumentCollection;
import ac.biu.nlp.nlp.ace_uima.stats.StatsException;
import ac.biu.nlp.nlp.ace_uima.stats.StatsFieldType;


public class SignalAnalyzerDocumentCollection extends StatsDocumentCollection {

	@Override
	public void updateDocs(Map<String,String> key, String fieldNameLvl1, String fieldNameLvl2, Object element, boolean isDynamic) throws StatsException {
		triggerDoc.startUpdate();
		triggerDoc.update(key, fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		triggerDoc.update(without(key, new String[] {"category"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		triggerDoc.update(without(key, new String[] {"EventSubType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		triggerDoc.update(without(key, new String[] {"category", "EventSubType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		triggerDoc.update(without(key, new String[] {"docId"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		triggerDoc.update(without(key, new String[] {"docId", "EventSubType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		triggerDoc.update(without(key, new String[] {"folder", "docId"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		triggerDoc.update(without(key, new String[] {"folder", "docId", "EventSubType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		triggerDoc.update(without(key, new String[] {"folder", "category", "docId",}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		triggerDoc.update(without(key, new String[] {"folder", "category", "docId", "EventSubType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		triggerDoc.endUpdate();
		
		argDoc.startUpdate();
		argDoc.update(key, fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		argDoc.update(without(key, new String[] {"EventSubType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		argDoc.update(without(key, new String[] {"EventSubType", "Role"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		argDoc.update(without(key, new String[] {"EventSubType", "ArgType"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		argDoc.update(without(key, new String[] {"EventSubType", "ArgType", "Role"}), fieldNameLvl1, fieldNameLvl2, element, isDynamic);
		argDoc.endUpdate();
		
//		globalDoc.startUpdate();
//		globalDoc.update(key, fieldNameLvl1, fieldNameLvl2, element, isDynamic);
//		globalDoc.endUpdate();
	}
	
	public void dumpAsCsvFiles(File entityFile, File roleFile, File typePerDocFile) throws IOException { //add a specific parameter for each specific file
		triggerDoc.dumpAsCsv(entityFile);
		argDoc.dumpAsCsv(roleFile);
		globalDoc.dumpAsCsv(typePerDocFile);
	}
	
	
	// The documents
	
	@SuppressWarnings("serial")
	private StatsDocument triggerDoc = new StatsDocument(
			Arrays.asList(new String[] {
					"folder", "category", "docId", "EventSubType", "SignalName"
			}),
			new LinkedHashMap<FieldName,StatsFieldType>() {{
				put(new FieldName("Perfrmace", ""), StatsFieldType.PERFORMANCE);
			}});
	
	@SuppressWarnings("serial")
	private StatsDocument argDoc = new StatsDocument(
			Arrays.asList(new String[] {
					"EventSubType", "ArgType", "Role"
			}),
			new LinkedHashMap<FieldName,StatsFieldType>() {{
				put(new FieldName("Perfrmace", ""), StatsFieldType.PERFORMANCE);
			}});
	
	@SuppressWarnings("serial")
	private StatsDocument globalDoc = new StatsDocument(
			Arrays.asList(new String[] {
					"?"
			}),
			new LinkedHashMap<FieldName,StatsFieldType>() {{
				put(new FieldName("Perfrmace", ""), StatsFieldType.PERFORMANCE);
			}});

}
