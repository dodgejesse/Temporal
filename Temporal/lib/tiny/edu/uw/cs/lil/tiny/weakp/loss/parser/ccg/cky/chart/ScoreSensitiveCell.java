package edu.uw.cs.lil.tiny.weakp.loss.parser.ccg.cky.chart;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.parser.ccg.cky.chart.Cell;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.weakp.loss.parser.IScoreFunction;

public class ScoreSensitiveCell<Y> extends Cell<Y> {
	private final boolean			calculatedScore	= false;
	private double					score;
	private final boolean			scoreIsPrimary;
	private final IScoreFunction<Y>	scoringFunction;
	
	ScoreSensitiveCell(Category<Y> category, String ruleName,
			Cell<Y> leftChild, Cell<Y> rightChild,
			IScoreFunction<Y> scoringFunction, boolean scoreIsPrimary,
			boolean isCompleteSpan, boolean isFullParse) {
		super(category, ruleName, leftChild, rightChild, isCompleteSpan,
				isFullParse);
		this.scoringFunction = scoringFunction;
		this.scoreIsPrimary = scoreIsPrimary;
	}
	
	ScoreSensitiveCell(Category<Y> category, String ruleName, Cell<Y> child,
			IScoreFunction<Y> scoringFunction, boolean scoreIsPrimary,
			boolean isCompleteSpan, boolean isFullParse) {
		super(category, ruleName, child, isCompleteSpan, isFullParse);
		this.scoringFunction = scoringFunction;
		this.scoreIsPrimary = scoreIsPrimary;
	}
	
	ScoreSensitiveCell(LexicalEntry<Y> lexicalEntry, int begin, int end,
			IScoreFunction<Y> scoringFunction, boolean scoreIsPrimary,
			boolean isCompleteSpan, boolean isFullParse) {
		super(lexicalEntry, begin, end, isCompleteSpan, isFullParse);
		this.scoringFunction = scoringFunction;
		this.scoreIsPrimary = scoreIsPrimary;
	}
	
	@Override
	public double getPruneScore() {
		if (scoreIsPrimary) {
			return calculateScore();
		} else {
			return super.getPruneScore();
		}
	}
	
	@Override
	public double getSecondPruneScore() {
		if (scoreIsPrimary) {
			return super.getPruneScore();
		} else {
			return calculateScore();
		}
	}
	
	private double calculateScore() {
		if (!calculatedScore) {
			score = getCategroy().getSem() == null ? 0.0 : scoringFunction
					.score(getCategroy().getSem());
		}
		return score;
	}
}
