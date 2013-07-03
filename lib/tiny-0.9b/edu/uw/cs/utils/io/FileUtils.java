package edu.uw.cs.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class FileUtils {
	/**
	 * Read entire file into a single string, decoding using the default
	 * charset.
	 * <p>
	 * Source:
	 * http://stackoverflow.com/questions/326390/how-to-create-a-java-string
	 * -from-the-contents-of-a-file
	 * </p>
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readFile(File file) throws IOException {
		final FileInputStream stream = new FileInputStream(file);
		try {
			final FileChannel fc = stream.getChannel();
			final MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY,
					0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}
	
}
