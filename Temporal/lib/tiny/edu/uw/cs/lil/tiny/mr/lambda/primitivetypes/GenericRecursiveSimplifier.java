package edu.uw.cs.lil.tiny.mr.lambda.primitivetypes;

import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

/**
 * Generic simplifier for literals with recursive predicates that support
 * folding. Maintains order, so can be used for both order-sensitive and
 * order-insensitive predicates.
 * 
 * @author Yoav Artzi
 */
public class GenericRecursiveSimplifier implements IPredicateSimplifier {
	
	static public final GenericRecursiveSimplifier	INSTANCE	= new GenericRecursiveSimplifier();
	
	private GenericRecursiveSimplifier() {
		// Access through static INSTANCE
	}
	
	@Override
	public LogicalExpression simplify(LogicalExpression exp) {
		boolean expChanged = false;
		if (exp instanceof Literal) {
			final Literal literal = (Literal) exp;
			// Consolidate arguments from all sub expressions that have the same
			// predicate
			final List<LogicalExpression> consolidatedArgs = new LinkedList<LogicalExpression>();
			for (final LogicalExpression arg : literal.getArguments()) {
				if (arg instanceof Literal
						&& ((Literal) arg).getPredicateType() == literal
								.getPredicateType()) {
					consolidatedArgs.addAll(((Literal) arg).getArguments());
					expChanged = true;
				} else {
					consolidatedArgs.add(arg);
				}
			}
			
			if (expChanged) {
				return new Literal(literal.getPredicate(), consolidatedArgs,
						LogicLanguageServices.getTypeComparator(),
						LogicLanguageServices.getTypeRepository());
			} else {
				return exp;
			}
		} else {
			return exp;
		}
	}
	
}
