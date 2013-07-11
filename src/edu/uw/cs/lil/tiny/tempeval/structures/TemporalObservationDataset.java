package edu.uw.cs.lil.tiny.tempeval.structures;

import edu.uw.cs.lil.tiny.data.IDataCollection;
import java.util.Collections;
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


	public int size() {
		return observations.size();
	}
}
