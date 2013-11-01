package edu.cuny.qc.perceptron.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.dom4j.DocumentException;

import edu.cuny.qc.ace.acetypes.Scorer;
import edu.cuny.qc.util.RecursiveFileListIterator;

/**
 * Another interface for decoding.
 * Exactly like {@link edu.cuny.qc.perceptron.core.Decoder}, only that
 * it allows to input two different folders - one for predicated apf.xml,
 * and one for gold apf.xml.<BR>
 * It also converts the extensions of sgm.xml files (as output by ENIE)
 * to apf.xml.
 * 
 * @author Ofer Bronstein
 * @since October 2013
 */
public class DecoderOverPredicted {

	public static String EXTENSION_ORIG = ".sgm.xml";
	public static String EXTENSION_NEW =  ".apf.xml";

	public static void main(String[] args) throws IOException, DocumentException, InstantiationException {
		if(args.length < 5)	{
			System.out.println("Usage:");
			System.out.println("args[0]: model");
			System.out.println("args[1]: gold dir");
			System.out.println("args[2]: predicted dir");
			System.out.println("args[3]: file list");
			System.out.println("args[4]: output dir");
			System.exit(-1);
		}
		
		// convert .sgm.xml to .apf.xml
		File predictedDir = new File(args[2]);
		RecursiveFileListIterator iterator = new RecursiveFileListIterator(predictedDir, true, new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(EXTENSION_ORIG);
			}
		});
		while (iterator.hasNext()) {
			File file = iterator.next();
			String newName = file.getAbsolutePath().replaceAll(EXTENSION_ORIG, EXTENSION_NEW);
			boolean res = file.renameTo(new File(newName));
			if (!res) {
				throw new IOException(String.format("Could not format %s to %s", file.getAbsolutePath(), newName));
			}
		}
		
		Decoder.main(new String[] {args[0], args[2], args[3], args[4], Decoder.OPTION_NO_SCORING});

		// get score
		File outputFile = new File(args[4] + File.separator + "Score");
		Scorer.main(new String[]{args[1], args[4], args[3], outputFile.getAbsolutePath()});

	}

}
