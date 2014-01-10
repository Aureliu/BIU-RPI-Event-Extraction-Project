// -*- tab-width: 4 -*-
package edu.cuny.qc.ace.acetypes;

import java.util.*;
import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

/**
 *  an Ace Event Argument, with information from the ACE key.
 */

public class AceEventArgument implements java.io.Serializable {

	/**
	 *  the role of the argument in the event
	 */
	public String role;
	/**
	 *  the value of the argument:  an AceEntity or AceTimex
	 */
	public AceEventArgumentValue value;
	/**
	 *  our confidence in the presence of this argument
	 */
	public double confidence = 1.0;

	public AceEventArgument (AceEventArgumentValue value, String role) {
		this.value = value;
		this.role = role;
	}

	/**
	 *  create an AceEventArgument from the information in the APF file.
	 *  @param argumentElement the XML element from the APF file containing
	 *                       information about this argument
	 *  @param acedoc  the AceDocument of which this AceEvent is a part
	 */

	public AceEventArgument (Element argumentElement, AceDocument acedoc) {
			role = argumentElement.getAttribute("ROLE");
			String entityid = argumentElement.getAttribute("REFID");
			value = acedoc.findEntityValueTimex(entityid);
			String confidenceString = argumentElement.getAttribute("p"); // Qi modified
			if(confidenceString !=null && !confidenceString.equals(""))
			{
				confidence = Double.parseDouble(confidenceString);
			}
	}

	public void write (PrintWriter w) {
		w.println ("    <event_argument REFID=\"" + value.id + "\" ROLE=\"" + role + "\" p=\"" + confidence + "\"/>");
	}

	public String toString () {
		return role + ":" + value + ":"+Double.toString(confidence);
	}

	static HashMap validArgumentTable = new HashMap();

