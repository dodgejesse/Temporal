package edu.uw.cs.lil.tiny.mr.lambda;

import java.util.Map;

import edu.uw.cs.lil.tiny.mr.language.type.Type;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;

/**
 * Logical expression term.
 * 
 * @author Yoav Artzi
 */
public abstract class Term extends LogicalExpression {
	public static final String	TYPE_SEPARATOR	= ":";
	private final Type			type;
	
	public Term(Type type) {
		this.type = type;
	}
	
	protected static Term parse(String string, Map<String, Variable> variables,
			TypeRepository typeRepository, ITypeComparator typeComparator,
			boolean lockOntology) {
		if (string.startsWith(Variable.PREFIX)) {
			return Variable.parse(string, variables, typeRepository);
		} else {
			return LogicalConstant.parse(string, typeRepository, lockOntology);
		}
	}
	
	@Override
	public int calcHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		// No need for variable mapping at this level
		return equals(obj, null);
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	protected boolean doEquals(Object obj,
			Map<Variable, Variable> variablesMapping) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Term other = (Term) obj;
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}
}
