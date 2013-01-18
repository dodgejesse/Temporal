package edu.uw.cs.lil.tiny.parser.ccg.factoredlex;

import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;

public class FactoredLexiconServices {
	private static FactoredLexiconServices	INSTANCE			= new FactoredLexiconServices();
	
	private final Set<LogicalConstant>		unfactoredConstants	= new HashSet<LogicalConstant>();
	
	private FactoredLexiconServices() {
	}
	
	public static boolean isFactorable(LogicalConstant constant) {
		return INSTANCE.doIsFactorable(constant);
	}
	
	public static void set(Set<LogicalConstant> unfactoredConstants) {
		INSTANCE = new FactoredLexiconServices();
		INSTANCE.addUnfactoredConstants(unfactoredConstants);
	}
	
	private void addUnfactoredConstants(Set<LogicalConstant> constants) {
		unfactoredConstants.addAll(constants);
	}
	
	private boolean doIsFactorable(LogicalConstant constant) {
		return !LogicLanguageServices.isCoordinationPredicate(constant)
				&& !LogicLanguageServices.isArrayIndexPredicate(constant)
				&& !LogicLanguageServices.isArraySubPredicate(constant)
				&& !LogicLanguageServices.getTypeRepository().getIndexType()
						.equals(constant.getType())
				&& !unfactoredConstants.contains(constant);
	}
	
}
