package edu.cuny.qc.perceptron.types;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.FeatureStructure;

import com.google.common.collect.Lists;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;
import edu.cuny.qc.ace.acetypes.AceDocument;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceEventMention;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.ace.acetypes.AceRelationMention;
import edu.cuny.qc.ace.acetypes.AceTimexMention;
import edu.cuny.qc.ace.acetypes.AceValueMention;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.util.Span;

/**
 * This class represents the original rich representation about one sentence
 * it may contains some reference to the whole document, or even the whole corpus
 * @author che
 *
 */
public class Sentence implements java.io.Serializable
{

	/**
	 * defines the sentence attributes types
	 * @author check
	 *
	 */
	static public enum Sent_Attribute
	{
		TOKENS,  		// tokens: String[]
		POSTAGS, 		// POS tags: String[]
		CHUNKS,  		// chunk tags: String[]
		DepGraph,		// graph representation about the deps 
		Token_FEATURE_MAPs,   // feature maps for each token: list->map<key,value>s
		TOKEN_SPANS,		  // absolute spans for each token
		ParseTree,		// the parse tree
		SentenceAnnotation, // UIMA Sentence Annotation
		TokenAnnotations,   // UIMA Token Annotations
	}
	
	/**
	 * the sentID, the number of sentence in its document
	 */
	public int sentID;
	
	/**
	 * Ofer: Add this for debugging purposes
	 */
	protected String text;
	
	/**
	 * a reference to its document object
	 */
	protected Document doc;
	
	/**
	 * the span of the sentence
	 */
	private Span extent;
	
	/**
	 * the actual content of the sentence, encoded in a map
	 */
	protected Map<Sent_Attribute, Object> map = new HashMap<Sent_Attribute, Object>();
	
	/**
	 * mentions of events, entities, and values
	 */
	/**
	 * Qi: all mentions of events / values / entities 
	 */
	public List<AceEventMention> eventMentions = new ArrayList<AceEventMention>();
	public List<AceEntityMention> entityMentions = new ArrayList<AceEntityMention>();
	public List<AceValueMention> valueMentions = new ArrayList<AceValueMention>();
	public List<AceTimexMention> timexMentions = new ArrayList<AceTimexMention>();
	public List<AceRelationMention> relationMentions = new ArrayList<AceRelationMention>();
	
	private Boolean filtered = false;

	private transient List<Token> tokenAnnos = null;
	private transient de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence sentenceAnno = null;

	
	private Sentence() {}
	
	public Sentence(Document doc, int sentID, String text)
	{
		final int MAX_TEXT_LEN = 6;
		this.doc = doc;
		this.sentID = sentID;
		this.text = (StringUtils.substring(text.replace("\"", "\\\""), 0, MAX_TEXT_LEN) + "..").intern();
	}
	
	/**
	 * Similarity to {@link edu.cuny.qc.ace.acetypes.AceDocument#deepCopy(AceDocument)},
	 * this clones the instance, while creating new objects for the lists. It doesn't
	 * clone the lists' elements, as we assume they are immutable.<BR>
	 * 
	 * Also, this deep-copy is partial as it doesn't create a new object for the map
	 * (we don't need that for our purposes).
	 * @param orig
	 * @return
	 */
	public static Sentence partiallyDeepCopy(Sentence orig) {
		Sentence newSent = new Sentence();
		
		newSent.sentID = orig.sentID;
		newSent.text = orig.text;
		newSent.doc = orig.doc;
		newSent.extent = orig.extent;
		newSent.map = orig.map;
		newSent.filtered = orig.filtered;
		newSent.eventMentions = Lists.newArrayList(orig.eventMentions);
		newSent.entityMentions = Lists.newArrayList(orig.entityMentions);
		newSent.valueMentions = Lists.newArrayList(orig.valueMentions);
		newSent.timexMentions = Lists.newArrayList(orig.timexMentions);
		newSent.relationMentions = Lists.newArrayList(orig.relationMentions);
		
		return newSent;
	}

	@Override
	public String toString() {
		final int TEXT_DISPLAY_MAX = 10;
		return String.format("%s[%s events: %s...]", sentID, eventMentions.size(), StringUtils.substring(text, 0, TEXT_DISPLAY_MAX));
	}
	
