package edu.uw.cs.lil.tiny.parser.ccg.lexicon;

import java.util.Set;


/**
 * Generate lexical items from evidence.
 * 
 * @author Yoav Artzi
 * @param <X>
 *            Type of sample
 * @param <E>
 *            Evidence object
 * @param <Y>
 *            Type of semantics
 */
public interface IEvidenceLexicalGenerator<X, Y, E> {
	
	/**
	 * Given an object representing an evidence, will generate lexical items
	 * from it and return them packed in a new Lexicon object.
	 * 
	 * @param sample
	 * @param evidence
	 * @return
	 */
	Set<LexicalEntry<Y>> generateLexicon(X sample, E evidence);
}
