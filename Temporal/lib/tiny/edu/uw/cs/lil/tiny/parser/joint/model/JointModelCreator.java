package edu.uw.cs.lil.tiny.parser.joint.model;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.Lexicon;
import edu.uw.cs.lil.tiny.parser.ccg.model.lexical.IIndependentLexicalFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.model.parse.IParseFeatureSet;

public class JointModelCreator<X extends IDataItem<X>, W, Y, Z> implements
		IResourceObjectCreator<JointModel<X, W, Y, Z>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public JointModel<X, W, Y, Z> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		final JointModel.Builder<X, W, Y, Z> builder = new JointModel.Builder<X, W, Y, Z>();
		
		// Lexicon
		builder.setLexicon((ILexicon<Y>) resourceRepo.getResource((parameters
				.get("lexicon"))));
		
		// Lexical feature sets
		for (final String setId : parameters.getSplit("lexicalFeatures")) {
			builder.addLexicalFeatureSet((IIndependentLexicalFeatureSet<X, Y>) resourceRepo
					.getResource(setId));
		}
		
		// Parse feature sets
		for (final String setId : parameters.getSplit("parseFeatures")) {
			builder.addParseFeatureSet((IParseFeatureSet<X, Y>) resourceRepo
					.getResource(setId));
		}
		
		// Joint feature sets
		for (final String setId : parameters.getSplit("jointFeatures")) {
			builder.addJointFeatureSet((IJointFeatureSet<X, W, Y, Z>) resourceRepo
					.getResource(setId));
		}
		
		final JointModel<X, W, Y, Z> model = builder.build();
		
		// Initial lexical entries, if exists
		if (parameters.get("initialLexicon") != null) {
			model.addFixedLexicalEntries((ILexicon<Y>) resourceRepo
					.getResource(parameters.get("initialLexicon")));
		}
		
		return model;
	}
	
	@Override
	public String resourceTypeName() {
		return "model.joint";
	}
	
	protected ILexicon<Y> createLexicon(String lexiconType) {
		if ("conventional".equals(lexiconType)) {
			return new Lexicon<Y>();
		} else {
			throw new IllegalArgumentException("Invalid lexicon type: "
					+ lexiconType);
		}
	}
	
}
