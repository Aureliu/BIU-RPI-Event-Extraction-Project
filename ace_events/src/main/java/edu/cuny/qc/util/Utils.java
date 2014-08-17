package edu.cuny.qc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.ace_uima.AceException;
import ac.biu.nlp.nlp.ace_uima.analyze.SignalAnalyzer;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.ace.acetypes.AceTimexMention;
import edu.cuny.qc.ace.acetypes.AceValueMention;
import edu.cuny.qc.perceptron.graph.GraphEdge;

public class Utils {
	public static List<String> logOnlyTheseSentences = null;
	private static final Runtime runtime = Runtime.getRuntime();
	public static final double MB = 1024.0*1024;
	private static final Random random = new Random();

	public static Set<String> PUNCTUATION = Sets.newHashSet(Arrays.asList(new String[] {
			".", ",", "!", "?", ":", "@", "#", "$", "%", "|", "'"
	}));
	
	public static double inMB(long bytes) {
		return bytes / MB;
	}
	public static String detailedLog() {
		long max = runtime.maxMemory();
		long total = runtime.totalMemory();
		return String.format("[%1$tH:%1$tM:%1$tS.%1$tL max=%2$.2f, total=%3$.2f, used=%4$.2f]",
				new Date(),
				max==Long.MAX_VALUE? "no limit" : inMB(max), 
				inMB(total), 
				inMB(total - runtime.freeMemory()));
	}
	
	public static void print(PrintStream out, String prefix, String postfix, String delimiter, Object sentID, Object...args) {
		if (out != null) {
			String sentIDStr = null;
			if (sentID!=null) {
				sentIDStr = sentID.toString();
			}
			if (logOnlyTheseSentences == null || logOnlyTheseSentences.size()==0 || sentIDStr==null || sentIDStr.isEmpty() ||
					sentIDStr.equals(Logs.POST_ITERATION_MARK) || logOnlyTheseSentences.contains(sentIDStr)) {
				out.print(prefix + StringUtils.join(args, delimiter) + postfix);
			}
		}
	}
	
	public static void fileInit(File f) throws FileNotFoundException {
		File prev = new File(f.getAbsolutePath() + ".previous");
		if (prev.isFile()) {
			prev.delete();
		}
		if (f.isFile()) {
			f.renameTo(prev);
		}
		PrintStream p = new PrintStream(f);
		p.printf("(file is writable - verified)");
		p.close();
	}

	public static <K,V> void addToMultimap(Multimap<K,V> multi, Map<K,V> map) {
		for (Entry<K,V> entry : map.entrySet()) {
			multi.put(entry.getKey(), entry.getValue());
		}
	}
	
	public static int randInt(int minInclusive, int maxInclusive) {
		int result = random.nextInt((maxInclusive - minInclusive) + 1) + minInclusive;
		return result;
	}
	
	/**
	 * Doesn't keep original order.
	 */
	public static <T> List<T> sample(List<T> elements, int amount) {
		if (amount > elements.size()) {
			throw new IllegalArgumentException(String.format("Got amount %s which is more than the list's size (%s)", amount, elements.size()));
		}
		List<T> result = Lists.newArrayListWithCapacity(amount);
		ImmutableSortedSet<Integer> indexesImm = ContiguousSet.create(Range.closed(0, elements.size()-1), DiscreteDomain.integers());
		List<Integer> indexes = Lists.newArrayList(indexesImm);
		for (int i=0; i<amount; i++) {
			int ii = randInt(0, indexes.size()-1);
			int index = indexes.remove(ii);
			T element = elements.get(index);
			result.add(element);
		}
		return result;
	}
	public static Logger handleLog() throws IOException {
		File target = new File("./target/classes/log4j.properties");
		Files.createParentDirs(target);
		Files.copy(new File("./log4j.properties"), target);
		return Logger.getLogger(SignalAnalyzer.class);
	}

	public static MultiMap<Sentence,Token> getCoveringSentences(Annotation covered, Map<Token, Collection<Sentence>> tokenIndex) throws CASException, AceException {
		MultiMap<Sentence,Token> result = new MultiHashMap<Sentence,Token>();
		// TODO Horrible HACK!!!!
		// This is because qi's modified dataset sometimes just doesn't have an ldc scope, so we skip it silently.
		// and since it's null, we can't event check: covered instanceof EventMentionLdcScope :(
		if (covered==null) {
			return result;
		}
		
		/// DEBUG
		if (covered.getCoveredText().equals("Egyptian")) {
			System.out.printf("");
		}
		///
		
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
	public static <T extends Annotation,S extends Annotation> MultiMap<T,S> selectCoveredByIndex(JCas jcas, Class<T> tClass, int begin, int end, Map<T, Collection<S>> t2sIndex) {
		List<T> tList = JCasUtil.selectCovered(jcas, tClass, begin, end);
		MultiMap<T,S> t2s = new MultiHashMap<T,S>();
		for (T t : tList) {
			t2s.putAll(t, t2sIndex.get(t));
		}
		return t2s;
		
	}

	public static Span getHead(AceMention mention) {
		if (mention instanceof AceEntityMention) {
			AceEntityMention m = (AceEntityMention) mention;
			return m.head;
		}
		else if (mention instanceof AceTimexMention || mention instanceof AceValueMention) {
			return mention.extent;
		}
		else {
			throw new IllegalArgumentException("Received mention of unknown type (class=" + mention.getClass().getName() + "): " + mention);
		}
	}
	
	public static String dependeciesToStr(List<Dependency> deps) {
		List<String> strs = Lists.newArrayListWithCapacity(deps.size());
		for (Dependency dep : deps) {
			Token dependent = dep.getDependent();
			Token governor = dep.getGovernor();
			strs.add(String.format("%s(%s[%s:%s]->%s[%s:%s])", dep.getDependencyType(),
					dependent.getCoveredText(), dependent.getBegin(), dependent.getEnd(),
					governor.getCoveredText(), governor.getBegin(), governor.getEnd()));
		}
		return String.format("%s deps: [%s]", deps.size(), StringUtils.join(strs, ", "));
	}
	
	public static String edgesToStr(List<GraphEdge> edges, List<Token> tokens) {
		List<String> strs = Lists.newArrayListWithCapacity(edges.size());
		for (GraphEdge edge : edges) {
			Token dependent = tokens.get(edge.getDependent());
			Token governor = tokens.get(edge.getGovernor());
			strs.add(String.format("%s(%s[%s:%s]->%s[%s:%s])", edge.getRelation(),
					dependent.getCoveredText(), dependent.getBegin(), dependent.getEnd(),
					governor.getCoveredText(), governor.getBegin(), governor.getEnd()));
		}
		return String.format("%s edges: [%s]", edges.size(), StringUtils.join(strs, ", "));
	}
}
