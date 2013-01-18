package edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.Lexeme;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.scorers.LexicalEntryLexemeBasedScorer;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.AbstractScaledScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.utils.collections.IScorer;

public class LexicalEntryLexemeBasedScorerCreator
		extends
		AbstractScaledScorerCreator<LexicalEntry<LogicalExpression>, LexicalEntryLexemeBasedScorer> {
	
	@SuppressWarnings("unchecked")
	@Override
	public LexicalEntryLexemeBasedScorer createScorer(Parameters parameters,
			IResourceRepository resourceRepo) {
		return new LexicalEntryLexemeBasedScorer(
				(IScorer<Lexeme>) resourceRepo.getResource(parameters
						.get("baseScorer")));
	}
	
	@Override
	public String resourceTypeName() {
		return "scorer.lex.lexemebased";
	}
	
}
