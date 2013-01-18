package edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources;

import java.io.File;
import java.io.IOException;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.Lexeme;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.scorers.LexemeCooccurrenceScorer;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.AbstractScaledScorerCreator;

public class LexemeCooccurrenceScorerCreator extends
		AbstractScaledScorerCreator<Lexeme, LexemeCooccurrenceScorer> {
	
	@Override
	public LexemeCooccurrenceScorer createScorer(Parameters parameters,
			IResourceRepository resourceRepo) {
		final File file = new File(parameters.get("file"));
		try {
			return new LexemeCooccurrenceScorer(file);
		} catch (final IOException e) {
			throw new IllegalStateException(
					"Failed to load lexical cooccurrence scorer from: " + file);
		}
	}
	
	@Override
	public String resourceTypeName() {
		return "scorer.lexeme.cooc";
	}
	
}
