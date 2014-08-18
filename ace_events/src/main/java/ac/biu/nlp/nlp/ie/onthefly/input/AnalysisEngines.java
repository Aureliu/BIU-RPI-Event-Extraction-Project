package ac.biu.nlp.nlp.ie.onthefly.input;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.factory.AggregateBuilder;

import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import eu.excitementproject.eop.lap.biu.uima.ae.postagger.MaxentPosTaggerAE;
import eu.excitementproject.eop.lap.biu.uima.ae.tokenizer.MaxentTokenizerAE;

public class AnalysisEngines {
	static {
		//System.err.println("AnalysisEngines: TODO - add POS tagging back to Document and Sentence");
		System.err.println("I started off with my implementeation of StanfordParserAE that builds upon Qi's use of Stanford Parser. I even discovered that while it considers punctuation, it doesn't output dependencies for it, so I wrote a new component that removes Token annotations from punctuation, so that they don't ruin the conversion later. I also removed some samples from spec due to some convertor issues. But now, it fails on many corpus sentences, which I am not sure what to do about. It's all coming from having a (non-deep) node with two parents, and from a brief check, it seems that at least one of them has the dependency 'dep', which doesn't sound real to me, but technically apear in the Stanford pdf, so maybe.\n");
	}
	
	public static AnalysisEngine forSpecTokenView(String viewName) throws AeException {
		try {
			//System.err.printf("\nAnalysisEngines.forSpecTokenView: removing lemmztizer for now.\n\n");
			
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
					
					// do stuff like split-by-hyphen
					//createPrimitiveDescription(TokenFixerAE.class),
					
					createPrimitiveDescription(StanfordLemmatizerAE.class),
					createPrimitiveDescription(StanfordPosTaggerAE.class,
							StanfordPosTaggerAE.PARAM_MODEL_FILE , MAXENT_POS_TAGGER_MODEL_FILE),
							
//					createPrimitiveDescription(EasyFirstParserAE.class,
//							EasyFirstParserAE.PARAM_HOST , "127.0.0.1",
//							EasyFirstParserAE.PARAM_PORT , 8080
//							),
							
					createPrimitiveDescription(StanfordParserAE.class),
					
					// This must be invoked AFTER THE PARSER - to give the parser everything, even punctuation (and even if he doesn't give punctuations any dependencies)
					createPrimitiveDescription(PunctuationTokenRemoverAE.class),
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
							
//					createPrimitiveDescription(EasyFirstParserAE.class,
//							EasyFirstParserAE.PARAM_HOST , "127.0.0.1",
//							EasyFirstParserAE.PARAM_PORT , 8080
//							),

							createPrimitiveDescription(StanfordParserAE.class),
							
							// This must be invoked AFTER THE PARSER - to give the parser everything, even punctuation (and even if he doesn't give punctuations any dependencies)
							createPrimitiveDescription(PunctuationTokenRemoverAE.class),
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
