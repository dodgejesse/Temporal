package edu.uw.cs.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Normal files input stream opener.
 * 
 * @author Yoav Artzi
 */
public class FileInputStreamOpener implements IInputStreamOpener {
	
	private final File	f;
	
	public FileInputStreamOpener(File f) {
		this.f = f;
	}
	
	@Override
	public InputStream open() throws FileNotFoundException, IOException {
		return new FileInputStream(f);
	}
	
}
