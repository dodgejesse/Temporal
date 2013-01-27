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
import edu.uw.cs.lil.tiny.paser.ccg.rules.lambda.typeshifting.basic.AbstractTypeShiftingFunctionForThreading;
import edu.uw.cs.lil.tiny.paser.ccg.rules.lambda.typeshifting.basic.PluralTypeShifting;

/**
 * AP : f:<ev,t> => AP : (a:<<ev,t>,ev> f)
 * 
 * @author Luke Zettlemoyer
 */
public class PluralAPTypeShifting extends
		AbstractTypeShiftingFunctionForThreading {
	private final LogicalConstant	aPred;
	private final Type				EVT;
	
	public PluralAPTypeShifting(
			ICategoryServices<LogicalExpression> categoryServices) {
		EVT = LogicLanguageServices.getTypeRepository().getTypeCreateIfNeeded(
				"<ev,t>");
		aPred = (LogicalConstant) categoryServices
				.parseSemantics("a:<<ev,t>,ev>");
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
		if (category.getSyntax().equals(Syntax.AP)
				&& category.getSem().getType().isExtendingOrExtendedBy(EVT)) {
			final List<LogicalExpression> args = new ArrayList<LogicalExpression>(
					1);
			args.add(category.getSem());
			final LogicalExpression aLit = new Literal(aPred, args,
					LogicLanguageServices.getTypeComparator(),
					LogicLanguageServices.getTypeRepository());
			return Category.create(Syntax.AP, aLit);
		}
		return null;
	}
}
