// -*- tab-width: 4 -*-
//Title:        JET
//Copyright:    2003, 2004, 2005
//Author:       Ralph Grishman
//Description:  A Java-based Information Extraction Toolkil
//              (ACE extensions)

package edu.cuny.qc.ace.acetypes;

import java.util.*;
import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

/**
 *  an Ace Relation, with information from the ACE key.
 */

public class AceRelation extends AceEventArgumentValue{

	/**
	 *  the type of the relation:
	 */
	public String type;
	/**
	 *  the subtype of the relation
	 */
	public String subtype;
	
	/**
	 *  the class of the mention:  explicit or implicit (not for ace2004 et seq.)
	 */
	public String relClass;
	/**
	 *  arg1 of the relation:  an entity
	 */
	public AceEntity arg1;
	/**
	 *  arg2 of the relation:  an entity
	 */
	public AceEntity arg2;
	/**
	 *  a list of the mentions of this relation (each of type AceRelationMention)
	 */
	public List<AceRelationMention> mentions = new ArrayList();

	static HashSet timeRoles = new HashSet();
	static {timeRoles.add("Time-Within");
          timeRoles.add("Time-Starting");
          timeRoles.add("Time-Ending");
          timeRoles.add("Time-Before");
          timeRoles.add("Time-After");
          timeRoles.add("Time-Holds");
          timeRoles.add("Time-At-Beginning");
          timeRoles.add("Time-At-End");
        }

	public AceRelation (String id, String type, String subtype, String relClass,
	    AceEntity arg1, AceEntity arg2) {
		this.id = id;
		this.type = type;
		this.subtype = subtype;
		this.relClass = relClass;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	/**
	 *  create an AceRelation from the information in the APF file.
	 *  @param relationElement the XML element from the APF file containing
	 *                       information about this entity
	 *  @param acedoc  the AceDocument of which this AceRelation is a part
	 */

	public AceRelation (Element relationElement, AceDocument acedoc, String fileText) {
			id = relationElement.getAttribute("ID");
			type = relationElement.getAttribute("TYPE");
			subtype = relationElement.getAttribute("SUBTYPE");
			relClass = relationElement.getAttribute("CLASS");
			// record arguments (prior to 2005)
			if (AceDocument.ace2005) {
				NodeList arguments = relationElement.getElementsByTagName("relation_argument");
				for (int j=0; j<arguments.getLength(); j++) {
					Element argument = (Element) arguments.item(j);
					String entityid = argument.getAttribute("REFID");
					String role = argument.getAttribute("ROLE");
					if (role.equals("Arg-1")) {
						arg1 = acedoc.findEntity(entityid);
					} else if (role.equals("Arg-2")) {
						arg2 = acedoc.findEntity(entityid);
					} else if (timeRoles.contains(role)) {
						// ignore time roles at present
					} else {
						System.err.println ("*** invalid ROLE \"" + role + "\" for relation");
					}
				}
			} else {
			// record arguments (format prior to 2005)
				NodeList arguments = relationElement.getElementsByTagName("rel_entity_arg");
				for (int j=0; j<arguments.getLength(); j++) {
					Element argument = (Element) arguments.item(j);
					String entityid = argument.getAttribute("ENTITYID");
					String argnum = argument.getAttribute("ARGNUM");
					if (argnum.equals("1")) {
						arg1 = acedoc.findEntity(entityid);
					} else if (argnum.equals("2")) {
						arg2 = acedoc.findEntity(entityid);
					} else {
						System.err.println ("*** invalid ARGNUM for relation");
					}
				}
			}
			/*
			NodeList mentionLists = relationElement.getElementsByTagName("relation_mentions");
			if (mentionLists.getLength() != 1) {
				System.out.println ("*** missing or multiple 'relation_mentions' tags");
				return;
			}
			Element mentionList = (Element) mentionLists.item(0);
			NodeList mentionElements = mentionList.getElementsByTagName("relation_mention");
			*/
			NodeList mentionElements = relationElement.getElementsByTagName("relation_mention");
			for (int j=0; j<mentionElements.getLength(); j++) {
				Element mentionElement = (Element) mentionElements.item(j);
				AceRelationMention mention = new AceRelationMention (mentionElement, acedoc, fileText);
				addMention(mention);
			}
	}

	void addMention (AceRelationMention mention) {
		mentions.add(mention);
		mention.relation = this;
	}

	public void write (PrintWriter w) {
		w.print   ("  <relation ID=\"" + id + "\" TYPE=\"" + type + "\"");
		if (subtype != null && !subtype.equals(""))
		  w.print (" SUBTYPE=\"" + subtype + "\"");
		if (!AceDocument.ace2004)
			w.print (" CLASS=\"" + relClass + "\"");
		w.println (">");
		if (AceDocument.ace2005) {
			w.println ("    <relation_argument REFID=\"" + arg1.id + "\" ROLE=\"Arg-1\" />");
			w.println ("    <relation_argument REFID=\"" + arg2.id + "\" ROLE=\"Arg-2\" />");
		} else {
			w.println ("    <rel_entity_arg ENTITYID=\"" + arg1.id + "\" ARGNUM=\"1\" />");
			w.println ("    <rel_entity_arg ENTITYID=\"" + arg2.id + "\" ARGNUM=\"2\" />");
		}
		if (!AceDocument.ace2004) w.println ("    <relation_mentions>");
		for (int i=0; i<mentions.size(); i++) {
			AceRelationMention mention = (AceRelationMention) mentions.get(i);
			mention.write(w);
		}
		if (!AceDocument.ace2004) w.println ("    </relation_mentions>");
		w.println ("  </relation>");
	}

	public String toString () {
		StringBuffer sbuf = new StringBuffer();
		for (int i=0; i<mentions.size(); i++) {
			sbuf.append(mentions.get(i).toString());
			sbuf.append(" ");
		}
		return sbuf.toString();
	}

}
