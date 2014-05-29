package edu.cuny.qc.perceptron.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;

public class BundledSignals implements Serializable {
	private static final long serialVersionUID = -517597227392807906L;

	/// These fields are taken from TypesContainer, and are here to document the environment in which the signals were calculated
	
	// map event subtype --> entity types
	public Map<String, Set<String>> eventEntityTypes = new HashMap<String, Set<String>>();
	// map event subtype --> argument role
	public Map<String, Set<String>> argumentRoles = new HashMap<String, Set<String>>();
	// map argument_role --> entity types 
	public Map<String, Set<String>> roleEntityTypes = new HashMap<String, Set<String>>();

	
	// These are the actual signals
	
	// Sentence\TriggerToken\Spec\Signals
	public Map<Integer, List<Map<String, Map<String, SignalInstance>>>> triggerSignals;
	
	// Sentence\TriggerToken\Spec\ArgToken\Role\Signals
	public Map<Integer, List<Map<String, List<Map<String, Map<String, SignalInstance>>>>>> argSignals;

	public BundledSignals(
			TypesContainer types,
			Map<Integer, List<Map<String, Map<String, SignalInstance>>>> triggerSignals,
			Map<Integer, List<Map<String, List<Map<String, Map<String, SignalInstance>>>>>> argSignals) {
		this.eventEntityTypes = types.eventEntityTypes;
		this.argumentRoles = types.argumentRoles;
		this.roleEntityTypes = types.roleEntityTypes;
		this.triggerSignals = triggerSignals;
		this.argSignals = argSignals;
	}

}
