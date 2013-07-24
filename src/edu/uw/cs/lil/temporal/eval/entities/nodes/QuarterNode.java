package edu.uw.cs.lil.temporal.eval.entities.nodes;

public class QuarterNode extends TemporalNode {
	public QuarterNode(TemporalNode... parents) {
		super(parents);
	}

	@Override
	public String getName() {
		return "quarter";
	}
	
	@Override
	public String getValue() {
		String value = isSpecified() ? String.format("-Q%d", n) : "-QXX";
		TemporalNode preferredChild = getPreferredChild();
		return preferredChild != null ? value + preferredChild.getValue() : value;
	}
}
