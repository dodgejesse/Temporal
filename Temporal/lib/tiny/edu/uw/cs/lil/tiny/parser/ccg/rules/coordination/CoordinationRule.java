package edu.uw.cs.lil.tiny.parser.ccg.rules.coordination;

import java.util.ArrayList;
import java.util.List;

import edu.uw.cs.lil.tiny.parser.ccg.rules.BinaryRulesSet;
import edu.uw.cs.lil.tiny.parser.ccg.rules.IBinaryParseRule;

public class CoordinationRule<Y> extends BinaryRulesSet<Y> {
	
	private CoordinationRule(List<IBinaryParseRule<Y>> rules) {
		super(rules);
	}
	
	public static <Y> CoordinationRule<Y> create(
			ICoordinationServices<Y> services) {
		final List<IBinaryParseRule<Y>> rules = new ArrayList<IBinaryParseRule<Y>>(
				3);
		
		rules.add(new C1Rule<Y>(services));
		rules.add(new C2Rule<Y>(services));
		rules.add(new CXRule<Y>(services));
		
		return new CoordinationRule<Y>(rules);
	}
	
}
