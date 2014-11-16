// -*- tab-width: 4 -*-
//Title:        JET
//Copyright:    2005
//Author:       Ralph Grishman
//Description:  A Java-based Information Extraction Toolkil
//              (ACE extensions)

package edu.cuny.qc.ace.acetypes;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;

import edu.cuny.qc.util.Span;

public class AceEventMentionArgument implements java.io.Serializable {

	/**
	 *  the role of the argument in the event
	 */
	public String role;
	
	/**
	 *  the value of the argument:  an AceEntityMention or AceTimexMention
	 */
	public AceMention value;
	/**
	 *  our confidence in the presence of this argument
	 */
	public double confidence = 1.0;
	/**
	 *  our confidence in this role assignment for this argument
	 */
	public double roleConfidence = 1.0;

	public AceEventMentionArgument (AceMention value, String role, AceEventMention mention) {
		this.value = value;
		this.role = role;
		this.mention = mention;
	}

	AceEventMention mention;
	/**
	 *  create an AceEventMentionArgument from the information in the APF file.
	 *  @param argumentElement the XML element from the APF file containing
	 *                       information about this argument
	 *  @param acedoc  the AceDocument of which this AceEvent is a part
	 */

	public AceEventMentionArgument (Element argumentElement, AceDocument acedoc) {
			role = argumentElement.getAttribute("ROLE");
			role = TypesContainer.getCanonicalRoleName(role); // Ofer 15.11.2014 - a single "Time" Role! Non of "Time-Within" and friends!
			String mentionid = argumentElement.getAttribute("REFID");
			value = acedoc.findMention(mentionid);
			confidence = 0.0; // Qi: to avoid empty string exception
			if(argumentElement.getAttribute("p") != null && !argumentElement.getAttribute("p").equals(""))
			{
				confidence = Double.parseDouble(argumentElement.getAttribute("p"));
			}
			
			if(argumentElement.getAttribute("pRole") != null && !argumentElement.getAttribute("pRole").equals(""))
			{
				roleConfidence = Double.parseDouble(argumentElement.getAttribute("pRole"));
			}
	}

	/**
	 *  write the APF representation of the event mention argument to <CODE>w</CODE>.
	 */
	 
	public void write (PrintWriter w) {
		w.print  ("      <event_mention_argument REFID=\"" + value.id + "\" ROLE=\"" + role + "\"");
		//if (Ace.writeEventConfidence) {
			//w.format(" p=\"%5.3f\"", confidence);
			//w.format(" pRole=\"%5.3f\"", roleConfidence);
			w.println(">");
		//}
			w.println("      	<extent>");
			AceEntityMention.writeCharseq (w, value.extent, value.text);
			w.println("      	</extent>");
			w.println("      </event_mention_argument>");
	}

	public String toString () {
		return role + ":" + ((value == null) ? "?" : value.getHeadText());
	}

	public boolean equals (Object o) {
		if (!(o instanceof AceEventMentionArgument))
			return false;
		AceEventMentionArgument p = (AceEventMentionArgument) o;
		if (!this.mention.event.subtype.equals(p.mention.event.subtype))
			return false;
		Span sp1 = this.value.extent;
		AceEventArgumentValue arg = p.value.getParent();
		boolean bOffset = false;
		if (arg instanceof AceEntity){
			for (int i=0;i<((AceEntity)arg).mentions.size();i++){
				AceEntityMention m = (AceEntityMention)((AceEntity)arg).mentions.get(i);
				if (sp1.equals(m.extent)){
					bOffset = true;
					break;
				}
			}
		}
		if (arg instanceof AceTimex){
			for (int i=0;i<((AceTimex)arg).mentions.size();i++){
				AceTimexMention m = (AceTimexMention)((AceTimex)arg).mentions.get(i);
				if (sp1.equals(m.extent)){
					bOffset = true;
					break;
				}
			}
		}
		if (arg instanceof AceValue){
			for (int i=0;i<((AceValue)arg).mentions.size();i++){
				AceValueMention m = (AceValueMention)((AceValue)arg).mentions.get(i);
				if (sp1.equals(m.extent)){
					bOffset = true;
					break;
				}
			}
		}
		if (!this.role.equals(p.role))
			return false;
		
		return bOffset;
	}
}
