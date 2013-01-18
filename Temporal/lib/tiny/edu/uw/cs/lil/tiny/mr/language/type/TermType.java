package edu.uw.cs.lil.tiny.mr.language.type;

/**
 * A type for a term with a possible parent.
 * 
 * @author Yoav Artzi
 */
public class TermType extends Type {
	private final TermType	parent;
	
	TermType(String label) {
		super(label);
		this.parent = null;
	}
	
	TermType(String label, TermType parent) {
		super(label);
		this.parent = parent;
	}
	
	@Override
	public Type getDomain() {
		return null;
	}
	
	public TermType getParent() {
		return parent;
	}
	
	@Override
	public Type getRange() {
		return this;
	}
	
	@Override
	public boolean isArray() {
		return false;
	}
	
	@Override
	public boolean isComplex() {
		return false;
	}
	
	@Override
	public boolean isExtending(Type other) {
		if (this.equals(other)) {
			return true;
		} else {
			return parent != null && parent.isExtending(other);
		}
	}
	
	@Override
	public boolean isExtendingOrExtendedBy(Type other) {
		return other != null
				&& (this.isExtending(other) || other.isExtending(this));
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
