package edu.cuny.qc.ace.analysis.forRelation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.DocumentException;

import edu.cuny.qc.ace.acetypes.AceRelationMention;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.util.RelationTypeConstraints;

/**
 * calculate some Statistics about the relation over the 
 * whole corpus
 * @author qli
 *
 */
public class CorpusStatistics
{
	public static void readInstanceList(File srcDir, File file_list, 
			Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, 
			Controller controller, boolean learnable, PrintStream out) throws IOException, DocumentException
	{
		// num of relation in each type
		Map<String, Double> countType = new HashMap<String, Double>();
		// the num of sents
		int num_sent = 0;
		// the num of sents contains relations
		int num_sent_relation = 0;
		// the num of sents contains multiple relations
		int num_sent_multi_relation = 0;
		// the num of relations
		int num_relations = 0;
		// num of relations in each genre
		Map<String, Double> countGenre = new HashMap<String, Double>();
		Map<String, Double> countSentGenre = new HashMap<String, Double>();
		
		System.out.println("Reading training instance ...");
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		while((line = reader.readLine()) != null)
		{
			String genre = "null";
			Pattern p = Pattern.compile("^(.+)/timex2norm/");
			Matcher matcher = p.matcher(line);
			if(matcher.find())
			{
				genre = matcher.group(1);
			}
			
			boolean monoCase = line.contains("bn/") ? true : false;
			String fileName = srcDir + File.separator + line;
			
			System.out.println(fileName);
			
			Document doc = new Document(fileName, true, monoCase);
			
			// fill in text feature vector for each token
			TextFeatureGenerator.doPreprocessCheap(doc);
			for(int sent_id=0 ; sent_id<doc.getSentences().size(); sent_id++)
			{
				Sentence sent = doc.getSentences().get(sent_id);
				num_sent++;
				num_relations += sent.relationMentions.size();
				
				
				// traverse relation mentions
				for(AceRelationMention relation : sent.relationMentions)
				{
					incrementCount(countGenre, genre);
					String subtype = relation.getSubType();
					
					// print error if there exists "Artifact" subtype 
					if(subtype.equals("Artifact"))
						out.println("ERROR: Artifact subtype " + fileName);
					
					incrementCount(countType, subtype);
					
					// check if the value of relation subtype is correct
					if(!RelationTypeConstraints.relationTypeMap.keySet().contains(subtype))
					{
						out.println("ERROR: Invalid relation type " + relation.id + "\t"+ fileName + "\t" + subtype);
					}
					
					// check compatibility with type constraints
					String entityType1 = relation.arg1.getType();
					String entityType2 = relation.arg2.getType();
					List<String[]> possibleAssns = RelationTypeConstraints.getPossibleRelations(entityType1, entityType2);
					boolean compatible = false;
					for(String[] assn : possibleAssns)
					{
						if(assn[0].equals(subtype) && assn[1].equals("Arg-1") 
								&& assn[2].equals("Arg-2"))
						{
							compatible = true;
							break;
						}
					}
					if(!compatible)
					{
						out.println("ERROR: Invalid entity types " + relation.id + "\t"+ fileName
								+ "\t" + subtype + " " + entityType1 + " " + entityType2);
					}
				}
				if(sent.relationMentions.size() > 0)
				{
					num_sent_relation++;
				}
				if(sent.relationMentions.size() > 1)
				{
					num_sent_multi_relation++;
				}
				incrementCount(countSentGenre, genre);
			}
		}
		
		out.println("\n\nSummary:\n");
		out.println(String.format("Num sents: %s \t num sents that contain relation: %s ", num_sent, num_sent_relation));
		out.println(String.format("Num sents that contain multiple relations: %s ", num_sent_multi_relation));
		out.println(String.format("Num relations: %s", num_relations));
		for(String subtype : countType.keySet())
		{
			Double freq = countType.get(subtype);
			out.println(subtype + "\t" + freq);
		}
		
		out.println("\n\nDistribution of relation mentions");
		for(String genre : countGenre.keySet())
		{
			out.println(genre + "\t" + countGenre.get(genre));
		}
		out.println("\n\nDistribution of sents");
		for(String genre : countSentGenre.keySet())
		{
			out.println(genre + "\t" + countSentGenre.get(genre));
		}
		
		out.println("done");
		return;
	}

	private static void incrementCount(Map<String, Double> countType,
			String type)
	{
		Double freq = countType.get(type);
		if(freq == null)
		{
			freq = 0.0;
		}
		freq++;
		countType.put(type, freq);
	}
	
	public static void main(String[] args) throws IOException, DocumentException
	{
		File src = new File("/Users/qli/Data/ACE/ACE2005-TrainingData-V6.0/English");
		File filelist = new File("/Users/qli/Data/ACE/filelist_ACE_all");
		
		Alphabet nodeTargetAlphabet = new Alphabet();
		Alphabet edgeTargetAlphabet = new Alphabet();
		Alphabet featureAlphabet = new Alphabet();
		Controller controller = new Controller();
		
		PrintStream out = new PrintStream(new File("/Users/qli/Analysis_relation"));
		readInstanceList(src, filelist, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, true, out);
		out.close();
	}
}
