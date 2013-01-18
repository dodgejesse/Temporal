package edu.uw.cs.lil.tiny.mr.lambda.primitivetypes;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

/**
 * Simplifier for and:t predicate.
 * 
 * @author Yoav Artzi
 */
public class AndSimplifier implements IPredicateSimplifier {
	static public final AndSimplifier	INSTANCE	= new AndSimplifier();
	
	private AndSimplifier() {
	}
	
	@Override
	public LogicalExpression simplify(LogicalExpression exp) {
		boolean expChanged = false;
		if (exp instanceof Literal) {
			final Literal literal = (Literal) exp;
			// Consolidate arguments from all sub and:t literals
			final List<LogicalExpression> consolidatedArgs = new LinkedList<LogicalExpression>();
			for (final LogicalExpression arg : literal.getArguments()) {
				if (arg instanceof Literal
						&& ((Literal) arg).getPredicate() == literal
								.getPredicate()) {
					consolidatedArgs.addAll(((Literal) arg).getArguments());
					expChanged = true;
				} else {
					consolidatedArgs.add(arg);
				}
			}
			
			// Next: Replace all arguments equal to 'true:t' with
			// a single 'true:t' and remove it if we have more than two
			// arguments. If we have a single arguments that is equal to
			// 'false:t' return the constant 'false:t' instead of the entire
			// literal
			
			// Remove all 'true:t' constants and search for 'false:t' constant
			final int originalLength = consolidatedArgs.size();
			final Iterator<LogicalExpression> iterator = consolidatedArgs
					.iterator();
			boolean falseArgExist = false;
			boolean nonTrueFalseExist = false;
			while (iterator.hasNext()) {
				final LogicalExpression arg = iterator.next();
				if (arg.equals(LogicLanguageServices.getTrue())) {
					iterator.remove();
					expChanged = true;
				} else if (arg.equals(LogicLanguageServices.getFalse())) {
					falseArgExist = true;
				} else {
					nonTrueFalseExist = true;
				}
			}
			
			if (falseArgExist) {
				// If we have a single false argument, we replace the entire
				// literal with 'false:t' constant
				return LogicLanguageServices.getFalse();
			}
			
			if (consolidatedArgs.size() != originalLength) {
				// Case we removed at least one 'true:t' constant
				if (!nonTrueFalseExist) {
					return LogicLanguageServices.getTrue();
				} else if (consolidatedArgs.size() < 2) {
					// If we have less than 2 arguments, just return the
					// argument
					return consolidatedArgs.get(0);
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
