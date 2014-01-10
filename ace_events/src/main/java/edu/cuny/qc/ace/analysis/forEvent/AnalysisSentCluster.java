package edu.cuny.qc.ace.analysis.forEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.dom4j.DocumentException;

import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.DocumentCrossSent;
import edu.cuny.qc.perceptron.types.Sentence;

public class AnalysisSentCluster
{
	public static void readInstanceList(File srcDir, File file_list, 
			Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, 
			Controller controller, boolean learnable) throws IOException, DocumentException
	{
		System.out.println("Reading training instance ...");
		
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		int num_cluster_with_event = 0;
		int num_singleton_cluster = 0;
		int num_sent_with_event = 0;
		while((line = reader.readLine()) != null)
		{
			boolean monoCase = line.contains("bn/") ? true : false;
			String fileName = srcDir + File.separator + line;
			
			System.out.println(fileName);
			
			DocumentCrossSent doc = new DocumentCrossSent(fileName, true, monoCase);
			// fill in text feature vector for each token
			TextFeatureGenerator.doPreprocessCheap(doc);
			doc.setSentenceClustersByTokens();
			int orginal_size = doc.getSentences().size();
			int size = doc.getSentenceClusters().size();
			
			
			for(List<Sentence> cluster : doc.getSentenceClusters())
			{
				if(cluster.size() > 5)
				{
					System.out.println("cluster size: " + cluster.size());
				}
				
				int hasEvent = 0;
				for(Sentence sent : cluster)
				{
					if(sent.eventMentions != null && sent.eventMentions.size() > 0)
					{
						hasEvent++;
					}
				}
				
				if(hasEvent > 0)
				{
					num_cluster_with_event++;
					if(cluster.size() == 1)
					{
						num_singleton_cluster++;
					}
					num_sent_with_event += hasEvent;
				}
			}
			
			System.out.println(orginal_size + "--->" + size);
		}
		
		System.out.println("number of cluster with events: " + num_cluster_with_event);
		System.out.println("number of singleton clusters: " + num_singleton_cluster);
		System.out.println("number of sent with events: " + num_sent_with_event);
		System.out.println("done");
		return;
	}
	
	public static void main(String[] args) throws IOException, DocumentException
	{
		File src = new File("/Users/XX/Data/ACE/ACE2005-TrainingData-V6.0/English");
		File filelist = new File("/Users/XX/Data/ACE/filelist_ACE_nw_test");
		
		Alphabet nodeTargetAlphabet = new Alphabet();
		Alphabet edgeTargetAlphabet = new Alphabet();
		Alphabet featureAlphabet = new Alphabet();
		Controller controller = new Controller();
		
		readInstanceList(src, filelist, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, true);
	}
}
