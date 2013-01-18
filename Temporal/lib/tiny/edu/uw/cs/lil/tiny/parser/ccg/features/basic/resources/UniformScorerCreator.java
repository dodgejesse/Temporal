package edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.UniformScorer;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;

/**
 * Creator for {@link UniformScorer}.
 * 
 * @author Yoav Artzi
 */
public class UniformScorerCreator implements
		IResourceObjectCreator<UniformScorer<?>> {
	
	@Override
	public UniformScorer<?> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		final String c = parameters.get("class");
		final Double weight = Double.valueOf(parameters.get("weight"));
		if ("lexEntry".equals(c)) {
			return new UniformScorer<LexicalEntry<LogicalExpression>>(weight);
		} else {
			throw new IllegalArgumentException(
					"Invalid class for uniform scorer: " + c);
		}
	}
	
	@Override
	public String resourceTypeName() {
		return "scorer.uniform";
	}
	
}
