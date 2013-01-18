package edu.uw.cs.lil.tiny.parser.ccg.model;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.utils.hashvector.KeyArgs;
import edu.uw.cs.utils.composites.Pair;

public class ModelLogger {
	private final boolean	sortLexicalEntries;
	
	public ModelLogger(boolean sortLexicalEntries) {
		this.sortLexicalEntries = sortLexicalEntries;
	}
	
	private static <X, Y> String lexicalEntryToString(LexicalEntry<Y> entry,
			IModelImmutable<X, Y> model) {
		return String.format("[%.4f] %s %s (%s)", model.score(entry), entry,
				model.getTheta().printValues(model.computeFeatures(entry)),
				entry.getOrigin());
	}
	
	public <X, Y> void log(final IModelImmutable<X, Y> model, PrintStream out) {
		// Lexicon
		if (sortLexicalEntries) {
			final Map<List<String>, List<LexicalEntry<Y>>> tokensToLexicalEntries = new HashMap<List<String>, List<LexicalEntry<Y>>>();
			for (final LexicalEntry<Y> entry : model.getLexicon()
					.toCollection()) {
				if (!tokensToLexicalEntries.containsKey(entry.getTokens())) {
					tokensToLexicalEntries.put(entry.getTokens(),
							new LinkedList<LexicalEntry<Y>>());
				}
				tokensToLexicalEntries.get(entry.getTokens()).add(entry);
			}
			
			for (final Entry<List<String>, List<LexicalEntry<Y>>> mapEntry : tokensToLexicalEntries
					.entrySet()) {
				Collections.sort(mapEntry.getValue(),
						new Comparator<LexicalEntry<Y>>() {
							
							@Override
							public int compare(LexicalEntry<Y> o1,
									LexicalEntry<Y> o2) {
								return -Double.compare(model.score(o1),
										model.score(o2));
							}
						});
				for (final LexicalEntry<Y> entry : mapEntry.getValue()) {
					out.println(lexicalEntryToString(entry, model));
				}
			}
			
		} else {
			for (final LexicalEntry<Y> entry : model.getLexicon()
					.toCollection()) {
				out.println(lexicalEntryToString(entry, model));
			}
		}
		
		// Theta
		out.println("Feature weights:");
		for (final Pair<KeyArgs, Double> feature : model.getTheta()) {
			out.println(String.format("%s=%s", feature.first(),
					feature.second()));
		}
		
	}
}
