package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.io.File;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;

/**
 * Returns the head string of the expression. This is a single string value.
 * 
 * @author Yoav Artzi
 */
public class GetHeadString implements ILogicalExpressionVisitor {
	private static final String	VARIABLE_HEAD_STRING	= "var";
	private String				headString				= null;
	
	private GetHeadString() {
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
		System.out.println(GetHeadString.of(l1));
	}
	
	public static String of(LogicalExpression exp) {
		final GetHeadString visitor = new GetHeadString();
		visitor.visit(exp);
		return visitor.getHeadString();
	}
	
	public String getHeadString() {
		return headString;
	}
	
	public void setHeadString(String headString) {
		this.headString = headString;
	}
	
	@Override
	public void visit(Lambda lambda) {
		lambda.getBody().accept(this);
	}
	
	@Override
	public void visit(Literal literal) {
		literal.getPredicate().accept(this);
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		headString = logicalConstant.getName();
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		headString = VARIABLE_HEAD_STRING;
	}
}
