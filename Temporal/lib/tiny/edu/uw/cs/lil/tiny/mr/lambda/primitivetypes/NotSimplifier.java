package edu.uw.cs.lil.tiny.mr.lambda.primitivetypes;

import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

/**
 * Simplifier for 'not:t' predicate.
 * 
 * @author Yoav Artzi
 */
public class NotSimplifier implements IPredicateSimplifier {
	static public final NotSimplifier	INSTANCE	= new NotSimplifier();
	
	private NotSimplifier() {
	}
	
	@Override
	public LogicalExpression simplify(LogicalExpression exp) {
		if (exp instanceof Literal) {
			// If the argument is a literal with 'not:t' predicate, return the
			// argument for the inner literal
			final Literal literal = (Literal) exp;
			
			// If we have more than one argument, don't do anything, this
			// expression is one bad apple
			if (literal.getArguments().size() == 1) {
				if (literal.getArguments().get(0) instanceof Literal
						&& ((Literal) literal.getArguments().get(0))
								.getPredicate() == literal.getPredicate()) {
					// Case the only argument is a 'not:t' literal, so return
					// its
					// single argument
					final Literal subNot = (Literal) literal.getArguments()
							.get(0);
					if (subNot.getArguments().size() == 1) {
						return subNot.getArguments().get(0);
					}
				} else if (literal.getArguments().get(0) == LogicLanguageServices
						.getTrue()) {
					// If the single argument is 'true:t' constant, return
					// 'false:t'
					return LogicLanguageServices.getFalse();
				} else if (literal.getArguments().get(0) == LogicLanguageServices
						.getFalse()) {
					// If the single argument is 'false:t' constant, return
					// 'true:t'
					return LogicLanguageServices.getTrue();
				}
				
			}
			// Case didn't change anything
			return exp;
		} else {
			return exp;
		}
	}
}
