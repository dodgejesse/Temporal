package edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.AbstractDecoderIntoFile;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.DecoderHelper;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.DecoderServices;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.IDecoder;
import edu.uw.cs.utils.collections.IScorer;

public class SkippingSensitiveLexicalEntryScorer<Y> implements
		IScorer<LexicalEntry<Y>> {
	
	private final ICategoryServices<Y>		categoryServices;
	private final IScorer<LexicalEntry<Y>>	defaultScorer;
	private final double					skippingCost;
	
	public SkippingSensitiveLexicalEntryScorer(
			ICategoryServices<Y> categoryServices, double skippingCost,
			IScorer<LexicalEntry<Y>> defaultScorer) {
		this.categoryServices = categoryServices;
		this.skippingCost = skippingCost;
		this.defaultScorer = defaultScorer;
	}
	
	public static <Y> IDecoder<SkippingSensitiveLexicalEntryScorer<Y>> getDecoder(
			DecoderHelper<Y> decoderHelper) {
		return new Decoder<Y>(decoderHelper);
	}
	
	@Override
	public double score(LexicalEntry<Y> lex) {
		if (categoryServices.getEmptyCategory().equals(lex.getCategory())) {
			return skippingCost;
		} else {
			return defaultScorer.score(lex);
		}
	}
	
	private static class Decoder<Y> extends
			AbstractDecoderIntoFile<SkippingSensitiveLexicalEntryScorer<Y>> {
		
		private static final int		VERSION	= 1;
		
		private final DecoderHelper<Y>	decoderHelper;
		
		public Decoder(DecoderHelper<Y> decoderHelper) {
			super(SkippingSensitiveLexicalEntryScorer.class);
			this.decoderHelper = decoderHelper;
		}
		
		@Override
		public int getVersion() {
			return VERSION;
		}
		
		@Override
		protected Map<String, String> createAttributesMap(
				SkippingSensitiveLexicalEntryScorer<Y> object) {
			final Map<String, String> attributes = new HashMap<String, String>();
			
			// Skipping cost
			attributes
					.put("skippingCost", Double.toString(object.skippingCost));
			
			return attributes;
		}
		
		@Override
		protected SkippingSensitiveLexicalEntryScorer<Y> doDecode(
				Map<String, String> attributes,
				Map<String, File> dependentFiles, BufferedReader reader)
				throws IOException {
			// Get default scorer
			final IScorer<LexicalEntry<Y>> defaultScorer = DecoderServices
					.decode(dependentFiles.get("defaultScorer"), decoderHelper);
			
			// Get skipping cost
			final double skippingCost = Double.valueOf(attributes
					.get("skippingCost"));
			
			return new SkippingSensitiveLexicalEntryScorer<Y>(
					decoderHelper.getCategoryServices(), skippingCost,
					defaultScorer);
		}
		
		@Override
		protected void doEncode(SkippingSensitiveLexicalEntryScorer<Y> object,
				BufferedWriter writer) throws IOException {
			// Nothing to do here
		}
		
		@Override
		protected Map<String, File> encodeDependentFiles(
				SkippingSensitiveLexicalEntryScorer<Y> object, File directory,
				File parentFile) throws IOException {
			final Map<String, File> files = new HashMap<String, File>();
			
			// Encode default scorer
			final File defaultScorerFile = new File(directory,
					parentFile.getName() + ".defaultScorer");
			DecoderServices.encode(object.defaultScorer, defaultScorerFile,
					decoderHelper);
			files.put("defaultScorer", defaultScorerFile);
			
			return files;
		}
		
	}
	
}
