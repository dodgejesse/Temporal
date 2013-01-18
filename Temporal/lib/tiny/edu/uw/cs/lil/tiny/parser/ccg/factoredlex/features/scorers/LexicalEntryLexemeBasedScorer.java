package edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.scorers;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.FactoredLexicon;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.Lexeme;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.utils.collections.IScorer;

public class LexicalEntryLexemeBasedScorer implements
		IScorer<LexicalEntry<LogicalExpression>> {
	
	private final IScorer<Lexeme>	lexemeScorer;
	
	public LexicalEntryLexemeBasedScorer(IScorer<Lexeme> lexemeScorer) {
		this.lexemeScorer = lexemeScorer;
	}
	
	@Override
	public double score(LexicalEntry<LogicalExpression> e) {
		return lexemeScorer.score(FactoredLexicon.factor(e).getLexeme());
	}
	
}
