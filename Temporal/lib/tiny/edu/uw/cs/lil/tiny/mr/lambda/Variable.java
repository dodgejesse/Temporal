package edu.uw.cs.lil.tiny.mr.lambda;

import java.util.Map;

import edu.uw.cs.lil.tiny.mr.lambda.visitor.ILogicalExpressionVisitor;
import edu.uw.cs.lil.tiny.mr.language.type.Type;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * Lambda calculus variable.
 * 
 * @author Yoav Artzi
 */
public class Variable extends Term {
	public static final String		PREFIX	= "$";
	private static final ILogger	LOG		= LoggerFactory
													.create(Variable.class);
	
	public Variable(Type type) {
		super(type);
	}
	
	protected static Variable parse(String string,
			Map<String, Variable> variables, TypeRepository typeRepository) {
		try {
			final String[] split = string.split(Term.TYPE_SEPARATOR);
			if (split.length == 2) {
				// Case of variable definition
				final Type type = typeRepository
						.getTypeCreateIfNeeded(split[1]);
				if (type == null) {
					throw new LogicalExpressionRuntimeException(
							"Invalid type for variable: " + string);
				}
				if (variables.containsKey(split[0])) {
					throw new LogicalExpressionRuntimeException(
							"Variable overwrite is not supported, please supply unique names: "
									+ string);
				}
				final Variable variable = new Variable(type);
				variables.put(split[0], variable);
				return variable;
			} else {
				// Case variable reference
				if (variables.containsKey(string)) {
					return variables.get(string);
				} else {
					throw new LogicalExpressionRuntimeException(
							"Undefined variable reference: " + string);
				}
			}
		} catch (final RuntimeException e) {
			LOG.error("Variable error: %s", string);
			throw e;
		}
		
	}
	
	@Override
	public void accept(ILogicalExpressionVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		// Without variable mapping, variables are only equal when they are
		// identical
		return this == obj;
	}
	
	@Override
	protected boolean doEquals(Object obj,
			Map<Variable, Variable> variablesMapping) {
		if (variablesMapping.containsKey(this)) {
			// Comparison through mapping of variables
			return variablesMapping.get(this) == obj;
		}
		
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}
		
		final Variable other = (Variable) obj;
		
		if (!variablesMapping.containsKey(this)
				&& !variablesMapping.values().contains(other)
				&& other.getType().equals(getType())) {
			// Case 'this' is a free variable, and 'other' is a free variable,
			// both of them are not mapped, add a mapping between them and
			// return true
			variablesMapping.put(this, other);
			return true;
		}
		
		return false;
	}
}
