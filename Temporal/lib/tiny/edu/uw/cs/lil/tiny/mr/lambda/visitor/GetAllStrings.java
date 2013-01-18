package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;

/**
 * Creates a list of all strings in an expression, except the ones describing
 * variables and the 'lambda' operator. The list will contain duplicates.
 * 
 * @author Yoav Artzi
 */
public class GetAllStrings implements ILogicalExpressionVisitor {
	private final List<String>	constantStrings	= new LinkedList<String>();
	
	private GetAllStrings() {
		// Usage only through static 'of' method.
	}
	
	public static void main(String[] args) {
		// TODO [yoav] [test]
		final String expDir = "experiments/comm-longs13";
		final String expDataDir = expDir + "/data_neu";
		
		LogicLanguageServices
				.setInstance(new LogicLanguageServices(new TypeRepository(
						new File(expDataDir + "/comm_longs13.types")), "n"));
		
		final LogicalExpression l1 = LogicalExpression
				.parse("(lambda $1 <e,t> (lambda $0 e (and:t (fr:t $0 bos:ci) (to:t $0 bos:ci) (atime:t $0 (around:tm 1800:tm)) ($1 $0))))",
						LogicLanguageServices.getTypeRepository(),
						LogicLanguageServices.getTypeComparator());
		System.out.println(GetAllStrings.of(l1));
	}
	
	public static List<String> of(LogicalExpression exp) {
		final GetAllStrings visitor = new GetAllStrings();
		visitor.visit(exp);
		return visitor.getConstantStrings();
	}
	
	public List<String> getConstantStrings() {
		return constantStrings;
	}
	
	@Override
	public void visit(Lambda lambda) {
		lambda.getBody().accept(this);
	}
	
	@Override
	public void visit(Literal literal) {
		literal.getPredicate().accept(this);
		for (final LogicalExpression arg : literal.getArguments()) {
			arg.accept(this);
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		constantStrings.add(logicalConstant.getName());
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
