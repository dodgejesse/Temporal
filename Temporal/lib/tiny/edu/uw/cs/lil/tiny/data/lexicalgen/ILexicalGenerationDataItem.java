package edu.uw.cs.lil.tiny.data.lexicalgen;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;

/**
 * @author Yoav Artzi
 * @param <X>
 *            Sample (input) type
 * @param <Y>
 *            Semantics type
 */
public interface ILexicalGenerationDataItem<X, Y> extends IDataItem<X> {
	ILexicon<Y> generateLexicon();
}
