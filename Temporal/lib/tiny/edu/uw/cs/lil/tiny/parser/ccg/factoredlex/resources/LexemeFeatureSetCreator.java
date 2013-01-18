package edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.Lexeme;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.LexemeFeatureSet;
import edu.uw.cs.utils.collections.IScorer;

/**
 * Creator for {@link LexemeFeatureSet}.
 * 
 * @author Yoav Artzi
 */
public class LexemeFeatureSetCreator<X> implements
		IResourceObjectCreator<LexemeFeatureSet<X>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public LexemeFeatureSet<X> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		final LexemeFeatureSet.Builder<X> builder = new LexemeFeatureSet.Builder<X>();
		
		if (parameters.contains("scale")) {
			builder.setScale(Double.valueOf(parameters.get("scale")));
		}
		
		if (parameters.contains("tag")) {
			builder.setFeatureTag(parameters.get("tag"));
		}
		
		if (parameters.contains("initFixed")) {
			builder.setInitialFixedScorer((IScorer<Lexeme>) resourceRepo
					.getResource(parameters.get("initFixed")));
		}
		
		if (parameters.contains("init")) {
			builder.setInitialScorer((IScorer<Lexeme>) resourceRepo
					.getResource(parameters.get("init")));
		}
		
		return builder.build();
	}
	
	@Override
	public String resourceTypeName() {
		return "feat.lexeme";
	}
	
}
