package edu.cuny.qc.ace.analysis.forEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.dom4j.DocumentException;

import edu.cuny.qc.perceptron.types.GlarfDocument;

/**
 * This class does analysis for glarf data
 * @author che
 *
 */
public class AnalysisGlarf
{
	/**
	 * give a file list, make analysis on all files
	 * @param srcDir
	 * @param file_list
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void doAnalysis(File srcDir, File file_list, File desDir) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		while((line = reader.readLine()) != null)
		{
			System.out.println("Processing " + line);
			try
			{
				GlarfDocument doc = new GlarfDocument(srcDir + File.separator + line);
				File output = new File(desDir, line);
				output.getParentFile().mkdirs();
				PrintWriter out = new PrintWriter(output);
				doc.printEventRelatedTuples(out);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				e.printStackTrace();
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		reader.close();
	}

	static public void main(String[] args) throws DocumentException, IOException
	{
		File srcDir = new File("/Users/che/Data/ACE-2005-GLARF-output");
		File desDir = new File("/Users/che/Glarf_Analysis");
		File file_list = new File("/Users/che/Data/ACE-2005-GLARF-output/filelist");
		
		doAnalysis(srcDir, file_list, desDir);
	}
}
