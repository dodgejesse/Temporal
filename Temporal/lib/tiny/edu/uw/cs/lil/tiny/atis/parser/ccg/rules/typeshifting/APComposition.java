package edu.uw.cs.lil.tiny.atis.parser.ccg.rules.typeshifting;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.ComplexCategory;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.rules.ParseRuleResult;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.AbstractApplication;

/**
 * A rule doing composition of PPs
 * <ul>
 * PP PP -> PP
 * </ul>
 * 
 * @author Luke Zettlemoyer
 */
public class APComposition extends AbstractApplication<LogicalExpression> {
	private static String								RULE_NAME	= "apcomp";
	
	private final ComplexCategory<LogicalExpression>	workerCategory;
	
	public APComposition(ICategoryServices<LogicalExpression> categoryServices) {
		super(RULE_NAME, categoryServices);
		workerCategory = (ComplexCategory<LogicalExpression>) categoryServices
				.parse("AP/AP/AP : (lambda $0:<ev,t> (lambda $1:<ev,t> (lambda $2:ev (and:<t*,t> ($0 $2) ($1 $2)))))");
	}
	
	@Override
	public Collection<ParseRuleResult<LogicalExpression>> apply(
			Category<LogicalExpression> left,
			Category<LogicalExpression> right, boolean isCompleteSentence) {
		
		// TODO [Yoav] make sure this function can't be applied on top of any
		// unary type shifting rules
		
		if (!left.getSyntax().equals(Syntax.AP)
				|| !right.getSyntax().equals(Syntax.AP)) {
			return Collections.emptyList();
		}
		
		final List<ParseRuleResult<LogicalExpression>> first = doApplication(
				workerCategory, left, false);
		if (first.size() == 0) {
			return Collections.emptyList();
		}
		return doApplication(first.get(0).getResultCategory(), right, false);
	}
}
