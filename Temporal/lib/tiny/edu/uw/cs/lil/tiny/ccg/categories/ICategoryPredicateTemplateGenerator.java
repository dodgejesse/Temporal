package edu.uw.cs.lil.tiny.ccg.categories;

import java.util.List;
import java.util.Set;

/**
 * Template to generate categories based on seeds.
 * 
 * @author Yoav Artzi
 * @param <Y>
 *            Type of semantics
 * @param <S>
 *            The type of the element used to instantiate the template.
 */
public interface ICategoryPredicateTemplateGenerator<Y, S> {
	List<Set<Category<Y>>> generateCategories(Set<S> seeds);
}
