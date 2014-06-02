package edu.cuny.qc.perceptron.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.util.InvalidFormatException;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.impl.CASCompleteSerializer;
import org.apache.uima.cas.impl.Serialization;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import ac.biu.nlp.nlp.ie.onthefly.input.AeException;
import ac.biu.nlp.nlp.ie.onthefly.input.AnalysisEngines;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceDocument;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceEventMention;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Sentence.Sent_Attribute;
import edu.cuny.qc.util.SentDetectorWrapper;
import edu.cuny.qc.util.Span;
import edu.cuny.qc.util.TokenizerWrapper;
import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;

/**
 * read the source data of i2b2
 * @author che
 *
 */
public class Document implements java.io.Serializable
{
	private static final long serialVersionUID = 2307017698146800811L;
	
	// file extentions
	static public final String textFileExt = ".sgm";
	static public final String apfFileExt = ".apf.xml";
	static public final String preprocessedFileExt = ".preprocessed.bz2";
	static public final String signalsFileExt = ".signals.bz2";
	//static public final String xmiFileExt = ".xmi";
	//public static final int SENT_ID_ADDITION = 100000;

	
	//static public final String AE_FILE_PATH = "/desc/DummyAEforCAS.xml";
	
	// the id (base file name) of the document
	public String docID;
	public String text;
	/**
	 * the difference between text and all text is that, alltext includes headline etc.
	 */
	public String allText;
	public String headline;
	private String before_text;
	public int textoffset;
	protected boolean hasLabel;
	public boolean signalsUpdated = false;
	
	// if the document is monoCase
	boolean monoCase = false;
	
	/* the list of sentences
	 * they are instances in the learning process, there can be a dummy list of sentences, where each sentence is a cluster of sentence
	 * e.g. the dummy sentence can be concatenation of sentences that linked by entity coreference 
	 */
	protected List<Sentence> sentences;
	
	// transient - to not be serialized
	public transient JCas jcas = null;
	protected static AnalysisEngine ae = null;
	
//	public transient List<List<Map<String, Map<String, SignalInstance>>>> triggerSignals;
//	public transient List<List<Map<String, Map<String, Map<String, SignalInstance>>>>> argSignals;
//	public transient boolean signalsLoaded = false;
	public transient BundledSignals signals = null;
	
	/**
	 * this object contains the parsed information from apf file (pretty much everything)
	 * it can be considered as gold standard for event extraction or can provide perfect entities etc.
	 */
	protected AceDocument aceAnnotations;
	
//	// Event type --> Trigger token
//	public static Map<String, List<String>> triggerTokens = new HashMap<String, List<String>>();
//	// Event subtype --> Trigger token
//	public static Map<String, List<String>> triggerTokensFineGrained = new HashMap<String, List<String>>();
//	// Event subtype --> trigger token with high confidence value
//	public static Map<String, List<String>> triggerTokensHighQuality = new HashMap<String, List<String>>();
	
	/**
	 * the container for the sentence "clusters"
	 */
//	protected List<List<Sentence>> sentClusters = new ArrayList<List<Sentence>>(); 
	
//	/**
//	 * return the sentence clusters to the client
//	 * @return
//	 */
//	public List<List<Sentence>> getSentenceClusters()
//	{
//		return sentClusters;
//	}
	
