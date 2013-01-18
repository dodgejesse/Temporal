package edu.uw.cs.lil.tiny.mr.language.type;

/**
 * A language entity type.
 * 
 * @author Yoav Artzi
 */
public abstract class Type {
	/**
	 * Mutable cache for the hashing code. This field is for internal use only!
	 * It mustn't be used when copying/comparing/storing/etc. the object.
	 */
	final private int		hashCodeCache;
	/**
	 * The name of the type. This name must be unique. Meaning, we don't allow
	 * function over-loading, for example.
	 */
	final private String	name;
	
	Type(String name) {
		this.name = name;
		this.hashCodeCache = calcHashCode();
	}
	
	@Override
	final public boolean equals(Object obj) {
		// There is one object for each type, so if they are not the same, they
		// are different types
		return this == obj;
	}
	
	public abstract Type getDomain();
	
	public String getName() {
		return name;
	}
	
	public abstract Type getRange();
	
	@Override
	final public int hashCode() {
		return hashCodeCache;
	}
	
	/**
	 * Returns true iff the type is an array.
	 * 
	 * @return
	 */
	public abstract boolean isArray();
	
	/**
	 * Return true iff the object is a complex function type.
	 * 
	 * @return
	 */
	public abstract boolean isComplex();
	
	/**
	 * Is current type a child of another.
	 * 
	 * @param other
	 */
	public abstract boolean isExtending(Type other);
	
	/**
	 * Return 'true' iff the given type and this type share a path on the
	 * hierarchical tree.
	 * 
	 * @param other
	 */
	public abstract boolean isExtendingOrExtendedBy(Type other);
	
	@Override
	public abstract String toString();
	
	private int calcHashCode() {
		return this.name.hashCode();
	}
	
}
