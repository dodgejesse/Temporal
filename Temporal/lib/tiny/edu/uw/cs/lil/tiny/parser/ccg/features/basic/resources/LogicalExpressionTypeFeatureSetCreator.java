package edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LogicalExpressionTypeFeatureSet;

/**
 * Creator for {@link LogicalExpressionTypeFeatureSet}.
 * 
 * @author Yoav Artzi
 */
public class LogicalExpressionTypeFeatureSetCreator<X> implements
		IResourceObjectCreator<LogicalExpressionTypeFeatureSet<X>> {
	
	@Override
	public LogicalExpressionTypeFeatureSet<X> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		return new LogicalExpressionTypeFeatureSet<X>();
	}
	
	@Override
	public String resourceTypeName() {
		return "feat.logexp.type";
	}
	
}
