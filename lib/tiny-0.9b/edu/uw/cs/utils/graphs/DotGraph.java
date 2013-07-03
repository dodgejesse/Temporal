package edu.uw.cs.utils.graphs;

import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.utils.composites.Pair;

/**
 * Graphviz .dot undirected graph object.
 * 
 * @author Yoav Artzi
 */
public class DotGraph {
	private final String								graphId;
	/**
	 * Edges. (from node, to node) --> label
	 */
	final protected Map<Pair<String, String>, String>	edges	= new HashMap<Pair<String, String>, String>();
	final protected String								graphLabel;
	/**
	 * Nodes. id --> label
	 */
	final protected Map<String, String>					nodes	= new HashMap<String, String>();
	
	public DotGraph(String graphId, String graphLabel) {
		this.graphLabel = graphLabel;
		this.graphId = graphId;
	}
	
	/**
	 * Add an edge between nodes a and b with a label. Assumes the nodes were
	 * already added.
	 * 
	 * @param a
	 * @param b
	 * @param label
	 */
	public void addEdge(String a, String b, String label) {
		if (nodes.containsKey(a) && nodes.containsKey(b)) {
			if (!edges.containsKey(Pair.of(b, a))) {
				edges.put(Pair.of(a, b), label);
			}
		} else {
			throw new IllegalStateException("At least one non-existing node: "
					+ a + ", " + b);
		}
	}
	
	final public void addNode(String id, String label) {
		nodes.put(id, label);
	}
	
	@Override
	public String toString() {
		final StringBuilder ret = new StringBuilder();
		
		// Header + graph title
		ret.append(getGraphElementLabel());
		ret.append(" ");
		ret.append(graphId);
		ret.append(" {\n");
		ret.append("\tgraph [label=\"");
		ret.append(graphLabel);
		ret.append("\"];\n");
		
		// Nodes
		for (final Map.Entry<String, String> entry : nodes.entrySet()) {
			ret.append("\t");
			ret.append(nodeToLine(entry.getKey(), entry.getValue()));
			ret.append("\n");
		}
		
		// Edges
		for (final Map.Entry<Pair<String, String>, String> entry : edges
				.entrySet()) {
			ret.append("\t");
			ret.append(edgeToLine(entry.getKey().first(), entry.getKey()
					.second(), entry.getValue()));
			ret.append("\n");
		}
		
		// Close graph
		ret.append("}");
		
		return ret.toString();
	}
	
	protected String edgeToLine(String a, String b, String label) {
		return a + " -- " + b + " [label=\"" + label + "\"];";
	}
	
	protected String getGraphElementLabel() {
		return "graph";
	}
	
	protected String nodeToLine(String id, String label) {
		return id + " [label=\"" + label + "\"];";
	}
	
}
