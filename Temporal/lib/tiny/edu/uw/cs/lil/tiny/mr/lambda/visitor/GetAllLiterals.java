package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

/**
 * Get all the literals from an expression of a specific arity
 * 
 * @author Yoav Artzi
 */
public class GetAllLiterals implements ILogicalExpressionVisitor {
	private final Integer	arity;
	final List<Literal>		literals	= new LinkedList<Literal>();
	
	/**
	 * Usage only through static 'of' method
	 * 
	 * @param arity
	 */
	private GetAllLiterals(Integer arity) {
		this.arity = arity;
		
	}
	
	public static List<Literal> of(LogicalExpression exp) {
		return of(exp, null);
	}
	
	public static List<Literal> of(LogicalExpression exp, Integer arity) {
		final GetAllLiterals visitor = new GetAllLiterals(arity);
		visitor.visit(exp);
		return visitor.getLiterals();
	}
	
	public List<Literal> getLiterals() {
		return literals;
	}
	
	@Override
	public void visit(Lambda lambda) {
		lambda.getBody().accept(this);
	}
	
	@Override
	public void visit(Literal literal) {
		if (arity == null || literal.getArguments().size() == arity) {
			literals.add(literal);
		}
		for (final LogicalExpression exp : literal.getArguments()) {
			exp.accept(this);
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		// Nothing to do
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		// Nothing to do
	}
	
}
