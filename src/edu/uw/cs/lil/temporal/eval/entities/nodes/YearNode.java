package edu.uw.cs.lil.temporal.eval.entities.nodes;

public class YearNode extends TemporalNode {
	public YearNode(TemporalNode... parents) {
		super(parents);
	}

	@Override
	public String getName() {
		return "year";
	}

	@Override
	public String getValue() {
		String value = isSpecified() ? String.format("%4d", n) : "XXXX";
		TemporalNode preferredChild = getPreferredChild();
		return preferredChild != null ? value + preferredChild.getValue() : value;
	}
}
