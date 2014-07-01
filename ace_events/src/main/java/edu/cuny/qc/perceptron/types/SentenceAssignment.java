package edu.cuny.qc.perceptron.types;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.cas.CASException;

import ac.biu.nlp.nlp.ie.onthefly.input.SpecAnnotator;
import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;
import edu.cuny.qc.ace.acetypes.AceEventMention;
import edu.cuny.qc.ace.acetypes.AceEventMentionArgument;
import edu.cuny.qc.ace.acetypes.AceMention;
import edu.cuny.qc.perceptron.core.Controller;
import edu.cuny.qc.perceptron.core.Perceptron;
import edu.cuny.qc.perceptron.featureGenerator.GlobalFeatureGenerator;
import edu.cuny.qc.perceptron.types.SentenceInstance.InstanceAnnotations;
import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.util.UnsupportedParameterException;

/**
 * For the (target) assignment, it should encode two types of assignment:
 * (1) label assignment for each token: refers to the event trigger classification 
 * (2) assignment for any sub-structure of the sentence, e.g. one assignment indicats that 
 * the second token is argument of the first trigger
 * in the simplest case, this type of assignment involves two tokens in the sentence
 * 
 * 
 * @author che
 *
 */
public class SentenceAssignment
{
	public static final String PAD_Trigger_Label = "O\t"; // pad for the intial state
	public static final String Default_Trigger_Label = PAD_Trigger_Label;
	public static final String Generic_Existing_Trigger_Label = "TRIGGER\t";
	public static final String Default_Argument_Label = "X\t";//"NON\t";
	public static final String Generic_Existing_Argument_Label = "IS_ARG\t";
	
	public static final String LABEL_MARKER = "\tcurrentLabel:";
	public static final String BIAS_FEATURE = "BIAS_FEATURE";
	
	public static final BigDecimal FEATURE_POSITIVE_VAL = BigDecimal.ONE;
	public static final BigDecimal FEATURE_NEGATIVE_VAL = BigDecimal.ZERO;

	/**
	 * the index of last processed (assigned/searched) token
	 */
	int state = -1;
	
	static {
		System.err.println("??? SentenceAssignment: Assumes binary labels O,ATTACK (around oMethod)");
		System.err.println("??? SentenceAssignment: Edge features are currently excluded, until I stabalize a policy with triggers. Then some policy should be decided for edges (that should handle, for example, the fact that if only the guess has some trigger that the gold doesn't have, then it would physically have more features than target (not same features just with different scores, like in the triggers' case), and this violated my assumption that guess and gold have the same features. Relevant methods: equals() (3 methods), makeEdgeLocalFeature(), BeamSearch.printBeam (check compatible)");
		System.err.println("??? SentenceAssignment: GLOBAL FEATURES ARE NOT IMPORTED YET!!!");
	}
	
	public static String getGenericTriggerLabel(String label) {
		if (label.equals(Default_Trigger_Label)) {
			return Default_Trigger_Label;
		}
		else {
			return Generic_Existing_Trigger_Label;
		}
	}
	
	public static String getGenericArgumentLabel(String label) {
		if (label.equals(Default_Argument_Label)) {
			return Default_Argument_Label;
		}
		else {
			return Generic_Existing_Argument_Label;
		}
	}
	
	public static String stripLabel(String featureName) {
		int count = StringUtils.countMatches(featureName, LABEL_MARKER);
		if (count>1) {
			throw new IllegalArgumentException("Got feature name with more than one marker label: '"+featureName+"'");
		}
		else if (count == 1) {
			final String LABAEL_REGEX = String.format("%s\\S+", LABEL_MARKER);
			String result = featureName.replaceFirst(LABAEL_REGEX, "");
			return result;
		}
		else { //count == 0
			return featureName;
		}
	}
	
	public void retSetState()
	{
		state = -1;
	}
	
	public int getState()
	{
		return state;
	}
	
	/*
	 * indicates if it violates gold-standard, useful for learning
	 */
	boolean violate = false;
	
	// the alphabet of the label for each node (token), shared by the whole application
	// they should be consistent with SentenceInstance object
	public Alphabet nodeTargetAlphabet;
	
	// the alphabet of the label for each edge (trigger-->argument link), shared by the whole application
	// they should be consistent with SentenceInstance object
	public Alphabet edgeTargetAlphabet;

	// the alphabet of features, shared by the whole application
	public Alphabet featureAlphabet;
	
	public Controller controller;
	
	//public TypesContainer types;
	
	//public transient SentenceInstance inst = null;
	public transient SentenceAssignment target = null;
	public List<AceMention> eventArgCandidates = null;
	
	// the feature vector of the current assignment
	public FeatureVectorSequence featVecSequence;
	
	// the score of the assignment, it can be partial score when the assignment is not complete
	protected BigDecimal score = BigDecimal.ZERO;
	protected List<BigDecimal> partial_scores;
	
	//public Map<Integer, Map<String, List<SignalInstance>>> featureToSignal; 
	public Map<Integer, Map<String, String>> featureToSignal; 
	public List<BigDecimal> getPartialScores() {
		return partial_scores;
	}
	
	public FeatureVector getFV(int index)
	{
		return featVecSequence.get(index);
	}
	
	public FeatureVector getCurrentFV()
	{
		return featVecSequence.get(state);
	}
	
