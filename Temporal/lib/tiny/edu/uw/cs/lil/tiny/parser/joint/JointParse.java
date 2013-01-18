package edu.uw.cs.lil.tiny.parser.joint;

import java.util.LinkedHashSet;

import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.RuleUsageTriplet;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.utils.composites.Pair;

public class JointParse<Y, Z> implements IJointParse<Y, Z> {
	
	private final Z							execResult;
	private final IParseResult<Y>			innerParse;
	private final IJointDataItemModel<Y, Z>	model;
	private final Pair<Y, Z>				resultPair;
	private final double					score;
	
	public JointParse(IParseResult<Y> innerParse, Z execResult,
			IJointDataItemModel<Y, Z> model) {
		this.innerParse = innerParse;
		this.execResult = execResult;
		this.model = model;
		this.resultPair = Pair.of(innerParse.getY(), execResult);
		this.score = innerParse.getScore() + model.score(resultPair);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final JointParse<?, ?> other = (JointParse<?, ?>) obj;
		if (execResult == null) {
			if (other.execResult != null) {
				return false;
			}
		} else if (!execResult.equals(other.execResult)) {
			return false;
		}
		if (innerParse == null) {
			if (other.innerParse != null) {
				return false;
			}
		} else if (!innerParse.equals(other.innerParse)) {
			return false;
		}
		return true;
	}
	
	@Override
	public LinkedHashSet<LexicalEntry<Y>> getAllLexicalEntries() {
		return innerParse.getAllLexicalEntries();
	}
	
	@Override
	public IHashVector getAverageMaxFeatureVector() {
		final IHashVector features = model.computeFeatures(resultPair);
		innerParse.getAverageMaxFeatureVector().addTimesInto(1.0, features);
		return features;
	}
	
	@Override
	public double getBaseScore() {
		return innerParse.getScore();
	}
	
	public Z getFinalResult() {
		return execResult;
	}
	
	@Override
	public LinkedHashSet<LexicalEntry<Y>> getMaxLexicalEntries() {
		return innerParse.getMaxLexicalEntries();
	}
	
	@Override
	public LinkedHashSet<RuleUsageTriplet> getMaxRulesUsed() {
		return innerParse.getMaxRulesUsed();
	}
	
	@Override
	public Pair<Y, Z> getResult() {
		return resultPair;
	}
	
	@Override
	public double getScore() {
		return score;
	}
	
	@Override
	public Y getY() {
		return innerParse.getY();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((execResult == null) ? 0 : execResult.hashCode());
		result = prime * result
				+ ((innerParse == null) ? 0 : innerParse.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(innerParse.toString()).append(" => ")
				.append(execResult).toString();
	}
	
}
