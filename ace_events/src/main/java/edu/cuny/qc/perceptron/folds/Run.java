package edu.cuny.qc.perceptron.folds;

import java.util.Map;

import org.apache.uima.jcas.JCas;

public class Run {
	public Map<String, JCas> trainEvents, devEvents, testEvents;
	public String suffix;
	public int id, idPerTest;
}