package edu.uw.cs.lil.tiny.learn.ubl.resources;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.learn.ubl.splitting.ConstrainedSplitter;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

public class ConstrainedSplitterCreator implements
		IResourceObjectCreator<ConstrainedSplitter> {
	
	@SuppressWarnings("unchecked")
	@Override
	public ConstrainedSplitter create(Parameters parameters,
			IResourceRepository resourceRepo) {
		return new ConstrainedSplitter(
				(ICategoryServices<LogicalExpression>) resourceRepo
						.getResource("categoryServices"));
	}
	
	@Override
	public String resourceTypeName() {
		return "ubl.splitter.constrained";
	}
	
}
