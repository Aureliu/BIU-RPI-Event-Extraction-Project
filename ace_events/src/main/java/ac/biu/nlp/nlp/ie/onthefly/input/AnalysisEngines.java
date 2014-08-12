package ac.biu.nlp.nlp.ie.onthefly.input;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.factory.AggregateBuilder;

import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;

public class AnalysisEngines {
	static {
		//System.err.println("AnalysisEngines: TODO - add POS tagging back to Document and Sentence");
	}
	
	public static AnalysisEngine forSpecTokenView(String viewName) throws AeException {
		try {
			return build(viewName, new AnalysisEngineDescription[] {
					// no need to do sentence splitting - there are no sentences in the token view!
					// no need to do tokenizations - tokens are annotated directly when reading the spec
					// Update: we are adding a tokenizer to handle multi-word values
					createPrimitiveDescription(OpenNlpTokenizerAE.class),
					createPrimitiveDescription(TypedStanfordLemmatizerAE.class)
			});
		}
		catch (ResourceInitializationException e) {
			throw new AeException(e);
		}

	}
	
	public static AnalysisEngine forSpecSentenceView(String viewName) throws AeException {
		try {
			return build(viewName, new AnalysisEngineDescription[] {
					// no need to do sentence splitting - sentences are annotated directly when reading the spec
					createPrimitiveDescription(OpenNlpTokenizerAE.class),
					createPrimitiveDescription(StanfordLemmatizerAE.class),
					createPrimitiveDescription(StanfordPosTaggerAE.class,
							StanfordPosTaggerAE.PARAM_MODEL_FILE , MAXENT_POS_TAGGER_MODEL_FILE),
					createPrimitiveDescription(StanfordParser.class),
			});
		}
		catch (ResourceInitializationException e) {
			throw new AeException(e);
		}

	}
	
	public static AnalysisEngine forDocument(String viewName) throws AeException {
		try {
			return build(viewName, new AnalysisEngineDescription[] {
					// No need to do sentence splitting and tokenization - these are done directly when reading the document, in Document.readDoc()
					createPrimitiveDescription(StanfordLemmatizerAE.class),
					createPrimitiveDescription(StanfordPosTaggerAE.class,
							StanfordPosTaggerAE.PARAM_MODEL_FILE , MAXENT_POS_TAGGER_MODEL_FILE),
					//createPrimitiveDescription(StanfordParser.class),
			});
		}
		catch (ResourceInitializationException e) {
			throw new AeException(e);
		}

	}
	
	private static AnalysisEngine build(String viewName, AnalysisEngineDescription[] descriptions) throws AeException {
		try {
			AggregateBuilder builder = new AggregateBuilder();
			for (AnalysisEngineDescription desc : descriptions) {
				if (viewName == null) {
					builder.add(desc);
				}
				else {
					builder.add(desc, VIEW_DEFAULT, viewName);
				}
			}
			return builder.createAggregate();
		}
		catch (ResourceInitializationException e) {
			throw new AeException(e);
		}
	}
	
	private static final String MAXENT_POS_TAGGER_MODEL_FILE = "src/main/resources/data/left3words-wsj-0-18.tagger";
	private static final String VIEW_DEFAULT = "_InitialView";
}
