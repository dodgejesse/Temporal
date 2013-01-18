package edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.ScalingScorer;
import edu.uw.cs.utils.collections.IScorer;

public abstract class AbstractScaledScorerCreator<E, T extends IScorer<E>>
		implements IResourceObjectCreator<IScorer<E>> {
	
	@Override
	public final IScorer<E> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		if (parameters.contains("scale")) {
			return new ScalingScorer<E>(
					Double.valueOf(parameters.get("scale")), createScorer(
							parameters, resourceRepo));
		} else {
			return createScorer(parameters, resourceRepo);
		}
	}
	
	protected abstract T createScorer(Parameters parameters,
			IResourceRepository resourceRepo);
	
}
