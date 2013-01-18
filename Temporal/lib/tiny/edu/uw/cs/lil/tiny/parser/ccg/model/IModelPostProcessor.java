package edu.uw.cs.lil.tiny.parser.ccg.model;

/**
 * Given a model, post processes it.
 * 
 * @author Yoav Artzi
 */
public interface IModelPostProcessor<X, Y> {
	/**
	 * Process the given mode. Modify it if needed.
	 * 
	 * @param model
	 */
	void process(Model<X, Y> model);
}
