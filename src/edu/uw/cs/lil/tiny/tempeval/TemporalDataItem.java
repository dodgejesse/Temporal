package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.utils.composites.Pair;

public class TemporalDataItem implements IDataItem<Pair<Sentence, String[]>>{

	Pair<Sentence, String[]> sample;
	
	public TemporalDataItem(Pair<Sentence, String[]> sample){
		this.sample = sample;
	}
	
	@Override
	public Pair<Sentence, String[]> getSample() {
		// TODO Auto-generated method stub
		return null;
	}

}
