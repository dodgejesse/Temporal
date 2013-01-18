package edu.uw.cs.lil.tiny.parser.ccg.model.lexical;

import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;

/**
 * Lexical feature set that is independent of the data item.
 * 
 * @author Yoav Artzi
 */
public interface IIndependentLexicalFeatureSet<X, Y> extends
		ILexicalFeatureSet<X, Y> {
	double score(LexicalEntry<Y> lexicalEntry, IHashVector theta);
	
	void setFeats(LexicalEntry<Y> lexicalEntry, IHashVector features);
	
}
