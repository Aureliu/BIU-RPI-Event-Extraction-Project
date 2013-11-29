package edu.cuny.qc.perceptron.learnCurve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import edu.cuny.qc.ace.acetypes.AceDocument;
import edu.cuny.qc.perceptron.core.Pipeline;
import edu.cuny.qc.util.LoggerUtils;


public class LearningCurve {
	protected List<List<List<String>>> allChunks = new ArrayList<List<List<String>>>();
	protected String trainDocsList;
	protected String devDocsList;
	protected String testDocsList;
	protected String outputFolder;
	protected Integer numIterations=null;
	protected Integer chunkSize=null;
	//protected Integer numChunks=null;
	protected Integer lastTrainIteration=-1;
	protected Integer lastTrainChunk=-1;
	protected Integer lastDecodeIteration=-1;
	protected Integer lastDecodeChunk=-1;
	//protected Boolean shouldChunkify=false;
	//protected Boolean shouldTrain=false;
	//protected Boolean shouldDecode=false;

	// Args for training
	public static final String ACE_PATH = "C:\\Lab\\Datasets\\Ace\\Files\\qi\\";
	//public static final String TRAINING_LIST = "C:\\Java\\Git\\breep\\ACE_EVENT\\run\\input\\new_filelist_ACE_training.txt";
	//public static final String DEV_LIST = "C:\\Java\\Git\\breep\\ACE_EVENT\\run\\input\\new_filelist_ACE_dev.txt";
	public static final String OTHER_ARGS_STR = "beamSize=4 maxIterNum=20 skipNonEventSent=true avgArguments=true skipNonArgument=true useGlobalFeature=true addNeverSeenFeatures=true crossSent=false crossSentReranking=false order=0 evaluatorType=1";
	public static final String[] OTHER_ARGS_ARR = OTHER_ARGS_STR.split(" ");
	//public static final List<String> OTHER_ARGS_LIST = Arrays.asList(OTHER_ARGS_STR.split(" "));
	
	public static final String FILENAME_PATTERN = "__iter%02d_chunk%03d_docs%03d_mentions%04d.txt";
	public static final String MODEL_FILENAME =       "%s\\Model" + FILENAME_PATTERN;
	public static final String TRAIN_LIST_FILENAME =  "%s\\TrainList" + FILENAME_PATTERN;
	public static final String OUT_FILENAME =         "%s\\Out" + FILENAME_PATTERN;
	public static final String ERR_FILENAME =         "%s\\Err" + FILENAME_PATTERN;

	public static final String FILENAME_PREFIX = "learning_curve_";
	public static final String FILENAME_RUN_SPEC =        FILENAME_PREFIX + "run_spec.txt";
	public static final String FILENAME_POINTER_TRAIN =   FILENAME_PREFIX + "last_completed_training.txt";
	public static final String FILENAME_POINTER_DECODE =  FILENAME_PREFIX + "last_completed_decoding.txt";
	public static final String FILENAME_DECODE_RESULTS =  FILENAME_PREFIX + "decoding_results.txt";
	public static final String FILENAME_DONE =            FILENAME_PREFIX + "DONE";
	protected File fileRunSpec;
	protected File filePointerTrain;
	protected File filePointerDecode;
	protected File fileDecodeResults;
	protected File fileDone;
	
	public static final String CHUNK_SEP = "===";

	protected static final Random RANDOM = new Random();
	protected static Logger logger = LoggerUtils.initLog(LearningCurve.class);

	protected static List<String> loadFileToList(File f) throws IOException {
		Reader reader = new FileReader(f);
		List<String> outList = new LinkedList<String>();
		String line;
		BufferedReader bufferedReader = new BufferedReader(reader);
		try {
			while ((line = bufferedReader.readLine()) != null) 
				outList.add(line);
			logger.info(String.format("Reading %d lines from: %s", outList.size(), f.getAbsolutePath()));
			return outList;
		}
		finally {
			bufferedReader.close();
		}
	}

