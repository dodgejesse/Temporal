package edu.uw.cs.lil.tiny.parser.ccg.model.storage;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;

/**
 * Helper class to carry utilities and services used during decoding.
 * 
 * @author Yoav Artzi
 * @param <Y>
 */
public class DecoderHelper<Y> {
	private final ICategoryServices<Y>	categoryServices;
	
	public DecoderHelper(ICategoryServices<Y> categoryServices) {
		this.categoryServices = categoryServices;
	}
	
	public ICategoryServices<Y> getCategoryServices() {
		return categoryServices;
	}
}
