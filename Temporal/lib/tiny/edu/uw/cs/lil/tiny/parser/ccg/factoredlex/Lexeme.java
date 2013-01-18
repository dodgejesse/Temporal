package edu.uw.cs.lil.tiny.parser.ccg.factoredlex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpressionRuntimeException;
import edu.uw.cs.lil.tiny.mr.language.type.Type;

public class Lexeme {
	private final List<LogicalConstant>	constants;
	private final String				origin;
	private final List<String>			tokens;
	private final List<Type>			typeSignature;
	
	public Lexeme(List<String> tokens, List<LogicalConstant> constants,
			String origin) {
		this.origin = origin;
		this.constants = Collections.unmodifiableList(constants);
		this.tokens = Collections.unmodifiableList(tokens);
		this.typeSignature = Collections
				.unmodifiableList(getSignature(constants));
	}
	
	public static List<Type> getSignature(List<LogicalConstant> constants) {
		final List<Type> types = new ArrayList<Type>(constants.size());
		for (final LogicalConstant constant : constants) {
			types.add(LogicLanguageServices.getTypeRepository().generalizeType(
					constant.getType()));
		}
		return types;
	}
	
	// public static void main(String[] args) {
	// // TODO [yoav] [test]
	//
	// // Data directories
	// final String expDir = "experiments/geo880-lambda";
	// final String expDataDir = expDir + "/data";
	//
	// // Init the logical expression type system
	// LogicLanguageServices.setInstance(new LogicLanguageServices(
	// new TypeRepository(new File(expDataDir + "/geo-lambda.types")),
	// "i"));
	// // CCG LogicalExpression category services for handling categories
	// // with LogicalExpression as semantics
	// final ICategoryServices<LogicalExpression> categoryServices = new
	// LogicalExpressionCategoryServices();
	//
	// final Lexeme lex = Lexeme.parse("[the, largest]=[argmax:e, size:i]",
	// categoryServices);
	//
	// System.out.println(lex);
	// System.out.println(lex.getTypeSignature());
	//
	// }
	
	/**
	 * Given a string, parse a lexeme from it.
	 * 
	 * @param line
	 * @return
	 */
	public static Lexeme parse(String line,
			ICategoryServices<LogicalExpression> categoryServices, String origin) {
		
		final int equalsIndex = line.indexOf("=");
		final String tokensString = line.substring(1, equalsIndex - 1);
		final String constantsString = line.substring(equalsIndex + 2,
				line.length() - 1);
		
		final List<String> tokens = new LinkedList<String>();
		for (final String token : tokensString.split(", ")) {
			tokens.add(token);
		}
		
		final List<LogicalConstant> constants = new LinkedList<LogicalConstant>();
		if (!constantsString.equals("")) {
			for (final String constant : constantsString.split(", ")) {
				
				final LogicalExpression exp = categoryServices
						.parseSemantics(constant);
				if (!(exp instanceof LogicalConstant)) {
					throw new LogicalExpressionRuntimeException(
							"Not a constant error: " + constant);
				}
				
				constants.add((LogicalConstant) exp);
			}
		}
		return new Lexeme(tokens, constants, origin);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Lexeme)) {
			return false;
		}
		final Lexeme other = (Lexeme) obj;
		if (constants == null) {
			if (other.constants != null) {
				return false;
			}
		} else if (!constants.equals(other.constants)) {
			return false;
		}
		if (tokens == null) {
			if (other.tokens != null) {
				return false;
			}
		} else if (!tokens.equals(other.tokens)) {
			return false;
		}
		return true;
	}
	
	public List<LogicalConstant> getConstants() {
		return constants;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public List<String> getTokens() {
		return tokens;
	}
	
	public List<Type> getTypeSignature() {
		return typeSignature;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constants == null) ? 0 : constants.hashCode());
		result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
		return result;
	}
	
	public boolean matches(List<String> inputTokens) {
		return tokens.equals(inputTokens);
	}
	
	public int numConstants() {
		return constants.size();
	}
	
	@Override
	public String toString() {
		return tokens + "=" + constants;
	}
	
}
