package edu.uw.cs.lil.tiny.atis.parser.ccg.rules.typeshifting;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.ComplexCategory;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.ComplexSyntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Slash;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.rules.ParseRuleResult;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.AbstractApplication;

/**
 * A rule that fills in miss 'that' phrases for that-less relative
 * constructions.
 * <ul>
 * N S\NP --> S or N S/NP --> S
 * </ul>
 * 
 * @author Luke Zettlemoyer
 */
public class ThatlessRelative extends AbstractApplication<LogicalExpression> {
	private static String								RULE_NAME	= "ppcomp";
	
	private final ComplexCategory<LogicalExpression>	workerCategoryBackSlash;
	private final ComplexCategory<LogicalExpression>	workerCategoryForwardSlash;
	
	public ThatlessRelative(
			ICategoryServices<LogicalExpression> categoryServices) {
		super(RULE_NAME, categoryServices);
		workerCategoryForwardSlash = (ComplexCategory<LogicalExpression>) categoryServices
				.parse("N/(S/NP)/N : (lambda $0:<e,t> (lambda $1:<e,t> (lambda $2:e (and:<t*,t> ($0 $2) ($1 $2)))))");
		workerCategoryBackSlash = (ComplexCategory<LogicalExpression>) categoryServices
				.parse("N/(S\\NP)/N : (lambda $0:<e,t> (lambda $1:<e,t> (lambda $2:e (and:<t*,t> ($0 $2) ($1 $2)))))");
	}
	
	@Override
	public Collection<ParseRuleResult<LogicalExpression>> apply(
			Category<LogicalExpression> left,
			Category<LogicalExpression> right, boolean isCompleteSentence) {
		
		// TODO [Yoav] make sure this function can't be applied on top of any
		// unary type shifting rules
		
		if (!(right.getSyntax() instanceof ComplexSyntax)) {
			return Collections.emptyList();
		}
		final ComplexSyntax complexSyntax = (ComplexSyntax) right.getSyntax();
		if (!complexSyntax.getLeft().equals(Syntax.S)
				|| !complexSyntax.getRight().equals(Syntax.NP)) {
			return Collections.emptyList();
		}
		
		ComplexCategory<LogicalExpression> workerCategory;
		if (complexSyntax.getSlash().equals(Slash.FORWARD)) {
			workerCategory = workerCategoryForwardSlash;
		} else {
			workerCategory = workerCategoryBackSlash;
		}
		final List<ParseRuleResult<LogicalExpression>> first = doApplication(
				workerCategory, left, false);
		if (first.size() == 0) {
			return Collections.emptyList();
		}
		return doApplication(first.get(0).getResultCategory(), right, false);
	}
}
