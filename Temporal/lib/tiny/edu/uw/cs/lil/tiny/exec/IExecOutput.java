package edu.uw.cs.lil.tiny.exec;

import java.util.List;

public interface IExecOutput<Z> {
	List<IExecution<Z>> getAllExecutions();
	
	List<IExecution<Z>> getBestExecutions();
	
	long getExecTime();
	
	List<IExecution<Z>> getExecutions(Z label);
	
}
