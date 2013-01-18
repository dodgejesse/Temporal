package edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.AbstractDecoderIntoFile;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.IDecoder;
import edu.uw.cs.utils.collections.IScorer;

/**
 * Lexical entry scorer that takes the number of tokens into account.
 * 
 * @author Yoav Artzi
 * @param <Y>
 */
public class ExpLengthLexicalEntryScorer<Y> implements IScorer<LexicalEntry<Y>> {
	
	private final double	coef;
	private final double	exponent;
	
	public ExpLengthLexicalEntryScorer(double coef, double exponent) {
		this.coef = coef;
		this.exponent = exponent;
	}
	
	public static <Y> IDecoder<ExpLengthLexicalEntryScorer<Y>> getDecoder() {
		return new Decoder<Y>();
	}
	
	@Override
	public double score(LexicalEntry<Y> lex) {
		return coef * Math.pow(lex.getTokens().size(), exponent);
	}
	
	private static class Decoder<Y> extends
			AbstractDecoderIntoFile<ExpLengthLexicalEntryScorer<Y>> {
		
		private static final int	VERSION	= 1;
		
		protected Decoder() {
			super(ExpLengthLexicalEntryScorer.class);
		}
		
		@Override
		public int getVersion() {
			return VERSION;
		}
		
		@Override
		protected Map<String, String> createAttributesMap(
				ExpLengthLexicalEntryScorer<Y> object) {
			final Map<String, String> attrbiutes = new HashMap<String, String>();
			attrbiutes.put("coef", Double.toString(object.coef));
			attrbiutes.put("exponent", Double.toString(object.exponent));
			return attrbiutes;
		}
		
		@Override
		protected ExpLengthLexicalEntryScorer<Y> doDecode(
				Map<String, String> attributes,
				Map<String, File> dependentFiles, BufferedReader reader)
				throws IOException {
			final double baseScore = Double.valueOf(attributes.get("coef"));
			final double exponent = Double.valueOf(attributes.get("exponent"));
			
			return new ExpLengthLexicalEntryScorer<Y>(baseScore, exponent);
		}
		
		@Override
		protected void doEncode(ExpLengthLexicalEntryScorer<Y> object,
				BufferedWriter writer) throws IOException {
			// Nothing to write
		}
		
		@Override
		protected Map<String, File> encodeDependentFiles(
				ExpLengthLexicalEntryScorer<Y> object, File directory,
				File parentFile) throws IOException {
			// No dependent files
			return new HashMap<String, File>();
		}
		
	}
}
