package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import edu.uw.cs.lil.tiny.mr.IMeaningRepresentationVisitor;
import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

public interface ILogicalExpressionVisitor extends
		IMeaningRepresentationVisitor {
	
	void visit(Lambda lambda);
	
	void visit(Literal literal);
	
	void visit(LogicalConstant logicalConstant);
	
	void visit(LogicalExpression logicalExpression);
	
	void visit(Variable variable);
	
}
