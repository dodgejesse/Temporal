package edu.uw.cs.lil.tiny.tempeval.featuresets;

import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalMention;
import edu.uw.cs.lil.tiny.utils.hashvector.HashVectorFactory;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVectorImmutable;
import edu.uw.cs.lil.tiny.utils.hashvector.KeyArgs;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.composites.Triplet;

public abstract class TemporalFeatureSet implements IJointFeatureSet<Sentence, TemporalMention, LogicalExpression, LogicalExpression>{
	@Override
	public List<Triplet<KeyArgs, Double, String>> getFeatureWeights(IHashVector theta) {
		final List<Triplet<KeyArgs, Double, String>> weights = new LinkedList<Triplet<KeyArgs, Double, String>>();
		for (final Pair<KeyArgs, Double> feature : theta.getAll(getFeatureTag())) {
			weights.add(Triplet.of(feature.first(), feature.second(),
					(String) null));
		}
		return weights;
	}

	abstract protected String getFeatureTag();

	abstract protected IHashVectorImmutable setTemporalFeats(LogicalExpression logic, IHashVector feats, IDataItem<Pair<Sentence, TemporalMention>> dataItem);
	
	protected String getOuterPred(String l){
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
			IDataItem<Pair<Sentence, TemporalMention>> dataItem) {
		return setTemporalFeats(executionStep, HashVectorFactory.create(), dataItem)
				.vectorMultiply(theta);
	}

	@Override
	public void setFeats(LogicalExpression executionStep, IHashVector feats,
			IDataItem<Pair<Sentence, TemporalMention>> dataItem) {
		setTemporalFeats(executionStep, feats, dataItem);		
	}

}
