package edu.uw.cs.lil.tiny.tempeval.predicates;

import edu.uw.cs.lil.tiny.tempeval.types.TemporalDuration;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalISO;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalNumber;

public class TemporalMultiplication extends TemporalPredicate {

	public TemporalDuration perform() {
		if (first == null || second == null)
			return null;
		checkInput();
		TemporalNumber temporalNum = (TemporalNumber)second;

		String baseDuration = "";
		for (String key : first.getKeys()){
			baseDuration = key;
		}
		int newNum = temporalNum.getNum();
		// Doesn't actually multiply! 
		if (TemporalISO.getValueFromDate(first, baseDuration) > 0)
			newNum *= TemporalISO.getValueFromDate(first, baseDuration);
		return new TemporalDuration(baseDuration, newNum);
	}

	private void checkInput(){
		// This first 'if' is in case of temporal phrases like "two" in the sentence "two or three days". "months" is the most common of durations, 
		// but "days" is the example in the training set. 
		if (first instanceof TemporalNumber){
			this.second = this.first;
			this.first = new TemporalDuration("day");
		} else if (first.getFullMapping().keySet().size() > 1)
			throw new IllegalArgumentException("Tryngi to multiply a number against a duration with more than one item set.");
		else if (!(first instanceof TemporalDuration))
			throw new IllegalArgumentException("The first element in TemporalMultiply isn't a TemporalDuration.");
		else if (!(second instanceof TemporalNumber))
			throw new IllegalArgumentException("The second element in TemporalMultiply isn't a TemporalNumber");
	}
}