	public void fillAceAnnotaions()
	{
		if(doc.isHasLabel())
		{
			// push the ace annotations to the sentence object
			fillMentions2Sent(doc.aceAnnotations.eventMentions, eventMentions);
			fillMentions2Sent(doc.aceAnnotations.entityMentions, entityMentions);
			fillMentions2Sent(doc.aceAnnotations.valueMentions, valueMentions);
			fillMentions2Sent(doc.aceAnnotations.timexMentions, timexMentions);
			fillMentions2Sent(doc.aceAnnotations.relationMentions, relationMentions);
			
			for(Iterator<AceEventMention> iter = eventMentions.iterator(); iter.hasNext();)
			{
				AceEventMention mention = iter.next();
				mention.setHeadIndices((Span[]) this.get(Sent_Attribute.TOKEN_SPANS), (String[]) this.get(Sent_Attribute.POSTAGS));
				if(mention.getHeadIndices() == null || mention.getHeadIndices().size() == 0)
				{
					// remove event mentions whose trigger is empty. They are empty because their pos tags are not one of (Verb, Noun and Adj)
					iter.remove();
					doc.aceAnnotations.eventMentions.remove(mention);
				}
			}
			
			for(AceMention mention : entityMentions)
			{
				mention.setHeadIndices((Span[]) this.get(Sent_Attribute.TOKEN_SPANS));
				mention.setExtentIndices((Span[]) this.get(Sent_Attribute.TOKEN_SPANS));
			}
			
			for(AceMention mention : valueMentions)
			{
				mention.setHeadIndices((Span[]) this.get(Sent_Attribute.TOKEN_SPANS));
			}
			
			for(AceMention mention : timexMentions)
			{
				mention.setHeadIndices((Span[]) this.get(Sent_Attribute.TOKEN_SPANS));
			}
			
			for(AceMention mention : relationMentions)
			{
				mention.setHeadIndices((Span[]) this.get(Sent_Attribute.TOKEN_SPANS));
			}
		}
	}
	
	public void filterBySpecs(TypesContainer types) {
		AceDocument.filterBySpecs(types, filtered, eventMentions, entityMentions, valueMentions, timexMentions, null, null, null, null, null);
	}

	/**
	 * fill ace annotations to the sentence 
	 * @param eventMentions2
	 * @param eventMentions3
	 * @param class1
	 */
	private void fillMentions2Sent(List mentions, List sentMentions)
	{
		for(Object obj : mentions)
		{
			if(obj instanceof AceMention)
			{
				AceMention mention = (AceMention) obj;
				Span mention_extent = mention.extent;
				
				// if the mention is overlapped with the sent, add it to the sent 
				if(extent.overlap(mention_extent))
				{
					sentMentions.add(mention);
				}
			}
		}	
	}

	public Object get(Sent_Attribute key)
	{
		return map.get(key);
	}
	
	public void put(Sent_Attribute key, Object value)
	{
		map.put(key, value);
	}

	public void setExtent(Span extent)
	{
		this.extent = extent;
	}

	public Span getExtent()
	{
		return extent;
	}
	
	public int size()
	{
		String[] tokens = (String[]) get(Sent_Attribute.TOKENS);
		if(tokens == null)
		{
			return 0;
		}
		else
		{
			return tokens.length;
		}
	}
	
//	public void printBasicSent(PrintStream out)
//	{
//		String[] tokens = (String[]) this.get(Sent_Attribute.TOKENS);
//		String[] posTags = (String[]) this.get(Sent_Attribute.POSTAGS);
//		String[] chunks = (String[]) this.get(Sent_Attribute.CHUNKS);
//		DependencyGraph graph = (DependencyGraph) this.get(Sent_Attribute.DepGraph);
//		
//		// print pos/chunks/tdl
//		out.print(Arrays.toString(tokens) + "\t");
//		out.print(Arrays.toString(posTags) + "\t");
//		out.print(Arrays.toString(chunks) + "\t");
//		out.print(graph);
//		out.println();
//		
//		// print tokens 
//		List<Map<Class<?>, Object>> list =  (List<Map<Class<?>, Object>>) this.get(Sent_Attribute.Token_FEATURE_MAPs);
//		for(Map<Class<?>, Object> token_features : list)
//		{
//			for(Class<?> key : token_features.keySet())
//			{
//				Object value = token_features.get(key);
//				out.print(value);
//				out.print("\t");
//			}
//			out.println();
//		}
//		
//		// print ace annotations
//		printAceAnnotatoin(out, this.entityMentions);
//		printAceAnnotatoin(out, this.relationMentions);
//		printAceAnnotatoin(out, this.valueMentions);
//		printAceAnnotatoin(out, this.timexMentions);
//		printAceAnnotatoin(out, this.eventMentions);
//	}
	
	private void printAceAnnotatoin(PrintStream out, List mentions)
	{
		for(Object obj : mentions)
		{
			if(obj instanceof AceMention)
			{
				AceMention mention = (AceMention) obj;
				mention.write(new PrintWriter(out, true));
			}
		}
	}
	
	public Token getTokenAnnotation(int i) {
		Token token = tokenAnnos.get(i);
		if (token == null) {
			List<Integer> tokenAddrs = (List<Integer>) this
					.get(Sent_Attribute.TokenAnnotations);
			Integer addr = tokenAddrs.get(i);
			FeatureStructure fs = doc.jcas.getLowLevelCas()
					.ll_getFSForRef(addr);
			token = (Token) fs;
			tokenAnnos.set(i, token);
		}
		return token;
	}

	public de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence getSentenceAnnotation() {
		if (sentenceAnno == null) {
			Integer addr = (Integer) this
					.get(Sent_Attribute.SentenceAnnotation);
			sentenceAnno = (de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence) doc.jcas
					.getLowLevelCas().ll_getFSForRef(addr);
		}
		return sentenceAnno;
	}

	public void initTokenAnnos() {
		tokenAnnos = new ArrayList<Token>(Collections.nCopies(size(),
				(Token) null));
	}

//	public String getText() {
//		return text;
//	}

}
