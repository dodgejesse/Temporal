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
 * Replaces all the occurrences of the given sub-expression with the replacement
 * expression.
 * 
 * @author Yoav Artzi
 */
public class ReplaceExpression implements ILogicalExpressionVisitor {
	
	private final BooleanComparator<LogicalExpression>	comparator;
	private final LogicalExpression						replacement;
	private final LogicalExpression						subExp;
	private LogicalExpression							tempReturn	= null;
	
	/**
	 * Usage only through 'of' static method.
	 * 
	 * @param subExp
	 * @param relacement
	 * @param useInstanceComparison
	 */
	private ReplaceExpression(LogicalExpression subExp,
			LogicalExpression relacement, boolean useInstanceComparison) {
		this.subExp = subExp;
		this.replacement = relacement;
		if (useInstanceComparison) {
			this.comparator = new InstanceComparator();
		} else {
			this.comparator = new ContentComparator();
		}
	}
	
	public static void main(String[] args) {
		// TODO [yoav] [test]
		
		final String expDir = "experiments/comm-longs13";
		final String expDataDir = expDir + "/data_neu";
		
		LogicLanguageServices
				.setInstance(new LogicLanguageServices(new TypeRepository(
						new File(expDataDir + "/comm_longs13.types")), "n"));
		
		final LogicalExpression l1 = LogicalExpression
				.parse("(lambda $1 <e,t> (lambda $0 e (and:t (fr:t $0 bos:ci) (to:t $0 la:ci) (atime:t $0 (around:tm 1800:tm)) ($1 $0))))",
						LogicLanguageServices.getTypeRepository(),
						LogicLanguageServices.getTypeComparator());
		final LogicalExpression l2 = LogicalExpression.parse(
				"(lambda $0 e (sg:t $0))",
				LogicLanguageServices.getTypeRepository(),
				LogicLanguageServices.getTypeComparator());
		
		System.out.println("l1 = " + l1);
		System.out.println("l2 = " + l2);
		System.out.println("l1(l2) = "
				+ ReplaceExpression.of(((Lambda) l1).getBody(),
						((Lambda) l1).getArgument(), l2));
		System.out.println("l1 --> l2 = " + ReplaceExpression.of(l1, l1, l2));
		System.out.println("wellTyped? "
				+ IsWellTyped.of(ReplaceExpression.of(((Lambda) l1).getBody(),
						((Lambda) l1).getArgument(), l2)));
		
	}
	
	public static LogicalExpression of(LogicalExpression exp,
			LogicalExpression subExp, LogicalExpression replacement) {
		return of(exp, subExp, replacement, false);
	}
	
	public static LogicalExpression of(LogicalExpression exp,
			LogicalExpression subExp, LogicalExpression replacement,
			boolean useInstanceComparison) {
		final ReplaceExpression visitor = new ReplaceExpression(subExp,
				replacement, useInstanceComparison);
		visitor.visit(exp);
		return visitor.getResult();
	}
	
	public LogicalExpression getResult() {
		return tempReturn;
	}
	
	@Override
	public void visit(Lambda lambda) {
		if (comparator.compare(subExp, lambda)) {
			tempReturn = replacement;
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
		if (comparator.compare(subExp, literal)) {
			tempReturn = replacement;
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
		if (comparator.compare(subExp, logicalConstant)) {
			tempReturn = replacement;
		} else {
			tempReturn = logicalConstant;
		}
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		if (comparator.compare(subExp, variable)) {
			tempReturn = replacement;
		} else {
			tempReturn = variable;
		}
	}
	
	private static interface BooleanComparator<E> {
		public boolean compare(E o1, E o2);
	}
	
	private static class ContentComparator implements
			BooleanComparator<LogicalExpression> {
		@Override
		public boolean compare(LogicalExpression o1, LogicalExpression o2) {
			return o1.equals(o2);
		}
	}
	
	private static class InstanceComparator implements
			BooleanComparator<LogicalExpression> {
		@Override
		public boolean compare(LogicalExpression o1, LogicalExpression o2) {
			return o1 == o2;
		}
	}
	
}
