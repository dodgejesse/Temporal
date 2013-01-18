package edu.uw.cs.lil.tiny.parser.ccg.model.storage;

import java.io.File;
import java.io.IOException;

/**
 * Decoder to store and load objects from the file system.
 * 
 * @author Yoav Artzi
 * @param <C>
 */
public interface IDecoder<C> {
	/**
	 * Decode an object from the file system.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	C decode(File file) throws IOException;
	
	/**
	 * Encode an object to the file system.
	 * 
	 * @param object
	 * @param file
	 * @throws IOException
	 */
	void encode(C object, File file) throws IOException;
}
