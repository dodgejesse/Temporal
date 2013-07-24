package edu.uw.cs.lil.temporal.eval.entities;

import java.io.File;

import edu.uw.cs.lil.temporal.TemporalMain;
import edu.uw.cs.lil.temporal.eval.TemporalEvaluation;
import edu.uw.cs.lil.temporal.eval.TemporalEvaluationConstants;
import edu.uw.cs.lil.temporal.eval.TemporalEvaluationServices;
import edu.uw.cs.lil.temporal.eval.entities.TemporalEntity;
import edu.uw.cs.lil.temporal.eval.entities.nodes.DayNode;
import edu.uw.cs.lil.temporal.eval.entities.nodes.MonthNode;
import edu.uw.cs.lil.temporal.eval.entities.nodes.QuarterNode;
import edu.uw.cs.lil.temporal.eval.entities.nodes.RootNode;
import edu.uw.cs.lil.temporal.eval.entities.nodes.TemporalNode;
import edu.uw.cs.lil.temporal.eval.entities.nodes.WeekNode;
import edu.uw.cs.lil.temporal.eval.entities.nodes.YearNode;
import edu.uw.cs.lil.temporal.eval.predicates.TemporalNth;
import edu.uw.cs.lil.tiny.mr.lambda.FlexibleTypeComparator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;

public class TemporalSequence extends TemporalEntity {
	private TemporalNode root;
	public TemporalSequence () {
		root = new RootNode();
		TemporalNode yearNode = new YearNode(root);
		TemporalNode monthNode = new MonthNode(yearNode);
		TemporalNode weekNode = new WeekNode(yearNode);
		TemporalNode dayNode = new DayNode(monthNode, weekNode);
		TemporalNode quarterNode = new QuarterNode(yearNode);
	}

	public TemporalSequence(String name) {
		this();
		activatePathsTo(name);
	}

	public void specifyDeepestActiveNode(int n, TemporalSequence scope) {
		TemporalNode deepestActiveNode = getDeepestActiveNode();
		deepestActiveNode.pruneParents(scope.getDeepestActiveNode().getName());
	}

	private TemporalNode getDeepestActiveNode() {
		return root.getDeepestActiveNode();
	}

	public void activatePathsTo(String name) {
		TemporalNode endOfPath = findNode(name);
		if (endOfPath == null)
			throw new IllegalArgumentException("Activating path to invalid node: " + name);
		endOfPath.activateAncestors();
	}

	private TemporalNode findNode(String name) {
		return root.findNode(name);
	}

	public String toString() {
		return root.toString();
	}

	public String getValue() {
		return root.getValue();
	}

	public int getActiveDepth() {
		return root.getActiveDepth(0);
	}

	public TemporalSequence intersect(TemporalSequence other) {
		// Assume active path s2 is a subset of this sequence's active path
		TemporalNode thisNode = this.root, otherNode = other.root;
		while(thisNode != null || otherNode != null) {
			if (thisNode != null && otherNode != null && !thisNode.getName().equals(otherNode.getName()))
				throw new IllegalArgumentException("Intersection when active paths not subsets");
			if (thisNode.isSpecified() && otherNode.isSpecified() && thisNode.getN () != otherNode.getN())
				throw new IllegalArgumentException("Empty intersection");
			if (otherNode != null && otherNode.isSpecified())
				thisNode.setN(otherNode.getN());
			
			thisNode = thisNode.getPreferredChild();
			otherNode = otherNode.getPreferredChild();
		}
		return this;
	}

	public static void main(String[] s) {
		LogicLanguageServices.setInstance(new LogicLanguageServices.Builder(new TypeRepository(new File("resources/tempeval.types.txt"))).setNumeralTypeName("i").setTypeComparator(new FlexibleTypeComparator()).build());
		LogicalExpressionCategoryServices categoryServices = new LogicalExpressionCategoryServices(true,false);
		TemporalEvaluationServices evaluationServices = new TemporalEvaluationServices(new TemporalEvaluationConstants());
		//System.out.println(categoryServices.parseSemantics("nth:<n,<s,<s,s>>>"));
		//System.out.println(((TemporalSequence)TemporalEvaluation.of(categoryServices.parseSemantics("week:s"), evaluationServices)).getValue());
	}
}