	static
	{
		System.err.println("??? Document: Still need to make sure XMI is not saved inside preprocessed doc, only in separate file");
		System.err.println("??? Document: Running both my UIMA-preprocessing and Qi's old preprocessing. Consider discarding Qi's.");
		System.err.println("??? Document: Not dumping processed document (and jcas), because annotations are referenced not only in cas but also in cuny.Sentence and cuny.SentenceInstance. To solve, I should load annotations to these classes separately, like by finding in Document.jcas rlevant single annotations in [begin, end] spans.");
		
		// initialize priorityQueueEntities
		try
		{
			ae = AnalysisEngines.forDocument(null);
			
			// initialize dict of triggerTokens
//			BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/data/triggerTokens"));
//			String line = null;
//			while((line = reader.readLine()) != null)
//			{
//				if(line.length() == 0)
//				{
//					continue;
//				}
//				String[] fields = line.split("\\t");
//				String eventSubType = fields[0];
//				String triggerToken = fields[1];
//				Double confidence = Double.parseDouble(fields[2]);
//				if(confidence < 0.150)
//				{
//					continue;
//				}
//				String eventType = TypeConstraints.eventTypeMapModified.get(eventSubType);
//				List<String> triggers = triggerTokens.get(eventType);
//				if(triggers == null)
//				{
//					triggers = new ArrayList<String>();
//					triggerTokens.put(eventType, triggers);
//				}
//				if(!triggers.contains(triggerToken))
//				{
//					triggers.add(triggerToken);
//				}
//				
//				triggers = triggerTokensFineGrained.get(eventSubType);
//				if(triggers == null)
//				{
//					triggers = new ArrayList<String>();
//					triggerTokensFineGrained.put(eventSubType, triggers);
//				}
//				if(!triggers.contains(triggerToken))
//				{
//					triggers.add(triggerToken);
//				}
//				
//				if(confidence >= 0.50)
//				{
//					triggers = triggerTokensHighQuality.get(eventSubType);
//					if(triggers == null)
//					{
//						triggers = new ArrayList<String>();
//						triggerTokensHighQuality.put(eventSubType, triggers);
//					}
//					if(!triggers.contains(triggerToken))
//					{
//						triggers.add(triggerToken);
//					}
//				}
//			}
//			reader.close();
		} 
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
		catch (AeException e)
		{
			e.printStackTrace();
		}
	}
	
//	public void printDocCluster(PrintStream out)
//	{
//		int i=0;
//		for(List<Sentence> cluster : this.sentClusters)
//		{
//			out.println("cluster " + i++);
//			for(Sentence sent : cluster)
//			{
//				String[] tokens = (String[]) sent.get(Sent_Attribute.TOKENS);
//				for(String token : tokens)
//				{
//					out.print(token + " ");
//				}
//				out.println();
//			}
//		}
//	}
	
//	public void setSentenceClustersByTokens()
//	{
//		final String allowedPOS = "IN|JJ|RB|DT|VBG|VBD|NN|NNPS|VB|VBN|NNS|VBP|NNP|PRP|VBZ";
//		
//		this.sentClusters.clear();
//		// travels each entity to get relevant sents
//		
//		for(String eventType : triggerTokens.keySet())
//		{
//			List<String> triggers = triggerTokens.get(eventType);
//			List<Sentence> cluster = new ArrayList<Sentence>();
//			// add sentence that contains this entity
//			for(int i=0; i<this.sentences.size(); i++)
//			{
//				Sentence sent = this.sentences.get(i);
//				List<Map<Class<?>, Object>> tokens = (List<Map<Class<?>, Object>>) sent.get(Sent_Attribute.Token_FEATURE_MAPs);
//				for(int j=0; j<tokens.size(); j++)
//				{
//					String pos = (String) tokens.get(j).get(TokenAnnotations.PartOfSpeechAnnotation.class);
//					if(pos.matches(allowedPOS))
//					{
//						String lemma = (String) tokens.get(j).get(TokenAnnotations.LemmaAnnotation.class);
//						if(triggers.contains(lemma))
//						{
//							cluster.add(sent);
//							break;
//						}
//					}
//				}
//			}
//			
//			// add cluster if doesn't exist
//			if(cluster.size() > 0 && !this.sentClusters.contains(cluster))
//			{
//				this.sentClusters.add(cluster);
//			}
//		}
//		
//		// sort clusters by size and remove overlapping sents
//		Collections.sort(this.sentClusters, new Comparator<List<Sentence>>()
//			{			@Override
//				public int compare(List<Sentence> set1, List<Sentence> set2)
//				{
//					if(set1.size() > set2.size())
//					{
//						return -1;
//					}
//					else if(set1.size() < set2.size())
//					{
//						return 1;
//					}
//					else
//					{
//						return 0;
//					}
//				}
//			}
//		);
//		
//		for(int i=1; i<this.sentClusters.size(); i++)
//		{
//			List<Sentence> cluster = this.sentClusters.get(i);
//			for(int j=0; j<i; j++)
//			{
//				List<Sentence> pre_cluster = this.sentClusters.get(j);
//				// remove overlapping sents
//				cluster.removeAll(pre_cluster);
//			}
//			if(cluster.size() == 0)
//			{
//				this.sentClusters.remove(cluster);
//				i--;
//			}
//		}
//		
//		// set the remaining sents as singleton clusters
//		for(int i=0; i<this.sentences.size(); i++)
//		{
//			// only add sents that are not in any existing clusters
//			Sentence sent = this.sentences.get(i);
//			if(isNotInCluster(sent))
//			{
//				List<Sentence> cluster = new ArrayList<Sentence>();
//				cluster.add(sent);
//				this.sentClusters.add(cluster);
//			}
//		}
//	}
	
	/**
	 * check if sent is in any cluster or not
	 * @param sent
	 * @return
	 */
//	private boolean isNotInCluster(Sentence sent)
//	{
//		for(List<Sentence> cluster : this.sentClusters)
//		{
//			if(cluster.contains(sent))
//			{
//				return false;
//			}
//		}
//		return true;
//	}
	
	/**
	 * implicit constructor
	 */
	protected Document()
	{
		;
	}
	
	public Document(String baseFileName) throws IOException
	{
		// by default, the hasLabel is true
		this(baseFileName, true, false);
	}
	
