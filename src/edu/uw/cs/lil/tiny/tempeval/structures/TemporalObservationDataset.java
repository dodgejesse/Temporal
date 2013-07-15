package edu.uw.cs.lil.tiny.tempeval.structures;

import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.tempeval.util.Debug;
import edu.uw.cs.lil.tiny.tempeval.util.Debug.Type;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TemporalObservationDataset implements IDataCollection<TemporalObservation>{
	private List<TemporalObservation> observations;
	
	public TemporalObservationDataset(List<TemporalObservation> observations) {
		this.observations = observations;
	}
	
	public TemporalObservationDataset() {
		observations = new LinkedList<TemporalObservation>();
	}
	
	public Iterator<TemporalObservation> iterator() {
		return observations.iterator();
	}
	
	public void addObservations(List<TemporalObservation> newObservations) {
		observations.addAll(newObservations);
	}

	public String toString() {
		String s = "";
		for(TemporalObservation o : observations)
			s += "[" + o.getReferenceTime() + "]\n";
		return s;
	}

	public int size() {
		return observations.size();
	}
}