	public FeatureVectorSequence getFeatureVectorSequence()
	{
		return featVecSequence;
	}
	
	public void addFeatureVector(FeatureVector fv)
	{
		featVecSequence.add(fv);
	}
	
	/**
	 * as the search processed, increament the state for next token
	 * creat a new featurevector for this state
	 */
	public void incrementState()
	{
		state++;
		FeatureVector fv = new FeatureVector();
		this.addFeatureVector(fv);
		this.partial_scores.add(BigDecimal.ZERO);
	}
	
	/**
	 * assignment to each node, node-->assignment
	 */
	protected Vector<Integer> nodeAssignment;
	
	public Vector<Integer> getNodeAssignment()
	{
		return nodeAssignment;
	}
	
	/**
	 * assignment to each argument edge trigger-->arg --> assignment
	 */
	protected Map<Integer, Map<Integer, Integer>> edgeAssignment;
	
	/**
	 * deep copy an assignment
	 */
	public SentenceAssignment clone()
	{
		// shallow copy the alphabets
		SentenceAssignment assn = new SentenceAssignment(/*types,*/ eventArgCandidates, target, nodeTargetAlphabet, edgeTargetAlphabet, featureAlphabet, controller);
		
		// shallow copy the assignment
		assn.nodeAssignment = (Vector<Integer>) this.nodeAssignment.clone();
		// deep copy the edge assignment for the last element 
		// (this is because in the beam search, we need expand the last statement for arguments labeling)
		for(Integer key : this.edgeAssignment.keySet())
		{
			Map<Integer, Integer> edgeMap = this.edgeAssignment.get(key);
			if(edgeMap != null)
			{
				if(key < this.getState())
				{
					assn.edgeAssignment.put(key, edgeMap);
				}
				else // deep copy the last element
				{
					Map<Integer, Integer> new_edgeMap = new HashMap<Integer, Integer>();
					new_edgeMap.putAll(edgeMap);
					assn.edgeAssignment.put(key, new_edgeMap);
				}
			}
		}
		
		// deep copy the feature vector sequence for the last element
		assn.featVecSequence = this.featVecSequence.clone2();
		
		// deep copy attributes
		assn.state = this.state;
		assn.score = this.score;
		//assn.local_score = this.local_score;
		assn.partial_scores.addAll(this.partial_scores);
		
		//assn.featureToSignal = new HashMap<Integer, Map<String, List<SignalInstance>>>();
		assn.featureToSignal = new HashMap<Integer, Map<String, String>>();
		for (Entry<Integer, Map<String, String>> entry : featureToSignal.entrySet()) {
			Map<String, String> map = new HashMap<String, String>();
			assn.featureToSignal.put(new Integer(entry.getKey()), map);
			for(Entry<String, String> entry2 : entry.getValue().entrySet()) {
				map.put(entry2.getKey(), entry2.getValue());
			}
		}
		return assn;
	}
	
	public Map<Integer, Map<Integer, Integer>> getEdgeAssignment()
	{
		return edgeAssignment;
	}
	
	/**
	 * get the node label of the current node
	 * @return
	 */
	public String getLabelAtToken(int i)
	{
		assert(i <= state);
		
		String label;
		if(i >= 0 )
		{
			label = (String) nodeTargetAlphabet.lookupObject(nodeAssignment.get(i));
		}
		else
		{
			label = PAD_Trigger_Label;
		}
		return label;
	}
	
	/**
	 * get the node label of the current node
	 * @return
	 */
	public String getCurrentNodeLabel()
	{
		String label;
		if(state >= 0 )
		{
			label = (String) nodeTargetAlphabet.lookupObject(nodeAssignment.get(state));
		}
		else
		{
			label = PAD_Trigger_Label;
		}
		return label;
	}
	
	/**
	 * set current node label
	 * @return
	 */
	public void setCurrentNodeLabel(int index)
	{
		if(state >= 0 )
		{
			if(nodeAssignment.size() <= state)
			{
				nodeAssignment.setSize(state + 1);
			}
			this.nodeAssignment.set(state, index);
		}
	}
	
	/**
	 * set current node label
	 * @return
	 */
	public void setCurrentNodeLabel(String label)
	{
		if(state >= 0 )
		{
			int index = nodeTargetAlphabet.lookupIndex(label);
			if(nodeAssignment.size() <= state)
			{
				nodeAssignment.setSize(state + 1);
			}
			this.nodeAssignment.set(state, index);
		}
	}
	
	public Map<Integer, Integer> getCurrentEdgeLabels()
	{
		Map<Integer, Integer> map = this.edgeAssignment.get(state);
		if(map == null)
		{
			map = new HashMap<Integer, Integer>();
			this.edgeAssignment.put(state, map);
		}
		return map;
	}
	
	/**
	 * set current node edge
	 * @return
	 */
	public void setCurrentEdgeLabel(int arg_idx, String label)
	{
		if(state >= 0 )
		{
			int index = edgeTargetAlphabet.lookupIndex(label);
			Map<Integer, Integer> map = this.edgeAssignment.get(state);
			if(map == null)
			{
				map = new HashMap<Integer, Integer>();
			}
			map.put(arg_idx, index);
			this.edgeAssignment.put(state, map);
		}
	}
	
