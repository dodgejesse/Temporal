package edu.uw.cs.lil.tiny.ccg.categories.syntax;

public class ComplexSyntax extends Syntax {
	
	private final int		hashCode;
	
	private final Syntax	left;
	
	private final int		numSlahes;
	
	private final Syntax	right;
	private final Slash		slash;
	/**
	 * String representation of the object. The string is computed on
	 * instantiation to avoid recursion. This representation is complete, and
	 * therefore it's used for equals().
	 */
	private final String	string;
	
	public ComplexSyntax(Syntax left, Syntax right, Slash slash) {
		this.left = left;
		this.right = right;
		this.numSlahes = left.numSlashes() + right.numSlashes() + 1;
		this.slash = slash;
		this.hashCode = calcHashCode();
		this.string = computeSyntaxString(left, right, slash);
	}
	
	private static String computeSyntaxString(Syntax left, Syntax right,
			Slash slash) {
		final StringBuilder ret = new StringBuilder();
		if (left instanceof ComplexSyntax) {
			ret.append("(").append(left).append(")");
		} else {
			ret.append(left);
		}
		ret.append(slash);
		if (right instanceof ComplexSyntax) {
			ret.append("(").append(right).append(")");
		} else {
			ret.append(right);
		}
		return ret.toString();
	}
	
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
		final ComplexSyntax other = (ComplexSyntax) obj;
		if (string == null) {
			if (other.string != null) {
				return false;
			}
		} else if (!string.equals(other.string)) {
			return false;
		}
		return true;
	}
	
	public Syntax getLeft() {
		return left;
	}
	
	public Syntax getRight() {
		return right;
	}
	
	public Slash getSlash() {
		return slash;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public int numSlashes() {
		return numSlahes;
	}
	
	@Override
	public String toString() {
		return string;
	}
	
	private int calcHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		result = prime * result + ((slash == null) ? 0 : slash.hashCode());
		return result;
	}
	
}
