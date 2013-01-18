package edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources;

import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.OriginLexicalEntryScorer;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.utils.collections.IScorer;

public class OriginLexicalEntryScorerCreator<Y> implements
		IResourceObjectCreator<OriginLexicalEntryScorer<Y>> {
	
	@Override
	public OriginLexicalEntryScorer<Y> create(Parameters params,
			final IResourceRepository repo) {
		final IScorer<LexicalEntry<Y>> defaultScorer = repo.getResource(params
				.get("default"));
		
		final Map<String, IScorer<LexicalEntry<Y>>> originScorers = new HashMap<String, IScorer<LexicalEntry<Y>>>();
		
		if (params.contains("scorers")) {
			for (final String entry : params.getSplit("scorers")) {
				final String[] split = entry.split(":", 2);
				final String origin = split[0];
				final IScorer<LexicalEntry<Y>> scorer = repo
						.getResource(split[1]);
				originScorers.put(origin, scorer);
			}
		}
		
		return new OriginLexicalEntryScorer<Y>(originScorers, defaultScorer);
	}
	
	@Override
	public String resourceTypeName() {
		return "scorer.lex.origin";
	}
	
}
