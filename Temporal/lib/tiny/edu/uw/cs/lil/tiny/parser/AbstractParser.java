package edu.uw.cs.lil.tiny.parser;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;

public abstract class AbstractParser<X, Y> implements IParser<X, Y> {
	@Override
	public IParserOutput<Y> parse(IDataItem<X> dataItem, IDataItemModel<Y> model) {
		return parse(dataItem, model, false);
	}
	
	@Override
	public IParserOutput<Y> parse(IDataItem<X> dataItem,
			IDataItemModel<Y> model, boolean allowWordSkipping) {
		return parse(dataItem, model, allowWordSkipping, null);
	}
	
	@Override
	public IParserOutput<Y> parse(IDataItem<X> dataItem,
			IDataItemModel<Y> model, boolean allowWordSkipping,
			ILexicon<Y> tempLexicon) {
		return parse(dataItem, model, allowWordSkipping, tempLexicon, null);
	}
	
	@Override
	public IParserOutput<Y> parse(IDataItem<X> dataItem,
			IDataItemModel<Y> model, boolean allowWordSkipping,
			ILexicon<Y> tempLexicon, Integer beamSize) {
		return parse(dataItem, null, model, allowWordSkipping, tempLexicon,
				beamSize);
	}
	
	@Override
	public IParserOutput<Y> parse(IDataItem<X> dataItem, Pruner<X, Y> pruner,
			IDataItemModel<Y> model) {
		return parse(dataItem, pruner, model, false);
	}
	
	@Override
	public IParserOutput<Y> parse(IDataItem<X> dataItem, Pruner<X, Y> pruner,
			IDataItemModel<Y> model, boolean allowWordSkipping) {
		return parse(dataItem, pruner, model, allowWordSkipping, null);
	}
	
	@Override
	public IParserOutput<Y> parse(IDataItem<X> dataItem, Pruner<X, Y> pruner,
			IDataItemModel<Y> model, boolean allowWordSkipping,
			ILexicon<Y> tempLexicon) {
		return parse(dataItem, pruner, model, allowWordSkipping, tempLexicon,
				null);
	}
}
