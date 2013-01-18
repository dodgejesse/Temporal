package edu.uw.cs.lil.tiny.parser.ccg.cky.chart;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;
import edu.uw.cs.utils.filter.IFilter;

/**
 * Factory for {@link Cell} objects.
 * 
 * @author Yoav Artzi
 */
public class CellFactory<Y> extends AbstractCellFactory<Y> {
	
	public CellFactory(IDataItemModel<Y> model, int sentenceSize,
			IFilter<Category<Y>> completeParseFilter) {
		super(model, sentenceSize, completeParseFilter);
	}
	
	@Override
	protected Cell<Y> doCreate(Category<Y> category, Cell<Y> child,
			boolean completeSpan, boolean fullParse, String ruleName) {
		return new Cell<Y>(category, ruleName, child, completeSpan, fullParse);
	}
	
	@Override
	protected Cell<Y> doCreate(Category<Y> category, Cell<Y> leftChild,
			Cell<Y> rightChild, boolean completeSpan, boolean fullParse,
			String ruleName) {
		return new Cell<Y>(category, ruleName, leftChild, rightChild,
				completeSpan, fullParse);
	}
	
	@Override
	protected Cell<Y> doCreate(LexicalEntry<Y> lexicalEntry, int begin,
			int end, boolean completeSpan, boolean fullParse) {
		return new Cell<Y>(lexicalEntry, begin, end, completeSpan, fullParse);
	}
}
