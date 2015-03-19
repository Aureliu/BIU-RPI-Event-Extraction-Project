package edu.cuny.qc.perceptron.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.cuny.qc.perceptron.core.AllTrainingScores.ScoresList;
import edu.cuny.qc.perceptron.core.Evaluator.Score;
import edu.cuny.qc.perceptron.types.Alphabet;
import edu.cuny.qc.perceptron.types.FeatureVector;
import edu.cuny.qc.perceptron.types.SentenceAssignment;
import edu.cuny.qc.perceptron.types.SentenceInstance;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.perceptron.types.SignalInstance;
import edu.cuny.qc.perceptron.types.SignalType;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.scorer.SignalMechanism;
import edu.cuny.qc.scorer.SignalMechanismException;
import edu.cuny.qc.scorer.SignalMechanismsContainer;
import edu.cuny.qc.scorer.mechanism.BrownClustersSignalMechanism;
import edu.cuny.qc.scorer.mechanism.DependencySignalMechanism;
import edu.cuny.qc.scorer.mechanism.POSSignalMechanism;
import edu.cuny.qc.scorer.mechanism.PlainSignalMechanism;
import edu.cuny.qc.scorer.mechanism.WordNetSignalMechanism;
import edu.cuny.qc.util.Logs;
import edu.cuny.qc.util.TokenAnnotations;
import edu.cuny.qc.util.UnsupportedParameterException;
import edu.cuny.qc.util.Utils;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;


/**
 * This class implements the learning/decoding part of perceptron, 
 * as well as serialization/deserialization
 * @author che
 *
 */
public class Perceptron implements java.io.Serializable
{
	private static final long serialVersionUID = -8870655270637917361L;
	
	// logs
	public static PrintStream wTrain = null, wDev = null;
	public static PrintStream fTrain = null, fDev = null;
	public static PrintStream pTrain = null, pDev = null;
	public static PrintStream uTrain = null, uDev = null;
	public static PrintStream bTrain = null, bDev = null;

	// the alphabet of node labels (trigger labels)
	//public Alphabet nodeTargetAlphabet;	
	// the alphabet of the label for each edge (trigger-->argument link), shared by the whole application
	// they should be consistent with SentenceInstance object
	//public Alphabet edgeTargetAlphabet;
	// the alphabet of features, shared by the whole application
	public Alphabet featureAlphabet;
	// the settings of the perceptron
	public Controller controller;// = new Controller();
	public transient SignalMechanismsContainer signalMechanismsContainer;
	
	// horrible hack - this way I can both use Controller as a static and not have to pass the goddamn perceptron to every method
	// but I also keep a non-static version of it, so it will be serialized to the file (which is mandatory for decoding)
	public static Controller controllerStatic;
	
	public String logSuffix = "";
	// label bigram
	//private Map<String, List<String>> labelBigram;
	
	// the weights of features, however, 
	public FeatureVector weights;
	public FeatureVector avg_weights;
	public FeatureVector avg_weights_base; // for average weights update
	
	//public transient List<SignalMechanism> signalMechanisms = new ArrayList<SignalMechanism>();
	
	public File outFolder;
	
	public static int iter=-1; // num of current iteration - public and static for logging
	public static int i; // num of current sentence - public and static for logging
	//public static boolean inEarlyUpdate=false; //DEBUG
	
	// default constructor 
	public Perceptron(Alphabet featureAlphabet, Controller controller, File outFolder, SignalMechanismsContainer signalMechanismsContainer) throws SignalMechanismException
	{
//		this.nodeTargetAlphabet = nodeTargetAlphabet;
//		this.edgeTargetAlphabet = edgeTargetAlphabet;
		this.featureAlphabet = featureAlphabet;
		
		// create weights vector
		this.setWeights(new FeatureVector());
		this.avg_weights_base = new FeatureVector();
		
		this.outFolder = outFolder;
		
		//labelBigram = new HashMap<String, List<String>>();
		
		setController(controller);
		//buildSignalMechanisms();
		this.signalMechanismsContainer = signalMechanismsContainer;
	}
	
	public void setController(Controller controller) {
		this.controller = controller;
		Perceptron.controllerStatic = controller;
	}
	
	public void setSignalMechanismsContainer(SignalMechanismsContainer signalMechanismsContainer) {
		this.signalMechanismsContainer = signalMechanismsContainer;
	}
	
