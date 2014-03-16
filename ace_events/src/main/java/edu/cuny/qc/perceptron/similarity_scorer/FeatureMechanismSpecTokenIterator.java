package edu.cuny.qc.perceptron.similarity_scorer;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.perceptron.types.FeatureInstance;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public abstract class FeatureMechanismSpecTokenIterator extends FeatureMechanismSpecIterator {

	static {System.err.println("Consider using Guava caches to cache feature values for specific textToken-specToken pairs (maybe also with their lemmas and/or POSes). Maybe also/instead, cache some intermediate values, like a lemma's WordNet sysnet.");}

	public FeatureMechanismSpecIterator init(JCas spec, String viewName, Class<? extends Annotation> type, Token textAnno) throws FeatureMechanismException {
		return super.init(spec, viewName, type, textAnno);
	}
	
	@Override
	public Double calcScore(Annotation text, Annotation spec) throws FeatureMechanismException {
		try {
			Token textToken = UimaUtils.selectCoveredSingle(text.getCAS().getJCas(), Token.class, text);
			Token specToken = UimaUtils.selectCoveredSingle(spec.getCAS().getJCas(), Token.class, spec);
			return calcTokenScore(textToken, specToken);
		} catch (CASException e) {
			throw new FeatureMechanismException(e);
		}
	}

	public Double calcTokenScore(Token text, Token spec) throws FeatureMechanismException {
		Boolean boolResult = calcTokenBooleanScore(text, spec);
		return FeatureInstance.toDouble(boolResult);
	}
	
	public Boolean calcTokenBooleanScore(Token text, Token spec) throws FeatureMechanismException {
		throw new UnsupportedOperationException("calcTokenBooleanScore must be implemented in subclass");
	}
}
