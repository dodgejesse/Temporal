package edu.uw.cs.lil.tiny.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.uw.cs.utils.composites.Pair;

public class RuleUsageTriplet {
	private final List<Pair<Integer, Integer>>	children;
	private final String						ruleName;
	
	public RuleUsageTriplet(String ruleName,
			List<Pair<Integer, Integer>> children) {
		this.children = Collections.unmodifiableList(children);
		this.ruleName = ruleName;
	}
	
	public RuleUsageTriplet(String ruleName, Pair<Integer, Integer>... children) {
		this(ruleName, Arrays.asList(children));
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
		final RuleUsageTriplet other = (RuleUsageTriplet) obj;
		if (children == null) {
			if (other.children != null) {
				return false;
			}
		} else if (!children.equals(other.children)) {
			return false;
		}
		if (ruleName == null) {
			if (other.ruleName != null) {
				return false;
			}
		} else if (!ruleName.equals(other.ruleName)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result
				+ ((ruleName == null) ? 0 : ruleName.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(ruleName).append("[");
		final Iterator<Pair<Integer, Integer>> iterator = children.iterator();
		while (iterator.hasNext()) {
			final Pair<Integer, Integer> child = iterator.next();
			sb.append(child.first()).append("-").append(child.second());
			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}
		return sb.append("]").toString();
	}
	
}
