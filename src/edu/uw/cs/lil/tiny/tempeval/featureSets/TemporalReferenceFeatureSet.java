package edu.uw.cs.lil.tiny.tempeval.featuresets;

import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointFeatureSet;
import edu.uw.cs.lil.tiny.utils.hashvector.HashVectorFactory;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVectorImmutable;
import edu.uw.cs.lil.tiny.utils.hashvector.KeyArgs;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.composites.Triplet;

public class TemporalReferenceFeatureSet implements IJointFeatureSet<Sentence, 
String[], LogicalExpression, LogicalExpression>{
	private static final String	FEATURE_TAG	= "TEMPORAL_REFERENCE_";

	@Override
	public List<Triplet<KeyArgs, Double, String>> getFeatureWeights(
			IHashVector theta) {
		// TODO throw error
		final List<Triplet<KeyArgs, Double, String>> weights = new LinkedList<Triplet<KeyArgs, Double, String>>();
		for (final Pair<KeyArgs, Double> feature : theta.getAll(FEATURE_TAG)) {
			weights.add(Triplet.of(feature.first(), feature.second(),
					(String) null));
		}
		return weights;
	}

	// mess with always having this feature vs only having it when the phrase is a temporal reference phrase
	private IHashVectorImmutable setTemporalFeats(LogicalExpression logic, IHashVector feats, IDataItem<Pair<Sentence, String[]>> dataItem) {
		//if (!isTempRefPhrase(dataItem.getSample().first()).equals("notTempRef")){//0){
		//if (logic.toString().startsWith("(temporal_ref"))
			feats.set(FEATURE_TAG + "temporal_ref" + getOuterPred(logic.toString()) + isTempRefPhrase(dataItem.getSample().first()) , 1);
		//}
		return feats;
	}
	
	private String getOuterPred(String l){
		if (l.startsWith("(previous:<")){
			return "_previous";
		} else if (l.startsWith("(this:<")){
			return "_this";
		} else if (l.startsWith("(next:<")){
			return "_next";
		} else if (l.startsWith("(temporal_ref:<")){
			return "temporal_ref";
		} else {
			return "_none";
		}
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
	public double score(LogicalExpression executionStep, IHashVector theta,
			IDataItem<Pair<Sentence, String[]>> dataItem) {
		return setTemporalFeats(executionStep, HashVectorFactory.create(), dataItem)
				.vectorMultiply(theta);
	}


	@Override
	public void setFeats(LogicalExpression executionStep, IHashVector feats,
			IDataItem<Pair<Sentence, String[]>> dataItem) {
		setTemporalFeats(executionStep, feats, dataItem);		
	}



}