	static {
		//  -- person roles --
		validArgumentTable.put("Person:AnyEvent",          "PER");
		validArgumentTable.put("Victim:Injure",            "PER");
		validArgumentTable.put("Victim:Die",               "PER");

		//  -- agentive roles --
		validArgumentTable.put("Buyer:Transfer-Ownership",  "PER  ORG  GPE");
		validArgumentTable.put("Seller:Transfer-Ownership", "PER  ORG  GPE");
		validArgumentTable.put("Beneficiary:Transfer-Ownership", "PER  ORG  GPE");
		validArgumentTable.put("Beneficiary:Transfer-Money", "PER  ORG  GPE");
		validArgumentTable.put("Giver:Transfer-Money",      "PER  ORG  GPE");
		validArgumentTable.put("Recipient:Transfer-Money",  "PER  ORG  GPE");
		validArgumentTable.put("Attacker:Attack",           "PER  ORG  GPE");

		validArgumentTable.put("Defendant:Trial-Hearing",   "PER  ORG  GPE");
		validArgumentTable.put("Defendant:Charge-Indict",   "PER  ORG  GPE");
		validArgumentTable.put("Defendant:Sue",             "PER  ORG  GPE");
		validArgumentTable.put("Defendant:Convict",         "PER  ORG  GPE");
		validArgumentTable.put("Defendant:Sentence",        "PER  ORG  GPE");
		validArgumentTable.put("Defendant:Acquit",          "PER  ORG  GPE");
		validArgumentTable.put("Defendant:Pardon",          "PER  ORG  GPE");
		validArgumentTable.put("Defendant:Appeal",          "PER  ORG  GPE");

		validArgumentTable.put("Adjudicator:Trial-Hearing", "PER  ORG  GPE");
		validArgumentTable.put("Adjudicator:Charge-Indict", "PER  ORG  GPE");
		validArgumentTable.put("Adjudicator:Sue",           "PER  ORG  GPE");
		validArgumentTable.put("Adjudicator:Convict",       "PER  ORG  GPE");
		validArgumentTable.put("Adjudicator:Sentence",      "PER  ORG  GPE");
		validArgumentTable.put("Adjudicator:Fine",          "PER  ORG  GPE");
		validArgumentTable.put("Adjudicator:Acquit",        "PER  ORG  GPE");
		validArgumentTable.put("Adjudicator:Pardon",        "PER  ORG  GPE");
		validArgumentTable.put("Adjudicator:Appeal",        "PER  ORG  GPE");

		validArgumentTable.put("Prosecutor:Trial-Hearing",  "PER  ORG  GPE");
		validArgumentTable.put("Prosecutor:Charge-Indict",  "PER  ORG  GPE");
		validArgumentTable.put("Prosecutor:Appeal",         "PER  ORG  GPE");
		validArgumentTable.put("Plaintiff:Sue",             "PER  ORG  GPE");
		validArgumentTable.put("Artifact:Transport",        "PER  WEA   VEH");
		validArgumentTable.put("Artifact:Transfer-Ownership", "VEH  WEA  FAC  ORG");

		validArgumentTable.put("Origin:Transport",      "GPE  LOC  FAC");
		validArgumentTable.put("Origin:Extradite",      "GPE  LOC  FAC");

		validArgumentTable.put("Destination:Transport", "GPE  LOC  FAC");
		validArgumentTable.put("Destination:Extradite", "GPE  LOC  FAC");

		validArgumentTable.put("Org:Start-Org",        "ORG");
		validArgumentTable.put("Org:Merge-Org",        "ORG");
		validArgumentTable.put("Org:End-Org",          "ORG");
		validArgumentTable.put("Org:Declare-Bankruptcy", "ORG  PER  GPE");

		validArgumentTable.put("Agent:Injure",         "PER  ORG  GPE");
		validArgumentTable.put("Agent:Die",            "PER  ORG  GPE");
		validArgumentTable.put("Agent:Transport",      "PER  ORG  GPE");
		validArgumentTable.put("Agent:Start-Org",      "PER  ORG  GPE");
		validArgumentTable.put("Agent:Nominate",       "PER  ORG  GPE  FAC");
		validArgumentTable.put("Agent:Elect",          "PER  ORG  GPE");  // not clear from guidelines
		validArgumentTable.put("Agent:Arrest-Jail",    "PER  ORG  GPE");
		validArgumentTable.put("Agent:Execute",        "PER  ORG  GPE");
		validArgumentTable.put("Agent:Extradite",      "PER  ORG  GPE");

		validArgumentTable.put("Instrument:AnyEvent",  "WEA  VEH");

		validArgumentTable.put("Entity:Demonstrate",   "PER  ORG");
		validArgumentTable.put("Entity:Meet",          "PER  ORG  GPE");
		validArgumentTable.put("Entity:Phone-Write",   "PER  ORG  GPE");
		validArgumentTable.put("Entity:Elect",         "PER  ORG  GPE");
		validArgumentTable.put("Entity:Fine",          "PER  ORG  GPE");
		validArgumentTable.put("Entity:Start-Position","ORG  GPE");
		validArgumentTable.put("Entity:End-Position",  "ORG  GPE");

		validArgumentTable.put("Target:AnyEvent",      "PER  ORG  VEH  FAC  WEA");

		validArgumentTable.put("Vehicle:AnyEvent",     "VEH");

		validArgumentTable.put("Place:AnyEvent",       "GPE  LOC  FAC");

		// -- value roles
		validArgumentTable.put("Crime:AnyEvent",       "CRI");
		validArgumentTable.put("Position:AnyEvent",    "JOB");
		validArgumentTable.put("Sentence:AnyEvent",    "SEN");
		validArgumentTable.put("Money:AnyEvent",       "NUM");
		validArgumentTable.put("Price:AnyEvent",       "NUM");

		//  -- time roles
		validArgumentTable.put("Time-Within:AnyEvent",   "TIM");
		validArgumentTable.put("Time-Starting:AnyEvent", "TIM");
		validArgumentTable.put("Time-Ending:AnyEvent",   "TIM");
		validArgumentTable.put("Time-Before:AnyEvent",   "TIM");
		validArgumentTable.put("Time-After:AnyEvent",    "TIM");
		validArgumentTable.put("Time-Holds:AnyEvent",    "TIM");
		validArgumentTable.put("Time-At-Beginning:AnyEvent", "TIM");
		validArgumentTable.put("Time-At-End:AnyEvent",   "TIM");
	}

	static boolean isValid (String eventType, String role, AceMention mention) {
		// get entry in validArgumentTable
		String entry = (String) validArgumentTable.get(role + ":AnyEvent");
		if (entry == null)
			entry = (String) validArgumentTable.get(role + ":" + eventType);
		if (entry == null) {
			// System.out.println ("$$$ Rejecting role " + role + " for " + eventType);
			return false;
		}
		// get type
		String type = mention.getType();
		if (type == null || type.length() < 3) {
			System.err.println ("$$$ Invalid type " + type + " for mention " + mention.getHeadText());
			return false;
		}
		type = type.substring(0,3).toUpperCase();
		boolean valid = entry.indexOf(type) >= 0;
		if (!valid)
			; // System.out.println ("$$$ Rejecting " + mention.getHeadText() + " (of type " + type +
			//                    ") as " + role + " of " + eventType);
		return valid;
	}
}
