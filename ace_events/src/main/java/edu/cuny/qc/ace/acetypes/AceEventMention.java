// -*- tab-width: 4 -*-
//Title:        JET
//Copyright:    2005
//Author:       Ralph Grishman
//Description:  A Java-based Information Extraction Toolkil
//              (ACE extensions)

package edu.cuny.qc.ace.acetypes;

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.cuny.qc.util.Span;

/**
 *  an Ace event mention, with information from the ACE key.
 */

public class AceEventMention extends AceMention{

	/**
	 * calculate the indice of each head token accodring to span of 
	 * token in this sentence. For Value/Timex, this corrresponds to extent, for Entity this corresponds to head, and anchor for event
	 * @param tokenSpans
	 */
	public void setHeadIndices(Span[] tokenSpans)
	{
		if(headIndices == null)
		{
			headIndices = new Vector<Integer>();
			int i = 0;
			for(Span tokenSpan : tokenSpans)
			{
				if(this.anchorExtent.overlap(tokenSpan))
				{
					headIndices.add(i);
				}
				i++;
			}
		}
	}
	
	/**
	 * set the indices of event trigger, if the trigger contains multiple words, choose one of them
	 * only keep triggers with nouns, verbs and adjectives
	 * @param tokenSpans
	 * @param posTags
	 */
	public void setHeadIndices(Span[] tokenSpans, String[] posTags)
	{
		if(headIndices == null)
		{
			headIndices = new Vector<Integer>();
			for(int i=0; i<tokenSpans.length; i++)
			{
				Span tokenSpan = tokenSpans[i];
				if(this.anchorExtent.overlap(tokenSpan))
				{
					headIndices.add(i);
				}
			}
			// if event trigger has more than one word, use simple rule to shrink it
			if(headIndices.size() > 1)
			{
				int final_trigger = -1;
				boolean hasNoun = false;
				boolean hasAdj = false;
				for(Integer index : headIndices)
				{
					if(posTags[index].charAt(0) == 'N' && !hasNoun) // noun
					{
						final_trigger = index;
						hasNoun = true;
					}
					if(posTags[index].charAt(0) == 'J' && !hasNoun && !hasAdj)
					{
						final_trigger = index;
						hasAdj = true;
					}
					if(posTags[index].charAt(0) == 'V' && !hasNoun && !hasAdj) // verb
					{						
						final_trigger = index;
					}
				}
				// set the first token as default if no verbs or nouns
				if(final_trigger == -1)
				{
					final_trigger = headIndices.get(0);
				}
				
				headIndices.clear();
				headIndices.add(final_trigger);
			}
		}
	}
	
	/**
	 *  arguments of the event mention (each of type AceEventMentionArgument)
	 */
	public ArrayList<AceEventMentionArgument> arguments =
		new ArrayList<AceEventMentionArgument>();
	
	public Span ldc_extent;
	public String ldc_text;
	/**
	 *  the span of the extent of the event, with start and end positions based
	 *  on Jet offsets (and so including following whitespace).
	 **/
	public Span jetExtent;
	
	
	/**
	 *  the span of the anchor of the event, with start and end positions based
	 *  on the ACE offsets (excluding XML tags).
	 */
	public Span anchorExtent;
	/**
	 *  the span of the anchor of the event, with start and end positions based
	 *  on Jet offsets (and so including following whitespace).
	 **/
	public Span anchorJetExtent;
	/**
	 *  the text of the anchor
	 */
	public String anchorText;
	/**
	 *  our confidence in the presence of this event mention
	 */
	public double confidence = 1.0;

	public AceEvent event;
	
	public AceEventMention (String id, Span anchorExtent, String fileText, List<AceEventMentionArgument> arguments) {
		this.id = id;
		this.arguments = new ArrayList<AceEventMentionArgument>();
		if(arguments != null)
		{
			this.arguments.addAll(arguments);
		}
		this.anchorExtent = anchorExtent;
		this.anchorText = fileText.substring(this.anchorExtent.start(), this.anchorExtent.end()+1);
	}
	
	public AceEventMention (String id, Span jetExtent, Span anchorJetExtent, String fileText) {
		this.id = id;
		this.arguments = new ArrayList<AceEventMentionArgument>();
		this.extent = AceEntityMention.convertSpan(jetExtent, fileText);
		this.jetExtent = jetExtent;
		this.text = fileText.substring(this.extent.start(), this.extent.end()+1);
		this.anchorExtent = AceEntityMention.convertSpan(anchorJetExtent, fileText);
		this.anchorJetExtent = anchorJetExtent;
		this.anchorText = fileText.substring(this.anchorExtent.start(), this.anchorExtent.end()+1);
	}

