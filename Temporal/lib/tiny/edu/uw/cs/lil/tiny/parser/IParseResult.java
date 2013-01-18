package edu.uw.cs.lil.tiny.parser;

import java.util.LinkedHashSet;

import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;

/**
 * Single parse result.
 * 
 * @author Yoav Artzi
 * @param <Y>
 *            Representation of the parser output.
 * @see IParser
 * @see IParserOutput
 */
public interface IParseResult<Y> {
	LinkedHashSet<LexicalEntry<Y>> getAllLexicalEntries();
	
	IHashVector getAverageMaxFeatureVector();
	
	LinkedHashSet<LexicalEntry<Y>> getMaxLexicalEntries();
	
	LinkedHashSet<RuleUsageTriplet> getMaxRulesUsed();
	
	double getScore();
	
	Y getY();
}
