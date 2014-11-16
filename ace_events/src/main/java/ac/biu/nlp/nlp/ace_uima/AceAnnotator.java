package ac.biu.nlp.nlp.ace_uima;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.FSCollectionFactory;
import org.uimafit.util.JCasUtil;
import org.xml.sax.SAXException;

import ac.biu.nlp.nlp.ace_uima.jaxb.ApfSourceFileType;
import ac.biu.nlp.nlp.ace_uima.jaxb.ArgumentMentionType;
import ac.biu.nlp.nlp.ace_uima.jaxb.ArgumentType;
import ac.biu.nlp.nlp.ace_uima.jaxb.EntityMentionType;
import ac.biu.nlp.nlp.ace_uima.jaxb.EntityType;
import ac.biu.nlp.nlp.ace_uima.jaxb.EventMentionType;
import ac.biu.nlp.nlp.ace_uima.jaxb.EventType;
import ac.biu.nlp.nlp.ace_uima.jaxb.ExtentType;
import ac.biu.nlp.nlp.ace_uima.jaxb.Timex2MentionType;
import ac.biu.nlp.nlp.ace_uima.jaxb.Timex2Type;
import ac.biu.nlp.nlp.ace_uima.jaxb.ValueMentionType;
import ac.biu.nlp.nlp.ace_uima.jaxb.ValueType;
import ac.biu.nlp.nlp.ace_uima.uima.BasicArgument;
import ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention;
import ac.biu.nlp.nlp.ace_uima.uima.Entity;
import ac.biu.nlp.nlp.ace_uima.uima.EntityMention;
import ac.biu.nlp.nlp.ace_uima.uima.EntityMentionExtent;
import ac.biu.nlp.nlp.ace_uima.uima.EntityMentionHead;
import ac.biu.nlp.nlp.ace_uima.uima.Event;
import ac.biu.nlp.nlp.ace_uima.uima.EventArgument;
import ac.biu.nlp.nlp.ace_uima.uima.EventMention;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionAnchor;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionExtent;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionLdcScope;
import ac.biu.nlp.nlp.ace_uima.uima.Timex2;
import ac.biu.nlp.nlp.ace_uima.uima.Timex2Mention;
import ac.biu.nlp.nlp.ace_uima.uima.Timex2MentionExtent;
import ac.biu.nlp.nlp.ace_uima.uima.Value;
import ac.biu.nlp.nlp.ace_uima.uima.ValueMention;
import ac.biu.nlp.nlp.ace_uima.uima.ValueMentionExtent;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

