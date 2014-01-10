package edu.cuny.qc.perceptron.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cuny.qc.ace.acetypes.AceDocument;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceEventMention;
import edu.cuny.qc.ace.acetypes.AceEventMentionArgument;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.types.Sentence.Sent_Attribute;
import edu.cuny.qc.util.ChunkWrapper;
import edu.cuny.qc.util.POSTaggerWrapperStanford;
import edu.cuny.qc.util.ParserWrapper;
import edu.cuny.qc.util.Span;
import edu.cuny.qc.util.TokenAnnotations;

public class GlarfDocument extends Document
{
	public static final String offTableExt = ".sgm2.sent.off-table";
	public static final String tupleExt = ".sgm2.sent.ns-2005-fast-ace-n-tuple102";
	public static final String offTableExtALT = ".sgm.sent.off-table";
	public static final String tupleExtALT = ".sgm.sent.ns-2005-fast-ace-n-tuple102";
	public static final String sentExt = ".sgm2.sent";
	public static final String sentExtALT = ".sgm.sent";
	
	public List<List<Integer>> offsets;
	public List<Integer> sentOffsets;
	public List<List<String[]>> tuples;
	
	public GlarfDocument(String baseFileName) throws IOException
	{
		hasLabel = true;
		
		docID = baseFileName;
		File txtFile = new File(baseFileName + textFileExt);
		
		// read apf 
		String apfFile = baseFileName + apfFileExt;
		setAceAnnotations(new AceDocument(txtFile.getAbsolutePath(), apfFile));
		
		// read offset table
		File offTableFile = new File(baseFileName + offTableExt);
		if(!offTableFile.exists())
		{
			offTableFile = new File(baseFileName + offTableExtALT);
		}
		readOffTable(offTableFile);
		
		// read tuples from glarf
		File tupleFile = new File(baseFileName + tupleExt);
		if(!tupleFile.exists())
		{
			tupleFile = new File(baseFileName + tupleExtALT);
		}
		readTuples(tupleFile);
		
		// read doc
		sentences = new ArrayList<Sentence>();
		readDoc(txtFile);
	}

	/**
	 * read offset table
	 * @param aceDocument
	 * @throws IOException 
	 */
	protected void readOffTable(File tableFile) throws IOException
	{
		String line = "";
		BufferedReader reader = new BufferedReader(new FileReader(tableFile));
		
		offsets = new ArrayList<List<Integer>>();
		while((line = reader.readLine()) != null)
		{
			line = line.trim();
			if(line.length() == 0)
			{
				continue;
			}
			if(line.startsWith("Tree Number: "))
			{
				int sent_num = Integer.parseInt(line.substring(13));
				for(int i=0; i< sent_num - offsets.size(); i++)
				{
					offsets.add(null);
				}
				List<Integer> sent = new ArrayList<Integer>();
				offsets.add(sent);
				assert(offsets.size() == sent_num + 1);
			}
			else
			{
				// each token
				String[] fields = line.split(": ");
				assert(fields.length ==2);
				int offset = Integer.parseInt(fields[1]);
				List<Integer> sent = offsets.get(offsets.size() - 1);
				sent.add(offset);
			}
		}
		reader.close();
	}

