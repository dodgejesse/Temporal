package edu.uw.cs.lil.tiny.tempeval;

import java.util.*;
import edu.uw.cs.lil.tiny.tempeval.structures.GoldSentence;


public class SerializableTemporalDataset implements java.io.Serializable {
	private static final long serialVersionUID = -9026405447128276097L;
	private final List<SerializableTemporalSentence> data;
	
	public SerializableTemporalDataset(TemporalSentenceDataset tsd){
		data = new LinkedList<SerializableTemporalSentence>();
		for (GoldSentence ts : tsd){
			data.add(new SerializableTemporalSentence(ts));
		}
	}
	
	public TemporalSentenceDataset makeTemporalSentenceDataset(){
		List<GoldSentence> newData = new LinkedList<GoldSentence>();
		for ( SerializableTemporalSentence sts : data){
			newData.add(sts.makeTemporalSentence());
		}
		return new TemporalSentenceDataset(newData);
	}
}
