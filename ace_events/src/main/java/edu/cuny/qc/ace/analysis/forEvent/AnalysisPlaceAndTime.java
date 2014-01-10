package edu.cuny.qc.ace.analysis.forEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;

import edu.cuny.qc.ace.acetypes.AceEntity;
import edu.cuny.qc.ace.acetypes.AceEntityMention;
import edu.cuny.qc.ace.acetypes.AceEventMention;
import edu.cuny.qc.ace.acetypes.AceEventMentionArgument;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.featureGenerator.TextFeatureGenerator;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.Document;
import edu.cuny.qc.perceptron.types.Sentence;
import edu.cuny.qc.perceptron.types.Sentence.Sent_Attribute;
import edu.cuny.qc.util.TokenAnnotations;

public class AnalysisPlaceAndTime
{
	public static void readInstanceList(File srcDir, File file_list, 
			Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, 
			Controller controller, boolean learnable, PrintStream out) throws IOException, DocumentException
	{
		System.out.println("Reading training instance ...");
		
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
				if(sent.timexMentions.size() == 1)
				{
					// print event types
					for(AceEventMention event : sent.eventMentions)
					{
						boolean hasTimeArg = false;
						for(AceEventMentionArgument arg : event.arguments)
						{
							AceMention mention = arg.value;
							if(mention.getType().equals("Time"))
							{
								hasTimeArg = true;
							}
							
							// the argument is an giver, and the previous token is "from"
							if(arg.role.equals("Giver"))
							{
								int start = mention.getExtentIndices().get(0);
								if(start > 0)
								{
									List<Map<Class<?>, Object>> tokens = (List<Map<Class<?>, Object>>) sent.get(Sent_Attribute.Token_FEATURE_MAPs);
									String lemma = (String) tokens.get(start - 1).get(TokenAnnotations.LemmaAnnotation.class);
									if(lemma.equals("from"))
									{
										System.out.println("From giver " + event);
									}
								}
							}
						}
						if(!hasTimeArg)
						{
							// the Timex is only Timex in the sent but not an argument
							System.out.println(event + "\t" + sent.timexMentions.get(0));
						}
					}
				}
				
			}
		}
		
		System.out.println("done");
		return;
	}

	public static void main(String[] args) throws IOException, DocumentException
	{
		File src = new File(args[0]);
		File filelist = new File(args[1]);
		
		Alphabet nodeTargetAlphabet = new Alphabet();
		Alphabet edgeTargetAlphabet = new Alphabet();
		Alphabet featureAlphabet = new Alphabet();
		Controller controller = new Controller();
		
		PrintStream out = new PrintStream(new File(args[2]));
		readInstanceList(src, filelist, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller, true, out);
		out.close();
	}
}
