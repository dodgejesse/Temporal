package edu.uw.cs.lil.temporal.predicates;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.uw.cs.lil.temporal.types.TemporalDate;

public class TemporalIntersect extends TemporalPredicate
{
	public TemporalDate perform()
	{
		if ((this.first == null) || (this.second == null))
			return null;
		Map<String, Set<Integer>> intersectedDate = new HashMap<String, Set<Integer>>();
		addStuff((TemporalDate)this.first, intersectedDate);
		addStuff((TemporalDate)this.second, intersectedDate);
		return new TemporalDate(intersectedDate);
	}

	public String toString()
	{
		String s = "TemporalPredicate with up to two dates:\n";
		s = s + this.first + "\n";
		s = s + this.second;
		return s;
	}

	private void addStuff(TemporalDate d, Map<String, Set<Integer>> iDate) {
		for (String s : d.getKeys())
			if (iDate.containsKey(s))
				(iDate.get(s)).addAll(d.getVal(s));
			else
				iDate.put(s, d.getVal(s));
	}
}

