package edu.cuny.qc.perceptron.types;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.cuny.qc.scorer.ScorerData;
import edu.cuny.qc.util.FinalKeysMap;

public class BundledSignals implements Serializable {
	private static final long serialVersionUID = -517597227392807906L;

	static {
		System.err.println("??? BundledSignals: Not supporting args yet as well! also, in args the spec and role are still numbers, should be strings");
	}
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
//	public Set<ScorerData> triggerScorers;
//	public Set<ScorerData> argumentScorers;

	
	// These are the actual signals
	
	// Sentence\TriggerToken\Spec\Signals
	public Map<Integer, List<Map<String, Map<ScorerData, SignalInstance>>>> triggerSignals;
	
	// Sentence\Spec\ArgCand\Role\Signals
	public Map<Integer, Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>> argFreeSignals;
	
	// Sentence\TriggerToken\Spec\ArgCand\Role\Signals
	public Map<Integer, List<Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>>> argDependentSignals;

	public BundledSignals(
			//TypesContainer types,
//			Perceptron perceptron,
//			Map<Integer, List<Map<Integer, Map<ScorerData, SignalInstance>>>> triggerSignals,
//			Map<Integer, List<Map<Integer, List<Map<Integer, Map<ScorerData, SignalInstance>>>>>> argSignals
			) {
//		this.eventEntityTypes = types.eventEntityTypes;
//		this.argumentRoles = types.argumentRoles;
//		this.roleEntityTypes = types.roleEntityTypes;
//		this.triggerScorers = perceptron.triggerScorers;
//		this.argumentScorers = perceptron.argumentScorers;
		
		this.triggerSignals = new FinalKeysMap<Integer, List<Map<String, Map<ScorerData, SignalInstance>>>>();
		this.argFreeSignals = new FinalKeysMap<Integer, Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>>();
		this.argDependentSignals = new FinalKeysMap<Integer, List<Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>>>();
	}

