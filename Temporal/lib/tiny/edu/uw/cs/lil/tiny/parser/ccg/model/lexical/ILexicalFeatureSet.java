package edu.uw.cs.lil.tiny.parser.ccg.model.lexical;

import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.parse.IParseFeatureSet;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;

/**
 * Lexical feature set.
 * 
 * @author Yoav Artzi
 */
public interface ILexicalFeatureSet<X, Y> extends IParseFeatureSet<X, Y> {
	/**
	 * Add an initialize a lexical entry.
	 * 
	 * @param entry
	 * @param prametersVector
	 * @return
	 */
	public boolean addEntry(LexicalEntry<Y> entry, IHashVector prametersVector);
	
	/**
	 * Add and initialize a fixed lexical entry.
	 * 
	 * @param entry
	 * @param prametersVector
	 * @return
	 */
	public boolean addFixedEntry(LexicalEntry<Y> entry,
			IHashVector prametersVector);
}
