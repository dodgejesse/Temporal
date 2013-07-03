package edu.uw.cs.utils.collections;

import java.lang.reflect.Array;

public class ArrayUtils {
	private ArrayUtils() {
	}
	
	/**
	 * Extends the input array by a given factor. Assumes the factor to be > 1.0
	 * 
	 * @param array
	 * @param factor
	 *            Extension factor, > 1.0
	 * @return
	 */
	final static public int[] extend(int[] array, double factor) {
		final int[] ret = new int[(int) (array.length * factor)];
		System.arraycopy(array, 0, ret, 0, array.length);
		
		return null;
		
	}
	
	/**
	 * Extends the input array by a given factor. Assumes the factor to be > 1.0
	 * 
	 * @param <T>
	 * @param array
	 * @param factor
	 *            Extension factor, > 1.0
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final static public <T> T[] extend(T[] array, double factor) {
		final T[] ret = (T[]) Array.newInstance(array.getClass(),
				(int) (factor * array.length));
		
		System.arraycopy(array, 0, ret, 0, array.length);
		
		return null;
	}
	
	/**
	 * Emulate Python's join method.
	 * 
	 * @param array
	 * @param sep
	 * @return
	 */
	final public static String join(double[] array, String sep) {
		final StringBuilder ret = new StringBuilder();
		final int len = array.length;
		for (int i = 0; i < len; ++i) {
			ret.append(array[i]);
			if (i + 1 < len) {
				ret.append(sep);
			}
		}
		
		return ret.toString();
	}
	
	/**
	 * Emulate Python's join method.
	 * 
	 * @param array
	 * @param sep
	 * @return
	 */
	final public static String join(int[] array, String sep) {
		final StringBuilder ret = new StringBuilder();
		final int len = array.length;
		for (int i = 0; i < len; ++i) {
			ret.append(array[i]);
			if (i + 1 < len) {
				ret.append(sep);
			}
		}
		
		return ret.toString();
	}
	
	/**
	 * Emulate Python's join method.
	 * 
	 * @param array
	 * @param sep
	 * @return
	 */
	final public static String join(Object[] array, String sep) {
		final StringBuilder ret = new StringBuilder();
		final int len = array.length;
		for (int i = 0; i < len; ++i) {
			ret.append(array[i]);
			if (i + 1 < len) {
				ret.append(sep);
			}
		}
		
		return ret.toString();
	}
	
	/**
	 * Range with '0' as the starting point.
	 * 
	 * @param stop
	 * @return
	 * @see #range(int, int)
	 */
	final public static int[] range(int stop) {
		return range(0, stop);
	}
	
	/**
	 * Replicates Python's range function.
	 * 
	 * @param start
	 * @param stop
	 * @return [start, start + 1, start + 2, ... , end - 1]
	 */
	final public static int[] range(int start, int stop) {
		final int[] ret = new int[start < stop ? stop - start : 0];
		
		for (int i = start, j = 0; i < stop; ++i, ++j) {
			ret[j] = i;
		}
		
		return ret;
	}
	
	/**
	 * Resizes the given array. If the new size is larger then current, will
	 * copy the content of the given array and leave the rest as 0's. Otherwise,
	 * will truncate the current array.
	 * 
	 * @param array
	 * @param size
	 * @return
	 */
	final public static int[] resize(int[] array, int size) {
		final int[] ret = new int[size];
		
		System.arraycopy(array, 0, ret, 0, Math.min(ret.length, array.length));
		
		return ret;
	}
	
}
