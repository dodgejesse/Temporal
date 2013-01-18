package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.utils.composites.Pair;

public class TemporalDataItem implements IDataItem<Pair<Sentence, Pair<String[], Pair<Sentence, TemporalSentence>>>> {

	Pair<String[],Pair<Sentence,TemporalSentence>> s;
	
	public TemporalDataItem(Pair<String[],Pair<Sentence,TemporalSentence>> s){
		this.s = s;
	}
	
	@Override
	public Pair<Sentence, Pair<String[], Pair<Sentence, TemporalSentence>>> getSample() {
		return Pair.of(s.second().first(), s);
	}

}