	public void close() {
		if (signalMechanismsContainer != null) {
			signalMechanismsContainer.close();
		}
	}
	
//	public void buildSignalMechanisms() throws SignalMechanismException {
//			signalMechanisms = new ArrayList<SignalMechanism>();
//		
////		try {
//			
//			signalMechanisms.add(new PlainSignalMechanism(this));
//			signalMechanisms.add(new WordNetSignalMechanism(this));
//			signalMechanisms.add(new BrownClustersSignalMechanism(this));
//			signalMechanisms.add(new POSSignalMechanism(this));
//			signalMechanisms.add(new DependencySignalMechanism(this));
//			
////		} catch (UnsupportedPosTagStringException e) {
////			throw new SignalMechanismException(e);
////		} catch (WordNetInitializationException e) {
////			throw new SignalMechanismException(e);
////		}
//			for (SignalMechanism mechanism : signalMechanisms) {
//				triggerScorers.addAll(mechanism.scorers.get(SignalType.TRIGGER));
//			}
//	}
		
	// default constructor 
//	public Perceptron(Controller controller)
//	{
//		this.nodeTargetAlphabet = new Alphabet();
//		this.edgeTargetAlphabet = new Alphabet();
//		this.featureAlphabet = new Alphabet();
//		this.controller = controller;
//		
//		// create weights vector
//		this.setWeights(new FeatureVector());
//		this.avg_weights_base = new FeatureVector();
//		labelBigram = new HashMap<String, List<String>>();
//	}
//	
	

	/**
	 *  given an instanceList, decode, and give the best assignmentList
	 * @param instance
	 * @return
	 */
	public List<SentenceAssignment> decoding(Logs logs, Collection<? extends SentenceInstance> instanceList, Integer iter, Integer i,
			FeatureVector weights, FeatureVector avg_weights, FeatureVector avg_weights_base, PrintStream w, PrintStream f, PrintStream u)
	{
		List<SentenceAssignment> ret = new ArrayList<SentenceAssignment>();
		BeamSearch beamSearcher = new BeamSearch(this, false);
		if(this.controller.updateType == 1)
		{
			throw new UnsupportedParameterException("updateType == 1");
		}
		for(SentenceInstance inst : instanceList)
		{
			SentenceAssignment assn = beamSearcher.beamSearch(inst, controller.beamSize, false, bDev);
			ret.add(assn);
			logs.logPostBeamSearch(inst, assn, Logs.MINUS_ONE, iter, i, weights, avg_weights, avg_weights_base, w, f, u, true);
		}
		return ret;
	}
	
//	public void learning(List<SentenceInstance> trainingList, int maxIter)
//	{
//		learning(trainingList, null, 0);
//	}
	
//	public void learning(List<SentenceInstance> trainingList, List<SentenceInstance> devList, int cutoff) {
//		learning(trainingList, devList, cutoff, null);
//	}
	
	/**
	 * Returns the score only if the current is the best one so far. Null otherwise.
	 */
	public void evaluateAndUpdateBest(Logs logs, Evaluator evaluator, Collection<SentenceInstance> instances, List<SentenceAssignment> assns,
			Integer iter, ScoresList currScores, FeatureVector weights, FeatureVector avgWeights, PrintStream p) {
		System.out.printf("%s Calling evaluator.evaluate().\n", Utils.detailedLog());
		Evaluator.Score score = evaluator.evaluate(assns, instances, iter);
		//// DEBUG
		System.out.printf("%s ** Just returned from evaluator.evaluate(). It got %s instances with %s mentions. Now, the score reports: count_trigger_gold=%s, count_trigger_ans=%s, count_trigger_correct=%s, count_trigger_total=%s\n",
				Utils.detailedLog(), instances.size(), SentenceInstance.getNumEventMentions(instances, null), score.count_trigger_gold, score.count_trigger_ans, score.count_trigger_correct, score.count_trigger_total);
		////
		currScores.scores.put(iter, score);
		logs.printScore(p, Integer.toString(iter), instances.size(), score, true);
		
		if (!controller.useArguments) {
			score.harmonic_mean = score.trigger_F1;
		}

		if ((score.harmonic_mean - currScores.bestScore.harmonic_mean) >= 0.00001) {
			currScores.bestScore = score;
			currScores.bestWeights = weights.clone();
			if(this.controller.avgArguments)
			{
				currScores.bestAvgWeights = avgWeights.clone();
			}

		}

	}
	
