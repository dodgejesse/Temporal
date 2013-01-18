package edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary;

import java.util.Collections;
import java.util.List;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.ComplexCategory;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Slash;
import edu.uw.cs.lil.tiny.parser.ccg.rules.IBinaryParseRule;
import edu.uw.cs.lil.tiny.parser.ccg.rules.ParseRuleResult;
import edu.uw.cs.utils.collections.ListUtils;

/**
 * An abstract rule for logical application.
 * 
 * @author Luke Zettlemoyer
 * @author Tom Kwiatkowski
 * @author Yoav Artzi
 */
public abstract class AbstractApplication<Y> implements IBinaryParseRule<Y> {
	private final ICategoryServices<Y>	categoryServices;
	private final String				ruleName;
	
	public AbstractApplication(String ruleName,
			ICategoryServices<Y> categoryServices) {
		this.ruleName = ruleName;
		this.categoryServices = categoryServices;
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
		final AbstractApplication other = (AbstractApplication) obj;
		if (categoryServices == null) {
			if (other.categoryServices != null) {
				return false;
			}
		} else if (!categoryServices.equals(other.categoryServices)) {
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
		result = prime
				* result
				+ ((categoryServices == null) ? 0 : categoryServices.hashCode());
		result = prime * result
				+ ((ruleName == null) ? 0 : ruleName.hashCode());
		return result;
	}
	
	@Override
	public boolean isOverLoadable() {
		return true;
	}
	
	/**
	 * Application combination.
	 * 
	 * @param function
	 *            The function cell
	 * @param argument
	 *            The argument cell
	 * @param backward
	 *            'true' if we the application direction is reversed.
	 * @param cellFactory
	 * @param result
	 *            The result cell, if exists, will be added to this list
	 */
	protected List<ParseRuleResult<Y>> doApplication(Category<Y> function,
			Category<Y> argument, boolean backward) {
		if (function instanceof ComplexCategory) {
			final ComplexCategory<Y> functionCategory = (ComplexCategory<Y>) function;
			
			// Check direction of function slash
			if (functionCategory.getSlash() == (backward ? Slash.BACKWARD
					: Slash.FORWARD)) {
				// Do application and create new cell
				final Category<Y> result = categoryServices.apply(
						functionCategory, argument);
				if (result != null) {
					return ListUtils
							.createSingletonList(new ParseRuleResult<Y>(
									ruleName, result));
				}
			}
		}
		
		return Collections.emptyList();
	}
}
