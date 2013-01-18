package edu.uw.cs.lil.tiny.parser.ccg.cky;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.cky.chart.Chart;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;

/**
 * Complete output of CKY parser, including the chart and all possible parses.
 * 
 * @author Yoav Artzi
 */
public class CKYParserOutput<Y> implements IParserOutput<Y> {
	
	/** All parses */
	private final List<IParseResult<Y>>	allParses;
	
	/** Best parses */
	private final List<IParseResult<Y>>	bestParses;
	
	/** The chart of this parse */
	private final Chart<Y>				chart;
	
	private final long					parsingTime;
	
	public CKYParserOutput(Chart<Y> chart, IDataItemModel<Y> model,
			long parsingTime) {
		this.chart = chart;
		
		// System.out.println(chart);
		
		this.parsingTime = parsingTime;
		this.allParses = Collections.unmodifiableList(chart
				.getParseResults(model));
		this.bestParses = Collections
				.unmodifiableList(findBestParses(allParses));
	}
	
	private static <Y> List<IParseResult<Y>> findBestParses(
			List<IParseResult<Y>> all) {
		return findBestParses(all, null);
	}
	
	private static <Y> List<IParseResult<Y>> findBestParses(
			List<IParseResult<Y>> all, Y exp) {
		final List<IParseResult<Y>> best = new LinkedList<IParseResult<Y>>();
		double bestScore = -Double.MAX_VALUE;
		for (final IParseResult<Y> p : all) {
			if ((exp == null || p.getY().equals(exp))) {
				if (p.getScore() == bestScore) {
					best.add(p);
				}
				if (p.getScore() > bestScore) {
					bestScore = p.getScore();
					best.clear();
					best.add(p);
				}
			}
		}
		return best;
	}
	
	@Override
	public List<IParseResult<Y>> getAllParses() {
		return allParses;
	}
	
	public List<IParseResult<Y>> getBestParses() {
		return bestParses;
	}
	
	public Chart<Y> getChart() {
		return chart;
	}
	
	/**
	 * Finds the max lexical items used to produce the highest scoring parse
	 * with the given semantics.
	 */
	@Override
	public List<LexicalEntry<Y>> getMaxLexicalEntries(Y semantics) {
		final List<LexicalEntry<Y>> result = new LinkedList<LexicalEntry<Y>>();
		for (final IParseResult<Y> p : findBestParses(allParses, semantics)) {
			result.addAll(p.getMaxLexicalEntries());
		}
		return result;
	}
	
	@Override
	public List<IParseResult<Y>> getMaxParses(Y label) {
		return findBestParses(allParses, label);
	}
	
	@Override
	public long getParsingTime() {
		return parsingTime;
	}
}
