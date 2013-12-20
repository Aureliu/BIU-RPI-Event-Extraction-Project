package edu.cuny.qc.perceptron.learnCurve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import edu.cuny.qc.ace.acetypes.AceDocument;
import edu.cuny.qc.ace.acetypes.Scorer;
import edu.cuny.qc.ace.acetypes.Scorer.Stats;
import edu.cuny.qc.perceptron.core.Decoder;
import edu.cuny.qc.perceptron.core.Pipeline;
import edu.cuny.qc.util.LoggerUtils;
import edu.cuny.qc.util.TypeConstraints;


public class LearningCurve {
	protected List<List<List<String>>> allChunks = new ArrayList<List<List<String>>>();
	protected List<String> allEventTypes = null;
	protected String trainDocsList;
	protected String devDocsList;
	protected String testDocsList;
	protected String outputFolder;
	protected Integer numIterations=null;
	protected Integer chunkSize=null;
	protected Boolean singleTypes=null;
	protected Integer maxType=null;
//	protected Integer lastTrainIteration=-1;
//	protected Integer lastTrainChunk=-1;
//	protected Integer lastDecodeIteration=-1;
//	protected Integer lastDecodeChunk=-1;
//	protected Integer lastScoreIteration=-1;
//	protected Integer lastScoreChunk=-1;

	// Args for training
	public static final String ACE_PATH = "corpus/qi/";
	//public static final String TRAINING_LIST = "C:\\Java\\Git\\breep\\ACE_EVENT\\run\\input\\new_filelist_ACE_training.txt";
	//public static final String DEV_LIST = "C:\\Java\\Git\\breep\\ACE_EVENT\\run\\input\\new_filelist_ACE_dev.txt";
	public static final String TRAIN_OTHER_ARGS_STR = "beamSize=4 maxIterNum=20 skipNonEventSent=true avgArguments=true skipNonArgument=true useGlobalFeature=true addNeverSeenFeatures=true crossSent=false crossSentReranking=false order=0 evaluatorType=1 learnBigrams=true";
	public static final String[] TRAIN_OTHER_ARGS_ARR = TRAIN_OTHER_ARGS_STR.split(" ");
	//public static final List<String> OTHER_ARGS_LIST = Arrays.asList(OTHER_ARGS_STR.split(" "));

	
	public static final String FILENAME_PATTERN = "__iter%02d_type%02d_chunk%03d_docs%03d_mentions%04d.txt";
	public static final String FOLDERNAME_PATTERN = "DIR__iter%02d_chunk%03d_docs%03d_mentions%04d.txt.";
	public static final String MODEL_FILENAME =       "%s/Model" + FILENAME_PATTERN;
	public static final String TRAIN_LIST_FILENAME =  "%s/TrainList" + FILENAME_PATTERN;
//	public static final String OUT_TRAIN_FILENAME =   "%s/OutTrain" + FILENAME_PATTERN;
//	public static final String OUT_DECODE_FILENAME =  "%s/OutDecode" + FILENAME_PATTERN;
//	public static final String ERR_TRAIN_FILENAME =   "%s/ErrTrain" + FILENAME_PATTERN;
//	public static final String ERR_DECODE_FILENAME =  "%s/ErrDecode" + FILENAME_PATTERN;
	public static final String OUT_ALL_FILENAME =     "%s/OutAll" + FILENAME_PATTERN;
	public static final String ERR_ALL_FILENAME =     "%s/ErrAll" + FILENAME_PATTERN;
	public static final String SCORE_FILENAME =       "%s/Score" + FILENAME_PATTERN;

