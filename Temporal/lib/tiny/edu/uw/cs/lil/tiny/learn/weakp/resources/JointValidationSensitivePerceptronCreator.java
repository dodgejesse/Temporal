package edu.uw.cs.lil.tiny.learn.weakp.resources;

import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.lexicalgen.ILexicalGenerationLabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.utils.IValidator;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.learn.weakp.validation.JointValidationSensitivePerceptron;
import edu.uw.cs.lil.tiny.learn.weakp.validation.JointValidationSensitivePerceptron.Builder;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutputLogger;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.utils.composites.Pair;

public class JointValidationSensitivePerceptronCreator<W, Y, Z> implements
		IResourceObjectCreator<JointValidationSensitivePerceptron<W, Y, Z>> {
	
	private final String	name;
	
	public JointValidationSensitivePerceptronCreator() {
		this("learner.weakp.valid.joint");
	}
	
	public JointValidationSensitivePerceptronCreator(String name) {
		this.name = name;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JointValidationSensitivePerceptron<W, Y, Z> create(
			Parameters params, IResourceRepository repo) {
		
		final IDataCollection<? extends ILexicalGenerationLabeledDataItem<Pair<Sentence, W>, Y, Z>> trainingData = repo
				.getResource(params.get("data"));
		
		final Builder<W, Y, Z> builder = new JointValidationSensitivePerceptron.Builder<W, Y, Z>(
				trainingData,
				(IJointParser<Sentence, W, Y, Z>) repo
						.getResource(ParameterizedExperiment.PARSER_RESOURCE),
				(IValidator<Pair<Sentence, W>, Pair<Y, Z>>) repo
						.getResource(params.get("validator")));
		
		if ("true".equals(params.get("hard"))) {
			builder.setHardUpdates(true);
		}
		
		if (params.contains("parseLogger")) {
			builder.setParserOutputLogger((IJointOutputLogger<Y, Z>) repo
					.getResource(params.get("parseLogger")));
		}
		
		if ("false".equals(params.get("lexiconlearn"))) {
			builder.setLexiconLearning(false);
		}
		
		if (params.contains("genlexbeam")) {
			builder.setLexiconGenerationBeamSize(Integer.valueOf(params
					.get("genlexbeam")));
		}
		
		if (params.contains("margin")) {
			builder.setMargin(Double.valueOf(params.get("margin")));
		}
		
		if (params.contains("maxSentenceLength")) {
			builder.setMaxSentenceLength(Integer.valueOf(params
					.get("maxSentenceLength")));
		}
		
		if (params.contains("iter")) {
			builder.setNumTrainingIterations(Integer.valueOf(params.get("iter")));
		}
		
		return builder.build();
	}
	
	@Override
	public String resourceTypeName() {
		return name;
	}
	
}
