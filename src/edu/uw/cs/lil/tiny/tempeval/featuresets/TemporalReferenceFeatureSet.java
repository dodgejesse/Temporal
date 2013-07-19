package edu.uw.cs.lil.tiny.tempeval.featuresets;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalMention;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVectorImmutable;
import edu.uw.cs.utils.composites.Pair;

public class TemporalReferenceFeatureSet extends TemporalFeatureSet {
	// mess with always having this feature vs only having it when the phrase is a temporal reference phrase
	protected IHashVectorImmutable setTemporalFeats(LogicalExpression logic, IHashVector feats, IDataItem<Pair<Sentence, TemporalMention>> dataItem) {
		//if (!isTempRefPhrase(dataItem.getSample().first()).equals("notTempRef")){//0){
		//if (logic.toString().startsWith("(temporal_ref"))
			feats.set(getFeatureTag() + "temporal_ref" + getOuterPred(logic.toString()) + isTempRefPhrase(dataItem.getSample().first()) , 1);
		//}
		return feats;
	}
	
	private String isTempRefPhrase(Sentence phrase){
		String p = phrase.getString();
		String[] tempPhrases = {"a year earlier", 
				"a year ago", "year earlier", "the quarter a year ago","the latest quarter",
				"the latest period", "that quarter", "that time", "that year", "the comparable year", "the following month", 
				"the following year"
				};
		for (int i = 0; i < tempPhrases.length; i++){
			if (p.equals(tempPhrases[i]))
				return tempPhrases[i]; // return "_tmpRef";
					
		}
		return "notTempRef";
	}

	@Override
	protected String getFeatureTag() {
		return "TEMPORAL_REFERENCE_";
	}

}
