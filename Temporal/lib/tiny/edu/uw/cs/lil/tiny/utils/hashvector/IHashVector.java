package edu.uw.cs.lil.tiny.utils.hashvector;

/**
 * Hash vector to store sparse vectors.
 * 
 * @author Yoav Artzi
 */
public interface IHashVector extends IHashVectorImmutable {
	
	/**
	 * Threshold value to drop values.
	 */
	public static final double	NOISE		= 0.001;
	
	/** The value of zero */
	static final double			ZERO_VALUE	= 0.0;
	
	/**
	 * Add a given constant to all the values in the vector.
	 * 
	 * @param num
	 */
	void add(final double num);
	
	/**
	 * Remove all the values from the vector.
	 */
	void clear();
	
	/**
	 * Divide all the values by the given constant.
	 * 
	 * @param d
	 */
	void divideBy(final double d);
	
	/**
	 * Drop all the small entries according to the {@link #NOISE} constant.
	 */
	void dropSmallEntries();
	
	void multiplyBy(final double d);
	
	void set(String arg1, double value);
	
	void set(String arg1, String arg2, double value);
	
	void set(String arg1, String arg2, String arg3, double value);
	
	void set(String arg1, String arg2, String arg3, String arg4, double value);
	
	void set(String arg1, String arg2, String arg3, String arg4, String arg5,
			double value);
	
}