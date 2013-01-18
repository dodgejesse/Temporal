package edu.uw.cs.lil.tiny.explat;

/**
 * Listener for job completion.
 * 
 * @author Yoav Artzi
 */
public interface IJobListener {
	public void jobCompleted(String jobId);
	
	public void jobException(String jobId, Exception e);
}