	/**
	 * Note: this function assume the *.sgm files and *.apf file are in the same folder
	 * @param baseFile the file of *.xml or any file name prefix excluding .txt .extent .tlink
	 * @param hasLabel if this document has .extent/.tlink annotations (ground truth)
	 * @throws Exception 
	 */
//	public Document(String baseFileName, boolean hasLabel, boolean monoCase, String year) throws IOException
//	{
//		this.monoCase = monoCase;
//		docID = baseFileName;
//		File txtFile = new File(baseFileName + textFileExt);
//		
//		this.setHasLabel(hasLabel);
//		if(this.isHasLabel())
//		{
//			String apfFile = baseFileName + apfFileExt;
//			setAceAnnotations(new AceDocument(txtFile.getAbsolutePath(), apfFile, year));
//		}
//		
//		sentences = new ArrayList<Sentence>();
//		readDoc(txtFile, this.monoCase);
//	}
	
	/**
	 * Note: this function assume the *.sgm files and *.apf file are in the same folder
	 * @param baseFile the file of *.xml or any file name prefix excluding .txt .extent .tlink
	 * @param hasLabel if this document has .extent/.tlink annotations (ground truth)
	 * @throws Exception 
	 */
	public Document(String baseFileName, boolean hasLabel, boolean monoCase) throws IOException
	{
		this(baseFileName, hasLabel, monoCase, null);
	}
	
	public Document(String baseFileName, boolean hasLabel, boolean monoCase, JCas existingJCas) throws IOException
	{
		this.monoCase = monoCase;
		docID = baseFileName;
		File txtFile = new File(baseFileName + textFileExt);
		
		this.setHasLabel(hasLabel);
		if(this.isHasLabel())
		{
			String apfFile = baseFileName + apfFileExt;
			setAceAnnotations(new AceDocument(txtFile.getAbsolutePath(), apfFile));
		}
		
		sentences = new ArrayList<Sentence>();
		readDoc(txtFile, this.monoCase, existingJCas);
	}
	
	public static Document createAndPreprocess(String baseFileName, boolean hasLabel, boolean monoCase, boolean tryLoadExisting, boolean dumpNewDoc, TypesContainer types, Perceptron perceptron) throws IOException {
		try {
			// Kludge - don't serialize for now
			//dumpNewDoc = false;
			//tryLoadExisting = false;
			///////////////////////////////////////////////////////////////////////////////////////////////////
			
			Document doc = null;
			File preprocessed = new File(baseFileName + preprocessedFileExt);
			//File xmi = new File(baseFileName + xmiFileExt);
			if (tryLoadExisting && preprocessed.isFile()) {
				InputStream in = new BZip2CompressorInputStream(new FileInputStream(preprocessed));
				doc = (Document) SerializationUtils.deserialize(in);
				in.close();
				//doc.jcas = UimaUtils.loadXmi(xmi, AE_FILE_PATH);
				if (types.specs != null) { 
					doc.filterBySpecs(types);
				}
			}
			if (doc==null) {
				doc = new Document(baseFileName, hasLabel, monoCase);
				
				// These two are separated only for historical reasons, and could be joint back.
				TextFeatureGenerator.doPreprocess(doc);
				TextFeatureGenerator.fillTextFeatures_NoPreprocessing(doc);
				
				ae.process(doc.jcas);
				
				if (dumpNewDoc) {
					try {
						OutputStream out = new BZip2CompressorOutputStream(new FileOutputStream(preprocessed));
						SerializationUtils.serialize(doc, out);
						out.close();
						//UimaUtils.dumpXmi(xmi, doc.jcas);
						//SerializationUtils.serialize(Serialization.serializeCASComplete(doc.jcas.getCasImpl()), new FileOutputStream(baseFileName + ".CasComplete"));
						//SerializationUtils.serialize(Serialization.serializeCAS(doc.jcas.getCasImpl()), new FileOutputStream(baseFileName + ".Cas"));
						//SerializationUtils.serialize(Serialization.serializeCASMgr(doc.jcas.getCasImpl()), new FileOutputStream(baseFileName + ".CasMgr"));
					}
					catch (IOException e) {
						Files.deleteIfExists(preprocessed.toPath());
						throw e;
					}
					catch (RuntimeException e) {
						Files.deleteIfExists(preprocessed.toPath());
						throw e;
					}
				}
				if (types.specs != null) {
					doc.filterBySpecs(types);
				}
			}
			
			doc.loadSignals(perceptron, types);

			return doc;
		//} catch (UimaUtilsException e) {
		//	throw new IOException(e);
		} catch (AnalysisEngineProcessException e) {
			throw new IOException(e);
		}
	}
	
