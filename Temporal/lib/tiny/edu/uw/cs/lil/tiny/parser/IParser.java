package edu.uw.cs.lil.tiny.parser;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;

/**
 * A parser for sentences {@link Sentence}.
 * 
 * @author Yoav Artzi
 * @param <X>
 *            The representation of the input
 * @param <Y>
 *            The representation of the output of the parse.
 */
public interface IParser<X, Y> {
	
	IParserOutput<Y> parse(IDataItem<X> dataItem, IDataItemModel<Y> model);
	
	IParserOutput<Y> parse(IDataItem<X> dataItem, IDataItemModel<Y> model,
			boolean allowWordSkipping);
	
	IParserOutput<Y> parse(IDataItem<X> dataItem, IDataItemModel<Y> model,
			boolean allowWordSkipping, ILexicon<Y> tempLexicon);
	
	IParserOutput<Y> parse(IDataItem<X> dataItem, IDataItemModel<Y> model,
			boolean allowWordSkipping, ILexicon<Y> tempLexicon, Integer beamSize);
	
	IParserOutput<Y> parse(IDataItem<X> dataItem, Pruner<X, Y> pruner,
			IDataItemModel<Y> model);
	
	IParserOutput<Y> parse(IDataItem<X> dataItem, Pruner<X, Y> pruner,
			IDataItemModel<Y> model, boolean allowWordSkipping);
	
	IParserOutput<Y> parse(IDataItem<X> dataItem, Pruner<X, Y> pruner,
			IDataItemModel<Y> model, boolean allowWordSkipping,
			ILexicon<Y> tempLexicon);
	
	IParserOutput<Y> parse(IDataItem<X> dataItem, Pruner<X, Y> pruner,
			IDataItemModel<Y> model, boolean allowWordSkipping,
			ILexicon<Y> tempLexicon, Integer beamSize);
	
}
