package edu.uw.cs.lil.tiny.learn.ubl.splitting;

import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.learn.ubl.splitting.SplittingServices.SplittingPair;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

/**
 * Splitting service object.
 * 
 * @author Yoav Artzi
 */
public class UnconstrainedSplitter implements IUBLSplitter {
	
	private final ICategoryServices<LogicalExpression>	categoryServices;
	
	public UnconstrainedSplitter(ICategoryServices<LogicalExpression> categoryServices) {
		this.categoryServices = categoryServices;
	}
	
	public Set<SplittingPair> getSplits(Category<LogicalExpression> category) {
		final Set<SplittingPair> splits = new HashSet<SplittingPair>();
		splits.addAll(MakeApplicationSplits.of(category, categoryServices));
		splits.addAll(MakeCompositionSplits.of(category, categoryServices));
		return splits;
	}
}
