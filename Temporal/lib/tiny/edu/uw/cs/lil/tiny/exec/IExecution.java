package edu.uw.cs.lil.tiny.exec;

public interface IExecution<Z> {
	Z getResult();
	
	double score();
	
	String toString();
	
	String toString(boolean verbose);
	
}
