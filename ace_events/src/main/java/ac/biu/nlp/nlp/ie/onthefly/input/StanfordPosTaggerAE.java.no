package ac.biu.nlp.nlp.ie.onthefly.input;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.util.POSTaggerWrapperStanford;
import eu.excitementproject.eop.lap.biu.uima.ae.postagger.PennPOSMapping;

/**
 * Taken mostly from {@link eu.excitementproject.eop.lap.biu.uima.ae.postagger.PosTaggerAE}
 * and {@link eu.excitementproject.eop.lap.biu.uima.ae.postagger.MaxentPosTaggerAE}.
 * 
 * @author Ofer Bronstein
 *
 */
public class StanfordPosTaggerAE extends JCasAnnotator_ImplBase {
	
	/**
	 * Model file of this POS tagger.
	 */
	public static final String PARAM_MODEL_FILE = "model_file";
	@ConfigurationParameter(name = PARAM_MODEL_FILE, mandatory = true)
	private String modelFile;

	
	protected MappingProvider mappingProvider;
	
	@Override
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		super.initialize(aContext);

		mappingProvider = new MappingProvider();
		mappingProvider.setDefault(MappingProvider.BASE_TYPE, POS.class.getName());
		mappingProvider.setDefault(MappingProvider.LOCATION, PennPOSMapping.MAPPING_LOCATION);
		mappingProvider.setDefault("tagger.tagset", "default");
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		CAS cas = jcas.getCas();
		mappingProvider.configure(cas);

		Collection<Token> tokens = JCasUtil.select(jcas, Token.class);
		List<String> tokenStrings = JCasUtil.toText(tokens);
		String[] tokensArray = tokenStrings.toArray(new String[tokenStrings.size()]);
		
		try {
			String[] posTags = (String[]) POSTaggerWrapperStanford.getPosTagger().posTag(tokensArray);
			if (posTags.length != tokens.size()) {
				throw new AeException(String.format("Got %d pos tags for %s tokens, should be same amount", posTags.length, tokens.size()));
			}
			int i=0;
			for (Token token : tokens) {
				String tag = posTags[i];

				Type posTag = mappingProvider.getTagType(tag);
				POS posAnnotation = (POS) cas.createAnnotation(posTag, token.getBegin(), token.getEnd());
				posAnnotation.setPosValue(tag);
				posAnnotation.addToIndexes();
				
				token.setPos(posAnnotation);
				i++;
			}
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		} catch (AeException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}
	}
}
