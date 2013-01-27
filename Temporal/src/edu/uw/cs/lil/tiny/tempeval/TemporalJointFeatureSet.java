package edu.uw.cs.lil.tiny.tempeval;

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

public class TemporalJointFeatureSet implements IJointFeatureSet<Sentence, 
String[], LogicalExpression, LogicalExpression>{
	private static final String	FEATURE_TAG	= "TEMPORAL";

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

	
	private IHashVectorImmutable setFeats(String logicToString, IHashVector feats) {
		
		if (logicToString.startsWith("(previous:<")){
			feats.set(FEATURE_TAG + "(previous:<", 1);
		} else if (logicToString.startsWith("(this:<")){
			feats.set(FEATURE_TAG + "(this:<", 1);
		} else if (logicToString.startsWith("(next:<")){
			feats.set(FEATURE_TAG + "(next:<", 1);
		} else if (logicToString.startsWith("(temporal_ref:<")){
			feats.set(FEATURE_TAG + "(temporal_ref:<", 1);
		} else {
			feats.set(FEATURE_TAG + "none", 1);
		}
		return feats;
	}

	@Override
	public double score(LogicalExpression executionStep, IHashVector theta,
			IDataItem<Pair<Sentence, String[]>> dataItem) {
		return setFeats(executionStep.toString(), HashVectorFactory.create())
				.vectorMultiply(theta);
	}


	@Override
	public void setFeats(LogicalExpression executionStep, IHashVector feats,
			IDataItem<Pair<Sentence, String[]>> dataItem) {
		setFeats(executionStep.toString(), feats);		
	}

}
