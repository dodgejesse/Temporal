package edu.uw.cs.lil.temporal.eval.entities;

import java.util.List;

import edu.uw.cs.lil.temporal.eval.entities.TemporalEntity;
import edu.uw.cs.lil.temporal.eval.entities.nodes.DayNode;
import edu.uw.cs.lil.temporal.eval.entities.nodes.MonthNode;
import edu.uw.cs.lil.temporal.eval.entities.nodes.QuarterNode;
import edu.uw.cs.lil.temporal.eval.entities.nodes.RootNode;
import edu.uw.cs.lil.temporal.eval.entities.nodes.TemporalNode;
import edu.uw.cs.lil.temporal.eval.entities.nodes.WeekNode;
import edu.uw.cs.lil.temporal.eval.entities.nodes.YearNode;

public class TemporalDuration extends TemporalEntity {
	private List<TemporalNode> nodes;
	public TemporalDuration () {
		nodes.add(new YearNode());
		nodes.add(new MonthNode());
		nodes.add(new QuarterNode());
		nodes.add(new WeekNode());
		nodes.add(new DayNode());
		for (TemporalNode n : nodes)
			n.activate();
	}
}