	/**
	 * set current node edges
	 * @return
	 */
	public void setCurrentEdges(Map<Integer, Integer> edges)
	{
		if(state >= 0 )
		{
			Map<Integer, Integer> map = this.edgeAssignment.get(state);
			if(map == null)
			{
				map = new HashMap<Integer, Integer>();
			}
			map.putAll(edges);
			this.edgeAssignment.put(state, map);
		}
	}
	
	/**
	 * set current node label
	 * @return
	 */
	public void setCurrentEdgeLabel(int arg_idx, int label)
	{
		if(state >= 0 )
		{
			Map<Integer, Integer> map = this.edgeAssignment.get(state);
			if(map == null)
			{
				map = new HashMap<Integer, Integer>();
			}
			map.put(arg_idx, label);
			this.edgeAssignment.put(state, map);
		}
	}
	
	public SentenceAssignment(/*TypesContainer types,*/ List<AceMention> eventArgCandidates, SentenceAssignment target, Alphabet nodeTargetAlphabet, Alphabet edgeTargetAlphabet, Alphabet featureAlphabet, Controller controller)
	{
		this.nodeTargetAlphabet = nodeTargetAlphabet;
		this.edgeTargetAlphabet = edgeTargetAlphabet;
		this.featureAlphabet = featureAlphabet;
		this.controller = controller;
		//this.types = types;
		//this.inst = instance;
		this.eventArgCandidates = eventArgCandidates;
		this.target = target;
		
		nodeAssignment = new Vector<Integer>();
		edgeAssignment = new HashMap<Integer, Map<Integer, Integer>>();
		
		featVecSequence = new FeatureVectorSequence();
		partial_scores = new ArrayList<BigDecimal>();
		featureToSignal = new HashMap<Integer, Map<String, String>>();
	}
	
	/**
	 * given an labeled instance, create a target assignment as ground-truth
	 * also create a full featureVectorSequence
	 * @param inst
	 */
	public SentenceAssignment(SentenceInstance inst, Perceptron perceptron)
	{
		this(/*inst.types,*/inst.eventArgCandidates, inst.target, inst.nodeTargetAlphabet, inst.edgeTargetAlphabet, inst.featureAlphabet, inst.controller);
		
		for(int i=0; i < inst.size(); i++)
		{
			int label_index = this.nodeTargetAlphabet.lookupIndex(Default_Trigger_Label);
			this.nodeAssignment.add(label_index);
			this.incrementState();
		}
		
		// use sentence data to assign event/arg tags
		for(AceEventMention mention : inst.eventMentions)
		{
			Vector<Integer> headIndices = mention.getHeadIndices();
			
			// for event, only pick up the first token as trigger
			int trigger_index = headIndices.get(0);  
			
			/// DEBUG
			if (trigger_index == -1) {
				System.err.printf("\nGot -1 for 0's location in: %s, which are head indices of: %s\n\n", headIndices, mention);
			}
			//////////
			// ignore the triggers that are with other POS
			if(!inst.types.isPossibleTriggerByPOS(inst, trigger_index))
			{	
				continue;
			}
			int feat_index = this.nodeTargetAlphabet.lookupIndex(mention.getSubType());
			this.nodeAssignment.set(trigger_index, feat_index);
			
			Map<Integer, Integer> arguments = edgeAssignment.get(trigger_index);
			if(arguments == null)
			{
				arguments = new HashMap<Integer, Integer>();
				edgeAssignment.put(trigger_index, arguments);
			}
			
			// set default edge label between each trigger-entity pair
			for(int can_id=0; can_id < inst.eventArgCandidates.size(); can_id++)
			{
				AceMention can = inst.eventArgCandidates.get(can_id);
				// ignore entity that are not compatible with the event
				if(inst.types.isEntityTypeEventCompatible(mention.getSubType(), can.getType()))
				{
					feat_index = this.edgeTargetAlphabet.lookupIndex(Default_Argument_Label);
					arguments.put(can_id, feat_index);
				}
			}
			
			// set argument role labels
			for(AceEventMentionArgument arg : mention.arguments)
			{
				AceMention arg_mention = arg.value;
				int arg_index = inst.eventArgCandidates.indexOf(arg_mention);
				/// DEBUG
				if (arg_index == -1) {
					System.err.printf("\n-1\n\n");
				}
				//////
				feat_index = this.edgeTargetAlphabet.lookupIndex(arg.role);
				arguments.put(arg_index, feat_index);
			}
		}
		
		//// DEBUG
		for (Map<Integer, Integer> map : edgeAssignment.values()) {
			if (map.keySet().contains(-1)) {
				System.err.printf("\nGot -1! Here: %s\n\n", map);
			}
		}
		///////
		// create featureVectorSequence
		for(int i=0; i<=state; i++)
		{
			makeAllFeatureForSingleState(inst, i, inst.learnable, inst.learnable, perceptron);
		}
	}
	
	private void makeAllFeatureForSingleState(SentenceInstance problem, int i, boolean addIfNotPresent,
			boolean useIfNotPresent, Perceptron perceptron)
	{
		// make basic bigram features for event trigger
		this.makeNodeFeatures(problem, i, addIfNotPresent, useIfNotPresent, perceptron);
		// make basic features of the argument-trigger link
		this.makeEdgeFeatures(problem, i, addIfNotPresent, useIfNotPresent, perceptron);
		if(problem.controller.useGlobalFeature)
		{
			// make various global features
			this.makeGlobalFeatures(problem, i, addIfNotPresent, useIfNotPresent, perceptron);
		}
	}

