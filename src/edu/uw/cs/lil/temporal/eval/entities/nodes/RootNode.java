package edu.uw.cs.lil.temporal.eval.entities.nodes;

public class RootNode extends TemporalNode {
	public RootNode(TemporalNode... parents) {
		super(parents);
	}

	@Override
	public String getName() {
		return "root";
	}

	@Override
	public String getValue() {
		for(TemporalNode c : children)
			if (c.isActive())
				return c.getValue();
		throw new IllegalArgumentException("Try to retrieve value with no active nodes");
	}
}

