package ac.biu.nlp.nlp.ace_uima;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath;

import java.net.URL;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.uimafit.factory.AggregateBuilder;

import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;
import eu.excitementproject.eop.lap.biu.uima.ae.ner.StanfordNamedEntityRecognizerAE;
import eu.excitementproject.eop.lap.biu.uima.ae.parser.EasyFirstParserAE;
import eu.excitementproject.eop.lap.biu.uima.ae.postagger.MaxentPosTaggerAE;
import eu.excitementproject.eop.lap.biu.uima.ae.sentencesplitter.NagelSentenceSplitterAE;
import eu.excitementproject.eop.lap.biu.uima.ae.tokenizer.MaxentTokenizerAE;

/**
 * Creates an analysis engine that annotated both ACE info and NLP info.
 * NLP info annotating is inspired by {@link eu.excitementproject.eop.lap.biu.uima.BIUFullLAP}.
 * 
 * @author Ofer Bronstein
 * @since July 2013
 */
public class AceFullAEFactory {

	public static AnalysisEngine create(
			//String tsDescriptorPath, String existingTypeName,
			String taggerModelFile, String nerModelFile,
			String parserHost, Integer parserPort)
					throws ResourceInitializationException, InvalidXMLException, UimaUtilsException {
		
		// ACE annotation
		AnalysisEngineDescription ace =        createPrimitiveDescription(AceAnnotator.class);

		// NLP annotation
//		AnalysisEngineDescription splitter =   createPrimitiveDescription(NagelSentenceSplitterAE.class);
//		AnalysisEngineDescription tokenizer =  createPrimitiveDescription(MaxentTokenizerAE.class);
		AnalysisEngineDescription aceSplitterTokenizer = 
											   createPrimitiveDescription(AceSentenceSplitterAndTokenizer.class);
		AnalysisEngineDescription tagger =     createPrimitiveDescription(MaxentPosTaggerAE.class,
				MaxentPosTaggerAE.PARAM_MODEL_FILE , taggerModelFile);
		AnalysisEngineDescription ner =        createPrimitiveDescription(StanfordNamedEntityRecognizerAE.class,
				MaxentPosTaggerAE.PARAM_MODEL_FILE , nerModelFile);
		AnalysisEngineDescription parser =     createPrimitiveDescription(EasyFirstParserAE.class,
				EasyFirstParserAE.PARAM_HOST , parserHost,
				EasyFirstParserAE.PARAM_PORT , parserPort
				);
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(ace);
		builder.add(aceSplitterTokenizer);
		builder.add(tagger); 
		builder.add(ner); 
		builder.add(parser); 
		
		return builder.createAggregate();
	}
	
	public static AnalysisEngine create() throws ResourceInitializationException, InvalidXMLException, UimaUtilsException {
		return create(  
				"../third-party/stanford-postagger-full-2008-09-28/models/bidirectional-wsj-0-18.tagger",
				"../third-party/stanford-ner-2009-01-16/classifiers/ner-eng-ie.crf-3-all2008-distsim.ser.gz",
				"localhost",
				8080);
	}
}
