package edu.uw.cs.lil.tiny.mr.lambda;

import edu.uw.cs.lil.tiny.mr.language.type.Type;

/**
 * An interface to create a type verifier. This object can be used to verify
 * type matching between arguments, for example.
 * 
 * @author Yoav Artzi
 */
public interface ITypeComparator {
	
	boolean verifyArgType(Type signatureType, Type argType);
	
}
