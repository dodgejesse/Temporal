package edu.uw.cs.lil.tiny.mr.language.type;

/**
 * Type of an array for a given base type.
 * 
 * @author Yoav Artzi
 */
public class ArrayType extends Type {
	public static final String	ARRAY_SUFFIX	= "[]";
	
	private final Type			baseType;
	private final Type			parent;
	
	ArrayType(String name, Type baseType, Type parent) {
		super(name);
		this.parent = parent;
		this.baseType = baseType;
	}
	
	public Type getBaseType() {
		return baseType;
	}
	
	@Override
	public Type getDomain() {
		return null;
	}
	
	@Override
	public Type getRange() {
		return this;
	}
	
	@Override
	public boolean isArray() {
		return true;
	}
	
	@Override
	public boolean isComplex() {
		return baseType.isComplex();
	}
	
	@Override
	public boolean isExtending(Type other) {
		if (other == null) {
			return false;
		}
		
		if (this.equals(other)) {
			return true;
		} else if (other.isArray()) {
			// An array A extends and array B, if the A.basetype extends
			// B.basetype
			return this.baseType.isExtending(((ArrayType) other).getBaseType());
		} else {
			return parent == null ? false : parent.isExtending(other);
		}
	}
	
	@Override
	public boolean isExtendingOrExtendedBy(Type other) {
		return other != null
				&& (this.isExtending(other) || other.isExtending(this));
	}
	
	@Override
	public String toString() {
		return baseType.toString() + ARRAY_SUFFIX;
	}
}
