package edu.uw.cs.lil.tiny.tempeval;


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
	
	private TemporalVisitor(String ref_time) {
		map = new TemporalMap(ref_time);
	}
	
	public static TemporalISO of(LogicalExpression exp, String ref_time) {
		final TemporalVisitor visitor = new TemporalVisitor(ref_time);
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
			pred = map.findComplexMap(logicalConstant);
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
