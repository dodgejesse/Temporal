package edu.uw.cs.lil.tiny.learn.weakp.loss;

import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;

/**
 * Represents a conjunction between a list of validators.
 * 
 * @author Yoav Artzi
 * @param <Y>
 */
public class CompositeValidator<Y> implements IValidator<Y> {
	
	private final List<IValidator<Y>>	validators;
	
	public CompositeValidator(List<IValidator<Y>> validators) {
		this.validators = validators;
	}
	
	@Override
	public boolean isValid(IDataItem<Sentence> dataItem, Y label) {
		for (final IValidator<Y> validator : validators) {
			if (!validator.isValid(dataItem, label)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(CompositeValidator.class.getName()).append(
				validators).toString();
	}
}
