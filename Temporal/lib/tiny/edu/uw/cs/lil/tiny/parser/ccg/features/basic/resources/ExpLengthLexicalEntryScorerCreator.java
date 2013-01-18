package edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.ExpLengthLexicalEntryScorer;

public class ExpLengthLexicalEntryScorerCreator<Y> implements
		IResourceObjectCreator<ExpLengthLexicalEntryScorer<Y>> {
	
	@Override
	public ExpLengthLexicalEntryScorer<Y> create(Parameters params,
			IResourceRepository repo) {
		return new ExpLengthLexicalEntryScorer<Y>(Double.valueOf(params
				.get("coef")), Double.valueOf(params.get("exp")));
	}
	
	@Override
	public String resourceTypeName() {
		return "scorer.lenexp";
	}
	
}
