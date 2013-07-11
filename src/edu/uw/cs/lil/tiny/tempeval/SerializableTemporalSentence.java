package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.tempeval.structures.GoldSentence;

public class SerializableTemporalSentence implements java.io.Serializable {
	private static final long serialVersionUID = 352404484131899126L;
	//private final Pair<String, String> label;
	private final String type;
	private final String val;
	// order of array: 
	// {docID, sentence, refDate, prevDocID, charNum, dependencyParse};
	private final String[] worldInfo;
	private final String phrase;
	
	public SerializableTemporalSentence(GoldSentence ts){
		type = ts.getLabel().type;
		val = ts.getLabel().val;
		worldInfo = ts.getSample().second();
		phrase = ts.getSample().first().getString();
	}
	
	public GoldSentence makeTemporalSentence(){

// 		order of params: 
//		docID = d;
//		sentence = s;
//		charNum = c;
//		phrase = p;
//		refDate = r;
//		type = t;
//		val = v;
//		prevDocID = prev;
//		dependencyParse = dp;

		return new GoldSentence(new Sentence(phrase), worldInfo[0], worldInfo[1], worldInfo[4], worldInfo[2], type, val, worldInfo[3], worldInfo[5]);
	}

}
