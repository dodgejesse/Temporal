package edu.uw.cs.lil.tiny.learn.ubl;

import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.FactoredLexicon;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;

/**
 * General services that UBL requires, but are also used in other places.
 * 
 * @author Yoav Artzi
 */
public class UBLServices {
	
	public static Set<LexicalEntry<LogicalExpression>> createSentenceLexicalEntries(
			final IDataCollection<? extends ILabeledDataItem<Sentence, LogicalExpression>> data,
			ICategoryServices<LogicalExpression> categoryServices) {
		final Set<LexicalEntry<LogicalExpression>> result = new HashSet<LexicalEntry<LogicalExpression>>();
		for (final ILabeledDataItem<Sentence, LogicalExpression> dataItem : data) {
			result.add(createSentenceLexicalEntry(dataItem, categoryServices));
		}
		return result;
	}
	
	public static LexicalEntry<LogicalExpression> createSentenceLexicalEntry(
			final ILabeledDataItem<Sentence, LogicalExpression> dataItem,
			ICategoryServices<LogicalExpression> categoryServices) {
		final LexicalEntry<LogicalExpression> l = new LexicalEntry<LogicalExpression>(
				dataItem.getSample().getTokens(), categoryServices
						.getSentenceCategory().cloneWithNewSemantics(
								dataItem.getLabel()),
				AbstractUBL.SPLITTING_LEXICAL_ORIGIN);
		return FactoredLexicon.factor(l);
	}
	
}
