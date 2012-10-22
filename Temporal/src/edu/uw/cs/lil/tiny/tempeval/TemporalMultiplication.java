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
		if (first.getFullMapping().keySet().size() > 1)
			throw new IllegalArgumentException("Tryngi to multiply a number against a duration with more than one item set.");
		if (!(first instanceof TemporalDuration))
			throw new IllegalArgumentException("The first element in TemporalMultiply isn't a TemporalDuration.");
		if (!(second instanceof TemporalNumber))
			throw new IllegalArgumentException("The second element in TemporalMultiply isn't a TemporalNumber");
		
	}
}
