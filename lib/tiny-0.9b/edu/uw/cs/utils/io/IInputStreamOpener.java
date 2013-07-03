package edu.uw.cs.utils.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wrapper to open input stream.
 * 
 * @author Yoav Artzi
 */
public interface IInputStreamOpener {
	InputStream open() throws FileNotFoundException, IOException;
}
