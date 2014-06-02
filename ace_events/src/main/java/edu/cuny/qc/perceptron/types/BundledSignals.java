package edu.cuny.qc.perceptron.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cuny.qc.perceptron.core.Perceptron;

import ac.biu.nlp.nlp.ie.onthefly.input.TypesContainer;

public class BundledSignals implements Serializable {
	private static final long serialVersionUID = -517597227392807906L;

	// 6.2.14: I don't see the point in knowing the environment.
	// Everything is done on-demand now. If some type is missing - we'll just add it online.
//	/// These fields are taken from TypesContainer, and are here to document the environment in which the signals were calculated
//	
//	// map event subtype --> entity types
//	public Map<String, Set<String>> eventEntityTypes;
//	// map event subtype --> argument role
//	public Map<String, Set<String>> argumentRoles;
//	// map argument_role --> entity types 
//	public Map<String, Set<String>> roleEntityTypes;

	
	// Signal Names (for perceptron)
	public Set<String> triggerSignalNames;
	public Set<String> argumentSignalNames;

	
	// These are the actual signals
	
	// Sentence\TriggerToken\Spec\Signals
	public Map<Integer, List<Map<String, Map<String, SignalInstance>>>> triggerSignals;
	
	// Sentence\TriggerToken\Spec\ArgToken\Role\Signals
	public Map<Integer, List<Map<String, List<Map<String, Map<String, SignalInstance>>>>>> argSignals;

	public BundledSignals(
			//TypesContainer types,
			Perceptron perceptron,
			Map<Integer, List<Map<String, Map<String, SignalInstance>>>> triggerSignals,
			Map<Integer, List<Map<String, List<Map<String, Map<String, SignalInstance>>>>>> argSignals) {
//		this.eventEntityTypes = types.eventEntityTypes;
//		this.argumentRoles = types.argumentRoles;
//		this.roleEntityTypes = types.roleEntityTypes;
		this.triggerSignalNames = perceptron.triggerSignalNames;
		this.argumentSignalNames = perceptron.argumentSignalNames;
		this.triggerSignals = triggerSignals;
		this.argSignals = argSignals;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((argSignals == null) ? 0 : argSignals.hashCode());
//		result = prime * result
//				+ ((argumentRoles == null) ? 0 : argumentRoles.hashCode());
		result = prime
				* result
				+ ((argumentSignalNames == null) ? 0 : argumentSignalNames
						.hashCode());
//		result = prime
//				* result
//				+ ((eventEntityTypes == null) ? 0 : eventEntityTypes.hashCode());
//		result = prime * result
//				+ ((roleEntityTypes == null) ? 0 : roleEntityTypes.hashCode());
		result = prime
				* result
				+ ((triggerSignalNames == null) ? 0 : triggerSignalNames
						.hashCode());
		result = prime * result
				+ ((triggerSignals == null) ? 0 : triggerSignals.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BundledSignals other = (BundledSignals) obj;
		if (argSignals == null) {
			if (other.argSignals != null)
				return false;
		} else if (!argSignals.equals(other.argSignals))
			return false;
//		if (argumentRoles == null) {
//			if (other.argumentRoles != null)
//				return false;
//		} else if (!argumentRoles.equals(other.argumentRoles))
//			return false;
		if (argumentSignalNames == null) {
			if (other.argumentSignalNames != null)
				return false;
		} else if (!argumentSignalNames.equals(other.argumentSignalNames))
			return false;
//		if (eventEntityTypes == null) {
//			if (other.eventEntityTypes != null)
//				return false;
//		} else if (!eventEntityTypes.equals(other.eventEntityTypes))
//			return false;
//		if (roleEntityTypes == null) {
//			if (other.roleEntityTypes != null)
//				return false;
//		} else if (!roleEntityTypes.equals(other.roleEntityTypes))
//			return false;
		if (triggerSignalNames == null) {
			if (other.triggerSignalNames != null)
				return false;
		} else if (!triggerSignalNames.equals(other.triggerSignalNames))
			return false;
		if (triggerSignals == null) {
			if (other.triggerSignals != null)
				return false;
		} else if (!triggerSignals.equals(other.triggerSignals))
			return false;
		return true;
	}

}
