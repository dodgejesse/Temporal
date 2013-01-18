package edu.uw.cs.lil.tiny.parser.ccg.cky.genlex.exact;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.cky.chart.Cell;
import edu.uw.cs.lil.tiny.parser.ccg.cky.genlex.IMarkedEntriesCounter;
import edu.uw.cs.lil.tiny.parser.ccg.genlex.ILexiconGenerator;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;

public class ExactMarkedCell extends Cell<LogicalExpression> implements
		IMarkedEntriesCounter {
	
	private final int	numMarkedLexicalEntries;
	
	protected ExactMarkedCell(Category<LogicalExpression> category,
			String ruleName, Cell<LogicalExpression> child,
			boolean isCompleteSpan, boolean isFullParse,
			int numMarkedLexicalEntries) {
		super(category, ruleName, child, isCompleteSpan, isFullParse);
		this.numMarkedLexicalEntries = numMarkedLexicalEntries;
	}
	
	protected ExactMarkedCell(Category<LogicalExpression> category,
			String ruleName, Cell<LogicalExpression> leftChild,
			Cell<LogicalExpression> rightChild, boolean isCompleteSpan,
			boolean isFullParse, int numMarkedLexicalEntries) {
		super(category, ruleName, leftChild, rightChild, isCompleteSpan,
				isFullParse);
		this.numMarkedLexicalEntries = numMarkedLexicalEntries;
	}
	
	protected ExactMarkedCell(LexicalEntry<LogicalExpression> lexicalEntry,
			int begin, int end, boolean isCompleteSpan, boolean isFullParse) {
		super(lexicalEntry, begin, end, isCompleteSpan, isFullParse);
		this.numMarkedLexicalEntries = lexicalEntry.getOrigin().equals(
				ILexiconGenerator.GENLEX_LEXICAL_ORIGIN) ? 1 : 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ExactMarkedCell other = (ExactMarkedCell) obj;
		if (numMarkedLexicalEntries != other.numMarkedLexicalEntries) {
			return false;
		}
		return true;
	}
	
	@Override
	public int getNumMarkedLexicalEntries() {
		return numMarkedLexicalEntries;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + numMarkedLexicalEntries;
		return result;
	}
	
	@Override
	public String toString(boolean recursive, String tokens) {
		return super.toString(recursive, tokens)
				+ String.format("{%d}", numMarkedLexicalEntries);
	}
	
}
