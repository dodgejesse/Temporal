package edu.uw.cs.lil.tiny.parser.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.model.init.LexiconModelInit;

public class LexiconModelInitCreator<X, Y> implements
		IResourceObjectCreator<LexiconModelInit<X, Y>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public LexiconModelInit<X, Y> create(Parameters params,
			IResourceRepository repo) {
		return new LexiconModelInit<X, Y>((ILexicon<Y>) repo.getResource(params
				.get("lexicon")), "true".equals(params.get("fixed")));
	}
	
	@Override
	public String resourceTypeName() {
		return "init.lex";
	}
	
}
