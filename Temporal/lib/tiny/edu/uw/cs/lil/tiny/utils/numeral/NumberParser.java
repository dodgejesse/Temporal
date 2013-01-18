package edu.uw.cs.lil.tiny.utils.numeral;

import java.io.File;

import normalization.BiuNormalizer;

/**
 * Numeral parser wrapping the BIU Normalizer.
 * 
 * @author Yoav Artzi
 */
public class NumberParser {
	final private BiuNormalizer	biuNormalizer;
	
	public NumberParser(File rulesDirectory) throws Exception {
		biuNormalizer = new BiuNormalizer(rulesDirectory);
	}
	
	public static void main(String[] args) {
		try {
			final NumberParser parser = new NumberParser(new File(
					"resources/biu-normalizer-rules"));
			System.out.println(parser.parse("nineteenth"));
			System.out.println(parser.parse("eighth"));
			System.out.println(parser.parse("twenty eighth"));
			System.out.println(parser.parse("twenty twenty two"));
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String parse(String numString) throws Exception {
		return biuNormalizer.normalize(numString);
	}
}
