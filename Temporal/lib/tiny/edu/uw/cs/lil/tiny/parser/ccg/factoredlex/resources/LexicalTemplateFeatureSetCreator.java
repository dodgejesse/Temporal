package edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.LexicalTemplate;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.LexicalTemplateFeatureSet;
import edu.uw.cs.utils.collections.IScorer;

public class LexicalTemplateFeatureSetCreator<X> implements
		IResourceObjectCreator<LexicalTemplateFeatureSet<X>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public LexicalTemplateFeatureSet<X> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		final LexicalTemplateFeatureSet.Builder<X> builder = new LexicalTemplateFeatureSet.Builder<X>();
		
		if (parameters.contains("scale")) {
			builder.setScale(Double.valueOf(parameters.get("scale")));
		}
		
		if (parameters.contains("tag")) {
			builder.setFeatureTag(parameters.get("tag"));
		}
		
		if (parameters.contains("initFixed")) {
			builder.setInitialFixedScorer((IScorer<LexicalTemplate>) resourceRepo
					.getResource(parameters.get("initFixed")));
		}
		
		if (parameters.contains("init")) {
			builder.setInitialScorer((IScorer<LexicalTemplate>) resourceRepo
					.getResource(parameters.get("init")));
		}
		
		return builder.build();
	}
	
	@Override
	public String resourceTypeName() {
		return "feat.lextemplate";
	}
	
}
