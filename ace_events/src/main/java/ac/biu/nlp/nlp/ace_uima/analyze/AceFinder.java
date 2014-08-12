package ac.biu.nlp.nlp.ace_uima.analyze;

import java.io.IOException;

import org.apache.commons.collections15.MultiMap;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.uimafit.util.JCasUtil;
import org.xml.sax.SAXException;

import ac.biu.nlp.nlp.ace_uima.AceException;
import ac.biu.nlp.nlp.ace_uima.analyze.TreeFragmentBuilder.TreeFragmentBuilderException;
import ac.biu.nlp.nlp.ace_uima.stats.StatsException;
import ac.biu.nlp.nlp.ace_uima.uima.BasicArgumentMention;
import ac.biu.nlp.nlp.ace_uima.uima.Event;
import ac.biu.nlp.nlp.ace_uima.uima.EventArgument;
import ac.biu.nlp.nlp.ace_uima.uima.EventMention;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionAnchor;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionArgument;
import ac.biu.nlp.nlp.ace_uima.uima.EventMentionExtent;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;
import eu.excitementproject.eop.lap.biu.uima.CasTreeConverterException;

/**
 * Finds sentences that comply to some hard-coded criteria. Started from a copy of {@link AceAnalyzer}.
 * Actually no, it inherits from it.
 * 
 * @author Ofer Bronstein
 * @since August 2013
 */
public class AceFinder extends AceAnalyzer {
	public AceFinder() {}
	
	@Override
	protected void analyzeOneJcas(JCas jcas) throws StatsException, CASException, CasTreeConverterException, UnsupportedPosTagStringException, AceException, TreeAndParentMapException, TreeFragmentBuilderException, PredicateArgumentIdentificationException {
		final int MIN_ARGS = 4;
		
		DocumentMetaData meta = JCasUtil.selectSingle(jcas, DocumentMetaData.class);
		String docId = meta.getDocumentId();
		logger.trace("starting file " + docId);
		
		tokenIndex = JCasUtil.indexCovering(jcas, Token.class, Sentence.class);
				
		
		for (Event event : JCasUtil.select(jcas, Event.class)) {
			String subtype = event.getSUBTYPE();
			logger.trace("- starting " + subtype + " event");

			for (EventMention mention : JCasUtil.select(event.getEventMentions(), EventMention.class)) {
				EventMentionAnchor eventAnchor = mention.getAnchor();
				EventMentionExtent eventExtent = mention.getExtent();
				
				MultiMap<Sentence, Token> sentences = getCoveringSentences(eventExtent, tokenIndex);
				if (sentences.size() == 1 && mention.getEventMentionArguments().size() >= MIN_ARGS) {
					Sentence sentence = sentences.keySet().iterator().next();
					
					// We actually only need the sentence, but this is for convenience - we keep all the indexes valid.
					// Eventually, we will extract only our (modified) sentence.
					StringBuffer out = new StringBuffer(jcas.getDocumentText());
					
					// Mark anchor with *
					out.replace(eventAnchor.getBegin(), eventAnchor.getBegin()+1, "*");
					out.replace(eventAnchor.getEnd()-1, eventAnchor.getEnd(), "*");
					
					for (EventMentionArgument eventArgMention : JCasUtil.select(mention.getEventMentionArguments(), EventMentionArgument.class)) {
						BasicArgumentMention argMention = eventArgMention.getArgMention();
						Annotation argMentionAnno = argMention.getExtent(); // If I will want to use the head and not the extent - just change here (but also remember that only Entity has a head, Value and TimeX2 still need the extent)
						EventArgument eventArg = eventArgMention.getEventArgument();
						
						// Mark each arg
						out.replace(argMentionAnno.getBegin(), argMentionAnno.getBegin()+3, "[" + eventArg.getRole().substring(0, 2).toUpperCase());
						out.replace(argMentionAnno.getEnd()-1, argMentionAnno.getEnd(), "]");
					}

					// Take only the sentence from the entire document (we kept entire document for index-convenience)
					out.delete(0,  sentence.getBegin());
					out.delete(sentence.getCoveredText().length(),  out.length());
					out.insert(0, docId + ": ");
					
					addDetails(subtype, out.toString());
				}

			}
		}
	}
	
	@Override
	protected void writeOutput(String entityStatsOutputPath, String roleStatsOutputFile, String typePerDocOutputFile, String detailedOutputFolderPath) throws IOException {
		dumpDetailedFiles(detailedOutputFolderPath, "\n");
	}

	public static void main(String args[]) throws Exception {
		if (args.length != 2) {
			System.err.println("USAGE: Acefinder <xmi input folder> <detailed output folder>");
			return;
		}
		initLog();
		new AceFinder().analyzeFolder(args[0], null, null, null, args[1]);
	}
	private static Logger logger = Logger.getLogger(AceFinder.class);
}
