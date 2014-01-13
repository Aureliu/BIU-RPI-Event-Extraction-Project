package edu.cuny.qc.maxent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.pipe.Csv2FeatureVector;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.InstanceList;

public class MaxentTrainer 
{
	static double Gaussian_Variance = 1.0;
	
	// in the training feature table
	// Lines should be formatted as:                                                                   
    //                                                                                                 
    //   [name] [label] [data ... ]                                                                    
    //                                                                                                          
	static public Classifier TrainMaxent(String trainingFilename, File modelFile) throws IOException 
	{
		// build data input pipe
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		
		// define pipe
		// the features in [data ...] should like: feature:value
		pipes.add(new Target2Label());
		pipes.add(new Csv2FeatureVector());
		
		Pipe pipe = new SerialPipes(pipes);
		pipe.setTargetProcessing(true);
		
		// read data
		InstanceList trainingInstances = new InstanceList(pipe);
		FileReader training_file_reader = new FileReader(trainingFilename);
		CsvIterator reader =
	            new CsvIterator(training_file_reader,
	                            "(\\w+)\\s+([^\\s]+)\\s+(.*)",
	                            3, 2, 1);  // (data, label, name) field indices    
		trainingInstances.addThruPipe(reader);
		training_file_reader.close();
		
		// calculate running time
		long startTime = System.currentTimeMillis();
		PrintStream temp = System.err;
		System.setErr(System.out);
		
		// train a Maxent classifier (could be other classifiers)
		ClassifierTrainer trainer = new MaxEntTrainer(Gaussian_Variance);
		Classifier classifier = trainer.train(trainingInstances);
		
		
		System.setErr(temp);
		// calculate running time
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total training time: " + totalTime);
		
		// write model
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelFile));
	    oos.writeObject(classifier);
	    oos.close();
	    
	    return classifier;
	}
	
	public static void main (String[] args) throws Exception 
	{
		if(args.length < 2)
		{
			System.out.println("Usage:");
			System.out.println("args[0] : feature table file");
			System.out.println("args[1] : model file name");
			System.exit(-1);
		}		
		
		File featureTable = new File(args[0]);
		File modelFile = new File(args[1]);
		
		// train the model
		TrainMaxent(featureTable.getAbsolutePath(), modelFile); 
	}
}
