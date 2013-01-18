package edu.uw.cs.lil.tiny.parser.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.Lexicon;

public class LexiconCreator<Y> implements IResourceObjectCreator<Lexicon<Y>> {
	
	@Override
	public Lexicon<Y> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		return new Lexicon<Y>();
	}
	
	@Override
	public String resourceTypeName() {
		return "lexicon";
	}
	
}