public class AceAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		DocumentMetaData meta = JCasUtil.selectSingle(jCas, DocumentMetaData.class);
		String docId = meta.getDocumentId();
		String sgmPath = meta.getDocumentUri();
		try {
			File apfXmlFile = new File(sgmPath.replaceFirst(".sgm", ".apf.xml"));
			if (!apfXmlFile.isFile()) {
				throw new AceException(String.format("Got .sgm file with no corresponding .apf.xml file: %s", sgmPath));
			}
			ApfSourceFileType apfXml = new ApfXmlFileReader().fromApfXmlFile(apfXmlFile.getAbsolutePath());
			
			Map<String, BasicArgument> idToArg = new HashMap<String, BasicArgument>();
			Map<String, BasicArgumentMention> idToArgMention = new HashMap<String, BasicArgumentMention>();			
			Map<BasicArgument, List<EventArgument>> argToEventArgs = new HashMap<BasicArgument, List<EventArgument>>();			
			Map<BasicArgumentMention, List<EventMentionArgument>> argMentionToEventMentionArgs = new HashMap<BasicArgumentMention, List<EventMentionArgument>>();			
			
			for (EntityType entity : apfXml.getDocument().getEntity()) {
				Entity entityAnno = new Entity(jCas);
				entityAnno.addToIndexes();
				entityAnno.setID(entity.getID());
				entityAnno.setTYPE(entity.getTYPE());
				entityAnno.setSUBTYPE(entity.getSUBTYPE());
				entityAnno.setCLASS(entity.getCLASS());
				idToArg.put(entityAnno.getID(), entityAnno);
				argToEventArgs.put(entityAnno, new ArrayList<EventArgument>());
				
				List<EntityMention> mentions = new LinkedList<EntityMention>();
				for (EntityMentionType entityMention : entity.getEntityMention()) {
					EntityMention entityMentionAnno = new EntityMention(jCas);
					entityMentionAnno.addToIndexes();
					entityMentionAnno.setArg(entityAnno);
					entityMentionAnno.setID(entityMention.getID());
					entityMentionAnno.setTYPE(entityMention.getTYPE());
					entityMentionAnno.setLDCTYPE(entityMention.getLDCTYPE());
					entityMentionAnno.setLDCATR(entityMention.getLDCATR());
					
					mentions.add(entityMentionAnno);
					idToArgMention.put(entityMentionAnno.getID(), entityMentionAnno);
					argMentionToEventMentionArgs.put(entityMentionAnno, new ArrayList<EventMentionArgument>());
					
					EntityMentionExtent extentAnno = new EntityMentionExtent(jCas);
					extentAnno.addToIndexes();
					extentAnno.setMention(entityMentionAnno);
					entityMentionAnno.setExtent(extentAnno);
					annotateCharseq(extentAnno, entityMention.getExtent());
					
					EntityMentionHead headAnno = new EntityMentionHead(jCas);
					headAnno.addToIndexes();
					headAnno.setMention(entityMentionAnno);
					entityMentionAnno.setHead(headAnno);
					annotateCharseq(headAnno, entityMention.getHead());
				}
				FSArray mentionsArray = (FSArray) FSCollectionFactory.createFSArray(jCas, mentions);
				entityAnno.setMentions(mentionsArray);
			}
			
			for (ValueType value : apfXml.getDocument().getValue()) {
				Value valueAnno = new Value(jCas);
				valueAnno.addToIndexes();
				valueAnno.setID(value.getID());
				valueAnno.setTYPE(value.getTYPE());
				valueAnno.setSUBTYPE(value.getSUBTYPE());
				idToArg.put(valueAnno.getID(), valueAnno);
				argToEventArgs.put(valueAnno, new ArrayList<EventArgument>());
				
				List<ValueMention> mentions = new LinkedList<ValueMention>();
				ValueMentionType valueMention = value.getValueMention();
				
				ValueMention valueMentionAnno = new ValueMention(jCas);
				valueMentionAnno.addToIndexes();
				valueMentionAnno.setArg(valueAnno);
				valueMentionAnno.setID(valueMention.getID());
				
				mentions.add(valueMentionAnno);
				idToArgMention.put(valueMentionAnno.getID(), valueMentionAnno);
				argMentionToEventMentionArgs.put(valueMentionAnno, new ArrayList<EventMentionArgument>());
				
				ValueMentionExtent extentAnno = new ValueMentionExtent(jCas);
				extentAnno.addToIndexes();
				extentAnno.setMention(valueMentionAnno);
				valueMentionAnno.setExtent(extentAnno);
				valueMentionAnno.setHead(null);
				annotateCharseq(extentAnno, valueMention.getExtent());

				FSArray mentionsArray = (FSArray) FSCollectionFactory.createFSArray(jCas, mentions);
				valueAnno.setMentions(mentionsArray);
			}
			
			for (Timex2Type timex2 : apfXml.getDocument().getTimex2()) {
				Timex2 timex2Anno = new Timex2(jCas);
				timex2Anno.addToIndexes();
				timex2Anno.setID(timex2.getID());
				timex2Anno.setVAL(timex2.getVAL());
				timex2Anno.setANCHOR_VAL(timex2.getANCHORVAL());
				timex2Anno.setANCHOR_DIR(timex2.getANCHORDIR());
				timex2Anno.setCOMMENT(timex2.getCOMMENT());
				timex2Anno.setMOD(timex2.getMOD());
				timex2Anno.setNON_SPECIFIC(timex2.getNONSPECIFIC());
				timex2Anno.setSET(timex2.getSET());
				idToArg.put(timex2Anno.getID(), timex2Anno);
				argToEventArgs.put(timex2Anno, new ArrayList<EventArgument>());
				
				List<Timex2Mention> mentions = new LinkedList<Timex2Mention>();
				Timex2MentionType timex2Mention = timex2.getTimex2Mention();
				
				Timex2Mention timex2MentionAnno = new Timex2Mention(jCas);
				timex2MentionAnno.addToIndexes();
				timex2MentionAnno.setArg(timex2Anno);
				timex2MentionAnno.setID(timex2Mention.getID());

				mentions.add(timex2MentionAnno);
				idToArgMention.put(timex2MentionAnno.getID(), timex2MentionAnno);
				argMentionToEventMentionArgs.put(timex2MentionAnno, new ArrayList<EventMentionArgument>());
				
				Timex2MentionExtent extentAnno = new Timex2MentionExtent(jCas);
				extentAnno.addToIndexes();
				extentAnno.setMention(timex2MentionAnno);
				timex2MentionAnno.setExtent(extentAnno);
				timex2MentionAnno.setHead(null);
				annotateCharseq(extentAnno, timex2Mention.getExtent());

				FSArray mentionsArray = (FSArray) FSCollectionFactory.createFSArray(jCas, mentions);
				timex2Anno.setMentions(mentionsArray);
			}

			
			for (EventType event : apfXml.getDocument().getEvent()) {
				Event eventAnno = new Event(jCas);
				eventAnno.addToIndexes();
				eventAnno.setID(event.getID());
				eventAnno.setTYPE(event.getTYPE());
				eventAnno.setSUBTYPE(event.getSUBTYPE());
				eventAnno.setMODALITY(event.getMODALITY());
				eventAnno.setPOLARITY(event.getPOLARITY());
				eventAnno.setGENERICITY(event.getGENERICITY());
				eventAnno.setTENSE(event.getTENSE());
				
				Map<ArgIdAndRole, EventArgument> argIdAndRoleToEventArgument = new HashMap<ArgIdAndRole, EventArgument>();

				List<EventArgument> arguments = new LinkedList<EventArgument>();
				for (ArgumentType eventArg : event.getEventArgument()) {
					EventArgument eventArgAnno = new EventArgument(jCas);
					eventArgAnno.addToIndexes();
					eventArgAnno.setEvent(eventAnno);
					String role = TypesContainer.getCanonicalRoleName(eventArg.getROLE());
					eventArgAnno.setRole(role);
					
					BasicArgument arg = idToArg.get(eventArg.getREFID());
					assertTrue(arg != null, String.format("DocId: %s, Event: %s - Can't find arg %s", docId, event.getID(), eventArg.getREFID()));
					eventArgAnno.setArg(arg);
					
					ArgIdAndRole key = new ArgIdAndRole(arg.getID(), role);
					assertTrue(!argIdAndRoleToEventArgument.containsKey(key),
							String.format("DocId: %s, Event: %s - Arg+Role (%s) appears more than once as an argument for this event", docId, event.getID(), key));
					argIdAndRoleToEventArgument.put(key, eventArgAnno);
					argToEventArgs.get(arg).add(eventArgAnno);

					arguments.add(eventArgAnno);
				}
				FSArray argumentsArray = (FSArray) FSCollectionFactory.createFSArray(jCas, arguments);
				eventAnno.setEventArguments(argumentsArray);

				List<EventMention> mentions = new LinkedList<EventMention>();
				for (EventMentionType eventMention : event.getEventMention()) {
					EventMention eventMentionAnno = new EventMention(jCas);
					eventMentionAnno.addToIndexes();
					eventMentionAnno.setEvent(eventAnno);
					eventMentionAnno.setID(eventMentionAnno.getID());

					mentions.add(eventMentionAnno);
					
					EventMentionExtent extentAnno = new EventMentionExtent(jCas);
					extentAnno.addToIndexes();
					extentAnno.setEventMention(eventMentionAnno);
					eventMentionAnno.setExtent(extentAnno);
					annotateCharseq(extentAnno, eventMention.getExtent());
					
					// this is like a "prefer non-null"
					// this if shouldn't exist (its contents should remain unconditions), when qi's dataset is fixed
					// currently it sometimes doesn't have an ldc scope. Which is a shame.
					if (eventMention.getLdcScope() != null) {
						EventMentionLdcScope ldcAnno = new EventMentionLdcScope(jCas);
						ldcAnno.addToIndexes();
						ldcAnno.setEventMention(eventMentionAnno);
						eventMentionAnno.setLdcScope(ldcAnno);
						annotateCharseq(ldcAnno, eventMention.getLdcScope());
					}
					else {
						warnLog.printf("DocId: %s Event: %s - No LdcScope found\n", docId, event.getID());
					}
					
					EventMentionAnchor anchorAnno = new EventMentionAnchor(jCas);
					anchorAnno.addToIndexes();
					anchorAnno.setEventMention(eventMentionAnno);
					eventMentionAnno.setAnchor(anchorAnno);
					annotateCharseq(anchorAnno, eventMention.getAnchor());

					List<EventMentionArgument> mentionArgs = new LinkedList<EventMentionArgument>();
					for (ArgumentMentionType eventMentionArg : eventMention.getEventMentionArgument()) {
						EventMentionArgument eventMentionArgAnno = new EventMentionArgument(jCas);
						eventMentionArgAnno.addToIndexes();
						eventMentionArgAnno.setEventMention(eventMentionAnno);
						String role = TypesContainer.getCanonicalRoleName(eventMentionArg.getROLE());
						eventMentionArgAnno.setRole(role); //Ofer 11.15.2014 - "Time" issue!
						
						BasicArgumentMention argMention = idToArgMention.get(eventMentionArg.getREFID());
						eventMentionArgAnno.setArgMention(argMention);

						String msg = String.format("DocId: %s Event: %s ArgMention: %s - ", docId, event.getID(), argMention.getID());
						
						String argId = getArgIdByArgMentionId(eventMentionArg.getREFID());
						ArgIdAndRole key = new ArgIdAndRole(argId, role);
						EventArgument eventArg = argIdAndRoleToEventArgument.get(key);
						
						// this is only to "prefer not null"
						// meaning that null should cause an exception (and this "if" should be removed),
						// once all mistakes in Qi's dataset are fixed
						if (eventArg == null) {
							warnLog.printf("%sEvent mention argument with ArgId %s and Role %s doesn't appear as event argument in current event\n", msg, key.argId, key.role);
							continue; // skip this event mention argument, unfortunately...
						}
							
						eventMentionArgAnno.setEventArgument(eventArg);

						argMentionToEventMentionArgs.get(argMention).add(eventMentionArgAnno);

						// Verify same role as event argument
						assertEquals(role, eventArg.getRole(),
								msg + "Event mention argument ROLE (%s) is different than that of event argument (%s)");
						
						// Verify extent is the same is that of linked arg mention
						preferEquals((int) eventMentionArg.getExtent().getCharseq().getSTART(), argMention.getExtent().getBegin(),
								msg + "Event mention argument START (%s) is different than that of arg mention (%s)");
						preferEquals((int) eventMentionArg.getExtent().getCharseq().getEND()+1, argMention.getExtent().getEnd(),
								msg + "Event mention argument END (%s) is different than that of arg mention (%s)");
						preferEquals(escapeApf(eventMentionArg.getExtent().getCharseq().getValue()), argMention.getExtent().getCoveredText(),
								msg + "Event mention argument text (%s) is different than that of arg mention (%s)");
						
						// Verify arg is the same for the event argument and the entity mention
						assertEquals(argMention.getArg(), eventArg.getArg(), msg + "Arg of arg mention (%s) is different than that of event arg (%s)");
						
						mentionArgs.add(eventMentionArgAnno);
					}
					FSArray mentionsArgsArray = (FSArray) FSCollectionFactory.createFSArray(jCas, mentionArgs);
					eventMentionAnno.setEventMentionArguments(mentionsArgsArray);
				}
				FSArray mentionsArray = (FSArray) FSCollectionFactory.createFSArray(jCas, mentions);
				eventAnno.setEventMentions(mentionsArray);
			}
			
			// Some more arrays - need to be at the very end, since they are affected by both args and events
			for (Entry<BasicArgument, List<EventArgument>> entry : argToEventArgs.entrySet()) {
				FSArray argumentsArray = (FSArray) FSCollectionFactory.createFSArray(jCas, entry.getValue());
				entry.getKey().setEventArguments(argumentsArray);
			}
			for (Entry<BasicArgumentMention, List<EventMentionArgument>> entry : argMentionToEventMentionArgs.entrySet()) {
				FSArray mentionArgsArray = (FSArray) FSCollectionFactory.createFSArray(jCas, entry.getValue());
				entry.getKey().setEventMentionArguments(mentionArgsArray);
			}
		}
		catch (Exception e) {
			System.err.println(docId);
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e); 
		}

	}
	
	private void assertEquals(Object o1, Object o2, String msg) throws AceException {
		if (!o1.equals(o2)) {
			throw new AceException(String.format(msg, o1, o2));
		}
	}
	
	private void preferEquals(Object o1, Object o2, String msg) throws AceException {
		if (!o1.equals(o2)) {
			warnLog.printf(msg+"\n", o1, o2);
		}
	}
	
	private void assertTrue(boolean condition, String msg) throws AceException {
		if (!condition) {
			throw new AceException(msg);
		}
	}
	
	private void assertEqualStrings(String expected, String retrieved) throws AceException {
		//TODO remove
		//if (expected.startsWith("Doctors")) {
		//	int u = 9898; //TODO put a breakpoint here! got some encoding problems with alt.vacation.las-vegas_20050109.0133!
		//}
		if (!expected.equals(retrieved)) {
			throw new AceException(String.format("Expected text \"%s\", got text \"%s\"", expected, retrieved));
		}
	}
	
	private void preferEqualStrings(String expected, String retrieved) throws AceException {
		if (!expected.equals(retrieved)) {
			warnLog.printf("Expected text \"%s\", got text \"%s\"\n", expected, retrieved);
		}
	}
	
	//TODO should this method stay???
	private String escapeSgm(String input) {
		String output = input.replace("&amp;", "&");
		return output;
	}
	
	//TODO should this method stay???
	private String escapeApf(String input) {
		String output = input; 
		output = input.replaceAll("\t|\n|\r", " "); //TODO comment out this line if SgmFile doesn't keep \r,\n,\t
		return output;
	}
	
	private void annotateCharseq(Annotation anno, ExtentType extent) throws AceException {
		try { //TODO remove try-catch
			anno.setBegin((int) extent.getCharseq().getSTART());
			anno.setEnd((int) extent.getCharseq().getEND()+1);
			//String escaped = StringEscapeUtils.escapeXml(anno.getCoveredText());
			String escapedSgm = escapeSgm(anno.getCoveredText());
			String escapedApf = escapeApf(extent.getCharseq().getValue());
			preferEqualStrings(escapedApf, escapedSgm);
		}
		catch (AceException e) {
			System.err.printf("Expected '%s'[%d..%d], Got '%s'\n\n", extent.getCharseq().getValue(),
					extent.getCharseq().getSTART(), extent.getCharseq().getEND(), anno.getCoveredText());
			try {
				String docId = JCasUtil.selectSingle(anno.getCAS().getJCas(), DocumentMetaData.class).getDocumentId();
				if (!docId.startsWith("alt.vacation.las-vegas_20050109.0133")) {
					throw e;
				}
			}
			catch (CASException c) {
				throw new AceException("Got CASException, see inner", c);
			}
		}
	}
	
	private String getArgIdByArgMentionId(String argMentionId) throws AceException {
		int index = argMentionId.lastIndexOf("-");
		if (index == -1) {
			throw new AceException("Cannot convert arg mention ID to arg ID - no '-' found: " + argMentionId);
		}
		String result = argMentionId.substring(0, index);
		return result;
	}
	