	/**
	 * extract global features from the current assignment
	 * @param assn
	 * @param problem
	 */
	public void makeEdgeFeatures(SentenceInstance problem, int index, boolean addIfNotPresent, 
			boolean useIfNotPresent, Perceptron perceptron)
	{
		// node label
		String nodeLabel = this.getLabelAtToken(index);
		if(!isArgumentable(nodeLabel))
		{
			return;
		}
		
		Map<Integer, Integer> edge = edgeAssignment.get(index);
		if(edge == null)
		{
			return;
		}
		
		// DEBUG
		if (edge.keySet().contains(-1)) {
			System.err.printf("\nGot a -1!!! keys: %s\n\n", edge.keySet());
		}
		/////
		for(Integer key : edge.keySet())
		{
			// edge label
			makeEdgeLocalFeature(problem, index, addIfNotPresent, key, useIfNotPresent, perceptron);
		}
	}

	public void makeEdgeLocalFeature(SentenceInstance problem, int index, boolean addIfNotPresent, 
			int entityIndex, boolean useIfNotPresent, Perceptron perceptron)
	{	
		if(this.edgeAssignment.get(index) == null)
		{
			// skip assignments that don't have edgeAssignment for index-th node
			return;
		}
		Integer edgeLabelIndx = this.edgeAssignment.get(index).get(entityIndex);
		if(edgeLabelIndx == null)
		{
			return;
		}
		String edgeLabel = (String) this.edgeTargetAlphabet.lookupObject(edgeLabelIndx);
		String genericEdgeLabel = getGenericArgumentLabel(edgeLabel);
		// if the argument role is NON, then do not produce any feature for it
		if(controller.skipNonArgument && edgeLabel.equals(SentenceAssignment.Default_Argument_Label))
		{
			return; 
		}
		
		String nodeLabel = getLabelAtToken(index);
		List<Map<String, List<Map<String, Map<String, SignalInstance>>>>> edgeSignals = (List<Map<String, List<Map<String, Map<String, SignalInstance>>>>>) problem.get(InstanceAnnotations.EdgeTextSignals);
		Map<String, SignalInstance> signals = edgeSignals.get(index).get(nodeLabel).get(entityIndex).get(edgeLabel);
		//Map<String, Map<String, Map<String, SignalInstance>>> allEntitySignals = edgeSignals.get(index).get(entityIndex);
		//AceMention mention = problem.eventArgCandidates.get(entityIndex);
		
		//int nodeLabelIndex = this.nodeAssignment.get(index);
		//String nodeLabel = (String) this.nodeTargetAlphabet.lookupObject(nodeLabelIndex);
		//FeatureVector fv = this.getFeatureVectorSequence().get(index);
//		if(allEntitySignals == null)
//		{
//			allEntitySignals = EdgeSignalGenerator.get_edge_text_signals(problem, index, mention, perceptron);
//			edgeSignals.get(index).set(entityIndex, allEntitySignals);
//		}
		
		if (genericEdgeLabel == Generic_Existing_Argument_Label) {
			//Map<String, SignalInstance> signalsOfEntity = allEntitySignals.get(nodeLabel).get(edgeLabel);
			for (SignalInstance signal : signals.values()) {
				BigDecimal featureValue = signal.positive ? FEATURE_POSITIVE_VAL : FEATURE_NEGATIVE_VAL;
				String featureStr = "EdgeLocalFeature:\t" + signal.getName();// + "\t" + genericEdgeLabel;
				
				// TODO this line should DEFINITELY be uncommented when I incorporate edges back to the story
				// of course, a policy regarding them should be decided and implemented
				//makeFeature(featureStr, fv, featureValue, index, signal, addIfNotPresent, useIfNotPresent);
			}
		}
		else {
//			Map<String, Map<String, SignalInstance>> signalsOfNodeLabel = allEntitySignals.get(nodeLabel);
//			for (Object signalNameObj : perceptron.argumentSignalNames) {
//				String signalName = (String) signalNameObj;
//				//BigDecimal numFalse = BigDecimal.ZERO;
//				BigDecimal featureValue = FEATURE_POSITIVE_VAL;
//				for (Map<String, SignalInstance> signalsOfLabel : signalsOfNodeLabel.values()) {
//					SignalInstance signal = signalsOfLabel.get(signalName);
//					if (signal.positive) {
//						featureValue = FEATURE_NEGATIVE_VAL;
//						break;
//					}
////					if (!signal.positive) {
////						numFalse = numFalse.add(BigDecimal.ONE); //numFalse += 1.0;
////					}
//				}
//				
//				//BigDecimal numRolesiInSpec = new BigDecimal(signalsOfNodeLabel.size());
//				//BigDecimal falseRatio = numFalse.divide(numRolesiInSpec);// divide by number of roles in current spec
//				
//				String featureStr = "EdgeLocalFeature:\t" + signalName;// + "\t" + genericEdgeLabel;
//
//				
//				// TODO this line should DEFINITELY be uncommented when I incorporate edges back to the story
//				// of course, a policy regarding them should be decided and implemented
//				//makeFeature(featureStr, fv, featureValue, index, null, addIfNotPresent, useIfNotPresent);
//
//			}
		}
	}
	
