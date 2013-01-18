package edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LogicalExpressionCooccurrenceFeatureSet;

public class LogicalExpressionCooccurrenceFeatureSetCreator<X> implements
		IResourceObjectCreator<LogicalExpressionCooccurrenceFeatureSet<X>> {
	
	@Override
	public LogicalExpressionCooccurrenceFeatureSet<X> create(
			Parameters parameters, IResourceRepository resourceRepo) {
		return new LogicalExpressionCooccurrenceFeatureSet<X>();
	}
	
	@Override
	public String resourceTypeName() {
		return "feat.logexp.cooc";
	}
	
}
