package edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LexicalFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.utils.collections.IScorer;

/**
 * Creator for {@link LexicalFeatureSet}.
 * 
 * @author Yoav Artzi
 */
public class LexicalFeatureSetCreator<X, Y> implements
		IResourceObjectCreator<LexicalFeatureSet<X, Y>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public LexicalFeatureSet<X, Y> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		final LexicalFeatureSet.Builder<X, Y> builder = new LexicalFeatureSet.Builder<X, Y>();
		
		if (parameters.contains("tag")) {
			builder.setFeatureTag(parameters.get("tag"));
		}
		
		if (parameters.contains("initFixed")) {
			builder.setInitialFixedScorer((IScorer<LexicalEntry<Y>>) resourceRepo
					.getResource(parameters.get("initFixed")));
		}
		
		if (parameters.contains("init")) {
			builder.setInitialScorer((IScorer<LexicalEntry<Y>>) resourceRepo
					.getResource(parameters.get("init")));
		}
		
		return builder.build();
	}
	
	@Override
	public String resourceTypeName() {
		return "feat.lex";
	}
	
}
