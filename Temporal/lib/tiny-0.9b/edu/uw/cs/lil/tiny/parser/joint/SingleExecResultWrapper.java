package edu.uw.cs.lil.tiny.parser.joint;

import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.lil.tiny.utils.hashvector.HashVectorFactory;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;

/**
 * Very simple wrapper for an execution with a single step.
 * 
 * @author Yoav Artzi
 * @param <ESTEP>
 * @param <ERESULT>
 */
public class SingleExecResultWrapper<ESTEP, ERESULT> implements
		IExecResultWrapper<ERESULT> {
	
	private final ESTEP							executionStep;
	private IHashVector							features	= null;
	private final IJointDataItemModel<?, ESTEP>	model;
	private final ERESULT						result;
	private Double								score		= null;
	
	public SingleExecResultWrapper(ESTEP executionStep,
			IJointDataItemModel<?, ESTEP> model, ERESULT result) {
		this.executionStep = executionStep;
		this.model = model;
		this.result = result;
	}
	
	@Override
	public IHashVector getFeatures() {
		
		if (features == null) {
			features = result == null ? HashVectorFactory.create() : model
					.computeFeatures(executionStep);
		}
		return features;
	}
	
	@Override
	public ERESULT getResult() {
		return result;
	}
	
	@Override
	public double getScore() {
		if (score == null) {
			score = result == null ? 0.0 : model.score(executionStep);
		}
		return score;
	}
	
}
