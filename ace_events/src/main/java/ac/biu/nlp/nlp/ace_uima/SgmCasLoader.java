package ac.biu.nlp.nlp.ace_uima;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.utilities.file.RecursiveFileListIterator;
import eu.excitementproject.eop.lap.LAPException;

/**
 * Loads ACE corpus files into JCases. Each corpus document gets its own JCas.<BR>
 * Implementation inspired by {@link LAPImplBase}.
 * @param rootPath path to corpus
 * @param typeAE AnalysisEngine holding the type system JCases should be initialized with
 * @author Ofer Bronstein
 * @since June 2013
 */
public class SgmCasLoader {
	/**
	 * @deprecated use the sparate methdos for getting the files and loading each CAS separately, to avoid having all CASes in the memory
	 * at the same time.
	 * @param rootPath
	 * @param typeAE
	 * @return
	 * @throws LAPException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws AceException
	 */
	public static Map<String, JCas> loadFromFolderTree(String rootPath, AnalysisEngine typeAE) throws LAPException, InstantiationException, IOException, AceException {
		Map<String, JCas> result = new HashMap<String, JCas>();
		
		if (!rootPath.contains(TOP_FOLDER_NAME)) {
			throw new AceException("Path to corpus must contain folder named \"" + TOP_FOLDER_NAME + "\"");
		}
		Pattern folderPattern = Pattern.compile("\\\\" + TOP_FOLDER_NAME + "\\\\(.+?)\\\\");
		
		RecursiveFileListIterator files = new RecursiveFileListIterator(new File(rootPath), true, new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(EXTENSION);
			}
		});
		while (files.hasNext()) {
			// Get file contents
			File file = files.next();
			String documentId = file.getName().replaceFirst(EXTENSION, "");
			String contents = new SgmFile(file).rawText();//SgmFileReader.readFile(file);
			Matcher m = folderPattern.matcher(file.getAbsolutePath());
			m.find();
			String sourceFolder = m.group(1);
			
			// Build CAS
			JCas aJCas = null; 
			try {
				aJCas = typeAE.newJCas(); 
			} catch(ResourceInitializationException e)
			{
				throw new LAPException("Failed to create a JCAS", e); 
			}
			
			// Set CAS data
			aJCas.setDocumentLanguage("EN");
			aJCas.setDocumentText(contents);
			
			if (result.containsKey(documentId)) {
				String otherUri = JCasUtil.selectSingle(result.get(documentId), DocumentMetaData.class).getDocumentUri();
				throw new AceException(String.format("Found documentId=%s, for duplicate documents: %s , %s",
						documentId, file.getAbsolutePath(), otherUri));
			}
			
			DocumentMetaData metadata = new DocumentMetaData(aJCas, 0, contents.length());
			metadata.setDocumentId(documentId);
			metadata.setDocumentUri(file.getAbsolutePath());
			metadata.setCollectionId(sourceFolder);
			metadata.addToIndexes();
			
			result.put(documentId, aJCas);
		}
		
		return result;
	}
	
	public static RecursiveFileListIterator getSgmFiles(String rootPath) throws AceException, InstantiationException {
		if (!rootPath.contains(TOP_FOLDER_NAME)) {
			throw new AceException("Path to corpus must contain folder named \"" + TOP_FOLDER_NAME + "\"");
		}
		
		RecursiveFileListIterator iter = new RecursiveFileListIterator(new File(rootPath), true, new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(EXTENSION);
			}
		});
		return iter; 
	}
	
	public static JCas loadCas(File file, AnalysisEngine typeAE) throws AceException, LAPException, IOException {
		String documentId = file.getName().replaceFirst(EXTENSION, "");
		String contents = new SgmFile(file).rawText();//SgmFileReader.readFile(file);
		Matcher m = FOLDER_PATTERN.matcher(file.getAbsolutePath());
		m.find();
		String sourceFolder = m.group(1);
		
		// Build CAS
		JCas aJCas = null; 
		try {
			aJCas = typeAE.newJCas(); 
		} catch(ResourceInitializationException e)
		{
			throw new LAPException("Failed to create a JCAS", e); 
		}
		
		// Set CAS data
		aJCas.setDocumentLanguage("EN");
		aJCas.setDocumentText(contents);
		
		if (fileToDocId.rightContains(documentId)) {
			File otherfile = fileToDocId.rightGet(documentId);
			throw new AceException(String.format("Found documentId=%s, for duplicate documents: %s , %s",
					documentId, file.getAbsolutePath(), otherfile));
		}
		
		DocumentMetaData metadata = new DocumentMetaData(aJCas, 0, contents.length());
		metadata.setDocumentId(documentId);
		metadata.setDocumentUri(file.getAbsolutePath());
		metadata.setCollectionId(sourceFolder);
		metadata.addToIndexes();

		fileToDocId.put(file, documentId);
		
		return aJCas;
	}
	
	public static String getDocId(File file) {
		return fileToDocId.leftGet(file);
	}
	
	public static final String TOP_FOLDER_NAME = "corpus";
	public static final String EXTENSION = ".sgm";
	public static final Pattern FOLDER_PATTERN = Pattern.compile("\\\\" + TOP_FOLDER_NAME + "\\\\(.+?)\\\\");
	public static BidirectionalMap<File,String> fileToDocId = new SimpleBidirectionalMap<File,String>();
}
