package edu.uw.cs.lil.tiny.ccg.categories;

/**
 * Category services, such as composition and application.
 * 
 * @author Yoav Artzi
 * @param <Y>
 */
public interface ICategoryServices<Y> {
	
	/**
	 * Apply the function category to the argument category.
	 * 
	 * @param function
	 * @param argument
	 * @return null if the application fails.
	 */
	Category<Y> apply(ComplexCategory<Y> function, Category<Y> argument);
	
	/**
	 * Compose the given categories, so the logical forms will compose to f(g).
	 * 
	 * @param fCategory
	 * @param gCategory
	 * @return null if the composition fails.
	 */
	Category<Y> compose(ComplexCategory<Y> fCategory,
			ComplexCategory<Y> gCategory);
	
	@Override
	boolean equals(Object obj);
	
	/**
	 * Returns an empty category.
	 */
	Category<Y> getEmptyCategory();
	
	/**
	 * Returns a NP category with no semantics.
	 */
	Category<Y> getNounPhraseCategory();
	
	/**
	 * Returns a S category with no semantics.
	 */
	Category<Y> getSentenceCategory();
	
	@Override
	int hashCode();
	
	/**
	 * Given a string representation (single line) parse it into a category.
	 * 
	 * @param string
	 * @return
	 */
	Category<Y> parse(String string);
	
	/**
	 * Parse the semantics from the given string.
	 * 
	 * @param string
	 * @return
	 */
	Y parseSemantics(String string);
}
