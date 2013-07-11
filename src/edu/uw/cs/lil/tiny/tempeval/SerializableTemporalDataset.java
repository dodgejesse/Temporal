package edu.uw.cs.lil.tiny.tempeval;

import java.util.*;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalObservation;


public class SerializableTemporalDataset implements java.io.Serializable {
	private static final long serialVersionUID = -9026405447128276097L;
	private final List<SerializableTemporalSentence> data;
	
	public SerializableTemporalDataset(TemporalSentenceDataset tsd){
		data = new LinkedList<SerializableTemporalSentence>();
		for (TemporalObservation ts : tsd){
			data.add(new SerializableTemporalSentence(ts));
		}
	}
	
	public TemporalSentenceDataset makeTemporalSentenceDataset(){
		List<TemporalObservation> newData = new LinkedList<TemporalObservation>();
		for ( SerializableTemporalSentence sts : data){
			newData.add(sts.makeTemporalSentence());
		}
		return new TemporalSentenceDataset(newData);
	}
}
