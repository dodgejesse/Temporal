package edu.uw.cs.lil.tiny.parser.joint;

import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.utils.composites.Pair;

public class JointOutput<Y, Z> implements IJointOutput<Y, Z> {
	
	private final IParserOutput<Y>			baseParserOutput;
	private final List<IJointParse<Y, Z>>	bestJointParses;
	private final List<IJointParse<Y, Z>>	bestSuccessfulJointParses;
	private final List<IJointParse<Y, Z>>	jointParses;
	private final long						parsingTime;
	private final List<IJointParse<Y, Z>>	successfulJointParses;
	
	public JointOutput(IParserOutput<Y> baseParserOutput,
			List<IJointParse<Y, Z>> jointParses, long parsingTime) {
		this.baseParserOutput = baseParserOutput;
		this.jointParses = jointParses;
		this.parsingTime = parsingTime;
		this.bestJointParses = findBestParses(jointParses);
		this.successfulJointParses = successfulOnly(jointParses);
		this.bestSuccessfulJointParses = findBestParses(successfulJointParses);
	}
	
	private static <Y, Z> List<IJointParse<Y, Z>> findBestParses(
			List<IJointParse<Y, Z>> all) {
		return findBestParses(all, null);
	}
	
	private static <Y, Z> List<IJointParse<Y, Z>> findBestParses(
			List<IJointParse<Y, Z>> all, Pair<Y, Z> label) {
		final List<IJointParse<Y, Z>> best = new LinkedList<IJointParse<Y, Z>>();
		double bestScore = -Double.MAX_VALUE;
		for (final IJointParse<Y, Z> p : all) {
			if ((label == null || ((label.first() == null || p.getResult()
					.first().equals(label.first())) && (label.second() == null || p
					.getResult().second().equals(label.second()))))) {
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
	
	private static <Y, Z> List<IJointParse<Y, Z>> successfulOnly(
			List<IJointParse<Y, Z>> parses) {
		final List<IJointParse<Y, Z>> successfulParses = new LinkedList<IJointParse<Y, Z>>();
		for (final IJointParse<Y, Z> parse : parses) {
			if (parse.getResult().second() != null) {
				successfulParses.add(parse);
			}
		}
		return successfulParses;
	}
	
	@Override
	public List<? extends IJointParse<Y, Z>> getAllJointParses() {
		return getAllJointParses(true);
	}
	
	@Override
	public List<? extends IJointParse<Y, Z>> getAllJointParses(
			boolean includeFails) {
		if (includeFails) {
			return jointParses;
		} else {
			return successfulJointParses;
		}
	}
	
	@Override
	public List<IParseResult<Y>> getAllParses() {
		return baseParserOutput.getAllParses();
	}
	
	@Override
	public IParserOutput<Y> getBaseParserOutput() {
		return baseParserOutput;
	}
	
	@Override
	public List<? extends IJointParse<Y, Z>> getBestJointParses() {
		return getBestJointParses(true);
	}
	
	@Override
	public List<? extends IJointParse<Y, Z>> getBestJointParses(
			boolean includeFails) {
		if (includeFails) {
			return bestJointParses;
		} else {
			return bestSuccessfulJointParses;
		}
	}
	
	@Override
	public List<IParseResult<Y>> getBestParses() {
		return baseParserOutput.getBestParses();
	}
	
	@Override
	public List<IJointParse<Y, Z>> getBestParsesFor(Pair<Y, Z> label) {
		final List<IJointParse<Y, Z>> parses = new LinkedList<IJointParse<Y, Z>>();
		final double score = -Double.MAX_VALUE;
		for (final IJointParse<Y, Z> p : jointParses) {
			if (p.getResult().equals(label)) {
				if (p.getScore() > score) {
					parses.clear();
					parses.add(p);
				} else if (p.getScore() == score) {
					parses.add(p);
				}
			}
		}
		return parses;
	}
	
	@Override
	public List<IJointParse<Y, Z>> getBestParsesForY(Y partialLabel) {
		final List<IJointParse<Y, Z>> parses = new LinkedList<IJointParse<Y, Z>>();
		final double score = -Double.MAX_VALUE;
		for (final IJointParse<Y, Z> p : jointParses) {
			if (p.getResult().first().equals(partialLabel)) {
				if (p.getScore() > score) {
					parses.clear();
					parses.add(p);
				} else if (p.getScore() == score) {
					parses.add(p);
				}
			}
		}
		return parses;
	}
	
	@Override
	public List<IJointParse<Y, Z>> getBestParsesForZ(Z partialLabel) {
		final List<IJointParse<Y, Z>> parses = new LinkedList<IJointParse<Y, Z>>();
		final double score = -Double.MAX_VALUE;
		for (final IJointParse<Y, Z> p : jointParses) {
			if (p.getResult().second().equals(partialLabel)) {
				if (p.getScore() > score) {
					parses.clear();
					parses.add(p);
				} else if (p.getScore() == score) {
					parses.add(p);
				}
			}
		}
		return parses;
	}
	
	public List<LexicalEntry<Y>> getMaxLexicalEntries(Pair<Y, Z> label) {
		final List<LexicalEntry<Y>> result = new LinkedList<LexicalEntry<Y>>();
		for (final IJointParse<Y, Z> p : findBestParses(jointParses, label)) {
			result.addAll(p.getMaxLexicalEntries());
		}
		return result;
	}
	
	@Override
	public List<LexicalEntry<Y>> getMaxLexicalEntries(Y label) {
		return getMaxLexicalEntries(Pair.of(label, (Z) null));
	}
	
	@Override
	public List<IParseResult<Y>> getMaxParses(Y label) {
		return baseParserOutput.getMaxParses(label);
	}
	
	@Override
	public List<IJointParse<Y, Z>> getParsesFor(Pair<Y, Z> label) {
		final List<IJointParse<Y, Z>> parses = new LinkedList<IJointParse<Y, Z>>();
		for (final IJointParse<Y, Z> p : jointParses) {
			if (p.getResult().equals(label)) {
				parses.add(p);
			}
		}
		return parses;
	}
	
	@Override
	public List<IJointParse<Y, Z>> getParsesForY(Y partialLabel) {
		final List<IJointParse<Y, Z>> parses = new LinkedList<IJointParse<Y, Z>>();
		for (final IJointParse<Y, Z> p : jointParses) {
			if (p.getResult().first().equals(partialLabel)) {
				parses.add(p);
			}
		}
		return parses;
	}
	
	@Override
	public List<IJointParse<Y, Z>> getParsesForZ(Z partialLabel) {
		final List<IJointParse<Y, Z>> parses = new LinkedList<IJointParse<Y, Z>>();
		for (final IJointParse<Y, Z> p : jointParses) {
			if (p.getResult().second().equals(partialLabel)) {
				parses.add(p);
			}
		}
		return parses;
	}
	
	public long getParsingTime() {
		return parsingTime;
	}
	
}
