package edu.cuny.qc.maxent;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.MaxEnt;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.featureGenerator.EdgeFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;

public class ArgumentClassifierTraining extends TriggerClassifierTraining
{
	
	public ArgumentClassifierTraining()
	{
		featTableFileName = "Temp/FeatureTableArg";
	}
	
	protected void printFeatureTableToFile(File srcDir, File trainingFileList, Controller controller)
	{
		Alphabet nodeTargetAlphabet = new Alphabet();
		Alphabet edgeTargetAlphabet = new Alphabet();
		Alphabet featureAlphabet = new Alphabet();
		
		try
		{
			List<SentenceInstance> trainInstanceList = readInstanceList(srcDir, trainingFileList, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, true);
			
			PrintWriter writer = new PrintWriter(new File(featTableFileName));
			for(SentenceInstance inst : trainInstanceList)
			{
				SentenceAssignment target = inst.target;
				Map<Integer, Map<Integer, Integer>> edgeAssn = target.getEdgeAssignment();
				for(int i=0; i<inst.size(); i++)
				{
					String triggerLabel = target.getLabelAtToken(i);
					// this is a trigger
					if(!triggerLabel.equals(SentenceAssignment.Default_Trigger_Label))
					{
						Map<Integer, Integer> edgeAssnTrigger = null;
						if(edgeAssn != null)
						{
							edgeAssnTrigger = edgeAssn.get(i);
						}
						// print feature table for each argument
						for(int k=0; k<inst.eventArgCandidates.size(); k++)
						{
							String argRole = SentenceAssignment.Default_Argument_Label;
							if(edgeAssnTrigger != null && edgeAssnTrigger.get(k) != null)
							{
								argRole = (String) inst.edgeTargetAlphabet.lookupObject(edgeAssnTrigger.get(k));
								
							}
							
							String argName = "Arg" + "#" + i + "#" + k;
							writer.print(argName);
							writer.print(" ");
							writer.print(argRole);
							
							// output features for one argument candidate
							AceMention mention = inst.eventArgCandidates.get(k);
							List<String> features = EdgeFeatureGenerator.get_edge_text_features(inst, i, mention);
							for(String feature : features)
							{
								writer.print(" ");
								writer.print(feature + ":1");
							}
							writer.println();
						}
					}
				}
			}
			writer.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		catch (DocumentException e)
		{
			e.printStackTrace();
		}	
	}
	
	static public void main(String[] args) throws IOException
	{
		if(args.length < 3)
		{
			System.out.println("Training perceptron Usage:");
			System.out.println("args[0]: source dir of training data");
			System.out.println("args[1]: file list of training data");
			System.out.println("args[2]: model file to be saved");
			System.out.println("args[3+]: controller arguments");
			System.exit(-1);
		}
		
		File srcDir = new File(args[0]);
		File trainingFileList = new File(args[1]);
		File modelFile = new File(args[2]);
		
		// set settings
		Controller controller = new Controller();
		if(args.length > 3)
		{
			String[] settings = Arrays.copyOfRange(args, 3, args.length);
			controller.setValueFromArguments(settings);
		}
		System.out.println(controller.toString());
		
		// train model
		ArgumentClassifierTraining trainer = new ArgumentClassifierTraining(); 
		Classifier model = trainer.trainClassifier(srcDir, trainingFileList, modelFile, controller);
		
		// print out weights or any other detail
		PrintStream out = new PrintStream(modelFile.getAbsoluteFile() + ".weights");
		printFeatureWeights((MaxEnt) model, out);
		out.close();
	}
}
