package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.language.type.Type;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;

/**
 * Infers types for all of the subexpressions. The inferred types are the most
 * specific type that could hold for each expression (variable, etc). Note: the
 * types can be null if there is no valid type for a subexpression.
 * 
 * @author Luke Zettlemoyer
 */
public class InferSubexpressionTypes implements ILogicalExpressionVisitor {
	/**
	 * Usually we don't see many subexpressions, so initializing this map to be
	 * relatively small.
	 */
	private final Map<LogicalExpression, Type>	expressionTypes	= new HashMap<LogicalExpression, Type>(
																		10);
	private final TypeRepository				typeRepository;
	
	private InferSubexpressionTypes(TypeRepository typeRepository) {
		// Usage only through static 'of' method.
		this.typeRepository = typeRepository;
	}
	
	public static void main(String[] args) {
		// TODO [yoav] [test]
		
		// Data directories
		final String expDir = "experiments/geo880-lambda";
		final String expDataDir = expDir + "/data";
		
		// Init the logical expression type system
		LogicLanguageServices.setInstance(new LogicLanguageServices(
				new TypeRepository(new File(expDataDir + "/geo-lambda.types")),
				"n"));
		// CCG LogicalExpression category services for handling categories
		// with LogicalExpression as semantics
		
		final LogicalExpression l1 = LogicalExpression
				.parse("(argmax:e (lambda $0:e (state:t $0)) (lambda $1:e (count:i (lambda $2:e (and:t (river:t $2) (loc:t $2 $1))))))",
						LogicLanguageServices.getTypeRepository(),
						LogicLanguageServices.getTypeComparator());
		System.out.println(l1 + " : ");
		for (final Map.Entry<LogicalExpression, Type> pair : of(l1,
				LogicLanguageServices.getTypeRepository()).entrySet()) {
			System.out.println(pair.getKey() + " = " + pair.getValue());
		}
		
	}
	
	public static Map<LogicalExpression, Type> of(LogicalExpression exp,
			TypeRepository typeRepository) {
		final InferSubexpressionTypes visitor = new InferSubexpressionTypes(
				typeRepository);
		visitor.visit(exp);
		return visitor.getTypes();
	}
	
	public Map<LogicalExpression, Type> getTypes() {
		return expressionTypes;
	}
	
	@Override
	public void visit(Lambda lambda) {
		expressionTypes.put(lambda.getArgument(), lambda.getArgument()
				.getType());
		lambda.getBody().accept(this);
		
		final Type bodyType = expressionTypes.get(lambda.getBody());
		final Type argType = expressionTypes.get(lambda.getArgument());
		if (bodyType == null || argType == null) {
			expressionTypes.put(lambda, null);
		} else {
			final Type newType = typeRepository.getTypeCreateIfNeeded(bodyType,
					argType);
			expressionTypes.put(lambda, newType);
		}
	}
	
	@Override
	public void visit(Literal literal) {
		literal.getPredicate().accept(this);
		
		// Check the arguments match the type of the function
		final Set<Variable> firstArgVars = new HashSet<Variable>();
		
		// Case we have a composite list of arguments
		Type currentDomain = literal.getPredicateType().getDomain();
		Type currentRange = literal.getPredicateType().getRange();
		for (final LogicalExpression arg : literal.getArguments()) {
			// Visit the argument, to get its type into the expressionTypes map
			arg.accept(this);
			
			// Match the type of the argument and that of the signature
			final Type newType = findCommonSubType(expressionTypes.get(arg),
					currentDomain);
			currentDomain = currentRange.getDomain();
			currentRange = currentRange.getRange();
			// Update the type of the argument with what was found when the
			// signature type was taken into account
			expressionTypes.put(arg, newType);
			
			// special case for argmax, etc.
			if (arg instanceof Lambda) {
				firstArgVars.add(((Lambda) arg).getArgument());
			}
		}
		
		// Add the current literal to typing list
		expressionTypes.put(literal, literal.getType().getRange());
		
		// TODO [yoav] [withluke] [posttyping] Let's fix this
		// this is a hack for argmax, etc. should think it through later...
		if (firstArgVars.size() > 0) {
			Type t = firstArgVars.iterator().next().getType();
			for (final Variable v : firstArgVars) {
				t = findCommonSubType(t, expressionTypes.get(v));
			}
			for (final Variable v : firstArgVars) {
				expressionTypes.put(v, t);
			}
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		expressionTypes.put(logicalConstant, logicalConstant.getType());
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		// Nothing to do
	}
	
	private Type findCommonSubType(Type t1, Type t2) {
		if (t1 == null || t2 == null) {
			return null;
		}
		if (t2.isExtending(t1)) {
			return t2;
		}
		if (t1.isExtending(t2)) {
			return t1;
		}
		return null;
	}
	
}
