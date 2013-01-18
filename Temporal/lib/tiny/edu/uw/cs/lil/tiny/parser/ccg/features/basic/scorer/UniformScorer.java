package edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.lil.tiny.parser.ccg.model.storage.AbstractDecoderIntoFile;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.IDecoder;
import edu.uw.cs.utils.collections.IScorer;

/**
 * Returns a constant value for every lexical item.
 * 
 * @author Luke Zettlemoyer
 * @param <E>
 */
public class UniformScorer<Y> implements IScorer<Y> {
	
	private final double	score;
	
	public UniformScorer(double value) {
		score = value;
	}
	
	public static <Y> IDecoder<UniformScorer<Y>> getDecoder() {
		return new Decoder<Y>();
	}
	
	@Override
	public double score(Y lex) {
		return score;
	}
	
	private static class Decoder<Y> extends
			AbstractDecoderIntoFile<UniformScorer<Y>> {
		
		private static final int	VERSION	= 1;
		
		protected Decoder() {
			super(UniformScorer.class);
		}
		
		@Override
		public int getVersion() {
			return VERSION;
		}
		
		@Override
		protected Map<String, String> createAttributesMap(
				UniformScorer<Y> object) {
			final Map<String, String> attrbiutes = new HashMap<String, String>();
			attrbiutes.put("score", Double.toString(object.score));
			return attrbiutes;
		}
		
		@Override
		protected UniformScorer<Y> doDecode(Map<String, String> attributes,
				Map<String, File> dependentFiles, BufferedReader reader)
				throws IOException {
			final double score = Double.valueOf(attributes.get("score"));
			
			return new UniformScorer<Y>(score);
		}
		
		@Override
		protected void doEncode(UniformScorer<Y> object, BufferedWriter writer)
				throws IOException {
			// Nothing to write
		}
		
		@Override
		protected Map<String, File> encodeDependentFiles(
				UniformScorer<Y> object, File directory, File parentFile)
				throws IOException {
			// No dependent files
			return new HashMap<String, File>();
		}
		
	}
	
}