	public void absorb(BundledSignals other) {
		for (Iterator<Entry<Integer, List<Map<String, Map<ScorerData, SignalInstance>>>>> iter1 = other.triggerSignals.entrySet().iterator(); iter1.hasNext();) {
			Entry<Integer, List<Map<String, Map<ScorerData, SignalInstance>>>> entry1 = iter1.next();
			if (!this.triggerSignals.containsKey(entry1.getKey())) {
				this.triggerSignals.put(entry1.getKey(), entry1.getValue());
			}
			else {
				List<Map<String, Map<ScorerData, SignalInstance>>> this1 = this.triggerSignals.get(entry1.getKey());
				int n=0;
				for (Iterator<Map<String, Map<ScorerData, SignalInstance>>> iter2 = entry1.getValue().iterator(); iter2.hasNext();) {
					Map<String, Map<ScorerData, SignalInstance>> elem2 = iter2.next();
					Map<String, Map<ScorerData, SignalInstance>> this2 = this1.get(n);
					n++;
					
					for (Iterator<Entry<String, Map<ScorerData, SignalInstance>>> iter3 = elem2.entrySet().iterator(); iter3.hasNext();) {
						Entry<String, Map<ScorerData, SignalInstance>> entry3 = iter3.next();
						if (!this2.containsKey(entry3.getKey())) {
							this2.put(entry3.getKey(), entry3.getValue());
						}
						else {
							Map<ScorerData, SignalInstance> this3 = this2.get(entry3.getKey());
							
							for (Iterator<Entry<ScorerData, SignalInstance>> iter4 = entry3.getValue().entrySet().iterator(); iter4.hasNext();) {
								Entry<ScorerData, SignalInstance> entry4 = iter4.next();
								if (!this3.containsKey(entry4.getKey()) ||
										(this3.get(entry4.getKey()).history == null && entry4.getValue().history != null) ) {
									this3.put(entry4.getKey(), entry4.getValue());
								}
							}
						}
					}
				}
			}
		}

		for (Iterator<Entry<Integer, Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>>> iter1b = other.argFreeSignals.entrySet().iterator(); iter1b.hasNext();) {
			Entry<Integer, Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>> entry1b = iter1b.next();
			if (!this.argFreeSignals.containsKey(entry1b.getKey())) {
				this.argFreeSignals.put(entry1b.getKey(), entry1b.getValue());
			}
			else {
				Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>> this1b = this.argFreeSignals.get(entry1b.getKey());
//				int nb=0;
//				for (Iterator<Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>> iter2b = entry1b.getValue().iterator(); iter2b.hasNext();) {
//					Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>> elem2b = iter2b.next();
//					Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>> this2b = this1b.get(nb);
//					nb++;
					
					for (Iterator<Entry<String, List<Map<String, Map<ScorerData, SignalInstance>>>>> iter3b = entry1b.getValue().entrySet().iterator(); iter3b.hasNext();) {
						Entry<String, List<Map<String, Map<ScorerData, SignalInstance>>>> entry3b = iter3b.next();
						if (!this1b.containsKey(entry3b.getKey())) {
							this1b.put(entry3b.getKey(), entry3b.getValue());
						}
						else {
							List<Map<String, Map<ScorerData, SignalInstance>>> this3b = this1b.get(entry3b.getKey());
							int mb=0;
							for (Iterator<Map<String, Map<ScorerData, SignalInstance>>> iter4b = entry3b.getValue().iterator(); iter4b.hasNext();) {
								Map<String, Map<ScorerData, SignalInstance>> elem4b = iter4b.next();
								Map<String, Map<ScorerData, SignalInstance>> this4b = this3b.get(mb);
								mb++;
								
								for (Iterator<Entry<String, Map<ScorerData, SignalInstance>>> iter5b = elem4b.entrySet().iterator(); iter5b.hasNext();) {
									Entry<String, Map<ScorerData, SignalInstance>> entry5b = iter5b.next();
									if (!this4b.containsKey(entry5b.getKey())) {
										this4b.put(entry5b.getKey(), entry5b.getValue());
									}
									else {
										Map<ScorerData, SignalInstance> this5b = this4b.get(entry5b.getKey());
										
										for (Iterator<Entry<ScorerData, SignalInstance>> iter6b = entry5b.getValue().entrySet().iterator(); iter6b.hasNext();) {
											Entry<ScorerData, SignalInstance> entry6b = iter6b.next();
											if (!this5b.containsKey(entry6b.getKey()) ||
													(this5b.get(entry6b.getKey()).history == null && entry6b.getValue().history != null) ) {
												this5b.put(entry6b.getKey(), entry6b.getValue());
											}
										}
									}
								}
							}
						}
					}
				//}
			}
		}
		for (Iterator<Entry<Integer, List<Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>>>> iter1b = other.argDependentSignals.entrySet().iterator(); iter1b.hasNext();) {
			Entry<Integer, List<Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>>> entry1b = iter1b.next();
			if (!this.argDependentSignals.containsKey(entry1b.getKey())) {
				this.argDependentSignals.put(entry1b.getKey(), entry1b.getValue());
			}
			else {
				List<Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>> this1b = this.argDependentSignals.get(entry1b.getKey());
				int nb=0;
				for (Iterator<Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>>> iter2b = entry1b.getValue().iterator(); iter2b.hasNext();) {
					Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>> elem2b = iter2b.next();
					Map<String, List<Map<String, Map<ScorerData, SignalInstance>>>> this2b = this1b.get(nb);
					nb++;
					
					for (Iterator<Entry<String, List<Map<String, Map<ScorerData, SignalInstance>>>>> iter3b = elem2b.entrySet().iterator(); iter3b.hasNext();) {
						Entry<String, List<Map<String, Map<ScorerData, SignalInstance>>>> entry3b = iter3b.next();
						if (!this2b.containsKey(entry3b.getKey())) {
							this2b.put(entry3b.getKey(), entry3b.getValue());
						}
						else {
							List<Map<String, Map<ScorerData, SignalInstance>>> this3b = this2b.get(entry3b.getKey());
							int mb=0;
							for (Iterator<Map<String, Map<ScorerData, SignalInstance>>> iter4b = entry3b.getValue().iterator(); iter4b.hasNext();) {
								Map<String, Map<ScorerData, SignalInstance>> elem4b = iter4b.next();
								Map<String, Map<ScorerData, SignalInstance>> this4b = this3b.get(mb);
								mb++;
								
								for (Iterator<Entry<String, Map<ScorerData, SignalInstance>>> iter5b = elem4b.entrySet().iterator(); iter5b.hasNext();) {
									Entry<String, Map<ScorerData, SignalInstance>> entry5b = iter5b.next();
									if (!this4b.containsKey(entry5b.getKey())) {
										this4b.put(entry5b.getKey(), entry5b.getValue());
									}
									else {
										Map<ScorerData, SignalInstance> this5b = this4b.get(entry5b.getKey());
										
										for (Iterator<Entry<ScorerData, SignalInstance>> iter6b = entry5b.getValue().entrySet().iterator(); iter6b.hasNext();) {
											Entry<ScorerData, SignalInstance> entry6b = iter6b.next();
											if (!this5b.containsKey(entry6b.getKey()) ||
													(this5b.get(entry6b.getKey()).history == null && entry6b.getValue().history != null) ) {
												this5b.put(entry6b.getKey(), entry6b.getValue());
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((argDependentSignals == null) ? 0 : argDependentSignals.hashCode());
//		result = prime * result
//				+ ((argumentRoles == null) ? 0 : argumentRoles.hashCode());
//		result = prime
//				* result
//				+ ((argumentScorers == null) ? 0 : argumentScorers
//						.hashCode());
//		result = prime
//				* result
//				+ ((eventEntityTypes == null) ? 0 : eventEntityTypes.hashCode());
//		result = prime * result
//				+ ((roleEntityTypes == null) ? 0 : roleEntityTypes.hashCode());
//		result = prime
//				* result
//				+ ((triggerScorers == null) ? 0 : triggerScorers
//						.hashCode());
		result = prime * result
				+ ((triggerSignals == null) ? 0 : triggerSignals.hashCode());
		result = prime * result
				+ ((argFreeSignals == null) ? 0 : argFreeSignals.hashCode());
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
		if (argDependentSignals == null) {
			if (other.argDependentSignals != null)
				return false;
		} else if (!argDependentSignals.equals(other.argDependentSignals))
			return false;
//		if (argumentRoles == null) {
//			if (other.argumentRoles != null)
//				return false;
//		} else if (!argumentRoles.equals(other.argumentRoles))
//			return false;
//		if (argumentScorers == null) {
//			if (other.argumentScorers != null)
//				return false;
//		} else if (!argumentScorers.equals(other.argumentScorers))
//			return false;
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
//		if (triggerScorers == null) {
//			if (other.triggerScorers != null)
//				return false;
//		} else if (!triggerScorers.equals(other.triggerScorers))
//			return false;
		if (triggerSignals == null) {
			if (other.triggerSignals != null)
				return false;
		} else if (!triggerSignals.equals(other.triggerSignals))
			return false;
		if (argFreeSignals == null) {
			if (other.argFreeSignals != null)
				return false;
		} else if (!argFreeSignals.equals(other.argFreeSignals))
			return false;
		return true;
	}

}
