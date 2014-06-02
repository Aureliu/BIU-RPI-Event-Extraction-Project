package ac.biu.nlp.nlp.ace_uima;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * Loads and stores one .sgm file.
 * Copied from Idan Szpektor's implementation in Trunk/common/Projects/corpora/org.BIU.nlp.corpora.ace
 */
public class SgmFile{
	public SgmFile(File iSGMFile) throws IOException
	{
		BufferedReader reader;
		boolean inText = false;
		int ci;
		char c;
		StringBuffer s = new StringBuffer();
		StringBuffer label = new StringBuffer();
		String labelStr;


		reader = new BufferedReader(new FileReader(iSGMFile));
		while((ci = reader.read()) != -1){
			c = (char)ci;
			if(c != '\n'){
				if(c == '\r' || c == '\t') //TODO Comment out this line and next line to keep \r,\t
					c = ' ';

				if(inText){
					if(c != '<')
						s.append(c);
					else{
						inText = false;
						label = new StringBuffer();
					}
				}
				if(c == '>'){
					inText = true;
					labelStr = label.toString().toLowerCase();
					if(labelStr.equals("text"))
						m_startTextPos = s.length();
					else if(labelStr.equals("/text"))
						m_endTextPos = s.length()-1;
				}
				else if(c != '<')
					label.append(c);
			}
			else if(inText)
				s.append(' ');
				//s.append('\n');  //TODO Comment out previous line and uncomment this line to keep \n
		}


		reader.close();

		m_sgmText = s.toString();
		if(m_endTextPos < 0)
			m_endTextPos = m_sgmText.length()-1;
		
		// Hack: some specific files need their "&amp;" replaced with "&", and some don't
		if (alternateProcessingFiles.contains(iSGMFile.getName())) {
			m_sgmText = m_sgmText.replaceAll("&amp;", "&");
		}
	}
	
	
	
	public int startTextPos()
	{
		return m_startTextPos;
	}
	
	
	
	public int endTextPos()
	{
		return m_endTextPos;
	}
	
	
	
	public String rawText()
	{
		return m_sgmText;
	}
	
	
	
	private int m_startTextPos = 0;
	private int m_endTextPos = -1;
	private String m_sgmText;
	
	public static final List<String> alternateProcessingFiles = Arrays.asList(new String[] {
			"BACONSREBELLION_20050209.0721.sgm",
			"BACONSREBELLION_20050226.1317.sgm",
			"FLOPPINGACES_20041114.1240.039.sgm",
			"alt.vacation.las-vegas_20050109.0133.sgm",
			"CNN_CF_20030304.1900.04.sgm",
			"CNN_ENG_20030616_130059.25.sgm",
			"FLOPPINGACES_20050217.1237.014.sgm",
	});
}
