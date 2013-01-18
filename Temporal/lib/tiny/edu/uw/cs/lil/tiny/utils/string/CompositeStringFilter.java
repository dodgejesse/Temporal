package edu.uw.cs.lil.tiny.utils.string;

import java.util.List;


/**
 * Combines a few string filters that are executed sequentially.
 * 
 * @author Yoav Artzi
 */
public class CompositeStringFilter implements IStringFilter {
	
	private final List<IStringFilter>	filters;
	
	public CompositeStringFilter(List<IStringFilter> filters) {
		this.filters = filters;
	}
	
	@Override
	public String filter(String str) {
		String ret = str;
		for (final IStringFilter filter : filters) {
			ret = filter.filter(ret);
		}
		return ret;
	}
}
