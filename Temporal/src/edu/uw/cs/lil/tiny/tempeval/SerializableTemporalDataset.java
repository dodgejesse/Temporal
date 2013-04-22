package edu.uw.cs.lil.tiny.tempeval;

import java.util.*;

public class SerializableTemporalDataset implements java.io.Serializable {
	private static final long serialVersionUID = -9026405447128276097L;
	private final List<SerializableTemporalSentence> data;
	
	public SerializableTemporalDataset(TemporalSentenceDataset tsd){
		data = new LinkedList<SerializableTemporalSentence>();
		for (TemporalSentence ts : tsd){
			data.add(new SerializableTemporalSentence(ts));
		}
	}
	
	public TemporalSentenceDataset makeTemporalSentenceDataset(){
		List<TemporalSentence> newData = new LinkedList<TemporalSentence>();
		for ( SerializableTemporalSentence sts : data){
			newData.add(sts.makeTemporalSentence());
		}
		return new TemporalSentenceDataset(newData);
	}
}
