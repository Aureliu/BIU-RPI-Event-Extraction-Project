package ac.biu.nlp.nlp.ie.onthefly.input;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.factory.AggregateBuilder;

import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;

import eu.excitementproject.eop.lap.biu.uima.ae.postagger.MaxentPosTaggerAE;

public class AnalysisEngines {
	public static AnalysisEngine forSpecTokenView() throws AeException {
		try {
			return build(new AnalysisEngineDescription[] {
					// no need to do sentence splitting - there are no sentences in the token view!
					// no need to do tokenizations - tokens are annotated directly when reading the spec
					createPrimitiveDescription(StanfordLemmatizerAE.class)
			});
		}
		catch (ResourceInitializationException e) {
			throw new AeException(e);
		}

	}
	
	public static AnalysisEngine forSpecSentenceView() throws AeException {
		try {
			return build(new AnalysisEngineDescription[] {
					// no need to do sentence splitting - sentences are annotated directly when reading the spec
					createPrimitiveDescription(OpenNlpTokenizerAE.class),
					createPrimitiveDescription(StanfordLemmatizerAE.class),
					createPrimitiveDescription(MaxentPosTaggerAE.class,
							MaxentPosTaggerAE.PARAM_MODEL_FILE , MAXENT_POS_TAGGER_MODEL_FILE),
					createPrimitiveDescription(StanfordParser.class),
			});
		}
		catch (ResourceInitializationException e) {
			throw new AeException(e);
		}

	}
	
	public static AnalysisEngine forDocument() throws AeException {
		try {
			return build(new AnalysisEngineDescription[] {
					// No need to do sentence splitting and tokenization - these are done directly when reading the document, in Document.readDoc()
					createPrimitiveDescription(StanfordLemmatizerAE.class),
					createPrimitiveDescription(MaxentPosTaggerAE.class,
							MaxentPosTaggerAE.PARAM_MODEL_FILE , MAXENT_POS_TAGGER_MODEL_FILE),
					createPrimitiveDescription(StanfordParser.class),
			});
		}
		catch (ResourceInitializationException e) {
			throw new AeException(e);
		}

	}
	
	private static AnalysisEngine build(AnalysisEngineDescription[] descriptions) throws AeException {
		try {
			AggregateBuilder builder = new AggregateBuilder();
			for (AnalysisEngineDescription desc : descriptions) {
				builder.add(desc);
			}
			return builder.createAggregate();
		}
		catch (ResourceInitializationException e) {
			throw new AeException(e);
		}
	}
	
	private static final String MAXENT_POS_TAGGER_MODEL_FILE = "src/main/resources/data/left3words-wsj-0-18.tagger";
}
