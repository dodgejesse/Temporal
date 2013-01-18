package edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.scorers.ExpLengthLexemeScorer;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.ExpLengthLexicalEntryScorer;

/**
 * Creator for {@link ExpLengthLexicalEntryScorer}.
 * 
 * @author Luke Zettlemoyer
 */
public class ExpLengthLexemeScorerCreator implements
		IResourceObjectCreator<ExpLengthLexemeScorer> {
	
	@Override
	public ExpLengthLexemeScorer create(Parameters parameters,
			IResourceRepository resourceRepo) {
		final Double base = Double.valueOf(parameters.get("base"));
		final Double exp = Double.valueOf(parameters.get("exponent"));
		return new ExpLengthLexemeScorer(base, exp);
	}
	
	@Override
	public String resourceTypeName() {
		return "scorer.lexeme.explength";
	}
	
}
