package edu.uw.cs.lil.tiny.parser.joint.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.Lexicon;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.parser.ccg.model.lexical.IIndependentLexicalFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.model.parse.IParseFeatureSet;
import edu.uw.cs.lil.tiny.utils.hashvector.HashVectorFactory;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.utils.composites.Pair;

public class JointModel<X, W, Y, Z> extends Model<X, Y> implements
		IJointModelImmutable<X, W, Y, Z> {
	
	private final List<IJointFeatureSet<X, W, Y, Z>>	jointFeatures;
	
	protected JointModel(
			List<IIndependentLexicalFeatureSet<X, Y>> lexicalFeatures,
			List<IParseFeatureSet<X, Y>> parseFeatures,
			List<IJointFeatureSet<X, W, Y, Z>> jointFeatures,
			ILexicon<Y> lexicon) {
		super(lexicalFeatures, parseFeatures, lexicon);
		this.jointFeatures = Collections.unmodifiableList(jointFeatures);
	}
	
	@Override
	public IHashVector computeFeatures(Pair<Y, Z> result,
			IDataItem<Pair<X, W>> dataItem) {
		final IHashVector features = HashVectorFactory.create();
		for (final IJointFeatureSet<X, W, Y, Z> featureSet : jointFeatures) {
			featureSet.setFeats(result, features, dataItem);
		}
		return features;
	}
	
	@Override
	public IJointDataItemModel<Y, Z> createJointDataItemModel(
			IDataItem<Pair<X, W>> dataItem) {
		return new JointDataItemModel<X, W, Y, Z>(this, dataItem);
	}
	
	@Override
	public double score(Pair<Y, Z> result, IDataItem<Pair<X, W>> dataItem) {
		double score = 0.0;
		for (final IJointFeatureSet<X, W, Y, Z> featureSet : jointFeatures) {
			score += featureSet.score(result, getTheta(), dataItem);
		}
		return score;
	}
	
	public static class Builder<X, W, Y, Z> {
		
		private final List<IJointFeatureSet<X, W, Y, Z>>		jointFeatures	= new LinkedList<IJointFeatureSet<X, W, Y, Z>>();
		private final List<IIndependentLexicalFeatureSet<X, Y>>	lexicalFeatures	= new LinkedList<IIndependentLexicalFeatureSet<X, Y>>();
		private ILexicon<Y>										lexicon			= new Lexicon<Y>();
		private final List<IParseFeatureSet<X, Y>>				parseFeatures	= new LinkedList<IParseFeatureSet<X, Y>>();
		
		public Builder<X, W, Y, Z> addJointFeatureSet(
				IJointFeatureSet<X, W, Y, Z> featureSet) {
			jointFeatures.add(featureSet);
			return this;
		}
		
		public Builder<X, W, Y, Z> addLexicalFeatureSet(
				IIndependentLexicalFeatureSet<X, Y> featureSet) {
			lexicalFeatures.add(featureSet);
			return this;
		}
		
		public Builder<X, W, Y, Z> addParseFeatureSet(
				IParseFeatureSet<X, Y> featureSet) {
			parseFeatures.add(featureSet);
			return this;
		}
		
		public JointModel<X, W, Y, Z> build() {
			return new JointModel<X, W, Y, Z>(lexicalFeatures, parseFeatures,
					jointFeatures, lexicon);
		}
		
		public Builder<X, W, Y, Z> setLexicon(ILexicon<Y> lexicon) {
			this.lexicon = lexicon;
			return this;
		}
		
	}
}