	protected void readDoc(File txtFile) throws IOException
	{
		// read text from the original data
		BufferedReader reader = new BufferedReader(new FileReader(txtFile));
		String line = ""; // buffer for each line
		this.headline = "";
		this.text = "";
		this.allText = "";
		boolean isText = false;
		boolean isHeadLine = false;
		String before_text = "";
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
				text += textline;
				text += "\n";
			}
		}
		if(text.length() > 0)
		{
			text = text.substring(0, text.length()-1);
			text = eraseXML(text);
			allText = before_text + text;
		}
		reader.close();
		
		// do sentence split and tokenization
		Span[] sentSpans = readSentSpans();
		
		int sentID = 0;
		for(Span sentSpan : sentSpans)
		{	
			Span[] tokenSpans = getTokenSpans(sentSpan, sentID);
			String[] tokens = new String[tokenSpans.length];
			for(int idx=0; idx < tokenSpans.length; idx++)
			{
				// get tokens
				Span tokenSpan = tokenSpans[idx];
				tokens[idx] = tokenSpan.getCoveredText(allText).toString();
			}
			
			Sentence sent = new Sentence(this, sentID++);
			sent.put(Sent_Attribute.TOKENS, tokens);
			// save span of the sent
			sent.setExtent(sentSpan);
			// get POS tags
			String[] posTags = POSTaggerWrapperStanford.getPosTagger().posTag(tokens);
			sent.put(Sent_Attribute.POSTAGS, posTags);
//			// get chunks
//			String[] chunks = ChunkWrapper.getChunker().chunk(tokens, posTags);
//			sent.put(Sent_Attribute.CHUNKS, chunks);
//			List<Map<Class<?>, Object>> tokenFeatureMaps = new ArrayList<Map<Class<?>, Object>>();
//			sent.put(Sent_Attribute.Token_FEATURE_MAPs, tokenFeatureMaps);
//			for(int idx=0; idx < tokenSpans.length; idx++)
//			{	
//				HashMap<Class<?>, Object> map = new HashMap<Class<?>, Object>();
//				map.put(TokenAnnotations.TextAnnotation.class, tokens[idx]);
//				map.put(TokenAnnotations.PartOfSpeechAnnotation.class, posTags[idx]);
//				String lemma = ParserWrapper.lemmanize(tokens[idx], posTags[idx]);
//				map.put(TokenAnnotations.LemmaAnnotation.class, lemma);
//				map.put(TokenAnnotations.ChunkingAnnotation.class, chunks[idx]);
//				map.put(TokenAnnotations.SpanAnnotation.class, tokenSpans[idx]);
//				tokenFeatureMaps.add(map);
//			}
			sent.put(Sent_Attribute.TOKEN_SPANS, tokenSpans);
			// fill in ace annotations such as event/relation/entity mentions
			sent.fillAceAnnotaions();
			this.sentences.add(sent);
		}
	}
	
	private Span[] readSentSpans() throws IOException
	{
		File sentFile = new File (docID + sentExt);
		if(!sentFile.exists())
		{
			sentFile = new File(docID + sentExtALT);
		}
		
		List<Span> ret = new ArrayList<Span>();
		String line = "";
		BufferedReader reader = new BufferedReader(new FileReader(sentFile));
		
		while((line = reader.readLine()) != null)
		{
			line = line.trim();
			if(line.length() == 0)
			{
				continue;
			}
			
			String fields[] = line.split("\\s", 2);
			int start = Integer.parseInt(fields[0]);
			int end = start + fields[1].length() - 1;
			ret.add(new Span(start, end));
		}
		reader.close();
		return ret.toArray(new Span[ret.size()]);
	}
	
	/**
	 * read Glarf tuples from glarf tuple 
	 * @param filename
	 * @throws IOException
	 */
	protected void readTuples(File tupleFile) throws IOException
	{
		tuples = new ArrayList<List<String[]>>();
		String line = "";
		BufferedReader reader = new BufferedReader(new FileReader(tupleFile));
		
		while((line = reader.readLine()) != null)
		{
			line = line.trim();
			if(line.length() == 0)
			{
				continue;
			}
			if(line.startsWith(";; Tuples for Tree "))
			{
				List<String[]> sent = new ArrayList<String[]>();
				tuples.add(sent);
			}
			else
			{
				String[] fields = line.split(" \\| ");
				tuples.get(tuples.size()-1).add(fields);
			}
		}
	}
	

	/**
	 * get token Spans
	 * @param sentSpan
	 * @return
	 */
	protected Span[] getTokenSpans(Span sentSpan, int sentID)
	{
		if(offsets.size() <= sentID || offsets.get(sentID) == null)
		{
			// this sent only contains 1 token and no definition in the off-table
			Span[] ret = new Span[1];
			ret[0] = new Span(sentSpan.start(), sentSpan.end());
			return ret;
		}
		List<Integer> list = offsets.get(sentID);
		Span[] ret = new Span[list.size()];
		for(int i=0; i<list.size(); i++)
		{
			int start = list.get(i);
			int end = 0;
			if(i == list.size() - 1)
			{
				end = sentSpan.end();
			}
			else
			{
				end = list.get(i+1) - 1;
				while(end >= start && (allText.charAt(end) == ' ' || allText.charAt(end) == '\n'))
				{
					end --;
				}
			}
			ret[i] = new Span(start, end);
		}
		return ret;
	}

	/**
	 * get sent spans based on offsets
	 * @return
	 */
	private Span[] getSentSpans()
	{
		Span[] spans = new Span[offsets.size()];
		for(int i=0; i<offsets.size(); i++)
		{
			int start = offsets.get(i).get(0);
			int end = 0;
			if(i == offsets.size() - 1)
			{
				end = allText.length()-1;
			}
			else
			{
				end = offsets.get(i+1).get(0) - 1;
			}
			
			spans[i] = new Span(start, end);
		}
		// fix the end offset
		for(Span sent : spans)
		{
			String sentText = allText.substring(sent.start(), sent.end()+1);
			for(int i=sentText.length()-1; i>=0; i--)
			{
				if(sentText.charAt(i) == ' ' || sentText.charAt(i) == '\n')
				{
					sent.setEnd(sent.end() - 1);
				}
				else
				{
					break;
				}
			}
		}
		return spans;
	}
	
	/**
	 * print event trigger/argument related tuple
	 * @param out
	 * @return 
	 */
	public void printEventRelatedTuples(PrintWriter out)
	{
		for(int sent_id=0; sent_id<tuples.size(); sent_id++)
		{	
			out.println("Sentence " + sent_id);
			Sentence sent = getSentences().get(sent_id);
			List<String[]> sentTuples = tuples.get(sent_id);
			for(AceEventMention mention : sent.eventMentions)
			{
				// print event mention
				mention.write(out);
				Span anchor = mention.anchorExtent;
				for(String[] tuple : sentTuples)
				{
					try
					{
						int gov = Integer.parseInt(tuple[6]);
						int dep = Integer.parseInt(tuple[18]);
						// if this tuple is related with the anchor
						if(anchor.contains(gov) || anchor.contains(dep))
						{
							out.println("trigger" + " | " + Arrays.toString(tuple));
						}
						
						for(AceEventMentionArgument arg : mention.arguments)
						{
							AceMention argument = arg.value;
							Span extent = argument.extent;
							if(argument instanceof AceEntityMention)
							{
								AceEntityMention entity = (AceEntityMention) argument;
								extent = entity.head;
							}
							
							if(anchor.contains(gov) && extent.contains(dep))
							{
								out.println("trigger-arg" + " | " + Arrays.toString(tuple));
							}
							else if(anchor.contains(dep) && extent.contains(gov))
							{
								out.println("trigger-arg" + " | " + Arrays.toString(tuple));
							}
						}
					}
					catch(NumberFormatException e)
					{
						;
					}
				}
			}
		}
	}
	
	static public void main(String[] args) throws IOException
	{
		File txtFile = new File("/Users/che/Data/ACE-2005-GLARF-output/cts/fsh_29348");
		GlarfDocument doc = new GlarfDocument(txtFile.getAbsolutePath());
		doc.printEventRelatedTuples(new PrintWriter(System.out));
	}
}
