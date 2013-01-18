package edu.uw.cs.lil.tiny.parser.ccg.cky.chart;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;
import edu.uw.cs.utils.filter.IFilter;

/**
 * Factory for {@link Chart} cells.
 * 
 * @author Yoav Artzi
 */
public abstract class AbstractCellFactory<Y> {
	
	private final IFilter<Category<Y>>	completeParseFilter;
	private final int					sentenceSize;
	protected final IDataItemModel<Y>	model;
	
	public AbstractCellFactory(IDataItemModel<Y> model, int sentenceSize,
			IFilter<Category<Y>> completeParseFilter) {
		this.model = model;
		this.sentenceSize = sentenceSize;
		this.completeParseFilter = completeParseFilter;
	}
	
	public final Cell<Y> create(Category<Y> category, Cell<Y> leftChild,
			Cell<Y> rightChild, String ruleName) {
		return score(doCreate(
				category,
				leftChild,
				rightChild,
				isCompleteSpan(leftChild.getStart(), rightChild.getEnd()),
				isFullParse(leftChild.getStart(), rightChild.getEnd(), category),
				ruleName));
	}
	
	public final Cell<Y> create(Category<Y> category, Cell<Y> child,
			String ruleName) {
		return score(doCreate(category, child,
				isCompleteSpan(child.getStart(), child.getEnd()),
				isFullParse(child.getStart(), child.getEnd(), category),
				ruleName));
	}
	
	public final Cell<Y> create(LexicalEntry<Y> lexicalEntry, int begin, int end) {
		return score(doCreate(lexicalEntry, begin, end,
				isCompleteSpan(begin, end),
				isFullParse(begin, end, lexicalEntry.getCategory())));
	}
	
	private boolean isCompleteSpan(int begin, int end) {
		return begin == 0 && end == sentenceSize - 1;
	}
	
	private boolean isFullParse(int begin, int end, Category<Y> category) {
		return isCompleteSpan(begin, end)
				&& completeParseFilter.isValid(category);
	}
	
	private final Cell<Y> score(Cell<Y> cell) {
		cell.score(model);
		return cell;
	}
	
	protected abstract Cell<Y> doCreate(Category<Y> category, Cell<Y> child,
			boolean completeSpan, boolean fullParse, String ruleName);
	
	protected abstract Cell<Y> doCreate(Category<Y> category,
			Cell<Y> leftChild, Cell<Y> rightChild, boolean completeSpan,
			boolean fullParse, String ruleName);
	
	protected abstract Cell<Y> doCreate(LexicalEntry<Y> lexicalEntry,
			int begin, int end, boolean completeSpan, boolean fullParse);
}
