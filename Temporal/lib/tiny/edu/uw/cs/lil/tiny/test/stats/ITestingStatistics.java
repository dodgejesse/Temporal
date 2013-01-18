package edu.uw.cs.lil.tiny.test.stats;

import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataItem;

/**
 * Accumulates testing statistics.
 * 
 * @author Yoav Artzi
 */
public interface ITestingStatistics<X, Y> {
	
	void recordNoParse(IDataItem<X> dataItem, Y gold);
	
	void recordNoParseWithSkipping(IDataItem<X> dataItem, Y gold);
	
	/**
	 * Record a parse.
	 */
	void recordParse(IDataItem<X> dataItem, Y gold, Y label);
	
	void recordParses(IDataItem<X> dataItem, Y gold, List<Y> labels);
	
	void recordParsesWithSkipping(IDataItem<X> dataItem, Y gold, List<Y> labels);
	
	/**
	 * Record a parse with word skipping enabled. Assumes a record parse for
	 * this data item has been called earlier.
	 */
	void recordParseWithSkipping(IDataItem<X> dataItem, Y gold, Y label);
	
	@Override
	String toString();
	
	/**
	 * Generate machine readable tab-delimited string. Formatting:
	 * <key>=<value>\t<key>=<value>...
	 * 
	 * @return
	 */
	String toTabDelimitedString();
}
