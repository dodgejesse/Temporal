package edu.uw.cs.lil.tiny.parser.ccg.joint.cky.resources;

import java.io.File;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.joint.cky.ChartLogger;

public class ChartLoggerCreator<Z> implements
		IResourceObjectCreator<ChartLogger<Z>> {
	
	private final String	name;
	
	public ChartLoggerCreator() {
		this("chart.logger");
	}
	
	public ChartLoggerCreator(String name) {
		this.name = name;
	}
	
	@Override
	public ChartLogger<Z> create(Parameters params, IResourceRepository repo) {
		return new ChartLogger<Z>(new File(params.get("outputDir")));
	}
	
	@Override
	public String resourceTypeName() {
		return name;
	}
	
}