//	/**
//	 * Returns the type of an argument by its REFID.
//	 * 
//	 * @param refid
//	 * @return
//	 * @throws AceException
//	 * @deprecated this method, although it is completely valid and functional, is not needed
//	 * right now, as at no point does an event care about the type of its arguments.
//	 */
//	@SuppressWarnings("unused")
//	private EventArgumentType getRefType(String refid) throws AceException {
//		Matcher m = REFID_PATTERN.matcher(refid);
//		if (!m.matches()) {
//			throw new AceException("REFID has bad format: " + refid);
//		}
//		String typeIndicator = m.group(1);
//		switch (typeIndicator) {
//			case "E": return EventArgumentType.ENTITY;
//			case "T": return EventArgumentType.TIMEX2;
//			case "V": return EventArgumentType.VALUE;
//			default: throw new AceException("REFID '" + refid + "' has invalid indicator: " + typeIndicator);
//		}
//	}
	
	private class ArgIdAndRole {
		public ArgIdAndRole(String argId, String role) {
			this.argId = argId;
			this.role = role;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((argId == null) ? 0 : argId.hashCode());
			result = prime * result + ((role == null) ? 0 : role.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ArgIdAndRole other = (ArgIdAndRole) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (argId == null) {
				if (other.argId != null)
					return false;
			} else if (!argId.equals(other.argId))
				return false;
			if (role == null) {
				if (other.role != null)
					return false;
			} else if (!role.equals(other.role))
				return false;
			return true;
		}
		private AceAnnotator getOuterType() {
			return AceAnnotator.this;
		}
		public String argId;
		public String role;
	}
	
	private Pattern REFID_PATTERN = Pattern.compile(".+\\-([a-zA-Z])\\d+(?:\\-\\d+)?");
	private static final String WARN_LOG_PATH = "C:\\Temp\\AceAnnotator_WarnLog.log";
	private static PrintStream warnLog;
	
	static {
		try {
			warnLog = new PrintStream(new FileOutputStream(new File(WARN_LOG_PATH), true)); //open existing, append
			warnLog.printf("===\n");
		} catch (FileNotFoundException e) {
			System.err.printf("Could not open AceAnnotator's warn log, in: %s\n", WARN_LOG_PATH);
			e.printStackTrace();
		}
	}
}
