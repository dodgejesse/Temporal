package edu.uw.cs.lil.tiny.learn.ubl.resources;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.learn.ubl.splitting.UnconstrainedSplitter;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

public class UnconstrainedSplitterCreator implements
		IResourceObjectCreator<UnconstrainedSplitter> {
	
	@SuppressWarnings("unchecked")
	@Override
	public UnconstrainedSplitter create(Parameters parameters,
			IResourceRepository resourceRepo) {
		return new UnconstrainedSplitter(
				(ICategoryServices<LogicalExpression>) resourceRepo
						.getResource("categoryServices"));
	}
	
	@Override
	public String resourceTypeName() {
		return "ubl.splitter.unconstrained";
	}
	
}
