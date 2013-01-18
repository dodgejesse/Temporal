package edu.uw.cs.lil.tiny.parser.ccg.lexicon;

import java.util.Collection;
import java.util.List;

import edu.uw.cs.lil.tiny.parser.ccg.model.Model;

public interface ILexiconImmutable<Y> {
	
	public List<? extends LexicalEntry<Y>> getLexEntries(List<String> words);
	
	boolean contains(LexicalEntry<Y> lex);
	
	ILexicon<Y> copy();
	
	int size();
	
	Collection<LexicalEntry<Y>> toCollection();
	
	String toString(Model<?, Y> model);
}
