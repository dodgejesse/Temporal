package edu.uw.cs.utils.graphs;

import edu.uw.cs.utils.composites.Pair;

/**
 * Graphviz .dot directed graph object.
 * 
 * @author Yoav Artzi
 */
public class DirectedDotGraph extends DotGraph {
	
	public DirectedDotGraph(String graphId, String graphLabel) {
		super(graphId, graphLabel);
	}
	
	@Override
	public void addEdge(String fromId, String toId, String label) {
		if (nodes.containsKey(fromId) && nodes.containsKey(fromId)) {
			edges.put(Pair.of(fromId, toId), label);
		} else {
			throw new IllegalStateException("Non existing node: " + fromId
					+ ", " + toId);
		}
	}
	
	@Override
	protected String edgeToLine(String fromId, String toId, String label) {
		return fromId + " -> " + toId + " [label=\"" + label + "\"];";
	}
	
	@Override
	protected String getGraphElementLabel() {
		return "digraph";
	}
}
