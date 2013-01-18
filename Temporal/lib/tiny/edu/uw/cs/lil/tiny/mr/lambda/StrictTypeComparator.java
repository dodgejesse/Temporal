package edu.uw.cs.lil.tiny.mr.lambda;

import edu.uw.cs.lil.tiny.mr.language.type.Type;

/**
 * Compare types. Allow only classical typing system as used in object oriented
 * programming. A type maybe used instead of another only if it extends the
 * other.
 * 
 * @author Yoav Artzi
 */
public class StrictTypeComparator implements ITypeComparator {
	
	@Override
	public boolean verifyArgType(Type signatureType, Type argType) {
		return argType.isExtending(signatureType);
	}
	
}
