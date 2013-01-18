package edu.uw.cs.lil.tiny.parser.ccg.rules.skipping;

import java.util.Collections;
import java.util.List;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.parser.ccg.rules.IBinaryParseRule;
import edu.uw.cs.lil.tiny.parser.ccg.rules.ParseRuleResult;
import edu.uw.cs.utils.collections.ListUtils;

/**
 * This is an abstract rule used to skip words tagged with an empty category.
 * 
 * @author Yoav Artzi
 */
public abstract class AbstractSkippingRule<Y> implements IBinaryParseRule<Y> {
	
	private final Category<Y>	emptyCategory;
	
	private final String		ruleName;
	
	public AbstractSkippingRule(String ruleName,
			ICategoryServices<Y> categoryServices) {
		this.ruleName = ruleName;
		this.emptyCategory = categoryServices.getEmptyCategory();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		final AbstractSkippingRule other = (AbstractSkippingRule) obj;
		if (emptyCategory == null) {
			if (other.emptyCategory != null) {
				return false;
			}
		} else if (!emptyCategory.equals(other.emptyCategory)) {
			return false;
		}
		if (ruleName == null) {
			if (other.ruleName != null) {
				return false;
			}
		} else if (!ruleName.equals(other.ruleName)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((emptyCategory == null) ? 0 : emptyCategory.hashCode());
		result = prime * result
				+ ((ruleName == null) ? 0 : ruleName.hashCode());
		return result;
	}
	
	@Override
	public boolean isOverLoadable() {
		return false;
	}
	
	protected List<ParseRuleResult<Y>> attemptSkipping(Category<Y> left,
			Category<Y> right, boolean backward) {
		final boolean rightCategoryIsEmpty = right.equals(emptyCategory);
		final boolean leftCategoryIsEmpty = left.equals(emptyCategory);
		
		// Only create new cells if one is empty and the other is not
		if (leftCategoryIsEmpty ^ rightCategoryIsEmpty) {
			if (leftCategoryIsEmpty && backward) {
				// Case left is empty
				return ListUtils.createSingletonList(new ParseRuleResult<Y>(
						ruleName, right));
			} else if (rightCategoryIsEmpty && !backward) {
				// Case right is empty
				return ListUtils.createSingletonList(new ParseRuleResult<Y>(
						ruleName, left));
			}
		}
		
		return Collections.emptyList();
	}
}
