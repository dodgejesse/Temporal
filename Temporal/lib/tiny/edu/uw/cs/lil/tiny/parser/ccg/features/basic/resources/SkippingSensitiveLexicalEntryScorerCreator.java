package edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.SkippingSensitiveLexicalEntryScorer;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.utils.collections.IScorer;

public class SkippingSensitiveLexicalEntryScorerCreator<Y>
		extends
		AbstractScaledScorerCreator<LexicalEntry<Y>, SkippingSensitiveLexicalEntryScorer<Y>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public SkippingSensitiveLexicalEntryScorer<Y> createScorer(
			Parameters parameters, IResourceRepository resourceRepo) {
		return new SkippingSensitiveLexicalEntryScorer<Y>(
				(ICategoryServices<Y>) resourceRepo
						.getResource("categoryServices"),
				Double.valueOf(parameters.get("cost")),
				(IScorer<LexicalEntry<Y>>) resourceRepo.getResource(parameters
						.get("baseScorer")));
	}
	
	@Override
	public String resourceTypeName() {
		return "scorer.lex.skipping";
	}
	
}
