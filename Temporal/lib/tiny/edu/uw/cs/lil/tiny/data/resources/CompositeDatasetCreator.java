package edu.uw.cs.lil.tiny.data.resources;

import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.composite.CompositeDataset;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.utils.collections.ListUtils;

public class CompositeDatasetCreator<T> implements
		IResourceObjectCreator<CompositeDataset<T>> {
	private static final String	DEFAULT_NAME	= "data.composite";
	private final String		resourceName;
	
	public CompositeDatasetCreator() {
		this(DEFAULT_NAME);
	}
	
	public CompositeDatasetCreator(String resourceName) {
		this.resourceName = resourceName;
	}
	
	@Override
	public CompositeDataset<T> create(Parameters parameters,
			final IResourceRepository resourceRepo) {
		return new CompositeDataset<T>(ListUtils.map(
				parameters.getSplit("sets"),
				new ListUtils.Mapper<String, IDataCollection<? extends T>>() {
					
					@Override
					public IDataCollection<T> process(String obj) {
						return resourceRepo.getResource(obj);
					}
				}));
	}
	
	@Override
	public String resourceTypeName() {
		return resourceName;
	}
	
}
