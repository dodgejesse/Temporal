package edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LogicalExpressionCoordinationFeatureSet;

/**
 * Creator for {@link LogicalExpressionCoordinationFeatureSet}.
 * 
 * @author Yoav Artzi
 */
public class LogicalExpressionCoordinationFeatureSetCreator<X> implements
		IResourceObjectCreator<LogicalExpressionCoordinationFeatureSet<X>> {
	
	@Override
	public LogicalExpressionCoordinationFeatureSet<X> create(
			Parameters params, IResourceRepository repo) {
		return new LogicalExpressionCoordinationFeatureSet<X>(
				"true".equals(params.get("cpp1")), "true".equals(params
						.get("rept")), "true".equals(params.get("cpap")));
	}
	
	@Override
	public String resourceTypeName() {
		return "feat.logexp.coordination";
	}
	
}
