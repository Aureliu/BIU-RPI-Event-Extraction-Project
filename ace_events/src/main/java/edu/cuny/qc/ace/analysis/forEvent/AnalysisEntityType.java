package edu.cuny.qc.ace.analysis.forEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;

import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.util.TypeConstraints;

public class AnalysisEntityType
{
	public static List<SentenceInstance> readInstanceList(File srcDir, File file_list, 
			Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, 
			Controller controller, boolean learnable) throws IOException, DocumentException
	{
		System.out.println("Reading training instance ...");
		
		List<SentenceInstance> instancelist = new ArrayList<SentenceInstance>();
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		while((line = reader.readLine()) != null)
		{
			boolean monoCase = line.contains("bn/") ? true : false;
			String fileName = srcDir + File.separator + line;
			
			System.out.println(fileName);
			
			Document doc = new Document(fileName, true, monoCase);
			// fill in text feature vector for each token
			TextFeatureGenerator.doPreprocessCheap(doc);
			for(int sent_id=0 ; sent_id<doc.getSentences().size(); sent_id++)
			{
				Sentence sent = doc.getSentences().get(sent_id);
				SentenceInstance inst = null;
				// during learning, skip instances that do not have event mentions 
				if(learnable && controller.skipNonEventSent)
				{
					if(sent.eventMentions != null && sent.eventMentions.size() > 0)
					{
						inst = new SentenceInstance(sent, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, learnable);
						instancelist.add(inst);
					}
				}
				else // add all instances
				{
					inst = new SentenceInstance(sent, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, 
							controller, learnable);
					instancelist.add(inst);
				}
				
				if(inst == null)
				{
					continue;
				}
				
				SentenceAssignment assn = inst.target;
				Map<Integer, Map<Integer, Integer>> edgeAssn = assn.getEdgeAssignment();
				for(Integer nodeIndex : edgeAssn.keySet())
				{
					String node_label = assn.getLabelAtToken(nodeIndex);
					Map<Integer, Integer> edges = edgeAssn.get(nodeIndex);
					for(Integer entityIndex : edges.keySet())
					{
						String mentionType = inst.eventArgCandidates.get(entityIndex).getType();
						Integer edgeLabelIndex = edges.get(entityIndex);
						String edgeLabel = (String) edgeTargetAlphabet.lookupObject(edgeLabelIndex);
						
						if(!edgeLabel.equals(SentenceAssignment.Default_Argument_Label))
						{
							if(!(TypeConstraints.isRoleCompatible(node_label, edgeLabel) && TypeConstraints.isEntityTypeCompatible(edgeLabel, mentionType)))
							{
								System.err.println("ERROR: " + node_label + "\t" + edgeLabel + "\t" + mentionType);
							}
						}
					}
				}
			}
		}
		
		System.out.println("done");
		return instancelist;
	}
	
	public static void main(String[] args) throws IOException, DocumentException
	{
		File src = new File("/Users/che/Data/ACE/ACE2005-TrainingData-V6.0/English");
		File filelist = new File("/Users/che/Data/ACE/filelist_ACE_all");
		
		Alphabet nodeTargetAlphabet = new Alphabet();
		Alphabet edgeTargetAlphabet = new Alphabet();
		Alphabet featureAlphabet = new Alphabet();
		Controller controller = new Controller();
		
		List<SentenceInstance> list = readInstanceList(src, filelist, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, true);
	}
}