	public static final String FILENAME_PREFIX = "^learning_curve_";
	public static final String FILENAME_RUN_SPEC =        FILENAME_PREFIX + "run_spec.txt";
	public static final String FILENAME_POINTER_TRAIN =   FILENAME_PREFIX + "last_completed_training.txt";
	public static final String FILENAME_POINTER_DECODE =  FILENAME_PREFIX + "last_completed_decoding.txt";
	public static final String FILENAME_POINTER_SCORE =   FILENAME_PREFIX + "last_completed_scoring.txt";
	public static final String FILENAME_DECODE_RESULTS =  FILENAME_PREFIX + "decoding_results.txt";
	public static final String FILENAME_DONE =            FILENAME_PREFIX + "DONE";
	protected File fileRunSpec;
	protected File filePointerTrain;
	protected File filePointerDecode;
	protected File filePointerScore;
	protected File fileDecodeResults;
	protected File fileDone;
	
	public static final String CHUNK_SEP = "===";
	public static final int JOKER_TYPE_NUM = -2;
	public static final String JOKER_TYPE_NAME = "#ALL";

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
			logger.info(String.format("Reading %d lines from: %s", outList.size(), f.getCanonicalPath()));
			return outList;
		}
		finally {
			bufferedReader.close();
		}
	}

	protected void loadRunSpec(String[] args) throws IOException {
		if (fileRunSpec.isFile()) {
			assert args.length==0 : "Run spec already exist, resuming existing run: command line arguments must not be passed";
			
			List<String> lines = loadFileToList(fileRunSpec);
			
			// Get params from first line
			Pattern pattern = Pattern.compile("numIterations=(\\d+),\\s*chunkSize=(\\d+),\\s*singleTypes=(\\w+),\\s*maxType=(\\d+),\\s*trainDocsList=(.+?),\\s*devDocsList=(.+?),\\s*testDocsList=(.+?)");
			Matcher m = pattern.matcher(lines.get(0));
			m.matches();
			numIterations = Integer.parseInt(m.group(1));
			chunkSize = Integer.parseInt(m.group(2));
			singleTypes = Boolean.parseBoolean(m.group(3));
			maxType = Integer.parseInt(m.group(4));
			trainDocsList = m.group(5);
			devDocsList = m.group(6);
			testDocsList = m.group(7);
			//outputFolder = m.group(6);
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
			populateTypes();
		}
		else { // meaning that the file doesn't exist
			if (args.length != 7) {
				throw new IllegalArgumentException("Usage: <output folder> <train docs list> <dev docs list> <test doc list> <num iterations> <chunk size> <singleTypes>");
			}
			trainDocsList = args[1];
			devDocsList = args[2];
			testDocsList = args[3];
			numIterations = Integer.parseInt(args[4]);
			chunkSize = Integer.parseInt(args[5]);
			singleTypes = Boolean.parseBoolean(args[6]);
			buildChunks();
			populateTypes();
			dumpSpecToFile();
		}

		logChunks();
	}
	
	protected void populateTypes() {
		if (singleTypes) {
			allEventTypes = new ArrayList<String>(TypeConstraints.eventTypeMap.keySet());
			Collections.sort(allEventTypes); // Alphabetically!
			maxType = allEventTypes.size()-1;
		}
//		else {
//			allEventTypes = Arrays.asList(new String[] {ITERATING_ALL_TYPES});
//		}
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
			out.printf("numIterations=%s,chunkSize=%s,singleTypes=%s,maxType=%s,trainDocsList=%s,devDocsList=%s,testDocsList=%s\n\n",
					numIterations, chunkSize, singleTypes, maxType, trainDocsList, devDocsList, testDocsList);
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
		assert line.length==3 : "Line should have exactly three elements, got: " + line;
		List<Integer> result = new ArrayList<Integer>();
		result.add(Integer.parseInt(line[0]));
		result.add(Integer.parseInt(line[1]));
		result.add(Integer.parseInt(line[2]));
		return result;
	}
	
	protected String getEventTypeName(int t) {
		if (t==JOKER_TYPE_NUM) {
			return JOKER_TYPE_NAME;
		}
		else {
			return allEventTypes.get(t);
		}
	}
	
	protected void doTraining() throws IOException, DocumentException {
		//doAction("train", new TrainAction(), filePointerTrain, lastTrainIteration, lastTrainChunk, OUT_ALL_FILENAME, ERR_ALL_FILENAME);
		doAction("train", new TrainAction(), filePointerTrain, OUT_ALL_FILENAME, ERR_ALL_FILENAME);
	}
	
	protected void doDecoding() throws IOException, DocumentException {
		//doAction("decode", new DecodeAction(), filePointerDecode, lastDecodeIteration, lastDecodeChunk, OUT_ALL_FILENAME, ERR_ALL_FILENAME);
		doAction("decode", new DecodeAction(), filePointerDecode, OUT_ALL_FILENAME, ERR_ALL_FILENAME);
	}
	
	protected void doScoring() throws IOException, DocumentException {
		//doAction("score", new ScoreAction(), filePointerScore, lastScoreIteration, lastScoreChunk, OUT_ALL_FILENAME, ERR_ALL_FILENAME);
		doAction("score", new ScoreAction(), filePointerScore, OUT_ALL_FILENAME, ERR_ALL_FILENAME);
	}
	
	protected void doAction(String actionLabel, Action action, File filePointer, String outFileName, String errFileName) throws IOException, DocumentException {
		logger.info(String.format("##### Starting %s", actionLabel));
		int tFirst = -800;
		int tLast = -800;
		Integer lastIteration = null;
		Integer lastType = null;
		Integer lastChunk = null; 
		if (filePointer.isFile()) {
			List<Integer> content = readPointerFile(filePointer);
			lastIteration = content.get(0);
			lastType = content.get(1);
			lastChunk = content.get(2);
			logger.info(String.format("Loaded %s pointer from file: last completed train iteration=%s, last completed type=%s, last completed train chunk=%s", actionLabel, lastIteration, lastType, lastChunk));
		}
		else {
			lastIteration = -1;
			lastChunk = -1;
			String logType;
			if (singleTypes) {
				lastType = -1;
				logType = "type 0";
			}
			else {
				lastType = JOKER_TYPE_NUM;
				logType = "not iterating types";
			}
			logger.info(String.format("No %s pointer file found, starting from iteration 0, %s, chunk 0.", actionLabel, logType));
		}
		
		if (lastType == JOKER_TYPE_NUM) {
			tFirst = JOKER_TYPE_NUM;
			tLast = JOKER_TYPE_NUM+1;
		}
		else {
			tFirst = 0;
			tLast = maxType+1;
		}
		
		// This has to be outside of the loop, as it should only effect the first iteration we process 
		//int j = lastChunk+1;
		
		// Note that if we've completed the training before, as indicated in
		// the pointer file - we ever enter this loop
		//for (int i=lastIteration+1; i<allChunks.size(); i++) {
		for (int i=0; i<allChunks.size(); i++) {
			logger.info(String.format("Starting iteration %s", i));
			List<List<String>> iteration = allChunks.get(i);
			
			//for (String eventType: allEventTypes) {
			for (int t=tFirst; t<tLast; t++) {
				String eventType = getEventTypeName(t);
			
				List<String> trainSet = new ArrayList<String>();
				int mentionsInTrainSet = 0;
				logger.info(String.format("Iteration %s: starting type %s (%s)", i, t, eventType));
				//for (; j<iteration.size(); j++) {
				for (int j=0; j<iteration.size(); j++) {
					List<String> chunk = iteration.get(j);
					trainSet.addAll(chunk);
					
					int mentionsInChunk = 0;
					for (String docname : chunk) {
//						if (docname.contains("APW_ENG_20030603.0303")) {
//							int u = 98;
//						}
						AceDocument doc = new AceDocument(ACE_PATH + docname + ".sgm", ACE_PATH + docname + ".apf.xml");
						if (!eventType.equals(JOKER_TYPE_NAME)) {
							doc.setSingleEventType(eventType);
						}
						mentionsInChunk += doc.eventMentions.size();
					}
					mentionsInTrainSet += mentionsInChunk;
					
					// OK, changing strategy - we check if we need to do the action only HERE, not before.
					// This is since we anyway need to go through all the training documents from the top,
					// to collect stats of total number of docs and total number of mentions.
					if ( (i == lastIteration) && (t == lastType) && (j > lastChunk) ||
						 (i == lastIteration) && (t > lastType) ||
						 (i > lastIteration) )
					{
						logger.info(String.format("Starting chunk %s, event type %s (%s), has %s event mentions. Total of %s event mentions in this train set.",
								j, t, eventType, mentionsInChunk, mentionsInTrainSet));
						
						// Set output and error stream - appending to an existing file, if exists
						FileOutputStream outStream = new FileOutputStream(String.format(outFileName, outputFolder, i, t, j, trainSet.size(), mentionsInTrainSet), true);
						FileOutputStream errStream = new FileOutputStream(String.format(errFileName, outputFolder, i, t, j, trainSet.size(), mentionsInTrainSet), true);
						System.setOut(new PrintStream(outStream));
						System.setErr(new PrintStream(errStream));
	
						action.go(outputFolder, i, t, j, eventType, trainSet, mentionsInTrainSet, devDocsList, testDocsList);
						
						PrintWriter f = null;
						try {
							f = new PrintWriter(filePointer);
							f.printf("%s,%s,%s", i, t, j);
						}
						finally {
							f.close();
						}
						logger.info(String.format("Updated %s pointer: %s,%s,%s", actionLabel, i, t, j));
					}
				}
			}
			//j=0;
		}
		logger.info(String.format("##### Finished %s", actionLabel));
	}
		
	public void run(String[] args) {
		try {
			logger.info(String.format("Starting learning curve, with args: %s", Arrays.asList(args)));

			// using given output folder
			outputFolder = args[0];
			fileRunSpec = new File(outputFolder, FILENAME_RUN_SPEC);
			filePointerTrain = new File(outputFolder, FILENAME_POINTER_TRAIN);
			filePointerDecode = new File(outputFolder, FILENAME_POINTER_DECODE);
			filePointerScore = new File(outputFolder, FILENAME_POINTER_SCORE);
			fileDecodeResults = new File(outputFolder, FILENAME_DECODE_RESULTS);
			fileDone = new File(outputFolder, FILENAME_DONE);

			// Understand current state and prepare
//			if (fileDone.isFile()) {
//				logger.info("This run is already done from before! Nothing to do! Exiting.");
//				return;
//			}
			
			loadRunSpec(args);
			doTraining();
			doDecoding();
			doScoring();
		}
		catch (Exception e) {
			logger.error("", e);
		}
	}
	
	private interface Action {
		public void go(String outputFolder, int i, int t, int j, String eventType, List<String> trainSet, int mentionsInTrainSet, String devDocsList, String testDocsList) throws IOException, DocumentException;
	}
	
	private class TrainAction implements Action {
		@Override
		public void go(String outputFolder, int i, int t, int j, String eventType, List<String> trainSet, int mentionsInTrainSet, String devDocsList, String testDocsList) throws IOException {
			String tempTrainDocList = String.format(TRAIN_LIST_FILENAME, outputFolder, i, t, j, trainSet.size(), mentionsInTrainSet);
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
					String.format(MODEL_FILENAME, outputFolder, i, t, j, trainSet.size(), mentionsInTrainSet),
					devDocsList,
			};
			String singleEventType = null;
			if (!eventType.equals(JOKER_TYPE_NAME)) {
				singleEventType = eventType;
				TRAIN_OTHER_ARGS_ARR[TRAIN_OTHER_ARGS_ARR.length-1] = "learnBigrams=false"; //in a single type scenario, bigrams must include just the single type, and not be learned.
			}
			args = ArrayUtils.addAll(args, TRAIN_OTHER_ARGS_ARR);

			logger.info(String.format("Running training with args: " + Arrays.asList(args)));
			Pipeline.mainWithSingleEventType(args, singleEventType);
			logger.info("Returned from training");		}
		
	}
	
	private class DecodeAction implements Action {
		@Override
		public void go(String outputFolder, int i, int t, int j, String eventType, List<String> trainSet, int mentionsInTrainSet, String devDocsList, String testDocsList) throws IOException, DocumentException {			
			String[] args = new String[] {
					String.format(MODEL_FILENAME, outputFolder, i, t, j, trainSet.size(), mentionsInTrainSet),
					ACE_PATH,
					testDocsList,
					outputFolder,
			};
			String filenameSuffix = String.format(FILENAME_PATTERN, i, t, j, trainSet.size(), mentionsInTrainSet);
			String folderNamePrefix = String.format(FOLDERNAME_PATTERN, i, t, j, trainSet.size(), mentionsInTrainSet);
			String singleEventType = null;
			if (!eventType.equals(JOKER_TYPE_NAME)) {
				singleEventType = eventType;
			}

			logger.info(String.format("Running decoding (no scoring) with args: " + Arrays.asList(args)));
			Decoder.mainNoScoring(args, filenameSuffix, folderNamePrefix, singleEventType);
			logger.info("Returned from decoding");
		}
	}
	
	private class ScoreAction implements Action {
		@Override
		public void go(String outputFolder, int i, int t, int j, String eventType, List<String> trainSet, int mentionsInTrainSet, String devDocsList, String testDocsList) throws IOException, DocumentException {
			String[] args = new String[] {
					ACE_PATH,
					outputFolder,
					testDocsList,
					String.format(SCORE_FILENAME, outputFolder, i, t, j, trainSet.size(), mentionsInTrainSet),
			};
			String folderNamePrefix = String.format(FOLDERNAME_PATTERN, i, t, j, trainSet.size(), mentionsInTrainSet);

			logger.info(String.format("Running scoring with args: " + Arrays.asList(args)));
			Stats stats = Scorer.mainMultiRunReturningStats(folderNamePrefix, args);
			logger.info("Returned from scoring");
			
			boolean exists = fileDecodeResults.isFile();
			FileOutputStream decodeResultsOut = new FileOutputStream(fileDecodeResults, true);
			PrintWriter f = null;
			try {
				f = new PrintWriter(decodeResultsOut);
				if (!exists) {
					logger.info("Cannot find decode results file, creating it: " + FILENAME_DECODE_RESULTS);
					f.printf("Iteration,TypeNum,TypeName,NumChunks,NumDocs,NumMentions,Trigger-Id-Prec,Trigger-Id-Rec,Trigger-Id-F1,Trigger-Total-Prec,Trigger-Total-Rec,Trigger-Total-F1,Arg-Id-Prec,Arg-Id-Rec,Arg-Id-F1,Arg-Total-Prec,Arg-Total-Rec,Arg-Total-F1\n");
				}
				logger.info(String.format("Updating decode results file. Trigger id F1=%f, Arg id F1=%f", stats.f1_trigger_idt, stats.f1_arg_idt));
				f.printf("%02d,%02d,%s,%03d,%03d,%03d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f\r\n",
						
						i, t, eventType, j, trainSet.size(), mentionsInTrainSet,
						
						stats.prec_trigger_idt, stats.recall_trigger_idt, stats.f1_trigger_idt,
						stats.prec_trigger, stats.recall_trigger, stats.f1_trigger,
						
						stats.prec_arg_idt, stats.recall_arg_idt, stats.f1_arg_idt,
						stats.prec_arg, stats.recall_arg, stats.f1_arg

						
						);
			}
			finally {
				f.close();
			}
		}
	}
	
	public static void main(String[] args) {
		LearningCurve prog = new LearningCurve();
		prog.run(args);
	}
}
