package edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.RuleUsageFeatureSet;

public class RuleUsageFeatureSetCreator<X, Y> implements
		IResourceObjectCreator<RuleUsageFeatureSet<X, Y>> {
	
	@Override
	public RuleUsageFeatureSet<X, Y> create(Parameters params,
			IResourceRepository repo) {
		return new RuleUsageFeatureSet<X, Y>(
				params.contains("scale") ? Double.valueOf(params.get("scale"))
						: 1.0);
	}
	
	@Override
	public String resourceTypeName() {
		return "feat.rules.count";
	}
	
}
