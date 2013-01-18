package edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.Lexeme;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.LexicalTemplate;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.UniformScorer;

/**
 * Creator for {@link UniformScorer}.
 * 
 * @author Yoav Artzi
 */
public class FactoredUniformScorerCreator implements
		IResourceObjectCreator<UniformScorer<?>> {
	
	@Override
	public UniformScorer<?> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		final String c = parameters.get("class");
		final Double weight = Double.valueOf(parameters.get("weight"));
		if ("lexeme".equals(c)) {
			return new UniformScorer<Lexeme>(weight);
		} else if ("lexTemplate".equals(c)) {
			return new UniformScorer<LexicalTemplate>(weight);
		} else {
			throw new IllegalArgumentException(
					"Invalid class for factored uniform scorer: " + c);
		}
	}
	
	@Override
	public String resourceTypeName() {
		return "scorer.uniform.factored";
	}
	
}
