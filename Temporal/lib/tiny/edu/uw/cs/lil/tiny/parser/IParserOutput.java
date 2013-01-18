package edu.uw.cs.lil.tiny.parser;

import java.util.List;

import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;

/**
 * Parser for sentences {@link Sentence}.
 * 
 * @author Yoav Artzi
 * @param <Y>
 *            The representation of the parser output
 * @see IParser
 */
public interface IParserOutput<Y> {
	
	/**
	 * Get all complete parses.
	 * 
	 * @return
	 */
	List<IParseResult<Y>> getAllParses();
	
	List<IParseResult<Y>> getBestParses();
	
	List<LexicalEntry<Y>> getMaxLexicalEntries(Y label);
	
	List<IParseResult<Y>> getMaxParses(Y label);
	
	long getParsingTime();
}
