package edu.cuny.qc.perceptron.similarity_scorer;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.perceptron.types.SignalInstance;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public abstract class SignalMechanismSpecTokenIterator extends SignalMechanismSpecIterator {

	static {System.err.println("Consider using Guava caches to cache signal values for specific textToken-specToken pairs (maybe also with their lemmas and/or POSes). Maybe also/instead, cache some intermediate values, like a lemma's WordNet sysnet.");}

	public SignalMechanismSpecIterator init(JCas spec, String viewName, AnnotationFS covering, Class<? extends Annotation> type, Token textAnno) throws SignalMechanismException {
		return super.init(spec, viewName, covering, type, textAnno);
	}
	
	@Override
	public Double calcScore(Annotation text, Annotation spec) throws SignalMechanismException {
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
			throw new SignalMechanismException(e);
		}
	}

	public Double calcTokenScore(Token text, Token spec) throws SignalMechanismException {
		Boolean boolResult = calcTokenBooleanScore(text, spec);
		return SignalInstance.toDouble(boolResult);
	}
	
	public Boolean calcTokenBooleanScore(Token text, Token spec) throws SignalMechanismException {
		throw new UnsupportedOperationException("calcTokenBooleanScore must be implemented in subclass");
	}
}
