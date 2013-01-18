package edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting;

import edu.uw.cs.lil.tiny.ccg.categories.Category;

public interface ITypeShiftingFunction<Y> {
	@Override
	boolean equals(Object obj);
	
	@Override
	int hashCode();
	
	Category<Y> typeRaise(Category<Y> category);
}
