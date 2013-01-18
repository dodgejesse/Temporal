package edu.uw.cs.lil.tiny.parser.ccg.model;

import java.util.List;

import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.lil.tiny.utils.hashvector.KeyArgs;
import edu.uw.cs.utils.composites.Triplet;

public interface IFeatureSet {
	/**
	 * Returns all the weights of the features represented by this feature set.
	 * 
	 * @return List of triplets of <hash vector key, weight value, optional
	 *         comment (might be null)>
	 */
	public abstract List<Triplet<KeyArgs, Double, String>> getFeatureWeights(
			IHashVector theta);
	
}
