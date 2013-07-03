package edu.uw.cs.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Open GZIP stream for input.
 * 
 * @author Yoav Artzi
 */
public class GZIPInputStreamOpener implements IInputStreamOpener {
	
	private final File	f;
	
	public GZIPInputStreamOpener(File f) {
		this.f = f;
	}
	
	@Override
	public InputStream open() throws FileNotFoundException, IOException {
		return new GZIPInputStream(new FileInputStream(f));
	}
	
}
