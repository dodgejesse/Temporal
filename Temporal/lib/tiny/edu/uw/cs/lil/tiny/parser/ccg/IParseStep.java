package edu.uw.cs.lil.tiny.parser.ccg;

import edu.uw.cs.lil.tiny.ccg.categories.Category;

/**
 * A single parse step: holds a parent and its children, plus the rule that
 * created them and a full-parse flag.
 * 
 * @author Luke Zettlemoyer
 * @param <Y>
 */
public interface IParseStep<Y> {
	Category<Y> getChild(int i);
	
	Category<Y> getRoot();
	
	String getRuleName();
	
	boolean isFullParse();
	
	int numChildren();
}
