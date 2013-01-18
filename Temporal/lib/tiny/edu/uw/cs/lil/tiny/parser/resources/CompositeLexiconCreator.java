package edu.uw.cs.lil.tiny.parser.resources;

import java.util.ArrayList;
import java.util.List;

import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.CompositeLexicon;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;

public class CompositeLexiconCreator<Y> implements
		IResourceObjectCreator<CompositeLexicon<Y>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public CompositeLexicon<Y> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		
		final ILexicon<Y> master = (ILexicon<Y>) resourceRepo
				.getResource(parameters.get("masterLexicon"));
		
		final List<String> otherLexicons = parameters.getSplit("otherLexicons");
		final List<ILexicon<Y>> subLexicons = new ArrayList<ILexicon<Y>>();
		for (final String lexName : otherLexicons) {
			subLexicons.add((ILexicon<Y>) resourceRepo.getResource(lexName));
		}
		
		return new CompositeLexicon<Y>(master, subLexicons);
	}
	
	@Override
	public String resourceTypeName() {
		return "lexicon.composite";
	}
	
}
