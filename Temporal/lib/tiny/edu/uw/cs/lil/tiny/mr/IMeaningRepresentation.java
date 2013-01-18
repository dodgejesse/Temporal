package edu.uw.cs.lil.tiny.mr;

/**
 * Meaning representation is compositional and hierarchical.
 * 
 * @author Yoav Artzi
 */
public interface IMeaningRepresentation<T extends IMeaningRepresentationVisitor> {
	public void accept(T visitor);
}