	/**
	 * given an training instance list, and max number of iterations, learn weights by perceptron
	 * in each iteration, use current weights to test the dev instance list, and in each peak, save the model to file
	 * @param trainingList
	 * @param maxIter
	 */
	public AllTrainingScores learning(Collection<SentenceInstance> trainingInsts, Collection<SentenceInstance> devInsts, int cutoff,
			String logSuffix, boolean doLogging, boolean createNewLogs)
	{	
//		System.out.printf("\n\n%s Before sort - Perceptron.learning: trainingInsts (%s) =\n", Utils.detailedLog(), trainingInsts.size());
//		for (SentenceInstance inst : trainingInsts) {
//			System.out.printf(" - %s\t/ %s\n", inst.sentInstID, /*inst.sentID, inst.specLetter,*/ inst.docID);
//		}
//		System.out.printf("\n%s\n", Utils.detailedLog());
		List<SentenceInstance> trainingList = Lists.newArrayList(trainingInsts);
		List<SentenceInstance> devList = Lists.newArrayList(devInsts);
		Collections.sort(trainingList, controller.sentenceSortingMethod.comparator);
		Collections.sort(devList, controller.sentenceSortingMethod.comparator);

		//System.out.printf("\n\n%s After sort (%s) - Perceptron.learning: trainingInsts (%s)\n", Utils.detailedLog(), controller.sentenceSortingMethod, trainingList.size());
//		System.out.printf("\n\n%s After sort (%s) - Perceptron.learning: trainingInsts (%s) =\n", Utils.detailedLog(), controller.sentenceSortingMethod, trainingList.size());
//		for (SentenceInstance inst : trainingList) {
//			System.out.printf("- %s\t/ %s\n", inst.sentInstID, /*inst.sentID, inst.specLetter,*/ inst.docID);
//		}
		int chars=0;
		System.out.printf("\n\n%s Perceptron.learning: trainingInsts (%s) =\n\t", Utils.detailedLog(), trainingList.size());
		for (SentenceInstance inst : trainingList) {
			String str = String.format("%s:%s,", inst.docID, inst.sentInstID);
			System.out.printf(str);
			chars += str.length();
			if (chars > 2000) {
				System.out.printf("\n\t");
				chars=0;
			}
		}
		chars=0;
		System.out.printf("\n\n%s Perceptron.learning: devList (%s) =\n\t", Utils.detailedLog(), devList.size());
		for (SentenceInstance inst : devList) {
			String str = String.format("%s:%s,", inst.docID, inst.sentInstID);
			System.out.printf(str);
			chars += str.length();
			if (chars > 2000) {
				System.out.printf("\n\t");
				chars=0;
			}
		}
		System.out.printf("\n\n");

		if (controller.lazyTargetFeatures) {
			SentenceInstance.makeAllTargetFeatures(trainingList);
			SentenceInstance.makeAllTargetFeatures(devList);
		}

			
		/// DEBUG
		System.out.printf("%s *** About to start learning(). %s input  dev instances, in them - %s mentions.\n", Utils.detailedLog(), devInsts.size(), SentenceInstance.getNumEventMentions(devInsts, null));
		System.out.printf("%s *** About to start learning(). %s copied dev instances, in them - %s mentions.\n", Utils.detailedLog(), devList.size(), SentenceInstance.getNumEventMentions(devList, null));
		/////
		
		Logs logs = new Logs(outFolder, controller, logSuffix);
		
		// the evaluator for dev set
		Evaluator evaluator = null;
		if(controller.evaluatorType == 0)
		{
			throw new UnsupportedParameterException("evaluatorType == 0");
		}
		else
		{
			evaluator = new EvaluatorLoose();
		}
		
//		fillLabelBigrams();
		
		BeamSearch beamSearcher = createBeamSearcher(this, true);
		
		System.out.print(Utils.detailedLog() + " Alphabet size: " + this.featureAlphabet.size() + "\t");
//		System.out.println("Node target alphabet:" + this.nodeTargetAlphabet);
//		System.out.println("edge target alphabet:" + this.edgeTargetAlphabet);
		System.out.println("instance num: " + trainingList.size());
		
		// feature cutoff
		if(cutoff > 0)
		{
			throw new UnsupportedParameterException("cutoff > 0");
		}
		
		if (doLogging && createNewLogs) {
			try {
				wTrain = logs.getW("Train");
				fTrain = logs.getF("Train");
				fDev =   logs.getF("Dev");
				pTrain = logs.getP("Train");
				pDev =   logs.getP("Dev");
				uTrain = logs.getU("Train");
				uDev =   logs.getU("Dev");
				bTrain = logs.getB("Train");
				bDev =   logs.getB("Dev");
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}	
		}
		
		// online learning with beam search and early update
		long totalTime = 0;
//		Evaluator.Score max_score = new Evaluator.Score();
//		Evaluator.Score maxTrainScore = new Evaluator.Score();
//		Integer best_iter = 0;
//		Integer bestTrainIter = 0;
//		FeatureVector best_weights = null;
//		FeatureVector bestTrainWeights = null;
//		FeatureVector best_avg_weights = null;
//		FeatureVector bestTrainAvgWeights = null;
		AllTrainingScores result = new AllTrainingScores();
		/*int*/ iter = 0;
		BigDecimal c = BigDecimal.ZERO; // for averaged parameter
		for(iter=0; iter<this.controller.maxIterNum; iter++)
		{
			long startTime = System.currentTimeMillis();	
			System.out.printf("%s Starting iteration %s\n", Utils.detailedLog(), iter);
			int error_num = 0;	
			/*int*/ i=0;
			List<SentenceAssignment> assnsTrain = Lists.newArrayListWithCapacity(trainingList.size());
			for(SentenceInstance instance : trainingList)
			{
				SentenceAssignment assn = beamSearcher.beamSearch(instance, controller.beamSize, true, bTrain);
				// for averaged parameter
				if(this.controller.avgArguments)
				{
					c = c.add(BigDecimal.ONE);
				}
				weights.updates = Maps.newHashMap();
				if(assn.getViolate() || !this.controller.updateOnlyOnViolation)
				{
					earlyUpdate(assn, instance.target, c);
				}
				if(assn.getViolate())
				{
					error_num ++;
				}
				
				logs.logPostBeamSearch(instance, assn, c, iter, i, weights, avg_weights, avg_weights_base, wTrain, fTrain, uTrain, true);
				assnsTrain.add(assn);

				i++;
			}
			
			makeAveragedWeights(c);

			//Score score = evaluateAndCheckBest(logs, evaluator, trainingList, assnsTrain, iter, maxTrainScore, pTrain);
			evaluateAndUpdateBest(logs, evaluator, trainingList, assnsTrain, iter, result.train, this.weights, this.avg_weights, pTrain);
//			if (score != null) {
//				maxTrainScore = score;
//				bestTrainIter = iter;
//				bestTrainWeights = this.weights.clone();
//				if(this.controller.avgArguments)
//				{
//					bestTrainAvgWeights = this.avg_weights.clone();
//				}
//			}
			
			long endTime = System.currentTimeMillis();
			long iterTime = endTime - startTime;
			totalTime += iterTime;
			//System.out.println("\nIter " + iter + "\t error num: " + error_num + "\t time:" + iterTime + "\t feature size:" + this.weights.size());
			
			// use current weight to decode and evaluate developement instances
			if(devList != null)
			{
				
				//TODO DEBUG
				logs.printWeights(wTrain, iter, "", Logs.POST_ITERATION_MARK, c, "", "", weights, avg_weights, avg_weights_base, true);
				/// TODO END DEBUG
				
				/// DEBUG
				System.out.printf("%s *** About to dev-decode. %s dev instances, in them - %s mentions.\n", Utils.detailedLog(), devList.size(), SentenceInstance.getNumEventMentions(devList, null));
				/////
				
				List<SentenceAssignment> devResult = decoding(logs, devList, iter, i, weights, avg_weights, avg_weights_base, wTrain, fDev, uDev);
				/// DEBUG
				System.out.printf("%s *** After dev-decode. %s dev instances, in them - %s mentions.\n", Utils.detailedLog(), devList.size(), SentenceInstance.getNumEventMentions(devList, null));
				/////
				evaluateAndUpdateBest(logs, evaluator, devList, devResult, iter, result.dev, this.weights, this.avg_weights, pDev);
//				if (score != null) {
//					max_score = score;
//					best_iter = iter;
//					best_weights = this.weights.clone();
//					if(this.controller.avgArguments)
//					{
//						best_avg_weights = this.avg_weights.clone();
//					}
//				}

			}
			
			//DEBUG
			System.out.printf("%s Finished iteration %s\n", Utils.detailedLog(), iter);
			////////////

			
			if(error_num == 0)
			{
				// converge
				break;
			}
			
			// print out num of invalid update
//			if(beamSearcher instanceof BeamSearchStandard)
//			{
//				((BeamSearchStandard) beamSearcher).print_num_update(System.out);
//			}
		}
		
		String lastDevIter = null;
		String lastTrainIter = null;
		if(iter < this.controller.maxIterNum)
		{
			// converge
			System.out.println(Utils.detailedLog() + " converge in iter " + iter + "\t time:" + totalTime);
			lastDevIter =   String.format("BestDev  (iter=%s, converged)", result.dev.bestScore.iteration);
			lastTrainIter = String.format("BestTrain(iter=%s, converged)", result.train.bestScore.iteration);
			iter++;
		}
		else
		{
			// stop without convergency
			System.out.println(Utils.detailedLog() + " Stop without convergency" + "\t time:" + totalTime);
			lastDevIter =   String.format("BestDev  (iter=%s, NO converge)", result.dev.bestScore.iteration);
			lastTrainIter = String.format("BestTrain(iter=%s, NO converge)", result.train.bestScore.iteration);
		}
		logs.printScore(pTrain, lastTrainIter, trainingList.size(), result.train.bestScore, true);
		logs.printScore(pDev, lastDevIter, devList.size(), result.dev.bestScore, true);

		
		if(devList != null && result.dev.bestWeights != null)
		{
			this.weights = result.dev.bestWeights;
			if(this.controller.avgArguments)
			{
				this.avg_weights = result.dev.bestAvgWeights;
			}
			System.out.println("best performance on dev set: iter " + result.dev.bestScore.iteration + " :" + result.dev.bestScore);
		}
		else if(this.controller.avgArguments)
		{
			makeAveragedWeights(c);
		}
		logs.printWeights(wTrain, lastTrainIter, "", Logs.POST_ITERATION_MARK, c, "", "", weights, avg_weights, avg_weights_base, true);
		logs.printWeights(wTrain, lastDevIter, "", Logs.POST_ITERATION_MARK, c, "", "", weights, avg_weights, avg_weights_base, true);
		
		return result;
	}
	
//	protected List<SentenceInstance> getCanonicalInstanceList(
//			List<? extends SentenceInstance> devList)
//	{
//		return (List<SentenceInstance>) devList;
//	}

