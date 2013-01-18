package edu.uw.cs.lil.tiny.explat.resources;

import java.util.HashMap;
import java.util.Map;

public class ResourceCreatorRepository {
	
	private final Map<String, IResourceObjectCreator<?>>	resourcesCreators	= new HashMap<String, IResourceObjectCreator<?>>();
	
	public ResourceCreatorRepository() {
	}
	
	public final IResourceObjectCreator<?> getCreator(String type) {
		return resourcesCreators.get(type);
	}
	
	public final void registerResourceCreator(IResourceObjectCreator<?> creator) {
		resourcesCreators.put(creator.resourceTypeName(), creator);
	}
	
}
