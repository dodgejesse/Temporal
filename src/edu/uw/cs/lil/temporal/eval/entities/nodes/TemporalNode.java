package edu.uw.cs.lil.temporal.eval.entities.nodes;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class TemporalNode {
	protected List<TemporalNode> parents, children;
	protected int n;

	public TemporalNode(TemporalNode... parents) {
		for(TemporalNode p : parents)
			p.claimChild(this);
		this.parents = new LinkedList<TemporalNode> (Arrays.asList(parents));
		this.children = new LinkedList<TemporalNode>();
		this.n = -1;
	}

	public abstract String getName();
	public abstract String getValue();
	
	public List<TemporalNode> getParents() {
		return parents;
	}

	public List<TemporalNode> getChildren() {
		return children;
	}
	
	public TemporalNode getPreferredChild() {
		for (TemporalNode c : children)
			if (c.isActive())
				return c;
		return null;
	}
	
	public TemporalNode getPreferredParent() {
		for (TemporalNode p : parents)
			if (p.isActive())
				return p;
		return null;
	}

	public void claimChild(TemporalNode child) {
		children.add(child);
	}


	public String toString() {
		return toString("");
	}
	
	public void deactivate(){
		n = -1;
	}

	public void activate() {
		n = 0;
	}

	protected boolean isActive() {
		return n >= 0;
	}

	public boolean isSpecified() {
		return n > 0;
	}

	public String toString(String indentation) {
		String s = indentation + getName() + "(" + n + ")\n";
		indentation = indentation + " ";
		for (TemporalNode c : children)
			s += c.toString(indentation);
		return s;
	}

	public TemporalNode findNode(String name) {
		if (name.equals(getName()))
			return this;
		else
			for(TemporalNode c : children) {
				TemporalNode result = c.findNode(name);
				if (result != null)
					return result;
			}
		return null;
	}

	public void activateAncestors() {
		n = 0;
		for (TemporalNode p : parents)
			p.activateAncestors();
	}

	public TemporalNode getDeepestActiveNode() {
		for (TemporalNode c : children)
			if(c.isActive())
				return c.getDeepestActiveNode();
		return this;
	}
	
	public int getActiveDepth(int currentDepth) {
		for (TemporalNode c : children)
			if(c.isActive())
				return c.getActiveDepth(currentDepth + 1);
		return currentDepth;
	}

	public void pruneParents(String parentName) {
		// Prefer the first node if the given scope is not a direct parent
		TemporalNode bestParent = parents.get(0);
		for(TemporalNode p : parents) {
			if (p.getName().equals(parentName)) {
				bestParent = p;
				break;
			}
		}
		parents.clear();
		parents.add(bestParent);
	}
	
	public int getN() {
		return n;
	}
	
	public void setN(int n){
		this.n = n;
	}
}
