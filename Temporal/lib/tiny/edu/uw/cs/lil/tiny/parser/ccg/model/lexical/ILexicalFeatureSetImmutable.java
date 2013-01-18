package edu.uw.cs.lil.tiny.parser.ccg.model.lexical;

import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.parse.IParseFeatureSetImmutable;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;

public interface ILexicalFeatureSetImmutable<X, Y> extends
		IParseFeatureSetImmutable<X, Y> {
	public double score(LexicalEntry<Y> obj, IHashVector theta);
	
	public void setFeats(LexicalEntry<Y> obj, IHashVector feats);
	
}
