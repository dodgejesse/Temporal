package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;

/**
 * Replaces all the N-th occurrence of the given sub-expression with the
 * replacement expression. The ordering is undefined, but consistent across
 * multiple calls with the same input expression. The indexing starts at 0 (the
 * first instance is position 0).
 * 
 * @author Luke Zettlemoyer
 */
public class ReplaceNthExpression implements ILogicalExpressionVisitor {
	
	private final int				N;
	private int						numTimesSeen	= 0;
	private final LogicalExpression	replacement;
	private final LogicalExpression	subExp;
	private LogicalExpression		tempReturn		= null;
	
	/**
	 * Usage only through 'of' static method.
	 * 
	 * @param subExp
	 * @param relacement
	 * @param N
	 */
	private ReplaceNthExpression(LogicalExpression subExp,
			LogicalExpression relacement, int N) {
		this.numTimesSeen = 0;
		this.subExp = subExp;
		this.N = N + 1; // switch from 0 first index to 1 first index
		this.replacement = relacement;
	}
	
	public static void main(String[] args) {
		// TODO [yoav] [test]
		
		final String expDir = "experiments/geo880-lambda";
		final String expDataDir = expDir + "/data";
		
		LogicLanguageServices.setInstance(new LogicLanguageServices(
				new TypeRepository(new File(expDataDir + "/geo-lambda.types")),
				"n"));
		
		final LogicalExpression l1 = LogicalExpression
				.parse("(lambda $1:<e,t> (lambda $0:e (and:t (loc:t $0 bos:c) (loc:t $0 bos:c) ($1 $0))))",
						LogicLanguageServices.getTypeRepository(),
						LogicLanguageServices.getTypeComparator());
		final LogicalExpression l2 = LogicalExpression.parse("bos:c",
				LogicLanguageServices.getTypeRepository(),
				LogicLanguageServices.getTypeComparator());
		final LogicalExpression l3 = LogicalExpression.parse("seattle:c",
				LogicLanguageServices.getTypeRepository(),
				LogicLanguageServices.getTypeComparator());
		
		System.out.println(l1);
		System.out.println(ReplaceNthExpression.of(l1, l2, l3, 0));
		System.out.println(ReplaceNthExpression.of(l1, l2, l3, 1));
		System.out.println(ReplaceNthExpression.of(l1, l2, l3, 2));
		
	}
	
	public static LogicalExpression of(LogicalExpression exp,
			LogicalExpression subExp, LogicalExpression replacement, int N) {
		final ReplaceNthExpression visitor = new ReplaceNthExpression(subExp,
				replacement, N);
		visitor.visit(exp);
		return visitor.getResult();
	}
	
	public LogicalExpression getResult() {
		return tempReturn;
	}
	
	@Override
	public void visit(Lambda lambda) {
		if (lambda.equals(subExp)) {
			numTimesSeen++;
			if (numTimesSeen == N) {
				tempReturn = replacement;
			} else {
				tempReturn = lambda;
			}
		} else {
			lambda.getArgument().accept(this);
			if (tempReturn == null) {
				return;
			}
			final LogicalExpression newArg = tempReturn;
			lambda.getBody().accept(this);
			if (tempReturn == null) {
				return;
			}
			final LogicalExpression newBody = tempReturn;
			if (newBody == lambda.getBody() && newArg == lambda.getArgument()) {
				tempReturn = lambda;
			} else {
				// Need to check that the new argument is actually a variable,
				// to avoid a runtime exception
				if (newArg instanceof Variable) {
					tempReturn = new Lambda((Variable) newArg, newBody,
							LogicLanguageServices.getTypeRepository());
				} else {
					// Case we don't have a legal expression, just return null
					tempReturn = null;
				}
			}
			
		}
	}
	
	@Override
	public void visit(Literal literal) {
		if (literal.equals(subExp)) {
			numTimesSeen++;
			if (numTimesSeen == N) {
				tempReturn = replacement;
			} else {
				tempReturn = literal;
			}
		} else {
			boolean literalChanged = false;
			// Visit the predicate
			literal.getPredicate().accept(this);
			final LogicalExpression newPredicate;
			if (tempReturn == literal.getPredicate()) {
				newPredicate = literal.getPredicate();
			} else {
				if (tempReturn == null) {
					return;
				}
				newPredicate = tempReturn;
				literalChanged = true;
			}
			// Go over the arguments
			final List<LogicalExpression> args = new ArrayList<LogicalExpression>(
					literal.getArguments().size());
			for (final LogicalExpression arg : literal.getArguments()) {
				arg.accept(this);
				if (tempReturn == null) {
					return;
				}
				final LogicalExpression newArg = tempReturn;
				if (newArg == arg) {
					args.add(arg);
				} else {
					args.add(newArg);
					literalChanged = true;
				}
			}
			if (literalChanged) {
				tempReturn = new Literal(newPredicate, args,
						LogicLanguageServices.getTypeComparator(),
						LogicLanguageServices.getTypeRepository());
			} else {
				tempReturn = literal;
			}
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		if (logicalConstant.equals(subExp)) {
			numTimesSeen++;
			if (numTimesSeen == N) {
				tempReturn = replacement;
				return;
			}
		}
		tempReturn = logicalConstant;
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		if (variable.equals(subExp)) {
			numTimesSeen++;
			if (numTimesSeen == N) {
				tempReturn = replacement;
				return;
			}
		}
		tempReturn = variable;
	}
	
}
