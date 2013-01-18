package edu.uw.cs.lil.tiny.mr.language.type;

import edu.uw.cs.lil.tiny.mr.language.type.RecursiveComplexType.Option;

public class ComplexType extends Type {
	public static final char	COMPLEX_TYPE_CLOSE_PAREN		= '>';
	public static final String	COMPLEX_TYPE_CLOSE_PAREN_STR	= String.valueOf(COMPLEX_TYPE_CLOSE_PAREN);
	public static final char	COMPLEX_TYPE_OPEN_PAREN			= '<';
	public static final String	COMPLEX_TYPE_OPEN_PAREN_STR		= String.valueOf(COMPLEX_TYPE_OPEN_PAREN);
	public static final char	COMPLEX_TYPE_SEP				= ',';
	
	private final Type			domain;
	
	private final Type			range;
	
	ComplexType(String label, Type domain, Type range) {
		super(label);
		this.domain = domain;
		this.range = range;
	}
	
	public static String composeString(Type range, Type domain, Option option) {
		return new StringBuilder(20).append(COMPLEX_TYPE_OPEN_PAREN)
				.append(domain.toString())
				.append((option == null ? "" : option.toString()))
				.append(COMPLEX_TYPE_SEP).append(range.toString())
				.append(COMPLEX_TYPE_CLOSE_PAREN).toString();
	}
	
	public static ComplexType create(String label, Type domain, Type range,
			Option option) {
		if (option == null) {
			return new ComplexType(label, domain, range);
		} else {
			return new RecursiveComplexType(label, domain, range, option);
		}
	}
	
	@Override
	public Type getDomain() {
		return domain;
	}
	
	@Override
	public Type getRange() {
		return range;
	}
	
	@Override
	public boolean isArray() {
		return false;
	}
	
	@Override
	public boolean isComplex() {
		return true;
	}
	
	@Override
	public boolean isExtending(Type other) {
		return other != null
				&& (other == this || (domain.isExtending(other.getDomain()) && range
						.isExtending(other.getRange())));
	}
	
	@Override
	public boolean isExtendingOrExtendedBy(Type other) {
		return isExtending(other) || other.isExtending(this);
	}
	
	public boolean isOrderSensitive() {
		return true;
	}
	
	@Override
	public String toString() {
		return composeString(getRange(), getDomain(), null);
	}
	
}
