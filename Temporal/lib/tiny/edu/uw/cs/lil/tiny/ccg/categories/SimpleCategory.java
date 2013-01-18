package edu.uw.cs.lil.tiny.ccg.categories;

import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax.SimpleSyntax;
import edu.uw.cs.utils.assertion.Assert;

public class SimpleCategory<Y> extends Category<Y> {
	
	/** The name of this atomic category */
	private final SimpleSyntax	syntax;
	
	public SimpleCategory(SimpleSyntax syntax, Y semantics) {
		super(semantics);
		Assert.ifNull(syntax);
		this.syntax = syntax;
	}
	
	@Override
	public Category<Y> cloneWithNewSemantics(Y newSemantics) {
		return new SimpleCategory<Y>(syntax, newSemantics);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		final SimpleCategory other = (SimpleCategory) obj;
		if (syntax == null) {
			if (other.syntax != null) {
				return false;
			}
		} else if (!syntax.equals(other.syntax)) {
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equalsNoSem(Object o) {
		return o instanceof SimpleCategory
				&& ((SimpleCategory) o).syntax.equals(syntax);
	}
	
	@Override
	public Syntax getSyntax() {
		return syntax;
	}
	
	@Override
	public boolean matches(Category<Y> ot) {
		return equals(ot);
	}
	
	@Override
	public boolean matchesNoSem(Category<Y> o) {
		return equalsNoSem(o);
	}
	
	@Override
	public int numSlashes() {
		return 0;
	}
	
	@Override
	public String toString() {
		if (getSem() == null) {
			return syntax.toString();
		} else {
			return syntax + " : " + getSem();
		}
	}
	
	@Override
	protected int syntaxHash() {
		return syntax.hashCode();
	}
	
}
