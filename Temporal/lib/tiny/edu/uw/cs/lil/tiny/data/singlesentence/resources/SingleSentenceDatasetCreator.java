package edu.uw.cs.lil.tiny.data.singlesentence.resources;

import java.io.File;

import edu.uw.cs.lil.tiny.data.singlesentence.SingleSentenceDataset;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.utils.string.StubStringFilter;

/**
 * Creator for {@link SingleSentenceDataset}.
 * 
 * @author Yoav Artzi
 */
public class SingleSentenceDatasetCreator implements
		IResourceObjectCreator<SingleSentenceDataset> {
	
	@Override
	public SingleSentenceDataset create(Parameters parameters,
			IResourceRepository resourceRepo) {
		return SingleSentenceDataset.read(
				new File(parameters.get("file")), new StubStringFilter(),
				true);
	}
	
	@Override
	public String resourceTypeName() {
		return "data.single";
	}
	
}
