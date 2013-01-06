package edu.uw.cs.lil.tiny.tempeval;

public class TemporalMultiplication extends TemporalPredicate {

	public TemporalDuration perform() {
		checkInput();
		TemporalNumber temporalNum = (TemporalNumber)second;

		String baseDuration = "";
		for (String key : first.getKeys()){
			baseDuration = key;
		}
		
		return new TemporalDuration(baseDuration, temporalNum.getNum());
		
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
