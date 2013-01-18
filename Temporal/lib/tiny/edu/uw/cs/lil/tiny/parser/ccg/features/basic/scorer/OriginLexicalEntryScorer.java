package edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.AbstractDecoderIntoFile;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.DecoderHelper;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.DecoderServices;
import edu.uw.cs.lil.tiny.parser.ccg.model.storage.IDecoder;
import edu.uw.cs.utils.collections.IScorer;

/**
 * Scores lexical entries by their origin.
 * 
 * @author Yoav Artzi
 * @param <Y>
 */
public class OriginLexicalEntryScorer<Y> implements IScorer<LexicalEntry<Y>> {
	
	private final IScorer<LexicalEntry<Y>>				defaultScorer;
	private final Map<String, IScorer<LexicalEntry<Y>>>	originScorers;
	
	public OriginLexicalEntryScorer(
			Map<String, IScorer<LexicalEntry<Y>>> originScorers,
			IScorer<LexicalEntry<Y>> defaultScorer) {
		this.originScorers = originScorers;
		this.defaultScorer = defaultScorer;
	}
	
	public static <Y> IDecoder<OriginLexicalEntryScorer<Y>> getDecoder(
			DecoderHelper<Y> decoderHelper) {
		return new Decoder<Y>(decoderHelper);
	}
	
	@Override
	public double score(LexicalEntry<Y> lex) {
		if (originScorers.containsKey(lex.getOrigin())) {
			return originScorers.get(lex.getOrigin()).score(lex);
		} else {
			return defaultScorer.score(lex);
		}
	}
	
	private static class Decoder<Y> extends
			AbstractDecoderIntoFile<OriginLexicalEntryScorer<Y>> {
		
		private static final int		VERSION	= 1;
		private final DecoderHelper<Y>	decoderHelper;
		
		public Decoder(DecoderHelper<Y> decoderHelper) {
			super(OriginLexicalEntryScorer.class);
			this.decoderHelper = decoderHelper;
		}
		
		@Override
		public int getVersion() {
			return VERSION;
		}
		
		@Override
		protected Map<String, String> createAttributesMap(
				OriginLexicalEntryScorer<Y> object) {
			// No attributes, everything is stored in external files
			return new HashMap<String, String>();
		}
		
		@Override
		protected OriginLexicalEntryScorer<Y> doDecode(
				Map<String, String> attributes,
				Map<String, File> dependentFiles, BufferedReader reader)
				throws IOException {
			
			// Get default scorer
			final IScorer<LexicalEntry<Y>> defaultScorer = DecoderServices
					.decode(dependentFiles.get("defaultScorer"), decoderHelper);
			
			// Get the origin scorers
			final Map<String, IScorer<LexicalEntry<Y>>> originScorers = new HashMap<String, IScorer<LexicalEntry<Y>>>();
			for (final Map.Entry<String, File> fileEntry : dependentFiles
					.entrySet()) {
				if (!fileEntry.getKey().equals("defaultScorer")) {
					final IScorer<LexicalEntry<Y>> scorer = DecoderServices
							.decode(fileEntry.getValue(), decoderHelper);
					originScorers.put(fileEntry.getKey(), scorer);
				}
			}
			
			return new OriginLexicalEntryScorer<Y>(originScorers, defaultScorer);
		}
		
		@Override
		protected void doEncode(OriginLexicalEntryScorer<Y> object,
				BufferedWriter writer) throws IOException {
			// Nothing to encode, everything is store in files
		}
		
		@Override
		protected Map<String, File> encodeDependentFiles(
				OriginLexicalEntryScorer<Y> object, File directory,
				File parentFile) throws IOException {
			final Map<String, File> files = new HashMap<String, File>();
			
			// Encode default scorer
			final File defaultScorerFile = new File(directory,
					parentFile.getName() + ".defaultScorer");
			DecoderServices.encode(object.defaultScorer, defaultScorerFile,
					decoderHelper);
			files.put("defaultScorer", defaultScorerFile);
			
			// Encode origin scorers
			for (final Map.Entry<String, IScorer<LexicalEntry<Y>>> entry : object.originScorers
					.entrySet()) {
				final File scorerFile = new File(directory,
						parentFile.getName() + "." + entry.getKey().toString());
				DecoderServices.encode(entry.getValue(), scorerFile,
						decoderHelper);
				files.put(entry.getKey().toString(), scorerFile);
			}
			
			return files;
		}
		
	}
}
