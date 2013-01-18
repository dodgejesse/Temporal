package edu.uw.cs.lil.tiny.learn.ubl.splitting;

import java.util.Set;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.learn.ubl.splitting.SplittingServices.SplittingPair;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

public interface IUBLSplitter {
	
	Set<SplittingPair> getSplits(Category<LogicalExpression> category);
	
}
