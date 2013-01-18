package edu.uw.cs.lil.tiny.mr.lambda.primitivetypes;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

/**
 * Simplifier for or:t predicate.
 * 
 * @author Yoav Artzi
 */
public class OrSimplifier implements IPredicateSimplifier {
	static public final OrSimplifier	INSTANCE	= new OrSimplifier();
	
	private OrSimplifier() {
	}
	
	@Override
	public LogicalExpression simplify(LogicalExpression exp) {
		boolean expChanged = false;
		if (exp instanceof Literal) {
			final Literal literal = (Literal) exp;
			// Consolidate arguments from all sub or:t literals
			final List<LogicalExpression> consolidatedArgs = new LinkedList<LogicalExpression>();
			for (final LogicalExpression arg : literal.getArguments()) {
				if (arg instanceof Literal
						&& ((Literal) arg).getPredicate() == literal
								.getPredicate()) {
					expChanged = true;
					consolidatedArgs.addAll(((Literal) arg).getArguments());
				} else {
					consolidatedArgs.add(arg);
				}
			}
			
			// Next: Remove all arguments that equal to 'false:t'. If we only
			// have one argument left, return it. If none left, just return
			// 'false:t'. If we detect a single 'true:t' argument, return
			// 'true:t'.
			
			// Remove all 'false:t' constants and search for 'true:t' constant
			final int originalLength = consolidatedArgs.size();
			final Iterator<LogicalExpression> iterator = consolidatedArgs
					.iterator();
			boolean trueArgExist = false;
			boolean nonTrueFalseExist = false;
			while (iterator.hasNext()) {
				final LogicalExpression arg = iterator.next();
				if (arg.equals(LogicLanguageServices.getFalse())) {
					iterator.remove();
					expChanged = true;
				} else if (arg.equals(LogicLanguageServices.getTrue())) {
					trueArgExist = true;
				} else {
					nonTrueFalseExist = true;
				}
			}
			
			if (trueArgExist) {
				// If we have a single 'true:t' argument, we replace the entire
				// literal with 'true:t' constant
				return LogicLanguageServices.getTrue();
			}
			
			if (consolidatedArgs.size() != originalLength) {
				// Case we removed at least one 'true:t' constant
				if (!nonTrueFalseExist) {
					return LogicLanguageServices.getFalse();
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
