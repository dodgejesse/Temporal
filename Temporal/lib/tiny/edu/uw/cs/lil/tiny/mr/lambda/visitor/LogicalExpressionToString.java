package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Term;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

public class LogicalExpressionToString implements ILogicalExpressionVisitor {
	private final Set<Variable>		definedVariables	= new HashSet<Variable>();
	private final StringBuilder		outputString		= new StringBuilder();
	private final List<Variable>	variablesNamingList	= new LinkedList<Variable>();
	
	private LogicalExpressionToString() {
		// Usage only through static 'of' method.
	}
	
	public static String of(LogicalExpression expression) {
		final LogicalExpressionToString visitor = new LogicalExpressionToString();
		visitor.visit(expression);
		return visitor.getOutputString();
	}
	
	public String getOutputString() {
		return outputString.toString();
	}
	
	@Override
	final public void visit(Lambda lambda) {
		outputString.append("(lambda ");
		lambda.getArgument().accept(this);
		outputString.append(' ');
		lambda.getBody().accept(this);
		outputString.append(')');
	}
	
	@Override
	public void visit(Literal literal) {
		outputString.append("(");
		literal.getPredicate().accept(this);
		// Visit the arguments to print them. Print a space before each
		// argument.
		for (final LogicalExpression argument : literal.getArguments()) {
			outputString.append(' ');
			argument.accept(this);
		}
		outputString.append(')');
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		outputString.append(logicalConstant.getName());
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		outputString.append(getVariableName(variable));
		if (!definedVariables.contains(variable)) {
			outputString.append(Term.TYPE_SEPARATOR);
			outputString.append(variable.getType().getName());
			definedVariables.add(variable);
		}
	}
	
	private String getVariableName(Variable variable) {
		int num = 0;
		for (final Variable namedVariable : variablesNamingList) {
			if (namedVariable == variable) {
				return "$" + String.valueOf(num);
			}
			++num;
		}
		variablesNamingList.add(variable);
		return "$" + String.valueOf(num);
	}
	
}
