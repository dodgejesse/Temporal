package edu.uw.cs.lil.tiny.parser.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.model.ModelLogger;

public class ModelLoggerCreator implements IResourceObjectCreator<ModelLogger> {
	
	@Override
	public ModelLogger create(Parameters params, IResourceRepository repo) {
		return new ModelLogger("true".equals(params.get("cluster")));
	}
	
	@Override
	public String resourceTypeName() {
		return "logger.model";
	}
	
}
