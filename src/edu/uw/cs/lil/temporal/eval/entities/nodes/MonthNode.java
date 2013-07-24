package edu.uw.cs.lil.temporal.eval.entities.nodes;

public class MonthNode extends TemporalNode {
	public MonthNode(TemporalNode... parents) {
		super(parents);
	}

	@Override
	public String getName() {
		return "month";
	}
	
	@Override
	public String getValue() {
		String value = isSpecified() ? String.format("-%2d", n) : "-XX";
		TemporalNode preferredChild = getPreferredChild();
		return preferredChild != null ? value + preferredChild.getValue() : value;
	}
}
