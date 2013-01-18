package edu.uw.cs.lil.tiny.parser.ccg;

import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;

/**
 * @author Yoav Artzi
 * @param <Y>
 */
public interface ILexicalParseStep<Y> extends IParseStep<Y> {
	public static final String	LEXICAL_DERIVATION_STEP_RULENAME	= "lex";
	
	LexicalEntry<Y> getLexicalEntry();
	
}
