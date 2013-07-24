package edu.uw.cs.lil.temporal.eval.entities.nodes;

public class WeekNode extends TemporalNode {
	public WeekNode(TemporalNode... parents) {
		super(parents);
	}

	@Override
	public String getName() {
		return "week";
	}

	@Override
	public String getValue() {
		TemporalNode preferredParent = getPreferredParent();
		TemporalNode preferredChild = getPreferredChild();
		if (preferredParent instanceof YearNode) {
			String value = isSpecified() ? String.format("-W%2d", n) : "-WXX";
			return preferredChild != null ? value + preferredChild.getValue() : value;
		}
		else {
			throw new IllegalArgumentException("Will support weeks of the month later");
		}
	}
}
