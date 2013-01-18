package edu.uw.cs.lil.tiny.test.exec.resources;

import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.test.exec.ExecTester;
import edu.uw.cs.lil.tiny.test.exec.ExecTester.Builder;
import edu.uw.cs.utils.filter.IFilter;

public class ExecTesterCreator<X, Z> implements
		IResourceObjectCreator<ExecTester<X, Z>> {
	private static final String	DEFAULT_NAME	= "tester.exec";
	private final String		resourceName;
	
	public ExecTesterCreator() {
		this(DEFAULT_NAME);
	}
	
	public ExecTesterCreator(String resourceName) {
		this.resourceName = resourceName;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ExecTester<X, Z> create(Parameters params, IResourceRepository repo) {
		final Builder<X, Z> builder = new ExecTester.Builder<X, Z>();
		
		if (params.contains("sloppyFilter")) {
			builder.setSkipParsingFilter((IFilter<ILabeledDataItem<X, Z>>) repo
					.getResource(params.get("sloppyFilter")));
		}
		
		return builder.build();
	}
	
	@Override
	public String resourceTypeName() {
		return resourceName;
	}
	
}
