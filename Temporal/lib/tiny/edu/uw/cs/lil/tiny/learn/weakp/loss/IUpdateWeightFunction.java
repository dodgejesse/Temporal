package edu.uw.cs.lil.tiny.learn.weakp.loss;

import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.utils.composites.Pair;

/**
 * Weighting function as described in Singh-Miller and Collins 2007.
 * 
 * @author Yoav Artzi
 * @param <Y>
 */
public interface IUpdateWeightFunction<Y> {
	
	/**
	 * Calculate weight for a non optimal parse.
	 * 
	 * @param nonOptimalParse
	 * @return
	 */
	double evalNonOptimalParse(
			Pair<Double, ? extends IParseResult<Y>> nonOptimalParse);
	
	/**
	 * Calculate weight for a optimal parse.
	 * 
	 * @return
	 */
	double evalOptimalParse(Pair<Double, ? extends IParseResult<Y>> optimalParse);
	
}