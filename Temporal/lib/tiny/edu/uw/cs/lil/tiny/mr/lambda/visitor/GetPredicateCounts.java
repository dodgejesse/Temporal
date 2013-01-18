package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;
import edu.uw.cs.utils.counter.Counter;

/**
 * Create map of predicate types to counts.
 * 
 * @author Yoav Artzi
 */
public class GetPredicateCounts implements ILogicalExpressionVisitor {
	private final Map<LogicalConstant, Counter>	predicates	= new HashMap<LogicalConstant, Counter>();
	
	private GetPredicateCounts() {
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
		System.out.println(GetPredicateCounts.of(l1));
	}
	
	public static Map<LogicalConstant, Counter> of(LogicalExpression exp) {
		final GetPredicateCounts visitor = new GetPredicateCounts();
		visitor.visit(exp);
		return visitor.getPredicates();
	}
	
	public Map<LogicalConstant, Counter> getPredicates() {
		return predicates;
	}
	
	@Override
	public void visit(Lambda lambda) {
		lambda.getBody().accept(this);
	}
	
	@Override
	public void visit(Literal literal) {
		if (literal.getPredicate() instanceof LogicalConstant) {
			if (!predicates.containsKey(literal.getPredicate())) {
				predicates.put((LogicalConstant) literal.getPredicate(),
						new Counter());
			}
			predicates.get(literal.getPredicate()).inc();
		}
		
		literal.getPredicate().accept(this);
		for (final LogicalExpression arg : literal.getArguments()) {
			arg.accept(this);
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