	/**
	 * This type of feature applies in each step of trigger classification
	 * @param problem
	 * @param index
	 * @param addIfNotPresent
	 * @param useIfNotPresent
	 */
	public void makeGlobalFeaturesTrigger(SentenceInstance problem, int index, boolean addIfNotPresent,
			boolean useIfNotPresent)
	{
		FeatureVector fv = this.getFV(index);
		List<String> featureStrs = GlobalFeatureGenerator.get_global_features_triggers(problem, index, this);
		for(String feature : featureStrs)
		{
			String featureStr = "TriggerLevelGlobalFeature:\t" + feature;
			makeFeature(featureStr, fv, null, null, addIfNotPresent, useIfNotPresent);
		}
	} 
	
	/**
	 * this is global feature applicable when arugment searching in a node is complete
	 * @param problem
	 * @param index
	 * @param addIfNotPresent
	 */
	public void makeGlobalFeaturesComplete(SentenceInstance problem, int index, boolean addIfNotPresent,
			boolean useIfNotPresent)
	{
		FeatureVector fv = this.getFV(index);
		List<String> featureStrs = GlobalFeatureGenerator.get_global_features_node_level_omplete(problem, index, this);
		for(String feature : featureStrs)
		{
			String featureStr = "NodeLevelGlobalFeature:\t" + feature;
			makeFeature(featureStr, fv, null, null, addIfNotPresent, useIfNotPresent);
		}
	}
	
	/**
	 * this version of global features are added when each step of argument searching
	 * @param problem
	 * @param index
	 * @param entityIndex
	 * @param addIfNotPresent
	 */
	public void makeGlobalFeaturesProgress(SentenceInstance problem, int index, int entityIndex, boolean addIfNotPresent,
			boolean useIfNotPresent)
	{
		FeatureVector fv = this.getFV(index);
		List<String> featureStrs = GlobalFeatureGenerator.get_global_features_node_level(problem, index, this, entityIndex);
		for(String feature : featureStrs)
		{
			String featureStr = "NodeLevelGlobalFeature:\t" + feature;
			makeFeature(featureStr, fv, null, null, addIfNotPresent, useIfNotPresent);
		}
		featureStrs = GlobalFeatureGenerator.get_global_features_sent_level(problem, index, this, entityIndex);
		for(String feature : featureStrs)
		{
			String featureStr = "SentLevelGlobalFeature:\t" + feature;
			makeFeature(featureStr, fv, null, null, addIfNotPresent, useIfNotPresent);
		}
		
	}
	
	/**
	 * this version of global features are added when argument searching for each node is completed 
	 * assume the argument search in token i is finished, then fill-in global features in token i
	 * @param problem
	 * @param i
	 * @param addIfNotPresent
	 */
	public void makeGlobalFeatures(SentenceInstance problem, int index, boolean addIfNotPresent, 
			boolean useIfNotPresent, Perceptron perceptron)
	{
		if(this.edgeAssignment.get(index) == null)
		{
			return;
		}
		makeGlobalFeaturesComplete(problem, index, addIfNotPresent, useIfNotPresent);
		for(int entityIndex : edgeAssignment.keySet())
		{
			makeGlobalFeaturesProgress(problem, index, entityIndex, addIfNotPresent, useIfNotPresent);
		}
	}
	
