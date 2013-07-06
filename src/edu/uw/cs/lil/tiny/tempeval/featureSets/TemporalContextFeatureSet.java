package edu.uw.cs.lil.tiny.tempeval.featureSets;

import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.GovernerVerbPOSExtractor;
import edu.uw.cs.lil.tiny.utils.hashvector.HashVectorFactory;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVectorImmutable;
import edu.uw.cs.lil.tiny.utils.hashvector.KeyArgs;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.composites.Triplet;

public class TemporalContextFeatureSet implements IJointFeatureSet<Sentence, 
String[], LogicalExpression, LogicalExpression>{
	private static final String	FEATURE_TAG	= "TEMPORAL_CONTEXT_";

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


	private IHashVectorImmutable setTemporalFeats(LogicalExpression logic, IHashVector feats, IDataItem<Pair<Sentence, String[]>> dataItem) {
		String logicToString = logic.toString();
		Pair<String, String>  govVerbTag = GovernerVerbPOSExtractor.getGovVerbTag(dataItem.getSample().second());
		String mod = govVerbTag.first();
		String govVerbPOS = govVerbTag.second();
		
		// TODO: try adding this all the time, to see if it helps.
		String verb = "";
		if (logic.getType().getName().toString().equals("s")){
			verb = mod + "_" + govVerbPOS;
			String outerPredicate = getOuterPred(logicToString);
			feats.set(FEATURE_TAG + outerPredicate + verb, 1);
		}
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
