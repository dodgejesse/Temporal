package edu.uw.cs.lil.tiny.parser.joint;

import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;

/**
 * Wraps an execution result to abstract the model signature from the joint
 * output.
 * 
 * @author Yoav Artzi
 * @param <ERESULT>
 */
public interface IExecResultWrapper<ERESULT> {
	
	IHashVector getFeatures();
	
	ERESULT getResult();
	
	double getScore();
	
}
