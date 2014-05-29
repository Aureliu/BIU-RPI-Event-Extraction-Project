package edu.cuny.qc.perceptron.types;

import java.util.Iterator;

import org.apache.uima.jcas.JCas;

import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Perceptron;

public class SentenceInstanceIterator implements Iterator<SentenceInstance> {
	public static final int SENT_ID_ADDITION = 100000;

	public Perceptron perceptron;
	public TypesContainer types;
	public Document doc;
	public Alphabet featureAlphabet; 
	public Controller controller;
	public boolean learnable;
	
	public int size;
	public SentenceInstance lastItem = null;
	public Sentence lastSentence = null;
	public Iterator<Sentence> sentIter;
	public Iterator<JCas> specIter;
	public int lastSpecNum;

	
	public SentenceInstanceIterator(Perceptron perceptron,
			TypesContainer types, Document doc, Alphabet featureAlphabet,
			Controller controller, boolean learnable) {
		this.perceptron = perceptron;
		this.types = types;
		this.doc = doc;
		this.featureAlphabet = featureAlphabet;
		this.controller = controller;
		this.learnable = learnable;
		
		if (doc.getSentences().size() >= SENT_ID_ADDITION) {
			throw new IllegalStateException("Got " + doc.getSentences().size() + " sentences, allowing only up to " + SENT_ID_ADDITION); 
		}

		sentIter = doc.getSentences().iterator();
		if (perceptron.controller.oMethod.equalsIgnoreCase("F")) {
			this.size = doc.getSentences().size() * types.specs.size();
		}
		else {
			this.size = doc.getSentences().size();
		}
	}

	@Override
	public boolean hasNext() {
		if (perceptron.controller.oMethod.equalsIgnoreCase("F")) {
			if (lastSentence == null) {
				return sentIter.hasNext();
			}
			else {
				return sentIter.hasNext() || specIter.hasNext();
			}
		}
		else {
			return sentIter.hasNext();
		}
	}

	@Override
	public SentenceInstance next() {
		if (perceptron.controller.oMethod.equalsIgnoreCase("F")) {
			if (lastSentence == null || !specIter.hasNext()) {
				lastSentence = sentIter.next();
				specIter = types.specs.iterator();
				lastSpecNum = 0;

				....
				JCas spec = specIter.next();
				lastItem = new SentenceInstance(perceptron, lastSentence, types, featureAlphabet,
						controller, learnable, spec, lastSentence.sentID + lastSpecNum*SENT_ID_ADDITION)
			}
			else {
				....
			}
		}
		else {
			....
		}
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

}
