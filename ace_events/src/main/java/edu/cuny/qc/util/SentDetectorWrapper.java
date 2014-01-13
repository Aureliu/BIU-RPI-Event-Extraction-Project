package edu.cuny.qc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class SentDetectorWrapper 
{
	public static SentDetectorWrapper sentDetectorWrapper;
	
	public static SentDetectorWrapper getSentDetector() throws InvalidFormatException, IOException
	{
		if(sentDetectorWrapper == null)
		{
			sentDetectorWrapper = new SentDetectorWrapper(new File("src/main/resources/data/en-sent.bin"));
		}
		return sentDetectorWrapper;
	}
	
	SentenceDetectorME sentDetector;
	
	static List<String> abbreviations = new ArrayList<String>();
	  static List<String> monocaseAbbreviations = new ArrayList<String>();

	  static 
	  {// titles
		  abbreviations.add("MT.");
		  abbreviations.add("D.C.");
	  	  abbreviations.add("Adm.");
	  	  abbreviations.add("Brig.");
	  	  abbreviations.add("Capt.");
	  	  abbreviations.add("Cmdr.");
	  	  abbreviations.add("Col.");
	  	  abbreviations.add("Dr.");
	  	  abbreviations.add("Gen.");
	  	  abbreviations.add("Gov.");
	  	  abbreviations.add("Lt.");
	  	  abbreviations.add("Maj.");
	  	  abbreviations.add("Messrs.");
	  	  abbreviations.add("Mr.");
	  abbreviations.add("Mrs.");
	  abbreviations.add("Ms.");
	  abbreviations.add("Prof.");
	  abbreviations.add("Rep.");
	  abbreviations.add("Reps.");
	  abbreviations.add("Rev.");
	  abbreviations.add("Sen.");
	  abbreviations.add("Sens.");
	  abbreviations.add("Sgt.");
	  abbreviations.add("Sr.");
	  abbreviations.add("St.");

	  // abbreviated first names
	  abbreviations.add("Alex.");
	  abbreviations.add("Benj.");
	  abbreviations.add("Chas.");

	  // other abbreviations
	  abbreviations.add("a.k.a.");
	  abbreviations.add("c.f.");
	  abbreviations.add("i.e.");
	  abbreviations.add("vs.");
	  abbreviations.add("v.");
	  
	  abbreviations.add("U.S.");
	  abbreviations.add("U.N.");

	  Iterator<String> it = abbreviations.iterator();
	  while (it.hasNext())
	  {
	  	monocaseAbbreviations.add(((String)it.next()).toLowerCase());
	  }
	}
	
	
	SentDetectorWrapper(File model_File) throws InvalidFormatException, IOException
	{
		InputStream modelIn = new FileInputStream(model_File);
		SentenceModel model = new SentenceModel(modelIn);
		modelIn.close();	
		sentDetector = new SentenceDetectorME(model);
	}
	
	/**
	 * split a text to sentences
	 */
	public String[] detect(String text)
	{
		return sentDetector.sentDetect(text);
	}
	
	public edu.cuny.qc.util.Span[] detectPosMonocase(String text)
	{ 
		for(int i=0; i<monocaseAbbreviations.size(); i++)
		{
			String abbr = monocaseAbbreviations.get(i);
			text = text.replace(" " + abbr, " " + abbreviations.get(i));
			text = text.replace("\n" + abbr, "\n" + abbreviations.get(i));
		}
		return detectPos(text);
	}
	
	/**
	 * split text to sentences with offset information
	 * @param text
	 * @return
	 */
	public edu.cuny.qc.util.Span[] detectPos(String text)
	{
		ArrayList<edu.cuny.qc.util.Span> ret = new ArrayList<edu.cuny.qc.util.Span>();
		
		// devide sentences in block first
		BufferedReader reader = new BufferedReader(new StringReader(text));
		String line = "";
		String buffer = "";
		int offset = 0;
		try
		{
			while((line = reader.readLine()) != null)
			{
				buffer += line + "\n";
				if(line.equals(""))
				{
					segBuffer(ret, buffer, offset);
					offset += buffer.length();
					buffer = "";
				}
			}
			reader.close();
			
			if(!buffer.equals(""))
			{
				segBuffer(ret, buffer, offset);
				offset += buffer.length();
				buffer = "";
			}	
		} 
		catch (IOException e)
		{
			
			e.printStackTrace();
		}
		return ret.toArray(new edu.cuny.qc.util.Span[ret.size()]);
	}

	private void segBuffer(ArrayList<edu.cuny.qc.util.Span> ret, String buffer, int offset)
	{
		Span[] spanBuffer = sentDetector.sentPosDetect(buffer);
		for(int i=0; i<spanBuffer.length; i++)
		{
			Span span = spanBuffer[i];
			edu.cuny.qc.util.Span adjustedSpan = new edu.cuny.qc.util.Span(span.getStart() + offset, span.getEnd() - 1 + offset);
			ret.add(adjustedSpan);
		}
	}
	
	public static void main(String[] args) throws InvalidFormatException, IOException
	{
		String text = "let's show you a little bit mt. everest, the highest summit on the planet.";
		
		String[] sents = SentDetectorWrapper.getSentDetector().detect(text);
		for(String sent : sents)
		{
			System.out.println(sent);
		}
		
		edu.cuny.qc.util.Span[] spans = SentDetectorWrapper.getSentDetector().detectPosMonocase(text);
		
		for(edu.cuny.qc.util.Span span : spans)
		{
			System.out.println(span);
			System.out.println(span.start());
			System.out.println(span.end());
			System.out.println(span.getCoveredText(text));
		}
		
		System.out.println(spans.length);
	}
}
