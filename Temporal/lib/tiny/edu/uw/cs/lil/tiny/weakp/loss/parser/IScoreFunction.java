package edu.uw.cs.lil.tiny.weakp.loss.parser;

/**
 * External scoring function for parser output. The higher the score is the
 * better the label is. The score doesn't have to be normalized. The score is
 * positive with the lowest possible score being 0.0
 * 
 * @author Yoav Artzi
 * @param <Y>
 *            Type of parser output.
 */
public interface IScoreFunction<Y> {
	double score(Y label);
}
