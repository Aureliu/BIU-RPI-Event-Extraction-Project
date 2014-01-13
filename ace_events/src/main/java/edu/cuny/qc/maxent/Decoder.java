package edu.cuny.qc.maxent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;

import edu.cuny.qc.ace.acetypes.AceEvent;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.ace.acetypes.Scorer;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.featureGenerator.EdgeFeatureGenerator;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;

/**
 * This is staged decoder combines the results of Trigger classifier 
 * and Argument classifier
 * @author che
 *
 */
public class Decoder extends edu.cuny.qc.perceptron.core.Decoder
{
	/**
	 * given a list of instances, decode them, and return 
	 * a list of SentenceAssignment
	 * @param instanceList
	 * @param triggerClassifier
	 * @param argClassifier
	 * @return
	 */
	private static List<SentenceAssignment> decoding(
			List<SentenceInstance> instanceList, MaxEntDecoder triggerClassifier, MaxEntDecoder argClassifier)
	{
		List<SentenceAssignment> ret = new ArrayList<SentenceAssignment>();
		for(SentenceInstance inst : instanceList)
		{
			SentenceAssignment assn = new SentenceAssignment(inst.nodeTargetAlphabet, inst.edgeTargetAlphabet, inst.featureAlphabet, inst.controller);
			ret.add(assn);
			
			for(int i=0; i<inst.size(); i++)
			{
				// get feature vector for the trigger candidate
				StringBuilder featureVector = new StringBuilder("trigger" + " " + SentenceAssignment.Default_Trigger_Label);
				List<String> features = ((List<List<String>>) inst.get(InstanceAnnotations.NodeTextFeatureVectors)).get(i);
				for(String feature : features)
				{
					featureVector.append(" ");
					featureVector.append(feature + ":1");
				}
				// predict trigger type
				String triggerLabel = triggerClassifier.decodeOnFeatureVector(featureVector.toString());
				// set trigger type in assn
				assn.incrementState();
				assn.setCurrentNodeLabel(triggerLabel);
				
				if(!triggerLabel.equals(SentenceAssignment.Default_Trigger_Label))
				{
					for(int k=0; k<inst.eventArgCandidates.size(); k++)
					{
						AceMention mention = inst.eventArgCandidates.get(k);
						// predict argument type
						featureVector = new StringBuilder("arg" + " " + SentenceAssignment.Default_Argument_Label);
						features = EdgeFeatureGenerator.get_edge_text_features(inst, i, mention);
						for(String feature : features)
						{
							featureVector.append(" ");
							featureVector.append(feature + ":1");
						}
						// predict argument role
						String argRole = argClassifier.decodeOnFeatureVector(featureVector.toString());
						// set arg role in assn
						assn.setCurrentEdgeLabel(k, argRole);
					}
				}
			}
		}
		return ret;
	}
	
	static public void main(String[] args) throws IOException, DocumentException
	{
		if(args.length < 5)
		{
			System.out.println("Usage:");
			System.out.println("args[0]: trigger classifier");
			System.out.println("args[1]: arg classifier");
			System.out.println("args[2]: src dir");
			System.out.println("args[3]: file list");
			System.out.println("args[4]: output dir");
			System.exit(-1);
		}
		
		// read Classifier models
		MaxEntDecoder triggerClassifier = new MaxEntDecoder(new File(args[0]), "TriggerClassifier");
		MaxEntDecoder argClassifier = new MaxEntDecoder(new File(args[1]), "ArgClassifier");
		
		// Perceptron read model from the serialized file		
		File srcDir = new File(args[2]);
		File fileList = new File(args[3]);
		File outDir = new File(args[4]);
		if(!outDir.exists())
		{
			outDir.mkdirs();
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(fileList));
		String line = "";
		TextFeatureGenerator featGen = new TextFeatureGenerator();
		// decode for each document
		while((line = reader.readLine()) != null)
		{
			List<SentenceInstance> localInstanceList = null;
			boolean monoCase = line.contains("bn/") ? true : false;
			String fileName = srcDir + File.separator + line;
			System.out.println(fileName);
			Document doc = null;
			
			doc = new Document(fileName, true, monoCase);
			// fill in text feature vector for each token
			featGen.fillTextFeatures(doc);
			
			Alphabet nodeTargetAlphabet = new Alphabet();
			Alphabet edgeTargetAlphabet = new Alphabet();
			Alphabet featureAlphabet = new Alphabet();
			Controller controller = new Controller();
			
			localInstanceList = doc.getInstanceList(nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, 
					controller, true);
			
			// docoding
			List<SentenceAssignment> localResults = decoding(localInstanceList, triggerClassifier, argClassifier);
			
			// print to docs
			File outputFile = new File(outDir + File.separator + line);
			if(!outputFile.getParentFile().exists())
			{
				outputFile.getParentFile().mkdirs();
			}
			String docID = doc.docID.substring(doc.docID.lastIndexOf(File.separator) + 1);
			String id_prefix = docID + "-" + "EV";
			PrintWriter out = new PrintWriter(outputFile);
			
			// output entities and predicted events from doc
			List<AceEvent> eventsInDoc = new ArrayList<AceEvent>();
			
			for(int inst_id=0; inst_id<localInstanceList.size(); inst_id++)
			{
				SentenceAssignment assn = localResults.get(inst_id);
				SentenceInstance inst = localInstanceList.get(inst_id);
				String id = id_prefix + inst_id;
				// each event only contains one single event mention
				List<AceEvent> events = inst.getEvents(assn, id, doc.allText);
				eventsInDoc.addAll(events);
			}
			writeEntities(out, doc.getAceAnnotations(), eventsInDoc);
			out.close();
		}
		
		// get score
		File outputFile = new File(outDir + File.separator + "Score");
		Scorer.main(new String[]{args[2], args[4], args[3], outputFile.getAbsolutePath()});
	}

}
