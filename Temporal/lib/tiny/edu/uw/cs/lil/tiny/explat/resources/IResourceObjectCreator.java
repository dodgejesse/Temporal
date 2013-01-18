package edu.uw.cs.lil.tiny.explat.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;

public interface IResourceObjectCreator<T> {
	T create(Parameters params, IResourceRepository repo);
	
	String resourceTypeName();
}
