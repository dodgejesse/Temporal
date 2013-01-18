package edu.uw.cs.lil.tiny.mr.lambda.primitivetypes;

import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

public class IncSimplifier implements IPredicateSimplifier {
	static public final IncSimplifier	INSTANCE	= new IncSimplifier();
	
	private IncSimplifier() {
	}
	
	@Override
	public LogicalExpression simplify(LogicalExpression exp) {
		if (exp instanceof Literal) {
			final Literal literal = (Literal) exp;
			if (literal.getArguments().size() == 1
					&& literal.getArguments().get(0) instanceof LogicalConstant
					&& literal.getArguments().get(0).getType() == LogicLanguageServices.getTypeRepository()
							.getIndexType()) {
				// If we have a single argument and it's a constant of type
				// index, replace it with a new constant
				final int i = LogicLanguageServices
						.indexConstantToInt((LogicalConstant) literal
								.getArguments().get(0));
				return LogicLanguageServices.intToIndexConstant(i + 1);
			}
		}
		return exp;
	}
	
}
