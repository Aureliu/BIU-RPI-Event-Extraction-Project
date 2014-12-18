package ac.biu.nlp.nlp.ace_uima.stats;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.cuny.qc.util.Utils;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.file.FileUtils;

public class StatsDocument {

	public StatsDocument(List<String> keyFields, LinkedHashMap<FieldName, StatsFieldType> linkedHashMap) {
		this(keyFields, linkedHashMap, false, null);
	}
	
	public StatsDocument(List<String> keyFields, LinkedHashMap<FieldName, StatsFieldType> linkedHashMap, boolean supportDynamicFields, StatsFieldType defaultFieldType) {
		this.keyFields = keyFields;
		this.fieldSpecs = linkedHashMap;
		this.supportDynamicFields = supportDynamicFields;
		this.defaultFieldType = defaultFieldType;
	}
	
	public StatsRow getRow(Map<String,String> key) throws StatsException {
		List<String> specificKey = new ArrayList<String>();
		for (String name : keyFields) {
			if (key.containsKey(name)) {
				specificKey.add(key.get(name));
			}
			else {
				specificKey.add(ANY);
			}
		}
		
		if (rows.containsKey(specificKey)) {
			return rows.get(specificKey);
		}
		else {
			StatsRow row = new StatsRow(fieldSpecs, supportDynamicFields, defaultFieldType);
			rows.put(specificKey, row);
			
			return row;
		}
	}
		
	public void update(Map<String,String> key, String fieldNameLvl1, String fieldNameLvl2, Object element, boolean isDynamic) throws StatsException {
		StatsRow row = getRow(key);
		StatsField field = row.getField(new FieldName(fieldNameLvl1, fieldNameLvl2), isDynamic);
		if (field != null && !rowsOfCurrentUpdate.contains(row)) {
			field.addElement(element);
			rowsOfCurrentUpdate.add(row);
		}
	}
	
	public void startUpdate() {
		rowsOfCurrentUpdate = new HashSet<StatsRow>();
	}
	
	public void endUpdate() {
		rowsOfCurrentUpdate = null;
	}
	
	public void dumpAsCsv(File file) throws IOException {
		System.out.printf("%s Starting StatsDocument.dumpAsCsv(), rows.size()=%s\n", Utils.detailedLog(), rows.size());
		StringBuffer title1 = new StringBuffer();
		StringBuffer title2 = new StringBuffer();
		StringBuffer subtitle = new StringBuffer("\n"); // Start line with a blank line, for Excel to sort+filter by it, not by first rows
		StringBuffer content = new StringBuffer();
		
		title1.append(StringUtil.join(keyFields, SEPARATOR));
		title2.append(StringUtil.join(Collections.nCopies(keyFields.size(), " "), SEPARATOR));
		subtitle.append(StringUtil.join(Collections.nCopies(keyFields.size(), " "), SEPARATOR));
		
		boolean first = true;
		int n=0;
		for (Entry<List<String>, StatsRow> row : rows.entrySet()) {
			/// DEBUG
			//System.out.printf("\nStatsdocument.dumpAsCsv() key=%s: ", row.getKey());
			///
			String keyStr = StringUtil.join(row.getKey(), SEPARATOR);
			content.append(keyStr);
			
			if (n<10 || n%500==0) {
				System.out.printf("%s StatsDocument.dumpAsCsv(), n=%s, key=%s\n", Utils.detailedLog(), n, keyStr);
			}
			
			for (Entry<FieldName,StatsField> field : row.getValue().getFields().entrySet()) {
				String line = SEPARATOR + StringUtil.join(field.getValue().getValues(MAX_CHARS), SEPARATOR);
				content.append(line);
				
				if (first) {
					List<String> subtitles = field.getValue().getSubtitles();
					for (int i=0; i<subtitles.size(); i++) {
						if (subtitles.get(i).isEmpty()) {
							subtitles.set(i, " "); //absent subtitles should contain a single space, so that Excel will let the subtitles row be a filter+sort header
						}
					}
					String title1Str = SEPARATOR + StringUtil.join(Collections.nCopies(subtitles.size(), field.getKey().getLvl1()), SEPARATOR);
					title1.append(title1Str);
					String title2Str = SEPARATOR + StringUtil.join(Collections.nCopies(subtitles.size(), field.getKey().getLvl2()), SEPARATOR);
					title2.append(title2Str);
					String subtitleStr = SEPARATOR + StringUtil.join(subtitles, SEPARATOR); 
					subtitle.append(subtitleStr);
					
					System.out.printf("%s StatsDocument.dumpAsCsv(), n=%s, first=True - Writing titles!\n   %s\n   %s\n   %s\n", Utils.detailedLog(), n, title1Str, title2Str, subtitleStr);
				}
				
				if (n<10 || n%500==0) {
					System.out.printf("%s StatsDocument.dumpAsCsv(), n=%s,     line=%s\n", Utils.detailedLog(), n, line);
				}

			}
			
			n++;
			first = false;
			content.append("\n");
		}
		
		title1.append("\n");
		title2.append("\n");
		subtitle.append("\n");
		
		String output = title1.toString() + title2.toString() + subtitle.toString() + content.toString();
		FileUtils.writeFile(file, output);
		System.out.printf("%s Finishing StatsDocument.dumpAsCsv()\n", Utils.detailedLog());
	}

	private class LexicographicKeyComparator implements Comparator<List<String>> {
		@Override
		public int compare(List<String> o1, List<String> o2) {
			Iterator<String> iter1 = o1.iterator();
			Iterator<String> iter2 = o2.iterator();
			while (iter1.hasNext()) {
				if (!iter2.hasNext()) {
					return 1;
				}
				int cmp;
				String s1 = iter1.next();
				String s2 = iter2.next();
				if (s1 == null && s2 == null) {
					cmp = 0;
				}
				else if (s1 == null) {
					return 1;
				}
				else if (s2 == null) {
					return -1;
				}
				else {
					cmp = s1.compareTo(s2);
					if (cmp!=0) {
						return cmp;
					}
				}
			}
			if (iter2.hasNext()) {
				return -1;
			}
			return 0;
		}
	}
	

	private List<String> keyFields;
	private LinkedHashMap<FieldName,StatsFieldType> fieldSpecs;
	private boolean supportDynamicFields = false;
	private StatsFieldType defaultFieldType = null;
	private SortedMap<List<String>, StatsRow> rows = new TreeMap<List<String>, StatsRow>(new LexicographicKeyComparator());
	private Set<StatsRow> rowsOfCurrentUpdate = null;
	
	public static final String ANY = "*";
	public static final String SEPARATOR = "^";
	public static final String SEPARATOR_SUBSTITUTE = "~";
	public static final int MAX_CHARS = 8000;
}