	protected BeamSearch createBeamSearcher(Perceptron perceptron, boolean b)
	{
		if(this.controller.updateType == 1)
		{
			throw new UnsupportedParameterException("updateType == 1");
		}
		else
		{
			return new BeamSearch(this, true);
		}
	}

	/**
	 *  w_0 - w_a/c (w_0 is standard weights, w_a is the base of averaged weights) 	
	 * @param c
	 */
	private void makeAveragedWeights(BigDecimal c)
	{
		this.avg_weights = new FeatureVector();
		for(Object feat : this.weights.getMap().keySet())
		{
			BigDecimal value = this.weights.get(feat); // w_0
			BigDecimal value_a = this.avg_weights_base.get(feat); // w_a
			BigDecimal quotient = value_a.divide(c, MathContext.DECIMAL128);
			value = value.subtract(quotient); //value - value_a / c; // w_0 - w_a/c
			this.avg_weights.add(feat, value);
		}
	}

//	/**
//	 * given a cutoff threshold, cut the features off when the number of occurrence 
//	 * is < cutoff
//	 * @param trainingList
//	 */
//	private void featureCutOff(List<SentenceInstance> trainingList, int cutoff)
//	{
//		if(cutoff <=0)
//		{
//			return;
//		}
//		Map<Object, Integer> countMap = new HashMap<Object, Integer>();
//		for(SentenceInstance inst : trainingList)
//		{
//			for(FeatureVector fv : inst.target.featVecSequence.getSequence())
//			{
//				for(Object key : fv.getMap().keySet())
//				{
//					Integer freq = countMap.get(key);
//					if(freq == null)
//					{
//						freq = 0;
//					}
//					freq++;
//					countMap.put(key, freq);
//				}
//			}
//		}
//		Alphabet newFeatAlphabet = new Alphabet(this.featureAlphabet.size());
//		for(Object feat : countMap.keySet())
//		{
//			Integer freq = countMap.get(feat);
//			if(freq > cutoff)
//			{
//				newFeatAlphabet.lookupIndex(feat, true);
//			}
//		}
//		this.featureAlphabet = newFeatAlphabet;
//		// update featureAlphabet
//		for(SentenceInstance inst : trainingList)
//		{
//			inst.featureAlphabet = newFeatAlphabet;
//			inst.target.featureAlphabet = newFeatAlphabet;
//		}
//	}
//
//	private void fillDefaultLabelBigrams(String singleEventType) {
//		if (singleEventType != null) {
//			getLabelBigram().put(singleEventType,                      Arrays.asList(new String[] {SentenceAssignment.PAD_Trigger_Label, singleEventType}));
//			getLabelBigram().put(SentenceAssignment.PAD_Trigger_Label, Arrays.asList(new String[] {SentenceAssignment.PAD_Trigger_Label, singleEventType}));
//		}
//		else {
//			List<String> allTypes = new ArrayList<String>(TypeConstraints.eventTypeMap.keySet());
//			allTypes.add(0, SentenceAssignment.PAD_Trigger_Label);
//			String currType = null;
//			for (int i=0; i<allTypes.size(); i++) {
//				currType = allTypes.get(i);
//				List<String> list = new ArrayList<String>(allTypes);
//				list.remove(i);
//				getLabelBigram().put(currType, list);
//			}
//		}
//	}
//
//	/**
//	 * After each type of trigger, can appear any other type of trigger. Default label ("O") included.
//	 */
//	public void fillLabelBigrams() {
//		List<String> allTypes = new ArrayList<String>(TypeConstraints.specTypes);
//		allTypes.add(0, SentenceAssignment.PAD_Trigger_Label);
//		String currType = null;
//		for (int i=0; i<allTypes.size(); i++) {
//			currType = allTypes.get(i);
//			List<String> list = new ArrayList<String>(allTypes);
//			//list.remove(i);
//			getLabelBigram().put(currType, list);
//		}
//	}
	
//	protected void extractTriggerLabelBigrams(List<SentenceInstance> traininglist)
//	{
//		for(SentenceInstance instance : traininglist)
//		{
//			SentenceAssignment target = instance.target;
//			String prev = SentenceAssignment.PAD_Trigger_Label;
//			for(int i=0; i<target.getNodeAssignment().size(); i++)
//			{
//				Integer index = target.getNodeAssignment().get(i);
//				String label = (String) this.nodeTargetAlphabet.lookupObject(index);
//				
//				List<String> list = getLabelBigram().get(prev);
//				if(list == null)
//				{
//					list = new ArrayList<String>();
//				}
//				if(!list.contains(label))
//				{
//					list.add(label);
//				}
//				getLabelBigram().put(prev, list);
//				prev = label;
//			}
//		}
//	}

