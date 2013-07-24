package edu.uw.cs.lil.temporal.eval.entities.nodes;

public class DayNode extends TemporalNode {
	public DayNode(TemporalNode... parents) {
		super(parents);
	}

	@Override
	public String getName() {
		return "day";
	}

	@Override
	public String getValue() {
		TemporalNode preferredParent = getPreferredParent();
		TemporalNode preferredChild = getPreferredChild();
		if (preferredParent instanceof MonthNode) {
			String value = isSpecified() ? String.format("-%2d", n) : "-XX";
			return preferredChild != null ? value + preferredChild.getValue() : value;
		}
		else {
			// Context-dependent variants should take of this. Return null after this is stable
			throw new IllegalArgumentException("ISO does not support sets of weekdays");
		}
	}
}
