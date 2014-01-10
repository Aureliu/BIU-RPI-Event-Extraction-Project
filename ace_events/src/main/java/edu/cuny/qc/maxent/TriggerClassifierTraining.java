package edu.cuny.qc.maxent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dom4j.DocumentException;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.MaxEnt;

import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;

public class TriggerClassifierTraining
{
	public static String featTableFileName = "Temp/FeatureTableEvent";
	
	public Classifier trainClassifier(File srcDir, File trainingFileList, File modelFile, Controller controller)
	{
		// print whole featureTable for the training instances
		printFeatureTableToFile(srcDir, trainingFileList, controller);
		
		// maxent training
		try
		{
			Classifier model = MaxentTrainer.TrainMaxent(featTableFileName, modelFile);
			return model;
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
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
				for(int i=0; i<inst.size(); i++)
				{
					// TODO: skip impossible POS for trigger later
					String instanceName = "Trigger" + i;
					String triggerLabel = target.getLabelAtToken(i);
					writer.print(instanceName);
					writer.print(" ");
					writer.print(triggerLabel);
					
					// output features for one trigger candidate
					List<String> features = ((List<List<String>>) inst.get(InstanceAnnotations.NodeTextFeatureVectors)).get(i);
					for(String feature : features)
					{
						writer.print(" ");
						writer.print(feature + ":1");
					}
					
					writer.println();
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

	/**
	 * give a file list and home dir, get an instance list
	 * @param srcDir
	 * @param file_list
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static List<SentenceInstance> readInstanceList(File srcDir, File file_list, 
			Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, 
			Controller controller, boolean learnable) throws IOException, DocumentException
	{
		System.out.println("Reading training instance ...");
		
		List<SentenceInstance> instancelist = new ArrayList<SentenceInstance>();
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		TextFeatureGenerator featGen = new TextFeatureGenerator();
		while((line = reader.readLine()) != null)
		{
			boolean monoCase = line.contains("bn/") ? true : false;
			String fileName = srcDir + File.separator + line;
			
			System.out.println(fileName);
			
			Document doc = new Document(fileName, true, monoCase);
			// fill in text feature vector for each token
			featGen.fillTextFeatures(doc);
			for(int sent_id=0 ; sent_id<doc.getSentences().size(); sent_id++)
			{
				Sentence sent = doc.getSentences().get(sent_id);
				// during learning, skip instances that do not have event mentions 
				if(learnable && controller.skipNonEventSent)
				{
					if(sent.eventMentions != null && sent.eventMentions.size() > 0)
					{
						SentenceInstance inst = new SentenceInstance(sent, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet,
								controller, learnable);
						instancelist.add(inst);
					}
				}
				else // add all instances
				{
					SentenceInstance inst = new SentenceInstance(sent, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, 
							controller, learnable);
					instancelist.add(inst);
				}
			}
		}
		
		System.out.println("done");
		return instancelist;
	}
	
	/**
	 * print the feature weights for the MaxEnt model
	 * @param maxEnt
	 * @param out
	 */
	public static void printFeatureWeights(MaxEnt maxEnt, PrintStream out)
	{
		maxEnt.print(out);
	}
	
	/**
	 * This is a very simple pipeline
	 * @param args
	 * @throws IOException
	 */
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
		TriggerClassifierTraining trainer = new TriggerClassifierTraining(); 
		Classifier model = trainer.trainClassifier(srcDir, trainingFileList, modelFile, controller);
		
		// print out weights or any other detail
		PrintStream out = new PrintStream(modelFile.getAbsoluteFile() + ".weights");
		printFeatureWeights((MaxEnt) model, out);
		out.close();
	}
}
