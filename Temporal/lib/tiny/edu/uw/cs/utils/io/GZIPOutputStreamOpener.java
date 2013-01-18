package edu.uw.cs.utils.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Open GZIP files for output.
 * 
 * @author Yoav Artzi
 */
public class GZIPOutputStreamOpener implements IOutputStreamOpener {
	
	private final File	f;
	
	public GZIPOutputStreamOpener(File f) {
		this.f = f;
	}
	
	@Override
	public OutputStream open() throws FileNotFoundException, IOException {
		return new GZIPOutputStream(new FileOutputStream(f));
	}
	
}