	public void makeNodeFeatures(SentenceInstance problem, int i, boolean addIfNotPresent, boolean useIfNotPresent, Perceptron perceptron)
	{
		try {
			// make node feature (bigram feature)
			List<Map<String, Map<ScorerData, SignalInstance>>> tokens = (List<Map<String, Map<ScorerData, SignalInstance>>>) problem.get(InstanceAnnotations.NodeTextSignalsBySpec);
			/// DEBUG
			if (tokens == null) {
				System.out.println(problem);
			}
			////
			Map<String, Map<ScorerData, SignalInstance>> token = tokens.get(i);
			//String previousLabel = this.getLabelAtToken(i-1);
			String label = this.getLabelAtToken(i);
			String genericLabel = getGenericTriggerLabel(label);
			
			if(this.controller.order >= 1)
			{
				throw new UnsupportedParameterException("order >= 1");
			}
			else // order = 0
			{
	//			Map<String, SignalInstance> signalsOfLabel = token.get(label);
	//			for (SignalInstance signal : signalsOfLabel.values()) {
	//				BigDecimal featureValue = signal.positive ? FEATURE_POSITIVE_VAL : FEATURE_NEGATIVE_VAL;
	//				//String featureStr = "BigramFeature:\t" + signal.name;
	//				String featureStr = "BigramFeature:\t" + signal.name + "\t" + LABEL_MARKER + genericLabel;
	//				List<SignalInstance> signals = Arrays.asList(new SignalInstance[] {signal});
	//				makeFeature(featureStr, this.getFV(i), featureValue, i, signals, addIfNotPresent, useIfNotPresent);
	//			}
				
				if (this.controller.oMethod.equalsIgnoreCase("F")) {
					// We don't check what is the label of this token, as the feature value is always according
					// to the associated spec, even when the token's label is O.
					// The only place in which the current label is expressed, is the "genericLabel" that is
					// added to the feature string
					try {
						String associatedSpecLabel = SpecAnnotator.getSpecLabel(problem.associatedSpec);
	//					/// DEBUG
	//					if (associatedSpecLabel == null || token == null) {
	//						System.err.printf("associatedSpecLabel=%s, token=%s\n", associatedSpecLabel, token);
	//					}
	//					///
						Map<ScorerData, SignalInstance> signalsOfLabel = token.get(associatedSpecLabel);
	//					/// DEBUG
	//					if (signalsOfLabel == null) {
	//						System.err.printf("associatedSpecLabel=%s, token=%s\n", associatedSpecLabel, token);
	//					}
	//					///
						for (SignalInstance signal : signalsOfLabel.values()) {
							List<SignalInstance> signals = Arrays.asList(new SignalInstance[] {signal});
							BigDecimal featureValuePositive = signal.positive ? FEATURE_POSITIVE_VAL : FEATURE_NEGATIVE_VAL;
							BigDecimal featureValueNegative = signal.positive ? FEATURE_NEGATIVE_VAL : FEATURE_POSITIVE_VAL;
							
							String featureStrPositive = "BigramFeature:\t" + signal.getName() + "\t" + "P+\t" + LABEL_MARKER + genericLabel;
							String featureStrNegative = "BigramFeature:\t" + signal.getName() + "\t" + "P-\t" + LABEL_MARKER + genericLabel;
							
							makeFeature(featureStrPositive, this.getFV(i), featureValuePositive, i, signals, addIfNotPresent, useIfNotPresent);
							makeFeature(featureStrNegative, this.getFV(i), featureValueNegative, i, signals, addIfNotPresent, useIfNotPresent);
						}
					} catch (CASException e) {
						throw new RuntimeException(e);
					}
	
				}
				else {
					if (genericLabel == Generic_Existing_Trigger_Label) {
						Map<ScorerData, SignalInstance> signalsOfLabel = token.get(label);
						for (SignalInstance signal : signalsOfLabel.values()) {
							List<SignalInstance> signals = Arrays.asList(new SignalInstance[] {signal});
							BigDecimal featureValue = signal.positive ? FEATURE_POSITIVE_VAL : FEATURE_NEGATIVE_VAL;
							String featureStr = null;
							if (this.controller.oMethod.equalsIgnoreCase("E")) {
								featureStr = "BigramFeature:\t" + signal.getName();// + "\t" + LABEL_MARKER + genericLabel;
							}
							else {
								featureStr = "BigramFeature:\t" + signal.getName() + "\t" + LABEL_MARKER + genericLabel;
							}
							makeFeature(featureStr, this.getFV(i), featureValue, i, signals, addIfNotPresent, useIfNotPresent);
						}
					}
					else if (this.controller.oMethod.equalsIgnoreCase("E")) { //genericLabel == Default_Trigger_Label + "E"
						String featureStr = "BigramFeature:\t" + BIAS_FEATURE;
						makeFeature(featureStr, this.getFV(i), BigDecimal.ONE, i, new ArrayList<SignalInstance>(), addIfNotPresent, useIfNotPresent);
					}
					else { //genericLabel == Default_Trigger_Label
						for (ScorerData scorerData : perceptron.triggerScorers) {
							//String signalName = (String) signalNameObj;
							List<SignalInstance> signals = new ArrayList<SignalInstance>();
							//double numFalse = 0.0;
							
		//					/**
		//					 * If at least one spec has a positive signal - then the value is FEATURE_NEGATIVE_VAL,
		//					 * meaning that the token doesn't fit Default_Trigger_Label ("O").
		//					 * 
		//					 * Otherwise (no spec fits), the value is FEATURE_POSITIVE_VAL - the token fits "O". 
		//					 */
		//					BigDecimal featureValue = FEATURE_NEGATIVE_VAL;//FEATURE_POSITIVE_VAL;
		//					for (Map<String, SignalInstance> signalsOfLabel : token.values()) {
		//						SignalInstance signal = signalsOfLabel.get(signalName);
		//						if (signal == null) {
		//							throw new IllegalArgumentException(String.format("Cannot find feature '%s' for non-label token %d", signalName, i));
		//						}
		//						signals.add(signal);
		//						if (signal.positive) {
		//							featureValue = FEATURE_POSITIVE_VAL;//FEATURE_NEGATIVE_VAL;//0.0 //-1.0;
		//							break;
		//						}
		//					}
							
							if (token.size() != 1) {
								throw new IllegalStateException("token.size() should be 1 (1 non-O label, ATTACK), but it's " + token.size());
							}
							Map<ScorerData, SignalInstance> signalsOfAttack = token.values().iterator().next();
							SignalInstance signalOfAttack = signalsOfAttack.get(scorerData);
							signals.add(signalOfAttack);
		
							// Set feature value according to requested O Method
							BigDecimal featureValue = null;
							if (this.controller.oMethod.equalsIgnoreCase("A")) {
								featureValue = signalOfAttack.positive ? BigDecimal.ZERO : BigDecimal.ONE;
							}
							else if (this.controller.oMethod.equalsIgnoreCase("B")) {
								featureValue = signalOfAttack.positive ? BigDecimal.ONE : BigDecimal.ZERO;
							}
							else if (this.controller.oMethod.equalsIgnoreCase("C")) {
								featureValue = BigDecimal.ZERO;
							}
							else if (this.controller.oMethod.equalsIgnoreCase("D")) {
								featureValue = BigDecimal.ONE;
							}
							else {
								throw new IllegalArgumentException("Illegal value for 'oMethod': '" + this.controller.oMethod + "'");
							}
							
												
							//String featureStr = "BigramFeature:\t" + signalName;
							String featureStr = "BigramFeature:\t" + signalOfAttack.getName() + "\t" + LABEL_MARKER + genericLabel;
							makeFeature(featureStr, this.getFV(i), featureValue, i, signals, addIfNotPresent, useIfNotPresent);
						}
					}
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException(String.format("Exception when building features, i=%s, SentenceInstance=%s", i, problem), e);
		}
	}
	
	protected void makeFeature(String featureStr, FeatureVector fv, Integer i, List<SignalInstance> signals, boolean add_if_not_present,
			boolean use_if_not_present)
	{
		makeFeature(featureStr, fv, BigDecimal.ONE, i, signals, add_if_not_present, use_if_not_present);
	}
	
	/**
	 * add a (possible) feature to feature vector 
	 * @param featureStr
	 * @param fv
	 * @param add_if_not_present true if the feature is not in featureAlphabet, add it
	 * @param use_if_not_present true if the feature is not in featureAlphaebt, still use it in FV
	 */
	protected void makeFeature(String featureStr, FeatureVector fv, BigDecimal value, Integer i, List<SignalInstance> signals, boolean add_if_not_present,
			boolean use_if_not_present)
	{
		// Feature feat = new Feature(null, featureStr);
		// lookup the feature table to create an assignment with the new feature
		boolean wasAdded = false;
		featureStr = featureStr.intern();
		if(!use_if_not_present || add_if_not_present)
		{
			int feat_index = lookupFeatures(this.featureAlphabet, featureStr, add_if_not_present);
			if(feat_index != -1)
			{
				fv.add(featureStr, value);
				wasAdded = true;
			}
		}
		else
		{
			fv.add(featureStr, value);
			wasAdded = true;
		}
		
		if (wasAdded) {
			String strippedFeatureStr = stripLabel(featureStr);
			Map<String, String> map = featureToSignal.get(i);
			if (!featureToSignal.containsKey(i)) {
				map = new HashMap<String, String>();
				featureToSignal.put(i, map);
			}
			if (map.containsKey(strippedFeatureStr)) {
				//List<SignalInstance> existingSignals = map.get(strippedFeatureStr);
				String existingSignals = map.get(strippedFeatureStr);
				// TODO I have some feeling that this exception will rise whenever I switch back to multiple labels.
				// Perhaps this would mean that featureToSignal should be even more complex and also have a distinct "label" level.
				throw new IllegalArgumentException(String.format("Tried to store signals %s for feature (stripped) '%s' over i=%s, but this feature already exists over this i, it already has the signals %s",
						signals, strippedFeatureStr, i, existingSignals));
			}
			String signalStr;
			if (signals.size() == 1) {
				signalStr = signals.get(0).getPositiveString();
			}
			else {
				throw new RuntimeException("So, I didn't have the energy to implement this for multiple signals per feature, but now it's rising... So, please implement :)");
			}
			map.put(strippedFeatureStr, signalStr);
		}
	}
	
	/**
	 * lookup the feature (new feature), and then add to alphabet / weights vector if needed
	 * @param assn
	 * @param feat
	 * @return
	 */
	protected static int lookupFeatures(Alphabet dict, Object feat, boolean add_if_not_present)
	{
		int feat_index = dict.lookupIndex(feat, add_if_not_present);
		
		if(feat_index == -1 && !add_if_not_present)
		{
			return -1;
		}
		
		return feat_index;
	}
	
	public void setScore(BigDecimal sc)
	{
		score = sc;
	}
	
	/**
	 * get the score according to feature and weight 
	 * @return
	 */
	public BigDecimal getScore()
	{
		return score;
	}
	
	/**
	 * assume a new state(token) is added during search, then calculate the score for this state, update the total score 
	 * and then, add it to the total score
	 */
	public void updateScoreForNewState(FeatureVector weights)
	{
		FeatureVector fv = this.getCurrentFV();
		BigDecimal partial_score = fv.dotProduct(weights);
		this.partial_scores.set(state, partial_score);
		
		this.score = BigDecimal.ZERO;
		for(int i=0; i<=state; i++)
		{
			this.score = this.score.add(this.partial_scores.get(i)); //this.score += this.partial_scores.get(i);
		}
	}

	/**
	 * check if two assignments equals to each other before (inclusive) state step
	 * @param target
	 * @param state2
	 */
	public boolean equals(SentenceAssignment assn, int step)
	{
		for(int i=0; i<=step && i<=assn.state && i<=this.state; i++)
		{
			if(!assn.nodeAssignment.get(i).equals(this.nodeAssignment.get(i)))
			{
				return false;
			}
			
			//// TODO Ofer 13.5 - uncomment this when re-introducing args!
//			if(assn.edgeAssignment.get(i) != null && !assn.edgeAssignment.get(i).equals(this.edgeAssignment.get(i)))
//			{
//				return false;
//			}
//			// Qi: added April 11th, 2013
//			if(assn.edgeAssignment.get(i) == null && this.edgeAssignment.get(i) != null)
//			{
//				System.err.println("SentenceAssignment:" + 721);
//				return false;
//			}
			//////
		}
		return true;
	}
	
	/**
	 * check if two assignments equals to each other 
	 * @param target
	 * @param state2
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof SentenceAssignment))
		{
			return false;
		}
		SentenceAssignment assn = (SentenceAssignment) obj;
		if(this.state != assn.state)
		{
			return false;
		}
		for(int i=0; i<=assn.state && i<=this.state; i++)
		{
			if(!assn.nodeAssignment.get(i).equals(this.nodeAssignment.get(i)))
			{
				return false;
			}
			
			//// TODO Ofer 13.5 - uncomment this when re-introducing args!
//			if(assn.edgeAssignment.get(i) != null && !assn.edgeAssignment.get(i).equals(this.edgeAssignment.get(i)))
//			{
//				return false;
//			}
			/////
		}
		return true;
	}
	
	/**
	 * check if two assignments equals to each other before (inclusive) state step
	 * @param target
	 * @param state2
	 */
	public boolean equals(SentenceAssignment assn, int step, int argNum)
	{
		for(int i=0; i<step && i<=assn.state && i<=this.state; i++)
		{
			if(!assn.nodeAssignment.get(i).equals(this.nodeAssignment.get(i)))
			{
				return false;
			}
			
			//// TODO Ofer 12.5 - uncomment this when re-introducing args!
//			if(assn.edgeAssignment.get(i) != null && !assn.edgeAssignment.get(i).equals(this.edgeAssignment.get(i)))
//			{
//				return false;
//			}
//			// Qi: added April 11th, 2013
//			if(assn.edgeAssignment.get(i) == null && this.edgeAssignment.get(i) != null)
//			{
//				System.err.println("SentenceAssignment:" + 779);
//				return false;
//			}
			/////
			
		}
		if(!assn.nodeAssignment.get(step).equals(this.nodeAssignment.get(step)))
		{
			return false;
		}
		if(argNum < 0)
		{
			// if argument num < 0, only consider the trigger labeling
			return true;
		}
		
		//// TODO Ofer 12.5 - uncomment this when re-introducing args!
//		else
//		{
//			Map<Integer, Integer> map_assn = assn.edgeAssignment.get(step);
//			Map<Integer, Integer> map = this.edgeAssignment.get(step);
//			if(map_assn == null && map == null)
//			{
//				return true;
//			}
//			for(int k=0; k<=argNum; k++)
//			{
//				Integer label_assn = null;
//				Integer label = null;
//				if(map_assn != null)
//				{
//					label_assn = map_assn.get(k);
//				}
//				if(map != null)
//				{
//					label = map.get(k);
//				}
//				if(label == null && label_assn != null || label != null && label_assn == null)
//				{
//					return false;
//				}
//				if(label != null && label_assn != null && (!label.equals(label_assn)))
//				{
//					return false;
//				}
//			}
//		}
		/////////
		
		return true;
	}
	
	public void setViolate(boolean violate)
	{
		this.violate = violate;
	}
	
	public boolean getViolate()
	{
		return this.violate;
	}
	
	@Override
	public String toString() {
		return toString(null);
	}
	
	public String toString(Integer maxNodes)
	{
		StringBuffer ret = new StringBuffer();;
		int i=0;
		for(Integer assn : this.nodeAssignment)
		{
			if (maxNodes == null || i < maxNodes) {
				String token_label = (String) this.nodeTargetAlphabet.lookupObject(assn);
				ret.append(" ");
				ret.append(token_label);
				
				Map<Integer, Integer> edges = this.edgeAssignment.get(i);
				if(edges != null)
				{
					ret.append("(");
					for(Integer key : edges.keySet())
					{
						ret.append(" ");
						ret.append(key);
						ret.append(":");
						Integer val = edges.get(key);
						String arg_role = (String) this.edgeTargetAlphabet.lookupObject(val);
						ret.append(arg_role);
					}
					ret.append(")");
				}
				i++;
			}
			else {
				ret.append("+");
				break;
			}
		}
		return ret.toString();
	}
	
	/**
	 * check if the label of the token is an event, thereby can be attached to argument
	 * @param currentNodeLabel
	 * @return
	 */
	public static boolean isArgumentable(String label)
	{
		if(label.equalsIgnoreCase(PAD_Trigger_Label))
		{
			return false;
		}
		return true;
	}

	// to store temprary local score
	//protected double local_score = 0.0;
	
//	public void setLocalScore(double local_score)
//	{
//		this.local_score = local_score;
//	}
	
//	public double getLocalScore()
//	{
//		return local_score;
//	}

//	public void addLocalScore(double local_score)
//	{
//		this.local_score += local_score;
//	}
	
	/**
	 * clear feature vectors, so that the Target assignment can creates its feature vector in beamSearch
	 */
	public void clearFeatureVectors()
	{
		for(int i=0; i<this.featVecSequence.size(); i++)
		{
			this.featVecSequence.sequence.set(i, new FeatureVector());
		}
		featureToSignal = new HashMap<Integer, Map<String, String>>();
	}
}
