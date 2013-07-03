package edu.uw.cs.lil.tiny.atis;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

public class AtisDev {
	
	public static void main(String[] args) {
		run("experiments/dev/dev.exp");
	}
	
	public static void run(String filename) {
		try {
			new AtisExperiment(new File(filename)).start();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final SAXException e) {
			e.printStackTrace();
		}
	}
	
}
