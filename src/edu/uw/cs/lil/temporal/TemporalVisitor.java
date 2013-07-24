package edu.uw.cs.lil.temporal;


import edu.uw.cs.lil.temporal.predicates.TemporalPredicate;
import edu.uw.cs.lil.temporal.types.TemporalISO;
import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.ILogicalExpressionVisitor;
import edu.uw.cs.lil.tiny.mr.language.type.Type;

public class TemporalVisitor implements ILogicalExpressionVisitor{
	private TemporalPredicate pred;
	private TemporalISO iso;
	private TemporalMap map;
	private TemporalISO previous;
	
	private TemporalVisitor(String ref_time, TemporalISO prev) {
		map = new TemporalMap(ref_time);
		previous = prev;
	}
	
	public static TemporalISO of(LogicalExpression exp, String ref_time, TemporalISO prev) {
		final TemporalVisitor visitor = new TemporalVisitor(ref_time, prev);
		visitor.visit(exp);
		return visitor.getISO();
	}
	
	public TemporalISO getISO() {
		return iso;
	}
	
	@Override
	public void visit(Lambda lambda) {
		lambda.getArgument().accept(this);
		lambda.getBody().accept(this);		
	}

	@Override
	public void visit(Literal literal) {
		literal.getPredicate().accept(this);
		TemporalPredicate tmpPred = pred;
		for (final LogicalExpression arg : literal.getArguments()) {
			arg.accept(this);
			tmpPred.storeISO(iso);
		}
		iso = tmpPred.perform();		
	}

	@Override
	public void visit(LogicalConstant logicalConstant) {
		final Type type = logicalConstant.getType();
		if (type.isComplex()){
			pred = map.findComplexMap(logicalConstant, previous);
		} else{
			iso = map.findNonComplexMap(logicalConstant);
		}
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
