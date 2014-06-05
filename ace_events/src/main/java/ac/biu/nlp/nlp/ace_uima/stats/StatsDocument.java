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
		StringBuffer title1 = new StringBuffer();
		StringBuffer title2 = new StringBuffer();
		StringBuffer subtitle = new StringBuffer();
		StringBuffer content = new StringBuffer();
		
		title1.append(StringUtil.join(keyFields, SEPARATOR));
		title2.append(StringUtil.join(Collections.nCopies(keyFields.size(), " "), SEPARATOR));
		subtitle.append(StringUtil.join(Collections.nCopies(keyFields.size(), " "), SEPARATOR));
		
		boolean first = true;
		for (Entry<List<String>, StatsRow> row : rows.entrySet()) {
			content.append(StringUtil.join(row.getKey(), SEPARATOR));
			
			for (Entry<FieldName,StatsField> field : row.getValue().getFields().entrySet()) {
				content.append(SEPARATOR + StringUtil.join(field.getValue().getValues(MAX_CHARS), SEPARATOR));
				
				if (first) {
					List<String> subtitles = field.getValue().getSubtitles();
					title1.append(SEPARATOR + StringUtil.join(Collections.nCopies(subtitles.size(), field.getKey().getLvl1()), SEPARATOR));
					title2.append(SEPARATOR + StringUtil.join(Collections.nCopies(subtitles.size(), field.getKey().getLvl2()), SEPARATOR));
					subtitle.append(SEPARATOR + StringUtil.join(subtitles, SEPARATOR));
				}
			}
			
			first = false;
			content.append("\n");
		}
		
		title1.append("\n");
		title2.append("\n");
		subtitle.append("\n");
		
		String output = title1.toString() + title2.toString() + subtitle.toString() + content.toString();
		FileUtils.writeFile(file, output);
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
				int cmp = iter1.next().compareTo(iter2.next());
				if (cmp!=0) {
					return cmp;
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
	private static final int MAX_CHARS = 5000;
}
