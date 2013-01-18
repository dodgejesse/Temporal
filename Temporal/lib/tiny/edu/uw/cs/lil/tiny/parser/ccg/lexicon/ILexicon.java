package edu.uw.cs.lil.tiny.parser.ccg.lexicon;

import java.io.File;
import java.util.Collection;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.utils.string.IStringFilter;

/**
 * Lexicon containing a collection of lexical entries that match textual tokens.
 * 
 * @author Luke Zettlemoyer
 */
public interface ILexicon<Y> extends ILexiconImmutable<Y> {
	
	boolean add(LexicalEntry<Y> lex);
	
	boolean addAll(Collection<LexicalEntry<Y>> entries);
	
	boolean addAll(ILexicon<Y> lexicon);
	
	void addEntriesFromFile(File file, IStringFilter textFilter,
			ICategoryServices<Y> categoryServices, String origin);
	
	boolean retainAll(Collection<LexicalEntry<Y>> entries);
	
	boolean retainAll(ILexicon<Y> entries);
	
}
