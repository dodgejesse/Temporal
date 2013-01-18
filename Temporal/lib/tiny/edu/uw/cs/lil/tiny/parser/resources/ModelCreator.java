package edu.uw.cs.lil.tiny.parser.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.Lexicon;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model.Builder;
import edu.uw.cs.lil.tiny.parser.ccg.model.lexical.IIndependentLexicalFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.model.parse.IParseFeatureSet;

public class ModelCreator<X, Y> implements IResourceObjectCreator<Model<X, Y>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public Model<X, Y> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		final Builder<X, Y> builder = new Model.Builder<X, Y>();
		
		// Lexicon
		builder.setLexicon((ILexicon<Y>) resourceRepo.getResource(parameters
				.get("lexicon")));
		
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
		
		final Model<X, Y> model = builder.build();
		
		// Initial lexical entries, if exists
		if (parameters.get("initialLexicon") != null) {
			model.addFixedLexicalEntries((ILexicon<Y>) resourceRepo
					.getResource(parameters.get("initialLexicon")));
		}
		
		return model;
	}
	
	@Override
	public String resourceTypeName() {
		return "model.new";
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
