package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;

/**
 * @author Yoav Artzi
 */
@Deprecated
public class LogicalExpressionToLatex implements ILogicalExpressionVisitor {
	// TODO [yoav] [latex] Re-do the latex printer properly
	private static final String		VARIABLE_NAMES[]	= { "x", "y", "z", "u",
			"v", "k", "l", "n", "m"					};
	private final Set<Variable>		definedVariables	= new HashSet<Variable>();
	private boolean					inLambdaArgument;
	private final StringBuilder		outputString		= new StringBuilder();
	private final List<Variable>	variablesNamingList	= new LinkedList<Variable>();
	
	private LogicalExpressionToLatex() {
		// Usage only through static 'of' method.
	}
	
	public static void main(String[] args) {
		// TODO [yoav] [test]
		
		final String expDir = "experiments/comm-longs13-lucent";
		final String expDataDir = expDir + "/data";
		
		LogicLanguageServices
				.setInstance(new LogicLanguageServices(new TypeRepository(
						new File(expDataDir + "/comm_longs13.types")), "n"));
		
		System.out.println(LogicalExpressionToLatex.of(LogicalExpression.parse(
				"(lambda $3:e (and:t $0:t $1:t $0))",
				LogicLanguageServices.getTypeRepository(),
				LogicLanguageServices.getTypeComparator())));
		
	}
	
	public static String of(LogicalExpression expression) {
		final LogicalExpressionToLatex visitor = new LogicalExpressionToLatex();
		visitor.visit(expression);
		return visitor.getOutputString();
	}
	
	private static String stringToLatex(String syntax) {
		return syntax.replace("\\", "\\backslash ").replace("_", "\\_");
	}
	
	public String getOutputString() {
		return outputString.toString();
	}
	
	@Override
	final public void visit(Lambda lambda) {
		outputString.append("\\lambda ");
		inLambdaArgument = true;
		lambda.getArgument().accept(this);
		inLambdaArgument = false;
		outputString.append(".");
		lambda.getBody().accept(this);
	}
	
	@Override
	public void visit(Literal literal) {
		if (LogicLanguageServices.isCoordinationPredicate(literal
				.getPredicate())) {
			final String predOp;
			if (literal.getPredicate().equals(
					LogicLanguageServices.getConjunctionPredicate())) {
				predOp = " \\land ";
			} else if (literal.getPredicate().equals(
					LogicLanguageServices.getDisjunctionPredicate())) {
				predOp = " \\lor ";
			} else {
				throw new IllegalStateException("unknown coordination operator");
			}
			// Visit the arguments to print them
			final Iterator<? extends LogicalExpression> iterator = literal
					.getArguments().iterator();
			while (iterator.hasNext()) {
				iterator.next().accept(this);
				if (iterator.hasNext()) {
					outputString.append(predOp);
				}
			}
		} else if (LogicLanguageServices.isArrayIndexPredicate(literal
				.getPredicate())) {
			literal.getArguments().get(0).accept(this);
			outputString.append("[");
			literal.getArguments().get(1).accept(this);
			outputString.append("]");
		} else {
			literal.getPredicate().accept(this);
			outputString.append("(");
			// Visit the arguments to print them
			final Iterator<? extends LogicalExpression> iterator = literal
					.getArguments().iterator();
			while (iterator.hasNext()) {
				iterator.next().accept(this);
				if (iterator.hasNext()) {
					outputString.append(", ");
				}
			}
			outputString.append(')');
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		if (logicalConstant.getType().isComplex()) {
			outputString.append(stringToLatex(logicalConstant.getName()));
		} else if (LogicLanguageServices.getTypeRepository().getIndexType()
				.equals(logicalConstant.getType())) {
			outputString.append(LogicLanguageServices
					.indexConstantToInt(logicalConstant));
		} else {
			outputString.append("\\mbox{{\\it ")
					.append(stringToLatex(logicalConstant.getName()))
					.append("}}");
		}
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		outputString.append(getVariableName(variable));
		if (variable.getType().isArray() && inLambdaArgument) {
			outputString.append("[]");
		}
		if (!definedVariables.contains(variable)) {
			definedVariables.add(variable);
		}
	}
	
	private String getVariableName(Variable variable) {
		int num = 0;
		for (final Variable namedVariable : variablesNamingList) {
			if (namedVariable == variable) {
				return VARIABLE_NAMES[num];
			}
			++num;
		}
		variablesNamingList.add(variable);
		return VARIABLE_NAMES[num];
	}
	
}
