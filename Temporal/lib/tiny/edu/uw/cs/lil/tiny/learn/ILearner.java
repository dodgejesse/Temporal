package edu.uw.cs.lil.tiny.learn;

import edu.uw.cs.lil.tiny.parser.ccg.model.Model;

/**
 * @author Yoav Artzi
 */
public interface ILearner<X, Z, M extends Model<X, Z>> {
	void train(M model);
}
