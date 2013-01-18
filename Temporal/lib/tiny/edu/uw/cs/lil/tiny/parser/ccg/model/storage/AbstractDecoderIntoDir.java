package edu.uw.cs.lil.tiny.parser.ccg.model.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * Objects supports storing into a directory. Storing requires multiple files.
 * 
 * @author Yoav Artzi
 */
public abstract class AbstractDecoderIntoDir<C> extends AbstractDecoder<C> {
	private static final ILogger	LOG					= LoggerFactory
																.create(AbstractDecoderIntoDir.class);
	
	protected static final String	ATTRIBUTE_FILE_NAME	= "attributes";
	
	protected AbstractDecoderIntoDir(Class<?> targetClass) {
		super(targetClass);
	}
	
	/**
	 * Extracts the class name from a file name, given the suffix.
	 * 
	 * @param filename
	 * @param suffix
	 * @return
	 */
	protected static String getClassName(String filename, String suffix) {
		if (!filename.endsWith(suffix)) {
			return null;
		} else {
			// Get the class name from the file name by stripping the suffix
			return filename.substring(0, filename.length() - suffix.length());
		}
	}
	
	@Override
	public final C decode(File file) throws IOException {
		
		// Verify that the given file is a directory
		if (!file.isDirectory()) {
			throw new IllegalArgumentException(
					"Model must be loaded from a directory");
		}
		final File dir = file;
		
		// Read the attributes file into a map
		final Map<String, String> attributes = readValueAttributeHeader(new BufferedReader(
				new FileReader(new File(dir, ATTRIBUTE_FILE_NAME))));
		
		// Decode the actual data and create the object
		return decodeFromDir(attributes, dir);
	}
	
	@Override
	public final void encode(C object, File file) throws IOException {
		LOG.info("Decoding using %s", this.getClass().getName());
		
		// Create the directory if needed
		if (!file.isDirectory() && !file.isFile()) {
			file.mkdirs();
		}
		
		// Verify that the given file is a directory
		if (!file.isDirectory()) {
			throw new IllegalArgumentException(
					"Model storage must be a directory");
		}
		final File dir = file;
		
		// Write attributes file
		final BufferedWriter writer = new BufferedWriter(new FileWriter(
				new File(dir, ATTRIBUTE_FILE_NAME)));
		writeHeader(createAttributesMap(object), writer);
		writer.close();
		
		// Write data to files
		writeFiles(object, dir);
		
		LOG.info("Decoding %s completed", this.getClass().getName());
	}
	
	protected abstract C decodeFromDir(Map<String, String> attributes, File dir)
			throws IOException;
	
	protected abstract void writeFiles(C object, File dir) throws IOException;
	
}
