package edu.uw.cs.lil.tiny.parser.ccg.rules.coordination;

import edu.uw.cs.lil.tiny.ccg.categories.syntax.ComplexSyntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Slash;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;

class SyntaxCoordinationServices {
	
	private SyntaxCoordinationServices() {
		// Not instantiation
	}
	
	public static Syntax getCoordinationType(Syntax syntax) {
		if (syntax instanceof ComplexSyntax) {
			final ComplexSyntax complexSyntax = (ComplexSyntax) syntax;
			if (complexSyntax.getLeft().equals(Syntax.C)
					&& complexSyntax.getSlash().equals(Slash.VERTICAL)) {
				return complexSyntax.getRight();
			}
		}
		return null;
	}
	
	/**
	 * 'true' iff the input syntax is a coordination of the type argument. If
	 * type == null, return true/false without typing constraint.
	 * 
	 * @param syntax
	 * @param type
	 * @return
	 */
	public static boolean isCoordinationOfType(Syntax syntax, Syntax type) {
		final Syntax coordinationType = getCoordinationType(syntax);
		if (coordinationType == null) {
			return false;
		} else {
			return type == null || type.equals(coordinationType);
		}
	}
}