	/**
	 *  create an AceEventMention from the information in the APF file.
	 *
	 *  @param mentionElement the XML element from the APF file containing
	 *                       information about this mention
	 *  @param acedoc        the AceDocument to which this relation mention
	 *                       belongs
	 */

	public AceEventMention (Element mentionElement, AceDocument acedoc, String fileText) {
		id = mentionElement.getAttribute("ID");
		confidence = 0.0f;	// Qi: to avoid empty string exception
		if(mentionElement.getAttribute("p") != null && !mentionElement.getAttribute("p").equals(""))
		{
			confidence = Double.parseDouble(mentionElement.getAttribute("p"));
		}
		NodeList extents = mentionElement.getElementsByTagName("extent");
		Element extentElement = (Element) extents.item(0);
		extent = AceEntityMention.decodeCharseq(extentElement);
		
		/*NodeList ldc_scope = mentionElement.getElementsByTagName("ldc_scope");
		Element scope = (Element) ldc_scope.item(0);
		ldc_extent= AceEntityMention.decodeCharseq(scope);
		ldc_text = fileText.substring(ldc_extent.start(), ldc_extent.end()+1);*/
		
		jetExtent = AceEntityMention.aceSpanToJetSpan(extent, fileText);
		text = fileText.substring(extent.start(), extent.end()+1);
		// Span jetExtent = AceEntityMention.aceSpanToJetSpan(extent, fileText);
		NodeList anchors = mentionElement.getElementsByTagName("anchor");
		Element anchorElement = (Element) anchors.item(0);
		anchorExtent = AceEntityMention.decodeCharseq(anchorElement);
		anchorText = fileText.substring(this.anchorExtent.start(), this.anchorExtent.end()+1);
		anchorJetExtent = AceEntityMention.aceSpanToJetSpan(anchorExtent, fileText);
		NodeList arguments = mentionElement.getElementsByTagName("event_mention_argument");
		for (int j=0; j<arguments.getLength(); j++) {
			Element argumentElement = (Element) arguments.item(j);
			AceEventMentionArgument argument = new AceEventMentionArgument (argumentElement, acedoc);
			addArgument(argument);
		}
	}

	public void addArgument (AceEventMentionArgument argument) {
		arguments.add(argument);
		argument.mention = this;
	}

	void setId (String id) {
		this.id = id;
	}

	/**
	 *  write the APF representation of the event mention to <CODE>w</CODE>.
	 */
	 
	public void write (PrintWriter w) {
		w.print  ("    <event_mention ID=\"" + id + "\"");
		//w.format(" p=\"%5.3f\"", confidence);
	/*	if (Ace.writeEventConfidence)
			w.format(" p=\"%5.3f\"", confidence);*/
		w.println(">");
		w.println("      <extent>");
		AceEntityMention.writeCharseq (w, extent, text);
		w.println("      </extent>");
		w.println("      <anchor>");
		AceEntityMention.writeCharseq (w, anchorExtent, anchorText);
		w.println("      </anchor>");
		for (int i=0; i<arguments.size(); i++) {
			AceEventMentionArgument argument = (AceEventMentionArgument) arguments.get(i);
			argument.write(w);
		}
		w.println("    </event_mention>");
	}

	public boolean equals (Object o) {
		if (!(o instanceof AceEventMention))
			return false;
		AceEventMention p = (AceEventMention) o;
		if (!this.event.subtype.equals(p.event.subtype))
			return false;
		if (!this.anchorExtent.overlap(p.anchorExtent))
			return false;
		/*if (this.arguments.size()!=p.arguments.size())
			return false;
		for (int i=0;i<this.arguments.size();i++){
			if (!checkArg(this.arguments.get(i),p.arguments))
				return false;
		}*/
		return true;
	}
	
	public boolean checkArg(AceEventMentionArgument arg, ArrayList args){
		for (int i=0;i<args.size();i++){
			if (arg.equals(args.get(i)))
				return true;
		}
		return false;
	}
	
	public String toString () {
		StringBuffer buf = new StringBuffer();
		buf.append(anchorText);
		// buf.append("[" + text + "]"); // display extent
		buf.append("(");
		for (int i=0; i<arguments.size(); i++) {
			if (i > 0) buf.append(", ");
			AceEventMentionArgument argument = (AceEventMentionArgument) arguments.get(i);
			buf.append(argument.toString());
		}
		buf.append(") ");
		return buf.toString();
	}

	@Override
	public AceEventArgumentValue getParent()
	{
		return event;
	}

	@Override
	public String getType()
	{
		return event.type;
	}

	public String getSubType()
	{
		return event.subtype;
	}
}
