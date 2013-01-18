package edu.uw.cs.lil.tiny.learn.weakp.loss;

import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.utils.composites.Pair;

/**
 * Uniform weight function. Gives an equal weight to all parses, so the sum
 * weight for all optimal parses is 1.0 and the sum weight for all non-optimal
 * parses is 1.0.
 * 
 * @author Yoav Artzi
 * @param <Y>
 */
public class UniformWeightFunction<Y> implements IUpdateWeightFunction<Y> {
	private final double	valueForNonOptimalParses;
	private final double	valueForOptimalParses;
	
	public UniformWeightFunction(int numViolatingOptimalParses,
			int numViolatingNonOptimalParses) {
		this.valueForOptimalParses = 1.0 / numViolatingOptimalParses;
		this.valueForNonOptimalParses = 1.0 / numViolatingNonOptimalParses;
	}
	
	@Override
	public double evalNonOptimalParse(
			Pair<Double, ? extends IParseResult<Y>> nonOptimalParse) {
		return valueForNonOptimalParses;
	}
	
	@Override
	public double evalOptimalParse(
			Pair<Double, ? extends IParseResult<Y>> optimalParse) {
		return valueForOptimalParses;
	}
}
