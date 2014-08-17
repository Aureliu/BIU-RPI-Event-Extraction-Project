package edu.cuny.qc.scorer;

import java.util.Collection;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import ac.biu.nlp.nlp.ie.onthefly.input.AnnotationUtils;
import ac.biu.nlp.nlp.ie.onthefly.input.uima.ArgumentInUsageSample;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.util.Span;
import edu.cuny.qc.util.Utils;

public abstract class ArgumentInUsageSampleScorer extends ArgumentScorer<ArgumentInUsageSample> {
	private static final long serialVersionUID = 3212246701761719333L;

	@Override
	protected void prepareSpecIteration(JCas spec) throws SignalMechanismException {
		try {
			Collection<ArgumentInUsageSample> aiuses = JCasUtil.select(argument.getAiuses(), ArgumentInUsageSample.class);
			specIterator = aiuses.iterator();
			docJcas = textTriggerToken.getCAS().getJCas();
		} catch (CASException e) {
			throw new SignalMechanismException(e);
		}
	}

	@Override
	public Boolean calcBooleanArgumentScore(ArgumentInUsageSample spec) throws SignalMechanismException {
		Span head = Utils.getHead(aceMention);
		Annotation headAnno = AnnotationUtils.spanToAnnotation(docJcas, head);
		boolean result = calcBoolPredicateSeedScore(textTriggerToken, aceMention, headAnno, spec, scorerData);
		if (result && debug) {
			addToHistory();
		}
		return result;
	}
	
	public abstract void addToHistory() throws SignalMechanismException;
	
//	public void addToHistory(String textStr, PartOfSpeech textPos, String specStr, PartOfSpeech specPos, PredicateSeed spec) throws SignalMechanismException {
//		throw new RuntimeException("not implemented yet");
//		String specBase = spec.getCoveredText();
//		String textBase = getForm(textTriggerToken);
//		String specExtra = specBase.equals(specStr)?"":String.format("(%s)",specStr);
//		String textExtra = textBase.equals(textStr)?"":String.format("(%s)",textStr);
//		
//		String specHistory = String.format("%s%s/%s", specBase, specExtra, specPos.getCanonicalPosTag());
//		String textHistory = String.format("%s%s/%s", textBase, textExtra, textPos.getCanonicalPosTag());
//		history.put(specHistory.intern(), textHistory.intern());
//	}
	
//	public abstract Boolean calcBoolPredicateSeedScore(BasicNode textFragment, BasicNode specfragment, ScorerData scorerData) throws SignalMechanismException;
	public abstract Boolean calcBoolPredicateSeedScore(Token textTriggerToken, AceMention textMention, Annotation textHeadAnno, ArgumentInUsageSample specAius, ScorerData scorerData) throws SignalMechanismException;
	
	private static JCas docJcas = null;
}
