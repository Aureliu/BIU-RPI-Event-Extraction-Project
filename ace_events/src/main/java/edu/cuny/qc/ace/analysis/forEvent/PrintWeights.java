package edu.cuny.qc.ace.analysis.forEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import edu.cuny.qc.perceptron.core.Perceptron;

public class PrintWeights
{
	public static void main(String[] args) throws FileNotFoundException
	{
		if(args.length < 1)
		{
			System.out.println("Usage:");
			System.out.println("args[0]: model");
			System.exit(-1);
		}
		
		File outputFile = new File(args[0] + ".weights");
		
		// Perceptron read model from the serialized file
		Perceptron model = Perceptron.deserializeObject(new File(args[0]));
		PrintStream out = new PrintStream(outputFile);
		if(model.controller.avgArguments)
		{
			out.println("Averaged Feature Weights");
			out.println(model.getAvg_weights());
		}
		else
		{
			out.println("Feature Weights");
			out.println(model.getWeights());
		}
		out.close();
	}
}
