package ac.biu.nlp.nlp.ie.onthefly.input;

import java.util.concurrent.ExecutionException;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.util.PosMap;
import edu.cuny.qc.util.Span;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

public class AnnotationUtils {

	public static PartOfSpeech tokenToPOS(Token token) throws UnsupportedPosTagStringException, ExecutionException {
		return PosMap.byString.get(token.getPos().getPosValue());
	}

	public static Annotation spanToAnnotation(JCas jcas, Span span) {
		Annotation anno = new Annotation(jcas, span.start(), span.end()+1);
		// anno.addToIndexes(); //no need!
		return anno;
	}

}
