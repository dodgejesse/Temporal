package edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.scorers;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.FactoredLexicon;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.LexicalTemplate;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.utils.collections.IScorer;

public class LexicalEntryLexicalTemplateBasedScorer implements
		IScorer<LexicalEntry<LogicalExpression>> {
	
	private final IScorer<LexicalTemplate>	templateScorer;
	
	public LexicalEntryLexicalTemplateBasedScorer(
			IScorer<LexicalTemplate> templateScorer) {
		this.templateScorer = templateScorer;
	}
	
	@Override
	public double score(LexicalEntry<LogicalExpression> e) {
		return templateScorer.score(FactoredLexicon.factor(e).getTemplate());
	}
	
}