	protected void loadRunSpec(String[] args) throws IOException {
		outputFolder = args[0];
		fileRunSpec = new File(outputFolder, FILENAME_RUN_SPEC);
		filePointerTrain = new File(outputFolder, FILENAME_POINTER_TRAIN);
		filePointerDecode = new File(outputFolder, FILENAME_POINTER_DECODE);
		fileDecodeResults = new File(outputFolder, FILENAME_DECODE_RESULTS);
		fileDone = new File(outputFolder, FILENAME_DONE);

		if (fileRunSpec.isFile()) {
			assert args.length==0 : "Run spec already exist, resuming existing run: command line arguments must not be passed";
			
			List<String> lines = loadFileToList(fileRunSpec);
			
			// Get params from first line
			Pattern pattern = Pattern.compile("numIterations=(\\d+),\\s*chunkSize=(\\d+),\\s*trainDocsList=(.+?),\\s*devDocsList=(.+?),\\s*testDocsList=(.+?)\\s*outputFolder=(.+?)\\s*");
			Matcher m = pattern.matcher(lines.get(0));
			m.matches();
			numIterations = Integer.parseInt(m.group(1));
			chunkSize = Integer.parseInt(m.group(2));
			trainDocsList = m.group(3);
			devDocsList = m.group(4);
			testDocsList = m.group(5);
			outputFolder = m.group(6);
			//numChunks = Integer.parseInt(m.group(3));
			
			assert lines.get(1).trim().isEmpty() : "Run Spec File: Second line must be empty!";
			
			// Get chunks from all other lines
			List<List<String>> iteration = null;
			for (int i=2; i<lines.size(); i++) {
				if (lines.get(i).equals(CHUNK_SEP)) {
					iteration = new ArrayList<List<String>>();
					allChunks.add(iteration);
				}
				else {
					String[] chunks = lines.get(i).split(",");
					iteration.add(Arrays.asList(chunks));
				}
			}
		}
		else { // meaning that the file doesn't exist
			if (args.length != 6) {
				throw new IllegalArgumentException("Usage: <output folder> <train docs list> <dev docs list> <test doc list> <num iterations> <chunk size>");
			}
			trainDocsList = args[1];
			devDocsList = args[2];
			testDocsList = args[3];
			numIterations = Integer.parseInt(args[4]);
			chunkSize = Integer.parseInt(args[5]);
			buildChunks();
			dumpSpecToFile();
		}

		logChunks();
	}
	
	protected void buildChunks() throws IOException {
		List<String> lines = loadFileToList(new File(trainDocsList));
		for (int i=0; i<numIterations; i++) {
			List<List<String>> iteration = new ArrayList<List<String>>();
			allChunks.add(iteration);
			List<String> chunk;
			List<String> linesTemp = new ArrayList<String>(lines); //make a copy we can delete from without destroying the original list
			while (!linesTemp.isEmpty()) {
				chunk = new ArrayList<String>();
				iteration.add(chunk);
				for (int j=0; j<chunkSize; j++) {
					if (linesTemp.isEmpty()) {
						break;
					}
					int pos = RANDOM.nextInt(linesTemp.size());
					chunk.add(linesTemp.remove(pos));
				}
			}
		}
	}
	
	protected void dumpSpecToFile() throws FileNotFoundException {
		PrintWriter out = null;
		try {
			out = new PrintWriter(fileRunSpec);
			out.printf("numIterations=%s,chunkSize=%s,trainDocsList=%s,devDocsList=%s,testDocsList=%s\n\n",
					numIterations, chunkSize, trainDocsList, devDocsList, testDocsList);
			for (List<List<String>> iteration : allChunks) {
				out.printf(CHUNK_SEP + "\n");
				for (List<String> chunk : iteration) {
					out.printf(StringUtils.join(chunk, ",") + "\n");
				}
			}
		}
		finally {
			out.close();
		}
	}
	
	protected void logChunks() {
		logger.info("Chunks are: " + allChunks);
		logger.info("Chunks statistics: ");
		logger.info("   - Num iterations: " + allChunks.size());

		StringBuffer iterationSizes = new StringBuffer();
		StringBuffer chunkSizes = new StringBuffer();
		
		for (List<List<String>> iteration : allChunks) {
			iterationSizes.append(iteration.size() + ",");
			chunkSizes.append("[");
			for (List<String> chunk : iteration) {
				chunkSizes.append(chunk.size() + ",");
			}
			chunkSizes.append("]");
		}
		
		logger.info("   - Num chunks (by iteration): [" + iterationSizes + "]");
		logger.info("   - Chunk Sizes: [" + chunkSizes + "]");
	}

