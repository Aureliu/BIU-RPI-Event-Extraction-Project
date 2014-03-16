package ac.biu.nlp.nlp.ie.onthefly.input;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

public class AnnotationUtils {

	public static PennPartOfSpeech tokenToPOS(Token token) throws UnsupportedPosTagStringException {
		return new PennPartOfSpeech(token.getPos().getPosValue());
	}

}
