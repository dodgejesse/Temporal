package edu.uw.cs.lil.tiny.mr.lambda.exec.tabular;

import java.util.Map;

import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

public interface IExecutionServices {
	void augmentTableWithConstant(LogicalConstant constant, Table table);
	
	void augmentTableWithLiteral(Literal literal, Table table);
	
	void augmentTableWithVariable(Variable variable, Table table);
	
	/**
	 * Called just before a variable is removed from the table. Should be used
	 * to update other row values that depend on the variable.
	 * 
	 * @param var
	 * @param row
	 */
	void removingVariable(Variable var, Map<LogicalExpression, Object> row);
}