	protected List<Integer> readPointerFile(File f) throws IOException {
		List<String> lines = loadFileToList(f);
		assert lines.size()==1 : "File should have exactly one line: " + f;
		String[] line = lines.get(0).split(",");
		assert line.length==2 : "Line should have exactly two elements, got: " + line;
		List<Integer> result = new ArrayList<Integer>();
		result.add(Integer.parseInt(line[0]));
		result.add(Integer.parseInt(line[1]));
		return result;
	}
	
	protected void doTraining() throws IOException {
		if (filePointerTrain.isFile()) {
			List<Integer> content = readPointerFile(filePointerTrain);
			lastTrainIteration = content.get(0);
			lastTrainChunk = content.get(1);
			logger.info(String.format("Loaded pointer from file: last completed train iteration=%s, last completed train chunk=%s", lastTrainIteration, lastTrainChunk));
		}
		else {
			lastTrainIteration = -1;
			lastTrainChunk = -1;
			logger.info(String.format("No train pointer file found, starting from iteration 0, chunk 0."));
		}
		
		// This has to be outside of the loop, as it should only effect the first iteration we process 
		int j = lastTrainChunk+1;
		
		// Note that if we've completed the training before, as indicated in
		// the pointer file - we ever enter this loop
		for (int i=lastTrainIteration+1; i<allChunks.size(); i++) {
			List<List<String>> iteration = allChunks.get(i);
			List<String> trainSet = new ArrayList<String>();
			int mentionsInTrainSet = 0;
			logger.info(String.format("Starting iteration %s", i));
			for (; j<iteration.size(); j++) {
				List<String> chunk = iteration.get(j);
				trainSet.addAll(chunk);
				
				int mentionsInChunk = 0;
				for (String docname : chunk) {
					AceDocument doc = new AceDocument(ACE_PATH + docname + ".sgm", ACE_PATH + docname + ".apf.xml");
					mentionsInChunk += doc.eventMentions.size();
				}
				mentionsInTrainSet += mentionsInChunk;
				logger.info(String.format("Starting chunk %s, has %s event mentions. Total of %s event mention in this train set.",
						j, mentionsInChunk, mentionsInTrainSet));
				
				String tempTrainDocList = String.format(TRAIN_LIST_FILENAME, outputFolder, i, j, trainSet.size(), mentionsInTrainSet);
				PrintWriter f = null;
				try {
					f = new PrintWriter(tempTrainDocList);
					f.write(StringUtils.join(trainSet, "\n") + "\n");
				}
				finally {
					f.close();
				}
				
				String[] args = new String[] {
						ACE_PATH,
						tempTrainDocList,
						String.format(MODEL_FILENAME, outputFolder, i, j, trainSet.size(), mentionsInTrainSet),
						devDocsList,
				};
				ArrayUtils.addAll(args, OTHER_ARGS_ARR);
				//args.addAll(OTHER_ARGS_LIST);
				//String[] argsArray = (String[]) args.<String>toArray();

				logger.info(String.format("Running training with args: " + args));
				System.setOut(new PrintStream(String.format(OUT_FILENAME, outputFolder, i, j, trainSet.size(), mentionsInTrainSet)));
				System.setErr(new PrintStream(String.format(ERR_FILENAME, outputFolder, i, j, trainSet.size(), mentionsInTrainSet)));
				Pipeline.main(args);
				logger.info("Returned from training");
				
				try {
					f = new PrintWriter(filePointerTrain);
					f.printf("%s,%s", i, j);
				}
				finally {
					f.close();
				}
				logger.info(String.format("Updated train pointer: %s,%s", i, j));
			}
			j=0;
		}
	}
		
	public void run(String[] args) {
		try {
			logger.info(String.format("Starting learning curve, with args: %s", Arrays.asList(args)));
			
			// Understand current state and prepare
			if (fileDone.isFile()) {
				logger.info("This run is already done from before! Nothing to do! Exiting.");
				return;
			}
			
			loadRunSpec(args);
			doTraining();

		}
		catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public static void main(String[] args) {
		LearningCurve prog = new LearningCurve();
		prog.run(args);
	}

}
