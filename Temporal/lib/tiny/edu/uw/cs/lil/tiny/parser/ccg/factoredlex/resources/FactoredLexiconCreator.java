package edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.FactoredLexicon;

public class FactoredLexiconCreator implements
		IResourceObjectCreator<FactoredLexicon> {
	
	@Override
	public FactoredLexicon create(Parameters parameters,
			IResourceRepository resourceRepo) {
		return new FactoredLexicon();
	}
	
	@Override
	public String resourceTypeName() {
		return "lexicon.factored";
	}
	
}
