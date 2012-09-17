package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.rules.IUnaryParseRule;
import edu.uw.cs.lil.tiny.parser.ccg.rules.ParseRuleResult;

import java.util.ArrayList;
import java.util.Collection;

public class SentenceCompilation
  implements IUnaryParseRule<LogicalExpression>
{
  private final ICategoryServices<LogicalExpression> categoryServices;

  public SentenceCompilation(ICategoryServices<LogicalExpression> categoryServices)
  {
    this.categoryServices = categoryServices;
  }

  public Collection<ParseRuleResult<LogicalExpression>> apply(Category<LogicalExpression> category)
  {
		final Collection<ParseRuleResult<LogicalExpression>> list = new ArrayList<ParseRuleResult<LogicalExpression>>();
		list.add(new ParseRuleResult<LogicalExpression>("shift_to_s", categoryServices
				.getSentenceCategory().cloneWithNewSemantics(category.getSem())));
				return list;
	  //return 
      //ListUtils.createSingletonList(new ParseRuleResult(
      //"to_s", this.categoryServices.getSentenceCategory()
      //.cloneWithNewSemantics((LogicalExpression)category.getSem())));
  }
}

