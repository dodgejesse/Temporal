package edu.uw.cs.utils.io;

import java.io.IOException;
import java.io.InputStream;

public class CombinedInputStream extends InputStream {
	
	private boolean				finishedFirst;
	private final InputStream	in1;
	private final InputStream	in2;
	
	public CombinedInputStream(InputStream in1, InputStream in2) {
		this.in1 = in1;
		this.in2 = in2;
	}
	
	@Override
	public int read() throws IOException {
		if (finishedFirst) {
			return in2.read();
		} else {
			final int ret = in1.read();
			
			if (ret == -1) {
				finishedFirst = true;
				
				return in2.read();
			} else {
				return ret;
			}
			
		}
	}
	
}