	/**
	 * given an assignment, and the gold-standard, update the weights
	 * @param assn
	 * @param target
	 * @param c 
	 * @return return true if it's updated, i.e. the assn is not correct
	 */
	protected void earlyUpdate(SentenceAssignment assn, SentenceAssignment target, BigDecimal c)
	{
		// the beam search may return a early assignment, and we only update the prefix
		for(int i=0; i <= assn.getState(); i++)
		{
			// weights = \phi(y*) - \phi(y)
			this.getWeights().addDelta(target.featVecSequence.get(i), assn.featVecSequence.get(i), BigDecimal.ONE, i);
			//this.getWeights().addDelta(target.featVecSequence.get(i), assn.featVecSequence.get(i), 1.0);
			
			if(this.controller.avgArguments)
			{
				this.avg_weights_base.addDelta(target.featVecSequence.get(i), assn.featVecSequence.get(i), c, i);
			}
		}
	}
	
	public void setWeights(FeatureVector weights)
	{
		this.weights = weights;
	}

	public FeatureVector getWeights()
	{
		return weights;
	}

//	protected void setLabelBigram(Map<String, List<String>> labelBigram)
//	{
//		this.labelBigram = labelBigram;
//	}

//	protected Map<String, List<String>> getLabelBigram()
//	{
//		return labelBigram;
//	}
	
	/**
	 * serialize the model (mainly weights/alphabets) to the file
	 * @param modelFile
	 */
	static public void serializeObject(Serializable model, File modelFile)
	{
		try
		{
			OutputStream stream = new FileOutputStream(modelFile);
			SerializationUtils.serialize(model, stream);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * deserialize a saved model from a file
	 * @param modelFile
	 * @return
	 */
	public static Perceptron deserializeObject(File modelFile)
	{
		Perceptron model = null;
		try
		{
			InputStream stream = new FileInputStream(modelFile);
			model = (Perceptron) SerializationUtils.deserialize(stream);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return model;
	}

	public FeatureVector getAvg_weights()
	{
		return avg_weights;
	}
}
