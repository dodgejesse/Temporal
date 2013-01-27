package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.utils.composites.Pair;

public class TemporalDataItem implements IDataItem<Pair<Sentence, String[]>> {

	Pair<Sentence, String[]> s;
	
	public TemporalDataItem(Pair<Sentence, String[]> s){
		this.s = s;
	}

	@Override
	public Pair<Sentence, String[]> getSample() {
		return s;
	}
	


}
