package edu.uw.cs.lil.tiny.geoquery;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

public class GeoGenericExperiment {
	
	public static void main(String[] args) {
		run(args[0]);
	}
	
	public static void run(String filename) {
		try {
			new GeoExperiment(new File(filename)).start();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final SAXException e) {
			e.printStackTrace();
		}
	}
	
}
