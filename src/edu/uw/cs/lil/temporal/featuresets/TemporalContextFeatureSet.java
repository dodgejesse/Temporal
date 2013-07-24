package edu.uw.cs.lil.temporal.featuresets;

import edu.uw.cs.lil.temporal.structures.TemporalMention;
import edu.uw.cs.lil.temporal.util.GovernerVerbPOSExtractor;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVectorImmutable;
import edu.uw.cs.utils.composites.Pair;

public class TemporalContextFeatureSet extends TemporalFeatureSet {
	protected IHashVectorImmutable setTemporalFeats(LogicalExpression logic, IHashVector feats, IDataItem<Pair<Sentence, TemporalMention>> dataItem) {
		String logicToString = logic.toString();
		Pair<String, String>  govVerbTag = GovernerVerbPOSExtractor.getGovVerbTag(dataItem.getSample().second());
		String mod = govVerbTag.first();
		String govVerbPOS = govVerbTag.second();
		
		// TODO: try adding this all the time, to see if it helps.
		String verb = "";
		if (logic.getType().getName().toString().equals("s")){
			verb = mod + "_" + govVerbPOS;
			String outerPredicate = getOuterPred(logicToString);
			feats.set(getFeatureTag() + outerPredicate + verb, 1);
		}
		return feats;
	}

	@Override
	protected String getFeatureTag() {
		return "TEMPORAL_CONTEXT_";
	}
	
}
