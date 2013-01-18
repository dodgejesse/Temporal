package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.language.type.Type;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;

/**
 * Verifies the typing of the expression. Function input should fit their
 * signature. For the higher level types, types are considered compatible if
 * either one is the parent of the other or they equal.
 * 
 * @author Yoav Artzi
 */
public class IsWellTyped implements ILogicalExpressionVisitor {
	/**
	 * Usually we don't see many variables, so initializing this map to be
	 * relatively small.
	 */
	private final Map<Variable, Type>	variableTypes	= new HashMap<Variable, Type>(
																4);
	private boolean						wellTyped		= true;
	
	private IsWellTyped() {
		// Usage only through static 'of' method.
	}
	
	public static void main(String[] args) {
		// TODO [yoav] [test]
		final String expDir = "experiments/comm-longs13-lucent";
		final String expDataDir = expDir + "/data";
		
		LogicLanguageServices
				.setInstance(new LogicLanguageServices(new TypeRepository(
						new File(expDataDir + "/comm_longs13.types")), "n"));
		
		final LogicalExpression l1 = LogicalExpression
				.parse("(lambda $0:e (and:t (sg:t (i:e $0 0:ind)) (fr:t (i:e $0 0:ind) duluth:ci) (to:t (i:e $0 0:ind) chicago:ci) (bef:t (i:e $0 0:ind) (i:e $0 1:ind)) (sg:t (i:e $0 1:ind)) (fr:t (i:e $0 1:ind) chicago:ci) (to:t (i:e $0 1:ind) new_orleans:ci) (bef:t (i:e $0 1:ind) (i:e $0 2:ind)) (sg:t (i:e $0 2:ind)) (return:t (i:e $0 2:ind)) (to:t $0 duluth:ci)))",
						LogicLanguageServices.getTypeRepository(),
						LogicLanguageServices.getTypeComparator());
		System.out.println(IsWellTyped.of(l1) + " :: " + l1);
		
		final LogicalExpression l2 = LogicalExpression.parse(
				"(lambda $0:e (and:t (sg:t $0) (to:t $0 boston:ci)))",
				LogicLanguageServices.getTypeRepository(),
				LogicLanguageServices.getTypeComparator());
		System.out.println(IsWellTyped.of(l2) + " :: " + l2);
		
		final LogicalExpression l3 = LogicalExpression.parse(
				"(lambda $0:e (and:t (ci:t $0) (to:t $0 boston:ci)))",
				LogicLanguageServices.getTypeRepository(),
				LogicLanguageServices.getTypeComparator());
		System.out.println(IsWellTyped.of(l3) + " :: " + l3);
		
		final LogicalExpression l4 = LogicalExpression
				.parse("(lambda $0:e (and:t (dtime:t $0 no_pref:e) (airline:t $0 baw:al)))",
						LogicLanguageServices.getTypeRepository(),
						LogicLanguageServices.getTypeComparator());
		System.out.println(IsWellTyped.of(l4) + " :: " + l4);
	}
	
	public static boolean of(LogicalExpression exp) {
		final IsWellTyped visitor = new IsWellTyped();
		visitor.visit(exp);
		return visitor.wellTyped;
	}
	
	@Override
	public void visit(Lambda lambda) {
		// Add the variable to the mapping, if it existed, the expression is not
		// well typed
		wellTyped = wellTyped
				&& variableTypes.put(lambda.getArgument(), lambda.getArgument()
						.getType()) == null;
		wellTyped = wellTyped
				&& lambda.getComplexType().getRange() == lambda.getBody()
						.getType();
		if (wellTyped) {
			lambda.getBody().accept(this);
		}
		// Remove the variable from the mapping, since we are leaving its scope
		variableTypes.remove(lambda.getArgument());
	}
	
	@Override
	public void visit(Literal literal) {
		literal.getPredicate().accept(this);
		// Check the arguments match the type of the function
		Type currentDomain = literal.getPredicateType().getDomain();
		Type currentRange = literal.getPredicateType().getRange();
		for (final LogicalExpression arg : literal.getArguments()) {
			// Visit the argument
			arg.accept(this);
			
			// Verify that the signature allows this argument
			
			// Case we don't expect this argument
			wellTyped = wellTyped && currentDomain != null;
			
			// Match the type of the argument with the signature type
			wellTyped = wellTyped && verifyLiteralArgTyping(arg, currentDomain);
			
			if (!wellTyped) {
				return;
			}
			currentDomain = currentRange.getDomain();
			currentRange = currentRange.getRange();
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		wellTyped = wellTyped && logicalConstant.getType() != null;
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		// Nothing to do
	}
	
	/**
	 * Verify that an argument matches the type of the signature in a literal.
	 * 
	 * @param arg
	 * @param signatureType
	 * @return
	 */
	private boolean verifyLiteralArgTyping(LogicalExpression arg,
			Type signatureType) {
		if (arg instanceof LogicalConstant) {
			// Case of a logical constant: require the argument type to extend
			// the signature type.
			return arg.getType().isExtendingOrExtendedBy(signatureType);
		} else if (arg instanceof Variable) {
			// Case variable: check according to historical type and allow more
			// flexibility.
			return verifyVariableType((Variable) arg, signatureType);
		} else if (signatureType.isArray()) {
			// If this is not a variable or a constant, and the signature
			// expects an array, the argument must have a type that is an array
			return arg.getType().isArray()
					&& arg.getType().isExtendingOrExtendedBy(signatureType);
		} else {
			// Case a more complex structure: allow more flexibility than in the
			// case of simple constants.
			return arg.getType().isExtendingOrExtendedBy(signatureType);
		}
	}
	
	/**
	 * Verifies consistency between a variable and its usage in a literal. If
	 * the variable's historic type is unknown, it's a free variable -- treat it
	 * like a constant. Therefore, this method shouldn't be called when
	 * encountering the variables definition, but only its usage.
	 * 
	 * @param variable
	 * @param signatureType
	 * @return
	 */
	private boolean verifyVariableType(Variable variable, Type signatureType) {
		// If we didn't see the variable by now, something is wrong
		final Type historicaltype = variableTypes.get(variable);
		if (historicaltype == null) {
			// Case not historical type, treat like a constant -- check it's
			// extending or extended by. Also, add to variableTypes. This
			// variable will never be removed from types, since its scoping is
			// global.
			variableTypes.put(variable, variable.getType());
			return variable.getType().isExtendingOrExtendedBy(signatureType);
		} else {
			if (signatureType.isExtending(historicaltype)) {
				// Case the current signature type is a narrower instance of the
				// historical type, so remember it and return 'true'
				variableTypes.put(variable, signatureType);
				return true;
			} else {
				// Return 'true' if the signature type is a narrower instance of
				// the
				// historical type, so just return 'true'
				return historicaltype.isExtending(signatureType);
			}
		}
	}
	
}
