package edu.uw.cs.lil.tiny.data.lexicalgen;

import edu.uw.cs.lil.tiny.data.ILossDataItem;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;

/**
 * @author Yoav Artzi
 * @param <X>
 *            Sample (input) type
 * @param <Y>
 *            Semantics type
 * @param <Z>
 *            Label type
 */
public interface ILexicalGenerationLossDataItem<X, Y, Z> extends
		ILossDataItem<X, Z> {
	ILexicon<Y> generateLexicon();
}