	private void filterBySpecs(TypesContainer types) {
		this.aceAnnotations.filterBySpecs(types);
		for (Sentence sent : sentences) {
			sent.filterBySpecs(types);
		}
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		
		CASCompleteSerializer ser = Serialization.serializeCASComplete(this.jcas.getCasImpl());
		out.writeObject(ser);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		// Must explicitly create an empty JCas, to deserialize into
		createJCas();
		CASCompleteSerializer ser = (CASCompleteSerializer) in.readObject();
		Serialization.deserializeCASComplete(ser, this.jcas.getCasImpl());
		
		// Workaround - as I have discussed with Richard Eckart de Castilho on May 20, 2014 in dev@uima.apache.org:
		// For some reason the newly created jcas has a CAS with a svd.basCAS different from it (from the jcas's CAS).
		// This has a very weird side effect - when calling ll_getFSForRef(addr) (in our case, in
		// SentenceInstance.getTokenAnnotation), the returned FeatureStructure is of type AnnotationImpl and
		// not Annotation, which have distinct inheritance hierarchies and therfore casting to Token throws
		// a ClassCastException. However, if just once you call jcas.getCas().getJCas(), then the jcas becomes "fixed".
		// Pretty weird.
		try {
			this.jcas.getCas().getJCas();
		} catch (CASException e) {
			throw new IOException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void readObjectNoData() throws ObjectStreamException {
		throw new StreamCorruptedException("I got to readObjectNoData(), but this should never be called! Something went wrong with deserializing.");
	}
	
	/**
	 *  remove all XML markups associated with multiple lines. 
	 *  e.g. <Quote xxxx
	 *   .....
	 *   />
	 * @param fileTextWithXML
	 * @return
	 */
	static String eraseXML (String fileTextWithXML) 
	{
		boolean inTag = false;
		int length = fileTextWithXML.length();
		StringBuffer fileText = new StringBuffer();
		for (int i=0; i<length; i++) 
		{
			char c = fileTextWithXML.charAt(i);
			if(c == '<')
			{
				inTag = true;
			}
			if (!inTag) 
			{
				fileText.append(c);
			}
			if(c == '>')
			{	
				inTag = false;
			}
		}
		return fileText.toString();
	}
	
	public static class TextSegment
	{
		public String tag;
		public String text;
		
		public boolean hasTag()
		{
			if(tag == null || tag.equals(""))
			{
				return false;
			}
			return true;
		}
		
		public TextSegment(String tag, String text)
		{
			this.tag = tag;
			this.text = text;
		}
		
		public TextSegment(String rawText)
		{
			Pattern pattern = Pattern.compile("<([^<]+)>([^<]+)</[^<]+>([.\\n]*)");
			Matcher matcher = pattern.matcher(rawText);
			if(matcher.find())
			{
				this.tag = matcher.group(1);
				this.text = matcher.group(2) + matcher.group(3);
			}
			else
			{
				this.tag = null;
				this.text = rawText;
			}
		}
	}
	
	/**
	 * read txt document, do POS tagging, chunking, parsing
	 * @param txtFile
	 * @throws IOException
	 */
	public void readDoc(File txtFile, boolean monoCase, JCas existingJcas) throws IOException
	{
		// read text from the original data
		List<TextSegment> segmnets = getSegments(txtFile);
		
		// do sentence split and tokenization
		Span[] sentSpans = null;
		sentSpans = splitSents(segmnets);
		
		// cure errors in sentence detection with the help of Ace annotation
		for(Span sentSpan : sentSpans)
		{
			// set the offset of sentence as the absolute offset, as consistent with offsets in APF 
			sentSpan.setStart(sentSpan.start() + this.textoffset);
			sentSpan.setEnd(sentSpan.end() + this.textoffset);
		}
		if(hasLabel())
		{
			sentSpans = fixSentBoundaries(sentSpans);
		}
		
		// Build JCas
		if (existingJcas != null) {
			jcas = existingJcas;			
		}
		else {
			createJCas();
			jcas.setDocumentText(allText);
		}
		
		int sentID = 0;
		for(Span sentSpan : sentSpans)
		{	
			String sentText = sentSpan.getCoveredText(allText).toString();
			edu.cuny.qc.util.Span[] tokenSpans = TokenizerWrapper.getTokenizer().tokenizeSpan(sentText);
			for(int idx=0; idx < tokenSpans.length; idx++)
			{
				// record offset for each token/sentence
				Span tokenSpan = tokenSpans[idx];
				// calculate absolute offset for tokens
				int offset = sentSpan.start();
				int absoluteStart = offset + tokenSpan.start();
				int absoluteEnd = offset + tokenSpan.end();
				Span absoluteTokenSpan = new Span(absoluteStart, absoluteEnd);
				tokenSpans[idx] = absoluteTokenSpan;
			}
			// fix tokenization error, e.g. split anti-war to three words
			tokenSpans = fixTokenBoudaries(tokenSpans, allText);
			Sentence sent = new Sentence(this, sentID++, sentText);
			sent.put(Sent_Attribute.TOKEN_SPANS, tokenSpans);
			
			de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence sentAnno = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence(jcas, sentSpan.start(), sentSpan.end()+1);
			sentAnno.addToIndexes();
			sent.put(Sent_Attribute.SentenceAnnotation, sentAnno.getAddress());
			
			String[] tokens = new String[tokenSpans.length];
			List<Integer> tokenAddrs = new ArrayList<Integer>(tokenSpans.length);
			for(int idx=0; idx < tokenSpans.length; idx++)
			{
				// get tokens
				Span tokenSpan = tokenSpans[idx];
				tokens[idx] = tokenSpan.getCoveredText(allText).toString();
				
				// Fill JCas
				Token tokenAnno = new Token(jcas, tokenSpan.start(), tokenSpan.end()+1);
				tokenAnno.addToIndexes();
				tokenAddrs.add(tokenAnno.getAddress());
			}
			
			sent.put(Sent_Attribute.TokenAnnotations, tokenAddrs);

			sent.put(Sent_Attribute.TOKENS, tokens);
			// save span of the sent
			sent.setExtent(sentSpan);
			List<Map<Class<?>, Object>> tokenFeatureMaps = new ArrayList<Map<Class<?>, Object>>();
			sent.put(Sent_Attribute.Token_FEATURE_MAPs, tokenFeatureMaps);
			this.sentences.add(sent);
			
		}
	}

	private void createJCas() throws IOException {
		try {
			//AnalysisEngine ae = UimaUtils.loadAE(AE_FILE_PATH);
			jcas = ae.newJCas();
			jcas.setDocumentLanguage("EN");
			//jcas.setDocumentText(allText);
		}
		//catch (UimaUtilsException e) {
		//	throw new IOException(e); 
		//}
		catch (ResourceInitializationException e) {
			throw new IOException(e); 
		}
	}
	
	private Span[] splitSents(List<TextSegment> segments) throws InvalidFormatException, IOException
	{
		final String tagWhiteList = "SUBJECT";
		
		this.text = "";
		this.allText = "";
		List<Span> ret = new ArrayList<Span>();
		int offset = 0;
		for(TextSegment sgm : segments)
		{
			// ignore the sents that has tags
			if(!sgm.hasTag() || sgm.tag.matches(tagWhiteList))
			{
				Span[] sentSpans = null;
				if(monoCase)
				{
					sentSpans = SentDetectorWrapper.getSentDetector().detectPosMonocase(sgm.text);
				}
				else
				{
					sentSpans = SentDetectorWrapper.getSentDetector().detectPos(sgm.text);
				}
				for(Span sent : sentSpans)
				{
					sent.setStart(sent.start() + offset);
					sent.setEnd(sent.end() + offset);
					ret.add(sent);
				}
			}
			offset += sgm.text.length();
			this.text += sgm.text;
		}
		
		allText = before_text + text;
		
		return ret.toArray(new Span[ret.size()]);
	}

	protected List<TextSegment> getSegments(File txtFile) throws IOException
	{
		List<TextSegment> segments = new ArrayList<TextSegment>();
		BufferedReader reader = new BufferedReader(new FileReader(txtFile));
		String line = ""; // buffer for each line
		String buffer = ""; // buffer of segment
		this.headline = "";
		boolean isText = false;
		boolean isHeadLine = false;
		before_text = "";
		boolean end_of_beforeText = false;
		while((line = reader.readLine()) != null)
		{	
			String textline = line.replaceAll("</?[^<]+>", "");	// remove xml markups
			
			if(!end_of_beforeText)
			{
				before_text += textline + "\n";
			}
			
			if(line.equals("<HEADLINE>"))
			{
				isHeadLine = true;
				continue;
			}
			else if(line.equals("</HEADLINE>"))
			{
				isHeadLine = false;
				continue;
			}
			else if(line.equals("<TEXT>"))
			{
				isText = true;
				this.textoffset = before_text.length();
				end_of_beforeText = true;
				continue;
			}
			else if(line.equals("</TEXT>"))
			{
				isText = false;
				continue;
			}
			if(isHeadLine)
			{
				headline += line;
			}
			else if(isText)
			{
				line = line + "\n";
				if(line.matches("<([^<]+)>([^<]+)</[^<]+>\\n"))
				{
					if(buffer.length() > 0)
					{
						TextSegment sgm = new TextSegment(null, buffer);
						segments.add(sgm);
						buffer = "";
					}
					TextSegment sgm = new TextSegment(line);
					segments.add(sgm);
				}
				else
				{
					buffer += line;
				}
			}
		}
		
		if(buffer.length() > 0)
		{
			TextSegment sgm = new TextSegment(null, buffer);
			segments.add(sgm);
		}
		
		for(TextSegment sgm : segments)
		{
			sgm.text = eraseXML(sgm.text);
		}
		
		reader.close();
		return segments;
	}
	
	/**
	 * Use ace annotations to fix tokenization boundaris
	 * e.g. if there is a trigger "war", while the token is "post-war", then split it
	 * @param tokenSpans
	 * @return
	 */
	private Span[] fixTokenBoudaries(Span[] tokenSpans, String text)
	{
		List<Span> ret = new ArrayList<Span>();
		for(int i=0; i<tokenSpans.length; i++)
		{
			Span token = tokenSpans[i];
			boolean flag = false;
			for(AceMention mention : this.aceAnnotations.allMentionsList)
			{
				if(flag)
				{
					break;
				}
				
				Span extent = null;
				String headText = null;
				if(mention instanceof AceEventMention)
				{
					AceEventMention event = (AceEventMention) mention;
					extent = event.anchorExtent;
					headText = event.anchorText;
				}
				else if(mention instanceof AceEntityMention)
				{
					AceEntityMention entity = (AceEntityMention) mention;
					extent = entity.head;
					headText = entity.head.getCoveredText(text);
				}
				if(extent != null && extent.smallerThan(token))
				{
					String tokenText = token.getCoveredText(text);
					if(tokenText.contains(headText + "-") || tokenText.contains("-" + headText))
					{
						String subTokens[] = tokenText.split("-");
						int start = token.start();
						for(int k=0; k<subTokens.length; k++)
						{
							String subToken = subTokens[k];
							if(subToken.length() > 0)
							{
								Span token_1 = new Span(start, start + subToken.length() - 1);
								start = token_1.end() + 1;
								ret.add(token_1);
							}
							if(k < subTokens.length - 1)
							{
								ret.add(new Span(start, start));
							}
							start++;
						}
						flag = true;
					}
					else if(tokenText.contains(headText + "/") || tokenText.contains("/" + headText))
					{
						String subTokens[] = tokenText.split("/");
						int start = token.start();
						for(int k=0; k<subTokens.length; k++)
						{
							String subToken = subTokens[k];
							if(subToken.length() > 0)
							{
								Span token_1 = new Span(start, start + subToken.length() - 1);
								start = token_1.end() + 1;
								ret.add(token_1);
							}
							if(k < subTokens.length - 1)
							{
								ret.add(new Span(start, start));
							}
							start++;
						}
						flag = true;
					}
					else if(!tokenText.equalsIgnoreCase("its"))
					{	
						
						System.err.print(tokenText + "\t--->\t" + headText);
						
						// just breakdown the token into 2 or 3 pieces
						Span token_1 = new Span(token.start(), extent.start()-1);
						Span token_2 = new Span(extent.start(), extent.end());
						Span token_3 = new Span(extent.end()+1, token.end());
						if(token_1.start() <= token_1.end())
						{
							ret.add(token_1);
						}
						if(token_2.start() <= token_2.end())
						{
							ret.add(token_2);
						}
						if(token_3.start() <= token_3.end())
						{
							ret.add(token_3);
						}
						flag = true;
					}
				}
			}
			if(flag == false)
			{
				ret.add(token);
			}
			
		}
	
		return ret.toArray(new Span[ret.size()]);
	}
	
	/**
	 * use Ace annotations to fix sentence boundaries
	 * the idea is: whennever there is an ace entity across two sents, merge the two sents 
	 * @param sentSpans
	 */
	private Span[] fixSentBoundaries(Span[] sentSpans)
	{
		if(sentSpans.length <= 1)
		{
			return sentSpans;
		}
		List<Span> ret = new ArrayList<Span>();
		for(int i=0; i<sentSpans.length - 1; i++)
		{
			Span sent_before = sentSpans[i];
			Span sent_after = sentSpans[i+1];
			for(AceMention mention : this.aceAnnotations.allMentionsList)
			{
				Span extent = mention.extent;
				if(extent.overlap(sent_before) && extent.overlap(sent_after))
				{
					// merge two sents
					Span new_sent = new Span(sent_before.start(), sent_after.end());
					sentSpans[i+1] = new_sent;
					sentSpans[i] = null;
					break;
				}
			}
			if(sentSpans[i] != null)
			{
				ret.add(sent_before);
			}
		}
		ret.add(sentSpans[sentSpans.length-1]);
		return ret.toArray(new Span[ret.size()]);
	}

//	public void printDocBasic(PrintStream out)
//	{
//		out.println(headline);
//		out.println("Text offset: " + this.textoffset);
//		
//		for(int i=0; i<this.sentences.size(); i++)
//		{
//			Sentence sent = this.sentences.get(i);
//			out.println("Sent num:\t" + i);
//			sent.printBasicSent(out);
//		}
//	}
	
	public List<Sentence> getSentences() 
	{
		return this.sentences;
	}

	public boolean hasLabel() 
	{
		return this.isHasLabel();
	}
	
	protected void setAceAnnotations(AceDocument aceAnnotations)
	{
		this.aceAnnotations = aceAnnotations;
	}

	public AceDocument getAceAnnotations()
	{
		return aceAnnotations;
	}
	
	protected void setHasLabel(boolean hasLabel)
	{
		this.hasLabel = hasLabel;
	}

	public boolean isHasLabel()
	{
		return hasLabel;
	}

	public void dumpSignals(List<SentenceInstance> instances, TypesContainer types, Perceptron perceptron) throws IOException {
		// 2.6.14: Now BundledSignals() is built lazily before.
//		if (signals == null) {
//			Map<Integer, List<Map<String, Map<String, SignalInstance>>>> triggerSignals = new HashMap<Integer, List<Map<String, Map<String, SignalInstance>>>>(sentences.size());
//			Map<Integer, List<Map<String, List<Map<String, Map<String, SignalInstance>>>>>> argSignals = new HashMap<Integer, List<Map<String, List<Map<String, Map<String, SignalInstance>>>>>>(sentences.size());
//			// sentences = null; //Ofer: Why the hell was this here???
//			for (SentenceInstance inst : instances) {
//				triggerSignals.put(/*inst.sentInstID*/inst.sentID, (List<Map<String, Map<String, SignalInstance>>>) inst.get(InstanceAnnotations.NodeTextSignalsBySpec));
//				argSignals.put(/*inst.sentInstID*/inst.sentID, (List<Map<String, List<Map<String, Map<String, SignalInstance>>>>>) inst.get(InstanceAnnotations.EdgeTextSignals));
//			}
//			signals = new BundledSignals(types, perceptron, triggerSignals, argSignals);
//			signalsUpdated = true;
//		}
		
		// These checks are here even though logically they should be when loading the signals
		// But we put them here since they must be performed only after we have all of the SentenceInstances
		// 1.6.14: I actually can't do these checks at all -
		// That's because sometimes event-less sentences are skipped (specifically - when the sentence is
		// loaded in training), and sometimes not (specifically, when sentence is loaded in dev).
		// So we can't compare sizes, they might be legitimately different. Bummer.
		// 1.6.14: And note that even though I thought an hour ago that I can't do these checks since sometimes
		// some sentences are skipped and sometimes not - well now, all sentence are always *constructed*, it's just
		// that sometimes they are not *used*. But all their signals would always be dumped, so it's fine.
		// 2.6.14: And now they're back here, but over Sentences, not SentenceInstances.
		// This way we check not just loaded signals, but also constructed ones. 
		if (signals.triggerSignals.size() != sentences.size()) {
			throw new IllegalStateException(String.format("Document %s has %s (non-skipped) sentences, but trigger signals are for %s sentences", docID, sentences.size(), signals.triggerSignals.size()));
		}
		if (signals.argSignals.size() != sentences.size()) {
			throw new IllegalStateException(String.format("Document %s has %s (non-skipped) sentences, but arg signals re for %s sentences", docID, sentences.size(), signals.argSignals.size()));
		}

		if (signals == null	) {
			throw new IllegalStateException("About to dump signals after finishing all SentenceInstances, but there is no BundledSignals in document: " + docID);
		}
		
		if (signalsUpdated) {
			File signalsFile = new File(docID + signalsFileExt);
			try {
				OutputStream out = new BZip2CompressorOutputStream(new FileOutputStream(signalsFile));
				SerializationUtils.serialize(signals, out);
				out.close();
			}
			catch (IOException e) {
				Files.deleteIfExists(signalsFile.toPath());
				throw e;
			}
			catch (RuntimeException e) {
				Files.deleteIfExists(signalsFile.toPath());
				throw e;
			}
		}
		else {
			System.out.printf("Not dumping signals, already had them in full: Document %s\n", docID);
		}
	}
	
	public void loadSignals(Perceptron perceptron, TypesContainer types) throws IOException {
		//List<List<Map<String, Map<String, SignalInstance>>>> triggerSignals = new ArrayList<List<Map<String, Map<String, SignalInstance>>>>();
		//List<List<Map<String, Map<String, Map<String, SignalInstance>>>>> argSignals = new ArrayList<List<Map<String, Map<String, Map<String, SignalInstance>>>>>();
		// read file 
		File signalsFile = new File(docID + signalsFileExt);

		// 22.5.14 Kludge - not loading signals, due to some weird ClassCastException
		if (signalsFile.isFile() /* && false */) {
			InputStream in = new BZip2CompressorInputStream(new FileInputStream(signalsFile));
			signals = (BundledSignals) SerializationUtils.deserialize(in);
			in.close();
		}
		
		if (signals != null) {
			perceptron.triggerSignalNames = signals.triggerSignalNames;
			perceptron.argumentSignalNames = signals.argumentSignalNames;
			
			// We can't have the check of the number of sentences be here, as we should check the number of SentenceInstances, which we don't have here yet
			// we only have the number of Sentences, but many of them may be later skipped and thus should be ignored)
			// Instead, we'll check it when dumping the signal
			// 2.6.14: Nope, sentences are not skipped anymore. And signals are by Sentence, not by SentenceInstance.
			// So these checks return to here.
			// 2.6.14: But actually we want to perform them also if signals were not loaded but constructed. Moving back there.
//			if (signals.triggerSignals.size() != sentences.size()) {
//				throw new IllegalStateException(String.format("Document %s has %s sentences, but trigger signals from file are for %s sentences", docID, sentences.size(), signals.triggerSignals.size()));
//			}
//			if (signals.argSignals.size() != sentences.size()) {
//				throw new IllegalStateException(String.format("Document %s has %s sentences, but arg signals from file are for %s sentences", docID, sentences.size(), signals.argSignals.size()));
//			}
			
			
//			if (!signals.eventEntityTypes.keySet().containsAll(types.eventEntityTypes.keySet())) {
//				throw new IllegalStateException(String.format("Working on these trigger types: %s - but loaded trigger signals of document %s has these trigger types: %s", types.eventEntityTypes, docID, signals.eventEntityTypes.keySet()));
//			}
//			
//			for (String triggerType : types.eventEntityTypes.keySet()) {
//				Set<String> workingEntityTypes = types.eventEntityTypes.get(triggerType);
//				Set<String> loadedEntityTypes = signals.eventEntityTypes.get(triggerType);
//				Set<String> workingRoles = types.argumentRoles.get(triggerType);
//				Set<String> loadedRoles = signals.argumentRoles.get(triggerType);
//				
//				if (!loadedRoles.containsAll(workingRoles)) {
//					throw new IllegalStateException(String.format("For trigger %s, working on these roles: %s - but loaded signals for this trigger type in document %s has these roles: %s", triggerType, workingRoles, docID, loadedRoles));
//				}
//				if (!loadedEntityTypes.containsAll(workingEntityTypes)) {
//					throw new IllegalStateException(String.format("For trigger %s, working on these entity types: %s - but loaded signals for this trigger type in document %s has these entity types: %s", triggerType, workingEntityTypes, docID, loadedEntityTypes));
//				}
//			}
		}
	}

	public List<SentenceInstance> getInstances(Perceptron perceptron, TypesContainer types, Alphabet featureAlphabet, 
			Controller controller, boolean learnable) throws CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, CASException, UimaUtilsException, IOException, AeException
	{		
		List<SentenceInstance> instancelist = new ArrayList<SentenceInstance>();
		for(int sent_id=0 ; sent_id<this.getSentences().size(); sent_id++)
		{
			Sentence sent = this.getSentences().get(sent_id);
			// add all instances
			List<SentenceInstance> insts = Document.getInstancesForSentence(perceptron, sent, types, featureAlphabet, learnable);
			instancelist.addAll(insts);
		}
		return instancelist;
	}
	
	public static List<SentenceInstance> getInstancesForSentence(Perceptron perceptron, Sentence sent, TypesContainer types, Alphabet featureAlphabet, 
			boolean learnable) throws CASRuntimeException, AnalysisEngineProcessException, ResourceInitializationException, CASException, UimaUtilsException, IOException, AeException {
		List<SentenceInstance> result = new ArrayList<SentenceInstance>();
		if (perceptron.controller.oMethod.equalsIgnoreCase("F")) {
			for (int specNum=0; specNum < types.specs.size(); specNum++) {
				JCas spec = types.specs.get(specNum);
				List<JCas> oneSpec = Arrays.asList(new JCas[] {spec});
				TypesContainer oneType = new TypesContainer(oneSpec); 
				result.add(new SentenceInstance(perceptron, sent, oneType, featureAlphabet,
						learnable, spec, specNum));
			}
		}
		else {
			result.add(new SentenceInstance(perceptron, sent, types, featureAlphabet,
					learnable, null, null));
		}
		
		return result;
	}
	

//	static public void main(String[] args) throws IOException
//	{
//		System.out.println("Default Charset=" + Charset.defaultCharset());
//		File txtFile = new File("/Users/XX/Data/ACE/ACE2005-TrainingData-V6.0/English/nw/timex2norm/AFP_ENG_20030417.0004");
//		Document doc = new Document(txtFile.getAbsolutePath(), true, false);
//		TextFeatureGenerator.doPreprocessCheap(doc);
//		doc.printDocBasic(System.out);
//		
//		doc.setSentenceClustersByTokens();
//		doc.printDocCluster(System.out);
//	}

}
