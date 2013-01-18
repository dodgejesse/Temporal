package edu.uw.cs.lil.tiny.mr.lambda;

import edu.uw.cs.lil.tiny.mr.language.type.Type;

/**
 * Flexible type comparison. Allow the usage of an argument even if it's not the
 * hierarchy of the signature type, but they still have a common parent.
 * 
 * @author Yoav Artzi
 */
public class FlexibleTypeComparator implements ITypeComparator {
	
	@Override
	public boolean verifyArgType(Type signatureType, Type argType) {
		// TODO [yoav] [literaltyping] Decide what to enforce here
		return true;
		// return argType.isExtendingOrExtendedBy(signatureType);
	}
	
}
