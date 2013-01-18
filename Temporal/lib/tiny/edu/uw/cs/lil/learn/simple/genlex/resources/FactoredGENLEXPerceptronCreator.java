package edu.uw.cs.lil.learn.simple.genlex.resources;

import edu.uw.cs.lil.learn.simple.genlex.FactoredGENLEXPerceptron;
import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.IParser;
import edu.uw.cs.lil.tiny.test.Tester;

public class FactoredGENLEXPerceptronCreator implements
		IResourceObjectCreator<FactoredGENLEXPerceptron> {
	
	private static final String	DEFAULT_NAME	= "learn.simple.genlex";
	private final String		resourceName;
	
	public FactoredGENLEXPerceptronCreator() {
		this(DEFAULT_NAME);
	}
	
	public FactoredGENLEXPerceptronCreator(String resourceName) {
		this.resourceName = resourceName;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FactoredGENLEXPerceptron create(Parameters params,
			IResourceRepository repo) {
		
		return new FactoredGENLEXPerceptron(
				Integer.parseInt(params.get("iter")),
				Integer.parseInt(params.get("trainingMaxSentenceLength")),
				(Tester<Sentence, LogicalExpression>) repo.getResource(params
						.get("tester")),
				(IDataCollection<? extends ILabeledDataItem<Sentence, LogicalExpression>>) repo
						.getResource(params.get("data")),
				(IParser<Sentence, LogicalExpression>) repo
						.getResource("parser"));
	}
	
	@Override
	public String resourceTypeName() {
		return resourceName;
	}
	
}
