package edu.uw.cs.lil.tiny.parser.ccg.model;

/**
 * Takes a model and initializes its weights.
 * 
 * @author Yoav Artzi
 */
public interface IModelInit<X, Y> {
	
	void init(Model<X, Y> model);
	
}
