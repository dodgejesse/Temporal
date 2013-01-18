package edu.uw.cs.lil.tiny.parser.joint;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.parser.IParser;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.utils.composites.Pair;

/**
 * @author Yoav Artzi
 * @param <X>
 *            Type of the natural language structure (e.g., Sentence).
 * @param <W>
 *            Type of added input information (e.g., task, starting position,
 *            state of the world etc.).
 * @param <Y>
 *            Type of meaning representation (e.g., LogicalExpression).
 * @param <Z>
 *            Type of execution output.
 */
public interface IJointParser<X extends IDataItem<X>, W, Y, Z> extends
		IParser<X, Y> {
	
	IJointOutput<Y, Z> parse(IDataItem<Pair<X, W>> dataItem,
			IJointDataItemModel<Y, Z> model);
	
	IJointOutput<Y, Z> parse(IDataItem<Pair<X, W>> dataItem,
			IJointDataItemModel<Y, Z> model, boolean allowWordSkipping);
	
	IJointOutput<Y, Z> parse(IDataItem<Pair<X, W>> dataItem,
			IJointDataItemModel<Y, Z> model, boolean allowWordSkipping,
			ILexicon<Y> tempLexicon);
	
	IJointOutput<Y, Z> parse(IDataItem<Pair<X, W>> dataItem,
			IJointDataItemModel<Y, Z> model, boolean allowWordSkipping,
			ILexicon<Y> tempLexicon, Integer beamSize);
	
}
