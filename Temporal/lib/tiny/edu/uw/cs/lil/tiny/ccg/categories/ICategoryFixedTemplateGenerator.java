package edu.uw.cs.lil.tiny.ccg.categories;

import java.util.Set;

/**
 * Template to generate categories without input.
 * 
 * @author Yoav Artzi
 * @param <Y>
 *            Type of semantics
 */
public interface ICategoryFixedTemplateGenerator<Y> {
	Set<Category<Y>> generateCategories();
}
