package edu.cuny.qc.ace.analysis.forValues;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.dom4j.DocumentException;

import edu.cuny.qc.ace.acetypes.AceDocument;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceValueMention;

/**
 * This class is to analysize the Values in ENIE output and gold standard
 *  
 * @author che
 *
 */
public class AnalysisForValues
{
	static class Accuracy
	{
		public Accuracy(String type)
		{
			this.type = type;
		}
		
		String type = "null";
		
		public double count_correct = 0.0;
		public double count_gold = 0.0;
		public double count_ans = 0.0;
		
		public double f1 = 0.0;
		public double prec = 0.0;
		public double recall = 0.0;
		
		public void calc()
		{
			prec = count_correct / count_ans;
			recall = count_correct / count_gold;
			f1 = 2 * prec * recall / (prec + recall);
		}
		
		@Override
		public String toString()
		{
			String ret = type + ":\n";
			ret += String.format("Prec. %.2f \t Recall %.2f \t F1 %.2f \t Missing %.2f (%.2f)", prec, recall, f1, (count_gold - count_correct), count_gold);
			return ret;
		}
	}
	
	public static void readInstanceList(File goldDir, File testDir, File file_list, PrintStream out) throws IOException, DocumentException
	{
		// record the accuracy of Values
		Accuracy valueAcc = new Accuracy("value");
		Accuracy nominalAcc = new Accuracy("nominal+pronoun");
		
		System.out.println("Reading training instance ...");
		
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		while((line = reader.readLine()) != null)
		{
			String gold_apf = goldDir.getAbsolutePath() + File.separator + line + ".apf.xml";
			String gold_sgm = goldDir.getAbsolutePath() + File.separator + line + ".sgm";
			AceDocument gold_doc = new AceDocument(gold_sgm, gold_apf);
			
			String test_apf = testDir.getAbsolutePath() + File.separator + line + ".apf.xml";
			String test_sgm = testDir.getAbsolutePath() + File.separator + line + ".sgm";
			AceDocument test_doc = new AceDocument(test_sgm, test_apf);
			
			// compare values
			List<AceValueMention> gold_values = gold_doc.valueMentions;
			List<AceValueMention> test_values = test_doc.valueMentions;
			
			valueAcc.count_gold += gold_values.size();
			valueAcc.count_ans += test_values.size();
			
			nominalAcc.count_gold += countNotNAM(gold_doc.entityMentions);
			nominalAcc.count_ans += countNotNAM(test_doc.entityMentions);
			
			// print gold values
			for(AceValueMention mention_gold : gold_values)
			{
				boolean correct = false;
				for(AceValueMention mention : test_values)
				{
					if(mention.extent.overlap(mention_gold.extent) 
							&& mention.getType().equals(mention_gold.getType()))
					{
						// correct
						correct = true;
						break;
					}
				}
				
				if(!correct)
				{
					System.out.print("Missing\t");
				}
				
				System.out.println(mention_gold.getType() + "\t" + mention_gold.getHeadText().replace("\n", " "));
				
			}
			
			// evaluate Value
			for(AceValueMention mention : test_values)
			{
				boolean correct = false;
				for(AceValueMention mention_gold : gold_values)
				{
					if(mention.extent.overlap(mention_gold.extent) 
							&& mention.getType().equals(mention_gold.getType()))
					{
						// correct
						correct = true;
						break;
					}
				}
				if(correct)
				{
					valueAcc.count_correct++;
				}
			}
			
			// evaluate entities
			for(AceEntityMention mention : test_doc.entityMentions)
			{
				if(mention.type.equals("NAM"))
				{
					continue;
				}
				boolean correct = false;
				for(AceEntityMention mention_gold : gold_doc.entityMentions)
				{
					if(mention_gold.type.equals("NAM"))
					{
						continue;
					}
					if(mention.head.overlap(mention_gold.head) 
							&& mention.getType().equals(mention_gold.getType()))
					{
						// correct
						correct = true;
						break;
					}
				}
				if(correct)
				{
					nominalAcc.count_correct++;
				}
			}
		}
		
		System.out.println("\n\nsummary:");
		// print stats
		valueAcc.calc();
		System.out.println(valueAcc.toString());
		// print stats
		nominalAcc.calc();
		System.out.println(nominalAcc.toString());
		
		System.out.println("done");
		return;
	}

	private static double countNotNAM(List<AceEntityMention> entityMentions)
	{
		double ret = 0.0;
		for(AceEntityMention mention : entityMentions)
		{
			if(!mention.type.equals("NAM"))
			{
				ret++;
			}
		}
		return ret;
	}

	public static void main(String[] args) throws IOException, DocumentException
	{
		File src = new File("/Users/qli/Data/ACE/ACE2005-TrainingData-V6.0/English");
		File test = new File("/Users/qli/experiment/system_output_enie");
		File filelist = new File("/Users/qli/experiment/new_filelists/new_filelist_ACE_test");
		
		PrintStream out = new PrintStream(new File("/Users/qli/values"));
		readInstanceList(src, test, filelist, out);
		out.close();
	}
}
