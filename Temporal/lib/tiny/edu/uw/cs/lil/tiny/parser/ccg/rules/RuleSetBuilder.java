package edu.uw.cs.lil.tiny.parser.ccg.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.ITypeShiftingFunction;
import edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.TypeShiftingRule;
import edu.uw.cs.utils.composites.Pair;

/**
 * Makes it easier to collect rules and generate the desired combinations. Takes
 * a set of binary rules {@link IBinaryParseRule} and a set of unary functions
 * {@link ITypeShiftingFunction} and creates all combinations of operating any
 * type-shifting rule and then the binary rule. Also includes just operating any
 * binary rule on the raw categories (i.e., without type-shifting).
 * 
 * @author Yoav Artzi
 */
public class RuleSetBuilder<Y> {
	
	private final Set<IBinaryParseRule<Y>>						binaryRules			= new HashSet<IBinaryParseRule<Y>>();
	private final Set<Pair<String, ITypeShiftingFunction<Y>>>	shiftingFunctions	= new HashSet<Pair<String, ITypeShiftingFunction<Y>>>();
	
	public RuleSetBuilder() {
	}
	
	public RuleSetBuilder<Y> add(IBinaryParseRule<Y> rule) {
		binaryRules.add(rule);
		return this;
	}
	
	public RuleSetBuilder<Y> add(String name, ITypeShiftingFunction<Y> function) {
		shiftingFunctions.add(Pair.of(name, function));
		return this;
	}
	
	public BinaryRulesSet<Y> build() {
		final List<IBinaryParseRule<Y>> rules = new ArrayList<IBinaryParseRule<Y>>();
		final BinaryRulesSet<Y> baseRules = new BinaryRulesSet<Y>(
				new ArrayList<IBinaryParseRule<Y>>(binaryRules));
		
		// Binary rules on their own
		rules.add(baseRules);
		
		// Type shifting rules, in combination with base rules
		for (final Pair<String, ITypeShiftingFunction<Y>> pair : shiftingFunctions) {
			rules.add(new TypeShiftingRule<Y>(pair.first(), pair.second(),
					baseRules));
		}
		
		return new BinaryRulesSet<Y>(rules);
	}
	
}
