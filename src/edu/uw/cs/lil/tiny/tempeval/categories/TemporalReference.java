package edu.uw.cs.lil.tiny.tempeval.categories;

import java.util.Map;
import java.util.Set;

public class TemporalReference extends TemporalPredicate{
	
	private TemporalISO previous;

	public TemporalReference(TemporalISO prev){
		previous = prev;
	}
	
	
	public TemporalISO perform(){
		if (previous == null || first.isFullySpecified() || !(first instanceof TemporalDuration) || notSameFields()){
//			System.out.println();
//			System.out.println("first");
//			System.out.println(first);
//			System.out.println();
//			System.out.println("second");
//			System.out.println(second);
//			System.out.println();
			return new TemporalDate("year", 0);
			//return first;
		}

		
		// TODO: Test here if first is a fully specified calendar date. If so, return it.
		// TODO: Test to see if going back or forward by a day changes the month, etc.
		
		Map<String, Set<Integer>> tmpMap = previous.getFullMapping();
		
		// For each thing set in first, add it's value to previous.
		for (String s : first.getKeys()){
			int firstValue = TemporalISO.getValueFromDate(first, s);
			int prevValue = TemporalISO.getValueFromDate(previous, s);
			if (firstValue + prevValue != 0){
				tmpMap.get(s).remove(prevValue);
				tmpMap.get(s).add(firstValue + prevValue);
			}
		}
		
//		System.out.println("TemporalRefenece time!");
//		System.out.println("First argument: " );
//		System.out.println(first);
//		System.out.println("Second argument: " );
//		System.out.println(second);
//		System.out.println();
//		System.out.println("The output from this call: ");
//		System.out.println(new TemporalDate(tmpMap));
		return new TemporalDate(tmpMap);
	}
	
	 private boolean notSameFields(){
		 for (String s : first.getKeys()){
			 if (!previous.isSet(s))
				 return true;
		 }
		 return false;
	 }
}
