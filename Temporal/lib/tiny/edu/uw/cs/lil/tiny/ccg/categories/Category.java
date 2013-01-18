package edu.uw.cs.lil.tiny.ccg.categories;

import edu.uw.cs.lil.tiny.ccg.categories.syntax.ComplexSyntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax.SimpleSyntax;

/**
 * A CCG Category has both a syntactic and semantic component. Each instance of
 * this class stores both.
 * 
 * @author Yoav Artzi
 */
public abstract class Category<Y> {
	
	/**
	 * Mutable cache for the hashing code. This field is for internal use only!
	 * It mustn't be used when copying/comparing/storing/etc. the object.
	 */
	private int		hashCodeCache;
	
	/**
	 * Mutable flag to indicate if the hash code cache is populated. This field
	 * is for internal use only! It mustn't be used when
	 * copying/comparing/storing/etc. the object.
	 */
	private boolean	hashCodeCalculated	= false;
	
	/**
	 * Category semantics
	 */
	private final Y	semantics;
	
	public Category(Y semantics) {
		this.semantics = semantics;
	}
	
	public static <Y> Category<Y> create(Syntax syntax) {
		return create(syntax, null);
	}
	
	public static <Y> Category<Y> create(Syntax syntax, Y semantics) {
		if (syntax instanceof SimpleSyntax) {
			return new SimpleCategory<Y>((SimpleSyntax) syntax, semantics);
		} else if (syntax instanceof ComplexSyntax) {
			return new ComplexCategory<Y>((ComplexSyntax) syntax, semantics);
		} else {
			throw new IllegalStateException("unsupported syntax type");
		}
	}
	
	/**
	 * Clones the category, but replaces the semantics of current with the given
	 * one.
	 * 
	 * @param newSemantics
	 * @return
	 */
	abstract public Category<Y> cloneWithNewSemantics(Y newSemantics);
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		final Category other = (Category) obj;
		if (semantics == null) {
			if (other.semantics != null) {
				return false;
			}
		} else if (!semantics.equals(other.semantics)) {
			return false;
		}
		return true;
	}
	
	/**
	 * tests for equality of syntax without checking if the lambda expressions
	 * are equals used primarily during parsing
	 */
	abstract public boolean equalsNoSem(Object o);
	
	public Y getSem() {
		return semantics;
	}
	
	abstract public Syntax getSyntax();
	
	@Override
	final public int hashCode() {
		if (!hashCodeCalculated) {
			hashCodeCache = calcHashCode();
			hashCodeCalculated = true;
		}
		return hashCodeCache;
	}
	
	// does the full thing match?
	abstract public boolean matches(Category<Y> c);
	
	// does just the syntactic component match?
	abstract public boolean matchesNoSem(Category<Y> c);
	
	abstract public int numSlashes();
	
	private int calcHashCode() {
		if (semantics == null) {
			return 0;
		}
		return syntaxHash() + semantics.hashCode();
	}
	
	abstract protected int syntaxHash();
}
