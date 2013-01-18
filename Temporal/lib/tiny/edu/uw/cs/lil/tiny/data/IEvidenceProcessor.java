package edu.uw.cs.lil.tiny.data;

/**
 * Processor for a set of evidence.
 * 
 * @author Yoav Artzi
 * @param <X>
 *            Type of sample
 * @param <E>
 *            Type of evidence
 */
public interface IEvidenceProcessor<X, E> {
	/**
	 * @param evidence
	 * @param sample
	 * @return 'true' if to continue to the next evidence, otherwise false.
	 */
	boolean processEvidence(X sample, E evidence);
}
