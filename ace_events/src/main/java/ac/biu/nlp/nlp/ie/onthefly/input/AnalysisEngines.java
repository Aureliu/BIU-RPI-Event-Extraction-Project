package ac.biu.nlp.nlp.ie.onthefly.input;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.factory.AggregateBuilder;

public class AnalysisEngines {
	public static AnalysisEngine forSpecTokenView() throws AeException {
		try {
			return build(new AnalysisEngineDescription[] {
					createPrimitiveDescription(GateLemmatizerAE.class)
			});
		}
		catch (ResourceInitializationException e) {
			throw new AeException(e);
		}

	}
	
	public static AnalysisEngine forSpecSentenceView() throws AeException {
		try {
			return build(new AnalysisEngineDescription[] {
					createPrimitiveDescription(???.class)
			});
		}
		catch (ResourceInitializationException e) {
			throw new AeException(e);
		}

	}
	
	public static AnalysisEngine forDocument() throws AeException {
		try {
			return build(new AnalysisEngineDescription[] {
					createPrimitiveDescription(???.class)
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
}
