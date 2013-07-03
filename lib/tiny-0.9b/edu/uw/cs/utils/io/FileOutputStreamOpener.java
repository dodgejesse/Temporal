package edu.uw.cs.utils.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Normal files output stream opener.
 * 
 * @author Yoav Artzi
 */
public class FileOutputStreamOpener implements IOutputStreamOpener {
	
	private final File	f;
	
	public FileOutputStreamOpener(File f) {
		this.f = f;
	}
	
	@Override
	public OutputStream open() throws FileNotFoundException, IOException {
		return new FileOutputStream(f);
	}
	
}
