package edu.cuny.qc.perceptron.similarity_scorer;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.perceptron.types.MeasureInstance;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public abstract class MeasureMechanismSpecTokenIterator extends MeasureMechanismSpecIterator {

	static {System.err.println("Consider using Guava caches to cache measure values for specific textToken-specToken pairs (maybe also with their lemmas and/or POSes). Maybe also/instead, cache some intermediate values, like a lemma's WordNet sysnet.");}

	public MeasureMechanismSpecIterator init(JCas spec, String viewName, Class<? extends Annotation> type, Token textAnno) throws MeasureMechanismException {
		return super.init(spec, viewName, type, textAnno);
	}
	
	@Override
	public Double calcScore(Annotation text, Annotation spec) throws MeasureMechanismException {
		try {
			Token textToken = null;
			Token specToken = null;
			
			if (text.getClass().equals(Token.class)) {
				textToken = (Token) text;
			}
			else {
				textToken = UimaUtils.selectCoveredSingle(text.getView().getJCas(), Token.class, text);
			}
			
			if (spec.getClass().equals(Token.class)) {
				specToken = (Token) spec;
			}
			else {
				specToken = UimaUtils.selectCoveredSingle(spec.getView().getJCas(), Token.class, spec);
			}
			
			return calcTokenScore(textToken, specToken);
		} catch (CASException e) {
			throw new MeasureMechanismException(e);
		}
	}

	public Double calcTokenScore(Token text, Token spec) throws MeasureMechanismException {
		Boolean boolResult = calcTokenBooleanScore(text, spec);
		return MeasureInstance.toDouble(boolResult);
	}
	
	public Boolean calcTokenBooleanScore(Token text, Token spec) throws MeasureMechanismException {
		throw new UnsupportedOperationException("calcTokenBooleanScore must be implemented in subclass");
	}
}
