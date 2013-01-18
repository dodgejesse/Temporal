package edu.uw.cs.lil.tiny.test;

import edu.uw.cs.lil.tiny.parser.ccg.model.IModelImmutable;
import edu.uw.cs.lil.tiny.test.stats.ITestingStatistics;

public interface ITester<X, Y> {
	
	void test(IModelImmutable<X, Y> model, ITestingStatistics<X, Y> stats);
	
}
