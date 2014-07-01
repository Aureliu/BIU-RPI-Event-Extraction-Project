package edu.cuny.qc.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;

public class NomlexPlus {

	public enum NomlexPlusDictionary {
		NOM         ("NOM",       ":ORTH", ":VERB", CanonicalPosTag.V,   nomMapFromNoun,        nomMapToNoun),
		NOMADJ      ("NOMADJ",    ":ORTH", ":ADJ",  CanonicalPosTag.ADJ, nomadjMapFromNoun,     nomadjMapToNoun),
		NOMLIKE     ("NOMLIKE",   ":ORTH", ":VERB", CanonicalPosTag.V,   nomlikeMapFromNoun,    nomlikeMapToNoun),
		NOMADJLIKE  ("NOMADJLIKE",":ORTH", ":ADJ",  CanonicalPosTag.ADJ, nomadjlikeMapFromNoun, nomadjlikeMapToNoun),
		ABLENOM     ("ABLE-NOM",  ":ORTH", ":VERB", CanonicalPosTag.V,   nomableMapFromNoun,    nomableMapToNoun);
		
		private NomlexPlusDictionary(String type, String nounMarker, String otherMarker, CanonicalPosTag tagOfOther, Multimap<String, String> mapFromNoun, Multimap<String, String> mapToNoun) {
			this.type = type;
			this.nounMarker = nounMarker;
			this.otherMarker = otherMarker;
			this.tagOfOther = tagOfOther;
			this.mapFromNoun = mapFromNoun;
			this.mapToNoun = mapToNoun;
			this.pattern = Pattern.compile(String.format(RECORD_CONTENT_PATTERN, type, nounMarker, otherMarker));
		}
		public String type;
		public String nounMarker;
		public String otherMarker;
		public CanonicalPosTag tagOfOther;
		public Multimap<String, String> mapFromNoun;
		public Multimap<String, String> mapToNoun;
		public Pattern pattern;
	}
	
	public NomlexPlus(File nomlexFile) throws IOException {
		loadFile(nomlexFile);	
	}
	
	private void loadFile(File nomlexFile) throws IOException {
		String content = FileUtils.loadFileToString(nomlexFile);
		if (!content.startsWith("\n")) {
			content = "\n" + content;
		}
		
		Matcher m1 = NOMLEX_RECORD.matcher(content);
		while (m1.find()) {
			String record = m1.group(1);
			for (NomlexPlusDictionary dict : NomlexPlusDictionary.values()) {
				Matcher m2 = dict.pattern.matcher(record);
				while (m2.find()) {
					String noun = m2.group(1);
					String other = m2.group(2);
					dict.mapFromNoun.put(noun, other);
					dict.mapToNoun.put(other, noun);
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		File f = new File("C:\\Java\\Git\\lab\\nlp-lab\\Trunk\\asher\\predargs\\src\\main\\resources\\nomlex\\nomlex-plus.txt");
		NomlexPlus inst = new NomlexPlus(f);
		System.out.printf("Done loading NomlexPlus! %s\n", inst);
	}
	
	private final static Pattern NOMLEX_RECORD = Pattern.compile("(?s)\\n(\\(.+?)(?=\\n\\()");
	private final static String RECORD_CONTENT_PATTERN = "(?s)\\(%s\\s+%s\\s+\"([^\"]+)\".+?%s\\s+\"([^\"]+)\"";

	private static Multimap<String, String> nomMapFromNoun = HashMultimap.create();
	private static Multimap<String, String> nomMapToNoun = HashMultimap.create();
	private static Multimap<String, String> nomadjMapFromNoun = HashMultimap.create();
	private static Multimap<String, String> nomadjMapToNoun = HashMultimap.create();
	private static Multimap<String, String> nomlikeMapFromNoun = HashMultimap.create();
	private static Multimap<String, String> nomlikeMapToNoun = HashMultimap.create();
	private static Multimap<String, String> nomadjlikeMapFromNoun = HashMultimap.create();
	private static Multimap<String, String> nomadjlikeMapToNoun = HashMultimap.create();
	private static Multimap<String, String> nomableMapFromNoun = HashMultimap.create();
	private static Multimap<String, String> nomableMapToNoun = HashMultimap.create();
}
