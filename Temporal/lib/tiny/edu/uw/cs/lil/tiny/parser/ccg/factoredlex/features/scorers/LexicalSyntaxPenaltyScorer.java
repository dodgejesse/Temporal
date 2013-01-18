package edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.scorers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.LexicalTemplate;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.AbstractDecoderIntoFile;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.IDecoder;
import edu.uw.cs.utils.collections.IScorer;

public class LexicalSyntaxPenaltyScorer implements IScorer<LexicalTemplate> {
	
	final double	scale;
	
	public LexicalSyntaxPenaltyScorer(double scale) {
		this.scale = scale;
	}
	
	public static IDecoder<LexicalSyntaxPenaltyScorer> getDecoder() {
		return new Decoder();
	}
	
	@Override
	public double score(LexicalTemplate template) {
		return scale * template.getTemplateCategory().numSlashes();
	}
	
	private static class Decoder extends
			AbstractDecoderIntoFile<LexicalSyntaxPenaltyScorer> {
		private static final int	VERSION	= 1;
		
		public Decoder() {
			super(LexicalSyntaxPenaltyScorer.class);
		}
		
		@Override
		public int getVersion() {
			return VERSION;
		}
		
		@Override
		protected Map<String, String> createAttributesMap(
				LexicalSyntaxPenaltyScorer object) {
			final Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("scale", Double.toString(object.scale));
			return attributes;
		}
		
		@Override
		protected LexicalSyntaxPenaltyScorer doDecode(
				Map<String, String> attributes,
				Map<String, File> dependentFiles, BufferedReader reader)
				throws IOException {
			final double scale = Double.valueOf(attributes.get("scale"));
			return new LexicalSyntaxPenaltyScorer(scale);
		}
		
		@Override
		protected void doEncode(LexicalSyntaxPenaltyScorer object,
				BufferedWriter writer) throws IOException {
			// Nothing to write
		}
		
		@Override
		protected Map<String, File> encodeDependentFiles(
				LexicalSyntaxPenaltyScorer object, File directory,
				File parentFile) throws IOException {
			// No dependent files
			return new HashMap<String, File>();
		}
		
	}
}
