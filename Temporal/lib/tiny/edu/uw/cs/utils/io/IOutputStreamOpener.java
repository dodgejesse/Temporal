package edu.uw.cs.utils.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Wrapper to open output stream.
 * 
 * @author Yoav Artzi
 */
public interface IOutputStreamOpener {
	OutputStream open() throws FileNotFoundException, IOException;
}
