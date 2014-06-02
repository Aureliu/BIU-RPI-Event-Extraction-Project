package ac.biu.nlp.nlp.ace_uima;

import java.io.IOException;
import java.util.Date;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import edu.cuny.qc.perceptron.types.Document;

public class AceSentenceSplitterAndTokenizer extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			DocumentMetaData meta = JCasUtil.selectSingle(jcas, DocumentMetaData.class);
			String path = meta.getDocumentUri();
			String baseName = path.replaceFirst(".sgm", "");
			boolean monoCase = path.contains("bn/") ? true : false;
			
			// the creation of Document() fills the CAS with Sentence and Token
			System.out.printf("\n[%s] new Document(%s)... ", new Date(), path);
			new Document(baseName, true, monoCase, jcas);
			System.out.printf("[%s] Done\n", new Date(), path);

		}
		catch (IOException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e); 
		}
	}

}
