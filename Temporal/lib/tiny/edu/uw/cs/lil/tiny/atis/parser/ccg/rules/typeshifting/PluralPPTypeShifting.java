package edu.uw.cs.lil.tiny.atis.parser.ccg.rules.typeshifting;

import java.util.ArrayList;
import java.util.List;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.language.type.Type;
import edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.basic.AbstractTypeShiftingFunctionForThreading;
import edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.basic.PluralTypeShifting;

/**
 * PP : f:<e,t> => NP (a:<<e,t>,e> f)
 * 
 * @author Luke Zettlemoyer
 */
public class PluralPPTypeShifting extends
		AbstractTypeShiftingFunctionForThreading {
	
	private final LogicalConstant	aPred;
	private final Type				ET;
	
	public PluralPPTypeShifting(
			ICategoryServices<LogicalExpression> categoryServices) {
		ET = LogicLanguageServices.getTypeRepository().getTypeCreateIfNeeded(
				"<e,t>");
		aPred = (LogicalConstant) categoryServices
				.parseSemantics("a:<<e,t>,e>");
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof PluralTypeShifting;
	}
	
	@Override
	public int hashCode() {
		return PluralTypeShifting.class.getName().hashCode();
	}
	
	@Override
	public Category<LogicalExpression> typeRaise(
			Category<LogicalExpression> category) {
		if (category.getSyntax().equals(Syntax.PP)
				&& category.getSem().getType().isExtendingOrExtendedBy(ET)) {
			final List<LogicalExpression> args = new ArrayList<LogicalExpression>(
					1);
			args.add(category.getSem());
			final LogicalExpression aLit = new Literal(aPred, args,
					LogicLanguageServices.getTypeComparator(),
					LogicLanguageServices.getTypeRepository());
			return Category.create(Syntax.PP, aLit);
		}
		return null;
	}
	
}
