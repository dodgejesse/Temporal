package edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.scorers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.Lexeme;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.AbstractDecoderIntoFile;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.IDecoder;
import edu.uw.cs.utils.collections.IScorer;

/**
 * Lexeme scorer that takes the number of tokens into account.
 * 
 * @author Luke Zettlemoyer
 * @param <Y>
 */
public class ExpLengthLexemeScorer implements IScorer<Lexeme> {
	
	private final double	baseScore;
	private final double	exponent;
	
	public ExpLengthLexemeScorer(double baseScore, double exponent) {
		this.baseScore = baseScore;
		this.exponent = exponent;
	}
	
	public static IDecoder<ExpLengthLexemeScorer> getDecoder() {
		return new Decoder();
	}
	
	@Override
	public double score(Lexeme lex) {
		return baseScore * Math.pow(lex.getTokens().size(), exponent);
	}
	
	private static class Decoder extends
			AbstractDecoderIntoFile<ExpLengthLexemeScorer> {
		
		private static final int	VERSION	= 1;
		
		protected Decoder() {
			super(ExpLengthLexemeScorer.class);
		}
		
		@Override
		public int getVersion() {
			return VERSION;
		}
		
		@Override
		protected Map<String, String> createAttributesMap(
				ExpLengthLexemeScorer object) {
			final Map<String, String> attrbiutes = new HashMap<String, String>();
			attrbiutes.put("baseScore", Double.toString(object.baseScore));
			attrbiutes.put("exponent", Double.toString(object.exponent));
			return attrbiutes;
		}
		
		@Override
		protected ExpLengthLexemeScorer doDecode(
				Map<String, String> attributes,
				Map<String, File> dependentFiles, BufferedReader reader)
				throws IOException {
			final double baseScore = Double
					.valueOf(attributes.get("baseScore"));
			final double exponent = Double.valueOf(attributes.get("exponent"));
			
			return new ExpLengthLexemeScorer(baseScore, exponent);
		}
		
		@Override
		protected void doEncode(ExpLengthLexemeScorer object,
				BufferedWriter writer) throws IOException {
			// Nothing to write
		}
		
		@Override
		protected Map<String, File> encodeDependentFiles(
				ExpLengthLexemeScorer object, File directory, File parentFile)
				throws IOException {
			// No dependent files
			return new HashMap<String, File>();
		}
		
	}
}
